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
# Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
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
my ($searchString, $offset, $prevId, $prevSortVal, $limit, $fetch, $sortBy, $types, $convId);
$offset = 0;
$limit = 5;
$fetch = 0;
$sortBy = "dateDesc";
$types = "message";

#standard options
my ($user, $pw, $host, $help); #standard
GetOptions("u|user=s" => \$user,
           "t|types=s" => \$types,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "conv=i" => \$convId,
           "query=s" => \$searchString,
           "sort=s" => \$sortBy,
           "offset=i" => \$offset,
           "limit=i" => \$limit,
           "fetch" => \$fetch,
           "pi=s" => \$prevId,
           "ps=s" => \$prevSortVal);



if (!defined($user) || !defined($searchString) || defined($help)) {
    my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER -q QUERYSTR [-s SORT] [-t TYPES] [-o OFFSET] [-l LIMIT] [-f FETCH] [-pi PREV-ITEM-ID -ps PREV-SORT-VALUE] [-c CONVID]
    SORT = dateDesc|dateAsc|subjDesc|subjAsc|nameDesc|nameAsc|score
    TYPES = message|conversation|contact|appointment
END_OF_USAGE
    die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doStdAuth();

my $d = new XmlDoc;
my $searchName = "SearchRequest";

my %args =  ( 'types' => $types,
              'sortBy' => $sortBy,
              'offset' => $offset,
              'limit' => $limit,
              'fetch' => $fetch
            );

if (defined($convId)) {
  $searchName = "SearchConvRequest";
  $args{'cid'} = $convId;
}

 
$d->start($searchName, $Soap::ZIMBRA_MAIL_NS, \%args);
{
    if (defined $prevId) {
        $d->add("cursor", undef, { "id" => $prevId, "sortVal" => $prevSortVal });
    }
    
    $d->add('query', undef, undef, $searchString);
    
} $d->end(); # 'SearchRequest'

my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

