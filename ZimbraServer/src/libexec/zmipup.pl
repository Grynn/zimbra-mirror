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

use lib "$ENV{ZIMBRA_HOME}/zimbramon/lib";
use strict;
use Getopt::Std;
use Zimbra::Failover::IPUtil;

sub usage() {
    print STDERR <<_EOM_;
Usage: zmipup.pl -d <eth device> -i <IP address> -r <router IP>
   -d: network interface device
   -i: IP address to bring up
   -r: next-hop router IP
_EOM_
    exit(-1);
}


my %opts;
getopts("d:i:r:", \%opts) or usage();
my $device = $opts{d} or usage();
my $ip = $opts{i} or usage();
my $router = $opts{r} or usage();

Zimbra::Failover::IPUtil::takeoverIP($device, $ip, $router);
my $status = Zimbra::Failover::IPUtil::getIPStatus($ip);
if ($status eq 'conflict') {
    print STDERR "IP came up; conflict detected\n";
    exit(-2);
} elsif ($status ne 'local') {
    print STDERR "IP not up: status=$status\n";
    exit(-3);
}
