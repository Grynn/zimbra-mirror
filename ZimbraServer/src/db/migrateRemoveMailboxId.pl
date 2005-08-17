#!/bin/perl

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

printLog("Found " . scalar(@mailboxIds) . " mailbox databases.");

my $id;
foreach $id (@mailboxIds) {
    dropConstraints($id);
    dropColumns($id);
    addConstraints($id);
}

exit(0);

#############


sub dropConstraints($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<DROP_CONSTRAINTS_EOF;

ALTER TABLE $dbName.open_conversation
DROP PRIMARY KEY,
DROP FOREIGN KEY open_conversation_ibfk_1,
DROP INDEX mailbox_id;

ALTER TABLE $dbName.appointment
DROP INDEX uid,
DROP INDEX mailbox_id,
DROP FOREIGN KEY appointment_ibfk_1;

ALTER TABLE $dbName.mail_item
DROP FOREIGN KEY mail_item_ibfk_1,
DROP FOREIGN KEY mail_item_ibfk_2;

ALTER TABLE $dbName.mail_item
DROP INDEX mailbox_id,
DROP INDEX mailbox_id_2,
DROP INDEX mailbox_id_3,
DROP INDEX sender,
DROP INDEX subject;

DROP_CONSTRAINTS_EOF

    printLog("Dropping constraints in $dbName.");
    runSql($ROOT_USER, $ROOT_PASSWORD, $sql);
}

sub dropColumns($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
    my $sql = <<DROP_COLUMNS_EOF;

ALTER TABLE $dbName.mail_item
DROP COLUMN mailbox_id;

ALTER TABLE $dbName.open_conversation
DROP COLUMN mailbox_id;

ALTER TABLE $dbName.appointment
DROP COLUMN mailbox_id;

DROP_COLUMNS_EOF

    printLog("Dropping mailbox_id from tables in $dbName.");
    runSql($ROOT_USER, $ROOT_PASSWORD, $sql);
}	

sub addConstraints($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
    my $sql = <<DROP_COLUMNS_EOF;

ALTER TABLE $dbName.mail_item
ADD INDEX i_type (type),
ADD INDEX i_parent_id (parent_id),
ADD INDEX i_folder_id (folder_id),
ADD INDEX i_sender (sender),
ADD INDEX i_subject (subject(128)),
ADD CONSTRAINT fk_parent_id FOREIGN KEY (parent_id) REFERENCES $dbName.mail_item(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_folder_id FOREIGN KEY (folder_id) REFERENCES $dbName.mail_item(id) ON DELETE CASCADE;

ALTER TABLE $dbName.open_conversation
ADD PRIMARY KEY (hash),
ADD INDEX i_conv_id (conv_id),
ADD CONSTRAINT fk_conv_id FOREIGN KEY (conv_id) REFERENCES $dbName.mail_item(id) ON DELETE CASCADE;
	
ALTER TABLE $dbName.appointment
ADD INDEX i_uid (uid),
ADD INDEX i_item_id (item_id),
ADD CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES $dbName.mail_item(id) ON DELETE CASCADE;
DROP_COLUMNS_EOF

    printLog("Adding constraints to tables in $dbName.");
    runSql($ROOT_USER, $ROOT_PASSWORD, $sql);
}	

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
