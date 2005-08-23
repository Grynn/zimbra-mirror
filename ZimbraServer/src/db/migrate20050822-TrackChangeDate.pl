#!/usr/bin/perl

use strict;
use Migrate;

my @mailboxIds = Migrate::getMailboxIds();

Migrate::verifySchemaVersion(15);
foreach my $id (@mailboxIds) {
    addChangeDateColumn($id);
}
Migrate::updateSchemaVersion(15, 16);

exit(0);

#####################

sub addChangeDateColumn($) {
    my ($mailboxId) = @_;
    my $sql = <<EOF;
ALTER TABLE mailbox$mailboxId.mail_item
ADD COLUMN change_date INTEGER UNSIGNED AFTER mod_metadata;

UPDATE mailbox$mailboxId.mail_item SET change_date = date;

EOF
    
    Migrate::log("Adding CHANGE_DATE column to mailbox$mailboxId.mail_item.");
    Migrate::runSql($sql);
}
