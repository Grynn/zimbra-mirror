#!/usr/bin/perl -w

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
