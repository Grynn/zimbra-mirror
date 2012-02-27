#!/usr/bin/perl
#
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2011 Zimbra, Inc.
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

Migrate::verifySchemaVersion(87);

addLastPurgeAtColumn();

Migrate::updateSchemaVersion(87, 88);

exit(0);

#####################

sub addLastPurgeAtColumn() {
    my $sql = <<MAILBOX_ADD_COLUMN_EOF;
ALTER TABLE mailbox ADD COLUMN last_purge_at INTEGER UNSIGNED NOT NULL DEFAULT 0;
MAILBOX_ADD_COLUMN_EOF
    
    Migrate::log("Adding last_purge_at column to ZIMBRA.MAILBOX table.");
    Migrate::runSql($sql);
}