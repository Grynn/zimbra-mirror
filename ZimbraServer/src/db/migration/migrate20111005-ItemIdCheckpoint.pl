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

my %mailboxes = getBadMailboxes();

foreach my $mboxId (sort(keys %mailboxes)) {
    my $gid = $mailboxes{$mboxId};
    Migrate::log("Bad item_id_checkpoint in mailbox $mboxId in group $gid");
    #find largest id < 1073741824
    my @newChkptRes = Migrate::runSql("SELECT max(id)+1000 from mboxgroup$gid.mail_item where mailbox_id = $mboxId and id < 1073741824");
    my $newChkpt = @newChkptRes[0];
	if ($newChkpt >= 1063741824) {
		#not enough room between new checkpoint and upper limit; mbox is in really bad shape
        Migrate::log("New checkpoint ($newChkpt) too close to upper limit; mailbox may have naturally exceeded limit. Please manually export, delete, recreate, and then import this mailbox");
	} else {
        Migrate::log("Updating to new checkpoint $newChkpt");
		Migrate::runSql("UPDATE mailbox set item_id_checkpoint=$newChkpt where id=$mboxId");
        Migrate::log("Fixed mailbox $mboxId in group $gid");
	}
}

exit(0);

########################################################################################################################


sub getBadMailboxes() {
  my @rows = Migrate::runSql("SELECT id,group_id from mailbox where item_id_checkpoint >= 1073741824");
  my %toRet;
  foreach my $row (@rows) {
    if ($row =~ /([^\t\s]+)\t+([^\t\s]+)/) {
      $toRet{$1} = $2;
    }
  }
  return %toRet;
}
