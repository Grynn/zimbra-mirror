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

my $home_dir = $ENV{HOME};
die("Error: unable to get user home directory") unless ($home_dir);

my $app_root = $ARGV[1];
my $data_root = "$home_dir/Library/Zimbra Desktop";
my $updater_app = "$app_root/macos/prism/Prism.app/Contents/Frameworks/XUL.framework/updater.app";
my $prism_app = "$app_root/macos/prism/Prism.app";
my $zd_app = "$app_root/macos/Zimbra Desktop.app";

if ($app_root =~ /^\/private\/tmp\//) {
    open (ZDP, "</private/tmp/.zimbra_desktop_path") ||
        die("Error: unable to open zd path file");
    $app_root = <ZDP>;
    chomp($app_root);
    close(ZDP);
} else {
    system("echo \"$app_root\" > /private/tmp/.zimbra_desktop_path");
    system("mv \"${prism_app}_noreloc\" \"$prism_app\"");
    system("mv \"${updater_app}_noreloc\" \"$updater_app\"");
    system("mv \"${zd_app}_noreloc\" \"$zd_app\"");
    exit;
}

# move over data files
system("mv -f \"/private/tmp/Zimbra Desktop\" \"$home_dir/Library\"");

my $tokens = {'@install.app.root@' => $app_root, '@install.data.root@' => $data_root};

# fix data files
find_and_replace("$data_root/conf/localconfig.xml", $tokens);
find_and_replace("$data_root/jetty/etc/jetty.xml", $tokens);
find_and_replace("$data_root/bin/zdesktop", $tokens);

# install zdesktop service
my $plist = "$home_dir/Library/LaunchAgents/com.zimbra.desktop.plist";
system("cp -f \"$app_root/macos/launchd.plist\" \"$plist\"");
find_and_replace($plist, $tokens);
find_and_replace("$data_root/bin/zdesktop", {'/dev/null &' => '/dev/null'});
system("chmod +x \"$data_root/bin/zdesktop\"");
system("/bin/launchctl unload \"$plist\"");
system("/bin/launchctl load \"$plist\"");

# create desktop bundle
system("cp -f -r -p \"$app_root/macos/Zimbra Desktop.app\" \"$home_dir/Desktop\"");
find_and_replace("$home_dir/Desktop/Zimbra Desktop.app/Contents/Resources/application.ini", $tokens);

