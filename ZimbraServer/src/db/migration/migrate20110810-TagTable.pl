#!/usr/bin/perl
#
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2011 Zimbra, Inc.
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

use strict;
use Migrate;

########################################################################################################################

my $concurrent = 10;

Migrate::verifySchemaVersion(83);

my @groups = Migrate::getMailboxGroups();

addTagTable();
addTaggedItemTable();
addTagNamesColumn();
#can't drop indexes until *after* migration is complete
#dropTagIndexes();

Migrate::updateSchemaVersion(83, 84);

exit(0);

########################################################################################################################

sub addTagTable() {
  my @sql = ();
  foreach my $group (@groups) {
    Migrate::logSql("Adding $group.TAG table...");
    my $sql = <<_EOF_;
CREATE TABLE IF NOT EXISTS $group.tag (
   mailbox_id    INTEGER UNSIGNED NOT NULL,
   id            INTEGER NOT NULL,
   name          VARCHAR(128) NOT NULL,
   color         BIGINT,
   item_count    INTEGER NOT NULL DEFAULT 0,
   unread        INTEGER NOT NULL DEFAULT 0,
   listed        BOOLEAN NOT NULL DEFAULT FALSE,
   sequence      INTEGER UNSIGNED NOT NULL,  -- change number for rename/recolor/etc.
   policy        VARCHAR(1024),

   PRIMARY KEY (mailbox_id, id),
   UNIQUE INDEX i_tag_name (mailbox_id, name),
   CONSTRAINT fk_tag_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id)
) ENGINE = InnoDB;
_EOF_
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);
}

sub addTaggedItemTable() {
  my @sql = ();
  foreach my $group (@groups) {
    Migrate::logSql("Adding $group.TAGGED_ITEM table...");
    my $sql = <<_EOF_;
CREATE TABLE IF NOT EXISTS $group.tagged_item (
   mailbox_id    INTEGER UNSIGNED NOT NULL,
   tag_id        INTEGER NOT NULL,
   item_id       INTEGER UNSIGNED NOT NULL,

   UNIQUE INDEX i_tagged_item_unique (mailbox_id, tag_id, item_id),
   CONSTRAINT fk_tagged_item_tag FOREIGN KEY (mailbox_id, tag_id) REFERENCES $group.tag(mailbox_id, id) ON DELETE CASCADE,
   CONSTRAINT fk_tagged_item_item FOREIGN KEY (mailbox_id, item_id) REFERENCES $group.mail_item(mailbox_id, id) ON DELETE CASCADE
) ENGINE = InnoDB;
_EOF_
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);
}

sub addTagNamesColumn() {
  my @sql = ();
  foreach my $group (@groups) {
    Migrate::logSql("Adding TAG_NAMES column to $group.MAIL_ITEM and $group.MAIL_ITEM_DUMPSTER...");
    my $sql = <<_EOF_;
ALTER TABLE $group.mail_item ADD COLUMN tag_names TEXT AFTER tags;
_EOF_
    push(@sql,$sql);
  }
  foreach my $group (@groups) {
    Migrate::logSql("Adding TAG_NAMES column to $group.MAIL_ITEM and $group.MAIL_ITEM_DUMPSTER...");
    my $sql = <<_EOF_;
ALTER TABLE $group.mail_item_dumpster ADD COLUMN tag_names TEXT AFTER tags;
_EOF_
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);
}

sub dropTagIndexes() {
  my @sql = ();
  foreach my $group (@groups) {
    Migrate::logSql("Dropping i_unread indexes from $group.MAIL_ITEM...");
    my $sql = <<_EOF_;
ALTER TABLE $group.mail_item DROP INDEX i_unread;
_EOF_
    push(@sql,$sql);
  }
  foreach my $group (@groups) {
    Migrate::logSql("Dropping i_tags indexes from $group.MAIL_ITEM...");
    my $sql = <<_EOF_;
ALTER TABLE $group.mail_item DROP INDEX i_tags_date;
_EOF_
    push(@sql,$sql);
  }
  foreach my $group (@groups) {
    Migrate::logSql("Dropping i_flags indexes from $group.MAIL_ITEM...");
    my $sql = <<_EOF_;
ALTER TABLE $group.mail_item DROP INDEX i_flags_date;
_EOF_
    push(@sql,$sql);
  }
  foreach my $group (@groups) {
    Migrate::logSql("Dropping i_unread indexes from $group.MAIL_ITEM_DUMPSTER...");
    my $sql = <<_EOF_;
ALTER TABLE $group.mail_item_dumpster DROP INDEX i_unread;
_EOF_
    push(@sql,$sql);
  }
  foreach my $group (@groups) {
    Migrate::logSql("Dropping i_tags_date indexes from $group.MAIL_ITEM_DUMPSTER...");
    my $sql = <<_EOF_;
ALTER TABLE $group.mail_item_dumpster DROP INDEX i_tags_date;
_EOF_
    push(@sql,$sql);
  }
  foreach my $group (@groups) {
    Migrate::logSql("Dropping i_flags_date indexes from $group.MAIL_ITEM_DUMPSTER...");
    my $sql = <<_EOF_;
ALTER TABLE $group.mail_item_dumpster DROP INDEX i_flags_date;
_EOF_
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);
}
