# 
# ***** BEGIN LICENSE BLOCK *****
# Version: ZPL 1.1
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 
CREATE DATABASE zimbra;
ALTER DATABASE zimbra DEFAULT CHARACTER SET utf8;

USE zimbra;

GRANT ALL ON zimbra.* TO 'zimbra' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra.* TO 'zimbra'@'localhost' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra.* TO 'zimbra'@'localhost.localdomain' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra.* TO 'root'@'localhost.localdomain' IDENTIFIED BY 'zimbra';

# The zimbra user needs to be able to create and drop databases and perform
# backup and restore operations.  Give
# zimbra root access for now to keep things simple until there is a need
# to add more security.
GRANT ALL ON *.* TO 'zimbra' WITH GRANT OPTION;
GRANT ALL ON *.* TO 'zimbra'@'localhost' WITH GRANT OPTION;
GRANT ALL ON *.* TO 'zimbra'@'localhost.localdomain' WITH GRANT OPTION;
GRANT ALL ON *.* TO 'root'@'localhost.localdomain' WITH GRANT OPTION;
 
#-----------------------------------------------------------------------
# volumes
#-----------------------------------------------------------------------

# list of known volumes
CREATE TABLE volume (
   id                 INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name               VARCHAR(255) NOT NULL,
   path               TEXT NOT NULL,
   file_bits          SMALLINT NOT NULL,
   file_group_bits    SMALLINT NOT NULL,
   mailbox_bits       SMALLINT NOT NULL,
   mailbox_group_bits SMALLINT NOT NULL
) ENGINE = InnoDB;

# This table has only one row.  It points to message and index volumes
# to use for newly provisioned mailboxes.
CREATE TABLE current_volumes (
   message_volume_id  INTEGER UNSIGNED NOT NULL,
   index_volume_id    INTEGER UNSIGNED NOT NULL,
   next_mailbox_id    INTEGER UNSIGNED NOT NULL,

   INDEX i_message_volume_id (message_volume_id),
   INDEX i_index_volume_id (index_volume_id),

   CONSTRAINT fk_current_volumes_message_volume_id FOREIGN KEY (message_volume_id) REFERENCES volume(id),
   CONSTRAINT fk_current_volumes_index_volume_id FOREIGN KEY (index_volume_id)     REFERENCES volume(id)
) ENGINE = InnoDB;

INSERT INTO volume (id, name, path, file_bits, file_group_bits, mailbox_bits, mailbox_group_bits)
  VALUES (1, 'message1', '/opt/zimbra/store', 12, 8, 12, 8);
INSERT INTO volume (id, name, path, file_bits, file_group_bits, mailbox_bits, mailbox_group_bits)
  VALUES (2, 'index1',   '/opt/zimbra/index', 12, 8, 12, 8);

INSERT INTO current_volumes (message_volume_id, index_volume_id, next_mailbox_id) VALUES (1, 2, 1);
COMMIT;


#-----------------------------------------------------------------------
# mailbox info
#-----------------------------------------------------------------------

CREATE TABLE mailbox (
   id                 INTEGER UNSIGNED NOT NULL PRIMARY KEY,
   account_id         CHAR(36) NOT NULL,          # e.g. "d94e42c4-1636-11d9-b904-4dd689d02402"
   index_volume_id    INTEGER UNSIGNED NOT NULL,
   item_id_checkpoint INTEGER UNSIGNED NOT NULL DEFAULT 0,
   size_checkpoint    BIGINT UNSIGNED NOT NULL DEFAULT 0,
   change_checkpoint  INTEGER UNSIGNED NOT NULL DEFAULT 0,
   tracking_sync      BOOLEAN NOT NULL DEFAULT 0,
   config             TEXT,
   comment            VARCHAR(255),               # usually the main email address originally associated with the mailbox

   UNIQUE INDEX i_account_id (account_id),
   INDEX i_index_volume_id (index_volume_id),

   CONSTRAINT fk_mailbox_index_volume_id FOREIGN KEY (index_volume_id) REFERENCES volume(id)
) ENGINE = InnoDB;

#-----------------------------------------------------------------------
# etc.
#-----------------------------------------------------------------------

# table for global config params
CREATE TABLE config (
  name        VARCHAR(255) NOT NULL PRIMARY KEY,
  value       TEXT,
  description TEXT,
  modified    TIMESTAMP
) ENGINE = InnoDB;

# table for status
CREATE TABLE service_status (
  server      VARCHAR(255) NOT NULL,
  service     VARCHAR(255) NOT NULL,
  time        DATETIME,
  status      BOOL,
  
  UNIQUE INDEX i_server_service (server(100), service(100))
) ENGINE = MyISAM;

# table for statistics
CREATE TABLE server_stat (
  time        DATETIME NOT NULL,
  name        VARCHAR(255) NOT NULL,
  value       VARCHAR(255),
  INDEX i_name (name),
  INDEX i_time (time)
) ENGINE = MyISAM;

# table for aggregate statistics
CREATE TABLE aggregate_stat (
  time        DATETIME NOT NULL,
  name        VARCHAR(255) NOT NULL,
  value       VARCHAR(255),
  period      INTEGER,
  PRIMARY KEY (time, name, period)
) ENGINE = MyISAM;

# table for tracking out-of-office replies
CREATE TABLE out_of_office (
  mailbox_id  INTEGER UNSIGNED NOT NULL,
  sent_to     VARCHAR(255) NOT NULL,
  sent_on     DATETIME NOT NULL,

  PRIMARY KEY (mailbox_id, sent_to),
  INDEX i_sent_on (sent_on),

  CONSTRAINT fk_out_of_office_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE
) ENGINE = InnoDB;

# table for tracking database table maintenance
CREATE TABLE table_maintenance (
  database_name      VARCHAR(64) NOT NULL,
  table_name         VARCHAR(64) NOT NULL,
  maintenance_date   DATETIME NOT NULL,
  last_optimize_date DATETIME,
  num_rows           INTEGER UNSIGNED NOT NULL,
  
  PRIMARY KEY (table_name, database_name)
) ENGINE = InnoDB;

# table for the current redo log file sequence number
CREATE TABLE redolog_sequence (
  sequence			BIGINT UNSIGNED NOT NULL DEFAULT 0,
  
  PRIMARY KEY (sequence)
) ENGINE = InnoDB;

INSERT INTO redolog_sequence(sequence) VALUES (0);
  
#------------------------------------------------------------
# config
#------------------------------------------------------------
INSERT INTO config(name, value, description) VALUES
  ('common.zimbraHome', '/opt/zimbra', 
    'install root'),

  ('store.compressBlobs', 'false',
    'whether or not to compress blobs'),

  ('indexing.mailboxIndexWriter.maxUncommittedOps', '200',
    'maximum number of uncommitted indexing operations that may accumulate per mailbox before forcing a commit'),
  ('indexing.mailboxIndexWriter.LRUSize', '100',
    'maximum number of open mailbox index writers in the LRU map'),
  ('indexing.mailboxIndexWriter.idleFlushTimeSec', '600',
    'flush uncommitted indexing ops in mailbox if idle for longer than this value')
;
