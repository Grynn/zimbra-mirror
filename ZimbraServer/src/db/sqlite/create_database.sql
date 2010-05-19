-- 
-- ***** BEGIN LICENSE BLOCK *****
-- Zimbra Collaboration Suite Server
-- Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
-- 
-- The contents of this file are subject to the Zimbra Public License
-- Version 1.3 ("License"); you may not use this file except in
-- compliance with the License.  You may obtain a copy of the License at
-- http://www.zimbra.com/license.
-- 
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
-- ***** END LICENSE BLOCK *****
-- 

PRAGMA ${DATABASE_NAME}.default_cache_size = 2000;
PRAGMA ${DATABASE_NAME}.encoding = "UTF-8";
PRAGMA ${DATABASE_NAME}.legacy_file_format = OFF;

-- -----------------------------------------------------------------------
-- mailbox statistics
-- -----------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.mailbox (
   id                  BIGINT UNSIGNED NOT NULL PRIMARY KEY,
   account_id          VARCHAR(127) NOT NULL UNIQUE,  -- e.g. "d94e42c4-1636-11d9-b904-4dd689d02402"
   index_volume_id     INTEGER NOT NULL,
   item_id_checkpoint  INTEGER UNSIGNED NOT NULL DEFAULT 0,
   contact_count       INTEGER UNSIGNED DEFAULT 0,
   size_checkpoint     BIGINT UNSIGNED NOT NULL DEFAULT 0,
   change_checkpoint   INTEGER UNSIGNED NOT NULL DEFAULT 0,
   tracking_sync       INTEGER UNSIGNED NOT NULL DEFAULT 0,
   tracking_imap       BOOLEAN NOT NULL DEFAULT 0,
   last_soap_access    INTEGER UNSIGNED NOT NULL DEFAULT 0,
   new_messages        INTEGER UNSIGNED NOT NULL DEFAULT 0,
   idx_deferred_count  INTEGER UNSIGNED NOT NULL DEFAULT 0,
   highest_indexed     VARCHAR(21)                    -- mod_content of highest item in the index
);

-- -----------------------------------------------------------------------
-- mailbox metadata info
-- -----------------------------------------------------------------------

CREATE TABLE ${DATABASE_NAME}.mailbox_metadata (
   section     VARCHAR(64) NOT NULL PRIMARY KEY,      -- e.g. "imap"
   metadata    MEDIUMTEXT
);

-- -----------------------------------------------------------------------
-- out-of-office reply history
-- -----------------------------------------------------------------------

CREATE TABLE ${DATABASE_NAME}.out_of_office (
   sent_to     VARCHAR(255) NOT NULL PRIMARY KEY,
   sent_on     DATETIME NOT NULL
);

CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_out_of_office_sent_on ON out_of_office(sent_on);


-- -----------------------------------------------------------------------
-- items in mailboxes
-- -----------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.mail_item (
   id            INTEGER UNSIGNED NOT NULL PRIMARY KEY,
   type          TINYINT NOT NULL,           -- 1 = folder, 3 = tag, etc.
   parent_id     INTEGER UNSIGNED,
   folder_id     INTEGER UNSIGNED,
   index_id      INTEGER UNSIGNED,
   imap_id       INTEGER UNSIGNED,
   date          INTEGER UNSIGNED NOT NULL,  -- stored as a UNIX-style timestamp
   size          BIGINT UNSIGNED NOT NULL,
   volume_id     TINYINT UNSIGNED,
   blob_digest   VARCHAR(28),                -- reference to blob, meaningful only for certain item types
   unread        INTEGER UNSIGNED,           -- stored separately from the other flags so we can index it
   flags         INTEGER NOT NULL DEFAULT 0,
   tags          BIGINT NOT NULL DEFAULT 0,
   sender        VARCHAR(128),
   subject       TEXT,
   name          VARCHAR(128),               -- namespace entry for item (e.g. tag name, folder name, document/wiki filename)
   metadata      MEDIUMTEXT,
   mod_metadata  INTEGER UNSIGNED NOT NULL,  -- change number for last row modification
   change_date   INTEGER UNSIGNED,           -- UNIX-style timestamp for last row modification
   mod_content   INTEGER UNSIGNED NOT NULL,  -- change number for last change to "content" (e.g. blob)
   change_mask   INTEGER UNSIGNED,           -- bitmask of changes since the last server push

   -- UNIQUE (folder_id, name),  -- for namespace uniqueness

   -- CONSTRAINT fk_mail_item_volume_id FOREIGN KEY (volume_id) REFERENCES zimbra.volume(id),
   CONSTRAINT fk_mail_item_parent_id FOREIGN KEY (parent_id) REFERENCES mail_item(id) ON UPDATE CASCADE,
   CONSTRAINT fk_mail_item_folder_id FOREIGN KEY (folder_id) REFERENCES mail_item(id) ON UPDATE CASCADE
);

CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_type ON mail_item(type);                      -- for looking up folders and tags
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_parent_id ON mail_item(parent_id);            -- for looking up a parent\'s children
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_folder_id_date ON mail_item(folder_id, date DESC); -- for looking up by folder and sorting by date
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_index_id ON mail_item(index_id);              -- for looking up based on search results
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_unread ON mail_item(unread);                  -- there should be a small number of items with unread=TRUE
                                                                         --   no compound index on (unread, date), so we save space at
                                                                         --   the expense of sorting a small number of rows
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_date ON mail_item(date DESC);                 -- fallback index in case other constraints are not specified
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_mod_metadata ON mail_item(mod_metadata);      -- used by the sync code
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_tags_date ON mail_item(tags, date DESC);      -- for tag searches
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_flags_date ON mail_item(flags, date DESC);    -- for flag searches
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_change_mask ON mail_item(change_mask);  -- for figuring out which items to push during sync

-- -----------------------------------------------------------------------
-- old versions of existing items
-- -----------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.revision (
   item_id       INTEGER UNSIGNED NOT NULL,
   version       INTEGER UNSIGNED NOT NULL,
   date          INTEGER UNSIGNED NOT NULL,  -- stored as a UNIX-style timestamp
   size          BIGINT UNSIGNED NOT NULL,
   volume_id     TINYINT UNSIGNED,
   blob_digest   VARCHAR(28),                -- reference to blob, meaningful for messages only (type == 5)
   name          VARCHAR(128),               -- namespace entry for item (e.g. tag name, folder name, document filename)
   metadata      MEDIUMTEXT,
   mod_metadata  INTEGER UNSIGNED NOT NULL,  -- change number for last row modification
   change_date   INTEGER UNSIGNED,           -- UNIX-style timestamp for last row modification
   mod_content   INTEGER UNSIGNED NOT NULL,  -- change number for last change to "content" (e.g. blob)

   PRIMARY KEY (item_id, version),
   CONSTRAINT fk_revision_item_id FOREIGN KEY (item_id) REFERENCES mail_item(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- -----------------------------------------------------------------------
-- conversations receiving new mail
-- -----------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.open_conversation (
   hash        CHAR(28) NOT NULL PRIMARY KEY,
   conv_id     INTEGER UNSIGNED NOT NULL,

   CONSTRAINT fk_open_conversation_conv_id FOREIGN KEY (conv_id) REFERENCES mail_item(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_open_conversation_conv_id ON open_conversation(conv_id);

-- -----------------------------------------------------------------------
-- calendar items (appointments, todos)
-- -----------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.appointment (
   uid         VARCHAR(255) NOT NULL PRIMARY KEY,
   item_id     INTEGER UNSIGNED NOT NULL UNIQUE,
   start_time  DATETIME NOT NULL,
   end_time    DATETIME,

   CONSTRAINT fk_appointment_item_id FOREIGN KEY (item_id) REFERENCES mail_item(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- -----------------------------------------------------------------------
-- deletion records for sync
-- -----------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.tombstone (
   sequence    INTEGER UNSIGNED NOT NULL,  -- change number for deletion
   date        INTEGER UNSIGNED NOT NULL,  -- deletion date as a UNIX-style timestamp
   type        TINYINT,                    -- 1 = folder, 3 = tag, etc.
   ids         TEXT
);

CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_sequence ON tombstone(sequence);

-- -----------------------------------------------------------------------
-- POP3 and IMAP sync
-- -----------------------------------------------------------------------

-- Tracks UID's of messages on remote POP3 servers
CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.pop3_message (
   data_source_id  CHAR(36) NOT NULL,
   uid             VARCHAR(255) NOT NULL,
   item_id         INTEGER UNSIGNED NOT NULL PRIMARY KEY,

   UNIQUE (uid, data_source_id)
);

-- Tracks folders on remote IMAP servers
CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.imap_folder (
   item_id            INTEGER UNSIGNED NOT NULL PRIMARY KEY,
   data_source_id     CHAR(36) NOT NULL,
   local_path         VARCHAR(1000) NOT NULL,
   remote_path        VARCHAR(1000) NOT NULL,
   uid_validity       INTEGER UNSIGNED,

   UNIQUE (local_path, data_source_id),
   UNIQUE (remote_path, data_source_id)
);

-- Tracks messages on remote IMAP servers
CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.imap_message (
   imap_folder_id  INTEGER UNSIGNED NOT NULL PRIMARY KEY,
   uid             BIGINT NOT NULL,
   item_id         INTEGER UNSIGNED NOT NULL,
   flags           INTEGER NOT NULL DEFAULT 0,

   UNIQUE (imap_folder_id, uid),
   CONSTRAINT fk_imap_message_imap_folder_id FOREIGN KEY (imap_folder_id) REFERENCES imap_folder(item_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tracks local MailItem created from remote objects via DataSource
CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.data_source_item (
   data_source_id  CHAR(36) NOT NULL,
   item_id         INTEGER UNSIGNED NOT NULL PRIMARY KEY,
   folder_id       INTEGER UNSIGNED NOT NULL DEFAULT 0,
   remote_id       VARCHAR(255) NOT NULL,
   metadata        MEDIUMTEXT,

   UNIQUE (data_source_id, remote_id)
);
