# 
# ***** BEGIN LICENSE BLOCK *****
# Version: ZPL 1.1
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 
package Migrate;

use strict;

#############

my $MYSQL = "mysql";
my $DB_USER = "zimbra";
my $DB_PASSWORD = "zimbra";
my $DATABASE = "zimbra";
my $ZIMBRA_HOME = $ENV{ZIMBRA_HOME} || '/opt/zimbra';
my $ZMLOCALCONFIG = "$ZIMBRA_HOME/bin/zmlocalconfig";

if (-f $ZMLOCALCONFIG) {
    $DB_PASSWORD = `$ZMLOCALCONFIG -s -m nokey zimbra_mysql_password`;
    chomp $DB_PASSWORD;
    $DB_USER = `$ZMLOCALCONFIG -m nokey zimbra_mysql_user`;
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
