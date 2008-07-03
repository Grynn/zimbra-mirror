#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2005 Zimbra, Inc.
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


#Migrate::verifyLoggerSchemaVersion(0);

addIndices();

#Migrate::updateLoggerSchemaVersion(0,1);

exit(0);

#####################

sub addIndices() {
    Migrate::log("Adding Indices");

    my $sql = <<EOF;
alter table mta add index i_arrive_time (arrive_time);
alter table amavis add index i_arrive_time (arrive_time);
alter table mta_aggregate add index i_period_start (period_start);
alter table mta_aggregate add index i_period_end (period_end);
alter table amavis_aggregate add index i_period_start (period_start);
alter table amavis_aggregate add index i_period_end (period_end); 
CREATE TABLE config (
	name        VARCHAR(255) NOT NULL PRIMARY KEY,
	value       TEXT,
	description TEXT,
	modified    TIMESTAMP
) ENGINE = MyISAM;
EOF

    Migrate::runLoggerSql($sql);
}
