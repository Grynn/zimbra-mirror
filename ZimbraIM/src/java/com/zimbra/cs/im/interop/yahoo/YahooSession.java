/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.im.interop.yahoo;

public interface YahooSession {

    public void setMyStatus(YMSGStatus status, String customStatusMsg);

    public void disconnect();

    public void sendMessage(String dest, String message);

    public Iterable<YahooBuddy> buddies();

    public Iterable<YahooGroup> groups();
    
    public void addBuddy(String id, String group);
    
    public void removeBuddy(String id, String group);
    
    public YahooBuddy getBuddy(String id);
    
    public YahooGroup getGroup(String id);
}