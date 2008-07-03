#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2005 Zimbra, Inc.
# 
# The contents of this file are subject to the Yahoo! Public License
# Version 1.0 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# 
# ***** END LICENSE BLOCK *****
# 

use strict;

#############

my $MYSQL = "mysql";
my $LIQUID_USER = "liquid";
my $LIQUID_PASSWORD = "liquid";
if (-f "/opt/liquid/bin/lqlocalconfig") {
    $LIQUID_PASSWORD = `lqlocalconfig -s -m nokey liquid_mysql_password`;
    chomp $LIQUID_PASSWORD;
}
my $DATABASE = "liquid";

#############

my @mailboxIds = runSql($LIQUID_USER,
			$LIQUID_PASSWORD,
			"SELECT id FROM mailbox ORDER BY id");

printLog("Found " . scalar(@mailboxIds) . " mailbox databases.");

my $spamFlag = 16;

my $id;
foreach $id (@mailboxIds) {
    clearSpamFlag($id);
}

exit(0);

#############

sub clearSpamFlag($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<CLEAR_SPAM_FLAG_EOF;

UPDATE $dbName.mail_item
SET flags = flags & ~$spamFlag WHERE flags & $spamFlag;

CLEAR_SPAM_FLAG_EOF

    printLog("Clearing spam flag (16) from $dbName.mail_item.flags.");
    runSql($LIQUID_USER, $LIQUID_PASSWORD, $sql);
}

#############

sub runSql($$$)
{
    my ($user, $password, $script) = @_;

    # Write the last script to a text file for debugging
    # open(LASTSCRIPT, ">lastScript.sql") || die "Could not open lastScript.sql";
    # print(LASTSCRIPT $script);
    # close(LASTSCRIPT);

    # Run the mysql command and redirect output to a temp file
    my $tempFile = "mysql.out";
    my $command = "$MYSQL --user=$user --password=$password " .
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

    return @output;
}

sub printLog
{
    print scalar(localtime()), ": ", @_, "\n";
}
