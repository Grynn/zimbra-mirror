#!/usr/bin/perl

use strict;
use Migrate;

addConfigColumn();
Migrate::updateSchemaVersion(13);

exit(0);

#####################

sub addConfigColumn() {
    my $sql = <<EOF;
ALTER TABLE liquid.mailbox
ADD COLUMN config TEXT AFTER tracking_sync;

EOF
    
    Migrate::log("Adding CONFIG column to liquid.mailbox.");
    Migrate::runSql($sql);
}
