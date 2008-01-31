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

Migrate::verifySchemaVersion(50);
foreach my $group (Migrate::getMailboxGroups()) {
    addImapFlagsColumn($group);
}
Migrate::updateSchemaVersion(50, 51);

exit(0);

#####################

sub addImapFlagsColumn($) {
  my ($group) = @_;
  
  Migrate::log("Adding flags column to $group.imap_message.");

  my $sql = <<ALTER_TABLE_EOF;
ALTER TABLE $group.imap_message
ADD COLUMN flags INTEGER NOT NULL DEFAULT 0;

UPDATE $group.imap_message
SET flags = (
  SELECT flags
  FROM $group.mail_item
  WHERE $group.imap_message.item_id = $group.mail_item.id
);

ALTER_TABLE_EOF

  Migrate::runSql($sql);
}
