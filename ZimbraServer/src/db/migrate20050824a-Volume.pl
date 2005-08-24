#!/usr/bin/perl

use strict;
use Migrate;

sub removeMessageVolumeIdColumn() {
	my $sql = <<END_OF_SQL;
ALTER TABLE mailbox DROP FOREIGN KEY fk_mailbox_message_volume_id;
ALTER TABLE mailbox DROP COLUMN message_volume_id;
END_OF_SQL
    Migrate::log("Removing MESSAGE_VOLUME_ID colume from zimbra.mailbox table");
    Migrate::runSql($sql);
}

# Set mail_item.volume_id column to NULL for items that don't use
# a blob.
sub nullifyVolumeOnBloblessItems($) {
    my ($mailboxId) = @_;
    my $sql = "UPDATE mailbox$mailboxId.mail_item SET volume_id = NULL WHERE type NOT IN (5, 6, 7, 8, 9, 11);";
    Migrate::log("Setting VOLUME_ID column to NULL for blobless items in mailbox$mailboxId.mail_item");
    Migrate::runSql($sql);
}


#
# Main
#

my @mailboxIds = Migrate::getMailboxIds();

Migrate::verifySchemaVersion(16);
removeMessageVolumeIdColumn();
foreach my $id (@mailboxIds) {
    nullifyVolumeOnBloblessItems($id);
}
Migrate::updateSchemaVersion(16, 17);

exit(0);
