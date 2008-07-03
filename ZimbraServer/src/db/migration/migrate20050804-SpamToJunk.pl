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

my @mailboxIds = Migrate::getMailboxIds();

foreach my $id (@mailboxIds) {
    # Get last change checkpoint
    my @result = Migrate::runSql("SELECT change_checkpoint FROM mailbox WHERE id = $id");
    my $checkpoint = $result[0];

    renameJunkFolder($id, $checkpoint + 100);
    renameSpamToJunk($id, $checkpoint + 100);
    renameInbox($id, $checkpoint + 100);

    Migrate::runSql("UPDATE mailbox " .
		     "SET change_checkpoint = " . ($checkpoint + 101) .
		     " WHERE id = $id");
}

Migrate::updateSchemaVersion(12);

exit(0);

#####################

sub renameJunkFolder($$) {
    my ($mailboxId, $modMetadata) = @_;
    my $sql = <<EOF;
UPDATE mailbox$mailboxId.mail_item
SET subject = CONCAT(subject, id), mod_metadata = $modMetadata
WHERE type = 1
AND subject = 'junk'
AND parent_id = 1;
EOF
    
    Migrate::log("Renaming preexisting top-level Junk folder in mailbox$mailboxId");
    Migrate::runSql($sql);
}

sub renameSpamToJunk($$) {
    my ($mailboxId, $modMetadata) = @_;

    my $sql = <<EOF;
UPDATE mailbox$mailboxId.mail_item
SET subject = 'Junk', mod_metadata = $modMetadata
WHERE id = 4;
EOF

    Migrate::log("Renaming Spam folder to Junk in mailbox$mailboxId");
    Migrate::runSql($sql);
}

sub renameInbox($$) {
    my ($mailboxId, $modMetadata) = @_;
    my $sql = <<EOF;
UPDATE mailbox$mailboxId.mail_item
SET subject = 'Inbox', mod_metadata = $modMetadata
WHERE id = 2;
EOF

    Migrate::log("Renaming INBOX folder to Inbox in mailbox$mailboxId");
    Migrate::runSql($sql);
}
