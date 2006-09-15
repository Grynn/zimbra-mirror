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

$sql = dropOrphans();
print SQL $sql;
print SQL "\n";

close(SQL);
print "Executing SQL statements in $sqlfile\n";
my $rc = system("/opt/zimbra/bin/mysql -v -A zimbra < $sqlfile");
$rc >>= 8;
if ($rc != 0) {
    die "mysql invication failed, exit code = $rc: $!";
}
print "Successfully finished executing SQL statements in $sqlfile\n";
#unlink($sqlfile);

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
