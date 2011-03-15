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

Migrate::verifySchemaVersion(66);

addDeviceInformationColumns();

Migrate::updateSchemaVersion(66, 67);

exit(0);

#####################

sub addDeviceInformationColumns() {
    my $sql = <<MOBILE_DEVICES_ADD_COLUMN_EOF;
ALTER TABLE mobile_devices ADD COLUMN model VARCHAR(64);
ALTER TABLE mobile_devices ADD COLUMN imei VARCHAR(64);
ALTER TABLE mobile_devices ADD COLUMN friendly_name VARCHAR(512);
ALTER TABLE mobile_devices ADD COLUMN os VARCHAR(64);
ALTER TABLE mobile_devices ADD COLUMN os_language VARCHAR(64);
ALTER TABLE mobile_devices ADD COLUMN phone_number VARCHAR(64);
MOBILE_DEVICES_ADD_COLUMN_EOF
    
    Migrate::log("Adding device information columns to ZIMBRA.MOBILE_DEVICES table.");
    Migrate::runSql($sql);
}
