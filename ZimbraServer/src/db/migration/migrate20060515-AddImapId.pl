#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2006 Zimbra, Inc.
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

Migrate::verifySchemaVersion(22);

my @mailboxIds = Migrate::getMailboxIds();
addImapSyncColumn();
foreach my $id (@mailboxIds) {
    addImapIdColumn($id);
}

Migrate::updateSchemaVersion(22, 23);

exit(0);

#############

sub addImapSyncColumn()
{
    my $sql = <<ADD_TRACKING_IMAP_COLUMN_EOF;
ALTER TABLE zimbra.mailbox
MODIFY tracking_sync INTEGER UNSIGNED NOT NULL DEFAULT 0;

ALTER TABLE zimbra.mailbox
ADD COLUMN tracking_imap BOOLEAN NOT NULL DEFAULT 0 AFTER tracking_sync;

ADD_TRACKING_IMAP_COLUMN_EOF

    Migrate::log("Adding zimbra.mailbox.tracking_imap.");
    Migrate::runSql($sql);
}

sub addImapIdColumn($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<ADD_IMAP_ID_COLUMN_EOF;
ALTER TABLE $dbName.mail_item
ADD COLUMN imap_id INTEGER UNSIGNED AFTER folder_id;

UPDATE $dbName.mail_item
SET imap_id = id WHERE type IN (5, 6, 8, 11, 14);

ADD_IMAP_ID_COLUMN_EOF

    Migrate::log("Adding and setting $dbName.mail_item.imap_id.");
    Migrate::runSql($sql);
}

