#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2009 Zimbra, Inc.
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

Migrate::verifySchemaVersion(62);
addMailboxIndexDeferredCountColumn();
Migrate::updateSchemaVersion(62, 63);

exit(0);

#####################

sub addMailboxIndexDeferredCountColumn() {
  Migrate::log("Adding idx_deferred_count column to Mailbox table.");
  
  my $sql = <<ALTER_TABLE_EOF;
ALTER TABLE zimbra.mailbox ADD COLUMN highest_indexed VARCHAR(21);
ALTER_TABLE_EOF
  
  Migrate::runSql($sql);
}
