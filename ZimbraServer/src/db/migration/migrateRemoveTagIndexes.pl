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
use lib "/opt/zimbra/zimbramon/lib";
use Migrate;

Migrate::loadOutdatedMailboxes("2.2");
removeTagIndexes();

exit(0);

#####################

sub removeTagIndexes() {
  Migrate::log("dropping tag indexes");

  my @groups = Migrate::getMailboxGroups();
  foreach my $group (@groups) {
    # mboxgroup DBs created since the upgrade won't have the indexes, so test before dropping
    my $sql = <<CHECK_INDEXES_EOF;
SHOW INDEXES IN $group.mail_item WHERE Key_name = 'i_unread';
CHECK_INDEXES_EOF
    my @indexes = Migrate::runSql($sql);

    if (scalar(@indexes) > 0) {
      $sql = <<DROP_INDEXES_EOF;
ALTER TABLE $group.mail_item DROP INDEX i_unread, DROP INDEX i_tags_date, DROP INDEX i_flags_date;
DROP_INDEXES_EOF
      Migrate::runSql($sql);
    } else {
      Migrate::log("$group.MAIL_ITEM tag indexes already dropped");
    }

    $sql = <<CHECK_DUMPSTER_INDEXES_EOF;
SHOW INDEXES IN $group.mail_item_dumpster WHERE Key_name = 'i_unread';
CHECK_DUMPSTER_INDEXES_EOF
    @indexes = Migrate::runSql($sql);

    if (scalar(@indexes) > 0) {
      $sql = <<DROP_DUMPSTER_INDEXES_EOF;
ALTER TABLE $group.mail_item_dumpster DROP INDEX i_unread, DROP INDEX i_tags_date, DROP INDEX i_flags_date;
DROP_DUMPSTER_INDEXES_EOF
      Migrate::runSql($sql);
    } else {
      Migrate::log("$group.MAIL_ITEM_DUMPSTER tag indexes already dropped");
    }
  }
}
