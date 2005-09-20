# 
# ***** BEGIN LICENSE BLOCK *****
# Version: ZPL 1.1
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 

CREATE DATABASE zimbra_logger;
ALTER DATABASE zimbra_logger DEFAULT CHARACTER SET utf8;

USE zimbra_logger;

GRANT ALL ON zimbra_logger.* TO 'zimbra' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra_logger.* TO 'zimbra'@'localhost' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra_logger.* TO 'zimbra'@'localhost.localdomain' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra_logger.* TO 'root'@'localhost.localdomain' IDENTIFIED BY 'zimbra';

# Raw log data

CREATE TABLE raw_logs (
	id					INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	log_date			DATETIME NOT NULL,
	host				VARCHAR(255) NOT NULL,
	app					VARCHAR(64) NOT NULL,
	pid					INTEGER UNSIGNED NOT NULL DEFAULT 0,
	msg 				TEXT NOT NULL,
	postfix_msgid		VARCHAR(12),
	INDEX i_app (app),
	INDEX i_postfix_msgid (postfix_msgid),
	INDEX i_log_date (log_date),
	INDEX i_host (host)
) ENGINE = MyISAM;

# Processing history (id is the last processed raw_log for app

CREATE TABLE processing_history (
	app					VARCHAR(64) NOT NULL,
	id					INTEGER UNSIGNED NOT NULL
) ENGINE = MyISAM;

# mta

CREATE TABLE mta (
	arrive_time			DATETIME NOT NULL,
	leave_time			DATETIME NOT NULL,
	host				VARCHAR(255) NOT NULL,
	msgid				VARCHAR(16),
	next_msgid			VARCHAR(16),
	sender				VARCHAR(255),
	recipient			VARCHAR(255),
	status				VARCHAR(64),
	from_host			VARCHAR(255),
	from_IP				VARCHAR(16),
	to_host				VARCHAR(255),
	to_IP				VARCHAR(16),
	INDEX i_msgid (msgid),
	INDEX i_arrive_time (arrive_time),
	INDEX i_leave_time (leave_time),
	INDEX i_host (host)
) ENGINE = MyISAM;

