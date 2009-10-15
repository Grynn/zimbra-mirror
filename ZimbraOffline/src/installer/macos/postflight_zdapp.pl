#!/usr/bin/perl
#/*
# * ***** BEGIN LICENSE BLOCK *****
# * 
# * Zimbra Desktop
# * Copyright (C) 2009 Zimbra, Inc.
# * 
# * The contents of this file are subject to the Yahoo! Public License
# * Version 1.0 ("License"); you may not use this file except in
# * compliance with the License.  You may obtain a copy of the License at
# * http://www.zimbra.com/license.
# * 
# * Software distributed under the License is distributed on an "AS IS"
# * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# * 
# * ***** END LICENSE BLOCK *****
# */
#
# MacOS post installation script for zimbra desktop.app
#

use strict;
use warnings;

my $home_dir = $ENV{HOME};
die("Error: unable to get user home directory") unless ($home_dir);

my $zd_app_name = "Zimbra Desktop.app";
my $zd_app_noreloc = "/private/tmp/${zd_app_name}_noreloc";
my $zd_app = "$home_dir/Desktop/$zd_app_name";

system("rm -rf \"$zd_app\"");
system("cp -R -f \"$zd_app_noreloc\" \"$zd_app\"");
system("rm -rf \"$zd_app_noreloc\"");
