#!/usr/bin/perl -w
# 
# ***** BEGIN LICENSE BLOCK *****
# Version: ZPL 1.1
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 

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

my $user;
my $convId;
my $searchString;

if (defined $ARGV[2] && $ARGV[2] ne "") {
    $user = $ARGV[0];
    $convId = $ARGV[1];
    $searchString = $ARGV[2];
} else {
    die "Usage search USER CONVID QUERYSTR";
}

my $SOAP = $Soap::Soap12;
my $d = new XmlDoc;
$d->start('AuthRequest', $ACCTNS);
$d->add('account', undef, { by => "name"}, $user);
$d->add('password', undef, undef, "test123");
$d->end();

my $authResponse = $SOAP->invoke($url, $d->root());

print "AuthResponse = ".$authResponse->to_string("pretty")."\n";

my $authToken = $authResponse->find_child('authToken')->content;

print "authToken($authToken)\n";

my $context = $SOAP->zimbraContext($authToken);

#
#<SearchRequest xmlns="urn:zimbraMail">
# <query>tag:\unseen</query>
#</SearchRequest>

my %msgAttrs;
#$msgAttrs{'l'} = "/sent mail";
#$msgAttrs{'t'} = "\\unseen ,34 , \\FLAGGED";

$d = new XmlDoc;
my %queryAttrs;
$queryAttrs{'cid'} = $convId;
$queryAttrs{'sortby'} = "datedesc";
$queryAttrs{'groupBy'} = "none";
$queryAttrs{'fetch'} = "1";
$d->start('SearchConvRequest', $MAILNS, \%queryAttrs);

$d->start('query', undef, undef, $searchString);

$d->end(); # 'query'
$d->end(); # 'SearchRequest'

print "\nOUTGOING XML:\n-------------\n";
print $d->to_string("pretty"),"\n";

my $response = $SOAP->invoke($url, $d->root(), $context);


print "\nRESPONSE:\n--------------\n";
my $str = $response->to_string("pretty")."\n";
$str =~ s/<\/?ns0\:/</g;
print $str;

