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

Migrate::verifySchemaVersion(84);

addApplListColumns();

Migrate::updateSchemaVersion(84, 85);

exit(0);

#####################

sub addApplListColumns() {
    my $sql = <<MOBILE_DEVICES_ADD_COLUMN_EOF;
ALTER TABLE mobile_devices ADD COLUMN unapproved_appl_list TEXT NULL;
ALTER TABLE mobile_devices ADD COLUMN approved_appl_list TEXT NULL;
MOBILE_DEVICES_ADD_COLUMN_EOF
    
    Migrate::log("Adding unapproved_appl_list and approved_appl_list column to ZIMBRA.MOBILE_DEVICES table.");
    Migrate::runSql($sql);
}
