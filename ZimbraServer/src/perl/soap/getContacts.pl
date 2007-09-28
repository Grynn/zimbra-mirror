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
my ($sortBy, $folder, $id, $attrs);
$sortBy = "dateDesc";

#standard options
my ($user, $pw, $host, $help); #standard
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "sort=s" => \$sortBy,
           "id=s" => \$id,
           "attrs=s" => \$attrs,
           "folder=s" => \$folder);



if (!defined($user) || defined($help)) {
    my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER [-s SORT] [-i id_list] [-f folder] [-a attrs]
    SORT = dateDesc|dateAsc|subjDesc|subjAsc|nameDesc|nameAsc|score
    TYPES = message|conversation|contact|appointment
END_OF_USAGE
    die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doStdAuth();

my $d = new XmlDoc;
my $searchName = "GetContactsRequest";

my %args =  ( 
             'sortBy' => $sortBy,
            );

if (defined($folder)) {
  $args{"l"} = $folder;
}
  

$d->start("GetContactsRequest", $Soap::ZIMBRA_MAIL_NS, \%args);
{
  if (defined $id) {
    $d->add('cn', undef, { "id" => $id });
  }
  
  if (defined($attrs)) {
    foreach (split(/,/, $attrs)) {
      $d->add('a', undef, { "n" => $_ } );
    }
  }
}
$d->end();                      # 'GetContactsRequest'

my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

