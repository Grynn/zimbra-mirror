#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2006, 2007 Zimbra, Inc.
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

my $CONCURRENCY = 10;
my $ID = 14;  # mail_item_id of new folder
my $METADATA =  'd1:ai1e4:unxti14e1:vi9e2:vti5ee';
my $NOW = time();

Migrate::verifySchemaVersion(29);

bumpUpMailboxChangeCheckpoints();

#
# Rename existing 'Chats' folder, if there is one
#
my @sqlRename;
my %mailboxes = Migrate::getMailboxes();
foreach my $mboxId (sort(keys %mailboxes)) {
  my $gid = $mailboxes{$mboxId};
  my $sql = renameExistingChatsFolder($mboxId, $gid);
  push(@sqlRename, $sql);
}
Migrate::runSqlParallel($CONCURRENCY, @sqlRename);


#
# Create a new 'Chats' folder
#
my %uniqueGroups;
foreach my $gid (values %mailboxes) {
  if (!exists($uniqueGroups{$gid})) {
    $uniqueGroups{$gid} = $gid;
  }
}
my @sqlInsert;
my @groups = sort(keys %uniqueGroups);
foreach my $gid (sort @groups) {
  my $sql = createChatsFolder($gid);
  push(@sqlInsert, $sql);
}
Migrate::runSqlParallel($CONCURRENCY, @sqlInsert);


#foreach my $cur (sort(keys %mailboxes)) {
#  createIMsFolder("mboxgroup".$mailboxes{$cur}, $cur);
#}

Migrate::updateSchemaVersion(29, 30);

exit(0);


#####################

# Increment change_checkpoint column for all rows in mailbox table.
# This SQL must be executed immediately rather than queued.
sub bumpUpMailboxChangeCheckpoints() {
  my $sql =<<_SQL_;
UPDATE mailbox
SET change_checkpoint = change_checkpoint + 100;
_SQL_
  Migrate::runSql($sql);
}

# Rename any existing Chats folder at root level.
#
# Renaming is done per mailbox rather than per group to force the use of
# the (mailbox_id, folder_id) index on mail_item table.  This should be
# more efficient than full table scan of mail_item tables most of whose
# rows have folder_id != 1.
sub renameExistingChatsFolder($$) {
  my ($mboxId, $gid) = @_;

  my $sql =<<_SQL_;
UPDATE mboxgroup$gid.mail_item mi, mailbox mb
SET mi.subject = CONCAT('Chats - renamed (', mi.id, ' - $NOW)'),
    mi.mod_metadata = mb.change_checkpoint,
    mi.mod_content = mb.change_checkpoint,
    mi.change_date = $NOW
WHERE mi.mailbox_id = $mboxId AND mi.folder_id = 1 AND
      mi.id != $ID AND LOWER(mi.subject) = 'chats' AND
      mb.id = mi.mailbox_id;
_SQL_
  return $sql;
}

# Create the system Chats folder for each mailbox in the specified
# mailbox group.
sub createChatsFolder($) {
  my $gid = shift;

  my $sql = <<_SQL_;
INSERT INTO mboxgroup$gid.mail_item (
    mailbox_id, id, type, parent_id, folder_id, index_id, imap_id,
    date, size, volume_id, blob_digest,
    unread, flags, tags, sender,
    subject, metadata,
    mod_metadata, change_date, mod_content
)
SELECT
    id, $ID, 1, 1, 1, null, null,
    $NOW, 0, null, null,
    0, 0, 0, null,
    'Chats', '$METADATA',
    change_checkpoint, $NOW, change_checkpoint
FROM mailbox
WHERE group_id = $gid
ON DUPLICATE KEY UPDATE subject = 'Chats';
_SQL_
  return $sql;
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

