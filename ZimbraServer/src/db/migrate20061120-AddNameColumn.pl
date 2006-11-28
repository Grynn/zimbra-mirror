#!/usr/bin/perl
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
# Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
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
SET name = subject, subject = null
WHERE id < 256 AND type IN (1, 3);
ADD_NAME_COLUMN_EOF
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  my @sql = ();
  foreach my $group (@groups) {
    my $sql = <<ADD_NAME_COLUMN_EOF;
UPDATE IGNORE $group.mail_item
SET name = subject, subject = null
WHERE type IN (1, 2, 3, 8, 13, 14) AND subject IS NOT NULL;
ADD_NAME_COLUMN_EOF
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  my @sql = ();
  foreach my $group (@groups) {
    my $sql = <<ADD_NAME_COLUMN_EOF;
UPDATE IGNORE $group.mail_item
SET name = CONCAT(SUBSTRING(subject, 1, 99), '{RENAMED-MIGRATE-$date}'), subject = null
WHERE type IN (1, 2, 3, 8, 13, 14) AND subject IS NOT NULL;
ADD_NAME_COLUMN_EOF
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  Migrate::updateSchemaVersion(31, 32);
}
