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

use Getopt::Long;

my ($thread);

my ($user, $pw, $host, $help);  #standard
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           "thread=s" => \$thread,
          );

if (!defined($user) || (!defined($thread))) {
  print "USAGE: $0 -u USER -t thread\n";
  exit 1;
}

if (!defined($pw) || ($pw eq "")) {
  $pw = "test123";
}

if (!defined($host) || ($host eq "")) {
  $host = "http://localhost:7070/service/soap";
} else {
  $host = $host . "/service/soap";
}

my $cmd = "zmsoap -v -m $user -p $pw -t im -u $host/service/soap -v IMGetChatConfigurationRequest/\@thread=\"$thread\"";

print "Running  '$cmd'\n";
print `$cmd`;
