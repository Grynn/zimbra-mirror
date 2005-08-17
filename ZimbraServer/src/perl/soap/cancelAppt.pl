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

my $ACCTNS = "urn:zimbraAccount";
my $MAILNS = "urn:zimbraMail";

my $invId;
my $userId;
if (defined($ARGV[1]) && $ARGV[1] ne "") {
    $userId = $ARGV[0];
    $invId = $ARGV[1];
} else {
    print ("USAGE: cancelAppt USERID INVITE-ID\n");
    exit ;
}

my $url = "http://localhost:7070/service/soap/";

my $SOAP = $Soap::Soap12;
my $d = new XmlDoc;
$d->start('AuthRequest', $ACCTNS);
$d->add('account', undef, { by => "name"}, $userId);
$d->add('password', undef, undef, "test123");
$d->end();

my $authResponse = $SOAP->invoke($url, $d->root());

print "AuthResponse = ".$authResponse->to_string("pretty")."\n";

my $authToken = $authResponse->find_child('authToken')->content;
#print "authToken($authToken)\n";

my $sessionId = $authResponse->find_child('sessionId')->content;
#print "sessionId = $sessionId\n";

my $context = $SOAP->liquidContext($authToken, $sessionId);

my $contextStr = $context->to_string("pretty");
#print("Context = $contextStr\n");

#######################################################################
#
# CreateAppointment
#
my $now  = int(time());
print("now is ".$now."\n");

my $nowPlus1Hr = $now+60*60;
my $nowPlus2Hr = $now+2*60*60;

# convert from secs to msecs for server
$nowPlus1Hr *= 1000;
$nowPlus2Hr *= 1000;


print "1 hour from now is ".($nowPlus2Hr)."\n";


$d = new XmlDoc;

$d->start('CancelAppointmentRequest', $MAILNS, { 'id' => $invId, 'comp' => '0' });

$d->start('m', undef, undef, undef);

if ($userId eq "user1") {
    $d->add('e', undef, { 'a' => "tim\@liquidsys.com",
                          't' => "t" } );

    $d->add('e', undef,
            {
                'a' => "user2\@timbre.liquidsys.com",
                't' => "t"
                } );

    $d->add('e', undef,
            {
                'a' => "user1\@timbre.liquidsys.com",
                't' => "t"
                } );
} else {
}

$d->add('su', undef, undef, "CANCELLED: TEST MEETING2");


$d->start('mp', undef, { 'ct' => "text/plain" });
$d->add('content', undef, undef, "This meeting has been cancelled");
$d->end(); #mp

$d->end(); # m
$d->end(); # ca


print "\nOUTGOING XML:\n-------------\n";
my $out =  $d->to_string("pretty")."\n";
$out =~ s/ns0\://g;
print $out."\n";

my $response;

$response = $SOAP->invoke($url, $d->root(), $context);
print "\nRESPONSE:\n--------------\n";
$out =  $response->to_string("pretty")."\n";
print $out."\n";
