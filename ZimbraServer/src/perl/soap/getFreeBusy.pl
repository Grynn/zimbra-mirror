#!/usr/bin/perl -w
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2005 Zimbra, Inc.
# 
# The contents of this file are subject to the Yahoo! Public License
# Version 1.0 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# 
# ***** END LICENSE BLOCK *****
# 

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

my $userIds;
my $startTime = 0;
my $endTime = 1158575000000;

if (defined($ARGV[2]) && $ARGV[2] ne "") {
    $userIds = $ARGV[0];
    $startTime = str2time($ARGV[1]);
    $endTime = str2time($ARGV[2]);
    my $startEng = localtime($startTime);
    my $endEnd = localtime($endTime);
    print "Requesting summaries from $startEng TO $endEnd\n";
    #convert to MSEC time
    $startTime = $startTime * 1000;
    $endTime = $endTime * 1000;
} else {
    print "USAGE: getFreeBusy.pl USERIDS START-DATE END-DATE";
    exit 1;
}

my $ACCTNS = "urn:zimbraAccount";
my $MAILNS = "urn:zimbraMail";

my $url = "http://localhost:7070/service/soap/";

my $SOAP = $Soap::Soap12;
my $d = new XmlDoc;
$d->start('AuthRequest', $ACCTNS);
$d->add('account', undef, { by => "name"}, "user1");
$d->add('password', undef, undef, "test123");
$d->end();

my $authResponse = $SOAP->invoke($url, $d->root());

print "AuthResponse = ".$authResponse->to_string("pretty")."\n";

my $authToken = $authResponse->find_child('authToken')->content;
print "authToken($authToken)\n";

my $sessionId = $authResponse->find_child('sessionId')->content;
print "sessionId = $sessionId\n";

my $context = $SOAP->zimbraContext($authToken, $sessionId);

my $contextStr = $context->to_string("pretty");
print("Context = $contextStr\n");

#

my %getApptSumAttrs = (
                       's' => "$startTime",
                       'e' => "$endTime",
                       'uid' => $userIds,
                       );


$d = new XmlDoc;
$d->start('GetFreeBusyRequest', $MAILNS, \%getApptSumAttrs);

$d->end(); # 'GetFreeBusyRequest'

print "\nOUTGOING XML:\n-------------\n";
my $out =  $d->to_string("pretty")."\n";
$out =~ s/ns0\://g;
print $out."\n";

my $response;

$response = $SOAP->invoke($url, $d->root(), $context);
print "\nRESPONSE:\n--------------\n";
$out =  $response->to_string("pretty")."\n";
print $out."\n";
