#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Version: MPL 1.1
# 
# The contents of this file are subject to the Mozilla Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite Server.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 


use strict;
use Migrate;

my $startTime = time();

my $CREATE_DB_SQL;
my $NUM_GROUPS = 1000;
my %MBOX_GROUPS;

sub init();
sub addGroupIdColumn();
sub getGroupId($);
sub createMailboxGroup($);
sub exportImportMailbox($);
sub dropDatabase($);
sub dropOrphans();

init();

Migrate::verifySchemaVersion(27);

addGroupIdColumn();

my @mailboxIds = Migrate::getMailboxIds();
foreach my $id (@mailboxIds) {
    my $oldDb = "mailbox$id";
    my $newDb = "mboxgroup" . getGroupId($id);
    createMailboxGroup($newDb);
    exportImportMailbox($id);
    dropDatabase($oldDb);
}

dropOrphans();

Migrate::updateSchemaVersion(27, 28);

my $elapsed = time() - $startTime;
print "Took $elapsed seconds\n";

exit(0);

#####################

sub init() {
    my $zimbraHome = $ENV{ZIMBRA_HOME} || '/opt/zimbra';
    my $createSql = "$zimbraHome/db/create_database.sql";
    $CREATE_DB_SQL = '';
    open(SQL, "< $createSql") or die "Unable to open $createSql: $!";
    while (<SQL>) {
        $CREATE_DB_SQL .= $_;
    }
    close(SQL);

    my $numGroups = `zmlocalconfig -q -m nokey zimbra_mailbox_groups`;
    chomp($numGroups) if (defined($numGroups));
    $numGroups += 0;  # make sure it's a number
    $numGroups = 1000 if ($numGroups == 0);
    $NUM_GROUPS = $numGroups;
}

sub getGroupId($) {
    my $id = shift;
    return ($id - 1) % $NUM_GROUPS + 1;
}

sub addGroupIdColumn() {
    my $sql = <<_ADD_GROUP_ID_;
ALTER TABLE zimbra.mailbox
ADD COLUMN group_id INTEGER UNSIGNED NOT NULL AFTER id;

UPDATE zimbra.mailbox SET group_id = MOD(id - 1, $NUM_GROUPS) + 1;
_ADD_GROUP_ID_

    Migrate::log("Adding group_id column to zimbra.mailbox table.");
    Migrate::runSql($sql);
}

sub createMailboxGroup($) {
    my $db = shift;
    return if (exists $MBOX_GROUPS{$db});
    my $sql = $CREATE_DB_SQL;
    $sql =~ s/\${DATABASE_NAME}/$db/gm;
    Migrate::log("Creating mailbox group $db");
    Migrate::runSql($sql);
    $MBOX_GROUPS{$db} = 1;
}

sub getDumpFile($$) {
    my ($id, $table) = @_;
    return "/tmp/migrate20060911-$$-mbox$id-$table.dat";
}

sub exportImportMailbox($) {
    my $id = shift;
    my $oldDb = "mailbox$id";
    my $newDb = "mboxgroup" . getGroupId($id);
    my $sql;
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

    $sql = <<_EXPORT_MAIL_ITEM_;
SELECT
    $id, id, type, parent_id, folder_id, index_id, imap_id,
    date, size, volume_id, blob_digest, unread, flags, tags, sender,
    subject, metadata, mod_metadata, change_date, mod_content
  INTO OUTFILE '$fileMailItem'
  $exportOptions
  FROM $oldDb.mail_item;
_EXPORT_MAIL_ITEM_

    Migrate::log("Exporting $oldDb.mail_item data to $fileMailItem");
    Migrate::runSql($sql);

    $sql = <<_EXPORT_OPEN_CONVERSATION_;
SELECT $id, hash, conv_id
  INTO OUTFILE '$fileOpenConversation'
  $exportOptions
  FROM $oldDb.open_conversation;
_EXPORT_OPEN_CONVERSATION_

    Migrate::log("Exporting $oldDb.open_conversation data to $fileOpenConversation");
    Migrate::runSql($sql);

    $sql = <<_EXPORT_APPOINTMENT_;
SELECT $id, uid, item_id, start_time, end_time
  INTO OUTFILE '$fileAppointment'
  $exportOptions
  FROM $oldDb.appointment;
_EXPORT_APPOINTMENT_

    Migrate::log("Exporting $oldDb.appointment data to $fileAppointment");
    Migrate::runSql($sql);

    $sql = <<_EXPORT_TOMBSTONE_;
SELECT $id, sequence, date, ids
  INTO OUTFILE '$fileTombstone'
  $exportOptions
  FROM $oldDb.tombstone;
_EXPORT_TOMBSTONE_

    Migrate::log("Exporting $oldDb.tombstone data to $fileTombstone");
    Migrate::runSql($sql);

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
        $sql = <<_IMPORT_;
SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;

LOAD DATA INFILE '$file'
  REPLACE
  INTO TABLE $newDb.$table
  $exportOptions;

SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS = 1;
_IMPORT_

        Migrate::log("Importing $file file into $newDb.$table");
        Migrate::runSql($sql);
    }

    for ($i = 0; $i < 4; $i++) {
        my $file = $files[$i];
        if (!unlink($file)) {
            Migrate::log("Error unlinking $file: $!");
        }
    }
}

sub dropDatabase($) {
    my $db = shift;
    Migrate::log("Dropping database $db");
    Migrate::runSql("DROP DATABASE $db");
}

sub dropOrphans() {
    my @orphans = Migrate::runSql("SHOW DATABASES LIKE 'mailbox\%'");
    foreach my $db (@orphans) {
        dropDatabase($db);
    }
}



##############################################################################
#
# Unused
#
##############################################################################

sub copyData($) {
    my $id = shift;
    my $oldDb = "mailbox$id";
    my $newDb = "mboxgroup" . getGroupId($id);
    my $sql = <<_COPY_DB_SQL_;
SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO $newDb.mail_item
    (mailbox_id, id, type, parent_id, folder_id, index_id, imap_id,
     date, size, volume_id, blob_digest, unread, flags, tags, sender,
     subject, metadata, mod_metadata, change_date, mod_content)
  SELECT
    $id, id, type, parent_id, folder_id, index_id, imap_id,
    date, size, volume_id, blob_digest, unread, flags, tags, sender,
    subject, metadata, mod_metadata, change_date, mod_content
  FROM $oldDb.mail_item;

INSERT INTO $newDb.open_conversation
    (mailbox_id, hash, conv_id)
  SELECT
    $id, hash, conv_id
  FROM $oldDb.open_conversation;

INSERT INTO $newDb.appointment
    (mailbox_id, uid, item_id, start_time, end_time)
  SELECT
    $id, uid, item_id, start_time, end_time
  FROM $oldDb.appointment;

INSERT INTO $newDb.tombstone
    (mailbox_id, sequence, date, ids)
  SELECT
    $id, sequence, date, ids
  FROM $oldDb.tombstone;

SET FOREIGN_KEY_CHECKS = 1;
_COPY_DB_SQL_

    Migrate::log("Copying data from $oldDb to $newDb");
    Migrate::runSql($sql);
}

sub updateMailboxDatabase($) {
    my $id = shift;
    my $db = "mailbox$id";
    my $newDb = "mboxgroup" . getGroupId($id);
    my $sql = <<_UPDATE_MAILBOX_DB_;
SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;

ALTER TABLE $db.mail_item
    DROP PRIMARY KEY,
    DROP INDEX i_type,
    DROP INDEX i_parent_id,
    DROP INDEX i_folder_id_date,
    DROP INDEX i_index_id,
    DROP INDEX i_unread,
    DROP INDEX i_date,
    DROP INDEX i_mod_metadata,
    DROP INDEX i_tags_date,
    DROP INDEX i_flags_date,
    DROP INDEX i_volume_id,
    DROP FOREIGN KEY fk_mail_item_parent_id,
    DROP FOREIGN KEY fk_mail_item_folder_id,
    ADD COLUMN mailbox_id INTEGER UNSIGNED NOT NULL FIRST,
    ADD PRIMARY KEY (mailbox_id, id),
    ADD INDEX i_type (mailbox_id, id),
    ADD INDEX i_parent_id (mailbox_id, parent_id),
    ADD INDEX i_folder_id_date (mailbox_id, folder_id, date),
    ADD INDEX i_index_id (mailbox_id, index_id),
    ADD INDEX i_unread (mailbox_id, unread),
    ADD INDEX i_date (mailbox_id, date),
    ADD INDEX i_mod_metadata (mailbox_id, mod_metadata),
    ADD INDEX i_tags_date (mailbox_id, tags, date),
    ADD INDEX i_flags_date (mailbox_id, flags, date),
    ADD INDEX i_volume_id (mailbox_id, volume_id),
    ADD CONSTRAINT fk_mail_item_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
    ADD CONSTRAINT fk_mail_item_volume_id FOREIGN KEY (volume_id) REFERENCES zimbra.volume(id);

ALTER TABLE $db.open_conversation
    DROP PRIMARY KEY,
    DROP INDEX i_conv_id,
    DROP CONSTRAINT fk_open_conversation_conv_id,
    ADD COLUMN mailbox_id INTEGER UNSIGNED NOT NULL FIRST,
    ADD PRIMARY KEY (mailbox_id, hash),
    ADD INDEX i_conv_id (mailbox_id, conv_id),
    ADD CONSTRAINT fk_open_conversation_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
    ADD CONSTRAINT fk_open_conversation_conv_id FOREIGN KEY (mailbox_id, conv_id) REFERENCES \$\{DATABASE_NAME\}.mail_item(mailbox_id, id) ON DELETE CASCADE;

ALTER TABLE $db.appointment
    DROP PRIMARY KEY
    DROP INDEX i_item_id,
    DROP CONSTRAINT fk_appointment_item_id,
    ADD COLUMN mailbox_id INTEGER UNSIGNED NOT NULL FIRST,
    ADD PRIMARY KEY (mailbox_id, uid),
    ADD INDEX i_item_id (mailbox_id, item_id),
    ADD CONSTRAINT fk_appointment_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
    ADD CONSTRAINT fk_appointment_item_id FOREIGN KEY (mailbox_id, item_id) REFERENCES \$\{DATABASE_NAME\}.mail_item(mailbox_id, id) ON DELETE CASCADE;

ALTER TABLE $db.tombstone
    DROP INDEX i_sequence,
    ADD COLUMN mailbox_id INTEGER UNSIGNED NOT NULL FIRST,
    ADD INDEX i_sequence (mailbox_id, sequence),
    ADD CONSTRAINT fk_tombstone_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id);

RENAME TABLE $db.mail_item TO $newDb.mail_item;

RENAME TABLE $db.open_conversation TO $newDb.open_conversation;

RENAME TABLE $db.appointment TO $newDb.appointment;

RENAME TABLE $db.tombstone TO $newDb.tombstone;

SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS = 1;

_UPDATE_MAILBOX_DB_

    Migrate::log("Updating mailbox $id");
    Migrate::runSql($sql);
}
