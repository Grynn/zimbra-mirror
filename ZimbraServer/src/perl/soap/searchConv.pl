#!/usr/bin/perl -w

#
# Simple SOAP test-harness for the AddMsg API
#

use strict;

use lib '.';

use LWP::UserAgent;

use XmlElement;
use XmlDoc;
use Soap;

my $ACCTNS = "urn:zimbraAccount";
my $MAILNS = "urn:zimbraMail";

my $url = "http://localhost:7070/service/soap/";

my $SOAP = $Soap::Soap12;
my $d = new XmlDoc;
$d->start('AuthRequest', $ACCTNS);
$d->add('account', undef, { by => "name"}, 'user1@liquidsys.com');
$d->add('password', undef, undef, "test123");
$d->end();

my $authResponse = $SOAP->invoke($url, $d->root());

print "AuthResponse = ".$authResponse->to_string("pretty")."\n";

my $authToken = $authResponse->find_child('authToken')->content;

print "authToken($authToken)\n";

my $context = $SOAP->liquidContext($authToken);

#
#<SearchRequest xmlns="urn:zimbraMail">
# <query>tag:\unseen</query>
#</SearchRequest>

my %msgAttrs;
#$msgAttrs{'l'} = "/sent mail";
#$msgAttrs{'t'} = "\\unseen ,34 , \\FLAGGED";

$d = new XmlDoc;
my %queryAttrs;
$queryAttrs{'cid'} = "288"; #503
$queryAttrs{'sortBy'} = "nameAsc";
#$queryAttrs{'offset'} = "1"; 
#$queryAttrs{'limit'} = "2";
$queryAttrs{'groupBy'} = "none";
$d->start('SearchConvRequest', $MAILNS, \%queryAttrs);

$d->start('query', undef, undef, "implementation");

$d->end(); # 'query'
$d->end(); # 'SearchRequest'

print "\nOUTGOING XML:\n-------------\n";
print $d->to_string("pretty"),"\n";

my $response = $SOAP->invoke($url, $d->root(), $context);


print "\nRESPONSE:\n--------------\n";
my $str = $response->to_string("pretty")."\n";
$str =~ s/<\/?ns0\:/</g;
print $str;

