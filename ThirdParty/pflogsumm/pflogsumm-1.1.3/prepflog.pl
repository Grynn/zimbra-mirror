#!/usr/bin/perl
eval 'exec perl -S $0 "$@"'
    if 0;

=pod

=head1 NAME

prepflog.pl - Pre-processor for pflogsumm

=head1 SYNOPSIS

prepflog.pl [-d <today|yesterday>][--syslog_name=string]
            [file1 [filen]]

prepflog.pl -[help|version]

If no file(s) specified, reads from stdin. Output is to stdout.

=head1 DESCRIPTION

Utility to filter out postfix log lines due to re-injection 
into postfix of messages from a content filter or antivirus 
scanner.

Reads from input and writes to output intercepting and
disregarding lines which are related to the second
passage of messages through postfix.

The output is suitable to pass to pflogsumm and should avoid
pflogsumm's double counting of these messages.

As with pflogsumm itself, only postfix lines are processed.
All other log lines are not passed on to the output.
A non standard syslog name can be handled via the
syslog_name parameter. Also log lines may be filtered by
today or yesterday's date.

=head1 OPTIONS

-d today       extract log lines just for today

-d yesterday   extract log lines just for yesterday

-help          Emit short usage message and bail out.

--syslog_name=name

               Set syslog_name to look for for Postfix log entries.

               By default, prepflog looks for entries in logfiles
               with a syslog name of "postfix," the default.
               If you've set a non-default "syslog_name" parameter
               in your Postfix configuration, use this option to
               tell prepflog what that is.

-version      Print program name and version and bail out.

=head1 EXAMPLES

Typical use of this pre-processor would be:

prepflog.pl -d yesterday /var/log/mail | pflogsumm.pl 

Any other options can be specified to pflogsumm as normal.
The -d flag can however be omitted from pflogsumm if already
specified with prepflog.pl. It will do no harm if left.
If --syslog_name is used with prepflog.pl it must be specified
again with pflogsumm.

Processing of log files should be carried out just before
rotating them. Even so it is still possible to miss messages
unless processing considers all log files for a particular
day. A script that may be helpful for this is (which 
considers that logfiles are compressed when rotated):

        #!/bin/sh
        LASTLOG=`ls -t /var/log/mail*.gz | head -n 1`
        /bin/zcat $LASTLOG | /bin/cat - /var/log/mail | \ 
		/usr/local/bin/prepflog.pl -d yesterday | \ 
		/usr/local/bin/pflogsumm 

=head1 CAVEATS

The current release is a beta version, which has
undergone internal testing. In particular it has not
been tested on a highly loaded server or a large
corpus of mail log examples

As always, use a program in a test environment until
you are comfortable about putting it into a production
environment.

=head1 BUGS

None known, but needs more testing.

=head1 NOTES

Compatible with postfix 2.3 snapshots and pflogsumm 1.1.0

=head1 SEE ALSO

pflogsumm(1)

=head1 AUTHOR

John Fawcett 

This script has been adapted from the pflogsumm
written by Jim Seymour. Whole sections of pflogsumm
code have been imported here. The reason for this
is that having taken into account the way pflogsumm
works, it should be easier to integrate in the
future, if indeed that step is found to be a useful one.

Any feedback is welcome: johnfawcett@tiscali.it

The script is currently available at:
http://web.tiscali.it/postfix/

=head1 COPYRIGHT AND LICENSE

Copyright (c) 2004 John Fawcett

The parts of the code derived from pflogsumm are
copyrighted by Jim Seymour.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You may have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
USA.

An on-line copy of the GNU General Public License can be found
http://www.fsf.org/copyleft/gpl.html.

=cut

# Taken from pflogsumm
use strict;
use locale;
use Getopt::Long;
# ---Begin: SMTPD_STATS_SUPPORT---
use Date::Calc qw(Delta_DHMS);
# ---End: SMTPD_STATS_SUPPOR

my $release = "0.2";

use vars qw(
    	$progName
    	$usageMsg
    	%opts
	@monthNames %monthNums $thisYr $thisMon
);

# Constants used throughout pflogsumm
@monthNames = qw(Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec);
%monthNums = qw(
    Jan  0 Feb  1 Mar  2 Apr  3 May  4 Jun  5
    Jul  6 Aug  7 Sep  8 Oct  9 Nov 10 Dec 11);
($thisMon, $thisYr) = (localtime(time()))[4,5];
$thisYr += 1900;


my (	$cmd, $qid, 
	$dateStr,
	$msgMonStr, $msgMon, $msgDay, $msgTimeStr, $msgHr, $msgMin, $msgSec,
    	$msgYr);

$progName = "prepflog.pl";
$usageMsg =
    "usage: $progName [-d <today|yesterday>] [--syslog_name=name] [file1 [filen]]

       $progName -[version|help]";


GetOptions(
    "d=s"                => \$opts{'d'},
    "help"               => \$opts{'help'},
    "syslog_name=s"      => \$opts{'syslogName'},
    "version"               => \$opts{'version'},
) || die "$usageMsg\n";
# internally: 0 == none, undefined == -1 == all
my $syslogName = $opts{'syslogName'}? $opts{'syslogName'} : "postfix";

if(defined($opts{'help'})) {
    print "$usageMsg\n";
    exit 0;
}

if(defined($opts{'version'})) {
    print "$progName $release\n";
    exit 0;
}

$dateStr = get_datestr($opts{'d'}) if(defined($opts{'d'}));

########################################################
# start of prepflog code section 
########################################################

# variables used by prepflog
my %block =  (); 	# array of lines which will be blocked
my %held_connect =  (); # array of smtpd connect lines which will be output or disregarded
my %held_client =  (); 	# array of smtpd client lines which will be output or disregarded
my %held_lookup =  (); 	# maps queue ids to process ids and hosts keys
my %seen =  (); 	# array of message ids for seen messages
my $lookup;		# temporary variable to hold keys for lookups 
my @out_queue = ();	# queue for passing log lines back to main loop

########################################################
# end of prepflog code section 
########################################################

while(<>) {
    next if(defined($dateStr) && ! /^$dateStr/o);
    s/: \[ID \d+ [^\]]+\] /: /o;        # lose "[ID nnnnnn some.thing]" stuff
    my $logRmdr;
    next unless((($msgMonStr, $msgDay, $msgHr, $msgMin, $msgSec, $logRmdr) =
        /^(...) +(\d+) (..):(..):(..) \S+ (.+)$/o) == 6);
    unless((($cmd, $qid) = $logRmdr =~ m#^(?:vmailer|postfix|$syslogName)/([^\[:]*).*?: ([^:\s]+)#o) == 2 ||
           (($cmd, $qid) = $logRmdr =~ m#^((?:vmailer|postfix)(?:-script)?)(?:\[\d+\])?: ([^:\s]+)#o) == 2)
    {
#        print UNPROCD "$_";
        next;
    }
    chomp;


########################################################
# start of prepflog code section 
########################################################

# Each log line is filtered through the preprocess routine.
# The task of preprocess is to decide whether to output a line. It may:
# 1. output the line immediately
# 2. disregard the line immediately
# 3. reserve judgement until some future line has been processed. 
# 4. In this case, in response to some future log line, preprocess may:
#    a. output the held line(s) and the future log line
#    b. disregard the held line(s) and the future log line
#
# The preprocess routine may return:
# - nothing (cases 2, 3, 4b) in which case processing continues with the next log line.
# - one or more lines (cases 1, 4a). If preprocess will return multiple lines
#   then the first line is returned on the first call. 
#   Any further lines are returned by calling preprocess with an empty input $_.
#   When the preprocess returns nothing (ie preprocessing of a particular log
#   line is finished), processing continues with the next log line.

    while ( $_ = preprocess($logRmdr) )
    {
	print "$_\n";
	$_ = "";
    }
}
exit 0;

# preprocess checks if it was called with non-empty input $_
# in which case it processes $_ via getnext() which may
# add lines to the queue.
# If called with empty input the next line from the queue is
# returned.

sub preprocess
{
	my $logRmdr = pop(@_);
	if ($_ ne "")
	{
		getnext($logRmdr);
	}
	return shift(@out_queue);
}

# getnext contains the logic which decides which lines
# can be output, which can be blocked and which have
# to be held for future decision.

sub getnext
{
	my $logRmdr = pop(@_);

	if( $cmd eq "qmgr" )
	{

# If qmgr line has been blocked, disregard this log line
# (Key for the block array is qmgr:qid) 
		$lookup = "qmgr:".$qid;
		if (is_blocked())
		{
# If qmgr 'removed' line, free up space in the block array
# since there are no more qmgr lines for this qid 
			if( $logRmdr =~ /: removed/o )
			{ 
				remove_block();
			}
			return;
		}
		else
		{
# qmgr line was not blocked so add to output queue 
			push (@out_queue, $_);
			return;
		}
	}
	if( $cmd eq "smtpd" )
	{
		if( $logRmdr =~ /\/smtpd\[(\d+)\]: disconnect from (.+)/o )
		{
# If smtpd disconnect line has been blocked, disregard log line 
# (Key in the block array is smtpd:pid:host)
# Free up space in the blocked array since we will not see any more 
# smtpd lines for this message 
			$lookup = "smtpd:".$1 .":".$2;	
			if ( is_blocked())
			{
				remove_block();
				return;
			}
			
		}
# If this is an smtpd reject/warning/hold/discard for previously held rows,
# add the held rows to the output queue followed by the current line
# (Key for the held array is qid)
		if ( $logRmdr =~ /\/smtpd\[\d+\]: (.*): (reject(?:_warning)?|hold|discard): /o)
		{
			$lookup=$1;
			output_held();
			push (@out_queue, $_);
			return;
		}
# If this is an smtpd connect line then hold the line
# (Key for the held_connect array is pid:host)
		if ($logRmdr =~ /\/smtpd\[(\d+)\]: connect from (.+)/o)
		{
			$lookup= $1.":".$2;
			hold_connect();
			return;
		}
# If this is an smtpd client line then hold the line
# (Key for the held_client array is $qid)
# The value of pid:host will be stored to cross reference 
# later to the smtpd connect line which is stored by pid:host 

		if ($logRmdr =~ /\/smtpd\[(\d+)\]: (.*): client=(.+?)(?:,|$)/o)
		{
			$lookup= $1.":".$3;
			$qid= $2;
			hold_client();
			return;
		}
	}
# If this is smtp line giving final disposal of message
# (such as smtp,local,lmtp,pipe) and if it is blocked  
# disregard this log line.
# (Key for the block array is deliver:qid)
	if ($logRmdr =~ /\[\d+\]: (.*): to=<[^>]*>, (?:orig_to=<[^>]*>, )?relay=[^,]+, delay=[^,]+, (?:dsn=\d+\.\d+\.\d+, )?status=\S+.*$/o )
		{
		$lookup="deliver:".$1;
		if( is_blocked() )
		{
			return;	
		}
	}
# Cleanup line links msg id to qid. If the msg id has already been seen
# held lines for this qid are disregarded. Otherwise they are added
# to output queue.
# (Key for the seen array is msg id)
	if ( $cmd eq "cleanup" )
	{
		if ($logRmdr =~ /\/cleanup\[\d+\]: (.*): message-id=\s?<?(.*)>?/o )
		{
			$lookup = $2;
			if ( is_seen())
			{
# msg id has been seen already
# add a block on future qmgr lines for this message 
# Key is qmgr:qid
				$lookup = "qmgr:".$1;
				add_block();
# add a block on future smtpd disconnect for this message
# Key is the same one used to hold smtpd connect line
				$lookup= $1;
				$lookup = "smtpd:".get_held_lookup();
				add_block();
# add a block on future final disposal of message lines (such as
# smtp,local,lmtp,pipe etc).
# Key is deliver:qid
				$lookup = "deliver:".$1;
				add_block();
# The held lines can be deleted
# Key is qid
				$lookup= $1;
				remove_held();
# msg id must be removed from seen array to be able to
# handle multiple recipients or representation of rejected
# messages
# Key is msg id
				$lookup = $2;
				remove_seen();
				return;
			}
			else
			{
# msg id has not been seen so add held lines to output queue.
# Key is qid
				$lookup=$1;
				output_held();
				remove_held();
# add current line to output queue
				push (@out_queue, $_);
# add msg id to seen array
				$lookup = $2;
				add_seen();
				return;
			}
		}
	}
# default case, just add line to output queue
	push (@out_queue, $_);
	return;
}

########################################################
# auxiliary routines for prepflog
########################################################

# these were so simple, that they could have been left
# in the main body of the code, but I preferred 
# extracting them to help readability in the main body

# add block 
sub add_block
{
	$block{$lookup}=1;
}

# check if blocked
sub is_blocked
{
	return $block{$lookup};
}

# remove block
sub remove_block
{
	delete $block{$lookup};
}

# hold the smtpd connect line
# Key is pid:host
sub hold_connect
{
	$held_connect{$lookup} = $_;
}

# hold the smtpd client line
# Key is qid
# Cross reference stored to key of held
# smtpd connect line
sub hold_client
{
	$held_lookup{$qid} = $lookup;
	$held_client{$qid} = $_;
}

# lookup key to held_connect line
# Key for the lookup is qid. 
sub get_held_lookup
{
	return $held_lookup{$lookup};
}

# output held lines.
# Key is qid
sub output_held
{
# output smtpd connect
	if( $held_connect{$held_lookup{$lookup}} )
	{
		push (@out_queue, $held_connect{$held_lookup{$lookup}});
	}
# ouput smtpd client
	if( $held_client{$lookup} )
	{
		push (@out_queue, $held_client{$lookup});
	}
}

# remove held lines 
# Key is qid
sub remove_held
{
	delete $held_connect{$held_lookup{$lookup}};
	delete $held_lookup{$lookup};
	delete $held_client{$lookup};
}

# add msg id to those seen
sub add_seen
{
	$seen{$lookup}=1;;
}

# check if msg id seen
sub is_seen
{
	return $seen{$lookup};	
}

# remove msg id from those seen
sub remove_seen
{
	delete $seen{$lookup};
}


########################################################
# end of prepflog code section 
########################################################

# Taken from pflogsumm
# return a date string to match in log
sub get_datestr {
    my $dateOpt = $_[0];

    my $aDay = 60 * 60 * 24;

    my $time = time();
    if($dateOpt eq "yesterday") {
        $time -= $aDay;
    } elsif($dateOpt ne "today") {
        die "$usageMsg\n";
    }
    my ($t_mday, $t_mon) = (localtime($time))[3,4];

    return sprintf("%s %2d", $monthNames[$t_mon], $t_mday);
}


