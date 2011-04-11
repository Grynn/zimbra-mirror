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
  createMailAddressTable($group);
}

Migrate::updateSchemaVersion(67, 68);

exit(0);

########################################################################################################################

sub createMailAddressTable($) {
  my ($group) = @_;

  my $sql = <<_EOF_;
CREATE TABLE $group.mail_address (
   mailbox_id    INTEGER UNSIGNED NOT NULL,
   id            INTEGER UNSIGNED NOT NULL,
   address       VARCHAR(128) NOT NULL,
   contact_count INTEGER NOT NULL,

   PRIMARY KEY (mailbox_id, id),
   UNIQUE INDEX i_mail_address_address (mailbox_id, address),
   CONSTRAINT fk_mail_address_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id) ON DELETE CASCADE
) ENGINE = InnoDB;
ALTER TABLE $group.mail_item ADD COLUMN sender_id INTEGER UNSIGNED DEFAULT NULL;
ALTER TABLE $group.mail_item ADD CONSTRAINT fk_mail_item_sender_id
  FOREIGN KEY (mailbox_id, sender_id) REFERENCES $group.mail_address(mailbox_id, id);
_EOF_

  Migrate::log("Creating $group.MAIL_ADDRESS table...");
  Migrate::runSql($sql);
}
