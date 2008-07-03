#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2005 Zimbra, Inc.
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

#############

my $MYSQL = "mysql";
my $LIQUID_USER = "liquid";
my $LIQUID_PASSWORD = "liquid";
if (-f "/opt/liquid/bin/lqlocalconfig") {
    $LIQUID_PASSWORD = `lqlocalconfig -s -m nokey liquid_mysql_password`;
    chomp $LIQUID_PASSWORD;
}
my $DATABASE = "liquid";

#############

my @mailboxIds = runSql($LIQUID_USER,
			$LIQUID_PASSWORD,
			"SELECT id FROM mailbox ORDER BY id");

printLog("Found " . scalar(@mailboxIds) . " mailbox databases.");

my $dbVersion = "6";

updateSchemaVersion();
addSyncStateColumns();

my $modMetadataColumn = "mod_metadata";
my $modContentColumn  = "mod_content";

my $id;
foreach $id (@mailboxIds) {
    renameModifiedColumn($id);
    addModContentColumn($id);
    shrinkDateColumn($id);
    addTombstoneTable($id);
    updateCheckpoints($id);
}

exit(0);

#############

sub updateSchemaVersion()
{
    my $sql = <<SET_SCHEMA_VERSION_EOF;

UPDATE $DATABASE.config SET value = '$dbVersion' WHERE name = 'db.version';

SET_SCHEMA_VERSION_EOF

    printLog("Updating DB schema version to $dbVersion.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub addSyncStateColumns()
{
    my $sql = <<ADD_MAILBOX_COLUMNS_EOF;

ALTER TABLE $DATABASE.mailbox
ADD (size_checkpoint    BIGINT UNSIGNED NOT NULL DEFAULT 0,
     change_checkpoint  BIGINT UNSIGNED NOT NULL DEFAULT 0,
     tracking_sync      BOOLEAN NOT NULL DEFAULT 0);

ALTER TABLE $DATABASE.mailbox
CHANGE COLUMN max_deleted_item item_id_checkpoint INTEGER UNSIGNED NOT NULL DEFAULT 0;

ADD_MAILBOX_COLUMNS_EOF

    printLog("Adding sync columns to $DATABASE.mailbox.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub renameModifiedColumn($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<RENAME_MODIFIED_COLUMN_EOF;

ALTER TABLE $dbName.mail_item
CHANGE COLUMN modified $modMetadataColumn BIGINT UNSIGNED NOT NULL;

RENAME_MODIFIED_COLUMN_EOF

    printLog("Renaming column $dbName.mail_item.modified to '$modMetadataColumn'.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub addModContentColumn($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<ADD_MOD_CONTENT_COLUMN_EOF;

ALTER TABLE $dbName.mail_item
ADD COLUMN ($modContentColumn BIGINT UNSIGNED NOT NULL);

ADD_MOD_CONTENT_COLUMN_EOF

    printLog("Adding column $dbName.mail_item.$modContentColumn.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub shrinkDateColumn($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<SHRINK_DATE_COLUMN_EOF;

UPDATE $dbName.mail_item
SET date = date / 1000;

ALTER TABLE $dbName.mail_item
MODIFY COLUMN date INTEGER UNSIGNED NOT NULL;

ALTER TABLE $dbName.mail_item
MODIFY COLUMN size INTEGER UNSIGNED NOT NULL;

SHRINK_DATE_COLUMN_EOF

    printLog("Shrinking column $dbName.mail_item.date to INTEGER.");
    printLog("Making column $dbName.mail_item.size UNSIGNED.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub addTombstoneTable($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<ADD_TOMBSTONE_TABLE_EOF;

CREATE TABLE IF NOT EXISTS $dbName.tombstone (
   sequence    BIGINT UNSIGNED NOT NULL,   # change number for deletion
   date        INTEGER UNSIGNED NOT NULL,  # deletion date as a UNIX-style timestamp
   ids         TEXT,

   INDEX i_sequence (sequence)
) ENGINE = InnoDB;

ADD_TOMBSTONE_TABLE_EOF

    printLog("Adding table $dbName.tombstone.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub updateCheckpoints($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
    my $sql = <<UPDATE_CHECKPOINT_EOF;

UPDATE $DATABASE.mailbox mbx,
       (SELECT MAX(id) max_id, MAX($modMetadataColumn) max_change FROM $dbName.mail_item) stats
SET change_checkpoint = max_change + 100,
    item_id_checkpoint = IF (item_id_checkpoint > max_id, item_id_checkpoint, max_id) + 20
WHERE mbx.id = $mailboxId;

UPDATE_CHECKPOINT_EOF

    printLog("Updating $DATABASE.mailbox.change_checkpoint for mailbox $mailboxId.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}	

#############

sub runSql($$$)
{
    my ($user, $password, $script) = @_;

    # Write the last script to a text file for debugging
    # open(LASTSCRIPT, ">lastScript.sql") || die "Could not open lastScript.sql";
    # print(LASTSCRIPT $script);
    # close(LASTSCRIPT);

    # Run the mysql command and redirect output to a temp file
    my $tempFile = "mysql.out";
    my $command = "$MYSQL --user=$user --password=$password " .
        "--database=$DATABASE --batch --skip-column-names";
    open(MYSQL, "| $command > $tempFile") || die "Unable to run $command";
    print(MYSQL $script);
    close(MYSQL);

    if ($? != 0) {
        die "Error while running '$command'.";
    }

    # Process output
    open(OUTPUT, $tempFile) || die "Could not open $tempFile";
    my @output;
    while (<OUTPUT>) {
        s/\s+$//;
        push(@output, $_);
    }

    return @output;
}

sub printLog
{
    print scalar(localtime()), ": ", @_, "\n";
}
