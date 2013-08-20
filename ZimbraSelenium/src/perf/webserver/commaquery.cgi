#!/usr/bin/perl -w
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2012, 2013 Zimbra Software, LLC.
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
use DBI;
use CGI::Session;
use Data::Dumper;
use GD::Graph::lines;
use MIME::Base64;

my $session = new CGI::Session(undef, undef, { Directory => '/tmp'} );
my $sessionid = $session->id();
my $counter = 0;




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

sub radio {
	my $name = $_[0];
	my (%h) = %{$_[1]};

	my $value = "";
	
	foreach my $k (keys %h) {
		$value = $value . "<input type='radio' name='$name' value='$k'/>$h{$k}<br/>\n";
	}

	return $value;
	
}

sub select {
	my $name = $_[0];
	my $id = $_[1];
	my $multiple = $_[2];
	my $size = $_[3];
	my (%h) = %{$_[4]};
	
	
	my $multipleAttr = "";
	if ( $multiple ) {
		$multipleAttr = "multiple='multiple'";
	}

	my $sizeAttr = "";
	if ( $size ) {
		$sizeAttr = "size='$size'";
	}



	my $value = "";
	
	$value = $value .  "<select name='$name' id='$id' $multipleAttr $sizeAttr >\n";
	 if ($name eq 'builds') {
                foreach (sort { ($h{$b} cmp $h{$a}) || ($a cmp $b) } keys %h) {
                        $value = $value . "<option value='$_'>$h{$_}</option>\n";
                }
        } else {
                foreach (sort { ($h{$a} cmp $h{$b}) || ($a cmp $b) } keys %h) {
                        $value = $value . "<option value='$_'>$h{$_}</option>\n";
                }
        }

        $value = $value . "</select>\n";
        return $value;
}

#	foreach my $k (sort { $a <=> $b } keys %h) {
#		$value = $value . "<option value='$k'>$h{$k}</option>\n";
#	}
#	$value = $value . "</select>\n";

#	return $value;


sub page {

	print "Content-type: text/html\n\n" ;
	print "<html >\n";
	print "<header><title>Comma Separated Text </title></header>\n";
	print "<body>\n";

	print "<h2>Generate comma separated text</h2>\n";

	print "<form name='input' action='comma.cgi' method='get'>\n";
	print "<fieldset>\n";
	print "<legend>Restrict To:</legend>\n";

	print "<table >\n";

	print "<tr>\n";
		
		print "<th align='left'>Apps:</th>\n";
		print "<th align='left'>Browsers:</th>\n";
		print "<th align='left'>Builds:</th>\n";
		print "<th align='left'>Milestones:</th>\n";
		print "<th align='left'>Actions:</th>\n";

	print "</tr>\n";

	print "<tr>\n";

		print "<td>".  &select("apps", "apps", undef, 5, \%apps) ."</td>\n";
		print "<td>".  &select("browsers", "browsers", 1, 5, \%browsers) ."</td>\n";
		print "<td>".  &select("builds", "builds", 1, 5, \%builds) ."</td>\n";
		print "<td>".  &select("milestones", "milestones", undef, 5, \%milestones) ."</td>\n";
		print "<td>".  &select("messages", "messages", undef, 5, \%messages) ."</td>\n";

	print "</tr>\n";
	print "</table>\n";
	print "</fieldset>\n";

	print "<fieldset>\n";
	print "<legend>Plot:</legend>\n";
	print "<table>\n";
	print "<tr>\n";

		print "<td>\n";
		my %plots = ();
		$plots{'Browsers'} = "Browsers";
		$plots{'Builds'} = "Builds";
		$plots{'Milestones'} = "Milestones";
		print &radio("plot", \%plots);
		print "</td>\n";

	print "</tr>\n";
	print "</table>\n";
	print "</fieldset>\n";




	print "<input type='submit' value='Generate'/>\n";
	print "</form>\n";
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



