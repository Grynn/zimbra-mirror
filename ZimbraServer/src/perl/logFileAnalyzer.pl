#!/usr/bin/perl -w
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
use strict;
use warnings;
use Getopt::Long;

my $nocase;
my $bufIn;


sub getNextLogLine();
sub getNextFileLine();

GetOptions(
           "i" => \$nocase,
          );

my $grepStr = shift @ARGV;
my $filename = shift @ARGV;

my $usage = <<END_OF_USAGE;
USAGE: $0 [-i] SEARCH_STRING FILE
END_OF_USAGE

if (!defined($grepStr)) {
  die $usage;
}
if (defined $filename) {
  open IN, "$filename" or die "Couldn't open $filename";
}

my $grepOpts = "";
if (defined $nocase) {
  $grepOpts .= "i";
}


my $line;
do {
  $line = getNextLogLine();
  if (defined($line)) {
    my $matched = 0;
    if (defined $nocase) {
      if ($line =~ /$grepStr/i) {
        $matched = 1;
      }
    } else {
      if ($line =~ /$grepStr/) {
        $matched = 1;
      }
    }
    if ($matched == 1) {
      print "$line";
    }
  }
} while (defined($line));
close IN;
exit(0);


sub getNextLogLine() {
  my $curLine = "";
  
  if (defined($bufIn)) {
    $curLine = $bufIn;
  } else {
    $curLine = getNextFileLine();
    if (!defined($curLine)) {
      return $curLine;
    }
  }

  while (1) {
    $bufIn = getNextFileLine();
    if (!defined($bufIn)) {
      return $curLine;
    }
    if ($bufIn =~ /^20[01][0-9]-[01][0-9]/) {
      return $curLine;
    } else {
      $curLine .= $bufIn;
    }
  }
}

sub getNextFileLine() {
  if (defined $filename) {
    return <IN>;
  } else {
    return <STDIN>;
  }
}
