#!/usr/bin/perl -w

use lib "$ENV{LIQUID_HOME}/liquidmon/lib";
use strict;
use Getopt::Std;
use Liquid::Failover::IPUtil;

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
my $status = Liquid::Failover::IPUtil::getIPStatus($ip);
print "$status\n";
