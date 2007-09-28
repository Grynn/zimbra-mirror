#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2005, 2006 Zimbra, Inc.
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
my $ROOT_USER = "root";
my $ROOT_PASSWORD = "liquid";
my $LIQUID_USER = "liquid";
my $LIQUID_PASSWORD = "liquid";
my $PASSWORD = "liquid";
my $DATABASE = "liquid";

#############

dropConstraints();
addConstraints();

exit(0);

#############


sub dropConstraints($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
	
    my $sql = <<DROP_CONSTRAINTS_EOF;
    
ALTER TABLE liquid.blob_info
    DROP INDEX uri,
    DROP INDEX idx_blob_digest;

ALTER TABLE liquid.current_volumes
    DROP INDEX message_volume_id,
    DROP INDEX index_volume_id,
    DROP FOREIGN KEY current_volumes_ibfk_1,
    DROP FOREIGN KEY current_volumes_ibfk_2;
    
ALTER TABLE liquid.mailbox
    DROP INDEX account_id,
    DROP INDEX message_volume_id,
    DROP INDEX index_volume_id,
    DROP FOREIGN KEY mailbox_ibfk_1,
    DROP FOREIGN KEY mailbox_ibfk_2;

ALTER TABLE liquid.out_of_office
    DROP FOREIGN KEY out_of_office_ibfk_1;

ALTER TABLE liquid.server_stat
    DROP INDEX `time`,
    DROP INDEX `name`;
    
ALTER TABLE liquid.shared_mime_part
    DROP INDEX message_blob_id,
    DROP INDEX part_blob_id;
    
DROP_CONSTRAINTS_EOF

    printLog("Dropping constraints.");
    runSql($ROOT_USER, $ROOT_PASSWORD, $sql);
}

sub addConstraints($)
{
    my ($mailboxId) = @_;
    my $dbName = "mailbox" . $mailboxId;
    my $sql = <<ADD_CONSTRAINTS_EOF;

ALTER TABLE liquid.blob_info
    ADD INDEX i_uri (uri),
    ADD UNIQUE INDEX i_digest (digest);
    
ALTER TABLE liquid.current_volumes
   ADD INDEX i_message_volume_id (message_volume_id),
   ADD INDEX i_index_volume_id (index_volume_id),
   ADD CONSTRAINT fk_current_volumes_message_volume_id FOREIGN KEY (message_volume_id) REFERENCES volume(id),
   ADD CONSTRAINT fk_current_volumes_index_volume_id FOREIGN KEY (index_volume_id)   REFERENCES volume(id);

ALTER TABLE liquid.mailbox
   ADD UNIQUE INDEX i_account_id (account_id),
   ADD INDEX i_message_volume_id (message_volume_id),
   ADD INDEX i_index_volume_id (index_volume_id),
   ADD CONSTRAINT fk_mailbox_message_volume_id FOREIGN KEY (message_volume_id) REFERENCES volume(id),
   ADD CONSTRAINT fk_mailbox_index_volume_id FOREIGN KEY (index_volume_id) REFERENCES volume(id);

ALTER TABLE liquid.out_of_office
   ADD CONSTRAINT fk_out_of_office_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE;

ALTER TABLE liquid.server_stat
   ADD INDEX i_name (name),
   ADD INDEX i_time (time);

ALTER TABLE liquid.shared_mime_part
   ADD INDEX i_message_blob_id (message_blob_id),
   ADD INDEX i_part_blob_id (part_blob_id);

ADD_CONSTRAINTS_EOF

    printLog("Adding constraints.");
    runSql($ROOT_USER, $ROOT_PASSWORD, $sql);
}	

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
