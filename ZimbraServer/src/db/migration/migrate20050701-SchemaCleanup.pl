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

my $dbVersion = "8";

updateSchemaVersion();

my $id;
foreach $id (@mailboxIds) {
    addIndexIdColumn($id);
    addVolumeIdColumn($id);
    moveTagNames($id);
}

addCommentColumn();

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

sub addCommentColumn()
{
    my $sql = <<ADD_COMMENT_COLUMN_EOF;

ALTER TABLE $DATABASE.mailbox
ADD COLUMN comment VARCHAR(255);

ADD_COMMENT_COLUMN_EOF

    printLog("Adding column $DATABASE.mailbox.comment.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);

    if (-f "/opt/liquid/bin/lqprov") {
        my @accountIds = runSql($LIQUID_USER,
                    $LIQUID_PASSWORD,
                    "SELECT account_id FROM mailbox");
        my $account;
        foreach $account (@accountIds) {
            my $name = `/opt/liquid/bin/lqprov ga $account | grep '^liquidMailDeliveryAddress' | sed -e 's/^.*: //'`;
            chomp($name);

            my $sql = <<SET_COMMENT_EOF;

UPDATE $DATABASE.mailbox
SET comment = '$name'
WHERE account_id = '$account';            

SET_COMMENT_EOF

            printLog("Setting comment to '$name' for account $account.");
            runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
        }
    }
}

sub addIndexIdColumn($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<ADD_INDEX_ID_COLUMN_EOF;

ALTER TABLE $dbName.mail_item
ADD COLUMN index_id INTEGER UNSIGNED AFTER folder_id;

UPDATE $dbName.mail_item
SET index_id = id WHERE type > 4;

ALTER TABLE $dbName.mail_item
ADD INDEX i_index_id (index_id);

ADD_INDEX_ID_COLUMN_EOF

    printLog("Adding, setting, and indexing $dbName.mail_item.index_id.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub addVolumeIdColumn($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<ADD_VOLUME_ID_COLUMN_EOF;

ALTER TABLE $dbName.mail_item
ADD COLUMN volume_id TINYINT UNSIGNED AFTER size;

UPDATE $dbName.mail_item, $DATABASE.mailbox
SET volume_id = message_volume_id
WHERE blob_digest IS NOT NULL AND $DATABASE.mailbox.id = $mailboxId;

ADD_VOLUME_ID_COLUMN_EOF

    printLog("Adding and setting $dbName.mail_item.volume_id.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub moveTagNames($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<MOVE_TAG_NAMES_EOF;

UPDATE $dbName.mail_item
SET subject = SUBSTRING_INDEX(SUBSTRING_INDEX(metadata, _utf8';', 1), _utf8':', -1)
WHERE type = 3 AND metadata LIKE _utf8'n%';

UPDATE $dbName.mail_item
SET metadata = IF(metadata LIKE _utf8'%;%;%', SUBSTRING_INDEX(metadata, _utf8';', -2), _utf8'')
WHERE type = 3 AND metadata LIKE _utf8'n%';

MOVE_TAG_NAMES_EOF

    printLog("Moving tag names to $dbName.mail_item.subject.");
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
