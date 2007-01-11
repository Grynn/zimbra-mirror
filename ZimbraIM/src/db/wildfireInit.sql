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
-- Portions created by Zimbra are Copyright (C) 2004, 2005, 2006 Zimbra, Inc.
-- All Rights Reserved.
-- 
-- Contributor(s):
-- 
-- ***** END LICENSE BLOCK *****
--

USE zimbra;

CREATE TABLE jiveUserProp (
  username           VARCHAR(200)     NOT NULL,
  name               VARCHAR(100)    NOT NULL,
  propValue          TEXT            NOT NULL,
  PRIMARY KEY (username, name)
);

CREATE TABLE jiveGroupProp (
  groupName             VARCHAR(50)     NOT NULL,
  name                  VARCHAR(100)    NOT NULL,
  propValue             TEXT            NOT NULL,
  PRIMARY KEY (groupName, name)
);

CREATE TABLE jiveGroupUser (
  groupName             VARCHAR(50)     NOT NULL,
  username               VARCHAR(200)    NOT NULL,
  administrator         TINYINT         NOT NULL,
  PRIMARY KEY (groupName, username, administrator)
);

CREATE TABLE jivePrivate (
  username               VARCHAR(200)     NOT NULL,
  name                  VARCHAR(100)    NOT NULL,
  namespace             VARCHAR(200)    NOT NULL,
  value                 TEXT            NOT NULL,
  PRIMARY KEY (username, name, namespace(20))
);

CREATE TABLE jiveOffline (
  username               VARCHAR(200)     NOT NULL,
  messageID             BIGINT          NOT NULL,
  creationDate          CHAR(15)        NOT NULL,
  messageSize           INTEGER         NOT NULL,
  message               TEXT            NOT NULL,
  PRIMARY KEY (username, messageID)
);

CREATE TABLE jiveRoster (
  rosterID              BIGINT          NOT NULL,
  username               VARCHAR(200)    NOT NULL,
  jid                   TEXT            NOT NULL,
  sub                   TINYINT         NOT NULL,
  ask                   TINYINT         NOT NULL,
  recv                  TINYINT         NOT NULL,
  nick                  VARCHAR(200),
  PRIMARY KEY (rosterID),
  INDEX jiveRoster_unameid_idx (username)
);

CREATE TABLE jiveRosterGroups (
  rosterID              BIGINT          NOT NULL,
  rank                  TINYINT         NOT NULL,
  groupName             VARCHAR(200)    NOT NULL,
  PRIMARY KEY (rosterID, rank),
  INDEX jiveRosterGroup_rosterid_idx (rosterID)
);

CREATE TABLE jiveVCard (
  username              VARCHAR(200)     NOT NULL,
  value                 TEXT            NOT NULL,
  PRIMARY KEY (username)
);

CREATE TABLE jiveID (
  idType                INTEGER         NOT NULL,
  id                    BIGINT          NOT NULL,
  PRIMARY KEY (idType)
);

CREATE TABLE jiveProperty (
  name        VARCHAR(100)              NOT NULL,
  propValue   TEXT                      NOT NULL,
  PRIMARY KEY (name)
);


CREATE TABLE jiveVersion (
  name     VARCHAR(50)  NOT NULL,
  version  INTEGER  NOT NULL,
  PRIMARY KEY (name)
);

CREATE TABLE jiveExtComponentConf (
  subdomain             VARCHAR(200)    NOT NULL,
  secret                VARCHAR(200),
  permission            VARCHAR(10)     NOT NULL,
  PRIMARY KEY (subdomain)
);

CREATE TABLE jiveRemoteServerConf (
  domain                VARCHAR(200)    NOT NULL,
  remotePort            INTEGER,
  permission            VARCHAR(10)     NOT NULL,
  PRIMARY KEY (domain)
);

CREATE TABLE jivePrivacyList (
  username               VARCHAR(200)     NOT NULL,
  name                  VARCHAR(100)    NOT NULL,
  isDefault             TINYINT         NOT NULL,
  list                  TEXT            NOT NULL,
  PRIMARY KEY (username, name),
  INDEX jivePList_default_idx (username, isDefault)
);

CREATE TABLE jiveSASLAuthorized (
  username             VARCHAR(200)   NOT NULL,
  principal           TEXT          NOT NULL,
  PRIMARY KEY (username, principal(100))
);


# Finally, insert default table values.

INSERT INTO jiveID (idType, id) VALUES (18, 1);
INSERT INTO jiveID (idType, id) VALUES (19, 1);
INSERT INTO jiveID (idType, id) VALUES (23, 1);

INSERT INTO jiveVersion (name, version) VALUES ('wildfire', 10);

# enable AOL and MSN gateway

INSERT INTO jiveProperty SET name="plugin.gateway.aim.enabled", propValue="true";
INSERT INTO jiveProperty SET name="plugin.gateway.msn.enabled", propValue="true";
