package Migrate;

use strict;

#############

my $MYSQL = "mysql";
my $DB_USER = "zimbra";
my $DB_PASSWORD = "zimbra";
my $DATABASE = "zimbra";

if (-f "/opt/liquid/bin/lqlocalconfig") {
    $DB_PASSWORD = `lqlocalconfig -s -m nokey liquid_mysql_password`;
    chomp $DB_PASSWORD;
    $DB_USER = `lqlocalconfig -m nokey liquid_mysql_user`;
    chomp $DB_USER;
}

sub verifySchemaVersion($) {
    my ($version) = @_;
    my $versionInDb = (runSql("SELECT value FROM config WHERE name = 'db.version'"))[0];
    if ($version != $versionInDb) {
        print("Schema version mismatch.  Expected version $version.  Version in the database is $versionInDb.\n");
        exit(1);
    }
    Migrate::log("Verified schema version $version.");
}

sub updateSchemaVersion($$) {
    my ($oldVersion, $newVersion) = @_;
    verifySchemaVersion($oldVersion);

    my $sql = <<SET_SCHEMA_VERSION_EOF;
UPDATE $DATABASE.config SET value = '$newVersion' WHERE name = 'db.version';
SET_SCHEMA_VERSION_EOF

    Migrate::log("Updating DB schema version from $oldVersion to $newVersion.");
    runSql($sql);
}

sub getMailboxIds() {
    return runSql("SELECT id FROM mailbox ORDER BY id");
}

sub runSql($) {
    my ($script) = @_;

    # Write the last script to a text file for debugging
    # open(LASTSCRIPT, ">lastScript.sql") || die "Could not open lastScript.sql";
    # print(LASTSCRIPT $script);
    # close(LASTSCRIPT);

    Migrate::log($script);

    # Run the mysql command and redirect output to a temp file
    my $tempFile = "mysql.out";
    my $command = "$MYSQL --user=$DB_USER --password=$DB_PASSWORD " .
        "--database=$DATABASE --batch --skip-column-names";
    open(MYSQL, "| $command > $tempFile") || die "Unable to run $command";
    print(MYSQL $script);
    close(MYSQL);

    if ($? != 0) {
        die "Error while running '$command'.";
    }

    # Process output
    open(OUTPUT, $tempFile) || die "Could not open $tempFile";
    my @output;
    while (<OUTPUT>) {
        s/\s+$//;
        push(@output, $_);
    }

    unlink($tempFile);
    return @output;
}

sub log
{
    print scalar(localtime()), ": ", @_, "\n";
}

1;
