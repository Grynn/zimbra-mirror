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
my $URL = 'http://localhost:7070/service/soap';
my $USER = 'user1';
my $PASSWORD = "test123";
######################################################################


my $ACCTNS = "urn:zimbraAccount";
my $MAILNS = "urn:zimbraMail";

my $SOAP = $Soap::Soap12;
my $d = new XmlDoc;
$d->start('AuthRequest', $ACCTNS);
$d->add('account', undef, { by => "name"}, $USER);
$d->add('password', undef, undef, $PASSWORD);
$d->end();

my $authResponse = $SOAP->invoke($URL, $d->root());

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
#         <c name="address" mod=":all" op=":contains" k0="From" k1="foo@bar.com"/>
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
    $d->start('rules', undef, undef);
    {
        ##
        ## Bugmail
        ##
        ## <r name="Bugmail" active="1">
        ##   <g op="anyof">
        ##     <c name="header" k0="from" k1="bugzilla-daemon@depot.liquidsys.com" op=":contains"/>
        ##   </g>
        ##   <action name="tag">
        ##     <arg>"Bugmail"</arg>
        ##   </action>
        ## </r>
        ##     
        $d->start('r', undef, { 'name' => "Bugmail", 'active' => "1" } ); 
        {
            $d->start('g', undef, { 'op' => "anyof" });
            {
                $d->add('c', undef, { 'name' => "header",
                                      'op' => ":contains",
                                      'k0' => "from",
                                      'k1' => " bugzilla-daemon\@depot.liquidsys.com" });
            } $d->end(); # 'g

            $d->start('action', undef, { 'name' => "tag"} ); {
                $d->add('arg', undef, undef, "Bugmail");
            } $d->end(); #action
            
        } $d->end(); # 'r 

        
        ##
        ## Checkins
        ##
        ## <r name="Checkins" active="1">
        ##   <g op="anyof">
        ##     <c k1="Indeed" name="header" k0="X-From-Perforce" op=":contains"/>
        ##   </g>
        ##   <action name="fileinto">
        ##     <arg>"/Checkins"</arg>
        ##   </action>
        ## </r>
        ##
        $d->start('r', undef, { 'name' => "Checkins", 'active' => "1" } ); 
        {
            $d->start('g', undef, { 'op' => "anyof" });
            {
                $d->add('c', undef, { 'name' => "header",
                                      'op' => ":contains",
                                      'k0' => "X-From-Perforce",
                                      'k1' => "Indeed"});
            } $d->end(); # 'g
            
            $d->start('action', undef, { 'name' => "fileinto" }); {
                $d->add('arg', undef, undef, "/Checkins");
            } $d->end(); #action
            
        } $d->end(); # 'r

        ##
        ## Lucene-User
        ##
        $d->start('r', undef, { 'name' => "Lucene-User", 'active' => "1" } ); 
        {
            $d->start('g', undef, { 'op' => "anyof" });
            {
                $d->add('c', undef, { 'name' => "header",
                                      'op' => ":contains",
                                      'k0' => "List-Id",
                                      'k1' => "java-user.lucene.apache.org" });
            } $d->end(); # 'g

            $d->start('action', undef, { 'name' => "fileinto"} ); {
                $d->add('arg', undef, undef, "/Lucene-User");
            } $d->end(); #action
            
        } $d->end(); # 'r 

        ##
        ## Lucene-Dev
        ##
        $d->start('r', undef, { 'name' => "Lucene-Dev", 'active' => "1" } ); 
        {
            $d->start('g', undef, { 'op' => "anyof" });
            {
                $d->add('c', undef, { 'name' => "header",
                                      'op' => ":contains",
                                      'k0' => "List-Id",
                                      'k1' => "java-dev.lucene.apache.org" });
            } $d->end(); # 'g

            $d->start('action', undef, { 'name' => "fileinto"} ); {
                $d->add('arg', undef, undef, "/Lucene-Dev");
            } $d->end(); #action
            
        } $d->end(); # 'r 

        ##
        ## P4 list
        ##
        $d->start('r', undef, { 'name' => "P4-User", 'active' => "1" } ); 
        {
            $d->start('g', undef, { 'op' => "anyof" });
            {
                $d->add('c', undef, { 'name' => "header",
                                      'op' => ":contains",
                                      'k0' => "List-Id",
                                      'k1' => "perforce-user.perforce.com" });
            } $d->end(); # 'g

            $d->start('action', undef, { 'name' => "fileinto"} ); {
                $d->add('arg', undef, undef, "/Perforce-User");
            } $d->end(); #action
            
        } $d->end(); # 'r 
        
        
    } $d->end(); # 'rules'
}
$d->end(); # 'SaveRulesRequest'

print "\nOUTGOING XML:\n-------------\n";
my $out =  $d->to_string("pretty")."\n";
$out =~ s/ns0\://g;
print $out."\n";

my $response = $SOAP->invoke($URL, $d->root(), $context);
print "\nRESPONSE:\n--------------\n";
$out =  $response->to_string("pretty")."\n";
$out =~ s/ns0\://g; #hack - remove the soap namespace, makes response more pleasant on my eye
print $out."\n";



