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

Migrate::verifySchemaVersion(29);

my %mailboxes = Migrate::getMailboxes();

foreach my $cur (sort(keys %mailboxes)) {
  createIMsFolder("mboxgroup".$mailboxes{$cur}, $cur);
}

Migrate::updateSchemaVersion(29, 30);

exit(0);


#####################

sub createIMsFolder() {
    my ($databaseName, $mailboxId) = (@_);
    my $timestamp = time();
    my $sql = <<EOF_CREATE_IMS_FOLDER;
    
UPDATE $databaseName.mail_item
SET subject = "IMs1"
WHERE subject = "IMs" AND folder_id = 1 AND id != 14 AND mailbox_id = $mailboxId;

EOF_CREATE_IMS_FOLDER

    Migrate::runSql($sql);
    
    my $sql = <<EOF_CREATE_IMS_FOLDER;
    
INSERT INTO $databaseName.mail_item
  (mailbox_id, subject, id, type, parent_id, folder_id, mod_metadata, mod_content, metadata, date, change_date)
VALUES
  ($mailboxId, "IMs", 14, 1, 1, 1, 1, 1, "d1:ai1e4:unxti14e1:vi9e2:vti5ee", $timestamp, $timestamp)
ON DUPLICATE KEY UPDATE id = 14;

UPDATE $databaseName.mail_item mi, zimbra.mailbox mbx
SET mod_metadata = change_checkpoint + 100,
    mod_content = change_checkpoint + 100,
    change_checkpoint = change_checkpoint + 200
WHERE mi.mailbox_id = $mailboxId AND mi.id = 14 AND mbx.id = $mailboxId;

EOF_CREATE_IMS_FOLDER

    Migrate::runSql($sql);
}


# Metadata:  "d1:ai1e4:unxti14e1:vi9e2:vti5ee"

# d  #is map

#   ###
#   #
#   # FN_ATTRS = 1  (FOLDER_IS_IMMUTABLE)
#   #
#   1:
#       a  # FN_ATTRS (See Folder.java, FOLDER_IS_IMMUTABLE)
#   i #int
#     1
#    e #end

#   ###
#   #
#   # UID_NEXT = 14  (for imap, this should just start as the type-id of the folder)
#   #
#   4:  # is string
#     unxt # value

#   i  # is number
#     7
#   e # end

#   ###
#   #
#   # MD_VERSION = 9  (current MD version from source code)
#   #
#   1:
#    v

#   i # number
#     9
#   e #end

#   ###
#   #
#   # FN_VIEW = 5  (MailItem TYPE_* entry, this one means MESSAGE)
#   #
#   2:
#     vt
 
#   i # int
#     5
#   e  #int end

# e # map end

