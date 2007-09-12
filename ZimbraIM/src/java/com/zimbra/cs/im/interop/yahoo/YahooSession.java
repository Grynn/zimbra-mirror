/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
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