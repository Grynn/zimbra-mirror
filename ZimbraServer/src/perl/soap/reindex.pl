#!/usr/bin/perl -w
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2005, 2006 Zimbra, Inc.
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

use Time::HiRes qw ( time );
use strict;

use lib '.';

use LWP::UserAgent;
use Getopt::Long;
use XmlElement;
use XmlDoc;
use Soap;
use ZimbraSoapTest;

my $ACCTNS = "urn:zimbraAdmin";
my $MAILNS = "urn:zimbraAdmin";

# If you're using ActivePerl, you'll need to go and install the Crypt::SSLeay
# module for htps: to work...
#
#         ppm install http://theoryx5.uwinnipeg.ca/ppms/Crypt-SSLeay.ppd
#

# app-specific options
my ($mbox, $action, $types, $ids);

#standard options
my ($user, $pw, $host, $help);  #standard
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "m|mbox=s" => \$mbox,
           "a|action=s" => \$action,
           "t|types=s" => \$types,
           "ids=s" => \$ids,
           "help|?" => \$help);

if (!defined($user)) {
  die "USAGE: $0 -u USER -m MAILBOXID -a ACTION [-pw PASSWD] [-h HOST] [-t TYPES] [-ids IDS]";
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doAdminAuth();

my %args = ( 'action' => $action );


my $d = new XmlDoc;
$d = new XmlDoc;
$d->start('ReIndexRequest', $MAILNS, \%args); {
  my %mbxArgs = ( 'id' => $mbox );
  if (defined $ids) {
    $mbxArgs{'ids'} = $ids;
  }
  if (defined $types) {
    $mbxArgs{'types'} = $types;
  }
  $d->add('mbox', $MAILNS, \%mbxArgs);
} $d->end();

print "\nOUTGOING XML:\n-------------\n";
my $out =  $d->to_string("pretty");
$out =~ s/ns0\://g;
print $out."\n";

my $start = time;
my $firstStart = time;

my $response = $z->invokeAdmin($d->root());

print "\nRESPONSE:\n--------------\n";
$out =  $response->to_string("pretty");
$out =~ s/ns0\://g;
print $out."\n";


