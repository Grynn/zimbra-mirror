#!/usr/bin/perl -w
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2006 Zimbra, Inc.
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

# specific to this app
my ($parentFolder, $name, $owner, $remoteId, $view);

#standard options
my ($user, $pw, $host, $help); #standard
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "p|parent=s" => \$parentFolder,
           "n|name=s" => \$name,
           "o|owner=s" => \$owner, 
           "i|id=s" => \$remoteId, 
           "v|view=s" => \$view,
          );

if (!defined($user) || defined($help) || !defined($parentFolder) ||
    !defined($name) || !defined($owner) || !defined($remoteId))
  {
    my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER -p PARENT_FOLDER_ID -n NAME_OF_LINK -o ZIMBRAID_OF_REMOTE_ACCT -i ID_IN_REMOTE_ACCOUNT [-v DEFAULT_VIEW]
   DEFAULT_VIEW can be one of conversation|message|contact|appointment|note
END_OF_USAGE
    die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doStdAuth();

my $d = new XmlDoc;
$d->start('CreateMountpointRequest', $Soap::ZIMBRA_MAIL_NS);
{
  $d->add('link', undef, { 'l' => $parentFolder,
                           'name' => $name,
                           'zid' => $owner,
                           'rid' => $remoteId,
                             defined($view) ? 'view' : 'bar'  => $view });
                             
}

$d->end(); # 'FolderActionRequest'

my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);
 
