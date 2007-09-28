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

import java.io.IOException;
import java.util.Map;

import org.xmpp.packet.JID;

public interface InteropRegistrationProvider {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    
    public Map<String, String> getIMGatewayRegistration(JID userJID, Interop.ServiceName service) throws IOException;
    
    public void putIMGatewayRegistration(JID userJID, Interop.ServiceName service, Map<String, String> data) throws IOException;
    
    public void removeIMGatewayRegistration(JID userJID, Interop.ServiceName service) throws IOException;
}
