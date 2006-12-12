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
# Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
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
