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

Migrate::verifySchemaVersion(32);
foreach my $group (Migrate::getMailboxGroups()) {
    createPop3MessageTable($group);
}
Migrate::updateSchemaVersion(32, 33);

exit(0);

#####################

sub createPop3MessageTable($) {
  my ($group) = @_;

  my $sql = <<CREATE_TABLE_EOF;
CREATE TABLE IF NOT EXISTS $group.pop3_message (
   mailbox_id     INTEGER UNSIGNED NOT NULL,
   data_source_id CHAR(36) NOT NULL,
   uid            VARCHAR(255) NOT NULL,
   item_id        INTEGER UNSIGNED NOT NULL,
   
   PRIMARY KEY (mailbox_id, item_id),
   CONSTRAINT fk_pop3_message_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id)
) ENGINE = InnoDB;

CREATE UNIQUE INDEX i_uid_pop3_id ON $group.pop3_message (uid, data_source_id);
CREATE_TABLE_EOF

  Migrate::runSql($sql);
}
