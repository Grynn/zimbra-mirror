#!/usr/bin/perl -w
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

#my $url = "http://localhost:7070/service/soap/";
my $url = "http://token:7070/service/soap/";


my $SOAP = $Soap::Soap12;

sub authenticate
{
    my $username = shift();
    
    my $d = new XmlDoc;
    $d->start('AuthRequest', $ACCTNS);
    $d->add('account', undef, { by => "name"}, $username);
    $d->add('password', undef, undef, "mypassWord");
    $d->end();
    
    my $authResponse = $SOAP->invoke($url, $d->root());
#    print "AuthResponse = ".$authResponse->to_string("pretty")."\n";
    my $authToken = $authResponse->find_child('authToken')->content;
#    print "authToken($authToken)\n";
    my $context = $SOAP->zimbraContext($authToken);
    return $context;
}

#
# <AddMsgRequest>
#    <m t="{tags}" l="{folder}" >
#    ...
#    </m>
# </AddMsgRequest>
#     
# <AddMsgResponse>
#    <m id="..." />
# </AddMsgResponse>
#

my %msgAttrs;
$msgAttrs{'l'} = "/INBOX";
$msgAttrs{'t'} = "\\unseen";

my $g_msg;

#my $dirname = "c:\\archive_mail\\out";
#opendir (DIR, $dirname) or die "couldn't open $dirname";
#open(IN, "c:\\archive_mail\\archive_32303");
open(IN, "$ARGV[0]");
$g_msg = next_file("user1\@example.zimbra.com");
my $context = authenticate("user1\@example.zimbra.com");

my $num = 2;

do {

my $d = new XmlDoc;
$d->start('AddMsgRequest', $MAILNS);
$d->start('m', undef, \%msgAttrs, undef);


my $usernum  = $num%100;
my $username = "user$num\@example.zimbra.com";
print("Adding mail for user: $username\n");
$g_msg = next_file($username);
$context = authenticate($username);
#print("Message is: $g_msg\n");

#setup_msg();

$d->start('content', undef, undef, $g_msg);

$d->end(); # 'content'
$d->end(); # 'm'
$d->end(); # 'AddMsgRequest'

#print "\nOUTGOING XML:\n-------------\n";
#print $d->to_string("pretty"),"\n";

$num++;
my $response = $SOAP->invoke($url, $d->root(), $context);

#print "\nRESPONSE:\n--------------\n";
#print $response->to_string("pretty");
#if ($num % 20 == 0)  {
    print $response->to_string()."\n";
#}
#$g_msg = next_file();



} while(defined($g_msg));


sub next_file
{
    my $username = shift();
    my $ret = "";
    my $found_to = 0;
    
    while (<IN>) {
        if (/^From \?\?\?\@\?\?\?.*/) {
            return $ret;
        }
        s/tim\@gurge.com/$username/ig;
        s/tim\@symphonatic.com/$username/ig;
        s/tim\@curple.com/$username/ig;
        if (($found_to == 0) && (~/^To:/)) {
            $found_to = 1;
            if (!($_ =~ /$username/)) {
                $_ = "To: user1\@example.zimbra.com\n";
            }
        }
        s/\000//g;
        s/\0x1b//g;

        $ret .= $_;
    }
    exit(1);
        
#     my $filename;
#     do {
#         $filename = readdir(DIR);
#     } while(defined($filename) && -d $filename);
# #    print("Opening file $filename\n");
#     if (!defined($filename)) {
#         return;
#     }

#     open(IN, "<c:\\archive_mail\\out\\$filename");
#     my $ret;
#     sysread(IN, $ret, 99999999);
#     close(IN);
#     $ret =~ s/^From \?\?\?\@\?\?\?.*\n//;
#     $ret =~ s/\r\n/\n/g;
#     return $ret;
}

