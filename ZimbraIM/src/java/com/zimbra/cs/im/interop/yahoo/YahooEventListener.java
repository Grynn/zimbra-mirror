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
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
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
    
    public void sessionClosed(YahooSession session);
    
    public void receivedBuddyList(YahooSession session);
    
    public void buddyStatusChanged(YahooSession session, YahooBuddy buddy);
    
    public void receivedMessage(YahooSession session, YahooMessage msg);
    
    public void buddyAdded(YahooSession session, YahooBuddy buddy, YahooGroup group);
    
    public void buddyAddedUs(YahooSession session, String ourId, String theirId, String msg);

    public void buddyRemoved(YahooSession session, YahooBuddy buddy, YahooGroup group);
    
    public void error(YahooSession session, YahooError error, long code, Object[] args);

}
