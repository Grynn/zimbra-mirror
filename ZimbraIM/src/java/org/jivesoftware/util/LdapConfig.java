/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
package org.jivesoftware.util;

/**
 * Thunk to handle ZimbraIM--ZimbraServer split
 * 
 * Returns config values that are only accessible from ZimbraServer
 */
public class LdapConfig {
    static IMConfigProperty getLdapProp(String name) {
        return sProvider.getLdapProp(name);
    }
    
    public interface ServerConfigProvider {
        // Provisioning.java
        IMConfigProperty getLdapProp(String name);
        
        // Providers Implemented 
        IMConfigProperty getConnectionProvider();
        IMConfigProperty getUserProvider();
        IMConfigProperty getAuthProvider();
        IMConfigProperty getGroupProvider();
        IMConfigProperty getProxyTransferProvider();
        IMConfigProperty getRoutingTableProvider();
        IMConfigProperty getVCardProvider();
    }
    
    private static ServerConfigProvider sProvider;
    
    
    public static ServerConfigProvider getProvider() { return sProvider; }
    
    public static void setProvider(ServerConfigProvider prov) {
        sProvider = prov;
    }
  
}
