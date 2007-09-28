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
