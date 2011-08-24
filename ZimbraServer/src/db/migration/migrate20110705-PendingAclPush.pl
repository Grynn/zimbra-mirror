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

Migrate::verifySchemaVersion(82);

my $sqlStmt = <<_SQL_;
CREATE TABLE pending_acl_push (
   mailbox_id  INTEGER UNSIGNED NOT NULL,
   item_id     INTEGER UNSIGNED NOT NULL,
   date        BIGINT UNSIGNED NOT NULL,

   PRIMARY KEY (mailbox_id, item_id, date),
   CONSTRAINT fk_pending_acl_push_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE,
   INDEX i_date (date)
) ENGINE = InnoDB;
_SQL_

Migrate::runSql($sqlStmt);

Migrate::updateSchemaVersion(82, 83);

exit(0);
