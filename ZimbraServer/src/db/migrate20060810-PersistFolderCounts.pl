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
