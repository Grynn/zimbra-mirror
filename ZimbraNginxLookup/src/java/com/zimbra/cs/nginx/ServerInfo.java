/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.nginx;

public class ServerInfo extends LookupEntry {
    
    private String mHttpPort;
    private String mHttpSSLPort;
    private String mHttpAdminPort;
    private String mHttpPop3Port;
    private String mHttpPop3SSLPort;
    private String mHttpImapPort;
    private String mHttpImapSSLPort;
    
    ServerInfo(String serverName) {
        super(serverName);
    }
    
    void setHttpPort(String port) {
        mHttpPort = port;
    }
    
    void setHttpSSLPort(String port) {
        mHttpSSLPort = port;
    }
    
    void setHttpAdminPort(String port) {
        mHttpAdminPort = port;
    }
    
    void setPop3Port(String port) {
        mHttpPop3Port = port;
    }
    
    void setPop3SSLPort(String port) {
        mHttpPop3SSLPort = port;
    }
    
    void setImapPort(String port) {
        mHttpImapPort = port;
    }
    
    void setImapSSLPort(String port) {
        mHttpImapSSLPort = port;
    }
    
    String getPortForProto(String proto, boolean isZimbraAdmin) {
        if (NginxLookupExtension.NginxLookupHandler.POP3.equalsIgnoreCase(proto))
            return mHttpPop3Port;
        else if (NginxLookupExtension.NginxLookupHandler.POP3_SSL.equalsIgnoreCase(proto))
            return mHttpPop3SSLPort;
        else if (NginxLookupExtension.NginxLookupHandler.IMAP.equalsIgnoreCase(proto))
            return mHttpImapPort;
        else if (NginxLookupExtension.NginxLookupHandler.IMAP_SSL.equalsIgnoreCase(proto))
            return mHttpImapSSLPort;
        else if (NginxLookupExtension.NginxLookupHandler.HTTP.equalsIgnoreCase(proto)) {
            return mHttpPort;            
        } else if (NginxLookupExtension.NginxLookupHandler.HTTP_SSL.equalsIgnoreCase(proto)) {
            if (isZimbraAdmin) {
                return mHttpAdminPort;
            } else {
                return mHttpSSLPort;
            }
        }
        
        return null;
    }
}

