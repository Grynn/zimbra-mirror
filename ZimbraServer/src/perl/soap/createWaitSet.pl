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

# If you're using ActivePerl, you'll need to go and install the Crypt::SSLeay
# module for htps: to work...
#
#         ppm install http://theoryx5.uwinnipeg.ca/ppms/Crypt-SSLeay.ppd
#
# specific to this app
my ($defTypes, $accounts, $admin, $allAccounts);

#standard options
my ($user, $pw, $host, $help); #standard
my ($name, $value);
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "d=s"  => \$defTypes,
           "a=s@" => \$accounts,
           "admin" => \$admin,
           "allAccounts" => \$allAccounts,
          );

if (!defined($user) || defined($help) || !defined($defTypes)) {
  my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER -d defTypes [-admin [-allAccounts]] [-a account -a account...]
END_OF_USAGE
    die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);

my $urn;
my $requestName;

if (defined($admin)) {
  $z->doAdminAuth();
  $urn = $Soap::ZIMBRA_ADMIN_NS;
  $requestName = "AdminCreateWaitSetRequest";
} else {
  $z->doStdAuth();
  $urn = $Soap::ZIMBRA_MAIL_NS;
  $requestName = "CreateWaitSetRequest";
}

my %args =  (  'defTypes' => "$defTypes" );

if (defined $allAccounts) {
  $args{'allAccounts'} = "1";
}
              

my $d = new XmlDoc;
  
$d->start($requestName, $urn, \%args);

if (defined $accounts) {
  $d->start("add");
  {
    foreach my $a (@$accounts) {
      (my $aid, my $tok) = split /,/,$a;
      if (!defined $tok) {
        $d->add("a", undef, { 'id' => $a, }); #'token'=>"608"
      } else {
        $d->add("a", undef, { 'id' => $aid, 'token'=>$tok}); 
      }
    }
  } $d->end(); # add
}
$d->end(); # 'CreateWaitSetRequest'

my $response;

if (defined($admin)) {
  $response = $z->invokeAdmin($d->root());
} else {
  $response = $z->invokeMail($d->root());
}

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

          
