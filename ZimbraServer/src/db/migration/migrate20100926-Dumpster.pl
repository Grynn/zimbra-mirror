#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2010 Zimbra, Inc.
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

sub addDumpsterTables($);

Migrate::verifySchemaVersion(64);
foreach my $group (Migrate::getMailboxGroups()) {
    addDumpsterTables($group);
}
Migrate::updateSchemaVersion(64, 65);

exit(0);

#####################

sub addDumpsterTables($) {
  my ($DATABASE_NAME) = @_;
  
  Migrate::log("Adding dumpster tables to $DATABASE_NAME.");

  my $sql = <<ADD_TABLES_EOF;

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.mail_item_dumpster (
   mailbox_id    INTEGER UNSIGNED NOT NULL,
   id            INTEGER UNSIGNED NOT NULL,
   type          TINYINT NOT NULL,           -- 1 = folder, 3 = tag, etc.
   parent_id     INTEGER UNSIGNED,
   folder_id     INTEGER UNSIGNED,
   index_id      INTEGER UNSIGNED,
   imap_id       INTEGER UNSIGNED,
   date          INTEGER UNSIGNED NOT NULL,  -- stored as a UNIX-style timestamp
   size          BIGINT UNSIGNED NOT NULL,
   volume_id     TINYINT UNSIGNED,
   blob_digest   VARCHAR(28) BINARY,         -- reference to blob, meaningful for messages only (type == 5)
   unread        INTEGER UNSIGNED,           -- stored separately from the other flags so we can index it
   flags         INTEGER NOT NULL DEFAULT 0,
   tags          BIGINT NOT NULL DEFAULT 0,
   sender        VARCHAR(128),
   subject       TEXT,
   name          VARCHAR(128),               -- namespace entry for item (e.g. tag name, folder name, document filename)
   metadata      MEDIUMTEXT,
   mod_metadata  INTEGER UNSIGNED NOT NULL,  -- change number for last row modification
   change_date   INTEGER UNSIGNED,           -- UNIX-style timestamp for last row modification
   mod_content   INTEGER UNSIGNED NOT NULL,  -- change number for last change to "content" (e.g. blob)

   PRIMARY KEY (mailbox_id, id),
   INDEX i_type (mailbox_id, type),          -- for looking up folders and tags
   INDEX i_parent_id (mailbox_id, parent_id),-- for looking up a parent\'s children
   INDEX i_folder_id_date (mailbox_id, folder_id, date), -- for looking up by folder and sorting by date
   INDEX i_index_id (mailbox_id, index_id),  -- for looking up based on search results
   INDEX i_unread (mailbox_id, unread),      -- there should be a small number of items with unread=TRUE
                                             -- no compound index on (unread, date), so we save space at
                                             -- the expense of sorting a small number of rows
   INDEX i_date (mailbox_id, date),          -- fallback index in case other constraints are not specified
   INDEX i_mod_metadata (mailbox_id, mod_metadata),      -- used by the sync code
   INDEX i_tags_date (mailbox_id, tags, date),           -- for tag searches
   INDEX i_flags_date (mailbox_id, flags, date),         -- for flag searches
   INDEX i_volume_id (mailbox_id, volume_id),            -- for the foreign key into the volume table

   -- Must not enforce unique index on (mailbox_id, folder_id, name) for the dumpster version!

   CONSTRAINT fk_mail_item_dumpster_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
   CONSTRAINT fk_mail_item_dumpster_volume_id FOREIGN KEY (volume_id) REFERENCES zimbra.volume(id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.revision_dumpster (
   mailbox_id    INTEGER UNSIGNED NOT NULL,
   item_id       INTEGER UNSIGNED NOT NULL,
   version       INTEGER UNSIGNED NOT NULL,
   date          INTEGER UNSIGNED NOT NULL,  -- stored as a UNIX-style timestamp
   size          BIGINT UNSIGNED NOT NULL,
   volume_id     TINYINT UNSIGNED,
   blob_digest   VARCHAR(28) BINARY,         -- reference to blob, meaningful for messages only (type == 5)
   name          VARCHAR(128),               -- namespace entry for item (e.g. tag name, folder name, document filename)
   metadata      MEDIUMTEXT,
   mod_metadata  INTEGER UNSIGNED NOT NULL,  -- change number for last row modification
   change_date   INTEGER UNSIGNED,           -- UNIX-style timestamp for last row modification
   mod_content   INTEGER UNSIGNED NOT NULL,  -- change number for last change to "content" (e.g. blob)

   PRIMARY KEY (mailbox_id, item_id, version),

   CONSTRAINT fk_revision_dumpster_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
   CONSTRAINT fk_revision_dumpster_item_id FOREIGN KEY (mailbox_id, item_id) REFERENCES ${DATABASE_NAME}.mail_item_dumpster(mailbox_id, id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.appointment_dumpster (
   mailbox_id  INTEGER UNSIGNED NOT NULL,
   uid         VARCHAR(255) NOT NULL,
   item_id     INTEGER UNSIGNED NOT NULL,
   start_time  DATETIME NOT NULL,
   end_time    DATETIME,

   PRIMARY KEY (mailbox_id, uid),
   CONSTRAINT fk_appointment_dumpster_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
   CONSTRAINT fk_appointment_dumpster_item_id FOREIGN KEY (mailbox_id, item_id) REFERENCES ${DATABASE_NAME}.mail_item_dumpster(mailbox_id, id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE UNIQUE INDEX i_item_id ON ${DATABASE_NAME}.appointment_dumpster (mailbox_id, item_id);

ADD_TABLES_EOF

  Migrate::runSql($sql);
}
