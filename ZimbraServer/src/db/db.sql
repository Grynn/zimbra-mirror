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
CREATE DATABASE zimbra;
ALTER DATABASE zimbra DEFAULT CHARACTER SET utf8;

USE zimbra;

GRANT ALL ON zimbra.* TO 'zimbra' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra.* TO 'zimbra'@'localhost' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra.* TO 'zimbra'@'localhost.localdomain' IDENTIFIED BY 'zimbra';
--GRANT ALL ON zimbra.* TO 'root'@'localhost.localdomain' IDENTIFIED BY 'zimbra';

-- The zimbra user needs to be able to create and drop databases and perform
-- backup and restore operations.  Give
-- zimbra root access for now to keep things simple until there is a need
-- to add more security.
GRANT ALL ON *.* TO 'zimbra' WITH GRANT OPTION;
GRANT ALL ON *.* TO 'zimbra'@'localhost' WITH GRANT OPTION;
GRANT ALL ON *.* TO 'zimbra'@'localhost.localdomain' WITH GRANT OPTION;
GRANT ALL ON *.* TO 'root'@'localhost.localdomain' WITH GRANT OPTION;
 
-- -----------------------------------------------------------------------
-- volumes
-- -----------------------------------------------------------------------

-- list of known volumes
CREATE TABLE volume (
   id                    TINYINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   type                  TINYINT NOT NULL,   -- 1 = primary msg, 2 = secondary msg, 10 = index
   name                  VARCHAR(255) NOT NULL,
   path                  TEXT NOT NULL,
   file_bits             SMALLINT NOT NULL,
   file_group_bits       SMALLINT NOT NULL,
   mailbox_bits          SMALLINT NOT NULL,
   mailbox_group_bits    SMALLINT NOT NULL,
   compress_blobs        BOOLEAN NOT NULL,
   compression_threshold BIGINT NOT NULL,

   UNIQUE INDEX i_name (name),
   UNIQUE INDEX i_path (path(255))   -- Index prefix length of 255 is the max prior to MySQL 4.1.2.  Should be good enough.
) ENGINE = InnoDB;

-- This table has only one row.  It points to message and index volumes
-- to use for newly provisioned mailboxes.
CREATE TABLE current_volumes (
   message_volume_id           TINYINT UNSIGNED NOT NULL,
   secondary_message_volume_id TINYINT UNSIGNED,
   index_volume_id             TINYINT UNSIGNED NOT NULL,
   next_mailbox_id             INTEGER UNSIGNED NOT NULL,

   INDEX i_message_volume_id (message_volume_id),
   INDEX i_secondary_message_volume_id (secondary_message_volume_id),
   INDEX i_index_volume_id (index_volume_id),

   CONSTRAINT fk_current_volumes_message_volume_id
              FOREIGN KEY (message_volume_id)
              REFERENCES volume(id),
   CONSTRAINT fk_current_volumes_secondary_message_volume_id
              FOREIGN KEY (secondary_message_volume_id)
              REFERENCES volume(id),
   CONSTRAINT fk_current_volumes_index_volume_id
              FOREIGN KEY (index_volume_id)
              REFERENCES volume(id)
) ENGINE = InnoDB;

INSERT INTO volume (id, type, name, path, file_bits, file_group_bits,
    mailbox_bits, mailbox_group_bits, compress_blobs, compression_threshold)
  VALUES (1, 1, 'message1', '/opt/zimbra/store', 12, 8, 12, 8, 0, 4096);
INSERT INTO volume (id, type, name, path, file_bits, file_group_bits,
    mailbox_bits, mailbox_group_bits, compress_blobs, compression_threshold)
  VALUES (2, 10, 'index1',   '/opt/zimbra/index', 12, 8, 12, 8, 0, 4096);

INSERT INTO current_volumes (message_volume_id, index_volume_id, next_mailbox_id) VALUES (1, 2, 1);
COMMIT;


-- -----------------------------------------------------------------------
-- mailbox info
-- -----------------------------------------------------------------------

CREATE TABLE mailbox (
   id                 INTEGER UNSIGNED NOT NULL PRIMARY KEY,
   group_id           INTEGER UNSIGNED NOT NULL,  -- mailbox group
   account_id         VARCHAR(127) NOT NULL,          -- e.g. "d94e42c4-1636-11d9-b904-4dd689d02402"
   index_volume_id    TINYINT UNSIGNED NOT NULL,
   item_id_checkpoint INTEGER UNSIGNED NOT NULL DEFAULT 0,
   contact_count      INTEGER UNSIGNED DEFAULT 0,
   size_checkpoint    BIGINT UNSIGNED NOT NULL DEFAULT 0,
   change_checkpoint  INTEGER UNSIGNED NOT NULL DEFAULT 0,
   tracking_sync      INTEGER UNSIGNED NOT NULL DEFAULT 0,
   tracking_imap      BOOLEAN NOT NULL DEFAULT 0,
   last_backup_at     INTEGER UNSIGNED,           -- last full backup time, UNIX-style timestamp
   comment            VARCHAR(255),               -- usually the main email address originally associated with the mailbox
   last_soap_access   INTEGER UNSIGNED NOT NULL DEFAULT 0,
   new_messages       INTEGER UNSIGNED NOT NULL DEFAULT 0,
   idx_deferred_count INTEGER UNSIGNED NOT NULL DEFAULT 0,

   UNIQUE INDEX i_account_id (account_id),
   INDEX i_index_volume_id (index_volume_id),
   INDEX i_last_backup_at (last_backup_at, id),

   CONSTRAINT fk_mailbox_index_volume_id FOREIGN KEY (index_volume_id) REFERENCES volume(id)
) ENGINE = InnoDB;

-- -----------------------------------------------------------------------
-- deleted accounts
-- -----------------------------------------------------------------------

CREATE TABLE deleted_account (
    email VARCHAR(255) NOT NULL PRIMARY KEY,
    account_id VARCHAR(127) NOT NULL,
    mailbox_id INTEGER UNSIGNED NOT NULL,
    deleted_at INTEGER UNSIGNED NOT NULL      -- UNIX-style timestamp
) ENGINE = InnoDB;

-- -----------------------------------------------------------------------
-- mailbox metadata info
-- -----------------------------------------------------------------------

CREATE TABLE mailbox_metadata (
   mailbox_id  INTEGER UNSIGNED NOT NULL,
   section     VARCHAR(64) NOT NULL,       -- e.g. "imap"
   metadata    MEDIUMTEXT,

   PRIMARY KEY (mailbox_id, section),

   CONSTRAINT fk_metadata_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE
) ENGINE = InnoDB;

-- -----------------------------------------------------------------------
-- out-of-office reply history
-- -----------------------------------------------------------------------

CREATE TABLE out_of_office (
  mailbox_id  INTEGER UNSIGNED NOT NULL,
  sent_to     VARCHAR(255) NOT NULL,
  sent_on     DATETIME NOT NULL,

  PRIMARY KEY (mailbox_id, sent_to),
  INDEX i_sent_on (sent_on),

  CONSTRAINT fk_out_of_office_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE
) ENGINE = InnoDB;

-- -----------------------------------------------------------------------
-- etc.
-- -----------------------------------------------------------------------

-- table for global config params
CREATE TABLE config (
  name        VARCHAR(255) NOT NULL PRIMARY KEY,
  value       TEXT,
  description TEXT,
  modified    TIMESTAMP
) ENGINE = InnoDB;

-- table for tracking database table maintenance
CREATE TABLE table_maintenance (
  database_name      VARCHAR(64) NOT NULL,
  table_name         VARCHAR(64) NOT NULL,
  maintenance_date   DATETIME NOT NULL,
  last_optimize_date DATETIME,
  num_rows           INTEGER UNSIGNED NOT NULL,
  
  PRIMARY KEY (table_name, database_name)
) ENGINE = InnoDB;

CREATE TABLE service_status (
  server      VARCHAR(255) NOT NULL,
  service     VARCHAR(255) NOT NULL,
  time        DATETIME,
  status      BOOL,
  
  UNIQUE INDEX i_server_service (server(100), service(100))
) ENGINE = MyISAM;

-- -----------------------------------------------------------------------
-- IM tables (Wildfire code)
-- -----------------------------------------------------------------------
CREATE TABLE jiveUserProp (
  username           VARCHAR(200)     NOT NULL,
  name               VARCHAR(100)    NOT NULL,
  propValue          TEXT            NOT NULL,
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
  username               VARCHAR(200)    NOT NULL,
  administrator         TINYINT         NOT NULL,
  PRIMARY KEY (groupName, username, administrator)
);

CREATE TABLE jivePrivate (
  username               VARCHAR(200)     NOT NULL,
  name                  VARCHAR(100)    NOT NULL,
  namespace             VARCHAR(200)    NOT NULL,
  value                 TEXT            NOT NULL,
  PRIMARY KEY (username, name, namespace(20))
);

CREATE TABLE jiveOffline (
  username               VARCHAR(200)     NOT NULL,
  messageID             BIGINT          NOT NULL,
  creationDate          CHAR(15)        NOT NULL,
  messageSize           INTEGER         NOT NULL,
  message               TEXT            NOT NULL,
  PRIMARY KEY (username, messageID)
);

CREATE TABLE jiveRoster (
  rosterID              BIGINT          NOT NULL,
  username               VARCHAR(200)    NOT NULL,
  jid                   TEXT            NOT NULL,
  sub                   TINYINT         NOT NULL,
  ask                   TINYINT         NOT NULL,
  recv                  TINYINT         NOT NULL,
  nick                  VARCHAR(200),
  PRIMARY KEY (rosterID),
  INDEX jiveRoster_unameid_idx (username)
);

CREATE TABLE jiveRosterGroups (
  rosterID              BIGINT          NOT NULL,
  rank                  TINYINT         NOT NULL,
  groupName             VARCHAR(200)    NOT NULL,
  PRIMARY KEY (rosterID, rank),
  INDEX jiveRosterGroup_rosterid_idx (rosterID)
);

CREATE TABLE jiveVCard (
  username              VARCHAR(200)     NOT NULL,
  value                 TEXT            NOT NULL,
  PRIMARY KEY (username)
);

CREATE TABLE jiveID (
  idType                INTEGER         NOT NULL,
  id                    BIGINT          NOT NULL,
  PRIMARY KEY (idType)
);

CREATE TABLE jiveProperty (
  name        VARCHAR(100)              NOT NULL,
  propValue   TEXT                      NOT NULL,
  PRIMARY KEY (name)
);


CREATE TABLE jiveVersion (
  name     VARCHAR(50)  NOT NULL,
  version  INTEGER  NOT NULL,
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
  username               VARCHAR(200)     NOT NULL,
  name                  VARCHAR(100)    NOT NULL,
  isDefault             TINYINT         NOT NULL,
  list                  TEXT            NOT NULL,
  PRIMARY KEY (username, name),
  INDEX jivePList_default_idx (username, isDefault)
);

CREATE TABLE jiveSASLAuthorized (
  username             VARCHAR(200)   NOT NULL,
  principal           TEXT          NOT NULL,
  PRIMARY KEY (username, principal(100))
);

CREATE TABLE mucRoom (
  roomID              BIGINT        NOT NULL,
  creationDate        CHAR(15)      NOT NULL,
  modificationDate    CHAR(15)      NOT NULL,
  name                VARCHAR(50)   NOT NULL,
  naturalName         VARCHAR(255)  NOT NULL,
  description         VARCHAR(255),
  lockedDate          CHAR(15)      NOT NULL,
  emptyDate           CHAR(15)      NULL,
  canChangeSubject    TINYINT       NOT NULL,
  maxUsers            INTEGER       NOT NULL,
  publicRoom          TINYINT       NOT NULL,
  moderated           TINYINT       NOT NULL,
  membersOnly         TINYINT       NOT NULL,
  canInvite           TINYINT       NOT NULL,
  password            VARCHAR(50)   NULL,
  canDiscoverJID      TINYINT       NOT NULL,
  logEnabled          TINYINT       NOT NULL,
  subject             VARCHAR(100)  NULL,
  rolesToBroadcast    TINYINT       NOT NULL,
  useReservedNick     TINYINT       NOT NULL,
  canChangeNick       TINYINT       NOT NULL,
  canRegister         TINYINT       NOT NULL,
  PRIMARY KEY (name),
  INDEX mucRoom_roomid_idx (roomID)
);

CREATE TABLE mucRoomProp (
  roomID                BIGINT          NOT NULL,
  name                  VARCHAR(100)    NOT NULL,
  propValue             TEXT            NOT NULL,
  PRIMARY KEY (roomID, name)
);

CREATE TABLE mucAffiliation (
  roomID              BIGINT        NOT NULL,
  jid                 TEXT          NOT NULL,
  affiliation         TINYINT       NOT NULL,
  PRIMARY KEY (roomID,jid(70))
);

CREATE TABLE mucMember (
  roomID              BIGINT        NOT NULL,
  jid                 TEXT          NOT NULL,
  nickname            VARCHAR(255)  NULL,
  firstName           VARCHAR(100)  NULL,
  lastName            VARCHAR(100)  NULL,
  url                 VARCHAR(100)  NULL,
  email               VARCHAR(100)  NULL,
  faqentry            VARCHAR(100)  NULL,
  PRIMARY KEY (roomID,jid(70))
);

CREATE TABLE mucConversationLog (
  roomID              BIGINT        NOT NULL,
  sender              TEXT          NOT NULL,
  nickname            VARCHAR(255)  NULL,
  time                CHAR(15)      NOT NULL,
  subject             VARCHAR(255)  NULL,
  body                TEXT          NULL,
  INDEX mucLog_time_idx (time)
);

-- Finally, insert default table values.
INSERT INTO jiveID (idType, id) VALUES (18, 1);
INSERT INTO jiveID (idType, id) VALUES (19, 1);
INSERT INTO jiveID (idType, id) VALUES (23, 1);
INSERT INTO jiveVersion (name, version) VALUES ('wildfire', 10);

-- Tracks scheduled tasks
CREATE TABLE scheduled_task (
   class_name      VARCHAR(255) BINARY NOT NULL,
   name            VARCHAR(255) NOT NULL,
   mailbox_id      INTEGER UNSIGNED NOT NULL,
   exec_time       DATETIME,
   interval_millis INTEGER UNSIGNED,
   metadata        MEDIUMTEXT,

   PRIMARY KEY (name, mailbox_id, class_name),
   CONSTRAINT fk_st_mailbox_id FOREIGN KEY (mailbox_id)
      REFERENCES mailbox(id) ON DELETE CASCADE,
   INDEX i_mailbox_id (mailbox_id)
) ENGINE = InnoDB;
