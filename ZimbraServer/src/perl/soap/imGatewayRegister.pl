#!/usr/bin/perl -w
# 
# ***** BEGIN LICENSE BLOCK *****
# Version: MPL 1.1
# 
# The contents of this file are subject to the Mozilla Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite Server.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2007 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
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

# specific to this app
my ($op, $gwName, $remName, $remPw);

#standard options
my ($user, $pw, $host, $help); #standard
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "op=s" => \$op,
           "gw=s" => \$gwName,
           "rn=s" => \$remName,
           "rp=s" => \$remPw,
          );

my $usage = <<END_OF_USAGE;
USAGE: $0 -u USER -op reg -gw GATEWAY_NAME -rn REMOTE_NAME -rp REMOTE_PASSWORD
USAGE: $0 -u USER -op unreg -gw GATEWAY_NAME
END_OF_USAGE

if (!defined($user) || !defined($gwName) || !defined($op) || defined($help)) {
  die $usage;
}

if ($op eq "reg" && (!defined($remName) || !defined($remPw))) {
  die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doStdAuth();

my $d = new XmlDoc;

my %args = (
            'service' => $gwName,
            'op' => $op,
           );

if ($op eq "reg") {
  $args{'name'} = $remName;
  $args{'password'} = $remPw;
}

$d->add("IMGatewayRegisterRequest", $Soap::ZIMBRA_IM_NS, \%args);
my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

