#!/usr/bin/perl -w

#
# Simple SOAP test-harness for the AddMsg API
#

use Date::Parse;
use Time::HiRes qw ( time );
use strict;

use lib '.';

use LWP::UserAgent;

use XmlElement;
use XmlDoc;
use Soap;

my $userId;
my $msgID = 0;

if (defined($ARGV[1]) && $ARGV[1] ne "") {
    $userId = $ARGV[0];
    $msgID = $ARGV[1];
} else {
    die "USAGE: sendInviteReply INVITE-MESSAGE-ID";
}

my $ACCTNS = "urn:liquidAccount";
my $MAILNS = "urn:liquidMail";

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
print "authToken($authToken)\n";

my $sessionId = $authResponse->find_child('sessionId')->content;
print "sessionId = $sessionId\n";

my $context = $SOAP->liquidContext($authToken, $sessionId);

my $contextStr = $context->to_string("pretty");
print("Context = $contextStr\n");

$d = new XmlDoc;
$d->start('SendInviteReplyRequest', $MAILNS,
          {
              'id' => $msgID,
              'compNum' => "0",
#              'verb' => "ACCEPT"
                  'verb' => "TENTATIVE"
              }

          );
$d->start('m', undef, { 'l' => "/INBOX" }, undef);

$d->add('e', undef,
        {
            'a' => "user1\@timbre.liquidsys.com",
            't' => "t"
            } );

$d->add('su', undef, undef, "ACCEPTED: $userId");

$d->start('mp', undef, { 'ct' => "text/plain" });
$d->add('content', undef, undef, "$userId is TENTATIVE for your request!  Hmm.");
$d->end(); #mp (text/plain )

$d->end(); # m

$d->end(); # SendInviteReplyRequest

print "\nOUTGOING XML:\n-------------\n";
my $out =  $d->to_string("pretty")."\n";
$out =~ s/ns0\://g;
print $out."\n";

my $start = time;
my $firstStart = time;
my $response;

my $i = 0;
my $end;
my $avg;
my $elapsed;

$start = time;
$response = $SOAP->invoke($url, $d->root(), $context);
print "\nRESPONSE:\n--------------\n";
$out =  $response->to_string("pretty")."\n";
$out =~ s/ns0\://g;
print $out."\n";

