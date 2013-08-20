-- 
-- ***** BEGIN LICENSE BLOCK *****
-- 
-- Zimbra Collaboration Suite Server
-- Copyright (C) 2013 Zimbra Software, LLC.
-- 
-- The contents of this file are subject to the Zimbra Public License
-- Version 1.4 ("License"); you may not use this file except in
-- compliance with the License.  You may obtain a copy of the License at
-- http://www.zimbra.com/license.
-- 
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
-- 
-- ***** END LICENSE BLOCK *****
-- 


/* Main accounting table, this defines the period the accounting items are over and how to track it */
CREATE TABLE accounting (
	ID			INTEGER PRIMARY KEY AUTOINCREMENT,

	PolicyID		INT8,
	
	Name			VARCHAR(255) NOT NULL,

	/* Tracking Options */
	Track			VARCHAR(255) NOT NULL,  /* Format:   <type>:<spec>

					      SenderIP - This takes a bitmask to mask the IP with. A good default is /24 

					      Sender & Recipient - Either "user@domain" (default), "user@" or "@domain" for the entire 
					      		email addy or email addy domain respectively. 
					   */

	/* Period over which to account traffic */
	AccountingPeriod		SMALLINT NOT NULL,  /* 0 - Track by day, 1 - Track by week, 2 - Track by month */

	/* Limits for this period */
	MessageCountLimit		UNSIGNED BIG INT,  /* Limit is in Kbyte, NULL means no limit */
	MessageCumulativeSizeLimit	UNSIGNED BIG INT,  /* LImit is in Kbyte, NULL means no limit */

	/* Verdict if limits are exceeded */
	Verdict			VARCHAR(255), /* Verdict when limit is exceeded */
	Data			TEXT, /* Data sent along with verdict */
	
	LastAccounting		SMALLINT NOT NULL DEFAULT '0',
		
	Comment			VARCHAR(1024),
	
	Disabled		SMALLINT NOT NULL DEFAULT '0',

	FOREIGN KEY (PolicyID) REFERENCES policies(ID)
) ;



/* This table is used for tracking the accounting */
CREATE TABLE accounting_tracking (

	AccountingID		INT8,
	TrackKey		VARCHAR(512),
	PeriodKey		VARCHAR(512),

	/* Last time this record was update */
	LastUpdate		UNSIGNED BIG INT,  /* NULL means not updated yet */

	MessageCount		UNSIGNED BIG INT,
	MessageCumulativeSize	UNSIGNED BIG INT,  /* Counter is in Kbyte */
	
	UNIQUE (AccountingID,TrackKey,PeriodKey),
	FOREIGN KEY (AccountingID) REFERENCES accounting(ID)
) ;
CREATE INDEX accounting_tracking_idx1 ON accounting_tracking (LastUpdate);


/* Amavisd-new integration for Policyd */

CREATE TABLE amavis_rules (
	ID			INTEGER PRIMARY KEY AUTOINCREMENT,

	PolicyID		INT8,

	Name			VARCHAR(255) NOT NULL,

/*
Mode of operation (the _m columns):

	This is done with the _m column names

	0 - Inherit
	1 - Merge  (only valid for lists)
	2 - Overwrite 

*/


	/* Bypass options */
	bypass_virus_checks	SMALLINT,
	bypass_virus_checks_m	SMALLINT NOT NULL DEFAULT '0',

	bypass_banned_checks	SMALLINT,
	bypass_banned_checks_m	SMALLINT NOT NULL DEFAULT '0',

	bypass_spam_checks	SMALLINT,
	bypass_spam_checks_m	SMALLINT NOT NULL DEFAULT '0',

	bypass_header_checks	SMALLINT,
	bypass_header_checks_m	SMALLINT NOT NULL DEFAULT '0',


	/* Anti-spam options: NULL = inherit */
	spam_tag_level		FLOAT,
	spam_tag_level_m	SMALLINT NOT NULL DEFAULT '0',

	spam_tag2_level		FLOAT,
	spam_tag2_level_m	SMALLINT NOT NULL DEFAULT '0',

	spam_tag3_level		FLOAT,
	spam_tag3_level_m	SMALLINT NOT NULL DEFAULT '0',

	spam_kill_level		FLOAT,
	spam_kill_level_m	SMALLINT NOT NULL DEFAULT '0',

	spam_dsn_cutoff_level	FLOAT,
	spam_dsn_cutoff_level_m	SMALLINT NOT NULL DEFAULT '0',

	spam_quarantine_cutoff_level	FLOAT,
	spam_quarantine_cutoff_level_m	SMALLINT NOT NULL DEFAULT '0',

	spam_modifies_subject	SMALLINT,
	spam_modifies_subject_m	SMALLINT NOT NULL DEFAULT '0',

	spam_tag_subject	VARCHAR(255),  /* _SCORE_ is the score, _REQD_ is the required score */
	spam_tag_subject_m	SMALLINT NOT NULL DEFAULT '0',
	
	spam_tag2_subject	VARCHAR(255),
	spam_tag2_subject_m	SMALLINT NOT NULL DEFAULT '0',
	
	spam_tag3_subject	VARCHAR(255),
	spam_tag3_subject_m	SMALLINT NOT NULL DEFAULT '0',


	/* General checks: NULL = inherit */
	max_message_size	BIGINT,  /* in Kbyte */
	max_message_size_m	SMALLINT NOT NULL DEFAULT '0',

	banned_files		TEXT,
	banned_files_m		SMALLINT NOT NULL DEFAULT '0',


	/* Whitelist & blacklist */
	sender_whitelist	TEXT,
	sender_whitelist_m	SMALLINT NOT NULL DEFAULT '0',

	sender_blacklist	TEXT,
	sender_blacklist_m	SMALLINT NOT NULL DEFAULT '0',


	/* Admin notifications */
	notify_admin_newvirus	VARCHAR(255),
	notify_admin_newvirus_m	SMALLINT NOT NULL DEFAULT '0',

	notify_admin_virus	VARCHAR(255),
	notify_admin_virus_m	SMALLINT NOT NULL DEFAULT '0',

	notify_admin_spam	VARCHAR(255),
	notify_admin_spam_m	SMALLINT NOT NULL DEFAULT '0',

	notify_admin_banned_file	VARCHAR(255),
	notify_admin_banned_file_m	SMALLINT NOT NULL DEFAULT '0',

	notify_admin_bad_header	VARCHAR(255),
	notify_admin_bad_header_m	SMALLINT NOT NULL DEFAULT '0',


	/* Quarantine options */
	quarantine_virus	VARCHAR(255),
	quarantine_virus_m	SMALLINT NOT NULL DEFAULT '0',

	quarantine_banned_file	VARCHAR(255),
	quarantine_banned_file_m	SMALLINT NOT NULL DEFAULT '0',

	quarantine_bad_header	VARCHAR(255),
	quarantine_bad_header_m	SMALLINT NOT NULL DEFAULT '0',
	
	quarantine_spam		VARCHAR(255),
	quarantine_spam_m	SMALLINT NOT NULL DEFAULT '0',


	/* Interception options */
	bcc_to			VARCHAR(255),
	bcc_to_m		SMALLINT NOT NULL DEFAULT '0',


	Comment			VARCHAR(1024),

	Disabled		SMALLINT NOT NULL DEFAULT '0',

	FOREIGN KEY (PolicyID) REFERENCES policies(ID)
) ;

INSERT INTO amavis_rules
	(
		PolicyID,
		Name,
		max_message_size,max_message_size_m,
		bypass_banned_checks, bypass_banned_checks_m
	) 
	VALUES 
	(
		1,
		'Default system amavis policy',
		100000,2,
		1,2
	);

