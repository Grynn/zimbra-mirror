#!/usr/bin/perl -w

use strict;
use CGI;
use DBI;
use Data::Dumper;
use GD::Graph::lines;
use MIME::Base64;


my $query = CGI->new;

my @appids = $query->param('apps');
my @browserids = $query->param('browsers');
my @buildids = $query->param('builds');
my @milestoneids = $query->param('milestones');
my @messageids = $query->param('messages');
my $plot = $query->param('plot');


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

# Restrict searches to the specified parameters
my $sqlApps = "";
my $sqlBrowsers = "";
my $sqlBuilds = "";
my $sqlMilestones = "";
my $sqlMessages = "";

# Build charts using these queries
my $sqlMessageID = "";
my $sqlColumnID = "";

my %apps = ();
my %browsers = ();
my %builds = ();
my %messages = ();
my %milestones = ();

# CHART VARIABLES
my $title;
my @legend;
my @data;
my $ymax = 5000;
my $ymin = 0;


sub imagedata {

        my $mygraph = GD::Graph::lines->new(600, 300);
        $mygraph->set(
            x_label     => 'Date',
            y_label     => 'msec',
            title       => $title,
            y_max_value => $ymax,
            y_min_value => $ymin,
#           line_types  => [1, 1],
#           line_width  => 2,
#           dclrs       => ['blue', 'cyan'],
        ) or warn $mygraph->error;

#       $mygraph->set_legend_font(GD::gdMediumBoldFont);
        $mygraph->set_legend(@legend);
        my $myimage = $mygraph->plot(\@data) or die $mygraph->error;

        return ($myimage->png);

}

sub graph {


        return (encode_base64(&imagedata()));

}

sub engine {

	# The graphe data
	my %browserData = ();
	my $maximum = 0;

	my $col = "browserid";
	my @ids = keys %browsers;
	my %hash = %browsers;
	if ( @browserids ) {
		@ids = @browserids;
	}
	if ( $plot eq "Builds" ) {
		$col = "buildid";
		@ids = keys %builds;
		%hash = %builds;
		if ( @buildids ) {
			@ids = @buildids;
		}
	}
	if ( $plot eq "Milestones" ) {
		$col = "milestoneid";
		@ids = keys %milestones;
		%hash = %milestones;
		if ( @milestoneids ) {
			@ids = @milestoneids;
		}
	}

	foreach my $id (sort @ids) {
		my $sqlPlot = "($col = $id)";

                # The data for this browser
                my @series;

		$sql = "SELECT created,appid,buildid,browserid,milestoneid,delta,messageid FROM perf WHERE ( $sqlPlot AND $sqlMessageID AND $sqlColumnID $sqlApps $sqlBrowsers $sqlBuilds $sqlMilestones $sqlMessages ) ORDER BY messageid ASC";
                $sth = $dbh->prepare($sql);
                $sth->execute();
                while ( my ($created, $appid, $buildid, $browserid, $milestoneid, $delta, $messageid ) = $sth->fetchrow_array() ) {
                        push(@series, $delta);
                }

                if ( scalar(@series) < 2 ) {
                        # IF less than 2, don't even graph it
                        next;
                }

                # Keep track of the longest series
                # since we need it to determine the
                # x series
                if ( $maximum < scalar(@series) ) {
                        $maximum = scalar(@series);
                }

                # Add this series to the data
                $browserData{$hash{$id}} = \@series;

        }

        if ( $maximum == 0 ) {
                # No data
                return;
        }

        @data = (
                [(1 .. $maximum)]
                );
	@legend = ();
        my $count = 1;
        foreach my $id (keys %browserData) {
                # Add the browser to the legend
                push(@legend, $id);
                # Add the series data
                $data[$count] = \@{$browserData{$id}};
                $count = $count + 1;
        }


        my $base64data = &graph();
        print "<img src='data:image/png;base64,$base64data' alt='Red dot'/>";

}

sub perColumn {
	my ($name, $value) = @_;

	$sqlColumnID = "($name = $value)";

	&engine();

}

sub perMessage {
	my ($messageid) = @_;

	$sqlMessageID = "(messageid = $messageid)";

	if ( ($plot eq "Builds") || ($plot eq "Milestones") ) {

		# Make one chart per browser
		my @ids = keys %browsers;
		if ( @browserids ) {
			@ids = @browserids;
		}
		foreach my $id (sort { $a <=> $b } @ids) {
			$title = "Ajax Client Perf - $browsers{$id}";
			&perColumn("browserid", $id);
		}
		
	} else { # Browsers

		# Make one chart per build
		my @ids = keys %builds;
		if ( @buildids ) {
			@ids = @buildids;
		}
		foreach my $id (sort { $a <=> $b } @ids) {
			$title = "Ajax Client Perf - $builds{$id}";
			&perColumn("buildid", $id);
		}

	}

}

sub page {

	print "Content-type: text/html\n\n" ;
	print "<html >\n";
	print "<header><title>Custom Chart</title></header>\n";
	print "<body>\n";

	print "<table >\n";

	# Use all message keys, unless message ids
	# were specified in the params
	my @ids = keys %messages;
	if ( @messageids ) {
		@ids = @messageids;
	}

	# Create one row per message
	# Column1 = Message Text
	# Column2 = Multiple charts
        foreach my $id (sort { $a <=> $b } @ids) {

                print "<tr>\n";
		print "<td>$messages{$id}</td>";
                print "<td>";
                &perMessage($id);
                print "</td>";
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

	$sql = "SELECT MAX(delta) AS delta FROM perf";
	$sth = $dbh->prepare($sql);
	$sth->execute();
	while (my ($max) = $sth->fetchrow_array()) {
		# Round to the upper hundred
		$ymax = ( (($max-($max%100))/100) + 1 ) * 100;
	}
	$sql = "SELECT MIN(delta) AS delta FROM perf";
	$sth = $dbh->prepare($sql);
	$sth->execute();
	while (my ($min) = $sth->fetchrow_array()) {
		# Round to the lower hundred
		$ymin = ( ($min-($min%100))/100 ) * 100;
	}

	$sqlApps = &buildWhere("appid", @appids) if ( @appids );
	$sqlBrowsers = &buildWhere("browserid", @browserids) if (@browserids);
	$sqlBuilds = &buildWhere("buildid", @buildids) if (@buildids);
	$sqlMilestones = &buildWhere("milestoneid", @milestoneids) if (@milestoneids);
	$sqlMessages = &buildWhere("messageid", @messageids) if (@messageids);

	# Build the page
	&page;
}

&main;

exit;
