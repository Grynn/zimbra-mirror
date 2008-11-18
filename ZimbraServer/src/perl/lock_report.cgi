#!/usr/bin/perl -w

push(@INC, "/usr/lib/cgi-bin");
use strict;
use CGI::Pretty;
use ThreadDumpAnalyzer;

sub getLockAnchorName($);
sub getThreadAnchorName($);
sub getThreadIdsWithLinks(@);
sub getStackTraceHtml($);
sub getUniqueFilename($);

# Maximum number of lines in the stack trace that will be printed
my $STACK_TRACE_MAX_LINES = 10;
# Where dumps and reports will be stored
my $FILESYSTEM_ROOT = "/var/www/lock_report";

# Prepended to generated report URL
my $HTTP_ROOT = "http://cosmonaut.corp.yahoo.com/lock_report";

$CGI::DISABLE_UPLOADS   = 0;
$CGI::POST_MAX          = 10 * 1024 * 1024;
my $q = new CGI::Pretty;

# Sanity checks.
if (! -d $FILESYSTEM_ROOT) {
    error($q, "$FILESYSTEM_ROOT is not a directory");
}
$q->cgi_error() and error( $q, "Error transferring file: " . $q->cgi_error() );

# Write original thread dump to the filesystem.

my $file      = $q->param("file")     || error( $q, "No file received." );
my $fh        = $q->upload("file");
my %lockIds; # IDs of locks that have at least one waiting thread

# Read thread dump and initialize dump analyzer.
my $filename = getUniqueFilename($file);
my $path = "$FILESYSTEM_ROOT/$filename";
open(DUMP, ">$path") or error($q, "Unable to open $path for write.");
my @lines;
while (<$fh>) {
    print(DUMP);
    chomp();
    push(@lines, $_);
}
close(DUMP);
close($fh);
ThreadDumpAnalyzer::initialize(@lines);

# Print locks
open(HTML, ">$path.html") or error($q, "Unable to open $path.html for write.");

print(HTML $q->start_html("Lock Report"),
      $q->p("<a href=\"$HTTP_ROOT/$filename\">$filename</a>"),
      $q->h3("Lock waited on by threads"));
print(HTML $q->start_table( { -border => 1 } ));
print(HTML $q->Tr( [
		  $q->th([ "Lock", "Type", "Owner", "Waiters"])
	      ] ));
for my $lockId (ThreadDumpAnalyzer::getLockIdsSortedByWaiters()) {
    my @waiters = ThreadDumpAnalyzer::getLockWaiters($lockId);
    if (scalar(@waiters) > 0) {
	$lockIds{$lockId} = 1;
	my $waitersHtml = getThreadIdsWithLinks(@waiters);
	my $lockOwner = ThreadDumpAnalyzer::getLockOwner($lockId);
	print(HTML $q->Tr( [
			  $q->td([ "<a name=\"" . getLockAnchorName($lockId) . "\">$lockId</a>",
				   ThreadDumpAnalyzer::getLockType($lockId),
				   "<a href=\"#" . getThreadAnchorName($lockOwner) . "\">$lockOwner</a>",
				   $waitersHtml ])
		      ] ));
    }
}
print(HTML $q->end_table());

# Print threads
print(HTML $q->h3("Threads"));
print(HTML $q->start_table( { -border => 1 } ));

for my $threadName (ThreadDumpAnalyzer::getThreadIdsSortedByWaiters()) {
    my @blockedThreads = ThreadDumpAnalyzer::getBlockedThreads($threadName);
    my $blockedThreadsString = "";
    if (scalar(@blockedThreads) > 0) {
	$blockedThreadsString = "<p>Blocks:<br>" . getThreadIdsWithLinks(@blockedThreads) . "</p>";
    }
    print(HTML $q->Tr( [
		      $q->td([ "<a name=\"" . getThreadAnchorName($threadName) . "\">$threadName</a>" . $blockedThreadsString,
			       getStackTraceHtml($threadName)
			     ])
		  ] ));
}

print(HTML $q->end_table());
print(HTML $q->end_html());

close(HTML);
print($q->redirect("$HTTP_ROOT/$filename.html"));
exit(0);

#######################

sub getUniqueFilename($) {
    my $filename = shift();
    if (! -f "$FILESYSTEM_ROOT/$filename") {
	return $filename;
    }
    
    my $name;
    my $ext;
    if ($filename =~ /(.*)\.(.*)/) {
	$name = $1;
	$ext = ".$2";
    } else {
	$name = $filename;
	$ext = "";
    }
    my $rev = 1;
    while (1) {
	if (! -f "$FILESYSTEM_ROOT/$name-$rev$ext") {
	    return "$name-$rev$ext";
	}
	$rev++;
    }
}
    
sub getStackTraceHtml($) {
    my $threadId = shift();
    my @lines;
    foreach (split('\n', ThreadDumpAnalyzer::getThreadStack($threadId))) {
	my $lockId;
	my $lockLink;
	if (/waiting on <(.*)>/) {
	    $lockId = $1;
	    if (defined($lockIds{$lockId})) {
		$lockLink = "<a href=\"#" . getLockAnchorName($lockId) . "\">$lockId</a>";
		s/waiting on <(.*)>/waiting on &lt;$lockLink&gt;/;
	    }
	}
	if (/locked <(.*)>/) {
	    $lockId = $1;
	    if (defined($lockIds{$lockId})) {
		$lockLink = "<a href=\"#" . getLockAnchorName($lockId) . "\">$lockId</a>";
		s/locked <(.*)>/locked &lt;$lockLink&gt;/;
	    }
	}
	if (scalar(@lines) == $STACK_TRACE_MAX_LINES) {
	    push(@lines, "$_ ...");
	    last;
	}
	push(@lines, $_);
    }
    return join("<br>\n", @lines);
}

sub getLockAnchorName($) {
    my $lockId = shift();
    return "lock-$lockId";
}

sub getThreadAnchorName($) {
    my $threadId = shift();
    return "thread-$threadId";
}

sub getThreadIdsWithLinks(@) {
    my @threadIds = @_;
    my $firstTime = 1;
    my $buf;
    for my $threadId (@threadIds) {
	if ($firstTime) {
	    $firstTime = 0;
	} else {
	    $buf .= ", ";
	}
	$buf .= "<a href=\"#" . getThreadAnchorName($threadId) . "\">$threadId</a>";
    }
    return $buf;
}

sub error {
    my( $q, $reason ) = @_;

    print $q->header( "text/html" ),
    $q->start_html( "Error" ),
    $q->h1( "Error" ),
    $q->p( "Your upload was not procesed because the following error ",
	   "occured: " ),
    $q->p( $q->i( $reason ) ),
    $q->end_html;
    exit;
}
