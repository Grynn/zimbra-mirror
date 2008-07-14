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

-- -----------------------------------------------------------------------
-- directory
-- -----------------------------------------------------------------------

CREATE TABLE directory (
   entry_id    INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
   entry_type  CHAR(4) NOT NULL,
   entry_name  VARCHAR(128) NOT NULL,
   zimbra_id   CHAR(36) UNIQUE,
   modified    SMALLINT NOT NULL,

   UNIQUE(entry_type, entry_name)
);


CREATE TABLE directory_attrs (
   entry_id    INTEGER NOT NULL,
   name        VARCHAR(255) NOT NULL,
   value       VARCHAR(10240) NOT NULL,

   CONSTRAINT fk_dattr_entry_id FOREIGN KEY (entry_id) REFERENCES directory(entry_id) ON DELETE CASCADE
);

CREATE INDEX i_dattr_entry_id_name ON directory_attrs(entry_id, name);
CREATE INDEX i_dattr_name ON directory_attrs(name);

CREATE TRIGGER fki_dattr_entry_id
BEFORE INSERT ON [directory_attrs]
FOR EACH ROW BEGIN
  SELECT RAISE(ROLLBACK, 'insert on table "directory_attrs" violates foreign key constraint "fki_dattr_entry_id"')
  WHERE (SELECT entry_id FROM directory WHERE entry_id = NEW.entry_id) IS NULL;
END;

CREATE TRIGGER fku_dattr_entry_id
BEFORE UPDATE OF mailbox_id ON [directory_attrs] 
FOR EACH ROW BEGIN
    SELECT RAISE(ROLLBACK, 'update on table "directory_attrs" violates foreign key constraint "fku_dattr_entry_id"')
      WHERE (SELECT entry_id FROM directory WHERE entry_id = NEW.entry_id) IS NULL;
END;

CREATE TRIGGER fkdc_dattr_entry_id
BEFORE DELETE ON directory
FOR EACH ROW BEGIN 
    DELETE FROM directory_attrs WHERE directory_attrs.entry_id = OLD.entry_id;
END;


CREATE TABLE directory_leaf (
   entry_id    INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
   parent_id   INTEGER NOT NULL,
   entry_type  CHAR(4) NOT NULL,
   entry_name  VARCHAR(128) NOT NULL,
   zimbra_id   CHAR(36) NOT NULL UNIQUE,

   UNIQUE (parent_id, entry_type, entry_name),
   CONSTRAINT fk_dleaf_entry_id FOREIGN KEY (parent_id) REFERENCES directory(entry_id) ON DELETE CASCADE
);

CREATE TRIGGER fki_dleaf_entry_id
BEFORE INSERT ON [directory_leaf]
FOR EACH ROW BEGIN
  SELECT RAISE(ROLLBACK, 'insert on table "directory_leaf" violates foreign key constraint "fki_dleaf_entry_id"')
  WHERE (SELECT entry_id FROM directory WHERE entry_id = NEW.entry_id) IS NULL;
END;

CREATE TRIGGER fku_dleaf_entry_id
BEFORE UPDATE OF mailbox_id ON [directory_leaf] 
FOR EACH ROW BEGIN
    SELECT RAISE(ROLLBACK, 'update on table "directory_leaf" violates foreign key constraint "fku_dleaf_entry_id"')
      WHERE (SELECT entry_id FROM directory WHERE entry_id = NEW.entry_id) IS NULL;
END;

CREATE TRIGGER fkdc_dleaf_entry_id
BEFORE DELETE ON directory
FOR EACH ROW BEGIN 
    DELETE FROM directory_leaf WHERE directory_leaf.entry_id = OLD.entry_id;
END;


CREATE TABLE directory_leaf_attrs (
   entry_id    INTEGER NOT NULL,
   name        VARCHAR(255) NOT NULL,
   value       VARCHAR(10240) NOT NULL,

   CONSTRAINT fk_dleafattr_entry_id FOREIGN KEY (entry_id) REFERENCES directory_leaf(entry_id) ON DELETE CASCADE
);

CREATE INDEX i_dleafattr_entry_id_name ON directory_leaf_attrs(entry_id, name);
CREATE INDEX i_dleafattr_name ON directory_leaf_attrs(name);

CREATE TRIGGER fki_dleafattr_entry_id
BEFORE INSERT ON [directory_leaf_attrs]
FOR EACH ROW BEGIN
  SELECT RAISE(ROLLBACK, 'insert on table "directory_leaf_attrs" violates foreign key constraint "fki_dleafattr_entry_id"')
  WHERE (SELECT entry_id FROM directory_leaf WHERE entry_id = NEW.entry_id) IS NULL;
END;

CREATE TRIGGER fku_dleafattr_entry_id
BEFORE UPDATE OF mailbox_id ON [directory_leaf_attrs] 
FOR EACH ROW BEGIN
    SELECT RAISE(ROLLBACK, 'update on table "directory_leaf_attrs" violates foreign key constraint "fku_dleafattr_entry_id"')
      WHERE (SELECT entry_id FROM directory_leaf WHERE entry_id = NEW.entry_id) IS NULL;
END;

CREATE TRIGGER fkdc_dleafattr_entry_id
BEFORE DELETE ON directory_leaf
FOR EACH ROW BEGIN 
    DELETE FROM directory_leaf_attrs WHERE directory_leaf_attrs.entry_id = OLD.entry_id;
END;
