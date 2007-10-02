#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite, Network Edition.
# Copyright (C) 2007 Zimbra, Inc.  All Rights Reserved.
# 
# ***** END LICENSE BLOCK *****
# 

my $MYSQL = "/opt/zimbra/bin/mysql";
my $DATABASE = "zimbrame";

my ($id, $file);
my $stmt = "";
my @files = glob("/opt/zimbra/j2me/*.jad");

foreach (@files) {
	$file = $_;
	s/^\/.+\///;
	s/.jad$//;
	$id = $_;
	if (/zimbrame-(\w+)-(.+)-(\w+)-(\d+\.\d+\.\d+)/) {
	    $stmt .= "REPLACE INTO devices (id,jadfile,brand,model,locale,version) ".
	    		  "VALUES (\"$id\",\"$file\",\"$1\",\"$2\",\"$3\",\"$4\");\n";
	}
}

my $command = "$MYSQL --database=$DATABASE --batch --skip-column-names";

unless (open(MYSQL, "| $command")) {
	print "unable to run mysql\n";
	exit(1);
}

print(MYSQL $stmt);
close(MYSQL);

if ($? != 0) {
	print "mysql returned an error\n";
	exit(1);
}
