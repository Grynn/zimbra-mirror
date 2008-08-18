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

#
# Simple SOAP test-harness for the AddMsg API
#

use strict;

use lib '.';

use LWP::UserAgent;
use Getopt::Long;
use XmlElement;
use XmlDoc;
use Soap;
use ZimbraSoapTest;

#standard options
my ($admin, $user, $pw, $host, $help, $adminHost); #standard
my ($token);
GetOptions("u|user=s" => \$user,
           "admin" => \$admin,
           "ah|adminHost=s" => \$adminHost,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           "t=s" => \$token,
          );

if (!defined($user) || defined($help)) {
  my $usage = <<END_OF_USAGE;

USAGE: $0 -u USER -t TOKEN
END_OF_USAGE
  die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw, undef, $adminHost);
$z->doStdAuth();

my $SOAP = $Soap::Soap12;

my $d = new XmlDoc;
if (defined($token) && ($token ne "")) {
    $d->start('SyncRequest', $Soap::ZIMBRA_MAIL_NS, { "token" => $token});
} else {
    $d->start('SyncRequest', $Soap::ZIMBRA_MAIL_NS);
}
$d->end(); # 'SyncRequest';'

my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

