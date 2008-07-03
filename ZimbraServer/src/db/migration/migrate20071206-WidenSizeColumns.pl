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
my $sqlGroupsWithSmallMailitem = <<_SQL_;
SELECT table_schema FROM information_schema.columns
WHERE table_name = 'mail_item' AND column_name = 'size' AND data_type = 'int'
ORDER BY table_schema;
_SQL_
my %mailItemGroups  = map { $_ => 1 } Migrate::runSql($sqlGroupsWithSmallMailitem);

my $sqlGroupsWithSmallRevisions = <<_SQL_;
SELECT table_schema FROM information_schema.columns
WHERE table_name = 'revision' AND column_name = 'size' AND data_type = 'int'
ORDER BY table_schema;
_SQL_
my %revisionGroups = map { $_ => 1 } Migrate::runSql($sqlGroupsWithSmallRevisions);

Migrate::verifySchemaVersion(49);

my @sql = ();
foreach my $group (@groups) {
  if (exists $mailItemGroups{$group}) {
    my $sql = <<_MAILITEM_SQL_;
ALTER TABLE $group.mail_item MODIFY COLUMN size BIGINT UNSIGNED NOT NULL;
_MAILITEM_SQL_
    push(@sql, $sql);
  } 
  if (exists $revisionGroups{$group}) {
    my $sql = <<_REVISION_SQL_;
ALTER TABLE $group.revision MODIFY COLUMN size BIGINT UNSIGNED NOT NULL;
_REVISION_SQL_
    push(@sql, $sql);
  } 
}
Migrate::runSqlParallel($concurrent, @sql);

Migrate::updateSchemaVersion(49, 50);

exit(0);

#####################
