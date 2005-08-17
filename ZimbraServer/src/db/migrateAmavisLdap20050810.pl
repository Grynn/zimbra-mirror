#!/usr/bin/perl

open LDAP, "ldapsearch -b ou=people,dc=dogfood,dc=liquidsys,dc=com objectClass=liquidAccount -L -L -L |" or die "Can't perform ldap search";
my @lines = <LDAP>;
close LDAP;

foreach (@lines) {
	if (/^$/) { print "objectClass: amavisAccount\n"; }
	print $_;
}
