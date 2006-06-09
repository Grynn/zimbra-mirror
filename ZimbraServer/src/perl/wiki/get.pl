#!/usr/bin/perl

use threads;
use LWP::UserAgent;
use HTTP::Request;
use Time::HiRes qw(gettimeofday);

my @buckets = (0,0,0,0,0,0,0,0,0,0,0);
my $num_threads = 0;
my $iteration = 0;
my $username = 'wikiuser';
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
    my $s0, $usec0, $s1, $usec1, $url, $resp;

    for (my $i = 0; $i < $iteration; $i++) {
	$url = int(rand(10)) . "/" . int(rand(10)) . "/" . int(rand(10));
	($s0, $usec0) = gettimeofday();
	$resp = sendRequest($url);
	($s1, $usec1) = gettimeofday();

	if ($resp->code() != 200) {
	    lock($buckets);
	    $buckets[0]++;
	}

	my $interval;

	$interval = $usec1 - $usec0;
	if ($interval < 0) {
	    $interval = $interval + 1000000;
	}

	$interval = int ($interval / 20000);
	if ($interval > 9) {
	    $interval = 9;
	}

	$interval++;

	{
	    lock($buckets);
#	    print "BEF: " . $buckets[$interval] ;
	    $buckets[$interval]++;
#	    printf "  CUR: $interval";
#	    print "  AFT: " . $buckets[$interval] . "\n";
        }
    }

#    tabulate($buckets);
}

sub tabulate($) {
    my ($buckets) = @_;
    printf "\nerror  200  400  600  800 1000 1200 1400 1600 1800 2000\n";
    printf "\n-------------------------------------------------------\n";

    foreach (@buckets) {
	printf(" %4d", $_);
    }
    printf "\n-------------------------------------------------------\n";
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

tabulate($buckets);
