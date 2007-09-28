#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2007 Zimbra, Inc.
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
my $concurrent = 10;

Migrate::verifySchemaVersion(39);

my @sql = ();
my $sqlStmt = <<_SQL_;
ALTER TABLE mailbox
ADD COLUMN last_backup_at INTEGER UNSIGNED AFTER tracking_imap,
ADD INDEX i_last_backup_at (last_backup_at, id);
_SQL_
push(@sql, $sqlStmt);
Migrate::runSqlParallel($concurrent, @sql);

Migrate::updateSchemaVersion(39, 40);

exit(0);
