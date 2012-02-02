#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2012 Zimbra, Inc.
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

sub doIt();

Migrate::verifySchemaVersion(86);
doIt();
Migrate::updateSchemaVersion(86, 87);

exit(0);

#####################

sub doIt() {
  Migrate::logSql("Adding uuid column and widening blob_digest column.");
  my @sqls;
  foreach my $group (Migrate::getMailboxGroups()) {
    my $sql;
    $sql = <<_EOF_;
ALTER TABLE $group.mail_item
  MODIFY COLUMN blob_digest VARCHAR(44) BINARY,
  ADD COLUMN uuid VARCHAR(127) AFTER mod_content,
  ADD INDEX i_uuid (mailbox_id, uuid);
_EOF_
    push(@sqls,$sql);

    $sql = <<_EOF_;
ALTER TABLE $group.mail_item_dumpster
  MODIFY COLUMN blob_digest VARCHAR(44) BINARY,
  ADD COLUMN uuid VARCHAR(127) AFTER mod_content,
  ADD INDEX i_uuid (mailbox_id, uuid);
_EOF_
    push(@sqls,$sql);

    $sql = <<_EOF_;
ALTER TABLE $group.revision
  MODIFY COLUMN blob_digest VARCHAR(44) BINARY;
_EOF_
    push(@sqls,$sql);

    $sql = <<_EOF_;
ALTER TABLE $group.revision_dumpster
  MODIFY COLUMN blob_digest VARCHAR(44) BINARY;
_EOF_
    push(@sqls,$sql);
  }

  my $concurrency = 10;
  Migrate::runSqlParallel($concurrency, @sqls);
}
