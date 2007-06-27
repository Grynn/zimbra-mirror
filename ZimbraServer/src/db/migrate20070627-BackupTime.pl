#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Version: MPL 1.1
# 
# The contents of this file are subject to the Mozilla Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite Server.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2007 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
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
