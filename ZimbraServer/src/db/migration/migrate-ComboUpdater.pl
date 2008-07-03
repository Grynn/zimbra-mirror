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
no strict "refs";
use Migrate;

my @mailboxIds = Migrate::getMailboxIds();
my $timestamp = time();
my $concurrent = 10;
my $sql;

my $curSchemaVersion = Migrate::getSchemaVersion();
my $beginSchemaVersion = $curSchemaVersion;

while ($curSchemaVersion >= 20 && $curSchemaVersion < 27) {
  &{"schema${curSchemaVersion}"};
  $curSchemaVersion = Migrate::getSchemaVersion();
}
exit(0);

sub schema20 {
  Migrate::verifySchemaVersion(20);
  my $sql = "drop table if exists redolog_sequence;\n";
  Migrate::runSql($sql);
  Migrate::updateSchemaVersion(20,21);
  return;
}

sub schema21 {
  Migrate::verifySchemaVersion(21);
  my @sql = ();
  foreach my $id (@mailboxIds) {
    my $sql = <<EOF_SQL;
UPDATE mailbox$id.mail_item SET subject = "Notebook1" WHERE subject = "Notebook" AND folder_id = 1 AND id != 12;
EOF_SQL
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  @sql = ();
  foreach my $id (@mailboxIds) {
    my $sql = <<EOF_SQL;
INSERT INTO mailbox$id.mail_item (subject, id, type, parent_id, folder_id, mod_metadata, mod_content, metadata, date, change_date) VALUES ("Notebook", 12, 1, 1, 1, 1, 1, "d1:ai1e1:vi9e2:vti14ee", $timestamp, $timestamp) ON DUPLICATE KEY UPDATE id = 12;
EOF_SQL
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  @sql = ();
  foreach my $id (@mailboxIds) {
    my $sql = <<EOF_SQL;
UPDATE mailbox$id.mail_item mi, zimbra.mailbox mbx SET mod_metadata = change_checkpoint + 100, mod_content = change_checkpoint + 100, change_checkpoint = change_checkpoint + 200 WHERE mi.id = 12 AND mbx.id = $id;
EOF_SQL
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);
  Migrate::updateSchemaVersion(21,22);
  return;
}

sub schema22 {
    Migrate::verifySchemaVersion(22);
    my $sql = <<ADD_TRACKING_IMAP_COLUMN_EOF;
ALTER TABLE zimbra.mailbox MODIFY tracking_sync INTEGER UNSIGNED NOT NULL DEFAULT 0;
ALTER TABLE zimbra.mailbox ADD COLUMN tracking_imap BOOLEAN NOT NULL DEFAULT 0 AFTER tracking_sync;
ADD_TRACKING_IMAP_COLUMN_EOF
    Migrate::runSql($sql);

   my @sql = ();
  foreach my $id (@mailboxIds) {
    my $dbName = "mailbox" . $id;
    my $sql = <<ADD_IMAP_ID_COLUMN_EOF;
ALTER TABLE $dbName.mail_item ADD COLUMN imap_id INTEGER UNSIGNED AFTER folder_id;
ADD_IMAP_ID_COLUMN_EOF
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  @sql = ();
  foreach my $id (@mailboxIds) {
    my $dbName = "mailbox" . $id;
    my $sql = <<ADD_IMAP_ID_COLUMN_EOF;
UPDATE $dbName.mail_item SET imap_id = id WHERE type IN (5, 6, 8, 11, 14);
ADD_IMAP_ID_COLUMN_EOF
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  Migrate::updateSchemaVersion(22,23);
  return;
}

sub schema23 {
  Migrate::verifySchemaVersion(23);

  my @sql = ();
  foreach my $id (@mailboxIds) {
    my $sql = <<EOF_SQL;
UPDATE mailbox$id.mail_item mi, zimbra.mailbox mbx SET subject = "Emailed Contacts_1", mod_metadata = change_checkpoint + 100, mod_content = change_checkpoint + 100, change_checkpoint = change_checkpoint + 200 WHERE subject = "Emailed Contacts" AND folder_id = 1 AND mi.id != 13 AND mbx.id = $id;
EOF_SQL
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  @sql = ();
  foreach my $id (@mailboxIds) {
    my $sql = <<EOF_CREATE_EMAILED_CONTACT_FOLDER;
INSERT INTO mailbox$id.mail_item (subject, id, type, parent_id, folder_id, mod_metadata, mod_content, metadata, date, change_date) VALUES ("Emailed Contacts", 13, 1, 1, 1, 1, 1, "d1:ai1e1:vi9e2:vti6ee", $timestamp, $timestamp) ON DUPLICATE KEY UPDATE id = 13;
EOF_CREATE_EMAILED_CONTACT_FOLDER
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  @sql = ();
  foreach my $id (@mailboxIds) {
    my $sql = <<EOF_CREATE_EMAILED_CONTACT_FOLDER;
UPDATE mailbox$id.mail_item mi, zimbra.mailbox mbx SET mod_metadata = change_checkpoint + 100, mod_content = change_checkpoint + 100, change_checkpoint = change_checkpoint + 200 WHERE mi.id = 13 AND mbx.id = $id;
EOF_CREATE_EMAILED_CONTACT_FOLDER
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  Migrate::updateSchemaVersion(23,24);
  return;
}

sub schema24 {
  Migrate::verifySchemaVersion(24);
  my @sql = ();
  foreach my $id (@mailboxIds) {
    my $sql = <<EOF_SET_CHECKED_CALENDAR_FLAG;
UPDATE mailbox$id.mail_item mi, zimbra.mailbox mbx SET flags = flags | 2097152, mod_metadata = change_checkpoint + 100, change_checkpoint = change_checkpoint + 200 WHERE mi.id = 10 AND mbx.id = $id;
EOF_SET_CHECKED_CALENDAR_FLAG
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  Migrate::updateSchemaVersion(24,25);
  return;
}

sub schema25 {
  Migrate::verifySchemaVersion(25);
  my $sql;
  $sql .= <<CREATE_MAILBOX_METADATA_EOF;
CREATE TABLE zimbra.mailbox_metadata ( mailbox_id  INTEGER UNSIGNED NOT NULL, section     VARCHAR(64) NOT NULL, metadata    MEDIUMTEXT, PRIMARY KEY (mailbox_id, section), CONSTRAINT fk_metadata_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE) ENGINE = InnoDB;
CREATE_MAILBOX_METADATA_EOF

  $sql .= <<REMOVE_CONFIG_EOF;
ALTER TABLE zimbra.mailbox DROP COLUMN config;
REMOVE_CONFIG_EOF
  Migrate::runSql($sql);

  Migrate::updateSchemaVersion(25,26);
  return;
}

sub schema26 {
  Migrate::verifySchemaVersion(26);
  my $sql = <<ADD_CONTACT_COUNT_COLUMN_EOF;
ALTER TABLE zimbra.mailbox ADD COLUMN contact_count INTEGER UNSIGNED DEFAULT 0 AFTER item_id_checkpoint;
UPDATE zimbra.mailbox SET contact_count = NULL;
ADD_CONTACT_COUNT_COLUMN_EOF
  Migrate::runSql($sql);

  my @sql = ();
  foreach my $id (@mailboxIds) {
    my $sql = <<RESIZE_UNREAD_COLUMN_EOF;
ALTER TABLE mailbox$id.mail_item MODIFY COLUMN unread INTEGER UNSIGNED;
RESIZE_UNREAD_COLUMN_EOF
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  Migrate::updateSchemaVersion(26,27);
  return;
}
