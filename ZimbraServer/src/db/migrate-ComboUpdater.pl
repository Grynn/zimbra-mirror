#!/usr/bin/perl
#
# ***** BEGINN LICENSE BLOCK *****
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
# Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
# All Rights Reserved.
#
# Contributor(s):
#
# ***** END LICENSE BLOCK *****
#

use strict;
no strict "refs";
use Migrate;

my @mailboxIds = Migrate::getMailboxIds();
my $timestamp = time();
my $sql;

my $curSchemaVersion = Migrate::getSchemaVersion();
my $beginSchemaVersion = $curSchemaVersion;

while ($curSchemaVersion >= 20 && $curSchemaVersion < 27) {
  $sql .= &{"schema${curSchemaVersion}"};
  $curSchemaVersion++;
}

Migrate::runSql($sql);

Migrate::updateSchemaVersion($beginSchemaVersion,27);

exit(0);

sub schema20 {
  my $sql = "drop table if exists redolog_sequence;\n";
  return $sql;
}

sub schema21 {
  my $sql;
  foreach my $id (@mailboxIds) {
    $sql .= <<EOF_SQL;
UPDATE mailbox$id.mail_item SET subject = "Notebook1" WHERE subject = "Notebook" AND folder_id = 1 AND id != 12;
INSERT INTO mailbox$id.mail_item (subject, id, type, parent_id, folder_id, mod_metadata, mod_content, metadata, date, change_date) VALUES ("Notebook", 12, 1, 1, 1, 1, 1, "d1:ai1e1:vi9e2:vti14ee", $timestamp, $timestamp) ON DUPLICATE KEY UPDATE id = 12;
UPDATE mailbox$id.mail_item mi, zimbra.mailbox mbx SET mod_metadata = change_checkpoint + 100, mod_content = change_checkpoint + 100, change_checkpoint = change_checkpoint + 200 WHERE mi.id = 12 AND mbx.id = $id;
EOF_SQL
    #print "$id -> $sql\n\n\n";
  }
  print "schema22: $sql\n\n\n";
  return $sql;
}

sub schema22 {
    my $sql = <<ADD_TRACKING_IMAP_COLUMN_EOF;
ALTER TABLE zimbra.mailbox MODIFY tracking_sync INTEGER UNSIGNED NOT NULL DEFAULT 0;
ALTER TABLE zimbra.mailbox ADD COLUMN tracking_imap BOOLEAN NOT NULL DEFAULT 0 AFTER tracking_sync;
ADD_TRACKING_IMAP_COLUMN_EOF

  foreach my $id (@mailboxIds) {
    my $dbName = "mailbox" . $id;
    $sql .= <<ADD_IMAP_ID_COLUMN_EOF;
ALTER TABLE $dbName.mail_item ADD COLUMN imap_id INTEGER UNSIGNED AFTER folder_id;
UPDATE $dbName.mail_item SET imap_id = id WHERE type IN (5, 6, 8, 11, 14);
ADD_IMAP_ID_COLUMN_EOF
  }
  return $sql;
}

sub schema23 {

  my $sql;

  foreach my $id (@mailboxIds) {
    $sql .= <<EOF_SQL;
UPDATE mailbox$id.mail_item mi, zimbra.mailbox mbx SET subject = "Emailed Contacts_1", mod_metadata = change_checkpoint + 100, mod_content = change_checkpoint + 100, change_checkpoint = change_checkpoint + 200 WHERE subject = "Emailed Contacts" AND folder_id = 1 AND mi.id != 13 AND mbx.id = $id;
EOF_SQL
    $sql .= <<EOF_CREATE_EMAILED_CONTACT_FOLDER;
INSERT INTO mailbox$id.mail_item (subject, id, type, parent_id, folder_id, mod_metadata, mod_content, metadata, date, change_date) VALUES ("Emailed Contacts", 13, 1, 1, 1, 1, 1, "d1:ai1e1:vi9e2:vti6ee", $timestamp, $timestamp) ON DUPLICATE KEY UPDATE id = 13;
UPDATE mailbox$id.mail_item mi, zimbra.mailbox mbx SET mod_metadata = change_checkpoint + 100, mod_content = change_checkpoint + 100, change_checkpoint = change_checkpoint + 200 WHERE mi.id = 13 AND mbx.id = $id;
EOF_CREATE_EMAILED_CONTACT_FOLDER
  }
  return $sql;

}

sub schema24 {
  my $sql;
  foreach my $id (@mailboxIds) {
    $sql .= <<EOF_SET_CHECKED_CALENDAR_FLAG;
UPDATE mailbox$id.mail_item mi, zimbra.mailbox mbx SET flags = flags | 2097152, mod_metadata = change_checkpoint + 100, change_checkpoint = change_checkpoint + 200 WHERE mi.id = 10 AND mbx.id = $id;
EOF_SET_CHECKED_CALENDAR_FLAG
  }
  return $sql;
}

sub schema25 {
  my $sql;
  $sql .= <<CREATE_MAILBOX_METADATA_EOF;
CREATE TABLE zimbra.mailbox_metadata ( mailbox_id  INTEGER UNSIGNED NOT NULL, section     VARCHAR(64) NOT NULL, metadata    MEDIUMTEXT, PRIMARY KEY (mailbox_id, section), CONSTRAINT fk_metadata_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE) ENGINE = InnoDB;
CREATE_MAILBOX_METADATA_EOF

  $sql .= <<REMOVE_CONFIG_EOF;
ALTER TABLE zimbra.mailbox DROP COLUMN config;
REMOVE_CONFIG_EOF
  return $sql;
}

sub schema26 {
  my $sql;
  $sql .= <<ADD_CONTACT_COUNT_COLUMN_EOF;
ALTER TABLE zimbra.mailbox ADD COLUMN contact_count INTEGER UNSIGNED DEFAULT 0 AFTER item_id_checkpoint;
UPDATE zimbra.mailbox SET contact_count = NULL;
ADD_CONTACT_COUNT_COLUMN_EOF
  foreach my $id (@mailboxIds) {
    $sql .= <<RESIZE_UNREAD_COLUMN_EOF;
ALTER TABLE mailbox$id.mail_item MODIFY COLUMN unread INTEGER UNSIGNED;
RESIZE_UNREAD_COLUMN_EOF
  }

  return $sql;
}
