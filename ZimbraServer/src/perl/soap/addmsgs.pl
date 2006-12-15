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
# Portions created by Zimbra are Copyright (C) 2004, 2005 Zimbra, Inc.
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
my $d = new XmlDoc;
$d->start('AuthRequest', $ACCTNS);
$d->add('account', undef, { by => "name"}, 'user1@example.zimbra.com');
$d->add('password', undef, undef, "mypassWord");
$d->end();



my $authResponse = $SOAP->invoke($url, $d->root());

print "AuthResponse = ".$authResponse->to_string("pretty")."\n";

my $authToken = $authResponse->find_child('authToken')->content;

print "authToken($authToken)\n";

my $context = $SOAP->zimbraContext($authToken);

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

my $dirname = "c:\\archive_mail\\out";
opendir (DIR, $dirname) or die "couldn't open $dirname";


do {

$d = new XmlDoc;
$d->start('AddMsgRequest', $MAILNS);
$d->start('m', undef, \%msgAttrs, undef);


$g_msg = next_file();

#setup_msg();

$d->start('content', undef, undef, $g_msg);

$d->end(); # 'content'
$d->end(); # 'm'
$d->end(); # 'AddMsgRequest'

#print "\nOUTGOING XML:\n-------------\n";
#print $d->to_string("pretty"),"\n";

my $response = $SOAP->invoke($url, $d->root(), $context);

#print "\nRESPONSE:\n--------------\n";
#print $response->to_string("pretty");
print $response->to_string()."\n";
$g_msg = next_file();

} while(defined($g_msg));


sub next_file
{
    my $filename;
    do {
        $filename = readdir(DIR);
    } while(defined($filename) && -d $filename);
#    print("Opening file $filename\n");
    if (!defined($filename)) {
        return;
    }

    open(IN, "<c:\\archive_mail\\out\\$filename");
    my $ret;
    sysread(IN, $ret, 99999999);
    close(IN);
    $ret =~ s/^From \?\?\?\@\?\?\?.*\n//;
    $ret =~ s/\r\n/\n/g;
    return $ret;
}


sub setup_msg
{
   
    $g_msg = <<END_OF_MSG;
    
Return-Path: <testest\@curple.com>
Received: from joplin.siteprotect.com (joplin.siteprotect.com [64.26.0.58])
	by lsh140.siteprotect.com (8.11.6/8.11.6) with ESMTP id i8TIi8N00839
	for <tim\@symphonatic.com>; Wed, 29 Sep 2004 13:44:08 -0500
Received: from c-24-13-52-25.client.comcast.net (c-24-13-52-25.client.comcast.net [24.13.52.25])
	by joplin.siteprotect.com (8.11.6/8.11.6) with SMTP id i8TIi4T09352;
Wed, 29 Sep 2004 13:44:04 -0500
X-Message-Info: VwyyDO050xxvROjpwoHCBRNMxgYUbehSkg471
Received: from shade-dns.gte.net (95.224.224.151) by dgk9-xfl0.gte.net with Microsoft SMTPSVC(5.0.2195.6824);
Wed, 29 Sep 2004 15:36:02 -0400
Date: Wed, 29 Sep 2004 14:39:02 -0600 (CST)
Message-Id: <77536181.ol184IIydJ898\@arsenal3.raymond05gte.net>
To: ttestest\@curple.com
CC: foo\@curple.com
Subject: Re: Foo A Diamond in The Rough Equity Report
From: foo\@gub.com
MIME-Version: 1.0
Status:

4
3
And also your mom
And your mom
Your mom 
END_OF_MSG
}
