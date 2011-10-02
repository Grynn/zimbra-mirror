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

Migrate::verifySchemaVersion(85);

addVersionColumn();

Migrate::updateSchemaVersion(85, 86);

exit(0);

#####################

sub addVersionColumn() {
    my $sql = <<MAILBOX_ADD_COLUMN_EOF;
ALTER TABLE mailbox ADD COLUMN version VARCHAR(16);
MAILBOX_ADD_COLUMN_EOF
    
    Migrate::log("Adding version column to ZIMBRA.MAILBOX table.");
    Migrate::runSql($sql);
}
