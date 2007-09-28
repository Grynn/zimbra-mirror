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

Migrate::verifySchemaVersion(35);

resetFolderCounts();

Migrate::updateSchemaVersion(35, 36);

exit(0);

#####################

sub resetFolderCounts() {
    my $sql = <<RESET_CONTACT_COUNT_EOF;
UPDATE zimbra.mailbox
SET contact_count = NULL;

RESET_CONTACT_COUNT_EOF

    Migrate::runSql($sql);
}
