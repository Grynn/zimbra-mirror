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
