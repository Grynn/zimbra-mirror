-- 
-- ***** BEGIN LICENSE BLOCK *****
-- 
-- Zimbra Collaboration Suite Server
-- Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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

PRAGMA encoding = "UTF-8"%

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

   -- UNIQUE (folder_id, name),  -- for namespace uniqueness

   -- CONSTRAINT fk_mail_item_volume_id FOREIGN KEY (volume_id) REFERENCES zimbra.volume(id),
   CONSTRAINT fk_mail_item_parent_id FOREIGN KEY (parent_id) REFERENCES mail_item(id),
   CONSTRAINT fk_mail_item_folder_id FOREIGN KEY (folder_id) REFERENCES mail_item(id)
)%

CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_type ON mail_item(type)%                      -- for looking up folders and tags
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_parent_id ON mail_item(parent_id)%            -- for looking up a parent\'s children
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_folder_id_date ON mail_item(folder_id, date)% -- for looking up by folder and sorting by date
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_index_id ON mail_item(index_id)%              -- for looking up based on search results
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_unread ON mail_item(unread)%                  -- there should be a small number of items with unread=TRUE
                                                                         --   no compound index on (unread, date), so we save space at
                                                                         --   the expense of sorting a small number of rows
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_date ON mail_item(date)%                      -- fallback index in case other constraints are not specified
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_mod_metadata ON mail_item(mod_metadata)%      -- used by the sync code
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_tags_date ON mail_item(tags, date)%           -- for tag searches
CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_mail_item_flags_date ON mail_item(flags, date)%         -- for flag searches

-- -- CONSTRAINT fk_mail_item_parent_id FOREIGN KEY (parent_id) REFERENCES mail_item(id)
-- CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fki_mail_item_parent_id
-- AFTER INSERT ON [mail_item]
-- FOR EACH ROW BEGIN
--   SELECT RAISE(ROLLBACK, 'insert on table "mail_item" violates foreign key constraint "fki_mail_item_parent_id"')
--   WHERE NEW.parent_id IS NOT NULL AND (SELECT COUNT(*) FROM mail_item WHERE id = NEW.parent_id) IS NULL;
-- END%

-- CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fku_mail_item_parent_id
-- BEFORE UPDATE OF parent_id ON [mail_item]
-- FOR EACH ROW BEGIN
--     SELECT RAISE(ROLLBACK, 'update on table "mail_item" violates foreign key constraint "fku_mail_item_parent_id"')
--       WHERE NEW.parent_id IS NOT NULL AND (SELECT id FROM mail_item WHERE id = NEW.parent_id) IS NULL;
-- END%

-- CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fkd_mail_item_parent_id
-- BEFORE DELETE ON [mail_item]
-- FOR EACH ROW BEGIN
--   SELECT RAISE(ROLLBACK, 'delete on table "mail_item" violates foreign key constraint "fkd_mail_item_parent_id"')
--   WHERE (SELECT parent_id FROM mail_item WHERE parent_id = OLD.id) IS NOT NULL;
-- END%

-- -- CONSTRAINT fk_mail_item_folder_id FOREIGN KEY (folder_id) REFERENCES mail_item(id)
-- CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fki_mail_item_folder_id
-- AFTER INSERT ON [mail_item]
-- FOR EACH ROW BEGIN
--   SELECT RAISE(ROLLBACK, 'insert on table "mail_item" violates foreign key constraint "fki_mail_item_folder_id"')
--   WHERE (SELECT id FROM mail_item WHERE id = NEW.folder_id) IS NULL;
-- END%

-- CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fku_mail_item_folder_id
-- BEFORE UPDATE OF folder_id ON [mail_item]
-- FOR EACH ROW BEGIN
--     SELECT RAISE(ROLLBACK, 'update on table "mail_item" violates foreign key constraint "fku_mail_item_folder_id"')
--       WHERE (SELECT id FROM mail_item WHERE id = NEW.folder_id) IS NULL;
-- END%

-- CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fkd_mail_item_folder_id
-- BEFORE DELETE ON [mail_item]
-- FOR EACH ROW BEGIN
--   SELECT RAISE(ROLLBACK, 'delete on table "mail_item" violates foreign key constraint "fkd_mail_item_folder_id"')
--   WHERE (SELECT folder_id FROM mail_item WHERE folder_id = OLD.id) IS NOT NULL;
-- END%

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
   CONSTRAINT fk_revision_item_id FOREIGN KEY (item_id) REFERENCES mail_item(id) ON DELETE CASCADE
)%

-- -- CONSTRAINT fk_revision_item_id FOREIGN KEY (item_id) REFERENCES mail_item(id) ON DELETE CASCADE
-- CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fki_revision_item_id
-- BEFORE INSERT ON [revision]
-- FOR EACH ROW BEGIN
--   SELECT RAISE(ROLLBACK, 'insert on table "revision" violates foreign key constraint "fki_revision_item_id"')
--   WHERE (SELECT id FROM mail_item WHERE id = NEW.item_id) IS NULL;
-- END%

-- CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fku_revision_item_id
-- BEFORE UPDATE OF item_id ON [revision]
-- FOR EACH ROW BEGIN
--     SELECT RAISE(ROLLBACK, 'update on table "revision" violates foreign key constraint "fku_revision_item_id"')
--       WHERE (SELECT id FROM mail_item WHERE id = NEW.item_id) IS NULL;
-- END%

CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fkdc_revision_item_id
BEFORE DELETE ON mail_item
FOR EACH ROW BEGIN 
    DELETE FROM revision WHERE item_id = OLD.id;
END%

-- -----------------------------------------------------------------------
-- conversations receiving new mail
-- -----------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.open_conversation (
   hash        CHAR(28) NOT NULL PRIMARY KEY,
   conv_id     INTEGER UNSIGNED NOT NULL,

   CONSTRAINT fk_open_conversation_conv_id FOREIGN KEY (conv_id) REFERENCES mail_item(id) ON DELETE CASCADE
)%

CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_open_conversation_conv_id ON open_conversation(conv_id)%

-- -- CONSTRAINT fk_open_conversation_conv_id FOREIGN KEY (conv_id) REFERENCES mail_item(id) ON DELETE CASCADE
-- CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fki_open_conversation_conv_id
-- BEFORE INSERT ON [open_conversation]
-- FOR EACH ROW BEGIN
--   SELECT RAISE(ROLLBACK, 'insert on table "open_conversation" violates foreign key constraint "fki_open_conversation_conv_id"')
--   WHERE (SELECT id FROM mail_item WHERE id = NEW.conv_id) IS NULL;
-- END%

-- CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fku_open_conversation_conv_id
-- BEFORE UPDATE OF conv_id ON [open_conversation]
-- FOR EACH ROW BEGIN
--     SELECT RAISE(ROLLBACK, 'update on table "open_conversation" violates foreign key constraint "fku_open_conversation_conv_id"')
--       WHERE (SELECT id FROM mail_item WHERE id = NEW.conv_id) IS NULL;
-- END%

CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fkdc_open_conversation_conv_id
BEFORE DELETE ON mail_item
FOR EACH ROW BEGIN 
    DELETE FROM open_conversation WHERE conv_id = OLD.id;
END%

-- -----------------------------------------------------------------------
-- calendar items (appointments, todos)
-- -----------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.appointment (
   uid         VARCHAR(255) NOT NULL PRIMARY KEY,
   item_id     INTEGER UNSIGNED NOT NULL UNIQUE,
   start_time  DATETIME NOT NULL,
   end_time    DATETIME,

   CONSTRAINT fk_appointment_item_id FOREIGN KEY (item_id) REFERENCES mail_item(id) ON DELETE CASCADE
)%

-- -- CONSTRAINT fk_appointment_item_id FOREIGN KEY (item_id) REFERENCES mail_item(id) ON DELETE CASCADE
-- CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fki_appointment_item_id
-- BEFORE INSERT ON [appointment]
-- FOR EACH ROW BEGIN
--   SELECT RAISE(ROLLBACK, 'insert on table "appointment" violates foreign key constraint "fki_appointment_item_id"')
--   WHERE (SELECT id FROM mail_item WHERE id = NEW.item_id) IS NULL;
-- END%

-- CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fku_appointment_item_id
-- BEFORE UPDATE OF item_id ON [appointment]
-- FOR EACH ROW BEGIN
--     SELECT RAISE(ROLLBACK, 'update on table "appointment" violates foreign key constraint "fku_appointment_item_id"')
--       WHERE (SELECT id FROM mail_item WHERE id = NEW.item_id) IS NULL;
-- END%

CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fkdc_appointment_item_id
BEFORE DELETE ON mail_item
FOR EACH ROW BEGIN 
    DELETE FROM appointment WHERE item_id = OLD.id;
END%

-- -----------------------------------------------------------------------
-- deletion records for sync
-- -----------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.tombstone (
   sequence    INTEGER UNSIGNED NOT NULL,  -- change number for deletion
   date        INTEGER UNSIGNED NOT NULL,  -- deletion date as a UNIX-style timestamp
   type        TINYINT,                    -- 1 = folder, 3 = tag, etc.
   ids         TEXT
)%

CREATE INDEX IF NOT EXISTS ${DATABASE_NAME}.i_sequence ON tombstone(sequence)%

-- -----------------------------------------------------------------------
-- POP3 and IMAP sync
-- -----------------------------------------------------------------------

-- Tracks UID's of messages on remote POP3 servers
CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.pop3_message (
   data_source_id  CHAR(36) NOT NULL,
   uid             VARCHAR(255) NOT NULL,
   item_id         INTEGER UNSIGNED NOT NULL PRIMARY KEY,

   UNIQUE (uid, data_source_id)
)%

-- Tracks folders on remote IMAP servers
CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.imap_folder (
   item_id            INTEGER UNSIGNED NOT NULL PRIMARY KEY,
   data_source_id     CHAR(36) NOT NULL,
   local_path         VARCHAR(1000) NOT NULL,
   remote_path        VARCHAR(1000) NOT NULL,
   uid_validity       INTEGER UNSIGNED,

   UNIQUE (local_path, data_source_id),
   UNIQUE (remote_path, data_source_id)
)%

-- Tracks messages on remote IMAP servers
CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.imap_message (
   imap_folder_id  INTEGER UNSIGNED NOT NULL PRIMARY KEY,
   uid             BIGINT NOT NULL,
   item_id         INTEGER UNSIGNED NOT NULL,
   flags           INTEGER NOT NULL DEFAULT 0,

   UNIQUE (imap_folder_id, uid),
   CONSTRAINT fk_imap_message_imap_folder_id FOREIGN KEY (imap_folder_id) REFERENCES imap_folder(item_id) ON DELETE CASCADE
)%

-- CONSTRAINT fk_imap_message_imap_folder_id FOREIGN KEY (imap_folder_id) REFERENCES imap_folder(item_id) ON DELETE CASCADE
CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fki_imap_message_imap_folder_id
BEFORE INSERT ON [imap_message]
FOR EACH ROW BEGIN
  SELECT RAISE(ROLLBACK, 'insert on table "imap_message" violates foreign key constraint "fki_imap_message_imap_folder_id"')
  WHERE (SELECT id FROM imap_folder WHERE item_id = NEW.imap_folder_id) IS NULL;
END%

CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fku_imap_message_imap_folder_id
BEFORE UPDATE OF imap_folder_id ON [imap_message]
FOR EACH ROW BEGIN
    SELECT RAISE(ROLLBACK, 'update on table "imap_message" violates foreign key constraint "fku_imap_message_imap_folder_id"')
      WHERE (SELECT id FROM imap_folder WHERE item_id = NEW.imap_folder_id) IS NULL;
END%

CREATE TRIGGER IF NOT EXISTS ${DATABASE_NAME}.fkdc_imap_message_imap_folder_id
BEFORE DELETE ON imap_folder
FOR EACH ROW BEGIN 
    DELETE FROM imap_message WHERE imap_folder_id = OLD.item_id;
END%
