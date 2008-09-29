#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2006 Zimbra, Inc.
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
use Migrate;


Migrate::verifyLoggerSchemaVersion(5);

modifyQID();

Migrate::updateLoggerSchemaVersion(5,6);

exit(0);

#####################

sub modifyQID() {
    Migrate::log("Modifying postfix_qid size");

	my $sql = <<EOF;
alter table raw_logs modify postfix_qid VARCHAR(25);
alter table mta modify qid VARCHAR(25);
EOF

    Migrate::runLoggerSql($sql);
}
