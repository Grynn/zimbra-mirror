#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2009, 2010 Zimbra, Inc.
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.3 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(61);
foreach my $group (Migrate::getMailboxGroups()) {
    updateDataSourceItemTable($group);
}
Migrate::updateSchemaVersion(61, 62);

exit(0);

#####################

sub updateDataSourceItemTable() {
  my ($group) = @_;
  Migrate::logSql("Updating data_source_item table for ".$group.".");
  
  my $sql = <<CREATE_TABLE_EOF;
DROP TABLE IF EXISTS $group.data_source_item;
CREATE TABLE IF NOT EXISTS $group.data_source_item (
   mailbox_id     INTEGER UNSIGNED NOT NULL,
   data_source_id CHAR(36) NOT NULL,
   item_id        INTEGER UNSIGNED NOT NULL,
   folder_id      INTEGER UNSIGNED NOT NULL DEFAULT 0,
   remote_id      VARCHAR(255) BINARY NOT NULL,
   metadata       MEDIUMTEXT,
   
   PRIMARY KEY (mailbox_id, item_id),
   UNIQUE INDEX i_remote_id (mailbox_id, data_source_id, remote_id),   -- for reverse lookup
   CONSTRAINT fk_data_source_item_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id) ON DELETE CASCADE
) ENGINE = InnoDB;
CREATE_TABLE_EOF

  Migrate::runSql($sql);
}
