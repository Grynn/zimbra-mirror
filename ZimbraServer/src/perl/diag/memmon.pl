#!/usr/bin/perl -w
# 
# ***** BEGIN LICENSE BLOCK *****
# Version: ZPL 1.1
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite.
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
use Getopt::Std;

my $PIDFILE = $ENV{ZIMBRA_HOME} . "/log/tomcat.pid";

sub usage() {
    print <<USAGE;
Monitor memory usage of a process over time.
At every interval prints "time<tab>RSS<tab>VIRT".
Usage: memmon.pl [-i <interval>] [-p <pid>]
   -i: interval between samples, in number of seconds (default 10)
   -p: process ID to monitor
       By default, pid stored in /opt/zimbra/log/tomcat.pid file is used.
USAGE
    exit(1);
}

sub readPid() {
    my $pid = `cat $PIDFILE`;
    chomp($pid) if (defined($pid));
    if (!$pid) {
        print STDERR "Tomcat not running!\n";
        exit(1);
    }
    return $pid;
}


my %opts;
getopts("i:p:", \%opts) or usage();

my $interval = $opts{i} || 10;
if ($interval < 1) {
    $interval = 1;
}
my $pid = $opts{p} || readPid();


while (1) {
    my ($sec, $min, $hour, $mday, $mon, $year, $wday, $yday, $isdst) =
        localtime();
    my $date = sprintf("%02d/%02d/%04d %02d:%02d:%02d",
                       $mon + 1, $mday, $year + 1900, $hour, $min, $sec);
    my $ps = `ps -p $pid -o rss,size --no-headers`;
    $ps |= '';
    chomp($ps);
    if ($ps =~ /^(\d+)\s+(\d+)$/) {
        print "$date\t$1\t$2\n";  # tab-delimited time, RSS, VIRT
    } else {
        print "$date\tProcess $pid went away!  Exiting.\n";
        exit(1);
    }
    sleep($interval);
}
