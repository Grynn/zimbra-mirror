#!/usr/bin/perl
#/*
# * ***** BEGIN LICENSE BLOCK *****
# * Zimbra Collaboration Suite Server
# * Copyright (C) 2009, 2010 Zimbra, Inc.
# * 
# * The contents of this file are subject to the Zimbra Public License
# * Version 1.3 ("License"); you may not use this file except in
# * compliance with the License.  You may obtain a copy of the License at
# * http://www.zimbra.com/license.
# * 
# * Software distributed under the License is distributed on an "AS IS"
# * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# * ***** END LICENSE BLOCK *****
# */
#
# MacOS post installation script
#

use strict;
use warnings;

sub find_and_replace($$) {
    my ($file, $tokens) = @_;
    my $tmpfile = $file . '.tmp';
   
    open(FIN, "<$file") or die("Error: cannot open file $file\n");
    open(FOUT, ">$tmpfile") or die("Error: cannot open file $tmpfile\n");
   
    my $line;
    while($line = <FIN>){
        foreach my $key (keys(%$tokens)) {
            my $pos = index($line, $key);
            while ($pos >= 0) {
                substr($line, $pos, length($key), $tokens->{$key});
                $pos = index($line, $key);
            }
        }
        print FOUT $line;
    }
   
    close FIN;
    close FOUT;
   
    my (undef, undef, $mode) = stat($file);
    unlink $file;
    rename $tmpfile, $file;
    chmod $mode, $file;
}

my $app_root = $ARGV[1];
my $updater_app = "$app_root/macos/prism/Prism.app/Contents/Frameworks/XUL.framework/updater.app";
my $prism_app = "$app_root/macos/prism/Prism.app";

system("mv \"${prism_app}_noreloc\" \"$prism_app\"");
system("mv \"${updater_app}_noreloc\" \"$updater_app\"");
system("mv \"$app_root/macos/Zimbra Desktop.app_noreloc\" \"$app_root/Zimbra Desktop.app\"");
system("chown -R root:admin \"$app_root\"");

my $tokens = {
    '@INSTALL.APP.ROOT@' => $app_root,
    '@INSTALL.APP.TIMESTAMP@' => time()
};
find_and_replace("$app_root/Zimbra Desktop.app/Contents/MacOS/zdrun", $tokens);

# open zd app in finder
# system("open \"$app_root\"");

# launch zd
system("open \"$app_root/Zimbra Desktop.app\"");

