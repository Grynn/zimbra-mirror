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
# Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 

my @tokens = (0,1,2,3,4,5,6,7,8,9);
my $datadir = "/Users/jylee/ws/main/ZimbraServer/data/wiki/loadtest/";

my $buf;

sub readTemplate($) {
    my ($name) = @_;
    open (TEMPLATE, $name) or die "can't open $name";
    my @lines = <TEMPLATE>;
    $buf = join ("\n", @lines);
    close (TEMPLATE);
}

sub writeFile($) {
    my ($name) = @_;
    open (F, ">$name") or die "can't open $name";
    print F $buf;
    close (F);
}

sub createDirs() {
    my $d1, d2, $f;
    for my $dir (@tokens) {
	$d1 = $datadir . $dir;
	mkdir $d1;
	for my $subdir (@tokens) {
	    $d2 = $d1 . "/" . $subdir;
	    mkdir $d2;
	    for my $file (@tokens) {
		$f = $d2 . "/" . $file;
		writeFile($f);
	    }
	}
    }
}

sub main() {
    readTemplate($datadir . "template");
    createDirs();
}


main();
