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
use Migrate;
my $concurrent = 10;

my @groups = Migrate::getMailboxGroups();

my @sql = ();
foreach my $group (@groups) {
    my $sql = <<_SQL_;
UPDATE $group.mail_item
SET blob_digest = NULL
WHERE type = 6
AND blob_digest = '';
_SQL_
    push(@sql, $sql);
}

Migrate::runSqlParallel($concurrent, @sql);

exit(0);
