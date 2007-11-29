--
-- ***** BEGIN LICENSE BLOCK *****
--
-- Zimbra Collaboration Suite Server
-- Copyright (C) 2006, 2007 Zimbra, Inc.
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
GRANT ALL ON zimbra.* TO 'root'@'localhost.localdomain' IDENTIFIED BY 'zimbra';

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
   message_volume_id           TINYINT UNSIGNED NOT NULL PRIMARY KEY,
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
