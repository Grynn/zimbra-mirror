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
