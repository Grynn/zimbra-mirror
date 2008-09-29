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

CREATE DATABASE zimbra_logger;
ALTER DATABASE zimbra_logger DEFAULT CHARACTER SET utf8;

USE zimbra_logger;

GRANT ALL ON zimbra_logger.* TO 'zimbra' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra_logger.* TO 'zimbra'@'localhost' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra_logger.* TO 'zimbra'@'localhost.localdomain' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra_logger.* TO 'root'@'localhost.localdomain' IDENTIFIED BY 'zimbra';

# Raw log data

CREATE TABLE raw_logs (
	id					BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	log_date			DATETIME NOT NULL,
	loghost				VARCHAR(255) NOT NULL,
	app					VARCHAR(64) NOT NULL,
	pid					INTEGER UNSIGNED NOT NULL DEFAULT 0,
	msg 				TEXT NOT NULL,
	postfix_qid			VARCHAR(25),
	INDEX i_app (app),
	INDEX i_postfix_qid (postfix_qid),
	INDEX i_log_date (log_date)
) ENGINE = MyISAM;

# Processing history (id is the last processed raw_log for app

CREATE TABLE processing_history (
	app					VARCHAR(64) NOT NULL,
	id					BIGINT UNSIGNED
) ENGINE = MyISAM;

# mta

CREATE TABLE mta (
	arrive_time			DATETIME NOT NULL,
	leave_time			DATETIME NOT NULL,
	host				VARCHAR(255) NOT NULL,
	msgid				VARCHAR(64) NOT NULL,
	sender				VARCHAR(255),
	recipient			VARCHAR(255),
	status				VARCHAR(64),
	statusmsg			VARCHAR(255),
	from_host			VARCHAR(255),
	from_IP				VARCHAR(16),
	to_host				VARCHAR(255),
	to_IP				VARCHAR(16),
	qid					VARCHAR(25),
	amavis_pid			VARCHAR(16),
	bytes				INTEGER,
	INDEX i_msgid (msgid),
	INDEX i_arrive_time (arrive_time),
	INDEX i_qid (qid)
) ENGINE = MyISAM;

CREATE TABLE amavis (
	arrive_time			DATETIME NOT NULL,
	host				VARCHAR(255) NOT NULL,
	pid					VARCHAR(16),
	msgid				VARCHAR(64) NOT NULL,
	sender				VARCHAR(255),
	recipient			VARCHAR(255),
	disposition			VARCHAR(16),
	status				VARCHAR(16),
	reason				VARCHAR(64),
	fromIP				VARCHAR(16),
	origIP				VARCHAR(16),
	hits				FLOAT,
	time				INTEGER,
	INDEX i_msgid (msgid),
	INDEX i_arrive_time (arrive_time)
) ENGINE = MyISAM;

CREATE TABLE mta_aggregate (
	period_start		DATETIME NOT NULL,
	period_end			DATETIME NOT NULL,
	host				VARCHAR(255) NOT NULL,
	period				ENUM ('hour','day','month','year'),
	msg_count			INTEGER UNSIGNED,
	msg_bytes			INTEGER UNSIGNED,
	INDEX i_period_start (period_start),
	INDEX i_period_end (period_end)
) ENGINE = MyISAM;

CREATE TABLE amavis_aggregate (
	period_start		DATETIME NOT NULL,
	period_end			DATETIME NOT NULL,
	host				VARCHAR(255) NOT NULL,
	period				ENUM ('hour','day','month','year'),
	msg_count			INTEGER UNSIGNED,
	spam_count			INTEGER UNSIGNED,
	virus_count			INTEGER UNSIGNED,
	INDEX i_period_start (period_start),
	INDEX i_period_end (period_end)
) ENGINE = MyISAM;

# table for status
CREATE TABLE service_status (
	server      VARCHAR(255) NOT NULL,
	service     VARCHAR(255) NOT NULL,
	time        DATETIME,
	status      BOOL,
	loghostname      VARCHAR(255) NOT NULL,

	UNIQUE INDEX i_server_service (server(100), service(100))
) ENGINE = MyISAM;

# table for disk status
CREATE TABLE disk_status (
	server      VARCHAR(255) NOT NULL,
	time        DATETIME,
	device		VARCHAR(64),
	mount_point VARCHAR(64),
	total		INTEGER UNSIGNED,
	available   INTEGER UNSIGNED
) ENGINE = MyISAM;

CREATE TABLE disk_aggregate (
	period_start		DATETIME NOT NULL,
	period_end			DATETIME NOT NULL,
	host				VARCHAR(255) NOT NULL,
	period				ENUM ('hour','day','month','year'),
	device				VARCHAR(64),
	total				INTEGER UNSIGNED,
	available			INTEGER UNSIGNED,
	INDEX i_device (device),
	INDEX i_host (host),
	INDEX i_period_start (period_start),
	INDEX i_period_end (period_end)
) ENGINE = MyISAM;

CREATE TABLE config (
	name        VARCHAR(255) NOT NULL PRIMARY KEY,
	value       TEXT,
	description TEXT,
	modified    TIMESTAMP
) ENGINE = MyISAM;

