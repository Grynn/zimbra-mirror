#!/usr/bin/perl

use strict;
use Migrate;

Migrate::verifySchemaVersion(20);

dropRedoLogSequence();

Migrate::updateSchemaVersion(20, 21);

exit(0);

#####################

sub dropRedoLogSequence() {
    my $sql = "drop table if exists redolog_sequence;";
    Migrate::runSql($sql);
}
