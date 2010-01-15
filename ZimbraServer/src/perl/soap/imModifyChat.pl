#!/usr/bin/perl -w
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.3 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
# 

use Date::Parse;
use Time::HiRes qw ( time );
use strict;

use lib '.';

use LWP::UserAgent;
use Getopt::Long;
use ZimbraSoapTest;
use XmlElement;
use XmlDoc;
use Soap;

#standard options
my ($user, $pw, $host, $help);  #standard
my ($thread, $op);

GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           "thread=s" => \$thread,
           "op=s" => \$op,
          );

if (!defined($user) || (!defined($thread)) || (!defined($op))) {
    print "USAGE: $0 -u USER -t thread -o op \n";
    exit 1;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doStdAuth();

my $d = new XmlDoc;
$d->start('IMModifyChatRequest', $Soap::ZIMBRA_IM_NS, { 'thread' => $thread, 'op'=>$op }); {
  if ($op eq "configure") {
    $d->add("var", undef, { 'name'=>"persistent" }, "false");
    $d->add("var", undef, { 'name'=>"publicroom" }, "1");
    $d->add("var", undef, { 'name'=>"moderated" }, "false");
    $d->add("var", undef, { 'name'=>"semianonymous" }, "true");
    $d->add("var", undef, { 'name'=>"noanonymous" }, "false");
    $d->add("var", undef, { 'name'=>"password" }, "test123");
    $d->add("var", undef, { 'name'=>"passwordprotect" }, "1");
  }
} $d->end();

my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

