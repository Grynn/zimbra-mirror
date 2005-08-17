#!/usr/bin/perl -w

use lib "$ENV{ZIMBRA_HOME}/zimbramon/lib";
use strict;
use Getopt::Std;
use Zimbra::Failover::IPUtil;

sub usage() {
    print STDERR <<_EOM_;
Usage: zmipdown.pl -i <IP address>
   -i: IP address to bring down
_EOM_
    exit(-1);
}


my %opts;
getopts("i:", \%opts) or usage();
my $ip = $opts{i} or usage();
Zimbra::Failover::IPUtil::relinquishIP($ip);
