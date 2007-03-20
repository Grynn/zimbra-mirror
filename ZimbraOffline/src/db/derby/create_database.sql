-- 
-- ***** BEGIN LICENSE BLOCK *****
-- 
-- Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
-- All Rights Reserved.
-- 
-- The Original Code is: Zimbra Network
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
   size          INTEGER NOT NULL,
   volume_id     SMALLINT,
   blob_digest   VARCHAR(28),                -- reference to blob, meaningful for messages only (type == 5)
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

   PRIMARY KEY (mailbox_id, id),
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
--  OPEN_CONVERSATION
-- -----------------------------------------------------------------------

CREATE TABLE ${DATABASE_NAME}.open_conversation (
   mailbox_id  INTEGER NOT NULL,
   hash        CHAR(28) NOT NULL,
   conv_id     INTEGER NOT NULL,

   PRIMARY KEY (mailbox_id, hash),
   CONSTRAINT fk_open_conversation_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
   CONSTRAINT fk_open_conversation_conv_id FOREIGN KEY (mailbox_id, conv_id) REFERENCES ${DATABASE_NAME}.mail_item(mailbox_id, id)
      ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE UNIQUE INDEX ${DATABASE_NAME}.i_open_conversation_conv_id
ON open_conversation(mailbox_id, conv_id);


-- -----------------------------------------------------------------------
--  APPOINTMENT
-- -----------------------------------------------------------------------

CREATE TABLE ${DATABASE_NAME}.appointment (
   mailbox_id  INTEGER NOT NULL,
   uid         VARCHAR(255) NOT NULL,
   item_id     INTEGER NOT NULL,
   start_time  TIMESTAMP NOT NULL,
   end_time    TIMESTAMP,

   PRIMARY KEY (mailbox_id, uid),
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

   PRIMARY KEY (mailbox_id, item_id),
   CONSTRAINT fk_pop3_message_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id)
      ON DELETE CASCADE
);

CREATE UNIQUE INDEX ${DATABASE_NAME}.i_uid_pop3_id
ON pop3_message(uid, data_source_id);
