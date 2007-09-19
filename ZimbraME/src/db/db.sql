-- 
-- ***** BEGIN LICENSE BLOCK *****
-- Version: MPL 1.1
-- 
-- The contents of this file are subject to the Mozilla Public License
-- Version 1.1 ("License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the License at
-- http://www.zimbra.com/license
-- 
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
-- the License for the specific language governing rights and limitations
-- under the License.
-- 
-- The Original Code is: Zimbra Collaboration Suite Server.
-- 
-- The Initial Developer of the Original Code is Zimbra, Inc.
-- Portions created by Zimbra are Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
-- All Rights Reserved.
-- 
-- Contributor(s):
-- 
-- ***** END LICENSE BLOCK *****
-- 

DROP DATABASE zimbrame;
CREATE DATABASE zimbrame;
ALTER DATABASE zimbrame DEFAULT CHARACTER SET utf8;

USE zimbrame;

GRANT ALL ON zimbrame.* TO 'zimbra' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbrame.* TO 'zimbra'@'localhost' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbrame.* TO 'zimbra'@'localhost.localdomain' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbrame.* TO 'root'@'localhost.localdomain' IDENTIFIED BY 'zimbra';

-- action: 1 (download), 2 (install), 3 (uninstall)

-- stats
CREATE TABLE stats (
   brand     VARCHAR(32) NOT NULL,
   model     VARCHAR(32) NOT NULL,
   locale    VARCHAR(32) NOT NULL,
   jadfile   VARCHAR(128) NOT NULL,
   ip        VARCHAR(64) NOT NULL,
   ua        VARCHAR(256) NOT NULL,
   email     VARCHAR(32),
   timestamp VARCHAR(32) NOT NULL,
   action    INT UNSIGNED NOT NULL,

   INDEX i_name (brand,model,jadfile),
   INDEX i_ip (ip),
   INDEX i_ua (ua),
   INDEX i_email (email)
) ENGINE = MyISAM;


-- devices
CREATE TABLE devices (
   jadfile  VARCHAR(128) NOT NULL,
   brand    VARCHAR(32) NOT NULL,
   model    VARCHAR(32) NOT NULL,
   locale   VARCHAR(32) NOT NULL,
   version  VARCHAR(16),

   INDEX i_jadfile (jadfile),
   INDEX i_brand (brand),
   INDEX i_model (model)
) ENGINE = MyISAM;

