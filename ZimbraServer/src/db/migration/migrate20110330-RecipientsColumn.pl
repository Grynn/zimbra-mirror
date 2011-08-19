#!/usr/bin/perl
#
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2011 Zimbra, Inc.
#
# The contents of this file are subject to the Zimbra Public License
# Version 1.3 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
#
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
#

use strict;
use Migrate;

########################################################################################################################

Migrate::verifySchemaVersion(67);

foreach my $group (Migrate::getMailboxGroups()) {
  addRecipientsColumn($group);
}

Migrate::updateSchemaVersion(67, 68);

exit(0);

########################################################################################################################

sub addRecipientsColumn($) {
  my ($group) = @_;

  my $sql = <<_EOF_;
ALTER TABLE $group.mail_item ADD COLUMN recipients VARCHAR(128) AFTER sender;
ALTER TABLE $group.mail_item_dumpster ADD COLUMN recipients VARCHAR(128) AFTER sender;
_EOF_

  Migrate::logSql("Adding RECIPIENTS column to $group.MAIL_ITEM and $group.MAIL_ITEM_DUMPSTER...");
  Migrate::runSql($sql);
}
