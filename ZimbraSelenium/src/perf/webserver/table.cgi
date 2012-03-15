#!/usr/bin/perl -w

use strict;
use CGI;
use DBI;

my $query = CGI->new;

my @appids = $query->param('apps');;
my @browserids = $query->param('browsers');;
my @messageids = $query->param('messages');;
my $build1id = $query->param('build1');;
my $build2id = $query->param('build2');;

# Restrict searches to the specified parameters
my $sqlApps = "";
my $sqlBrowsers = "";
my $sqlMessages = "";


# DB VARIABLES
my $dbh;
my $sql;
my $sth;

# CONFIG VARIABLES
my $host = "10.137.244.6";
my $database = "perf";
my $tablename = "perf";
my $user = "perf";
my $pw = "perf";

my %apps = ();
my %browsers = ();
my %builds = ();
my %messages = ();
my %milestones = ();

# Table properties
my $tableLast = 5;
my $limitGreen = 5;
my $limitYellow = 25;
my $limitRed = 50;
my $limitOrange = 1000;

sub doAvg {
	my ($messageid, $buildid) = @_;

	$sql = "SELECT AVG(delta) FROM perf WHERE ( (messageid = $messageid) AND (buildid=$buildid) $sqlApps $sqlBrowsers $sqlMessages )";
	$sth = $dbh->prepare($sql);
	$sth->execute();
	while (my ($ave) = $sth->fetchrow_array()) {
		return int($ave);
	}
	return 1;
}

sub doAvgLimit {
        my ($messageid, $buildid) = @_;

	$sql = "SELECT AVG(delta) FROM (SELECT delta FROM perf WHERE ( (messageid = $messageid) AND (buildid=$buildid) $sqlApps $sqlBrowsers $sqlMessages ) ORDER BY created DESC LIMIT $tableLast) as t";
	$sth = $dbh->prepare($sql);
	$sth->execute();
	while (my ($ave) = $sth->fetchrow_array()) {
		return int($ave);
	}
	return 1;
}

sub doRow {
	my ($messageid) = @_;
	
	my $average = 1;
	my $bg = "";

	my $ave1 = &doAvg($messageid, $build1id);
	my $ave1limit = &doAvgLimit($messageid, $build1id);
	my $ave2 = &doAvg($messageid, $build2id);
	my $ave2limit = &doAvgLimit($messageid, $build2id);

	my $value = "";

	$value .= "<td> $messages{$messageid} </td>\n";
	$bg = '';
	$bg = "bgcolor='#E69900'"	if ($ave1 > $limitOrange);	#green
	$value .= "<td $bg> $ave1 </td>\n";
	$bg = '';
	$bg = "bgcolor='#E69900'"	if ($ave1limit > $limitOrange);	#green
	$value .= "<td $bg> $ave1limit </td>\n";
	$bg = '';
	$bg = "bgcolor='#E69900'"	if ($ave2 > $limitOrange);	#green
	$value .= "<td $bg> $ave2 </td>\n";
	$bg = '';
	$bg = "bgcolor='#E69900'"	if ($ave2limit > $limitOrange);	#green
	$value .= "<td $bg> $ave2limit </td>\n";

	$average =  int((($ave2-$ave1) * 100)/ $ave1);
	$bg = '';
	$bg = "bgcolor='#009900'"	if ($average < $limitGreen);	#green
	$bg = "bgcolor='#FFFF00'"	if ($average > $limitYellow);	#yellow
	$bg = "bgcolor='#FF0000'"	if ($average > $limitRed);	#red
	$value .= "<td $bg>$average</td>\n";

	$average =  int((($ave2limit-$ave1limit) * 100)/ $ave1limit);
	$bg = '';
	$bg = "bgcolor='#009900'"	if ($average < $limitGreen);	#green
	$bg = "bgcolor='#FFFF00'"	if ($average > $limitYellow);	#yello
	$bg = "bgcolor='#FF0000'"	if ($average > $limitRed);	#red
	$value .= "<td $bg>$average</td>\n";

	return $value;
}

sub page {

	print "Content-type: text/html\n\n" ;
	print "<html >\n";
	print "<head>\n";
	print "<title>Perf Comparison Table</title>\n";
	print "<style>\n";
	print "table
{
    border-color: #000;
    border-width: 0 0 1px 1px;
    border-style: solid;
}

td
{
    border-color: #000;
    border-width: 1px 1px 0 0;
    border-style: solid;
    text-align: right;
    margin: 4px;
    padding: 4px;
}\n";
	print "</style>\n";
	print "</head>\n";
	print "<body>\n";

	print "<table >\n";
	print "<tr><td bgcolor='#009900'>&nbsp;&nbsp;&nbsp;&nbsp;</td><td>Percentages less than $limitGreen are green</td></tr>\n";
	print "<tr><td bgcolor='#FFFF00'>&nbsp;&nbsp;&nbsp;&nbsp;</td><td>Percentages greater than $limitYellow are yellow</td></tr>\n";
	print "<tr><td bgcolor='#FF0000'>&nbsp;&nbsp;&nbsp;&nbsp;</td><td>Percentages greater than $limitRed are red</td></tr>\n";
	print "<tr><td bgcolor='#E69900'>&nbsp;&nbsp;&nbsp;&nbsp;</td><td>Durations longer than $limitOrange are orange</td></tr>\n";
	print "</table >\n";
	print "<br/><br/>\n";

	print "<table >\n";

	print "<tr>\n";
	print "<td>&nbsp;</td>\n";
	print "<td colspan='2'>$builds{$build1id}</td>\n";
	print "<td colspan='2'>$builds{$build2id}</td>\n";
	print "<td colspan='2'>Change</td>\n";
	print "</tr>\n";

	print "<tr>\n";
	print "<td>Action</td>\n";
	print "<td>Average</td>\n";
	print "<td>Last $tableLast</td>\n";
	print "<td>Average</td>\n";
	print "<td>Last $tableLast</td>\n";
	print "<td>% change</td>\n";
	print "<td>Last $tableLast % change</td>\n";
	print "</tr>\n";


	my @ids = keys %messages;
	if ( @messageids ) {
		@ids = @messageids;
	}
	foreach my $id (sort { $a <=> $b } @ids) {

		print "<tr>\n";
		print &doRow($id);
		print "</tr>\n";

	}

	print "</table>\n";




	print "</body>\n";
	print "</html>\n";
}

sub buildWhere {
        my ($column, @ids) = @_;

        if (@ids) {
                my $where = undef;
                for my $id (@ids) {
                        if ( defined($where) ) {
                                $where = "$where OR ($column = $id)";
                        } else {
                                $where = "($column = $id)";
                        }
                }
                $where = "AND ($where)";
                return $where;
        }

        return "";

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

        $sqlApps = &buildWhere("appid", @appids) if ( @appids );
        $sqlBrowsers = &buildWhere("browserid", @browserids) if (@browserids);
        $sqlMessages = &buildWhere("messageid", @messageids) if (@messageids);

	# Build the page
	&page;
}

&main;

exit;
