#!/usr/bin/perl
#
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
no strict "refs";

use Zimbra::Util::LDAP;

sub logMsg{
	print join (' ',@_),"\n";
}

our %config = (
	ldap_is_master	=>	$ARGV[0],
	ldap_root_password	=>	$ARGV[1],
	);

Zimbra::Util::LDAP->doLdap($ARGV[2],$ARGV[3]);
