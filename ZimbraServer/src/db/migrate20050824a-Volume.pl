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
# Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 


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
