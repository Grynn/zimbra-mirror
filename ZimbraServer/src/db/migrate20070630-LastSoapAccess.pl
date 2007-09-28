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

Migrate::verifySchemaVersion(41);

my $sqlStmt = <<_SQL_;
ALTER TABLE zimbra.mailbox
ADD COLUMN last_soap_access INTEGER UNSIGNED NOT NULL DEFAULT 0 AFTER comment,
ADD COLUMN new_messages INTEGER UNSIGNED NOT NULL DEFAULT 0 AFTER last_soap_access;
_SQL_

Migrate::runSql($sqlStmt);

Migrate::updateSchemaVersion(41, 42);

exit(0);
