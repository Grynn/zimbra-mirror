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
# MacOS post installation script
#

use strict;
use warnings;

my $app_root = $ARGV[1];
my $updater_app = "$app_root/macos/prism/Prism.app/Contents/Frameworks/XUL.framework/updater.app";
my $prism_app = "$app_root/macos/prism/Prism.app";

system("mv \"${prism_app}_noreloc\" \"$prism_app\"");
system("mv \"${updater_app}_noreloc\" \"$updater_app\"");
system("mv \"$app_root/macos/Zimbra Desktop.app_noreloc\" \"$app_root/Zimbra Desktop.app\"");
system("chown -R root:admin \"$app_root\"");

