#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2005 Zimbra, Inc.
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

Migrate::verifySchemaVersion(14);

my @mailboxIds = Migrate::getMailboxIds();

foreach my $id (@mailboxIds) {
    addIndexes($id);
}

Migrate::updateSchemaVersion(14, 15);

exit(0);

#####################

sub addIndexes($) {
    my ($mailboxId) = @_;
    my $sql = <<EOF;
ALTER TABLE mailbox$mailboxId.mail_item
ADD INDEX i_tags_date (tags, date),
ADD INDEX i_flags_date (flags, date);
EOF
    
    Migrate::runSql($sql);
}
