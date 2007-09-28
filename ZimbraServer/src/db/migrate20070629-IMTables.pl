#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2007 Zimbra, Inc.
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


use strict;
use Migrate;
my $concurrent = 10;

my @groups = Migrate::getMailboxGroups();

Migrate::verifySchemaVersion(40);

# Coming in, the server either has:
#   - no IM tables
#   - IM tables with old names
#
# On exit, the server will have IM tables.
#
my $sql = <<_SQL_;

USE zimbra;

CREATE TABLE IF NOT EXISTS jiveUserProp (
                           username           VARCHAR(200)     NOT NULL,
                           name               VARCHAR(100)    NOT NULL,
                           propValue          TEXT            NOT NULL,
                           PRIMARY KEY (username, name)
                          );

CREATE TABLE IF NOT EXISTS jiveGroupProp (
                            groupName             VARCHAR(50)     NOT NULL,
                            name                  VARCHAR(100)    NOT NULL,
                            propValue             TEXT            NOT NULL,
                            PRIMARY KEY (groupName, name)
                           );

CREATE TABLE IF NOT EXISTS jiveGroupUser (
                            groupName             VARCHAR(50)     NOT NULL,
                            username               VARCHAR(200)    NOT NULL,
                            administrator         TINYINT         NOT NULL,
                            PRIMARY KEY (groupName, username, administrator)
                           );

CREATE TABLE IF NOT EXISTS jivePrivate (
                          username               VARCHAR(200)     NOT NULL,
                          name                  VARCHAR(100)    NOT NULL,
                          namespace             VARCHAR(200)    NOT NULL,
                          value                 TEXT            NOT NULL,
                          PRIMARY KEY (username, name, namespace(20))
                         );

CREATE TABLE IF NOT EXISTS jiveOffline (
                          username               VARCHAR(200)     NOT NULL,
                          messageID             BIGINT          NOT NULL,
                          creationDate          CHAR(15)        NOT NULL,
                          messageSize           INTEGER         NOT NULL,
                          message               TEXT            NOT NULL,
                          PRIMARY KEY (username, messageID)
                         );

CREATE TABLE IF NOT EXISTS jiveRoster (
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

CREATE TABLE IF NOT EXISTS jiveRosterGroups (
                               rosterID              BIGINT          NOT NULL,
                               rank                  TINYINT         NOT NULL,
                               groupName             VARCHAR(200)    NOT NULL,
                               PRIMARY KEY (rosterID, rank),
                               INDEX jiveRosterGroup_rosterid_idx (rosterID)
                              );

CREATE TABLE IF NOT EXISTS jiveVCard (
                        username              VARCHAR(200)     NOT NULL,
                        value                 TEXT            NOT NULL,
                        PRIMARY KEY (username)
                       );

CREATE TABLE IF NOT EXISTS jiveID (
                     idType                INTEGER         NOT NULL,
                     id                    BIGINT          NOT NULL,
                     PRIMARY KEY (idType)
                    );

CREATE TABLE IF NOT EXISTS jiveProperty (
                           name        VARCHAR(100)              NOT NULL,
                           propValue   TEXT                      NOT NULL,
                           PRIMARY KEY (name)
                          );


CREATE TABLE IF NOT EXISTS jiveVersion (
                          name     VARCHAR(50)  NOT NULL,
                          version  INTEGER  NOT NULL,
                          PRIMARY KEY (name)
                         );

CREATE TABLE IF NOT EXISTS jiveExtComponentConf (
                                   subdomain             VARCHAR(200)    NOT NULL,
                                   secret                VARCHAR(200),
                                   permission            VARCHAR(10)     NOT NULL,
                                   PRIMARY KEY (subdomain)
                                  );

CREATE TABLE IF NOT EXISTS jiveRemoteServerConf (
                                   domain                VARCHAR(200)    NOT NULL,
                                   remotePort            INTEGER,
                                   permission            VARCHAR(10)     NOT NULL,
                                   PRIMARY KEY (domain)
                                  );

CREATE TABLE IF NOT EXISTS jivePrivacyList (
                              username               VARCHAR(200)     NOT NULL,
                              name                  VARCHAR(100)    NOT NULL,
                              isDefault             TINYINT         NOT NULL,
                              list                  TEXT            NOT NULL,
                              PRIMARY KEY (username, name),
                              INDEX jivePList_default_idx (username, isDefault)
                             );

CREATE TABLE IF NOT EXISTS jiveSASLAuthorized (
                                 username             VARCHAR(200)   NOT NULL,
                                 principal           TEXT          NOT NULL,
                                 PRIMARY KEY (username, principal(100))
                                );

CREATE TABLE IF NOT EXISTS mucRoom (
                      roomID              BIGINT        NOT NULL,
                      creationDate        CHAR(15)      NOT NULL,
                      modificationDate    CHAR(15)      NOT NULL,
                      name                VARCHAR(50)   NOT NULL,
                      naturalName         VARCHAR(255)  NOT NULL,
                      description         VARCHAR(255),
                      lockedDate          CHAR(15)      NOT NULL,
                      emptyDate           CHAR(15)      NULL,
                      canChangeSubject    TINYINT       NOT NULL,
                      maxUsers            INTEGER       NOT NULL,
                      publicRoom          TINYINT       NOT NULL,
                      moderated           TINYINT       NOT NULL,
                      membersOnly         TINYINT       NOT NULL,
                      canInvite           TINYINT       NOT NULL,
                      password            VARCHAR(50)   NULL,
                      canDiscoverJID      TINYINT       NOT NULL,
                      logEnabled          TINYINT       NOT NULL,
                      subject             VARCHAR(100)  NULL,
                      rolesToBroadcast    TINYINT       NOT NULL,
                      useReservedNick     TINYINT       NOT NULL,
                      canChangeNick       TINYINT       NOT NULL,
                      canRegister         TINYINT       NOT NULL,
                      PRIMARY KEY (name),
                      INDEX mucRoom_roomid_idx (roomID)
                     );

CREATE TABLE IF NOT EXISTS mucRoomProp (
                          roomID                BIGINT          NOT NULL,
                          name                  VARCHAR(100)    NOT NULL,
                          propValue             TEXT            NOT NULL,
                          PRIMARY KEY (roomID, name)
                         );

CREATE TABLE IF NOT EXISTS mucAffiliation (
                             roomID              BIGINT        NOT NULL,
                             jid                 TEXT          NOT NULL,
                             affiliation         TINYINT       NOT NULL,
                             PRIMARY KEY (roomID,jid(70))
                            );

CREATE TABLE IF NOT EXISTS mucMember (
                        roomID              BIGINT        NOT NULL,
                        jid                 TEXT          NOT NULL,
                        nickname            VARCHAR(255)  NULL,
                        firstName           VARCHAR(100)  NULL,
                        lastName            VARCHAR(100)  NULL,
                        url                 VARCHAR(100)  NULL,
                        email               VARCHAR(100)  NULL,
                        faqentry            VARCHAR(100)  NULL,
                        PRIMARY KEY (roomID,jid(70))
                       );

CREATE TABLE IF NOT EXISTS mucConversationLog (
                                 roomID              BIGINT        NOT NULL,
                                 sender              TEXT          NOT NULL,
                                 nickname            VARCHAR(255)  NULL,
                                 time                CHAR(15)      NOT NULL,
                                 subject             VARCHAR(255)  NULL,
                                 body                TEXT          NULL,
                                 INDEX mucLog_time_idx (time)
                                );


# Insert default table values, if they aren't already there
INSERT INTO jiveID (idType, id) VALUES (18, 1) ON DUPLICATE KEY UPDATE id=id;
INSERT INTO jiveID (idType, id) VALUES (19, 1) ON DUPLICATE KEY UPDATE id=id;
INSERT INTO jiveID (idType, id) VALUES (23, 1) ON DUPLICATE KEY UPDATE id=id;

# force the version to be correct
INSERT INTO jiveVersion (name, version) VALUES ('wildfire', 10) ON DUPLICATE KEY UPDATE name='wildfire', version='10';

_SQL_

Migrate::runSql($sql);

Migrate::updateSchemaVersion(40, 41);

exit(0);
