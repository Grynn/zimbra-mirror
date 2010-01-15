-- 
-- ***** BEGIN LICENSE BLOCK *****
-- 
-- Zimbra Collaboration Suite Server
-- Copyright (C) 2009 Zimbra, Inc.
-- 
-- The contents of this file are subject to the Zimbra Public License
-- Version 1.2 ("License"); you may not use this file except in
-- compliance with the License.  You may obtain a copy of the License at
-- http://www.zimbra.com/license.
-- 
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
-- 
-- ***** END LICENSE BLOCK *****
-- 
CONNECT 'jdbc:derby:@ZIMBRA_INSTALL@derby;create=true';

RUN '@ZIMBRA_INSTALL@db/db.sql';
RUN '@ZIMBRA_INSTALL@db/wildfire.sql';
RUN '@ZIMBRA_INSTALL@db/versions-init.sql';
RUN '@ZIMBRA_INSTALL@db/default-volumes.sql';

EXIT;

