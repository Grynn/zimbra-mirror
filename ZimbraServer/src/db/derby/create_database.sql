--
-- ***** BEGIN LICENSE BLOCK *****
--
-- Zimbra Collaboration Suite Server
-- Copyright (C) 2007 Zimbra, Inc.
--
-- The contents of this file are subject to the Yahoo! Public License
-- Version 1.0 ("License"); you may not use this file except in
-- compliance with the License.  You may obtain a copy of the License at
-- http://www.zimbra.com/license.
--
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
--
-- ***** END LICENSE BLOCK *****
-- 

CREATE SCHEMA ${DATABASE_NAME};

-- -----------------------------------------------------------------------
--  MAIL_ITEM
-- -----------------------------------------------------------------------

CREATE TABLE ${DATABASE_NAME}.mail_item (
   mailbox_id    INTEGER NOT NULL,
   id            INTEGER NOT NULL,
   type          SMALLINT NOT NULL,          -- 1 = folder, 3 = tag, etc.
   parent_id     INTEGER,
   folder_id     INTEGER,
   index_id      INTEGER,
   imap_id       INTEGER,
   date          INTEGER NOT NULL,           -- stored as a UNIX-style timestamp
   size          BIGINT NOT NULL,
   volume_id     SMALLINT,
   blob_digest   VARCHAR(28),                -- SHA-1 hash of blob, or NULL if item has no blob
   unread        INTEGER,                    -- stored separately from the other flags so we can index it
   flags         INTEGER NOT NULL DEFAULT 0,
   tags          BIGINT NOT NULL DEFAULT 0,
   sender        VARCHAR(128),
   subject       VARCHAR(1024),
   name          VARCHAR(128),               -- namespace entry for item (e.g. tag name, folder name, document filename)
   metadata      CLOB,
   mod_metadata  INTEGER NOT NULL,           -- change number for last row modification
   change_date   INTEGER,                    -- UNIX-style timestamp for last row modification
   mod_content   INTEGER NOT NULL,           -- change number for last change to "content" (e.g. blob)
   change_mask   INTEGER,                    -- bitmask of changes since the last server push

   CONSTRAINT pk_mail_item PRIMARY KEY (mailbox_id, id),
   CONSTRAINT fk_mail_item_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
   CONSTRAINT fk_mail_item_volume_id FOREIGN KEY (volume_id) REFERENCES zimbra.volume(id),
   CONSTRAINT fk_mail_item_parent_id FOREIGN KEY (mailbox_id, parent_id) REFERENCES ${DATABASE_NAME}.mail_item(mailbox_id, id),
   CONSTRAINT fk_mail_item_folder_id FOREIGN KEY (mailbox_id, folder_id) REFERENCES ${DATABASE_NAME}.mail_item(mailbox_id, id)
);

CREATE INDEX ${DATABASE_NAME}.i_mail_item_type
ON mail_item(mailbox_id, type);                      -- for looking up folders and tags

CREATE INDEX ${DATABASE_NAME}.i_mail_item_folder_id_date
ON mail_item(mailbox_id, folder_id, date DESC);      -- for looking up by folder and sorting by date

CREATE INDEX ${DATABASE_NAME}.i_mail_item_index_id
ON mail_item(mailbox_id, index_id);                  -- for looking up based on search results

CREATE INDEX ${DATABASE_NAME}.i_mail_item_unread
ON mail_item(mailbox_id, unread);                    -- there should be a small number of items with unread=TRUE

                             -- no compound index on (unread, date); so we save space at
                             -- the expense of sorting a small number of rows
                                             
CREATE INDEX ${DATABASE_NAME}.i_mail_item_date
ON mail_item(mailbox_id, date DESC);                      -- fallback index in case other constraints are not specified

CREATE INDEX ${DATABASE_NAME}.i_mail_item_mod_metadata
ON mail_item(mailbox_id, mod_metadata);              -- used by the sync code

CREATE INDEX ${DATABASE_NAME}.i_mail_item_tags_date
ON mail_item(mailbox_id, tags, date DESC);           -- for tag searches

CREATE INDEX ${DATABASE_NAME}.i_mail_item_flags_date
ON mail_item(mailbox_id, flags, date DESC);          -- for flag searches

CREATE INDEX ${DATABASE_NAME}.i_mail_item_volume_id
ON mail_item(mailbox_id, volume_id);                 -- for the foreign key into the volume table

CREATE INDEX ${DATABASE_NAME}.i_mail_item_change_mask
ON mail_item(mailbox_id, change_mask);               -- for figuring out which items to push during sync

                              -- the following is a UNIQUE INDEX in the mainline database schema

CREATE INDEX ${DATABASE_NAME}.i_mail_item_name_folder_id
ON mail_item(mailbox_id, folder_id, name);           -- for namespace uniqueness


-- -----------------------------------------------------------------------
--  REVISION
-- -----------------------------------------------------------------------

CREATE TABLE ${DATABASE_NAME}.revision (
   mailbox_id    INTEGER NOT NULL,
   item_id       INTEGER NOT NULL,
   version       INTEGER NOT NULL,
   date          INTEGER NOT NULL,           -- stored as a UNIX-style timestamp
   size          BIGINT NOT NULL,
   volume_id     SMALLINT,
   blob_digest   VARCHAR(28),                -- SHA-1 hash of blob, or NULL if item has no blob
   name          VARCHAR(128),               -- namespace entry for item (e.g. tag name, folder name, document filename)
   metadata      CLOB,
   mod_metadata  INTEGER NOT NULL,           -- change number for last row modification
   change_date   INTEGER,                    -- UNIX-style timestamp for last row modification
   mod_content   INTEGER NOT NULL,           -- change number for last change to "content" (e.g. blob)

   CONSTRAINT pk_revision PRIMARY KEY (mailbox_id, item_id, version),
   CONSTRAINT fk_revision_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
   CONSTRAINT fk_revision_item_id FOREIGN KEY (mailbox_id, item_id) REFERENCES ${DATABASE_NAME}.mail_item(mailbox_id, id)
      ON DELETE CASCADE ON UPDATE NO ACTION
);


-- -----------------------------------------------------------------------
--  OPEN_CONVERSATION
-- -----------------------------------------------------------------------

CREATE TABLE ${DATABASE_NAME}.open_conversation (
   mailbox_id  INTEGER NOT NULL,
   hash        CHAR(28) NOT NULL,
   conv_id     INTEGER NOT NULL,

   CONSTRAINT pk_open_conversation PRIMARY KEY (mailbox_id, hash),
   CONSTRAINT ui_open_conversation_conv_id UNIQUE (mailbox_id, conv_id),
   CONSTRAINT fk_open_conversation_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
   CONSTRAINT fk_open_conversation_conv_id FOREIGN KEY (mailbox_id, conv_id) REFERENCES ${DATABASE_NAME}.mail_item(mailbox_id, id)
      ON DELETE CASCADE ON UPDATE NO ACTION
);


-- -----------------------------------------------------------------------
--  APPOINTMENT
-- -----------------------------------------------------------------------

CREATE TABLE ${DATABASE_NAME}.appointment (
   mailbox_id  INTEGER NOT NULL,
   uid         VARCHAR(255) NOT NULL,
   item_id     INTEGER NOT NULL,
   start_time  TIMESTAMP NOT NULL,
   end_time    TIMESTAMP,

   CONSTRAINT pk_appointment PRIMARY KEY (mailbox_id, uid),
   CONSTRAINT fk_appointment_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
   CONSTRAINT fk_appointment_item_id FOREIGN KEY (mailbox_id, item_id) REFERENCES ${DATABASE_NAME}.mail_item(mailbox_id, id)
      ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE INDEX ${DATABASE_NAME}.i_appointment_item_id
ON appointment(mailbox_id, item_id);


-- -----------------------------------------------------------------------
--  TOMBSTONE
-- -----------------------------------------------------------------------

CREATE TABLE ${DATABASE_NAME}.tombstone (
   mailbox_id  INTEGER NOT NULL,
   sequence    INTEGER NOT NULL,     -- change number for deletion
   date        INTEGER NOT NULL,     -- deletion date as a UNIX-style timestamp
   type        SMALLINT,             -- 1 = folder, 3 = tag, etc.
   ids         CLOB,

   CONSTRAINT fk_tombstone_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id)
      ON DELETE CASCADE
);

CREATE INDEX ${DATABASE_NAME}.i_tombstone_sequence
ON tombstone(mailbox_id, sequence);


-- -----------------------------------------------------------------------
--  POP3_MESSAGE
-- -----------------------------------------------------------------------

CREATE TABLE ${DATABASE_NAME}.pop3_message (
   mailbox_id      INTEGER NOT NULL,
   data_source_id  CHAR(36) NOT NULL,
   uid             VARCHAR(255) NOT NULL,
   item_id         INTEGER NOT NULL,

   CONSTRAINT pk_pop3_message PRIMARY KEY (mailbox_id, item_id),
   CONSTRAINT ui_uid_pop3_id UNIQUE (uid, data_source_id),
   CONSTRAINT fk_pop3_message_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id)
      ON DELETE CASCADE
);

-- Tracks folders on remote IMAP servers
CREATE TABLE ${DATABASE_NAME}.imap_folder (
   mailbox_id         INTEGER NOT NULL,
   item_id            INTEGER NOT NULL,
   data_source_id     CHAR(36) NOT NULL,
   local_path         VARCHAR(1000) NOT NULL,
   remote_path        VARCHAR(1000) NOT NULL,
   uid_validity       INTEGER,
   PRIMARY KEY (mailbox_id, item_id),
   CONSTRAINT fk_imap_folder_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX i_local_path
ON ${DATABASE_NAME}.imap_folder (local_path, data_source_id, mailbox_id);

CREATE UNIQUE INDEX i_remote_path
ON ${DATABASE_NAME}.imap_folder (remote_path, data_source_id, mailbox_id);

-- Tracks messages on remote IMAP servers
CREATE TABLE ${DATABASE_NAME}.imap_message (
   mailbox_id     INTEGER NOT NULL,
   imap_folder_id INTEGER NOT NULL,
   uid            BIGINT NOT NULL,
   item_id        INTEGER NOT NULL,
   flags         INTEGER NOT NULL DEFAULT 0,
   
   PRIMARY KEY (mailbox_id, item_id),
   CONSTRAINT fk_imap_message_mailbox_id FOREIGN KEY (mailbox_id)
      REFERENCES zimbra.mailbox(id) ON DELETE CASCADE,
   CONSTRAINT fk_imap_message_imap_folder_id FOREIGN KEY (mailbox_id, imap_folder_id)
      REFERENCES ${DATABASE_NAME}.imap_folder(mailbox_id, item_id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX i_uid_imap_id ON ${DATABASE_NAME}.imap_message (mailbox_id, imap_folder_id, uid);
