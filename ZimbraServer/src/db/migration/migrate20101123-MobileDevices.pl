#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2010 Zimbra, Inc.
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

Migrate::verifySchemaVersion(65);

addLastUsedDateColumn();

Migrate::updateSchemaVersion(65, 66);

exit(0);

#####################

sub addLastUsedDateColumn() {
    my $sql = <<MOBILE_DEVICES_ADD_COLUMN_EOF;
ALTER TABLE mobile_devices ADD COLUMN last_used_date DATE;
ALTER TABLE mobile_devices ADD COLUMN deleted_by_user BOOLEAN NOT NULL DEFAULT 0 AFTER last_used_date;
ALTER TABLE mobile_devices ADD INDEX i_last_used_date (last_used_date);
MOBILE_DEVICES_ADD_COLUMN_EOF
    
    Migrate::log("Adding last_used_date and deleted_by_user column to ZIMBRA.MOBILE_DEVICES table.");
    Migrate::runSql($sql);
}
