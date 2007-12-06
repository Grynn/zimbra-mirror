#!/usr/bin/perl
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

$status = $ld->delete("uid=zimbrareplication,cn=admins,cn=zimbra");
$status = $ld->delete("uid=zmpostfix,cn=admins,cn=zimbra");
$status = $ld->delete("uid=zmamavis,cn=admins,cn=zimbra");

$ld->unbind();

print "done.\n";
