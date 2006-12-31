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

USE zimbra;


-- -----------------------------------------------------------------------
-- directory
-- -----------------------------------------------------------------------

CREATE TABLE directory (
   entry_id    INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   entry_type  CHAR(4) NOT NULL,
   entry_name  VARCHAR(128) NOT NULL,
   zimbra_id   CHAR(73),
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
