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

use Time::HiRes qw ( time );
use strict;

use lib '.';

use LWP::UserAgent;

use XmlElement;
use XmlDoc;
use Soap;

######################################################################
#
# Set these here right now in lieu of command-line args
#
######################################################################
#my $HOST = 'dogfood';
#my $USER = 'tim@dogfood.example.zimbra.com';
my $HOST = 'localhost';
my $USER = 'user1@example.zimbra.com';
######################################################################





my $ACCTNS = "urn:zimbraAccount";
my $MAILNS = "urn:zimbraMail";


my $url = "http://$HOST:7070/service/soap/";

my $SOAP = $Soap::Soap12;
my $d = new XmlDoc;
$d->start('AuthRequest', $ACCTNS);
$d->add('account', undef, { by => "name"}, $USER);
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


# <SaveRulesRequest>
#   <rules>
#     <r name="test">
#       <g op="anyof">
#         <c name="address" mod=":all" op=":contains" k0="From" k1="tim@curple.com"/>
#       </g>
#       <action name="tag">
#         <arg>fromme</arg>
#       </action>   
#     </r>
#   </rules>
# </SaveRulesRequest>

$d = new XmlDoc;
$d->start('SaveRulesRequest', $MAILNS, undef);
{
    my %r1Attrs;
    $r1Attrs{'name'} = "test";
    $d->start('rules', undef, undef);
    $d->start('r', undef, \%r1Attrs);
    {
        my %gAttrs;
        $gAttrs{'op'} = "anyof";
        $d->start('g', undef, \%gAttrs);
        {
            my %c1Attrs;
            $c1Attrs{'name'} = "address";
            $c1Attrs{'mod'} = ":all";
            $c1Attrs{'op'} = ":contains";
            $c1Attrs{'k0'} = "From";
            $c1Attrs{'k1'} = "tim\@curple.com";
            $d->start('c', undef, \%c1Attrs);
            $d->end(); # 'c
        }
        $d->end(); # 'g
        
        my %aAttrs;
        $aAttrs{'name'} = "tag";
        $d->start('action', undef, \%aAttrs);
        {
            $d->start('arg', undef, undef, "fromme");
            $d->end(); # 'arg'
            
        }
        $d->end(); # 'action

    }
    $d->end(); # 'r name=test'
    $d->end(); # 'rules'
}
$d->end(); # 'SaveRulesRequest'

print "\nOUTGOING XML:\n-------------\n";
my $out =  $d->to_string("pretty"),"\n";
$out =~ s/ns0\://g;
print $out."\n";

my $response = $SOAP->invoke($url, $d->root(), $context);
print "\nRESPONSE:\n--------------\n";
$out =  $response->to_string("pretty"),"\n";
$out =~ s/ns0\://g; #hack - remove the soap namespace, makes response more pleasant on my eye
print $out."\n";



