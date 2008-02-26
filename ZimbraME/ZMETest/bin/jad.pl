#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
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
# 
# ***** END LICENSE BLOCK *****
# 

my $filename = shift;
my $version = shift;
my $sz = -s "dist/$filename.jar";
my $jad = <<EOF_JAD;
MIDlet-Name: ZMETest
MIDlet-Version: $version
MIDlet-Vendor: Zimbra
MIDlet-Jar-URL: $filename.jar
MIDlet-Jar-Size: $sz
MIDlet-Description: Zimbra Mobile Tester
MIDlet-Info-URL: http://www.zimbra.com
MIDlet-1: ZMETest,AppLogo.png,com.zimbra.zme.ZMETest
MIDlet-Delete-Confirm: Do you really want to uninstall ZMETest?
Midlet-Icon: AppLogo.png
EOF_JAD

open JAD, "> dist/$filename.jad" or die "Can't open $filename.jad";
print JAD $jad;
close JAD;
