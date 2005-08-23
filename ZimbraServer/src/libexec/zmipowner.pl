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
Usage: zmipowner.pl -i <IP address>
   -i: IP address to check ownership of
_EOM_
    exit(-1);
}


my %opts;
getopts("i:", \%opts) or usage();
my $ip = $opts{i} or usage();
my $status = Zimbra::Failover::IPUtil::getIPStatus($ip);
print "$status\n";
