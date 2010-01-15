#!/usr/bin/perl -w
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2008, 2009 Zimbra, Inc.
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.2 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
#

use strict;
use Getopt::Long;
use lib '.';
use LWP::UserAgent;
use Getopt::Long;
use ZimbraSoapTest;
use XmlElement;
use XmlDoc;
use Soap;

my ($thread, $addr, $ownermode);

my ($user, $pw, $host, $help);  #standard
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           "thread=s" => \$thread,
           "o|ownermode" => \$ownermode,
           "addr=s" => \$addr,
          );

if (!defined($user) ||
    (!defined($thread) && !defined($addr))) {
  print "USAGE: $0 -u USER (-t thread OR -a addr) [-o]\n";
  exit 1;
}

# if (!defined($pw) || ($pw eq "")) {
#   $pw = "test123";
# }

# if (!defined($host) || ($host eq "")) {
#   $host = "http://localhost:7070/service/soap";
# } else {
#   $host = $host . "/service/soap";
# }

#my $cmd;

# if (!defined($ownermode)) {
#   $cmd = "-m $user -p $pw -t im -u $host/service/soap -v IMGetChatConfigurationRequest/\@thread=\"$thread\"";
# } else {
#   $cmd = "-m $user -p $pw -t im -u $host/service/soap -v IMGetChatConfigurationRequest/\@thread=\"$thread\" IMGetChatConfigurationRequest/\@requestOwnerConfig=\"1\"";
# }

# print "Running  'zmsoap $cmd'\n";
# print `zmsoap -v $cmd`;

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doStdAuth();

my $d = new XmlDoc;
if (defined($thread)) {
  $d->start('IMGetChatConfigurationRequest', $Soap::ZIMBRA_IM_NS, { 'thread' => $thread,
                                                                    'requestOwnerConfig'=> defined($ownermode) ? "1" : "0",
                                                                  });
} else {
  $d->start('IMGetChatConfigurationRequest', $Soap::ZIMBRA_IM_NS, { 'addr' => $addr,
                                                                    'requestOwnerConfig'=> defined($ownermode) ? "1" : "0",
                                                                  });
}
$d->end();  

my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

