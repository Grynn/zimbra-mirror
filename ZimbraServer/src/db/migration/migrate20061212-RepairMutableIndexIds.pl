#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2006 Zimbra, Inc.
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

repairMutableIndexIds();

exit(0);

#####################

sub repairMutableIndexIds($) {
  my ($group) = @_;
  my @groups = Migrate::getMailboxGroups();

  Migrate::verifySchemaVersion(34);

  my @sql = ();
  foreach my $group (@groups) {
    my $sql = <<REPAIR_INDEX_IDS_EOF;
UPDATE $group.mail_item
SET index_id = id
WHERE index_id IS NOT NULL AND index_id <> id AND (type <> 5 OR index_id = 0);
REPAIR_INDEX_IDS_EOF
    push(@sql, $sql);
  }
  Migrate::runSqlParallel($concurrent, @sql);

  Migrate::updateSchemaVersion(34, 35);
}
