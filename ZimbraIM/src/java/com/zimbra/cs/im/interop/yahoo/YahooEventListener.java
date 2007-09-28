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

/**
 * 
 */
public interface YahooEventListener {
    
    public void connectFailed(YahooSession session);
    
    public void authFailed(YahooSession session);
    
    public void loggedOn(YahooSession session);
    
    /**
     * Received the "You have logged in from another location" message
     * from the remote service -- session will be disconnected soon
     */
    public void connectedFromOtherLocation(YahooSession session);
    
    public void sessionClosed(YahooSession session);
    
    public void receivedBuddyList(YahooSession session);
    
    public void buddyStatusChanged(YahooSession session, YahooBuddy buddy);
    
    public void receivedTypingStatus(YahooSession session, String fromId, boolean isTyping, YahooBuddy buddyOrNull);
    
    public void receivedMessage(YahooSession session, YahooMessage msg);
    
    public void buddyAdded(YahooSession session, YahooBuddy buddy, YahooGroup group);
    
    public void buddyAddedUs(YahooSession session, String ourId, String theirId, String msg);

    public void buddyRemoved(YahooSession session, YahooBuddy buddy, YahooGroup group);
    
    public void error(YahooSession session, YahooError error, long code, Object[] args);

}
