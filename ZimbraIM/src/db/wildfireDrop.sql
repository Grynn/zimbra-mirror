--
-- ***** BEGIN LICENSE BLOCK *****
-- Zimbra Collaboration Suite Server
-- Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
-- 
-- The contents of this file are subject to the Zimbra Public License
-- Version 1.3 ("License"); you may not use this file except in
-- compliance with the License.  You may obtain a copy of the License at
-- http://www.zimbra.com/license.
-- 
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
-- ***** END LICENSE BLOCK *****
--

USE zimbra;

drop TABLE IF EXISTS jiveUser;
drop TABLE IF EXISTS jiveUserProp;
drop TABLE IF EXISTS jiveGroup;
drop TABLE IF EXISTS jiveGroupProp;
drop TABLE IF EXISTS jiveGroupUser;
drop TABLE IF EXISTS jivePrivate;
drop TABLE IF EXISTS jiveOffline;
drop TABLE IF EXISTS jiveRoster;
drop TABLE IF EXISTS jiveRosterGroups;
drop TABLE IF EXISTS jiveVCard;
drop TABLE IF EXISTS jiveID;
drop TABLE IF EXISTS jiveProperty;
drop TABLE IF EXISTS jiveVersion;
drop TABLE IF EXISTS jiveExtComponentConf;
drop TABLE IF EXISTS jiveRemoteServerConf;
drop TABLE IF EXISTS jivePrivacyList;
drop TABLE IF EXISTS jiveSASLAuthorized;
drop TABLE IF EXISTS mucRoom;
drop TABLE IF EXISTS mucRoomProp;
drop TABLE IF EXISTS mucAffiliation;
drop TABLE IF EXISTS mucMember;
drop TABLE IF EXISTS mucConversationLog;
