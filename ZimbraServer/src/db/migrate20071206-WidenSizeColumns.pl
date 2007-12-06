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

Migrate::verifySchemaVersion(49);
foreach my $group (Migrate::getMailboxGroups()) {
    widenSizeColumns($group);
}
Migrate::updateSchemaVersion(49, 50);

exit(0);

#####################

sub widenSizeColumns($) {
  my ($group) = @_;

  my $sql = <<ALTER_TABLE_EOF;
ALTER TABLE $group.mail_item MODIFY COLUMN size BIGINT UNSIGNED NOT NULL;
ALTER TABLE $group.revision MODIFY COLUMN size BIGINT UNSIGNED NOT NULL;
ALTER_TABLE_EOF

  Migrate::runSql($sql);
}
