-- 
-- ***** BEGIN LICENSE BLOCK *****
-- Zimbra Collaboration Suite J2ME Client
-- Copyright (C) 2007 Zimbra, Inc.
-- 
-- The contents of this file are subject to the Yahoo! Public License
-- Version 1.0 ("License"); you may not use this file except in
-- compliance with the License.  You may obtain a copy of the License at
-- http://www.zimbra.com/license.
-- 
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
-- ***** END LICENSE BLOCK *****
-- 

-- DROP DATABASE zimbrame;
CREATE DATABASE zimbrame;
ALTER DATABASE zimbrame DEFAULT CHARACTER SET utf8;

USE zimbrame;

-- action: 1 (download), 2 (install), 3 (uninstall)

-- stats
CREATE TABLE stats (
   timestamp VARCHAR(32) NOT NULL,
   action    INT UNSIGNED NOT NULL,
   ip        VARCHAR(64) NOT NULL,
   ua        VARCHAR(256) NOT NULL,
   brand     VARCHAR(32),
   model     VARCHAR(32),
   locale    VARCHAR(32),
   email     VARCHAR(32),
   version   VARCHAR(16),

   INDEX i_ip (ip),
   INDEX i_ua (ua),
   INDEX i_email (email),
   INDEX i_timestamp (timestamp)
) ENGINE = MyISAM;


-- devices
CREATE TABLE devices (
   id       VARCHAR(128) NOT NULL,
   jadfile  VARCHAR(128) NOT NULL,
   brand    VARCHAR(32) NOT NULL,
   model    VARCHAR(32) NOT NULL,
   locale   VARCHAR(32) NOT NULL,
   active   TINYINT NOT NULL DEFAULT 0,
   version  VARCHAR(16),

   INDEX i_id (id),
   INDEX i_jadfile (jadfile),
   PRIMARY KEY i_device (brand,model,locale)
) ENGINE = MyISAM;

