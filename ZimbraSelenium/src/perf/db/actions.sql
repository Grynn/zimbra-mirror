-- 
-- ***** BEGIN LICENSE BLOCK *****
-- Zimbra Collaboration Suite Server
-- Copyright (C) 2012, 2013 Zimbra Software, LLC.
-- 
-- The contents of this file are subject to the Zimbra Public License
-- Version 1.4 ("License"); you may not use this file except in
-- compliance with the License.  You may obtain a copy of the License at
-- http://www.zimbra.com/license.
-- 
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
-- ***** END LICENSE BLOCK *****
-- 
create table actions ( 
 id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
 name VARCHAR(256) 
 );
insert into actions (name) VALUES ('ZmMailApp');
insert into actions (name) VALUES ('ZmMailItem');
insert into actions (name) VALUES ('ZmCalendarApp');
insert into actions (name) VALUES ('ZmContactsApp');
insert into actions (name) VALUES ('ZmContactsItem');
insert into actions (name) VALUES ('ZmTasksApp');
insert into actions (name) VALUES ('ZmTaskItem');

