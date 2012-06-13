#!/usr/bin/perl
#
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2012 Zimbra, Inc.
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
my $concurrent = 10;
########################################################################################################################

Migrate::verifySchemaVersion(65);

my @groups = Migrate::getMailboxGroups();

addDeviceInformationColumns();
addPendingAclPushTable();
addTagTable();
addTaggedItemTable();
addVersionLastPurgeAtColumns();
addMailItemColumns();
dropIMTables();
 
Migrate::updateSchemaVersion(65, 90);

exit(0);

########################################################################################################################

sub addMailItemColumns() {
  Migrate::log("Adding/Modifying columns in mail_item, mail_item_dumpster, revision, revision_dumpster tables...");

  my @sql = ();
  foreach my $group (@groups) {
    my $sql = <<_EOF_;
ALTER TABLE $group.mail_item
  ADD COLUMN recipients VARCHAR(128) AFTER sender,
  MODIFY COLUMN blob_digest VARCHAR(44) BINARY,
  ADD COLUMN uuid VARCHAR(127) AFTER mod_content,
  ADD INDEX i_uuid (mailbox_id, uuid),
  MODIFY COLUMN name VARCHAR(255),
  ADD COLUMN tag_names TEXT AFTER tags;
_EOF_
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  @sql = ();
  foreach my $group (@groups) {
    my $sql = <<_EOF_;
ALTER TABLE $group.mail_item_dumpster
  ADD COLUMN recipients VARCHAR(128) AFTER sender,
  MODIFY COLUMN blob_digest VARCHAR(44) BINARY,
  ADD COLUMN uuid VARCHAR(127) AFTER mod_content,
  ADD INDEX i_uuid (mailbox_id, uuid),
  MODIFY COLUMN name VARCHAR(255),
  ADD COLUMN tag_names TEXT AFTER tags;
_EOF_
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  @sql = ();
  foreach my $group (@groups) {
    my $sql = <<_EOF_;
ALTER TABLE $group.revision
  MODIFY COLUMN blob_digest VARCHAR(44) BINARY,
  MODIFY COLUMN name VARCHAR(255);
_EOF_
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  @sql = ();
  foreach my $group (@groups) {
    my $sql = <<_EOF_;
ALTER TABLE $group.revision_dumpster
  MODIFY COLUMN blob_digest VARCHAR(44) BINARY,
  MODIFY COLUMN name VARCHAR(255);
_EOF_
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);
}

sub addTagTable() {
  my @sql = ();
  Migrate::log("Adding TAG table...");
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
  Migrate::log("Adding TAGGED_ITEM table...");
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

sub addPendingAclPushTable() {
  Migrate::log("Adding ZIMBRA.PENDING_ACL_PUSH table...");
  Migrate::logSql("Adding ZIMBRA.PENDING_ACL_PUSH table...");
  my $sqlStmt = <<_SQL_;
CREATE TABLE pending_acl_push (
     mailbox_id  INTEGER UNSIGNED NOT NULL,
     item_id     INTEGER UNSIGNED NOT NULL,
     date        BIGINT UNSIGNED NOT NULL,

     PRIMARY KEY (mailbox_id, item_id, date),
     CONSTRAINT fk_pending_acl_push_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE,
     INDEX i_date (date)
  ) ENGINE = InnoDB;
_SQL_

  Migrate::runSql($sqlStmt);
}

sub dropIMTables() {
  Migrate::log("Dropping IM tables...");
  Migrate::logSql("Dropping IM tables");
  my $sql = <<_SQL_;
USE zimbra;
DROP TABLE IF EXISTS jiveUserProp;
DROP TABLE IF EXISTS jiveGroupProp;
DROP TABLE IF EXISTS jiveGroupUser;
DROP TABLE IF EXISTS jivePrivate;
DROP TABLE IF EXISTS jiveOffline;
DROP TABLE IF EXISTS jiveRoster;
DROP TABLE IF EXISTS jiveRosterGroups;
DROP TABLE IF EXISTS jiveVCard;
DROP TABLE IF EXISTS jiveID;
DROP TABLE IF EXISTS jiveProperty;
DROP TABLE IF EXISTS jiveVersion;
DROP TABLE IF EXISTS jiveExtComponentConf;
DROP TABLE IF EXISTS jiveRemoteServerConf;
DROP TABLE IF EXISTS jivePrivacyList;
DROP TABLE IF EXISTS jiveSASLAuthorized;
DROP TABLE IF EXISTS mucRoom;
DROP TABLE IF EXISTS mucRoomProp;
DROP TABLE IF EXISTS mucAffiliation;
DROP TABLE IF EXISTS mucMember;
DROP TABLE IF EXISTS mucConversationLog;
_SQL_
  Migrate::runSql($sql);
}

sub addDeviceInformationColumns() {
    my $sql = <<MOBILE_DEVICES_ADD_COLUMN_EOF;
ALTER TABLE mobile_devices
  ADD COLUMN last_used_date DATE,
  ADD COLUMN deleted_by_user BOOLEAN NOT NULL DEFAULT 0 AFTER last_used_date,
  ADD INDEX i_last_used_date (last_used_date),
  ADD COLUMN model VARCHAR(64),
  ADD COLUMN imei VARCHAR(64),
  ADD COLUMN friendly_name VARCHAR(512),
  ADD COLUMN os VARCHAR(64),
  ADD COLUMN os_language VARCHAR(64),
  ADD COLUMN phone_number VARCHAR(64),
  ADD COLUMN unapproved_appl_list TEXT NULL,
  ADD COLUMN approved_appl_list TEXT NULL;
MOBILE_DEVICES_ADD_COLUMN_EOF

    Migrate::log("Adding device information columns to ZIMBRA.MOBILE_DEVICES table...");
    Migrate::runSql($sql);
}

sub addVersionLastPurgeAtColumns() {
    my $sql = <<MAILBOX_ADD_COLUMN_EOF;
ALTER TABLE mailbox 
  ADD COLUMN version VARCHAR(16),
  ADD COLUMN last_purge_at INTEGER UNSIGNED NOT NULL DEFAULT 0;
MAILBOX_ADD_COLUMN_EOF

    Migrate::log("Adding version and last_purge_at columns to ZIMBRA.MAILBOX table...");
    Migrate::runSql($sql);
}
