#!/usr/bin/perl -w
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2013 Zimbra Software, LLC.
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.4 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
# 

use strict;
use CGI;
use DBI;
use Date::Parse;
use JSON;

my $query = CGI->new;

my @appids = $query->param('apps');;
my @browserids = $query->param('browsers');;
my @buildids = $query->param('builds');;
my @milestoneids = $query->param('milestones');;
my @messageids = $query->param('messages');;
my $plot = $query->param('plot');;

# The JSON object
my $json = JSON::XS->new->ascii->pretty->allow_nonref;


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

sub buildWhere {
	my ($column, @ids) = @_;

	my $where = "";
	if ( scalar(@ids) > 0 ) {
		for my $id (@ids) {
			if ( $where eq "" ) {
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

#         Example:
#
#         "c" : [
#            {
#               "v" : "Date(2013, 2, 5)",
#               "f" : null
#            },
#            {
#               "v" : 999,
#               "f" : null
#            },
#            {
#               "v" : 2013,
#               "f" : null
#            }
#         ]



	my @rows;

	my $sqlApps = &buildWhere("appid", @appids);
	my $sqlBrowsers = &buildWhere("browserid", @browserids);
	my $sqlBuilds = &buildWhere("buildid", @buildids);
	my $sqlMilestones = &buildWhere("milestoneid", @milestoneids);
	my $sqlMessages = &buildWhere("messageid", @messageids);

	$sql = "SELECT created,buildid,delta FROM perf WHERE ( (id > 0) $sqlApps $sqlBrowsers $sqlBuilds $sqlMilestones $sqlMessages ) ORDER BY created DESC LIMIT 5000";
	$sth = $dbh->prepare($sql);

	$sth->execute();
        while (my ($created, $buildid, $delta) = $sth->fetchrow_array()) {



		my @c;

		# Add all the elements (with unset) for the number
		# of builds specified
		for (my $i = 0; $i < $#buildids + 1; $i++) {
			my %e;
			$e{'v'} = undef;
			$e{'f'} = undef;
			push(@c, \%e);
		}


		# Depending on the build ID, change the index
		my( $index ) = grep { $buildids[$_] eq $buildid } 0..$#buildids;
		next unless defined($index);

		my %v_element;
		$v_element{'sql'} = "$sql";
		$v_element{'v'} = $delta;
		$v_element{'f'} = undef;
		$c[$index] = \%v_element;


		# Add the date element to the beginning

		# 'Created' looks like 2012-11-27 12:37:35
        	my ($sec,$min,$hour,$day,$month,$year,undef) = strptime($created);

		#
		my %d_element;
		$d_element{'v'} = "Date(". ($year + 1900) .", ". ($month) .", ". (0 + $day) .", ". $hour .", ". $min .", ". $sec .")";
		$d_element{'f'} = undef;
		unshift(@c, \%d_element);

		my %row;
		$row{'c'} = [ @c ];
		push(@rows, \%row);


        }

	# Return all the rows
	return (@rows);
}


sub page {


	# Build the 'cols', which is the
	# chart columns

	# First element is  the time
	my %c_time = (
		'id' => '',
		'label' =>'Time',
		'pattern' => '',
		'type'=>'datetime',
	);

	my @columns = (  \%c_time );

	# Other elements are the requested build id(s)
	foreach ( @buildids ) {
		my %element;
		$element{'id'} = '';
		$element{'pattern'} = '';
		$element{'type'} = 'number';
		$element{'label'} = $builds{$_};
		push(@columns, \%element);
	}

	# Non-chart data
	# Return the messages

	my @titles;
	foreach ( @messageids ) {
		push(@titles, $messages{$_});
	}



	# Convert the data to JSON, specifically
	# according to Google charts format
	#
	my $json;
	$json->{'cols'} = [ @columns ];
	# $json->{'cols'} = [ ( \%c_time, \%c_rel1, \%c_rel2) ];
	$json->{'rows'} = [ &data() ];
	$json->{'p'} = undef;
	$json->{'titles'} = [ @titles ];;

	print $query->header('application/json');
	print to_json($json, { pretty => 1 } );

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
