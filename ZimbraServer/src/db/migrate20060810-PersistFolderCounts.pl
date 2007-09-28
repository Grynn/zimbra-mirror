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

Migrate::verifySchemaVersion(26);

my @mailboxIds = Migrate::getMailboxIds();
my $sql = addContactCountColumn();
foreach my $id (@mailboxIds) {
    $sql .= resizeUnreadColumn($id);
}

Migrate::runSql($sql);

Migrate::updateSchemaVersion(26, 27);

exit(0);

#####################

sub addContactCountColumn() {
    my $sql = <<ADD_CONTACT_COUNT_COLUMN_EOF;
ALTER TABLE zimbra.mailbox
ADD COLUMN contact_count INTEGER UNSIGNED DEFAULT 0 AFTER item_id_checkpoint;

UPDATE zimbra.mailbox
SET contact_count = NULL;

ADD_CONTACT_COUNT_COLUMN_EOF

    return $sql;
}

sub resizeUnreadColumn($) {
    my ($mailboxId) = @_;
    my $sql = <<RESIZE_UNREAD_COLUMN_EOF;
ALTER TABLE mailbox$mailboxId.mail_item
MODIFY COLUMN unread INTEGER UNSIGNED;

RESIZE_UNREAD_COLUMN_EOF

    return $sql;
}
