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
my $concurrent = 10;
########################################################################################################################

Migrate::verifySchemaVersion(81);

addRecipientsColumn();

Migrate::updateSchemaVersion(81, 82);

exit(0);

########################################################################################################################

sub addRecipientsColumn() {
  my @groups = Migrate::getMailboxGroups();
  my @sql = ();
  foreach my $group (@groups) {
    my $sql = <<_EOF_;
ALTER TABLE $group.mail_item ADD COLUMN recipients VARCHAR(128) AFTER sender;
_EOF_
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);

  @sql = ();
  foreach my $group (@groups) {
    my $sql = <<_EOF_;
ALTER TABLE $group.mail_item_dumpster ADD COLUMN recipients VARCHAR(128) AFTER sender;
_EOF_
    push(@sql,$sql);
  }
  Migrate::runSqlParallel($concurrent,@sql);
}
