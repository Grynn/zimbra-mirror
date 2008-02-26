-- 
-- ***** BEGIN LICENSE BLOCK *****
-- Zimbra Collaboration Suite, Network Edition.
-- Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
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

