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

Migrate::verifySchemaVersion(24);

my @mailboxIds = Migrate::getMailboxIds();
foreach my $id (@mailboxIds) {
    setCheckedCalendarFlag($id);
}

Migrate::updateSchemaVersion(24, 25);

exit(0);

#####################

sub setCheckedCalendarFlag($) {
    my ($mailboxId) = @_;
    my $sql = <<EOF_SET_CHECKED_CALENDAR_FLAG;
    
UPDATE mailbox$mailboxId.mail_item mi, zimbra.mailbox mbx
SET flags = flags | 2097152,
    mod_metadata = change_checkpoint + 100,
    change_checkpoint = change_checkpoint + 200
WHERE mi.id = 10 AND mbx.id = $mailboxId;

EOF_SET_CHECKED_CALENDAR_FLAG
    Migrate::runSql($sql);
}
