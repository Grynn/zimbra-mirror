/*
 * ***** BEGIN LICENSE BLOCK ***** Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 ("License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc. Portions created
 * by Zimbra are Copyright (C) 2005 Zimbra, Inc. All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.im.interop.aol;

import java.util.List;

import net.kano.joscar.snaccmd.FullUserInfo;
import net.kano.joscar.snaccmd.InfoData;
import net.kano.joscar.snaccmd.conn.WarningNotification;
import net.kano.joscar.snaccmd.icbm.InstantMessage;
import net.kano.joscar.ssiitem.SsiItemObj;

public interface AolEventListener {
    
    /**
     * An exception was thrown somewhere in the library
     * 
     * @param desc
     * @param t
     * @param fatal
     */
    void exception(String desc, Throwable t, boolean fatal);
    
    /**
     * Received when a buddy goes offline
     * 
     * @param name
     */
    void receivedBuddyOffline(String name);
    
    /**
     * 
     * @param info
     */
    void receivedBuddyStatus(FullUserInfo info);
    
    /**
     * @param sender
     * @param message
     */
    void receivedIM(FullUserInfo sender, InstantMessage message);
    
    /**
     * Server Side Items: buddies, blocked buddy info, etc
     */
    void receivedSSI(List<SsiItemObj>items);
    
    /**
     * @param conn
     * @param screenName
     * @param typingState
     */
    void receivedTypingNotification(AolConnection conn, String screenName, int typingState);
    
    /**
     * For some other user, usually because you requested it
     * 
     * @param user
     * @param info
     */
    void receivedUserInfo(FullUserInfo user, InfoData info);
        
    
    /**
     * Command indicating that the client has been "warned" by another user.
     * 
     * @param warning
     */
    void receivedWarning(WarningNotification warning);
    
    /**
     * Info for your user
     * 
     * @param user
     */
    void receivedYourUserInfo(FullUserInfo user);
}
