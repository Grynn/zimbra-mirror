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

Migrate::verifySchemaVersion(23);

my @mailboxIds = Migrate::getMailboxIds();
foreach my $id (@mailboxIds) {
    createEmailedContactFolder($id);
}

Migrate::updateSchemaVersion(23, 24);

exit(0);

#####################

sub createEmailedContactFolder($) {
    my ($mailboxId) = @_;
    my $timestamp = time();
    my $sql = <<EOF_RENAME_EMAILED_CONTACT_FOLDER;
    
UPDATE mailbox$mailboxId.mail_item mi, zimbra.mailbox mbx
SET subject = "Emailed Contacts_1",
    mod_metadata = change_checkpoint + 100,
    mod_content = change_checkpoint + 100,
    change_checkpoint = change_checkpoint + 200
WHERE subject = "Emailed Contacts" AND folder_id = 1 AND mi.id != 13 AND mbx.id = $mailboxId;

EOF_RENAME_EMAILED_CONTACT_FOLDER
    Migrate::runSql($sql);

    my $sql = <<EOF_CREATE_EMAILED_CONTACT_FOLDER;
    
INSERT INTO mailbox$mailboxId.mail_item
  (subject, id, type, parent_id, folder_id, mod_metadata, mod_content, metadata, date, change_date)
VALUES
  ("Emailed Contacts", 13, 1, 1, 1, 1, 1, "d1:ai1e1:vi9e2:vti6ee", $timestamp, $timestamp)
ON DUPLICATE KEY UPDATE id = 13;

UPDATE mailbox$mailboxId.mail_item mi, zimbra.mailbox mbx
SET mod_metadata = change_checkpoint + 100,
    mod_content = change_checkpoint + 100,
    change_checkpoint = change_checkpoint + 200
WHERE mi.id = 13 AND mbx.id = $mailboxId;

EOF_CREATE_EMAILED_CONTACT_FOLDER
    Migrate::runSql($sql);
}
