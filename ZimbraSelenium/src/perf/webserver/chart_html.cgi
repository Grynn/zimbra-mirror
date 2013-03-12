#!/usr/bin/perl -w

use strict;
use CGI;
use DBI;
use Date::Parse;

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


sub page {

	my @divs;
	my @methods;
	my $url = "http://zqa-006.eng.vmware.com/perf/chart_json.cgi?";
	my $query = undef;

	# There must be an easier way to copy the query parameters

	foreach (@appids) {
		if ( defined($query) ) {
			$query = $query ."&apps=". $_;
		} else {
			$query = "apps=". $_;
		}
	}

	foreach (@browserids) {
		if ( defined($query) ) {
			$query = $query ."&browsers=". $_;
		} else {
			$query = "browsers=". $_;
		}
	}

	foreach (@buildids) {
		if ( defined($query) ) {
			$query = $query ."&builds=". $_;
		} else {
			$query = "builds=". $_;
		}
	}

	foreach (@milestoneids) {
		if ( defined($query) ) {
			$query = $query ."&milestones=". $_;
		} else {
			$query = "milestones=". $_;
		}
	}



	if ( @messageids ) {

		# Only chart the requested messages (use-cases)
		foreach ( @messageids ) {

			my $divid = "div". $_;
			my $u;
			if ( defined($query) ) {
				$u = "$url$query&messages=$_";
			} else {
				$u = $url."messages=$_";
			}
			

			push( @divs, "<div id=\"$divid\"  style=\"width: 900px; height: 500px;\"></div>" );
			push( @methods, "drawChart(\"". $messages{$_} ."\", \"$divid\",\"$u\");" );

		}

	} else {

		# No messages (use-cases) were specified
		# Chart all of them

		foreach ( keys %messages ) {

			my $divid = "div". $_;
			my $u;
			if ( defined($query) ) {
				$u = "$url$query&messages=$_";
			} else {
				$u = $url."messages=$_";
			}

			push( @divs, "<div id=\"$divid\" style=\"width: 900px; height: 500px;\"></div>" );
			push( @methods, "drawChart(\"". $messages{$_} ."\", \"$divid\",\"$u\");" );

		}

	}

        print "Content-type: text/html\n\n" ;
        print "<html >\n";
        print "<head >\n";
	print "<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>\n";
	print "<script src=\"//ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js\"></script>\n";
	print "<script type=\"text/javascript\">\n";
	print "google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});\n";
	print "google.setOnLoadCallback(draw);\n";
	print "function draw() {\n";
	
	foreach (@methods) {
		print "$_\n";
	}

	print "}\n";

	print "function drawChart(chart_title, divid, jsonurl) {\n";
	print "var jsonData = \$.ajax({\n";
	print "url: jsonurl,\n";
	print "dataType:\"json\",\n";
	print "async: false\n";
	print "}).responseText;\n";
	print "var data = new google.visualization.DataTable(jsonData);\n";
	print "var options = {\n";
	print "title: chart_title ,\n";
	print "vAxis: { title: \"msec\" },\n";
	print "hAxis: { title: \"date\" },\n";
	print "};\n";
	print "var chart = new google.visualization.ScatterChart(document.getElementById(divid));\n";
	print "chart.draw(data, options);\n";

	print "}\n";

        print "</script >\n";
        print "</head >\n";
        print "<header><title>Custom chart</title></header>\n";
        print "<body>\n";

        print "<h2>Client Performance</h2>\n";

	foreach ( @divs ) {
		print $_ ."\n";
	}

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
        $sql = "SELECT id,build FROM builds ORDER BY build ASC";
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
