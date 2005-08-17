DROP DATABASE IF EXISTS ${DATABASE_NAME};

CREATE DATABASE ${DATABASE_NAME}
DEFAULT CHARACTER SET utf8;

GRANT ALL ON ${DATABASE_NAME}.* TO 'liquid';
GRANT ALL ON ${DATABASE_NAME}.* TO 'liquid'@'localhost';
GRANT ALL ON ${DATABASE_NAME}.* TO 'liquid'@'localhost.localdomain';

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.mail_item (
   id            INTEGER UNSIGNED NOT NULL,
   type          TINYINT NOT NULL,           # 1 = folder, 3 = tag, etc.
   parent_id     INTEGER UNSIGNED,
   folder_id     INTEGER UNSIGNED,
   index_id      INTEGER UNSIGNED,
   date          INTEGER UNSIGNED NOT NULL,  # date is stored as a UNIX-style timestamp
   size          INTEGER UNSIGNED NOT NULL,
   volume_id     TINYINT UNSIGNED,
   blob_digest   VARCHAR(28) BINARY,         # reference to blob, meaningful for messages only (type == 5)
   unread        BOOLEAN NULL,               # stored separately from the other flags so we can index it
   flags         INTEGER NOT NULL DEFAULT 0,
   tags          BIGINT NOT NULL DEFAULT 0,
   sender        VARCHAR(128),
   subject       TEXT,
   metadata      TEXT,
   mod_metadata  INTEGER UNSIGNED NOT NULL,  # change number for last row modification
   mod_content   INTEGER UNSIGNED NOT NULL,  # change number for last change to "content" (e.g. blob)

   PRIMARY KEY (id),
   INDEX i_type (type),                      # for looking up folders and tags
   INDEX i_parent_id (parent_id),            # for looking up a parent's children
   INDEX i_folder_id_date (folder_id, date), # for looking up by folder and sorting by date
   INDEX i_index_id (index_id),              # for looking up based on search results
   INDEX i_unread (unread),                  # there should be a small number of items with unread=TRUE
                                             # no compound index on (unread, date), so we save space at
                                             # the expense of sorting a small number of rows
   INDEX i_date (date),                      # fallback index in case other constraints are not specified
   INDEX i_mod_metadata (mod_metadata),      # used by the sync code
   CONSTRAINT fk_parent_id FOREIGN KEY (parent_id) REFERENCES ${DATABASE_NAME}.mail_item(id),
   CONSTRAINT fk_folder_id FOREIGN KEY (folder_id) REFERENCES ${DATABASE_NAME}.mail_item(id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.open_conversation (
   hash        CHAR(28) BINARY NOT NULL,
   conv_id     INTEGER UNSIGNED NOT NULL,

   PRIMARY KEY (hash),
   INDEX i_conv_id (conv_id),
   CONSTRAINT fk_conv_id FOREIGN KEY (conv_id) REFERENCES ${DATABASE_NAME}.mail_item(id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.appointment (
   uid         VARCHAR(255) NOT NULL,
   item_id     INTEGER UNSIGNED NOT NULL,
   start_time  DATETIME NOT NULL,
   end_time    DATETIME,

   PRIMARY KEY (uid),
   INDEX i_item_id (item_id),
   CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES ${DATABASE_NAME}.mail_item(id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.tombstone (
   sequence    INTEGER UNSIGNED NOT NULL,  # change number for deletion
   date        INTEGER UNSIGNED NOT NULL,  # deletion date as a UNIX-style timestamp
   ids         TEXT,

   INDEX i_sequence (sequence)
) ENGINE = InnoDB;

