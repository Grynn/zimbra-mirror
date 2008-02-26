#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite J2ME Client
# Copyright (C) 2007 Zimbra, Inc.
# 
# The contents of this file are subject to the Yahoo! Public License
# Version 1.0 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
# 

my $MYSQL = "/opt/zimbra/bin/mysql";
my $DATABASE = "zimbrame";

my ($id, $file);
my @files = glob("/opt/zimbra/j2me/*.jad");
my $stmt = "UPDATE devices SET active=\"0\";\n";

foreach (@files) {
	$file = $_;
	s/^\/.+\///;
	s/.jad$//;
	$id = $_;
	if (/zimbrame-((T-\w+)|(Sony-\w+)|(\w+))-(.+)-(\w+)-(\d+\.\d+\.\d+)/) {
	    $stmt .= "REPLACE INTO devices (id,jadfile,brand,model,locale,version,active) ".
	    		  "VALUES (\"$id\",\"$file\",\"$1\",\"$5\",\"$6\",\"$7\",\"1\");\n";
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
