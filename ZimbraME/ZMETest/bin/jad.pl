#!/usr/bin/perl

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
