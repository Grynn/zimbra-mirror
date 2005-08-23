#!/usr/bin/perl

use strict;
use Migrate;

Migrate::verifySchemaVersion(14);

my @mailboxIds = Migrate::getMailboxIds();

foreach my $id (@mailboxIds) {
    addIndexes($id);
}

Migrate::updateSchemaVersion(14, 15);

exit(0);

#####################

sub addIndexes($) {
    my ($mailboxId) = @_;
    my $sql = <<EOF;
ALTER TABLE mailbox$mailboxId.mail_item
ADD INDEX i_tags_date (tags, date),
ADD INDEX i_flags_date (flags, date);
EOF
    
    Migrate::runSql($sql);
}
