#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Version: MPL 1.1
# 
# The contents of this file are subject to the Mozilla Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite Server.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 
use strict;
use warnings;

if (@ARGV < 1) {
  my $str =<<EOF;
  Usage $0 CSVNAME [-NUMBER] [colname, colname, colname...]

  This script takes a .CSV stats file, an optional number of lines,
  and a set of column names.  It parses the CSV file, finds maps the
  requested column names to the proper column index, and then formats
  and prints the most recent values for the columns.

  Calling this script with no arguments will list the available columns.

  Example usage:
     "$0"  - list available column names
     "$0 -50 timestamp heap_free"  - Returns the most recent 50 values of the "timestamp" and "heap_free" column
EOF
  
  die $str;
}

my $filename = $ARGV[0];
my $number = 10;
my $numberSpecified = 0; # if nonzero, then a nubmer (e.g. "-10") was specified on command-line
my @colsToReturn;

if (@ARGV > 1) {
  shift @ARGV;
  @colsToReturn = @ARGV;
  if ($colsToReturn[0] =~ /^-.*/) {
    $number = -1 * $colsToReturn[0];
    shift @colsToReturn;
    $numberSpecified = 1;
  }
}

# print "file is $filename\n";
# print "number is $number\n";
# foreach my $col (@colsToReturn) {
#   print "\tcol: $col\n";
# }

my $lastColnamesRow;
my @buf;      # buffered lines of data, number requested by user 
my @colnums;  # column-numbers to print out
my %widths;   # maximum width of the columns
open IN, "<$filename" or die "Couldn't open $filename\n";

while(<IN>) {
  chomp;                        # kill trainline newline
  push @buf, $_;
  if (@buf > $number) {
    shift @buf;
  }
  
  if (/^timestamp,/) {
    $lastColnamesRow = $_;
    $#buf = -1; # clear the array
  }
}

close IN;

# map col names to col offsets
if (@colsToReturn > 0) {
  my @colnames = split(/,/, $lastColnamesRow);
  for (my $j = 0; $j < @colsToReturn; $j++) {
    my $matchedCol = 0;
    for (my $i = 0; $i < @colnames; $i++) {
      if (lc($colnames[$i]) eq lc($colsToReturn[$j])) {
        push @colnums, $i;
        my $thisLength = (length $colsToReturn[$j]); 
        $widths{ $i } = $thisLength;
        $matchedCol = 1;
      }
    }
    if ($matchedCol == 0) {
      print STDERR "Could not find a match for requested column \"$colsToReturn[$j]\"\n";
      push @colnums, -1;
      my $thisLength =  (length $colsToReturn[$j]);
      if (!defined ($widths{'-1'}) || $widths{'-1'} < $thisLength) {
        $widths{'-1'} = $thisLength;
      }
    }
  }

  # calc the column widths
  foreach (@buf) {
    my $curLine;
    my @vals = split(/,/);
    foreach (@colnums) {
      my $width = 30;
      if ($_ != -1) {
        my $thisVal = $vals[$_];
        my $thisLength = length($thisVal);
        if ($widths{$_} < $thisLength) {
          $widths{$_} = $thisLength;
        } else {
        }
      }
    }
  }

  # tell the user what columns we're going to return  
  for (my $i = 0; $i < @colsToReturn; $i++) {
    my $width = $widths{$colnums[$i]};
    print padToWidth($colsToReturn[$i], $width).", ";
  }
  print "\n";
  
  # print the column data  
  foreach (@buf) {
    my $curLine;
    my @vals = split(/,/);
    foreach (@colnums) {
      my $width = $widths{$_};
      if ($_ == -1) {
        $curLine .= padToWidth(" ", $width).", ";
      } else {
        $curLine .= padToWidth($vals[$_], $width) . ", ";
      }
    }
    print "$curLine\n";
  }
} else {
  if ($numberSpecified == 0) {
    print "Available columns:\n";
    foreach (split(",", $lastColnamesRow)) {
      print "\t$_\n";
    }
  } else {
    print "$lastColnamesRow\n";
    foreach (@buf) {
      print "$_\n";
    }
  }
}


sub padToWidth {
  (my $str, my $width) = @_;
  
  return sprintf "%".$width.".".$width."s", $str;
}
