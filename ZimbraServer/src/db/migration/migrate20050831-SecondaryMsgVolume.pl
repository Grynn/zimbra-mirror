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
