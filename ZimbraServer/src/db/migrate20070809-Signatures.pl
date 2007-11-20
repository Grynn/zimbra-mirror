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
use Data::UUID;
use Net::LDAPapi;
use strict;

my ($binddn,$bindpwd,$host,$junk,$result,@localconfig,$ismaster);
@localconfig=`/opt/zimbra/bin/zmlocalconfig -s ldap_master_url zimbra_ldap_userdn ldap_root_password ldap_is_master`;

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

my @attrs=("zimbraPrefMailSignature");

print "Beginning identity migration";
my $ld = Net::LDAPapi->new(-url=>"$host");
my $status=$ld->start_tls_s();
$status = $ld->bind_s($binddn,$bindpwd);
$status = $ld->search_s("",LDAP_SCOPE_SUBTREE,"(&(!(zimbraSignatureID=*))(zimbraPrefMailSignature=*))",\@attrs,0,$result);

my ($ent,$dn,$attr,$sigContent,$rdn, $rdnValue, $ug, $sigId, $sigName, $sigDN, $baseDN);

for ($ent = $ld->first_entry; $ent != 0; $ent = $ld->next_entry) {
	#
	#  Get Full DN
	if (($dn = $ld->get_dn) eq "")
	{
		$ld->unbind;
		die "get_dn: ", $ld->errstring, ": ", $ld->extramsg;
	}
	($rdn, $sigName) = split /,/, $dn, 2;
	($rdn, $sigName) = split /=/, $rdn, 2;

	$attr = $ld->first_attribute;
	$sigContent=($ld->get_values($attr))[0];
	$ug = Data::UUID->new;
	$sigId = $ug->create_str();

	if ($rdn eq "uid") {
		$sigDN = $dn;
	}
	else {
		($junk,$baseDN) = split /,/, $dn, 2;
		$sigDN = "zimbraSignatureName=".$sigName.",".$baseDN;
	}
  
	my %ldap_modifications;
	if ($rdn eq "uid" ) {
		%ldap_modifications = (
			"zimbraSignatureId", "$sigId",
			"zimbraSignatureName", "$sigName",
		);
		$ld->modify_s($sigDN,\%ldap_modifications);	
	} else {
		%ldap_modifications = (
			"zimbraSignatureName", "$sigName",
			"objectClass", "zimbraSignature",
			"zimbraSignatureId", "$sigId",
			"zimbraPrefMailSignature", "$sigContent",
		);
		$ld->add_s($sigDN,\%ldap_modifications);
	}
	%ldap_modifications = (
		"zimbraPrefDefaultSignatureId", "$sigId",
	);
	$ld->modify_s($dn,\%ldap_modifications);
	print ".";
}
print " done!\n";

#print "Updating old Identity classes";
#@attrs = ("zimbraPrefBccAddress", "zimbraPrefForwardIncludeOriginalText", "zimbraPrefForwardReplyFormat", "zimbraPrefForwardReplyPrefixChar", "zimbraPrefMailSignature",
#			"zimbraPrefMailSignatureEnabled", "zimbraPrefMailSignatureStyle", "zimbraPrefReplyIncludeOriginalText", "zimbraPrefSaveToSent", "zimbraPrefSentMailFolder",
#			"zimbraPrefUseDefaultIdentitySettings");
#$status = $ld->search_s("",LDAP_SCOPE_SUBTREE,"objectClass=zimbraIdentity",\@attrs,0,$result);
#foreach ($ent = $ld->first_entry; $ent != 0; $ent = $ld->next_entry) {
#	if (($dn = $ld->get_dn) eq "")
#	{
#		$ld->unbind;
#		die "get_dn: ", $ld->errstring, ": ", $ld->extramsg;
#	}
#	$attr=$ld->first_attribute;
#	foreach ($attr = $ld->first_attribute; $attr ne ""; $attr = $ld->next_attribute) {
#		my %ldap_modifications = (
#			"$attr", "",
#		);
#		$ld->modify_s($dn,\%ldap_modifications);
#	}
#	print ".";
#}
#print "done!\n";

$ld->unbind();
