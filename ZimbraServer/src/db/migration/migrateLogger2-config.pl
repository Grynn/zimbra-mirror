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

addConfig();

#Migrate::updateLoggerSchemaVersion(1,2);

exit(0);

#####################

sub addConfig() {
    Migrate::log("Adding Config");

    my $sql = <<EOF;
DROP TABLE IF EXISTS config;
CREATE TABLE config (
	name        VARCHAR(255) NOT NULL PRIMARY KEY,
	value       TEXT,
	description TEXT,
	modified    TIMESTAMP
) ENGINE = MyISAM;
EOF

    Migrate::runLoggerSql($sql);

	$sql = <<EOF;
DELETE from zimbra_logger.config WHERE name = 'db.version';
INSERT into zimbra_logger.config (name,value) values ('db.version',2);
EOF
    Migrate::runLoggerSql($sql);
}
