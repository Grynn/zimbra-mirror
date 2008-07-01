-- 
-- ***** BEGIN LICENSE BLOCK *****
-- 
-- Zimbra Collaboration Suite Server
-- Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
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

PRAGMA legacy_file_format = OFF;
PRAGMA encoding = "UTF-8";

-- -----------------------------------------------------------------------
-- volumes
-- -----------------------------------------------------------------------

-- list of known volumes
CREATE TABLE volume (
   id                     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
   type                   TINYINT NOT NULL,   -- 1 = primary msg, 2 = secondary msg, 10 = index
   name                   VARCHAR(255) NOT NULL UNIQUE,
   path                   TEXT NOT NULL UNIQUE,
   file_bits              SMALLINT NOT NULL,
   file_group_bits        SMALLINT NOT NULL,
   mailbox_bits           SMALLINT NOT NULL,
   mailbox_group_bits     SMALLINT NOT NULL,
   compress_blobs         BOOLEAN NOT NULL,
   compression_threshold  BIGINT NOT NULL
);


-- This table has only one row.  It points to message and index volumes
-- to use for newly provisioned mailboxes.
CREATE TABLE current_volumes (
   message_volume_id            INTEGER NOT NULL,
   secondary_message_volume_id  INTEGER,
   index_volume_id              INTEGER NOT NULL,
   next_mailbox_id              INTEGER NOT NULL,

   CONSTRAINT fk_current_volumes_message_volume_id FOREIGN KEY (message_volume_id) REFERENCES volume(id),
   CONSTRAINT fk_current_volumes_secondary_message_volume_id FOREIGN KEY (secondary_message_volume_id) REFERENCES volume(id),
   CONSTRAINT fk_current_volumes_index_volume_id FOREIGN KEY (index_volume_id) REFERENCES volume(id)
);

-- CREATE TRIGGER fki_current_volumes_volume_id
-- BEFORE INSERT ON [current_volumes]
-- FOR EACH ROW BEGIN
--   SELECT RAISE(ROLLBACK, 'insert on table "current_volumes" violates foreign key constraint "fki_current_volumes_volume_id"')
--   WHERE (SELECT id FROM volume WHERE id = NEW.message_volume_id) IS NULL OR
--         (SELECT id FROM volume WHERE id = NEW.secondary_message_volume_id OR NEW.secondary_message_volume_id IS NULL) IS NULL OR
--         (SELECT id FROM volume WHERE id = NEW.index_volume_id) IS NULL;
-- END;

-- CREATE TRIGGER fku_current_volumes_volume_id
-- BEFORE UPDATE ON [current_volumes] 
-- FOR EACH ROW BEGIN
--     SELECT RAISE(ROLLBACK, 'update on table "current_volumes" violates foreign key constraint "fku_current_volumes_volume_id"')
--       WHERE (SELECT id FROM volume WHERE id = NEW.message_volume_id) IS NULL OR
--             (SELECT id FROM volume WHERE id = NEW.secondary_message_volume_id OR NEW.secondary_message_volume_id IS NULL) IS NULL OR
--             (SELECT id FROM volume WHERE id = NEW.index_volume_id) IS NULL;
-- END;

-- CREATE TRIGGER fkd_current_volumes_volume_id
-- BEFORE DELETE ON volume
-- FOR EACH ROW BEGIN
--   SELECT RAISE(ROLLBACK, 'delete on table "volume" violates foreign key constraint "fkd_current_volumes_volume_id"')
--   WHERE (SELECT message_volume_id FROM current_volumes WHERE message_volume_id = OLD.id) IS NOT NULL OR
--         (SELECT secondary_message_volume_id FROM current_volumes WHERE secondary_message_volume_id = OLD.id) IS NOT NULL OR
--         (SELECT index_volume_id FROM current_volumes WHERE index_volume_id = OLD.id) IS NOT NULL;
-- END;

INSERT INTO volume (id, type, name, path, file_bits, file_group_bits,
    mailbox_bits, mailbox_group_bits, compress_blobs, compression_threshold)
  VALUES (1, 1, 'message1', '/opt/zimbra/store', 12, 8, 12, 8, 0, 4096);
INSERT INTO volume (id, type, name, path, file_bits, file_group_bits,
    mailbox_bits, mailbox_group_bits, compress_blobs, compression_threshold)
  VALUES (2, 10, 'index1', '/opt/zimbra/index', 12, 8, 12, 8, 0, 4096);

INSERT INTO current_volumes (message_volume_id, index_volume_id, next_mailbox_id) VALUES (1, 2, 1);


-- -----------------------------------------------------------------------
-- mailbox info
-- -----------------------------------------------------------------------

CREATE TABLE mailbox (
   id                  INTEGER UNSIGNED NOT NULL PRIMARY KEY,
   group_id            INTEGER UNSIGNED NOT NULL,     -- mailbox group
   account_id          VARCHAR(127) NOT NULL UNIQUE,  -- e.g. "d94e42c4-1636-11d9-b904-4dd689d02402"
   index_volume_id     INTEGER NOT NULL,
   item_id_checkpoint  INTEGER UNSIGNED NOT NULL DEFAULT 0,
   contact_count       INTEGER UNSIGNED DEFAULT 0,
   size_checkpoint     BIGINT UNSIGNED NOT NULL DEFAULT 0,
   change_checkpoint   INTEGER UNSIGNED NOT NULL DEFAULT 0,
   tracking_sync       INTEGER UNSIGNED NOT NULL DEFAULT 0,
   tracking_imap       BOOLEAN NOT NULL DEFAULT 0,
   last_backup_at      INTEGER UNSIGNED,              -- last full backup time, UNIX-style timestamp
   comment             VARCHAR(255),                  -- usually the main email address originally associated with the mailbox
   last_soap_access    INTEGER UNSIGNED NOT NULL DEFAULT 0,
   new_messages        INTEGER UNSIGNED NOT NULL DEFAULT 0,
   idx_deferred_count  INTEGER UNSIGNED NOT NULL DEFAULT 0,

   CONSTRAINT fk_mailbox_index_volume_id FOREIGN KEY (index_volume_id) REFERENCES volume(id)
);

CREATE INDEX i_mailbox_index_volume_id ON mailbox(index_volume_id);
CREATE INDEX i_mailbox_last_backup_at ON mailbox(last_backup_at, id);

-- CREATE TRIGGER fki_mailbox_index_volume_id
-- BEFORE INSERT ON [mailbox]
-- FOR EACH ROW BEGIN
--   SELECT RAISE(ROLLBACK, 'insert on table "mailbox" violates foreign key constraint "fki_mailbox_index_volume_id"')
--   WHERE (SELECT id FROM volume WHERE id = NEW.index_volume_id) IS NULL;
-- END;

-- CREATE TRIGGER fku_mailbox_index_volume_id
-- BEFORE UPDATE OF index_volume_id ON [mailbox] 
-- FOR EACH ROW BEGIN
--     SELECT RAISE(ROLLBACK, 'update on table "mailbox" violates foreign key constraint "fku_mailbox_index_volume_id"')
--       WHERE (SELECT id FROM volume WHERE id = NEW.index_volume_id) IS NULL;
-- END;

-- CREATE TRIGGER fkd_mailbox_index_volume_id
-- BEFORE DELETE ON volume
-- FOR EACH ROW BEGIN
--   SELECT RAISE(ROLLBACK, 'delete on table "volume" violates foreign key constraint "fkd_mailbox_index_volume_id"')
--   WHERE (SELECT index_volume_id FROM mailbox WHERE index_volume_id = OLD.id) IS NOT NULL;
-- END;

-- -----------------------------------------------------------------------
-- deleted accounts
-- -----------------------------------------------------------------------

CREATE TABLE deleted_account (
   email       VARCHAR(255) NOT NULL PRIMARY KEY,
   account_id  VARCHAR(127) NOT NULL,
   mailbox_id  INTEGER UNSIGNED NOT NULL,
   deleted_at  INTEGER UNSIGNED NOT NULL      -- UNIX-style timestamp
);

-- -----------------------------------------------------------------------
-- mailbox metadata info
-- -----------------------------------------------------------------------

CREATE TABLE mailbox_metadata (
   mailbox_id  INTEGER UNSIGNED NOT NULL,
   section     VARCHAR(64) NOT NULL,       -- e.g. "imap"
   metadata    MEDIUMTEXT,

   PRIMARY KEY (mailbox_id, section),
   CONSTRAINT fk_metadata_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE
);

-- CREATE TRIGGER fki_metadata_mailbox_id
-- BEFORE INSERT ON [mailbox_metadata]
-- FOR EACH ROW BEGIN
--   SELECT RAISE(ROLLBACK, 'insert on table "mailbox_metadata" violates foreign key constraint "fki_metadata_mailbox_id')
--   WHERE (SELECT id FROM mailbox WHERE id = NEW.mailbox_id) IS NULL;
-- END;

-- CREATE TRIGGER fku_metadata_mailbox_id
-- BEFORE UPDATE OF mailbox_id ON [mailbox_metadata] 
-- FOR EACH ROW BEGIN
--     SELECT RAISE(ROLLBACK, 'update on table "mailbox_metadata" violates foreign key constraint "fku_metadata_mailbox_id')
--       WHERE (SELECT id FROM mailbox WHERE id = NEW.mailbox_id) IS NULL;
-- END;

CREATE TRIGGER fkdc_metadata_mailbox_id
BEFORE DELETE ON mailbox
FOR EACH ROW BEGIN 
    DELETE FROM mailbox_metadata WHERE mailbox_metadata.mailbox_id = OLD.id;
END;


-- -----------------------------------------------------------------------
-- out-of-office reply history
-- -----------------------------------------------------------------------

CREATE TABLE out_of_office (
   mailbox_id  INTEGER UNSIGNED NOT NULL,
   sent_to     VARCHAR(255) NOT NULL,
   sent_on     DATETIME NOT NULL,

   PRIMARY KEY (mailbox_id, sent_to),
   CONSTRAINT fk_out_of_office_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE
);

CREATE INDEX i_out_of_office_sent_on ON out_of_office(sent_on);

-- -- CONSTRAINT fk_out_of_office_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE
-- CREATE TRIGGER fki_out_of_office_mailbox_id
-- BEFORE INSERT ON [out_of_office]
-- FOR EACH ROW BEGIN
--   SELECT RAISE(ROLLBACK, 'insert on table "out_of_office" violates foreign key constraint "fki_out_of_office_mailbox_id"')
--   WHERE (SELECT id FROM mailbox WHERE id = NEW.mailbox_id) IS NULL;
-- END;

-- CREATE TRIGGER fku_out_of_office_mailbox_id
-- BEFORE UPDATE OF mailbox_id ON [out_of_office] 
-- FOR EACH ROW BEGIN
--     SELECT RAISE(ROLLBACK, 'update on table "out_of_office" violates foreign key constraint "fku_out_of_office_mailbox_id"')
--       WHERE (SELECT id FROM mailbox WHERE id = NEW.mailbox_id) IS NULL;
-- END;

CREATE TRIGGER fkdc_out_of_office_mailbox_id
BEFORE DELETE ON mailbox
FOR EACH ROW BEGIN 
    DELETE FROM out_of_office WHERE out_of_office.mailbox_id = OLD.id;
END;

-- -----------------------------------------------------------------------
-- etc.
-- -----------------------------------------------------------------------

-- table for global config params
CREATE TABLE config (
   name         VARCHAR(255) NOT NULL PRIMARY KEY,
   value        TEXT,
   description  TEXT,
   modified     TIMESTAMP
);

-- table for tracking database table maintenance
CREATE TABLE table_maintenance (
   database_name       VARCHAR(64) NOT NULL,
   table_name          VARCHAR(64) NOT NULL,
   maintenance_date    DATETIME NOT NULL,
   last_optimize_date  DATETIME,
   num_rows            INTEGER UNSIGNED NOT NULL,
  
   PRIMARY KEY (table_name, database_name)
);

CREATE TABLE service_status (
   server   VARCHAR(255) NOT NULL,
   service  VARCHAR(255) NOT NULL,
   time     DATETIME,
   status   BOOL,
  
   UNIQUE (server, service)
);

-- Tracks scheduled tasks
CREATE TABLE scheduled_task (
   class_name       VARCHAR(255) NOT NULL,
   name             VARCHAR(255) NOT NULL,
   mailbox_id       INTEGER UNSIGNED NOT NULL,
   exec_time        DATETIME,
   interval_millis  INTEGER UNSIGNED,
   metadata         MEDIUMTEXT,

   PRIMARY KEY (name, mailbox_id, class_name),
   CONSTRAINT fk_st_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE
);

CREATE INDEX i_scheduled_task_mailbox_id ON scheduled_task(mailbox_id);

-- CREATE TRIGGER fki_scheduled_task_mailbox_id
-- BEFORE INSERT ON [scheduled_task]
-- FOR EACH ROW BEGIN
--   SELECT RAISE(ROLLBACK, 'insert on table "scheduled_task" violates foreign key constraint "fki_scheduled_task_mailbox_id"')
--   WHERE (SELECT id FROM mailbox WHERE id = NEW.mailbox_id) IS NULL;
-- END;

-- CREATE TRIGGER fku_scheduled_task_mailbox_id
-- BEFORE UPDATE OF mailbox_id ON [scheduled_task] 
-- FOR EACH ROW BEGIN
--     SELECT RAISE(ROLLBACK, 'update on table "scheduled_task" violates foreign key constraint "fku_scheduled_task_mailbox_id"')
--       WHERE (SELECT id FROM mailbox WHERE id = NEW.mailbox_id) IS NULL;
-- END;

CREATE TRIGGER fkdc_scheduled_task_mailbox_id
BEFORE DELETE ON mailbox
FOR EACH ROW BEGIN 
    DELETE FROM scheduled_task WHERE scheduled_task.mailbox_id = OLD.id;
END;


-- -----------------------------------------------------------------------
-- IM tables (Wildfire code)
-- -----------------------------------------------------------------------
CREATE TABLE jiveUserProp (
  username              VARCHAR(200)    NOT NULL,
  name                  VARCHAR(100)    NOT NULL,
  propValue             TEXT            NOT NULL,
  PRIMARY KEY (username, name)
);

CREATE TABLE jiveGroupProp (
  groupName             VARCHAR(50)     NOT NULL,
  name                  VARCHAR(100)    NOT NULL,
  propValue             TEXT            NOT NULL,
  PRIMARY KEY (groupName, name)
);

CREATE TABLE jiveGroupUser (
  groupName             VARCHAR(50)     NOT NULL,
  username              VARCHAR(200)    NOT NULL,
  administrator         TINYINT         NOT NULL,
  PRIMARY KEY (groupName, username, administrator)
);

CREATE TABLE jivePrivate (
  username              VARCHAR(200)    NOT NULL,
  name                  VARCHAR(100)    NOT NULL,
  namespace             VARCHAR(200)    NOT NULL,
  value                 TEXT            NOT NULL,
  PRIMARY KEY (username, name, namespace)
);

CREATE TABLE jiveOffline (
  username              VARCHAR(200)    NOT NULL,
  messageID             BIGINT          NOT NULL,
  creationDate          CHAR(15)        NOT NULL,
  messageSize           INTEGER         NOT NULL,
  message               TEXT            NOT NULL,
  PRIMARY KEY (username, messageID)
);

CREATE TABLE jiveRoster (
  rosterID              BIGINT          NOT NULL,
  username              VARCHAR(200)    NOT NULL,
  jid                   TEXT            NOT NULL,
  sub                   TINYINT         NOT NULL,
  ask                   TINYINT         NOT NULL,
  recv                  TINYINT         NOT NULL,
  nick                  VARCHAR(200),
  PRIMARY KEY (rosterID)
);
CREATE INDEX jiveRoster_unameid_idx ON jiveRoster(username);

CREATE TABLE jiveRosterGroups (
  rosterID              BIGINT          NOT NULL,
  rank                  TINYINT         NOT NULL,
  groupName             VARCHAR(200)    NOT NULL,
  PRIMARY KEY (rosterID, rank)
);
CREATE INDEX jiveRosterGroup_rosterid_idx ON jiveRosterGroups(rosterID);

CREATE TABLE jiveVCard (
  username              VARCHAR(200)    NOT NULL,
  value                 TEXT            NOT NULL,
  PRIMARY KEY (username)
);

CREATE TABLE jiveID (
  idType                INTEGER         NOT NULL,
  id                    BIGINT          NOT NULL,
  PRIMARY KEY (idType)
);

CREATE TABLE jiveProperty (
  name                  VARCHAR(100)    NOT NULL,
  propValue             TEXT            NOT NULL,
  PRIMARY KEY (name)
);


CREATE TABLE jiveVersion (
  name                  VARCHAR(50)     NOT NULL,
  version               INTEGER         NOT NULL,
  PRIMARY KEY (name)
);

CREATE TABLE jiveExtComponentConf (
  subdomain             VARCHAR(200)    NOT NULL,
  secret                VARCHAR(200),
  permission            VARCHAR(10)     NOT NULL,
  PRIMARY KEY (subdomain)
);

CREATE TABLE jiveRemoteServerConf (
  domain                VARCHAR(200)    NOT NULL,
  remotePort            INTEGER,
  permission            VARCHAR(10)     NOT NULL,
  PRIMARY KEY (domain)
);

CREATE TABLE jivePrivacyList (
  username              VARCHAR(200)    NOT NULL,
  name                  VARCHAR(100)    NOT NULL,
  isDefault             TINYINT         NOT NULL,
  list                  TEXT            NOT NULL,
  PRIMARY KEY (username, name)
);
CREATE INDEX jivePList_default_idx ON jivePrivacyList(username, isDefault);

CREATE TABLE jiveSASLAuthorized (
  username              VARCHAR(200)    NOT NULL,
  principal             TEXT            NOT NULL,
  PRIMARY KEY (username, principal)
);

CREATE TABLE mucRoom (
  roomID                BIGINT          NOT NULL,
  creationDate          CHAR(15)        NOT NULL,
  modificationDate      CHAR(15)        NOT NULL,
  name                  VARCHAR(50)     NOT NULL,
  naturalName           VARCHAR(255)    NOT NULL,
  description           VARCHAR(255),
  lockedDate            CHAR(15)        NOT NULL,
  emptyDate             CHAR(15)        NULL,
  canChangeSubject      TINYINT         NOT NULL,
  maxUsers              INTEGER         NOT NULL,
  publicRoom            TINYINT         NOT NULL,
  moderated             TINYINT         NOT NULL,
  membersOnly           TINYINT         NOT NULL,
  canInvite             TINYINT         NOT NULL,
  password              VARCHAR(50)     NULL,
  canDiscoverJID        TINYINT         NOT NULL,
  logEnabled            TINYINT         NOT NULL,
  subject               VARCHAR(100)    NULL,
  rolesToBroadcast      TINYINT         NOT NULL,
  useReservedNick       TINYINT         NOT NULL,
  canChangeNick         TINYINT         NOT NULL,
  canRegister           TINYINT         NOT NULL,
  PRIMARY KEY (name)
);
CREATE INDEX mucRoom_roomid_idx ON mucRoom(roomID);

CREATE TABLE mucRoomProp (
  roomID                BIGINT          NOT NULL,
  name                  VARCHAR(100)    NOT NULL,
  propValue             TEXT            NOT NULL,
  PRIMARY KEY (roomID, name)
);

CREATE TABLE mucAffiliation (
  roomID                BIGINT          NOT NULL,
  jid                   TEXT            NOT NULL,
  affiliation           TINYINT         NOT NULL,
  PRIMARY KEY (roomID, jid)
);

CREATE TABLE mucMember (
  roomID                BIGINT          NOT NULL,
  jid                   TEXT            NOT NULL,
  nickname              VARCHAR(255)    NULL,
  firstName             VARCHAR(100)    NULL,
  lastName              VARCHAR(100)    NULL,
  url                   VARCHAR(100)    NULL,
  email                 VARCHAR(100)    NULL,
  faqentry              VARCHAR(100)    NULL,
  PRIMARY KEY (roomID, jid)
);

CREATE TABLE mucConversationLog (
  roomID                BIGINT          NOT NULL,
  sender                TEXT            NOT NULL,
  nickname              VARCHAR(255)    NULL,
  time                  CHAR(15)        NOT NULL,
  subject               VARCHAR(255)    NULL,
  body                  TEXT            NULL
);
CREATE INDEX mucLog_time_idx ON mucConversationLog(time);

-- Finally, insert default table values.
INSERT INTO jiveID (idType, id) VALUES (18, 1);
INSERT INTO jiveID (idType, id) VALUES (19, 1);
INSERT INTO jiveID (idType, id) VALUES (23, 1);
INSERT INTO jiveVersion (name, version) VALUES ('wildfire', 10);
