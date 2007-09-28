-- 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite, Network Edition.
# Copyright (C) 2007 Zimbra, Inc.  All Rights Reserved.
# 
# ***** END LICENSE BLOCK *****
-- 

USE zimbrame;

INSERT INTO devices (id,jadfile,brand,model,locale,version)
	VALUES ("Generic-MppPhone","/var/tmp/zimbrame-Generic-MppPhone.jad","MPP","MPP","en_US","1.0");
INSERT INTO devices (id,jadfile,brand,model,locale,version)
	VALUES ("Motorola-V3-CLDC1.0-en_US","/var/tmp/zimbrame-Motorola-V3-CLDC1.0-en_US.jad","Motorola","V3","en_US","1.0");
INSERT INTO devices (id,jadfile,brand,model,locale,version)
	VALUES ("Motorola-V3xx-CLDC1.0-en_US","/var/tmp/zimbrame-Motorola-V3xx-CLDC1.0-en_US.jad","Motorola","V3xx","en_US","1.0");
INSERT INTO devices (id,jadfile,brand,model,locale,version)
	VALUES ("Nokia-N73-en_US","/var/tmp/zimbrame-Nokia-N73-en_US.jad","Nokia","N73","en_US","1.0");
INSERT INTO devices (id,jadfile,brand,model,locale,version)
	VALUES ("Motorola-V3-CLDC1.0-en_US","/var/tmp/zimbrame-Motorola-V3-CLDC1.0-en_US.jad","Motorola","V3","en_US","1.0");


-- action: 1 (download), 2 (install), 3 (uninstall)

INSERT INTO stats (brand,model,locale,ip,ua,email,version,timestamp,action)
	VALUES ("Generic","MPP","en_US","192.168.1.1","Mozilla/5.0","jylee@zimbra.com","0.0.1","2007-09-18 17:32:51",1);

