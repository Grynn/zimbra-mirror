#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
use lib '/opt/zimbra/zimbramon/lib';
use Net::LDAP;
use XML::Simple;
use Getopt::Std;

if ( ! -d "/opt/zimbra/openldap/etc" ) {
  print "ERROR: openldap does not appear to be installed - exiting\n";
  exit(1);
}

my $id = getpwuid($<);
chomp $id;
if ($id ne "zimbra") {
    print STDERR "Error: must be run as zimbra user\n";
    exit (1);
}


my $localxml = XMLin("/opt/zimbra/conf/localconfig.xml");
my $ldap_root_password = $localxml->{key}->{ldap_root_password}->{value};
chomp($ldap_root_password);
my $ldap_is_master = $localxml->{key}->{ldap_is_master}->{value};
chomp($ldap_is_master);
my $zimbra_home = $localxml->{key}->{zimbra_home}->{value};

if ($zimbra_home eq "") {
   $zimbra_home = "/opt/zimbra";
}

my $zmprov="${zimbra_home}/bin/zmprov -l --";

my $ldap = Net::LDAP->new('ldapi://%2fopt%2fzimbra%2fopenldap%2fvar%2frun%2fldapi/') or die "$@";

my $mesg = $ldap->bind("cn=config", password=>"$ldap_root_password");

$mesg->code && die "Bind: ". $mesg->error . "\n"; 

my $dn="cn=module{0},cn=config";

$mesg = $ldap->modify(
    $dn,
    add =>{olcModuleLoad => 'dynlist.la'},
  );

my $bdn="olcDatabase={2}hdb,cn=config";

if(lc($ldap_is_master) eq "true") {
  $mesg = $ldap->search(
                        base=> "cn=accesslog",
                        filter=>"(objectClass=*)",
                        scope => "base",
                        attrs => ['1.1'],
                 );
  my $size = $mesg->count;
  if ($size > 0 ) {
    $bdn="olcDatabase={3}hdb,cn=config";
  }
}

$mesg = $ldap ->search(
                    base=>"$bdn",
                    filter=>"(objectClass=olcDynamicList)",
                    scope=>"sub",
                    attrs => ['olcDlAttrSet'],
                );

my $size = $mesg->count;
if ($size == 0) {
  $dn="olcOverlay=dynlist,$bdn";
  $mesg = $ldap->add( "$dn",
                       attr => [
                         'olcDlAttrSet' => 'groupOfURLs memberURL member',
                         'objectclass' => ['olcOverlayConfig', 'olcDynamicList', ],
                       ]
                     );
  $mesg->code && warn "failed to add entry: ", $mesg->error ;
}

$dn = "cn=groups,cn=zimbra";
$mesg = $ldap->add( "$dn",
                       attr => [
                         'description' => 'global dynamic groups',
                         'cn' => 'groups',
                         'objectclass' => 'organizationalRole',
                       ]
                     );
$mesg->code && warn "failed to add entry: ", $mesg->error ;

open(ZMPROV, "${zmprov} gad |");
my @DOMAINS = <ZMPROV>;
close(ZMPROV);

foreach my $domain (@DOMAINS) {
  chomp($domain);
  print "Creating group entry for domain $domain\n";
  $domain =~ s/\./,dc=/g;
  $domain = "dc=$domain";
  $dn="cn=groups,$domain";
  $mesg = $ldap->add( "$dn",
                       attr => [
                         'description' => "dynamic groups for $domain domain",
                         'cn' => 'groups',
                         'objectclass' => 'organizationalRole',
                       ]
                     );
  $mesg->code && warn "failed to add entry: ", $mesg->error ;
}

#Add acl for postfix
my $dn=$bdn;
my ($entry,@attrvals,$aclNumber,$attrMod);
my $aclsearch='to attrs=zimbraId,zimbraMailAddress,zimbraMailAlias,zimbraMailCanonicalAddress,zimbraMailCatchAllAddress,zimbraMailCatchAllCanonicalAddress,zimbraMailCatchAllForwardingAddress,zimbraMailDeliveryAddress,zimbraMailForwardingAddress,zimbraPrefMailForwardingAddress,zimbraMailHost,zimbraMailStatus,zimbraMailTransport,zimbraDomainName,zimbraDomainType,zimbraPrefMailLocalDeliveryDisabled  by dn.children="cn=admins,cn=zimbra" write  by dn.base="uid=zmpostfix,cn=appaccts,cn=zimbra" read  by dn.base="uid=zmamavis,cn=appaccts,cn=zimbra" read  by \* none';
$mesg = $ldap ->search(
                    base=>"$bdn",
                    filter=>"(olcAccess=$aclsearch)",
                    scope=>"base",
                    attrs => ['olcAccess'],
                );

my $size = $mesg->count;
if ($size != 0)  {
  $entry=$mesg->entry($size-1);
  @attrvals=$entry->get_value("olcAccess");
  $aclNumber=-1;
  $attrMod="";
  foreach my $attr (@attrvals) {
    if ($attr =~ /to attrs=zimbraId/) {
      ($aclNumber) = $attr =~ /^\{(\d+)\}*/;
      $attrMod=$attr;
    }
  }
  if ($aclNumber != -1 && $attrMod ne "") {
    $attrMod =~ s/zimbraPrefMailLocalDeliveryDisabled  /zimbraPrefMailLocalDeliveryDisabled,member,memberURL,zimbraMemberOf  /;
    $mesg = $ldap->modify(
        $dn,
        delete => {olcAccess => "{$aclNumber}"},
    );
    $mesg = $ldap->modify(
        $dn,
        add =>{olcAccess=>"$attrMod"},
    );
  }
} else {
  $aclsearch='to attrs=zimbraId,zimbraMailAddress,zimbraMailAlias,zimbraMailCanonicalAddress,zimbraMailCatchAllAddress,zimbraMailCatchAllCanonicalAddress,zimbraMailCatchAllForwardingAddress,zimbraMailDeliveryAddress,zimbraMailForwardingAddress,zimbraPrefMailForwardingAddress,zimbraMailHost,zimbraMailStatus,zimbraMailTransport,zimbraDomainName,zimbraDomainType,zimbraPrefMailLocalDeliveryDisabled  by dn.children="cn=admins,cn=zimbra" write  by dn.base="uid=zmpostfix,cn=appaccts,cn=zimbra" read  by dn.base="uid=zmamavis,cn=appaccts,cn=zimbra" read  by \* read';
  $mesg = $ldap ->search(
                    base=>"$bdn",
                    filter=>"(olcAccess=$aclsearch)",
                    scope=>"base",
                    attrs => ['olcAccess'],
                );
  $size = $mesg->count;
  if ($size != 0) {
    $entry=$mesg->entry($size-1);
    @attrvals=$entry->get_value("olcAccess");
    $aclNumber=-1;
    $attrMod="";
    foreach my $attr (@attrvals) {
      if ($attr =~ /to attrs=zimbraId/) {
        ($aclNumber) = $attr =~ /^\{(\d+)\}*/;
        $attrMod=$attr;
      }
    }
    if ($aclNumber != -1 && $attrMod ne "") {
      $attrMod =~ s/zimbraPrefMailLocalDeliveryDisabled  /zimbraPrefMailLocalDeliveryDisabled,member,memberURL,zimbraMemberOf  /;
      $mesg = $ldap->modify(
          $dn,
          delete => {olcAccess => "{$aclNumber}"},
      );
      $mesg = $ldap->modify(
          $dn,
          add =>{olcAccess=>"$attrMod"},
      );
    }
  }
}

my $acl='{9}to dn.subtree="cn=groups,cn=zimbra" attrs=zimbraMailAlias,member,zimbraMailStatus,entry  by dn.children="cn=admins,cn=zimbra" write  by dn.base="uid=zmpostfix,cn=appaccts,cn=zimbra" read';
$aclsearch='to dn.subtree="cn=groups,cn=zimbra" attrs=zimbraMailAlias,member,zimbraMailStatus,entry  by dn.children="cn=admins,cn=zimbra" write  by dn.base="uid=zmpostfix,cn=appaccts,cn=zimbra" read';
$mesg = $ldap ->search(
                    base=>"$bdn",
                    filter=>"(olcAccess=$aclsearch)",
                    scope=>"base",
                    attrs => ['olcAccess'],
                );

$size = $mesg->count;
if ($size == 0) {
  $mesg = $ldap->modify(
    $dn,
    add =>{olcAccess=>"$acl"},
  );
}

$ldap->unbind;
