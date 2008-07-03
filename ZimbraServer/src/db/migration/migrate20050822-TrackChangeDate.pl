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

my @mailboxIds = Migrate::getMailboxIds();

Migrate::verifySchemaVersion(15);
foreach my $id (@mailboxIds) {
    addChangeDateColumn($id);
}
Migrate::updateSchemaVersion(15, 16);

exit(0);

#####################

sub addChangeDateColumn($) {
    my ($mailboxId) = @_;
    my $sql = <<EOF;
ALTER TABLE mailbox$mailboxId.mail_item
ADD COLUMN change_date INTEGER UNSIGNED AFTER mod_metadata;

UPDATE mailbox$mailboxId.mail_item SET change_date = date;

EOF
    
    Migrate::log("Adding CHANGE_DATE column to mailbox$mailboxId.mail_item.");
    Migrate::runSql($sql);
}
