#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2009 Zimbra, Inc.
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
use Getopt::Long;
use Pod::Usage;
use Migrate;

my ($opt_help, $opt_mbox, $opt_all);

GetOptions("help"       => \$opt_help,
           "mailbox=i"  => \$opt_mbox,
           "all"        => \$opt_all
          ) || pod2usage(2);

pod2usage(1) if ($opt_help);
if (!defined($opt_mbox) && !defined($opt_all)) {
  pod2usage(-msg => "One of -m or -a must be specified");
}
if (defined($opt_mbox) && defined($opt_all)) {
  pod2usage(-msg => "Only one of -m or -a can be specified");
}

Migrate::log("clearArchivedFlag begin...");

my %mboxes = Migrate::getMailboxes();
if ($opt_all) {
  foreach my $mid (sort(keys %mboxes)) {
    my $gid = $mboxes{$mid};
    clearArchivedFlag($gid, $mid);
  }
} else {
  my $gid = $mboxes{$opt_mbox};
  die "No mailbox group found for mailbox $opt_mbox.\n" unless $gid > 0;
  clearArchivedFlag($gid, $opt_mbox);
}

Migrate::log("clearArchivedFlag complete.");

exit(0);

#####################

sub clearArchivedFlag() {
  my ($gid, $mid) = @_;
  print "Checking mailbox $mid of group $gid...";

  my $flag = 134217728;
  my $sql = <<ARCHIVED_FLAG_LOOKUP_EOF;
SELECT mailbox_id,id,type,flags,name FROM mboxgroup$gid.mail_item
WHERE mailbox_id=$mid and flags >= $flag and flags & $flag;
ARCHIVED_FLAG_LOOKUP_EOF
  my @rows = Migrate::runSql($sql);
  print " Found " . scalar(@rows) . " item(s) with Archived flag\n";
  if (scalar(@rows) == 0) {
    return;
  }

  my @ids;
  foreach my $row (@rows) {
    print "$row\n";
    if ($row =~ /[^\t\s]+\t+([^\t\s]+)/) {
      push(@ids, $1);
    }
  }
  my $list = join(', ', @ids);
  
  print "Clearing " . scalar(@ids) . " item(s) of Archived flag...";

  $sql = <<ARCHIVED_FLAG_LOOKUP_EOF;
UPDATE mboxgroup$gid.mail_item SET flags = flags & ~$flag
WHERE mailbox_id=$mid and id in ($list);
ARCHIVED_FLAG_LOOKUP_EOF
  Migrate::runSql($sql);
  print " Done\n";
}

__END__
    
=head1 NAME

clearArchivedFlag.pl - clear the Archived flag off all items

=head1 SYNOPSIS

clearArchivedFlag.pl [options]

      Options:
        -h --help        print this message
        -m --mailbox=%d  ID of mailbox to fix
        -a --all         fix all mailboxes on this server

=cut
