#!/usr/bin/perl
use Data::UUID;
use Net::LDAPapi;
use strict;

my ($binddn,$bindpwd,$host,$junk,$result,@localconfig);
@localconfig=`/opt/zimbra/bin/zmlocalconfig -s ldap_master_url zimbra_ldap_userdn ldap_root_password`;

$host=$localconfig[0];
($junk,$host) = split /= /, $host, 2;
chomp $host;

$binddn=$localconfig[1];
($junk,$binddn) = split /= /, $binddn, 2;
chomp $binddn;

$bindpwd=$localconfig[2];
($junk,$bindpwd) = split /= /, $bindpwd, 2;
chomp $bindpwd;

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
		$ld->modify_s($dn,\%ldap_modifications);	
	} else {
		%ldap_modifications = (
			"zimbraSignatureId", "$sigId",
			"zimbraSignatureName", "$sigName",
			"zimbraPrefMailSignature", "$sigContent",
		);
		$ld->modify_s($dn,\%ldap_modifications);
	}
	%ldap_modifications = (
		"zimbraPrefDefaultSignatureId", "$sigId",
	);
	$ld->modify_s($dn,\%ldap_modifications);
	print ".";
}

$status = $ld->search_s("",LDAP_SCOPE_SUBTREE,"objectClass=zimbraIdentity","dn",0,$result);
foreach ($ent = $ld->first_entry; $ent != 0; $ent = $ld->next_entry) {
	my %ldap_modifications = (
		"zimbraPrefBccAddress", "",
		"zimbraPrefForwardIncludeOriginalText", "",
		"zimbraPrefForwardReplyFormat", "",
		"zimbraPrefForwardReplyPrefixChar", "",
		"zimbraPrefMailSignature", "",
		"zimbraPrefMailSignatureEnabled", "",
		"zimbraPrefMailSignatureStyle", "",
		"zimbraPrefReplyIncludeOriginalText", "",
		"zimbraPrefSaveToSent", "",
		"zimbraPrefSentMailFolder", "",
		"zimbraPrefUseDefaultIdentitySettings", "",
	);
	$ld->modify_s($dn,\%ldap_modifications);
}

$ld->unbind();

print "done.\n";
