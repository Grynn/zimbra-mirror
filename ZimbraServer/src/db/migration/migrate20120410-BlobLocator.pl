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

Migrate::verifySchemaVersion(90);
doIt();
Migrate::updateSchemaVersion(90, 91);

exit(0);

#####################

sub doIt() {
  foreach my $group (Migrate::getMailboxGroups()) {
    my $volumeIdInt = (Migrate::runSql("show columns from $group.mail_item where Field = 'volume_id' and Type like '%int%';"))[0];
    if (length $volumeIdInt == 0) {
      #existing install using external store; volume_id was manually altered
      Migrate::logSql("$group.mail_item.volume_id is not currently an int; this installation probably altered it for older StoreManager implementation");
      fixVolumeId($group);	
    } else {
      #standard migration
      Migrate::logSql("Adding blob locator columns in $group");
      alterVolumeId($group);	
    }
  } 
}

sub alterVolumeId($) {
#standard migration
  my ($group) = @_;
  my $sql;
  $sql = <<_EOF_;
ALTER TABLE $group.mail_item DROP INDEX i_volume_id;
ALTER TABLE $group.mail_item DROP FOREIGN KEY fk_mail_item_volume_id;
ALTER TABLE $group.mail_item DROP KEY fk_mail_item_volume_id;
ALTER TABLE $group.mail_item_dumpster DROP INDEX i_volume_id;
ALTER TABLE $group.mail_item_dumpster DROP FOREIGN KEY fk_mail_item_dumpster_volume_id;
ALTER TABLE $group.mail_item_dumpster DROP KEY fk_mail_item_dumpster_volume_id;
ALTER TABLE $group.mail_item CHANGE volume_id locator VARCHAR(1024);
ALTER TABLE $group.mail_item_dumpster CHANGE volume_id locator VARCHAR(1024);
ALTER TABLE $group.revision CHANGE volume_id locator VARCHAR(1024);
ALTER TABLE $group.revision_dumpster CHANGE volume_id locator VARCHAR(1024);
_EOF_
  Migrate::runSql($sql);
}

sub fixVolumeId($) {
#migration for installs which hacked our db schema for legacy HttpStore support
  my ($group) = @_;
  my $sql;
  
#drop any existing keys/indexes from mail_item
  my $mailItemVolIdx = (Migrate::runSql("show indexes from $group.mail_item where key_name='i_volume_id';"))[0];  
  if (length $mailItemVolIdx > 0) {
    $sql = <<_EOF_;
ALTER TABLE $group.mail_item DROP INDEX i_volume_id;
_EOF_
    Migrate::runSql($sql);
  }
  my $mailItemVolFk = (Migrate::runSql("select * from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where REFERENCED_TABLE_NAME= 'volume' and CONSTRAINT_SCHEMA='$group' and CONSTRAINT_NAME='fk_mail_item_volume_id';"))[0];  
  if (length $mailItemVolFk > 0) {
    $sql = <<_EOF_;
ALTER TABLE $group.mail_item DROP FOREIGN KEY fk_mail_item_volume_id;
_EOF_
    Migrate::runSql($sql);
  }
  my $mailItemVolFkIdx = (Migrate::runSql("show indexes from $group.mail_item where key_name='fk_mail_item_volume_id';"))[0];  
  if (length $mailItemVolFkIdx > 0) {
    $sql = <<_EOF_;
ALTER TABLE $group.mail_item DROP KEY fk_mail_item_volume_id;
_EOF_
    Migrate::runSql($sql);
  }

#drop any existing keys/indexes from mail_item_dumpster
  my $dumpsterVolIdx = (Migrate::runSql("show indexes from $group.mail_item_dumpster where key_name='i_volume_id';"))[0];  
  if (length $dumpsterVolIdx > 0) {
    $sql = <<_EOF_;
ALTER TABLE $group.mail_item_dumpster DROP INDEX i_volume_id;
_EOF_
    Migrate::runSql($sql);
  }
  my $dumpsterVolFk = (Migrate::runSql("select * from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where REFERENCED_TABLE_NAME= 'volume' and CONSTRAINT_SCHEMA='$group' and CONSTRAINT_NAME='fk_mail_item_dumpster_volume_id';"))[0];  
  if (length $dumpsterVolFk > 0) {
    $sql = <<_EOF_;
ALTER TABLE $group.mail_item_dumpster DROP FOREIGN KEY fk_mail_item_dumpster_volume_id;
_EOF_
    Migrate::runSql($sql);
  }
  my $dumpsterVolFkIdx = (Migrate::runSql("show indexes from $group.mail_item_dumpster where key_name='fk_mail_item_dumpster_volume_id';"))[0];  
  if (length $dumpsterVolFkIdx > 0) {
    $sql = <<_EOF_;
ALTER TABLE $group.mail_item_dumpster DROP KEY fk_mail_item_dumpster_volume_id;
_EOF_
    Migrate::runSql($sql);
  }
  
#alter columns  
  $sql = <<_EOF_;
ALTER TABLE $group.mail_item CHANGE volume_id locator VARCHAR(1024);
ALTER TABLE $group.mail_item_dumpster CHANGE volume_id locator VARCHAR(1024);
ALTER TABLE $group.revision CHANGE volume_id locator VARCHAR(1024);
ALTER TABLE $group.revision_dumpster CHANGE volume_id locator VARCHAR(1024);
_EOF_
  Migrate::runSql($sql);
}

