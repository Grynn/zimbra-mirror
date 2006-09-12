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

my $CREATE_DB_SQL;

sub init();
sub addGroupIdColumn();
sub createMailboxGroup($);
sub copyData($);
sub dropDatabase($);
sub dropOrphans();

init();

Migrate::verifySchemaVersion(27);

addGroupIdColumn();

my @mailboxIds = Migrate::getMailboxIds();
foreach my $id (@mailboxIds) {
    my $oldDb = "mailbox$id";
    my $newDb = "mboxgroup$id";
    createMailboxGroup($newDb);
    copyData($id);
    dropDatabase($oldDb);
}

dropOrphans();

Migrate::updateSchemaVersion(27, 28);

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
}

sub addGroupIdColumn() {
    my $sql = <<_ADD_GROUP_ID_;
ALTER TABLE zimbra.mailbox
ADD COLUMN group_id INTEGER UNSIGNED NOT NULL AFTER id;

UPDATE zimbra.mailbox SET group_id = id;
_ADD_GROUP_ID_

    Migrate::log("Adding group_id column to zimbra.mailbox table.");
    Migrate::runSql($sql);
}

sub createMailboxGroup($) {
    my $db = shift;
    my $sql = $CREATE_DB_SQL;
    $sql =~ s/\${DATABASE_NAME}/$db/gm;
    Migrate::log("Creating mailbox group $db");
    Migrate::runSql($sql);
}

sub copyData($) {
    my $id = shift;
    my $oldDb = "mailbox$id";
    my $newDb = "mboxgroup$id";
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

sub dropDatabase($) {
    my $db = shift;
    Migrate::log("Dropping database $db");
    Migrate::runSql("DROP DATABASE $db");
}

sub dropOrphans() {
    my @orphans = runSql("SHOW DATABASES LIKE 'mailbox\%'");
    foreach my $db (@orphans) {
	dropDatabase($db);
    }
}
