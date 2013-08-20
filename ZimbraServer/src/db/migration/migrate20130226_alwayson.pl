#!/usr/bin/perl
#
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2013 Zimbra Software, LLC.
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.4 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
#

use strict;
use Migrate;

########################################################################################################################

Migrate::verifySchemaVersion(92);

addItemcacheCheckpointColumn();
addCurrentSessionsTable();

Migrate::updateSchemaVersion(92, 100);

exit(0);

########################################################################################################################

sub addItemcacheCheckpointColumn() {
    Migrate::logSql("Adding ITEMCACHE_CHECKPOINT column to mailbox table...");
    my $sql = <<_EOF_;
ALTER TABLE mailbox ADD COLUMN itemcache_checkpoint INTEGER UNSIGNED NOT NULL DEFAULT 0;
_EOF_
  Migrate::runSql($sql);
}

sub addCurrentSessionsTable() {
    Migrate::logSql("Adding CURRENT_SESSIONS table...");
    my $sql = <<_EOF_;
CREATE TABLE IF NOT EXISTS current_sessions (
	id				INTEGER UNSIGNED NOT NULL,
	server_id		VARCHAR(127) NOT NULL,
	PRIMARY KEY (id, server_id)
) ENGINE = InnoDB;
_EOF_
  Migrate::runSql($sql);
}
