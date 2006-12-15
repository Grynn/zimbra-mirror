#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Version: MPL 1.1
# 
# The contents of this file are subject to the Mozilla Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite Server.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 

use threads;
use LWP::UserAgent;
use HTTP::Request;
use Time::HiRes qw(gettimeofday);

my @buckets = (0,0,0,0,0,0,0,0,0,0);
my $num_threads = 0;
my $iteration = 0;
my $username = 'wikitest';
my $password = 'test123';

sub sendRequest($) {
    my ($url) = @_;
    my $ua, $req, $resp;

    $req = new HTTP::Request ('GET', "http://localhost:7070/home/$username/" . $url);

    $ua = new LWP::UserAgent;
    $ua->credentials('localhost:7070', 'Zimbra', $username, $password);

    $resp = $ua->request($req);
    return $resp;
}

sub runTests() {
    my $s0, $usec0, $s1, $usec1, $url, $resp, $nerror = 0, $min = 999999999, $max = 0, $sum = 0;

    for (my $i = 0; $i < $iteration; $i++) {
	$url = int(rand(10)) . "/" . int(rand(10)) . "/" . int(rand(10));
	($s0, $usec0) = gettimeofday();
	$resp = sendRequest($url);
	($s1, $usec1) = gettimeofday();

	if ($resp->code() != 200) {
		$nerror++;
	}

	my $interval;

	$interval = $usec1 - $usec0;
	if ($interval < 0) {
	    $interval = $interval + 1000000;
	}

	$sum += $interval;
	if ($interval < $min) {
		$min = $interval;
	}
	if ($interval > $max) {
		$max = $interval;
	}
	
	$interval = int ($interval / 20000);
	if ($interval > 9) {
	    $interval = 9;
	}

	{
	    lock($buckets);
#	    print "BEF: " . $buckets[$interval] ;
	    $buckets[$interval]++;
#	    printf "  CUR: $interval";
#	    print "  AFT: " . $buckets[$interval] . "\n";
        }
    }

    tabulate($nerror, $min / 1000, $max / 1000, ($sum / $iteration) / 1000, $buckets);
}

sub tabulate($$$$$) {
    my ($nerror, $min, $max, $avg, $buckets) = @_;
    printf "\nerror  min  max  avg | Latency   20   40   60   80  100  120  140  160  180  200\n";
    printf "\n--------------------------------------------------------------------------------\n";
	printf(" %4d %4d %4d %4d |        ", $nerror, $min, $max, $avg);
    foreach (@buckets) {
	printf(" %4d", $_);
    }
    printf "\n--------------------------------------------------------------------------------\n";
}

sub mergeArrays($$) {
    my (@arr1, @arr2) = @_;
    for (my $i = 0; $i < $#arr1; $i++) {
	$arr1[$i] += $arr2[$i];
    }
}

if (defined($ARGV[0])) {
    $num_threads = $ARGV[0];
}
if (defined($ARGV[1])) {
    $iteration = $ARGV[1];
}

my @th;

for (my $i = 0; $i < $num_threads; $i++) {
    $th[$i] = threads->create('runTests');
}
for (my $i = 0; $i < $num_threads; $i++) {
    $th[$i]->join();
}

#tabulate($buckets);
