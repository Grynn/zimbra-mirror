#!/usr/bin/perl -w

use lib "$ENV{LIQUID_HOME}/liquidmon/lib";
use strict;
use Getopt::Std;
use Liquid::Failover::IPUtil;

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
Liquid::Failover::IPUtil::relinquishIP($ip);
