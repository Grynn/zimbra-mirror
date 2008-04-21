#!/usr/bin/perl -w
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2006, 2007 Zimbra, Inc.
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
use lib '.';

use LWP::UserAgent;
use Getopt::Long;
use XmlDoc;
use Soap;
use ZimbraSoapTest;

#standard options
my ($sessionId, $authToken, $user, $pw, $host, $help, $verbose); #standard
my ($idle);

GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           "v" => \$verbose,
           "sessionId=s" => \$sessionId,
           "at=s" => \$authToken,
           "idle" => \$idle,
          );

my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER [-v] [-at authToken] [-s sessionId] [-idle]
END_OF_USAGE

if (!defined($user)) {
  die $usage;
}

my %soapargs;
$soapargs{ 'NOTIFY'} = 1;

if (defined($sessionId)) {
  $soapargs{'SESSIONID'} = $sessionId;
} else {
  die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw, \%soapargs);
$z->verbose(3);

if (defined($sessionId) && defined($authToken)) {
  $z->setAuthContext($authToken, $sessionId, \%soapargs);
} else {
  print "AUTH REQUEST:\n--------------------";
  $z->doStdAuth();
}

my $d = new XmlDoc;

my %args = ( );

if (defined($idle)) {
  $args{'isIdle'} = 1;
} else {
  $args{'isIdle'} = 0;
}

$args{'idleTime'} = 300;

$d->add('IMSetIdleRequest', $Soap::ZIMBRA_IM_NS, \%args);

print "\n\nEND_SESSION:\n--------------------";
my $response = $z->invokeMail($d->root());

#print "REQUEST:\n-------------\n".$z->to_string_simple($d);
#print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

