#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2006, 2007 Zimbra, Inc.
# 
# The contents of this file are subject to the Yahoo! Public License
# Version 1.0 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# 
# ***** END LICENSE BLOCK *****
# 


use strict;
use Migrate;

my $startTime = time();

my $CREATE_DB_SQL;
my $NUM_GROUPS = 1000;
my %MBOX_GROUPS;
my %DROPPED_DBS;

sub init();
sub addGroupIdColumn();
sub getGroupId($);
sub createMailboxGroup($);
sub exportImportMailbox($);
sub dropDatabase($);
sub dropOrphans();

init();

Migrate::verifySchemaVersion(27);

my $sql;
my $sqlfile = "/tmp/migrate-MailboxGroup.sql";
open(SQL, "> $sqlfile") or die "Unable to open $sqlfile for write: $!";

$sql = addGroupIdColumn();
print SQL $sql;
print SQL "\n";

my @mailboxIds = Migrate::getMailboxIds();
foreach my $id (@mailboxIds) {
    my $oldDb = "mailbox$id";
    my $newDb = "mboxgroup" . getGroupId($id);
    $sql = createMailboxGroup($newDb);
    if ($sql) {
        print SQL $sql;
        print SQL "\n";
    }
    $sql = exportImportMailbox($id);
    print SQL $sql;
    print SQL "\n";
    $sql = dropDatabase($oldDb);
    print SQL $sql;
    print SQL "\n";
}

#$sql = dropOrphans();
#print SQL $sql;
#print SQL "\n";

close(SQL);
print "Executing SQL statements in $sqlfile\n";
my $tempFile = "/tmp/migrate20060911.out.$$";
my $rc = 0xffff & system("/opt/zimbra/bin/mysql -v -A zimbra < $sqlfile > $tempFile 2>&1");
if ($rc != 0) {
    die "mysql invocation failed, exit code = $rc: $!";
    open(OUTPUT, $tempFile);
    while (<OUTPUT>) {
      print;
    }
    close(OUTPUT);
}
print "Successfully finished executing SQL statements in $sqlfile\n";
#unlink($sqlfile);

Migrate::updateSchemaVersion(27, 28);

my $elapsed = time() - $startTime;
print "Took $elapsed seconds\n";

exit(0);

#####################

sub init() {
    $CREATE_DB_SQL = getMboxGroupSchemaSql();
    my $numGroups = `zmlocalconfig -q -m nokey zimbra_mailbox_groups`;
    chomp($numGroups) if (defined($numGroups));
    $numGroups += 0;  # make sure it's a number
    $numGroups = 1000 if ($numGroups == 0);
    $NUM_GROUPS = $numGroups;
}

sub dropDatabase($) {
    my $db = shift;
    $DROPPED_DBS{$db} = 1;
    return "# Dropping database $db\nDROP DATABASE IF EXISTS $db;\n";
}

sub dropOrphans() {
    my $sql = '';
    my @orphans = Migrate::runSql("SHOW DATABASES LIKE 'mailbox\%'");
    foreach my $db (@orphans) {
        if (!exists($DROPPED_DBS{$db})) {
            $sql .= dropDatabase($db);
        }
    }
    return $sql;
}

sub getGroupId($) {
    my $id = shift;
    return ($id - 1) % $NUM_GROUPS + 1;
}

sub addGroupIdColumn() {
    my $sql = <<_ADD_GROUP_ID_;
# Adding group_id column to zimbra.mailbox table
ALTER TABLE zimbra.mailbox
ADD COLUMN group_id INTEGER UNSIGNED NOT NULL AFTER id;

UPDATE zimbra.mailbox SET group_id = MOD(id - 1, $NUM_GROUPS) + 1;
_ADD_GROUP_ID_

    return $sql;
}

sub createMailboxGroup($) {
    my $db = shift;
    return '' if (exists $MBOX_GROUPS{$db});
    my $sql = $CREATE_DB_SQL;
    $sql =~ s/\${DATABASE_NAME}/$db/gm;
    $MBOX_GROUPS{$db} = 1;
    return $sql;
}

sub getDumpFile($$) {
    my ($id, $table) = @_;
    return "/tmp/migrate20060911-$$-mbox$id-$table.dat";
}

sub exportImportMailbox($) {
    my $id = shift;
    my $oldDb = "mailbox$id";
    my $newDb = "mboxgroup" . getGroupId($id);
    my $sql = '';
    my $file;

    my $exportOptions =
        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\\n'";

    my $fileMailItem = getDumpFile($id, 'mail_item');
    my $fileOpenConversation = getDumpFile($id, 'open_conversation');
    my $fileAppointment = getDumpFile($id, 'appointment');
    my $fileTombstone = getDumpFile($id, 'tombstone');

    ##########################
    #
    # Export tables
    #
    ##########################

    $sql .= <<_EXPORT_MAIL_ITEM_;
# Exporting $oldDb.mail_item data to $fileMailItem
SELECT
    $id, id, type, parent_id, folder_id, index_id, imap_id,
    date, size, volume_id, blob_digest, unread, flags, tags, sender,
    subject, metadata, mod_metadata, change_date, mod_content
  INTO OUTFILE '$fileMailItem'
  $exportOptions
  FROM $oldDb.mail_item;

_EXPORT_MAIL_ITEM_

    $sql .= <<_EXPORT_OPEN_CONVERSATION_;
# Exporting $oldDb.open_conversation data to $fileOpenConversation
SELECT $id, hash, conv_id
  INTO OUTFILE '$fileOpenConversation'
  $exportOptions
  FROM $oldDb.open_conversation;

_EXPORT_OPEN_CONVERSATION_

    $sql .= <<_EXPORT_APPOINTMENT_;
# Exporting $oldDb.appointment data to $fileAppointment
SELECT $id, uid, item_id, start_time, end_time
  INTO OUTFILE '$fileAppointment'
  $exportOptions
  FROM $oldDb.appointment;

_EXPORT_APPOINTMENT_

    $sql .= <<_EXPORT_TOMBSTONE_;
# Exporting $oldDb.tombstone data to $fileTombstone
SELECT $id, sequence, date, ids
  INTO OUTFILE '$fileTombstone'
  $exportOptions
  FROM $oldDb.tombstone;

_EXPORT_TOMBSTONE_

    ##########################
    #
    # Import tables into mboxgroupN database
    #
    ##########################

    my @tables = ('mail_item', 'open_conversation', 'appointment', 'tombstone');
    my @files = ($fileMailItem, $fileOpenConversation, $fileAppointment, $fileTombstone);
    my $i;
    for ($i = 0; $i < 4; $i++) {
        my $table = $tables[$i];
        my $file = $files[$i];
        $sql .= <<_IMPORT_;
# Importing $file file into $newDb.$table
SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;

LOAD DATA INFILE '$file'
  REPLACE
  INTO TABLE $newDb.$table
  $exportOptions;

SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS = 1;

_IMPORT_
    }

    $sql .= 'system rm -f';
    for ($i = 0; $i < 4; $i++) {
        my $file = $files[$i];
	$sql .= " $file";
    }
    $sql .= "\n";

    return $sql;
}

sub getMboxGroupSchemaSql() {
    my $sql = <<'_SCHEMA_SQL_';
DROP DATABASE IF EXISTS ${DATABASE_NAME};

CREATE DATABASE ${DATABASE_NAME}
DEFAULT CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.mail_item (
   mailbox_id    INTEGER UNSIGNED NOT NULL,
   id            INTEGER UNSIGNED NOT NULL,
   type          TINYINT NOT NULL,           # 1 = folder, 3 = tag, etc.
   parent_id     INTEGER UNSIGNED,
   folder_id     INTEGER UNSIGNED,
   index_id      INTEGER UNSIGNED,
   imap_id       INTEGER UNSIGNED,
   date          INTEGER UNSIGNED NOT NULL,  # stored as a UNIX-style timestamp
   size          INTEGER UNSIGNED NOT NULL,
   volume_id     TINYINT UNSIGNED,
   blob_digest   VARCHAR(28) BINARY,         # reference to blob, meaningful for messages only (type == 5)
   unread        INTEGER UNSIGNED,           # stored separately from the other flags so we can index it
   flags         INTEGER NOT NULL DEFAULT 0,
   tags          BIGINT NOT NULL DEFAULT 0,
   sender        VARCHAR(128),
   subject       TEXT,
   metadata      TEXT,
   mod_metadata  INTEGER UNSIGNED NOT NULL,  # change number for last row modification
   change_date   INTEGER UNSIGNED,           # UNIX-style timestamp for last row modification
   mod_content   INTEGER UNSIGNED NOT NULL,  # change number for last change to "content" (e.g. blob)

   PRIMARY KEY (mailbox_id, id),
   INDEX i_type (mailbox_id, type),          # for looking up folders and tags
   INDEX i_parent_id (mailbox_id, parent_id),# for looking up a parent\'s children
   INDEX i_folder_id_date (mailbox_id, folder_id, date), # for looking up by folder and sorting by date
   INDEX i_index_id (mailbox_id, index_id),  # for looking up based on search results
   INDEX i_unread (mailbox_id, unread),      # there should be a small number of items with unread=TRUE
                                             # no compound index on (unread, date), so we save space at
                                             # the expense of sorting a small number of rows
   INDEX i_date (mailbox_id, date),          # fallback index in case other constraints are not specified
   INDEX i_mod_metadata (mailbox_id, mod_metadata),      # used by the sync code
   INDEX i_tags_date (mailbox_id, tags, date),           # for tag searches
   INDEX i_flags_date (mailbox_id, flags, date),         # for flag searches
   INDEX i_volume_id (mailbox_id, volume_id),            # for the foreign key into the volume table

   CONSTRAINT fk_mail_item_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
   CONSTRAINT fk_mail_item_parent_id FOREIGN KEY (mailbox_id, parent_id) REFERENCES ${DATABASE_NAME}.mail_item(mailbox_id, id),
   CONSTRAINT fk_mail_item_folder_id FOREIGN KEY (mailbox_id, folder_id) REFERENCES ${DATABASE_NAME}.mail_item(mailbox_id, id),
   CONSTRAINT fk_mail_item_volume_id FOREIGN KEY (volume_id) REFERENCES zimbra.volume(id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.open_conversation (
   mailbox_id  INTEGER UNSIGNED NOT NULL,
   hash        CHAR(28) BINARY NOT NULL,
   conv_id     INTEGER UNSIGNED NOT NULL,

   PRIMARY KEY (mailbox_id, hash),
   INDEX i_conv_id (mailbox_id, conv_id),
   CONSTRAINT fk_open_conversation_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
   CONSTRAINT fk_open_conversation_conv_id FOREIGN KEY (mailbox_id, conv_id) REFERENCES ${DATABASE_NAME}.mail_item(mailbox_id, id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.appointment (
   mailbox_id  INTEGER UNSIGNED NOT NULL,
   uid         VARCHAR(255) NOT NULL,
   item_id     INTEGER UNSIGNED NOT NULL,
   start_time  DATETIME NOT NULL,
   end_time    DATETIME,

   PRIMARY KEY (mailbox_id, uid),
   INDEX i_item_id (mailbox_id, item_id),
   CONSTRAINT fk_appointment_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
   CONSTRAINT fk_appointment_item_id FOREIGN KEY (mailbox_id, item_id) REFERENCES ${DATABASE_NAME}.mail_item(mailbox_id, id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS ${DATABASE_NAME}.tombstone (
   mailbox_id  INTEGER UNSIGNED NOT NULL,
   sequence    INTEGER UNSIGNED NOT NULL,  # change number for deletion
   date        INTEGER UNSIGNED NOT NULL,  # deletion date as a UNIX-style timestamp
   ids         TEXT,

   INDEX i_sequence (mailbox_id, sequence),
   CONSTRAINT fk_tombstone_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id)
) ENGINE = InnoDB;
_SCHEMA_SQL_
    return $sql;
}
