#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
# 
# The contents of this file are subject to the Yahoo! Public License
# Version 1.0 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# 
# ***** END LICENSE BLOCK *****
# 

# look for files named "archiver.log.nnn" and output CSV summary of
# the "Timing Averages for..." data for Indexer and Archiver in CSV
# format

use strict;

if (@ARGV[0] eq "") {
    print("\nUSAGE: sumtocsv.pl DIRECTORY\n");
    print("\n\tlooks for files named \"archiver.log.nnn\" and output CSV summary of Timing Averages for Indexer and Archiver in CSV format\n");
    exit(1);
}

my $IN_DIR = @ARGV[0];

my @file_list;

opendir(DIR, $IN_DIR) or die "couldn't open directory ".$IN_DIR."\n";
my $file;
my %files;
while (defined($file = readdir(DIR))) {
    if ($file =~ /archiver\.log\.([0-9]+)/) {
	push(@file_list, $file);
	$files{$1} = $file;
    }
}
close(DIR);

print("Num, Indexer:IndexUpdate, Indexer:DB Update, Indexer: Total, IndexerItems, Archiver:Journal Parsing, Archiver:Cov/Idx Prep, Archiver:DB Update, Archiver:Total, ArchiverItems, Total Time, Total Messages\n");
my $i = 0;
while ($i < 500) {
    process_file($i, $files{$i});
    $i++;
}


sub process_file
{
    (my $num, my $fn) = @_;

#    print("Processing ".$num." ".$fn."\n");

    $fn = $IN_DIR."/".$fn;
    open(IN, "<".$fn) or die "Couldn't open file ".$fn;

    my $indexerTime;
    my $archiverTime;
    my $totalTime;
    my $totalMessages;
    
    while(<IN>) {
# 2004-07-23 19:13:28,112 INFO  [IndexerMain]     - Timing Averages for Indexer: 55.846ms, 15.318ms, 71.194ms (1950 items)
	if (/Indexer: ([0-9]+\.[0-9]+)ms,\s+([0-9]+\.[0-9]+)ms,\s+([0-9]+\.[0-9]+)ms.*\(([0-9]+) items/) {
	    $indexerTime = "$1, $2, $3, $4";
	}
#2004-07-23 19:13:31,336 INFO  [ArchiverMain]     - Timing Averages for Archiver: 5.112ms, 5.642ms, 180.932ms, 192.629ms (2000 items)
	if (/Archiver: ([0-9]+\.[0-9]+)ms,\s+([0-9]+\.[0-9]+)ms,\s+([0-9]+\.[0-9]+)ms,\s+([0-9]+\.[0-9]+)ms.*\(([0-9]+) items/) {
	    $archiverTime = "$1, $2, $3, $4, $5";

	}
	if (/Total running time: ([0-9]+\.[0-9]+)/) {
	    $totalTime = $1;
	}
	if (/Finished processing ([0-9]+)/) {
	    $totalMessages = $1;
	    print("$num, $indexerTime, $archiverTime, $totalTime, $totalMessages\n");
	}
    }
    close(IN);
}
