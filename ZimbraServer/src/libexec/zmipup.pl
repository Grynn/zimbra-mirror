#!/usr/bin/perl -w

use lib "$ENV{LIQUID_HOME}/liquidmon/lib";
use strict;
use Getopt::Std;
use Liquid::Failover::IPUtil;

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

Liquid::Failover::IPUtil::takeoverIP($device, $ip, $router);
my $status = Liquid::Failover::IPUtil::getIPStatus($ip);
if ($status eq 'conflict') {
    print STDERR "IP came up; conflict detected\n";
    exit(-2);
} elsif ($status ne 'local') {
    print STDERR "IP not up: status=$status\n";
    exit(-3);
}
