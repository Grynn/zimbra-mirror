#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
use Net::LDAPapi;

my ($binddn,$bindpwd,$host,$junk,$result,@localconfig,$ismaster);
@localconfig=`/opt/zimbra/bin/zmlocalconfig -s ldap_master_url zimbra_ldap_userdn zimbra_ldap_password ldap_is_master`;
$host=$localconfig[0];
($junk,$host) = split /= /, $host, 2;
chomp $host;

$binddn=$localconfig[1];
($junk,$binddn) = split /= /, $binddn, 2;
chomp $binddn;

$bindpwd=$localconfig[2];
($junk,$bindpwd) = split /= /, $bindpwd, 2;
chomp $bindpwd;

$ismaster=$localconfig[3];
($junk,$ismaster) = split /= /, $ismaster, 2;
chomp $ismaster;

if ($ismaster ne "true") {
  exit;
}

print "Deleting old LDAP users\n";
my $ld = Net::LDAPapi->new(-url=>"$host");
my $status;

if ($host !~ /^ldaps/i) {
  $status=$ld->start_tls_s();
}

$status = $ld->bind_s($binddn,$bindpwd);

$status = $ld->delete_s("uid=zimbrareplication,cn=admins,cn=zimbra");
$status = $ld->delete_s("uid=zmpostfix,cn=admins,cn=zimbra");
$status = $ld->delete_s("uid=zmamavis,cn=admins,cn=zimbra");

$ld->unbind();

print "done.\n";
