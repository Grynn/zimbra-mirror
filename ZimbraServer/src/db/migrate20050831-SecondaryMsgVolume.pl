#!/usr/bin/perl

use strict;
use Migrate;

sub secondaryMessageVolume() {
	my $sql = <<END_OF_SQL;
ALTER TABLE volume DISABLE KEYS;
ALTER TABLE volume ADD COLUMN type TINYINT NOT NULL AFTER id;
UPDATE volume SET type=1 WHERE name NOT LIKE '%index%';
UPDATE volume SET type=10 WHERE name LIKE '%index%';
ALTER TABLE volume ENABLE KEYS;

ALTER TABLE current_volumes ADD COLUMN secondary_message_volume_id INTEGER UNSIGNED AFTER message_volume_id;
ALTER TABLE current_volumes ADD INDEX i_secondary_message_volume_id (secondary_message_volume_id);
ALTER TABLE current_volumes ADD CONSTRAINT
    fk_current_volumes_secondary_message_volume_id
    FOREIGN KEY (secondary_message_volume_id)
    REFERENCES volume(id);
END_OF_SQL
    Migrate::log("Removing MESSAGE_VOLUME_ID colume from zimbra.mailbox table");
    Migrate::runSql($sql);
}


#
# Main
#

my @mailboxIds = Migrate::getMailboxIds();

Migrate::verifySchemaVersion(17);
secondaryMessageVolume();
Migrate::updateSchemaVersion(17, 18);

exit(0);
