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

Migrate::verifySchemaVersion(36);
foreach my $group (Migrate::getMailboxGroups()) {
    modifyPop3MessageSchema($group);
}
Migrate::updateSchemaVersion(36, 37);

exit(0);

#####################

sub modifyPop3MessageSchema($) {
  my ($group) = @_;

  my $sql = <<MODIFY_POP3_MESSAGE_SCHEMA_EOF;
ALTER TABLE $group.pop3_message
CHANGE uid uid VARCHAR(255) BINARY NOT NULL;
MODIFY_POP3_MESSAGE_SCHEMA_EOF

  Migrate::runSql($sql);
}
