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
package com.zimbra.cs.im.interop;

import org.xmpp.packet.JID;

import com.zimbra.common.service.ServiceException;

public interface SessionFactory {
    /**
     * Encode the password (possibly by communicating with the IM service and grabbing a "token") 
     * before it is stored in the Zimbra DB.  The value returned by this function will be stored
     * in the User's DB and it will be passed to createSession().
     * 
     * @param service
     * @param jid
     * @param username
     * @param password
     * @return
     */
    String encodePassword(Service service, JID jid, String username, String password)  throws ServiceException;
    
    InteropSession createSession(Service service, JID jid, String username, String password);
    boolean isEnabled();
    
    String getName();
    String getDescription();
}