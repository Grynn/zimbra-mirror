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

Migrate::verifySchemaVersion(25);

addMailboxMetadataTable();
removeConfigColumn();

Migrate::updateSchemaVersion(25, 26);

exit(0);

#####################

sub addMailboxMetadataTable() {
    my $sql = <<CREATE_MAILBOX_METADATA_EOF;
CREATE TABLE zimbra.mailbox_metadata (
   mailbox_id  INTEGER UNSIGNED NOT NULL,
   section     VARCHAR(64) NOT NULL,       # e.g. "imap"
   metadata    MEDIUMTEXT,

   PRIMARY KEY (mailbox_id, section),

   CONSTRAINT fk_metadata_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE_MAILBOX_METADATA_EOF
    
    Migrate::log("Adding ZIMBRA.MAILBOX_METADATA table.");
    Migrate::runSql($sql);
}

sub removeConfigColumn() {
    my $sql = <<REMOVE_CONFIG_EOF;
ALTER TABLE zimbra.mailbox
DROP COLUMN config;

REMOVE_CONFIG_EOF
    
    Migrate::log("Removing CONFIG column from ZIMBRA.MAILBOX.");
    Migrate::runSql($sql);
}
