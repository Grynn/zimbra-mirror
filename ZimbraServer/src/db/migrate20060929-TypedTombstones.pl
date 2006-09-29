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

Migrate::verifySchemaVersion(28);

my @groups = Migrate::getMailboxGroups();
my $sql = "";
foreach my $group (@groups) {
    $sql .= addTombstoneTypeColumn($group);
}

Migrate::runSql($sql);

Migrate::updateSchemaVersion(28, 29);

exit(0);

#####################

sub addTombstoneTypeColumn($) {
    my ($group) = @_;
    my $sql = <<ADD_TYPE_COLUMN_EOF;
ALTER TABLE $group.tombstone
ADD COLUMN type TINYINT AFTER date;

ADD_TYPE_COLUMN_EOF

    return $sql;
}
