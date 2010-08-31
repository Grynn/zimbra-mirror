#!/usr/bin/perl

use strict;

use File::Copy;
use XML::XPath;

my $COMMAND_LINE="$0 @ARGV";
if ( $#ARGV != 0 )
{
        print "You typed: $COMMAND_LINE\n";
        print "Usage: $0 <Nunit output root>\n";
        print " <Nunit output root>: the base of the *.out/*.txt files\n";
        exit 1;
}



#Store the command line arguments
my $NUNIT_ROOT=shift (@ARGV);


# Output files
my $bugsTextFile = "$NUNIT_ROOT/BugReport.txt";

#my $logFile = "$NUNIT_ROOT/reports.log";
my $logFile = "/dev/null";
open(LOG, ">> $logFile");


# Date and times
#
my (undef,undef,undef,$mday,$mon,$year,undef,undef,undef) = localtime;
$mon=$mon+1;
$year=$year+1900;
my $today = "$mon-$mday-$year";


# Temp files and databases
#
my $dbRoot="T:\\BugReports";
my $bugStatusDBM = "$dbRoot/bugStatus.txt";
my $bugTestcaseDBM = "$dbRoot/bugTestcase.txt";
my $bugQaContactDBM = "$dbRoot/bugQaContact.txt";




my $tcExecuted = 0;
my $tcFailures = 0;
my $tcSkipped = 0;
my $tcDuration = 0;

my %bugsStatus;
my %bugsTestcase;
my %bugsQaContact;

my %newBugs;
my %failBugs;
my %failBugsNoAction;
my %passBugs;
my %passBugsNoAction;

sub BUGZILLA_OPEN
{
        my ($id) = (@_);
        return (
                $bugsStatus{$id} eq 'UNCONFIRMED' ||
                $bugsStatus{$id} eq 'NEW' ||
                $bugsStatus{$id} eq 'ASSIGNED' ||
                $bugsStatus{$id} eq 'REOPENED'
        );
}


sub BUGZILLA_FIXED
{
        my ($id) = (@_);
        return (
                $bugsStatus{$id} eq 'RESOLVED'
        );
}

sub BUGZILLA_CLOSED
{
        my ($id) = (@_);
        return (
                $bugsStatus{$id} eq 'VERIFIED' ||
                $bugsStatus{$id} eq 'CLOSED'
        );
}





sub CREATE_RESULTS_EMAIL_TEXT
{

	my $timeStamp = localtime;
	my $text = "\n\nAutomated bug report\n";

	
	$text .= "\n\n";
	$text .= "Date: $timeStamp\n\n";


	$text .= "Total tests: ". ($tcExecuted + $tcSkipped) ."\n";
	$text .= "Tests run:   $tcExecuted\n";
	$text .= "Success:     ". ($tcExecuted - $tcFailures) ."\n";
	$text .= "Failures:    $tcFailures\n";
	$text .= "Not run:     $tcSkipped\n";
	$text .= "Time:        $tcDuration seconds\n";


	$text .= "\nBug Reports:\n";
	$text .= "(Items with an asterisk are out of sync and need follow up)\n";

	$text .= "\nFAILED TEST CASES:\n";
	while ( my ($tcId, undef) = each %newBugs )
	{
		$text .= "* NEW $tcId -- http://bugzilla.zimbra.com/enter_bug.cgi\n";
	}

	while ( my ($bugId, $tcId) = each %failBugs )
	{
		$text .= "* $bugId $bugsStatus{$bugId} $tcId ";
		$text .= " http://bugzilla.zimbra.com/show_bug.cgi?id=$bugId";
		$text .= " ( $bugsQaContact{$bugId} )\n";
	}

	while ( my ($bugId, $tcId) = each %failBugsNoAction )
	{
		$text .= "$bugId $bugsStatus{$bugId} $tcId ";
		$text .= " http://bugzilla.zimbra.com/show_bug.cgi?id=$bugId";
		$text .= " ( $bugsQaContact{$bugId} )\n";
	}

	$text .= "\nPASSED TEST CASES:\n";
	while ( my ($bugId, $tcId) = each %passBugs )
	{
		$text .= "* $bugId $bugsStatus{$bugId} $tcId ";
		$text .= " http://bugzilla.zimbra.com/show_bug.cgi?id=$bugId";
		$text .= " ( $bugsQaContact{$bugId} )\n";
	}
	while ( my ($bugId, $tcId) = each %passBugsNoAction )
	{
		$text .= "$bugId $bugsStatus{$bugId} $tcId ";
		$text .= " http://bugzilla.zimbra.com/show_bug.cgi?id=$bugId";
		$text .= " ( $bugsQaContact{$bugId} )\n";
	}

	$text;
}

sub CREATE_RESULTS_EMAIL_TEXT2
{
	my $text = "Hello world!";
	$text;
}

sub CREATE_TEXT_FILE
{

	
	#Combine them into an e-mail format
	print LOG "building text report ...\n";
	open FH, "> $bugsTextFile";

	#Create the plain text version
	print FH &CREATE_RESULTS_EMAIL_TEXT;


	close FH;

}


sub STATUS_UPDATE
{
	my($tcId, $tcStatus) = (@_);

	print LOG "STATUS_UPDATE: $tcId $tcStatus\n";

	if ( $tcStatus eq 'True' ) {

		# If no bugs are associated, then keep going
		if ( !defined($bugsTestcase{$tcId}) ) {
			return;
		}

		# If any open bugs are passing, list them in the report
		foreach my $bugId (split / /, $bugsTestcase{$tcId}) {
			if ( BUGZILLA_OPEN($bugId) || BUGZILLA_FIXED($bugId) ) {
				$passBugs{$bugId} = $tcId;
			} else {
				$passBugsNoAction{$bugId} = $tcId;
			}
				
		}

	} else {

		if ( !defined($bugsTestcase{$tcId}) ) {
			$newBugs{$tcId} = "NEW";
			return;
		}

		my @open;
		my @closed;
		foreach my $bugId (split / /, $bugsTestcase{$tcId}) {

			# If there are any open bugs, assume those
			# open bugs supercede any closed bugs.
			# List only the open bug(s), not the closed bugs
			#
			# If all bugs are closed, then list all of them
			# to be reopened
			#
			if ( BUGZILLA_OPEN($bugId) ) {
				push @open, $bugId;
			}
			if ( BUGZILLA_FIXED($bugId) || BUGZILLA_CLOSED($bugId) ) {
				push @closed, $bugId;
			}

		}

		if ( (@closed > 0) && (@open == 0) ) {
			foreach my $bugId (@closed) {
				$failBugs{$bugId} = $tcId;
			}
		} elsif (@open > 0) {
			foreach my $bugId (@open) {
				$failBugsNoAction{$bugId} = $tcId;
			}
		}
		
	}

}

sub SAVE_TESTSUMMARY
{
        my ($tcExecuted, $tcFailures, $tcSkipped) = (@_);
        
        my $testSummary = "$NUNIT_ROOT/testsummary.txt";
        open(TS, "> $testSummary");
        
        my $t = localtime;
        print TS "# $t\n";
        print TS "Errors: 0\n";
        print TS "Failed: $tcFailures\n";
        print TS "Passed: ". ($tcExecuted-$tcFailures) ."\n";
        
        close(TS);

}


sub GATHER_RESULTS
{

	# The XML Output file
	my $resultsFile = "$NUNIT_ROOT/Results.xml";
	my $xp = XML::XPath->new(filename => $resultsFile);

	my $name;
	my $result;

	foreach my $node ($xp->find('//test-results')->get_nodelist)
	{
		$tcExecuted = $node->getAttribute("total");
		$tcFailures = $node->getAttribute("failures");
		$tcSkipped = $node->getAttribute("not-run");
	}

	&SAVE_TESTSUMMARY($tcExecuted, $tcFailures, $tcSkipped);
	
	foreach my $node ($xp->find('//test-suite[@name="UNNAMED"]')->get_nodelist)
	{
		$tcDuration = $node->getAttribute("time");
	}

	# key: testcase ID (i.e. <test-case name="..."/>)
	# value: testcase results (i.e. <test-case success="True"|"False"/>)
	#
	my %testStatus;

	foreach my $node ($xp->find('//test-case[@executed="True"]')->get_nodelist)
	{

		$name = $node->getAttribute("name");
		$result = $node->getAttribute("success");

		print LOG "$name: $result\n";

		$testStatus { $name } = $result;

	}

	while ( my ($tcId, $tcStatus) = each(%testStatus) ) {
		&STATUS_UPDATE($tcId, $tcStatus);
	}



}

sub TEXT_TO_HASH
{

	my $bug;
	my $status;
	my $tc;
	my $buglist;
	my $contact;
	
	# Copy the DB file locally
	my $localBugStatusDBM = "/Program Files/ZimbraQA/bugStatus.txt";
	my $localBugTestcaseDBM = "/Program Files/ZimbraQA/bugTestcase.txt";
	my $localBugQaContactDBM = "/Program Files/ZimbraQA/bugQaContact.txt";
	copy("$bugStatusDBM", "$localBugStatusDBM")
		or die "Unable to copy $bugStatusDBM to $localBugStatusDBM";
	copy("$bugTestcaseDBM", "$localBugTestcaseDBM")
		or die "Unable to copy $bugTestcaseDBM to $localBugTestcaseDBM";
	copy("$bugQaContactDBM", "$localBugQaContactDBM")
		or die "Unable to copy $bugQaContactDBM to $localBugQaContactDBM";
	
	# Open the DB files
	open(BUGSTATUS, "< $localBugStatusDBM");
	while (<BUGSTATUS>)
	{
		chomp;
		($bug, $status) = split(/	/);
		$bugsStatus { $bug } = $status;
	}
	close(BUGSTATUS);
	
	open(TESTCASE, "< $localBugTestcaseDBM");
	while (<TESTCASE>)
	{
		chomp;
		($tc, $buglist) = split(/	/);
		$bugsTestcase { $tc } = $buglist;
	}
	close(TESTCASE);

	open(CONTACT, "< $localBugQaContactDBM");
	while (<CONTACT>)
	{
		chomp;
		($tc, $contact) = split(/	/);
		$bugsQaContact { $tc } = $contact;
	}
	close(CONTACT);

}

sub MAIN
{

#	&TEXT_TO_HASH;
#
#	&GATHER_RESULTS;
#	
	&CREATE_TEXT_FILE;


}




&MAIN;


