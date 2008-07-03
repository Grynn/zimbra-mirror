#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2005, 2006 Zimbra, Inc.
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
my $ROOT_USER = "root";
my $ROOT_PASSWORD = "liquid";
my $LIQUID_USER = "liquid";
my $LIQUID_PASSWORD = "liquid";
my $PASSWORD = "liquid";
my $DATABASE = "liquid";

#############

my @mailboxIds = runSql($LIQUID_USER,
			$LIQUID_PASSWORD,
			"SELECT id FROM mailbox ORDER BY id");

print("Creating " . scalar(@mailboxIds) . " mailbox databases.\n");

grant();

my $id;
foreach $id (@mailboxIds) {
    createDatabase($id);
    copyData($id);
    addConstraintsAndIndexes($id);
}

exit(0);

#############

sub runSql($$$)
{
    my ($user, $password, $script) = @_;

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

sub grant
{
    print("Granting permissions to $LIQUID_USER.\n");

    my $sql = <<GRANT_EOF;
GRANT ALL ON *.* TO '$LIQUID_USER' WITH GRANT OPTION;
GRANT ALL ON *.* TO '$LIQUID_USER'\@'localhost' WITH GRANT OPTION;
GRANT ALL ON *.* TO '$LIQUID_USER'\@'localhost.localdomain' WITH GRANT OPTION;
GRANT_EOF

    # print($sql, "\n");
    runSql($ROOT_USER, $ROOT_PASSWORD, $sql);
}

sub createDatabase($)
{
    my ($mailboxId) = @_;
    if (!($mailboxId =~ /^[0-9]+$/)) {
	die "Invalid mailbox id: '$mailboxId'";
    }
    my $databaseName = "mailbox" . $mailboxId;

    print("Creating database $databaseName.\n");

    my $sql = <<CREATE_EOF;
DROP DATABASE IF EXISTS $databaseName;

CREATE DATABASE $databaseName
DEFAULT CHARACTER SET utf8;

GRANT ALL ON $databaseName.* TO '$LIQUID_USER' WITH GRANT OPTION;
GRANT ALL ON $databaseName.* TO '$LIQUID_USER'\@'localhost' WITH GRANT OPTION;
GRANT ALL ON $databaseName.* TO '$LIQUID_USER'\@'localhost.localdomain' WITH GRANT OPTION;

CREATE TABLE IF NOT EXISTS $databaseName.mail_item (
   id           INTEGER UNSIGNED NOT NULL,
   type         TINYINT NOT NULL,           # 1 = folder, 3 = tag, etc.
   mailbox_id   INTEGER UNSIGNED NOT NULL,
   parent_id    INTEGER UNSIGNED,
   folder_id    INTEGER UNSIGNED,
   date         DATETIME NOT NULL,
   size         INTEGER NOT NULL,
   blob_digest  VARCHAR(28) BINARY,         # reference to blob, meaningful for messages only (type == 5)
   flags        INTEGER NOT NULL DEFAULT 0,
   tags         BIGINT NOT NULL DEFAULT 0,
   sender       VARCHAR(128),
   subject      TEXT,
   metadata     TEXT,
   modified     DATETIME NOT NULL
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS $databaseName.open_conversation (
   mailbox_id  INTEGER UNSIGNED NOT NULL,
   hash        CHAR(28) BINARY NOT NULL,
   conv_id     INTEGER UNSIGNED NOT NULL
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS $databaseName.appointment (
   mailbox_id  INTEGER UNSIGNED NOT NULL,
   uid         VARCHAR(255) NOT NULL,
   item_id     INTEGER UNSIGNED NOT NULL,
   start_time  DATETIME NOT NULL,
   end_time    DATETIME,
   comp_id     TINYINT(1) NOT NULL

) ENGINE = InnoDB;
CREATE_EOF

    # print($sql);
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub copyData($)
{
    my ($mailboxId) = @_;
    if (!($mailboxId =~ /^[0-9]+$/)) {
	die "Invalid mailbox id: '$mailboxId'";
    }
    my $databaseName = "mailbox" . $mailboxId;

    print("Copying data to $databaseName.\n");

    my $sql = <<COPY_EOF;
INSERT INTO $databaseName.mail_item
  SELECT * FROM liquid.mail_item
  WHERE mailbox_id = $mailboxId;

INSERT INTO $databaseName.appointment
  SELECT * FROM liquid.appointment
  WHERE mailbox_id = $mailboxId;

INSERT INTO $databaseName.open_conversation
  SELECT * FROM liquid.open_conversation
  WHERE mailbox_id = $mailboxId;
COPY_EOF

    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

sub addConstraintsAndIndexes($)
{
    my ($mailboxId) = @_;
    if (!($mailboxId =~ /^[0-9]+$/)) {
	die "Invalid mailbox id: '$mailboxId'";
    }
    my $databaseName = "mailbox" . $mailboxId;

    print("Adding constraints and indexes to $databaseName.\n");

    my $sql = <<CONSTRAINTS_EOF;
    ALTER TABLE $databaseName.mail_item ADD INDEX (mailbox_id, type);
    ALTER TABLE $databaseName.mail_item ADD INDEX (mailbox_id, parent_id);
    ALTER TABLE $databaseName.mail_item ADD INDEX (mailbox_id, folder_id);
    ALTER TABLE $databaseName.mail_item ADD INDEX (sender);
    ALTER TABLE $databaseName.mail_item ADD INDEX (subject(128));

    ALTER TABLE $databaseName.mail_item ADD PRIMARY KEY (mailbox_id, id);

    ALTER TABLE $databaseName.mail_item
    ADD CONSTRAINT FOREIGN KEY (mailbox_id, parent_id) REFERENCES $databaseName.mail_item(mailbox_id, id) ON DELETE CASCADE;

    ALTER TABLE $databaseName.mail_item
    ADD CONSTRAINT FOREIGN KEY (mailbox_id, folder_id) REFERENCES $databaseName.mail_item(mailbox_id, id) ON DELETE CASCADE;

    ALTER TABLE $databaseName.open_conversation ADD PRIMARY KEY (mailbox_id, hash);

    ALTER TABLE $databaseName.open_conversation
    ADD CONSTRAINT FOREIGN KEY (mailbox_id, conv_id) REFERENCES $databaseName.mail_item(mailbox_id, id) ON DELETE CASCADE;

    ALTER TABLE $databaseName.appointment ADD INDEX (uid);

    ALTER TABLE $databaseName.appointment
	ADD CONSTRAINT FOREIGN KEY (mailbox_id, item_id) REFERENCES $databaseName.mail_item(mailbox_id, id) ON DELETE CASCADE;

CONSTRAINTS_EOF

    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}
