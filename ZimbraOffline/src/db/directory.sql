-- 
-- ***** BEGIN LICENSE BLOCK *****
-- Version: MPL 1.1
-- 
-- The contents of this file are subject to the Mozilla Public License
-- Version 1.1 ("License"). You may not use this file except in
-- compliance with the License. You may obtain a copy of the License at
-- http://www.zimbra.com/license
-- 
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
-- the License for the specific language governing rights and limitations
-- under the License.
-- 
-- The Original Code is: Zimbra Collaboration Suite Server.
-- 
-- The Initial Developer of the Original Code is Zimbra, Inc.
-- Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
-- All Rights Reserved.
-- 
-- Contributor(s):
-- 
-- ***** END LICENSE BLOCK *****
-- 

USE zimbra;


-- -----------------------------------------------------------------------
-- directory
-- -----------------------------------------------------------------------

CREATE TABLE directory (
   entry_id    INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   entry_type  CHAR(4) NOT NULL,
   entry_name  VARCHAR(128) NOT NULL,
   zimbra_id   CHAR(36),
   modified    BOOLEAN NOT NULL,

   UNIQUE INDEX i_zimbra_id (zimbra_id),
   UNIQUE INDEX i_entry_type_name (entry_type, entry_name)
) ENGINE = InnoDB;

CREATE TABLE directory_attrs (
   entry_id    INTEGER UNSIGNED NOT NULL,
   name        VARCHAR(255) NOT NULL,
   value       TEXT NOT NULL,

   INDEX i_entry_id_name (entry_id, name),
   INDEX i_name (name),

   CONSTRAINT fk_directory_entry_id FOREIGN KEY (entry_id) REFERENCES directory(entry_id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE directory_leaf (
   entry_id    INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   parent_id   INTEGER UNSIGNED NOT NULL,
   entry_type  CHAR(4) NOT NULL,
   entry_name  VARCHAR(128) NOT NULL,
   zimbra_id   CHAR(73) NOT NULL,

   UNIQUE INDEX i_zimbra_id (zimbra_id),
   UNIQUE INDEX i_parent_entry_type_name (parent_id, entry_type, entry_name),

   CONSTRAINT fk_dleaf_entry_id FOREIGN KEY (parent_id) REFERENCES directory(entry_id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE directory_leaf_attrs (
   entry_id    INTEGER UNSIGNED NOT NULL,
   name        VARCHAR(255) NOT NULL,
   value       TEXT NOT NULL,

   INDEX i_entry_id_name (entry_id, name),
   INDEX i_name (name),

   CONSTRAINT fk_dleaf_attr_entry_id FOREIGN KEY (entry_id) REFERENCES directory_leaf(entry_id) ON DELETE CASCADE
) ENGINE = InnoDB;
