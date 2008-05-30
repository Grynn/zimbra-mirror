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
my ($defTypes, $accountsRem, $accountsUp, $accountsAdd, $waitSet, $seq, $block, $admin);

#standard options
my ($user, $pw, $host, $help); #standard
my ($name, $value);
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "admin" => \$admin,
           "w=s" => \$waitSet,
           "s=s" => \$seq,
           "block" => \$block,
           "a=s@" => \$accountsAdd,
           "m=s@" => \$accountsUp,
           "r=s@" => \$accountsRem,
           "d=s"  => \$defTypes,
          );

if (!defined($user) || defined($help) || !defined($waitSet) || !defined($seq)) {
  my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER -w waitSetId [-d defTypes] [-admin] -s lastKnownSeqNo [-block] [-a accountAdd -a...] [-m accountModify -m...] [-r accountRemove -r...]
END_OF_USAGE
    die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);

my $urn;
my $requestName;

if (defined($admin)) {
  $z->doAdminAuth();
  $urn = $Soap::ZIMBRA_ADMIN_NS;
  $requestName = "AdminWaitSetRequest";
} else {
  $z->doStdAuth();
  $urn = $Soap::ZIMBRA_MAIL_NS;
  $requestName = "WaitSetRequest";
}

my $d = new XmlDoc;

my %args = ( 'waitSet' => $waitSet,
             'seq' => $seq,
             );

if (defined $defTypes) {
  $args{'defTypes'} = $defTypes;
}

if (defined $block && $block ne "0") {
  $args{'block'} = "1";
}
  
$d->start($requestName, $urn, \%args);
if (defined $accountsAdd) {
  $d->start("add");
  {
    foreach my $a (@$accountsAdd) {
      (my $aid, my $tok) = split /,/,$a;
      if (!defined $tok) {
        $d->add("a", undef, { 'id' => $a, }); #'token'=>"608"
      } else {
        $d->add("a", undef, { 'id' => $aid, 'token'=>$tok}); 
      }
    }
    
  } $d->end(); # add
}

if (defined $accountsUp) {
  $d->start("update");
  {
    foreach my $a (@$accountsUp) {
      (my $aid, my $tok) = split /,/,$a;
      if (!defined $tok) {
        $d->add("a", undef, { 'id' => $a, }); #'token'=>"608"
      } else {
        $d->add("a", undef, { 'id' => $aid, 'token'=>$tok}); 
      }
    }
  } $d->end(); #update
}
    
if (defined $accountsRem) {
  $d->start("remove");
  {
    foreach my $a (@$accountsRem) {
      $d->add("a", undef, { 'id' => $a });
    }
  } $d->end(); #remove
  
}
$d->end(); # 'WaitMultipleAccountsRequest'

my $response;
if (defined($admin)) {
  $response = $z->invokeAdmin($d->root());
} else {
  $response = $z->invokeMail($d->root());
}

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

          
