#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2007, 2008, 2009 Zimbra, Inc.
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.2 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
# 
use strict;
use lib "/opt/zimbra/zimbramon/lib";
use Zimbra::Util::Common;
use File::Grep qw (fgrep);

my $dbfile= "/opt/zimbra/openldap-data/DB_CONFIG";

print "Checking for necessary locker settings for OpenLDAP database\n";
if ( -f "${dbfile}" ) {
  if ( !fgrep { /set_lk_max_locks|SET_LK_MAX_LOCKS/ } "${dbfile}" ) {
	chmod(0660, "$dbfile");
	open(DB,">>$dbfile") || die("Cannot Open File");
	print DB "set_lk_max_locks\t3000\n";
	close(DB);
	chmod(0440, "$dbfile");
  }
  if ( !fgrep { /set_lk_max_objects|SET_LK_MAX_OBJECTS/ } "${dbfile}" ) {
	chmod(0660, "$dbfile");
	open(DB,">>$dbfile") || die("Cannot Open File");
	print DB "set_lk_max_objects\t1500\n";
	close(DB);
	chmod(0440, "$dbfile");
  }
  if ( !fgrep { /set_lk_max_lockers|SET_LK_MAX_LOCKERS/ } "${dbfile}" ) {
	chmod(0660, "$dbfile");
	open(DB,">>$dbfile") || die("Cannot Open File");
	print DB "set_lk_max_lockers\t1500\n";
	close(DB);
	chmod(0440, "$dbfile");
  }
}

