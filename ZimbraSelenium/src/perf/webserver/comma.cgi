#!/usr/bin/perl -w

use strict;
use CGI;
use DBI;

my $query = CGI->new;

my @appids = $query->param('apps');;
my @browserids = $query->param('browsers');;
my @buildids = $query->param('builds');;
my @milestoneids = $query->param('milestones');;
my @messageids = $query->param('messages');;
my $plot = $query->param('plot');;


# DB VARIABLES
my $dbh;
my $sql;
my $sth;

# CONFIG VARIABLES
my $host = "10.20.140.198";
my $database = "perf";
my $tablename = "perf2";
my $user = "perf";
my $pw = "perf";

my %apps = ();
my %browsers = ();
my %builds = ();
my %messages = ();
my %milestones = ();

sub buildWhere {
	my ($column, @ids) = @_;

	my $where = "";
	if ( scalar(@ids) > 0 ) {
		for my $id (@ids) {
			if ( $where == "" ) {
				$where = "($column = $id)";
			} else {
				$where = "$where OR ($column = $id)";
			}
		}
		$where = " AND ($where)";
	}

	return $where;
}

sub data {

	my $sqlApps = &buildWhere("appid", @appids);
	my $sqlBrowsers = &buildWhere("browserid", @browserids);
	my $sqlBuilds = &buildWhere("buildid", @buildids);
	my $sqlMilestones = &buildWhere("milestoneid", @milestoneids);
	my $sqlMessages = &buildWhere("messageid", @messageids);

	$sql = "SELECT created,appid,buildid,browserid,milestoneid,delta,messageid FROM perf2 WHERE ( (id > 0) $sqlApps $sqlBrowsers $sqlBuilds $sqlMilestones $sqlMessages ) ORDER BY messageid ASC";
	$sth = $dbh->prepare($sql);

	$sth->execute();
        while (my ($created, $appid, $buildid, $browserid, $milestoneid, $delta, $messageid) = $sth->fetchrow_array()) {

		print "$created, $apps{$appid}, $builds{$buildid}, $browsers{$browserid}, $milestones{$milestoneid}, $delta, $messages{$messageid}<br/>\n";

        }

}


sub page {

	print "Content-type: text/html\n\n" ;
	print "<html >\n";
	print "<header><title>Custom Chart</title></header>\n";
	print "<body>\n";

	print "Created, App, Build, Browser, Milestone, msec, Description<br/>\n";

	&data();



	print "</body>\n";
	print "</html>\n";
}



sub main {

	# PERL MYSQL CONNECT
	$dbh = DBI->connect( "DBI:mysql:$database;host=$host", "$user", "$pw" )
		|| die "Could not connect to database: $DBI::errstr";

	# Get the list of tests (messages table)
	#
	$sql = "SELECT id,name FROM apps ORDER BY id";
	$sth = $dbh->prepare($sql);
	$sth->execute();
	while (my ($id, $name) = $sth->fetchrow_array()) {
		$apps{$id} = $name;
	}

	# Get the list of tests (messages table)
	#
	$sql = "SELECT id,name FROM browsers WHERE name != 'unknown' ORDER BY id";
	$sth = $dbh->prepare($sql);
	$sth->execute();
	while (my ($id, $name) = $sth->fetchrow_array()) {
		$browsers{$id} = $name;
	}

	# Get the list of tests (messages table)
	#
	$sql = "SELECT id,build FROM builds ORDER BY id";
	$sth = $dbh->prepare($sql);
	$sth->execute();
	while (my ($id, $build) = $sth->fetchrow_array()) {
		$builds{$id} = $build;
	}

	# Get the list of tests (messages table)
	#
	$sql = "SELECT id,name FROM messages ORDER BY id";
	$sth = $dbh->prepare($sql);
	$sth->execute();
	while (my ($id, $name) = $sth->fetchrow_array()) {
		$messages{$id} = $name;
	}

	# Get the list of tests (messages table)
	#
	$sql = "SELECT id,milestone FROM milestones ORDER BY id";
	$sth = $dbh->prepare($sql);
	$sth->execute();
	while (my ($id, $milestone) = $sth->fetchrow_array()) {
		$milestones{$id} = $milestone;
	}

	# Build the page
	&page;
}

&main;

exit;
