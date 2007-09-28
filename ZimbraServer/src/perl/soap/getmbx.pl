#!/usr/bin/perl -w
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2005, 2007 Zimbra, Inc.
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

# If you're using ActivePerl, you'll need to go and install the Crypt::SSLeay
# module for htps: to work...
#
#         ppm install http://theoryx5.uwinnipeg.ca/ppms/Crypt-SSLeay.ppd
#
my $url = "https://qa14.liquidsys.com:7071/service/admin/soap/";


use Time::HiRes qw ( time );
use strict;

use lib '.';

use LWP::UserAgent;
use Getopt::Long;
use XmlElement;
use XmlDoc;
use Soap;
use ZimbraSoapTest;

#specific options
my ($acct);

#standard options
my ($user, $pw, $host, $help);  #standard
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           "a=s" => \$acct);

if (!defined($user) || !defined($acct)) {
  my $usage = <<END_OF_USAGE;
USAGE: $0 -u ADMIN_USER -a ACCOUNT  [-h host] [-pw password]
END_OF_USAGE
  die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doAdminAuth();

my $d = new XmlDoc;

$d->start('GetAccountRequest', $Soap::ZIMBRA_ADMIN_NS); {
  $d->add('account', $Soap::ZIMBRA_ADMIN_NS, { "by" => "name" }, $acct);
} $d->end();

my $response = $z->invokeAdmin($d->root());
print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

my $acctInfo = $response->find_child('account');
if (!defined $acctInfo) {
  die "Couldn't find <account> entry in response";
}
my $acctId = $acctInfo->attr("id");


print "AccountID is $acctId\n";

$d = new XmlDoc;

$d->start('GetMailboxRequest', $Soap::ZIMBRA_ADMIN_NS); {
  $d->start('mbox', $Soap::ZIMBRA_MAIL_NS, { "id" => $acctId });
} $d->end();

$response = $z->invokeAdmin($d->root());
print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);
