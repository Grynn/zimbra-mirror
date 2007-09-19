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

USE zimbrame;

INSERT INTO devices (jadfile,brand,model,locale,version)
	VALUES ("/var/tmp/zimbrame-Generic-MppPhone.jad","MPP","MPP","en_US","1.0");
INSERT INTO devices (jadfile,brand,model,locale,version)
	VALUES ("/var/tmp/zimbrame-Motorola-V3-CLDC1.0-en_US.jad","Motorola","V3","en_US","1.0");
INSERT INTO devices (jadfile,brand,model,locale,version)
	VALUES ("/var/tmp/zimbrame-Motorola-V3xx-CLDC1.0-en_US.jad","Motorola","V3xx","en_US","1.0");
INSERT INTO devices (jadfile,brand,model,locale,version)
	VALUES ("/var/tmp/zimbrame-Nokia-N73-en_US.jad","Nokia","N73","en_US","1.0");
INSERT INTO devices (jadfile,brand,model,locale,version)
	VALUES ("/var/tmp/zimbrame-Motorola-V3-CLDC1.0-en_US.jad","Motorola","V3","en_US","1.0");


-- action: 1 (download), 2 (install), 3 (uninstall)

INSERT INTO stats (jadfile,brand,model,locale,ip,ua,email,timestamp,action)
	VALUES ("zimbrame-Generic-MppPhone","MPP","MPP","en_US","192.168.1.1","Mozilla/5.0","jylee@zimbra.com","2007-09-18 17:32:51",1);

