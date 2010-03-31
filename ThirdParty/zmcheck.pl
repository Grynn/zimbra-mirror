#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2008 Zimbra, Inc.
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
use Getopt::Std;

my %options;
my $exit_status=0;

getopts('bls',\%options) or die "Unable to set options\n";

if ( $options{b} + $options{l} + $options{s} > 1) {
  print "Error: Only one of binary, library, or sbinary can be selected\n";
  $exit_status=1;
}

if ( $options{b} + $options{l} + $options{s} < 1) {
  print "Error: One of binary, library, or sbinary must be selected\n";
  $exit_status=1;
}

if ($#ARGV < 0) {
  print "Error: File(s) to check must be supplied as arguments\n";
  $exit_status=1;
}

foreach my $file (@ARGV) {
  if ( $options{b} ) {
    check_bin($file);
  }
  if ( $options{l} ) {
    check_lib($file);
  }
  if ( $options{s} ) {
    check_sbin($file);
  }
}

exit $exit_status;

sub check_sbin () {
  my ($m) = @_;
  if (!-x $m) {
    print "Error: $m not executable\n";
    $exit_status = 1;
  }
}

sub check_bin() {
  my ($m) = @_;
  if (!-x $m) {
    print "Error: $m not executable\n";
    $exit_status = 1;
  }
}

sub check_lib() {
  my ($m) = @_;
  if (!-f $m) {
    print "Error: $m not found\n";
    $exit_status = 1;
  }
}
