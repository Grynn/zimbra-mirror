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
use Migrate;


Migrate::verifySchemaVersion(18);

dropForeignKeys();
alterVolume();
alterCurrentVolumes();
alterMailbox();

my @mailboxIds = Migrate::getMailboxIds();
foreach my $id (@mailboxIds) {
    alterMailItem($id);
}

Migrate::updateSchemaVersion(18, 19);

exit(0);

#####################

sub dropForeignKeys() {
    my $sql = <<EOF;

ALTER TABLE mailbox
DROP FOREIGN KEY fk_mailbox_index_volume_id;

ALTER TABLE current_volumes
DROP FOREIGN KEY fk_current_volumes_message_volume_id,
DROP FOREIGN KEY fk_current_volumes_secondary_message_volume_id,
DROP FOREIGN KEY fk_current_volumes_index_volume_id;

EOF
    
    Migrate::log("Removing foreign keys referencing volume.id");
    Migrate::runSql($sql);
}

sub alterVolume() {
    Migrate::log("Updating volume table");

    my $sql = "ALTER TABLE volume" .
	" MODIFY id TINYINT UNSIGNED NOT NULL AUTO_INCREMENT," .
	" ADD compress_blobs BOOLEAN NOT NULL;";

    Migrate::runSql($sql);

    $sql = "SELECT value FROM config " .
	"WHERE name = 'store.compressBlobs'";
    my $oldValue = (Migrate::runSql($sql))[0];
    $oldValue = lc($oldValue);

    my $newValue;
    if ($oldValue eq "true") {
	$newValue = 1;
    } elsif ($oldValue eq "false") {
	$newValue = 0;
    } else {
	print("Unexpected value for store.compressBlobs: '" + $oldValue + "'\n");
	exit(1);
    }

    $sql = "UPDATE volume" .
        " SET compress_blobs = $newValue;";
    Migrate::runSql($sql);
}

sub alterCurrentVolumes() {
    Migrate::log("Updating current_volumes table");

    my $sql = <<EOF;

ALTER TABLE current_volumes
MODIFY message_volume_id TINYINT UNSIGNED NOT NULL,
MODIFY secondary_message_volume_id TINYINT UNSIGNED NULL,
MODIFY index_volume_id TINYINT UNSIGNED NOT NULL;

ALTER TABLE current_volumes
ADD CONSTRAINT fk_current_volumes_message_volume_id
    FOREIGN KEY (message_volume_id)
    REFERENCES volume(id),
ADD CONSTRAINT fk_current_volumes_secondary_message_volume_id
    FOREIGN KEY (secondary_message_volume_id)
    REFERENCES volume(id),
ADD CONSTRAINT fk_current_volumes_index_volume_id
    FOREIGN KEY (index_volume_id)
    REFERENCES volume(id);

EOF

    Migrate::runSql($sql);
}

sub alterMailbox() {
    Migrate::log("Updating mailbox table");

    my $sql = <<EOF;

ALTER TABLE mailbox
MODIFY index_volume_id TINYINT UNSIGNED NOT NULL;

ALTER TABLE mailbox
ADD CONSTRAINT fk_mailbox_index_volume_id
    FOREIGN KEY (index_volume_id) REFERENCES volume(id);

EOF

    Migrate::runSql($sql);
  }

sub alterMailItem($) {
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
    Migrate::log("Updating $dbName.mail_item");

    my $sql = <<EOF;

ALTER TABLE $dbName.mail_item
DROP FOREIGN KEY fk_parent_id,
DROP FOREIGN KEY fk_folder_id;

ALTER TABLE $dbName.open_conversation
DROP FOREIGN KEY fk_conv_id;

ALTER TABLE $dbName.appointment
DROP FOREIGN KEY fk_item_id;

ALTER TABLE $dbName.mail_item
ADD INDEX i_volume_id (volume_id);

ALTER TABLE $dbName.mail_item
ADD CONSTRAINT fk_mail_item_parent_id
    FOREIGN KEY (parent_id) REFERENCES $dbName.mail_item(id),
ADD CONSTRAINT fk_mail_item_folder_id
    FOREIGN KEY (folder_id) REFERENCES $dbName.mail_item(id),
ADD CONSTRAINT fk_mail_item_volume_id
    FOREIGN KEY (volume_id) REFERENCES zimbra.volume(id);

ALTER TABLE $dbName.open_conversation
ADD CONSTRAINT fk_open_conversation_conv_id
    FOREIGN KEY (conv_id) REFERENCES $dbName.mail_item(id)
    ON DELETE CASCADE;

ALTER TABLE $dbName.appointment
ADD CONSTRAINT fk_appointment_item_id
    FOREIGN KEY (item_id) REFERENCES $dbName.mail_item(id)
    ON DELETE CASCADE;

EOF

    Migrate::runSql($sql);
}
