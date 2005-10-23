#!/usr/bin/perl

use strict;
use Migrate;


Migrate::verifySchemaVersion(21);

alterVolume();

exit(0);

#####################

sub alterVolume() {
    Migrate::log("Updating volume table");

    my $sql = <<EOF;
ALTER TABLE volume
ADD UNIQUE i_name (name),
ADD UNIQUE i_path (path(255));

EOF

    Migrate::runSql($sql);
}
