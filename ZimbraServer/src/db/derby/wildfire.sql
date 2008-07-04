--
-- ***** BEGIN LICENSE BLOCK *****
--
-- Zimbra Collaboration Suite Server
-- Copyright (C) 2007 Zimbra, Inc.
--
-- The contents of this file are subject to the Yahoo! Public License
-- Version 1.0 ("License"); you may not use this file except in
-- compliance with the License.  You may obtain a copy of the License at
-- http://www.zimbra.com/license.
--
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
--
-- ***** END LICENSE BLOCK *****
--

SET SCHEMA zimbra;

-- -----------------------------------------------------------------------
-- IM tables (Wildfire code)
-- -----------------------------------------------------------------------
CREATE TABLE jiveUserProp (
   username              VARCHAR(200)    NOT NULL,
   name                  VARCHAR(100)    NOT NULL,
   propValue             CLOB            NOT NULL,

   CONSTRAINT pk_jiveUserProp PRIMARY KEY (username, name)
);

CREATE TABLE jiveGroupProp (
   groupName             VARCHAR(50)     NOT NULL,
   name                  VARCHAR(100)    NOT NULL,
   propValue             CLOB            NOT NULL,
   CONSTRAINT pk_jiveGroupProp PRIMARY KEY (groupName, name)
);

CREATE TABLE jiveGroupUser (
   groupName             VARCHAR(50)     NOT NULL,
   username              VARCHAR(200)    NOT NULL,
   administrator         SMALLINT        NOT NULL,

   CONSTRAINT pk_jiveGroupUser PRIMARY KEY (groupName, username, administrator)
);

CREATE TABLE jivePrivate (
   username              VARCHAR(200)    NOT NULL,
   name                  VARCHAR(100)    NOT NULL,
   namespace             VARCHAR(200)    NOT NULL,
   value                 CLOB            NOT NULL,

   CONSTRAINT pk_jivePrivate PRIMARY KEY (username, name, namespace)
);

CREATE TABLE jiveOffline (
   username              VARCHAR(200)    NOT NULL,
   messageID             BIGINT          NOT NULL,
   creationDate          CHAR(15)        NOT NULL,
   messageSize           INTEGER         NOT NULL,
   message               CLOB            NOT NULL,

   CONSTRAINT pk_jiveOffline PRIMARY KEY (username, messageID)
);

CREATE TABLE jiveRoster (
   rosterID              BIGINT          NOT NULL,
   username              VARCHAR(200)    NOT NULL,
   jid                   VARCHAR(32672)  NOT NULL,
   sub                   SMALLINT        NOT NULL,
   ask                   SMALLINT        NOT NULL,
   recv                  SMALLINT        NOT NULL,
   nick                  VARCHAR(200),

   CONSTRAINT pk_jiveRoster PRIMARY KEY (rosterID)
);

CREATE INDEX jiveRoster_unameid_idx ON jiveRoster(username);

CREATE TABLE jiveRosterGroups (
   rosterID              BIGINT          NOT NULL,
   rank                  SMALLINT        NOT NULL,
   groupName             VARCHAR(200)    NOT NULL,

   CONSTRAINT pk_jiveRosterGroups PRIMARY KEY (rosterID, rank)
);

CREATE INDEX jiveRosterGroup_rosterid_idx ON jiveRosterGroups(rosterID);

CREATE TABLE jiveVCard (
   username              VARCHAR(200)    NOT NULL,
   value                 CLOB            NOT NULL,

   CONSTRAINT pk_jiveVCard PRIMARY KEY (username)
);

CREATE TABLE jiveID (
   idType                INTEGER         NOT NULL,
   id                    BIGINT          NOT NULL,

   CONSTRAINT pk_jiveID PRIMARY KEY (idType)
);

CREATE TABLE jiveProperty (
   name                  VARCHAR(100)    NOT NULL,
   propValue             CLOB            NOT NULL,

   CONSTRAINT pk_jiveProperty PRIMARY KEY (name)
);

CREATE TABLE jiveVersion (
   name                  VARCHAR(50)     NOT NULL,
   version               INTEGER         NOT NULL,

   CONSTRAINT pk_jiveVersion PRIMARY KEY (name)
);

CREATE TABLE jiveExtComponentConf (
   subdomain             VARCHAR(200)    NOT NULL,
   secret                VARCHAR(200),
   permission            VARCHAR(10)     NOT NULL,

   CONSTRAINT pk_jiveExtComponentConf PRIMARY KEY (subdomain)
);

CREATE TABLE jiveRemoteServerConf (
   domain                VARCHAR(200)    NOT NULL,
   remotePort            INTEGER,
   permission            VARCHAR(10)     NOT NULL,

   CONSTRAINT pk_jiveRemoteServerConf PRIMARY KEY (domain)
);

CREATE TABLE jivePrivacyList (
   username              VARCHAR(200)    NOT NULL,
   name                  VARCHAR(100)    NOT NULL,
   isDefault             SMALLINT        NOT NULL,
   list                  CLOB            NOT NULL,

   CONSTRAINT pk_jivePrivacyList PRIMARY KEY (username, name)
);

CREATE INDEX jivePList_default_idx ON jivePrivacyList(username, isDefault);

CREATE TABLE jiveSASLAuthorized (
   username              VARCHAR(200)    NOT NULL,
   principal             VARCHAR(32672)  NOT NULL,

   CONSTRAINT pk_jiveSASLAuthorized PRIMARY KEY (username, principal)
);

CREATE TABLE mucRoom (
   roomID                BIGINT          NOT NULL,
   creationDate          CHAR(15)        NOT NULL,
   modificationDate      CHAR(15)        NOT NULL,
   name                  VARCHAR(50)     NOT NULL,
   naturalName           VARCHAR(255)    NOT NULL,
   description           VARCHAR(255),
   lockedDate            CHAR(15)        NOT NULL,
   emptyDate             CHAR(15),
   canChangeSubject      SMALLINT        NOT NULL,
   maxUsers              INTEGER         NOT NULL,
   publicRoom            SMALLINT        NOT NULL,
   moderated             SMALLINT        NOT NULL,
   membersOnly           SMALLINT        NOT NULL,
   canInvite             SMALLINT        NOT NULL,
   password              VARCHAR(50),
   canDiscoverJID        SMALLINT        NOT NULL,
   logEnabled            SMALLINT        NOT NULL,
   subject               VARCHAR(100),
   rolesToBroadcast      SMALLINT        NOT NULL,
   useReservedNick       SMALLINT        NOT NULL,
   canChangeNick         SMALLINT        NOT NULL,
   canRegister           SMALLINT        NOT NULL,

   CONSTRAINT pk_mucRoom PRIMARY KEY (name)
);

CREATE INDEX mucRoom_roomid_idx ON mucRoom(roomID);

CREATE TABLE mucRoomProp (
   roomID                BIGINT          NOT NULL,
   name                  VARCHAR(100)    NOT NULL,
   propValue             CLOB            NOT NULL,

   CONSTRAINT pk_mucRoomProp PRIMARY KEY (roomID, name)
);

CREATE TABLE mucAffiliation (
   roomID                BIGINT          NOT NULL,
   jid                   VARCHAR(32672)  NOT NULL,
   affiliation           SMALLINT        NOT NULL,

   CONSTRAINT pk_mucAffiliation PRIMARY KEY (roomID, jid)
);

CREATE TABLE mucMember (
   roomID                BIGINT          NOT NULL,
   jid                   VARCHAR(32672)  NOT NULL,
   nickname              VARCHAR(255),
   firstName             VARCHAR(100),
   lastName              VARCHAR(100),
   url                   VARCHAR(100),
   email                 VARCHAR(100),
   faqentry              VARCHAR(100),

   CONSTRAINT pk_mucMember PRIMARY KEY (roomID, jid)
);

CREATE TABLE mucConversationLog (
   roomID                BIGINT          NOT NULL,
   sender                CLOB            NOT NULL,
   nickname              VARCHAR(255),
   time                  CHAR(15)        NOT NULL,
   subject               VARCHAR(255),
   body                  CLOB
);

CREATE INDEX mucLog_time_idx ON mucConversationLog(time);

-- Finally, insert default table values.
INSERT INTO jiveID (idType, id) VALUES (18, 1);
INSERT INTO jiveID (idType, id) VALUES (19, 1);
INSERT INTO jiveID (idType, id) VALUES (23, 1);
INSERT INTO jiveVersion (name, version) VALUES ('wildfire', 10);
