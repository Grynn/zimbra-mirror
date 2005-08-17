#!/usr/bin/perl

use strict;
use Migrate;

my @mailboxIds = Migrate::getMailboxIds();

foreach my $id (@mailboxIds) {
    # Get last change checkpoint
    my @result = Migrate::runSql("SELECT change_checkpoint FROM mailbox WHERE id = $id");
    my $checkpoint = $result[0];

    renameJunkFolder($id, $checkpoint + 100);
    renameSpamToJunk($id, $checkpoint + 100);
    renameInbox($id, $checkpoint + 100);

    Migrate::runSql("UPDATE mailbox " .
		     "SET change_checkpoint = " . ($checkpoint + 101) .
		     " WHERE id = $id");
}

Migrate::updateSchemaVersion(12);

exit(0);

#####################

sub renameJunkFolder($$) {
    my ($mailboxId, $modMetadata) = @_;
    my $sql = <<EOF;
UPDATE mailbox$mailboxId.mail_item
SET subject = CONCAT(subject, id), mod_metadata = $modMetadata
WHERE type = 1
AND subject = 'junk'
AND parent_id = 1;
EOF
    
    Migrate::log("Renaming preexisting top-level Junk folder in mailbox$mailboxId");
    Migrate::runSql($sql);
}

sub renameSpamToJunk($$) {
    my ($mailboxId, $modMetadata) = @_;

    my $sql = <<EOF;
UPDATE mailbox$mailboxId.mail_item
SET subject = 'Junk', mod_metadata = $modMetadata
WHERE id = 4;
EOF

    Migrate::log("Renaming Spam folder to Junk in mailbox$mailboxId");
    Migrate::runSql($sql);
}

sub renameInbox($$) {
    my ($mailboxId, $modMetadata) = @_;
    my $sql = <<EOF;
UPDATE mailbox$mailboxId.mail_item
SET subject = 'Inbox', mod_metadata = $modMetadata
WHERE id = 2;
EOF

    Migrate::log("Renaming INBOX folder to Inbox in mailbox$mailboxId");
    Migrate::runSql($sql);
}
