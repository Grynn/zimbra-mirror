#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2007 Zimbra, Inc.
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
use strict;
use warnings;
use lib '.';

use LWP::UserAgent;
use Getopt::Long;
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
# specific to this app
my ($add, $remove, $clear, $list);

#standard options
my ($user, $pw, $host, $help); #standard
my ($name, $value);
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "a=s" => \$add,
           "r=s" => \$remove,
           "c", => \$clear,
           "l", => \$list
          );

if (!defined($add) && !defined($remove) && !defined($clear) && !defined($list)) {
  my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER (-a account | -r account | -c | -l)
END_OF_USAGE
    die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doAdminAuth();

my $d = new XmlDoc;

my %args;

if (defined $add) {
  $args{'op'} = "ADD";
  $args{'id'} = $add;
} elsif (defined $remove) {
  $args{'op'} = "REMOVE";
  $args{'id'} = $remove;
} elsif (defined $clear) {
  $args{'op'} = "CLEAR";
} else {
  $args{'op'} = "LIST";
}

$d->start("SoapLoggerRequest", $MAILNS, \%args);
$d->end(); # 'WaitMultipleAccountsRequest'
  
my $response = $z->invokeAdmin($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

          
