#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2005, 2007 Zimbra, Inc.
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

if (@ARGV[0] eq "") {
    print "USAGE: t2d SECONDS_OR_MILLISECONDS_SINCE_EPOCH\n";
    exit(1);
}
my $str = @ARGV[0];
                
if ($str > 9999999999) {
    $str /= 1000;
}

my $now_string = localtime($str);

print "$now_string\n";
