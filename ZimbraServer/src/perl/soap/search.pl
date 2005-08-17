#!/usr/bin/perl -w

#
# Simple SOAP test-harness for the AddMsg API
#

use Time::HiRes qw ( time );
use strict;

use lib '.';

use LWP::UserAgent;

use XmlElement;
use XmlDoc;
use Soap;

my $ACCTNS = "urn:liquidAccount";
my $MAILNS = "urn:liquidMail";

my $url = "http://localhost:7070/service/soap/";
#my $url = "http://dogfood:7070/service/soap/";

my $user;
my $searchString;

if (defined $ARGV[1] && $ARGV[1] ne "") {
    $user = $ARGV[0];
    $searchString = $ARGV[1];
} else {
    die "Usage search USER QUERYSTR";
}


my $SOAP = $Soap::Soap12;
my $d = new XmlDoc;
$d->start('AuthRequest', $ACCTNS);
$d->add('account', undef, { by => "name"}, "$user");
$d->add('password', undef, undef, "test123");
$d->end();

my $authResponse = $SOAP->invoke($url, $d->root());

print "AuthResponse = ".$authResponse->to_string("pretty")."\n";

my $authToken = $authResponse->find_child('authToken')->content;
print "authToken($authToken)\n";

my $sessionId = $authResponse->find_child('sessionId')->content;
print "sessionId = $sessionId\n";

my $context = $SOAP->liquidContext($authToken, $sessionId);

my $contextStr = $context->to_string("pretty");
print("Context = $contextStr\n");

#
#<SearchRequest xmlns="urn:liquidMail">
# <query>tag:\unseen</query>
#</SearchRequest>

my %msgAttrs;
#$msgAttrs{'types'} = "conversation";
$msgAttrs{'types'} = "message";
$msgAttrs{'sortby'} = "datedesc";
$msgAttrs{'offset'} = "0";
$msgAttrs{'limit'} = "10";
#$msgAttrs{'t'} = "\\unseen ,34 , \\FLAGGED";

$d = new XmlDoc;
$d->start('SearchRequest', $MAILNS, \%msgAttrs);
$d->start('query', undef, undef, $searchString);

$d->end(); # 'query'
$d->end(); # 'SearchRequest'

print "\nOUTGOING XML:\n-------------\n";
my $out =  $d->to_string("pretty"),"\n";
$out =~ s/ns0\://g;
print $out."\n";

my $start = time;
my $firstStart = time;
my $response;

$response = $SOAP->invoke($url, $d->root(), $context);
#$avg = ($lastEnd - $firstStart) / $i * 1000;
print "\nRESPONSE:\n--------------\n";
$out =  $response->to_string("pretty"),"\n";
$out =~ s/ns0\://g;
print $out."\n";

# my $i = 0;
# my $end;
# my $avg;
# my $elapsed;

# do {

#     $start = time;
#     $msgAttrs{'sortby'} = "dateDesc";
#     $end = time;
#     $elapsed = $end - $start;
#     $avg = $elapsed *1000;
#     print("Ran iter in $elapsed time ($avg ms)\n");
    
#     $start = time;
#     $msgAttrs{'sortby'} = "subjdesc";
# $response = $SOAP->invoke($url, $d->root(), $context);
#     $end = time;
#     $elapsed = $end - $start;
#     $avg = $elapsed *1000;
#     print("Ran iter in $elapsed time ($avg ms)\n");

# $i++;
# } while($i < 50) ;

# my $lastEnd = time;

#  print("\nRan $i iters in $elapsed time (avg = $avg ms)\n");



