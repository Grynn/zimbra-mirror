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

PRAGMA default_cache_size = 2000;
PRAGMA encoding = "UTF-8";
PRAGMA legacy_file_format = OFF;

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

-- -----------------------------------------------------------------------
-- mailbox info
-- -----------------------------------------------------------------------

CREATE TABLE mailbox (
   id                  BIGINT UNSIGNED NOT NULL PRIMARY KEY,
   account_id          VARCHAR(127) NOT NULL UNIQUE,  -- e.g. "d94e42c4-1636-11d9-b904-4dd689d02402"
   last_backup_at      INTEGER UNSIGNED,              -- last full backup time, UNIX-style timestamp
   comment             VARCHAR(255)                   -- usually the main email address originally associated with the mailbox
);

CREATE INDEX i_mailbox_last_backup_at ON mailbox(last_backup_at, id);

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
-- etc.
-- -----------------------------------------------------------------------

-- table for global config params
CREATE TABLE config (
   name         VARCHAR(255) NOT NULL PRIMARY KEY,
   value        TEXT,
   description  TEXT,
   modified     TIMESTAMP DEFAULT (DATETIME('NOW'))
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

-- Mobile Devices
CREATE TABLE mobile_devices (
   mailbox_id          BIGINT UNSIGNED NOT NULL,
   device_id           VARCHAR(64) NOT NULL,
   device_type         VARCHAR(64) NOT NULL,
   user_agent          VARCHAR(64),
   protocol_version    VARCHAR(64),
   provisionable       BOOLEAN NOT NULL DEFAULT 0,
   status              TINYINT UNSIGNED NOT NULL DEFAULT 0,
   policy_key          INTEGER UNSIGNED,
   recovery_password   VARCHAR(64),
   first_req_received  INTEGER UNSIGNED NOT NULL,
   last_policy_update  INTEGER UNSIGNED,
   remote_wipe_req     INTEGER UNSIGNED,
   remote_wipe_ack     INTEGER UNSIGNED,
   policy_values       VARCHAR(512),

   PRIMARY KEY (mailbox_id, device_id),
   CONSTRAINT fk_mobile_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE
);
