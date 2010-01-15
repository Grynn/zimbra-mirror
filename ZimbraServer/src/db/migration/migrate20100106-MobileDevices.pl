#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2010 Zimbra, Inc.
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.2 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(63);

addPolicyValuesColumn();

Migrate::updateSchemaVersion(63, 64);

exit(0);

#####################

sub addPolicyValuesColumn() {
    my $sql = <<MOBILE_DEVICES_ADD_COLUMN_EOF;
ALTER TABLE mobile_devices ADD COLUMN policy_values VARCHAR(512);
MOBILE_DEVICES_ADD_COLUMN_EOF
    
    Migrate::log("Adding policy_values column to ZIMBRA.MOBILE_DEVICES table.");
    Migrate::runSql($sql);
}
