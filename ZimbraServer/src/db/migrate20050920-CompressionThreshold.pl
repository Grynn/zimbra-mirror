#!/usr/bin/perl

use strict;
use Migrate;

Migrate::verifySchemaVersion(19);

alterVolume();

Migrate::updateSchemaVersion(19, 20);

exit(0);

#####################

sub alterVolume() {
    my $sql = "ALTER TABLE volume" .
	" ADD compression_threshold BIGINT NOT NULL;";
    Migrate::runSql($sql);

    Migrate::runSql("DELETE FROM config WHERE name = 'store.compressBlobs'");
}
