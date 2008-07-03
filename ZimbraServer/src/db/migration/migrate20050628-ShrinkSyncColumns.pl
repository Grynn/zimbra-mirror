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

my $dbVersion = "7";

updateSchemaVersion();

my $id;
foreach $id (@mailboxIds) {
    populateModContentColumn($id);
    clearTombstones($id);
    shrinkSyncColumns($id);
    updateCheckpoint($id);
}

shrinkCheckpointColumn();

exit(0);

#############

sub updateSchemaVersion()
{
    my $sql = <<SET_SCHEMA_VERSION_EOF;

UPDATE $DATABASE.config
SET value = '$dbVersion' WHERE name = 'db.version';

SET_SCHEMA_VERSION_EOF

    printLog("Updating DB schema version to $dbVersion.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub populateModContentColumn($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<POPULATE_MOD_CONTENT_EOF;

UPDATE $dbName.mail_item,
       (SELECT MIN(IF(mod_content = 0, 4000000000000000, mod_content)) d, MAX(mod_metadata) e FROM $dbName.mail_item) tmp
SET mod_content = IF(d = 4000000000000000, e, d) WHERE mod_content = 0;

POPULATE_MOD_CONTENT_EOF

    printLog("Populating missing values for $dbName.mail_item.mod_content.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub clearTombstones($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<CLEAR_TOMBSTONES_EOF;

DELETE FROM $dbName.tombstone;

CLEAR_TOMBSTONES_EOF

    printLog("Emptying $dbName.tombstone.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub shrinkSyncColumns($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<SHRINK_SYNC_COLUMNS_EOF;

UPDATE $dbName.mail_item, (SELECT MIN(mod_content) d FROM $dbName.mail_item) tmp
SET mod_metadata = IF(mod_metadata > d, mod_metadata - d, 0), mod_content = mod_content - d;

ALTER TABLE $dbName.mail_item
MODIFY COLUMN mod_metadata INTEGER UNSIGNED NOT NULL;

ALTER TABLE $dbName.mail_item
MODIFY COLUMN mod_content INTEGER UNSIGNED NOT NULL;

ALTER TABLE $dbName.tombstone
MODIFY COLUMN sequence INTEGER UNSIGNED NOT NULL;

SHRINK_SYNC_COLUMNS_EOF

    printLog("Shrinking $dbName.mail_item.mod_content to INTEGER.");
    printLog("Shrinking $dbName.mail_item.mod_metadata to INTEGER.");
    printLog("Shrinking $dbName.tombstone.sequence to INTEGER.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub updateCheckpoint($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<UPDATE_CHECKPOINT_EOF;

UPDATE $DATABASE.mailbox
SET change_checkpoint = (SELECT MAX(mod_metadata) + 100 FROM $dbName.mail_item)
WHERE id = $mailboxId;

UPDATE_CHECKPOINT_EOF

    printLog("Updating $DATABASE.mailbox.change_checkpoint for mailbox $mailboxId.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub shrinkCheckpointColumn()
{
    my $sql = <<SHRINK_CHECKPOINT_COLUMN_EOF;

ALTER TABLE $DATABASE.mailbox
MODIFY COLUMN change_checkpoint INTEGER UNSIGNED NOT NULL;

SHRINK_CHECKPOINT_COLUMN_EOF

    printLog("Shrinking $DATABASE.mailbox.change_checkpoint to INTEGER.");
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
