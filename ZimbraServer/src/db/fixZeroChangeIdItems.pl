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

my @mailboxIds = Migrate::getMailboxIds();
foreach my $id (@mailboxIds) {
    fixZeroChangeIdItems($id);
}

exit(0);

#####################

sub fixZeroChangeIdItems($) {
    my ($mailboxId) = @_;
    my $sql = <<EOF_FIX_ZERO_SEQUENCE_NUMBERS;

UPDATE mailbox$mailboxId.mail_item
SET mod_content = 1
WHERE mod_content = 0;

UPDATE mailbox$mailboxId.mail_item
SET mod_metadata = 1
WHERE mod_metadata = 0;

EOF_FIX_ZERO_SEQUENCE_NUMBERS

    Migrate::runSql($sql);
}
