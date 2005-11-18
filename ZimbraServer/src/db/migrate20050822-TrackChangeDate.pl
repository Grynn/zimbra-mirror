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
# Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
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
