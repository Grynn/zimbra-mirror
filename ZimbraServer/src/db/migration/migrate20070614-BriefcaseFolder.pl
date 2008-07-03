#!/usr/bin/perl

# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2007 Zimbra, Inc.
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

#
# Add Briefcase system folder (id = 16) to each mailbox.
#

use strict;
use Migrate;

sub bumpUpMailboxChangeCheckpoints();
sub renameExistingTasksFolder($$);
sub createTasksFolder($);

my $CONCURRENCY = 10;
my $ID = 16;
my $METADATA = 'd1:ai1e4:mseqi1e4:unxti17e1:vi10e2:vti8ee';
my $NOW = time();
my $FOLDERNAME = 'Briefcase';

Migrate::verifySchemaVersion(38);

bumpUpMailboxChangeCheckpoints();

my @sqlRename;
my %mailboxes = Migrate::getMailboxes();
foreach my $mboxId (sort(keys %mailboxes)) {
    my $gid = $mailboxes{$mboxId};
    my $sql = renameExistingFolder($mboxId, $gid);
    push(@sqlRename, $sql);
}
Migrate::runSqlParallel($CONCURRENCY, @sqlRename);

my %uniqueGroups;
foreach my $gid (values %mailboxes) {
    if (!exists($uniqueGroups{$gid})) {
        $uniqueGroups{$gid} = $gid;
    }
}
my @sqlInsert;
my @groups = sort(keys %uniqueGroups);
foreach my $gid (sort @groups) {
    my $sql = createFolder($gid);
    push(@sqlInsert, $sql);
}
Migrate::runSqlParallel($CONCURRENCY, @sqlInsert);

Migrate::updateSchemaVersion(38, 39);

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

# Rename any existing Briefcase folder at root level.
#
# Renaming is done per mailbox rather than per group to force the use of
# the (mailbox_id, folder_id) index on mail_item table.  This should be
# more efficient than full table scan of mail_item tables most of whose
# rows have folder_id != 1.
sub renameExistingFolder($$) {
    my ($mboxId, $gid) = @_;

    my $sql =<<_SQL_;
UPDATE mboxgroup$gid.mail_item mi, mailbox mb
SET mi.name = CONCAT('$FOLDERNAME - renamed (', mi.id, ' - $NOW)'),
    mi.subject = CONCAT('$FOLDERNAME - renamed (', mi.id, ' - $NOW)'),
    mi.mod_metadata = mb.change_checkpoint,
    mi.mod_content = mb.change_checkpoint,
    mi.change_date = $NOW
WHERE mi.mailbox_id = $mboxId AND mi.folder_id = 1 AND
      mi.id != $ID AND LOWER(mi.name) = LOWER('$FOLDERNAME') AND
      mb.id = mi.mailbox_id;
_SQL_
    return $sql;
}

# Create the system Briefcase folder for each mailbox in the specified
# mailbox group.
sub createFolder($) {
    my $gid = shift;

    my $sql = <<_SQL_;
INSERT INTO mboxgroup$gid.mail_item (
    mailbox_id, id, type, parent_id, folder_id, index_id, imap_id,
    date, size, volume_id, blob_digest,
    unread, flags, tags, sender,
    subject, name, metadata,
    mod_metadata, change_date, mod_content
)
SELECT
    id, $ID, 1, 1, 1, null, null,
    $NOW, 0, null, null,
    0, 0, 0, null,
    '$FOLDERNAME', '$FOLDERNAME', '$METADATA',
    change_checkpoint, $NOW, change_checkpoint
FROM mailbox
WHERE group_id = $gid
ON DUPLICATE KEY UPDATE name = '$FOLDERNAME';
_SQL_
    return $sql;
}


__END__


Metadata:  "d1:ai1e4:mseqi1e4:unxti17e1:vi10e2:vti8ee"

d #map

  ###
  #
  # FN_ATTRS = 1  (FOLDER_IS_IMMUTABLE)
  #
  1: #len("a")
    a # FN_ATTRS (See Folder.java, FOLDER_IS_IMMUTABLE)
  i #int
    1
  e #int end

  ###
  #
  # FN_MODSEQ = 1 (maybe not required, but for consistency)
  #
  4: #len("mseq")
    unxt
  i #int
    1
  e #int end

  ###
  #
  # UID_NEXT = 17  (for imap, this should just start as the type-id of the folder)
  #
  4: #len("unxt")
    unxt
  i #int
    17
  e #int end

  ###
  #
  # MD_VERSION = 10  (current MD version from source code)
  #
  1: #len("v")
    v
  i #int
    10
  e #int end

  #
  # FN_VIEW = 8  (MailItem TYPE_* entry, this one means TYPE_DOCUMENT)
  #
  2: #len("vt")
    vt
  i #int
    8
  e #int end

e #map end
