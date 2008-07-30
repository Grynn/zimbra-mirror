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
use strict;
use warnings;
use Getopt::Long;

my ($PRIMARY, $SECONDARY, $REMOTE_USER, $ENABLE_IM, $REMOTE_JAVA_HOME);
GetOptions("p=s", \$PRIMARY,
           "s=s", \$SECONDARY,
           "u=s", \$REMOTE_USER,
           "im", \$ENABLE_IM,
           "j=s", \$REMOTE_JAVA_HOME,
           );

sub usage() {
  my $usage = <<END_OF_USAGE;
USAGE: $0  -u REMOTE_USERNAME -s SECONDARY_HOSTNAME [-p PRIMARY_HOSTNAME] [-j REMOTE_JAVA_HOME] [-im]

You must run this script on the 'primary' server (the one that LDAP will be running on)

  Required:
     REMOTE_USERNAME  - unix username on the 'secondary' server
     SECONDARY_HOSTNAME - 'secondary' servers hostname

  Optional:
     PRIMARY_HOSTNAME - hostname of the 'primary' server (defaults to `hostname`)
     REMOTE_JAVA_HOME - on the 'secondary' server, the path to JAVA (defaults to '/opt/zimbra/java')
     ENABLE_IM - if set, enable IM on both boxes (defaults to no)


This script is intended to automated the setup of a multiserver configuration in a dev environment.

The assumption is that you have two servers, the 'primary' is the one you're running this script on, and the 'secondary'.
SSH must be enabled between the boxes, and ~/.ssh/authorized_keys should be configured so that you can ssh from 'primary' to 'secondary' without typing a password.
This script does the following things:
  1) configure LDAP so that both servers point at the same ldap
  2) create server records for the 'secondary' server
  3) enable the 'secondary' server as a mail server
  4) create test users: "secondary[1-3]\@DOMAIN" where DOMAIN is derived from the PRIMARY_HOSTNAME
  5) configure SSH keys for admin console, so that it can be used properly
  6) optionally enable IM on both servers
  7) start the zimbra services

END_OF_USAGE
  die $usage;
}
if (!defined $REMOTE_USER) {
  $REMOTE_USER = $ENV{USER};
}
if (!defined $PRIMARY) {
  $PRIMARY = `hostname`;
  chomp $PRIMARY;
  print "Using $PRIMARY as primary server hostname\n";
}
if (!defined $REMOTE_JAVA_HOME) {
  #/usr/lib/jvm/java-1.5.0-sun/
  $REMOTE_JAVA_HOME = "/opt/zimbra/java";
}
if (!defined $PRIMARY || !defined $SECONDARY || !defined $REMOTE_USER || !defined $REMOTE_JAVA_HOME) {
  usage();
}

sub loc($);
sub remote($);
sub both($);

sub both($) {
  my $cmd = shift();
  loc($cmd);
  remote($cmd);
}

sub loc($) {
  my $cmd = shift();
  print "LOCAL: $cmd\n";
  my $ret = `$cmd`;
  print $ret;
  return $ret;
}

sub remote($) {
  my $cmd = shift();
  print "REMOTE: $cmd\n";
  $cmd = "ssh $SECONDARY PATH=\$PATH:~/bin:/usr/local/bin:/opt/zimbra/bin:/opt/zimbra/openldap/bin:/opt/zimbra/java/bin:/opt/zimbra/snmp/bin:/bin:/sbin:/usr/bin:/usr/sbin LD_LIBRARY_PATH=/opt/zimbra/lib: ZIMBRA_HOME=/opt/zimbra ZIMBRA_HOSTNAME=$SECONDARY JAVA_HOME=$REMOTE_JAVA_HOME ZIMBRA_USE_JETTY=1 $cmd";
  my $ret = `$cmd`;
  print $ret;
  return $ret;
}

###########################################

#4) Stop a few services:
loc "jetty stop";
loc "ldap stop";
remote "jetty stop";
remote "ldap stop";

#5) Update localconfig on the 'secondary' box:
remote "zmlocalconfig -e ldap_master_url=ldap://$PRIMARY:389";
remote "zmlocalconfig -e ldap_url=ldap://$PRIMARY:389";
remote "zmlocalconfig -e ldap_is_master=false";


#6) Set up the new server account
print "About to start ldap locally - if you get a password prompt, it's because the 'ldap start' script is trying to sudo -- add yourself to sudoers, or type your password\n";
loc "ldap start";
loc "zmprov -l cs $SECONDARY";
loc "zmprov -l ms $SECONDARY zimbraServiceInstalled mailbox zimbraServiceEnabled mailbox";
loc "zmprov -l ms $SECONDARY zimbraMailMode http zimbraMailPort 7070 zimbraSmtpHostname $PRIMARY";

#7) Add the new server account to the pool of mail servers.  This is necessary so you can create mailboxes on either server.
my $prim_zid = "UNKNOWN";
if (loc("zmprov -l gs $PRIMARY zimbraid") =~ /zimbraId:\s([0-9a-f\-]+)/) {
  $prim_zid = $1;
}
print "Primary ZimbraID is $prim_zid\n";

my $sec_zid = "UNKNOWN";
if (loc("zmprov -l gs $SECONDARY zimbraid") =~ /zimbraId:\s([0-9a-f\-]+)/) {
  $sec_zid = $1;
}
print "Secondary ZimbraID is $sec_zid\n";

loc "zmprov -l mc default zimbraMailHostPool $prim_zid zimbraMailHostPool $sec_zid";

#9) OPTIONAL: Set up SSH keys so remote admin/management will work.  This is necessary to make some parts of the admin console work, and probably in other places too.
loc "zmlocalconfig -e zimbra_user=$REMOTE_USER";
remote "zmlocalconfig -e zimbra_user=$REMOTE_USER";

loc "/opt/zimbra/bin/zmupdateauthkeys";
remote "/opt/zimbra/bin/zmupdateauthkeys";

loc "zmprov -l ms $PRIMARY zimbraRemoteManagementUser $REMOTE_USER";
loc "zmprov -l ms $SECONDARY zimbraRemoteManagementUser $REMOTE_USER";

if (defined($ENABLE_IM)) {
  both "zmprov -l mcf zimbraXMPPEnabled TRUE";
  both "zmprov -l mc default zimbraFeatureIMEnabled TRUE";
  both "zmprov -l mc default zimbraFeatureInstantNotify TRUE";
}

loc "mkdir /tmp/zimbra";
loc "mysql.server start";
remote "mkdir /tmp/zimbra";
remote "mysql.server start";

loc "jetty start";
remote "jetty start";

loc "zmprov ca secondary1\@$PRIMARY test123 displayName \"Seocndary One\" zimbraMailHost $SECONDARY";
loc "zmprov ca secondary2\@$PRIMARY test123 displayName \"Seocndary Two\" zimbraMailHost $SECONDARY";
loc "zmprov ca secondary3\@$PRIMARY test123 displayName \"Seocndary Three\" zimbraMailHost $SECONDARY";



