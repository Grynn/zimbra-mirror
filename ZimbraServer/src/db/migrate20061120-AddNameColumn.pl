#!/usr/bin/perl
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
use Migrate;
my $concurrent = 10;

addNameColumn();

exit(0);

#####################

sub addNameColumn($) {
  my ($group) = @_;
  Migrate::verifySchemaVersion(31);
  my $date = time();
  my @groups = Migrate::getMailboxGroups();
  my @sql = ();
  foreach my $group (@groups) {
    my $sql = <<ADD_NAME_COLUMN_EOF;
ALTER TABLE $group.mail_item
ADD COLUMN name VARCHAR(128) AFTER subject;
ADD_NAME_COLUMN_EOF
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  my @sql = ();
  foreach my $group (@groups) {
    my $sql = <<ADD_NAME_COLUMN_EOF;
CREATE UNIQUE INDEX i_name_folder_id ON $group.mail_item(mailbox_id, folder_id, name);
ADD_NAME_COLUMN_EOF
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  my @sql = ();
  foreach my $group (@groups) {
    my $sql = <<ADD_NAME_COLUMN_EOF;
UPDATE IGNORE $group.mail_item
SET name = subject
WHERE id < 256 AND type IN (1, 3);
ADD_NAME_COLUMN_EOF
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  my @sql = ();
  foreach my $group (@groups) {
    my $sql = <<ADD_NAME_COLUMN_EOF;
UPDATE IGNORE $group.mail_item
SET name = subject
WHERE type IN (1, 2, 3, 8, 13, 14) AND subject IS NOT NULL AND name IS NULL;
ADD_NAME_COLUMN_EOF
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  my @sql = ();
  foreach my $group (@groups) {
    my $sql = <<ADD_NAME_COLUMN_EOF;
UPDATE IGNORE $group.mail_item
SET name = CONCAT(SUBSTRING(subject, 1, 99), '{RENAMED-MIGRATE-$date}'), subject = name
WHERE type IN (1, 2, 3, 8, 13, 14) AND subject IS NOT NULL AND name IS NULL;
ADD_NAME_COLUMN_EOF
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  Migrate::updateSchemaVersion(31, 32);
}
