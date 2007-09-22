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
# Portions created by Zimbra are Copyright (C) 2007 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(46);
foreach my $group (Migrate::getMailboxGroups()) {
    alterImapFolderSchema($group);
}
Migrate::updateSchemaVersion(46, 47);

exit(0);

#####################

sub alterImapFolderSchema($) {
  my ($group) = @_;
  my $table = $group . ".imap_folder";

  # The DELETE statement removes bogus folder trackers
  # (remote_path prefixed by /) that were discovered when fixing bug 19108.

  my $sql = <<ALTER_TABLE_EOF;
DELETE FROM $table
WHERE remote_path like '/%';

ALTER TABLE $table
ADD COLUMN uid_validity INTEGER UNSIGNED;

CREATE UNIQUE INDEX i_local_path
ON $table (local_path(200), data_source_id, mailbox_id);

CREATE UNIQUE INDEX i_remote_path
ON $table (remote_path(200), data_source_id, mailbox_id);
ALTER_TABLE_EOF

  Migrate::runSql($sql);
}
