/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.nginx;

public class DomainExternalRouteInfo extends LookupEntry {
    
    private String mPop3Port;
    private String mPop3SSLPort;
    private String mImapPort;
    private String mImapSSLPort;
    private String mPop3Hostname;
    private String mPop3SSLHostname;
    private String mImapHostname;
    private String mImapSSLHostname;
    
    DomainExternalRouteInfo(String domainName,
                            String pop3Port,
                            String pop3SSLPort,
                            String imapPort,
                            String imapSSLPort,
                            String pop3Hostname,
                            String pop3SSLHostname,
                            String imapHostname,
                            String imapSSLHostname) {
        super(domainName);
        
        mPop3Port        = pop3Port;
        mPop3SSLPort     = pop3SSLPort;
        mImapPort        = imapPort;
        mImapSSLPort     = imapSSLPort;
        mPop3Hostname    = pop3Hostname;
        mPop3SSLHostname = pop3SSLHostname;
        mImapHostname    = imapHostname;
        mImapSSLHostname = imapSSLHostname;
    }

    String getHostname(String proto) {
        if (NginxLookupExtension.NginxLookupHandler.POP3.equalsIgnoreCase(proto))
            return mPop3Hostname;
        else if (NginxLookupExtension.NginxLookupHandler.POP3_SSL.equalsIgnoreCase(proto))
            return mPop3SSLHostname;
        else if (NginxLookupExtension.NginxLookupHandler.IMAP.equalsIgnoreCase(proto))
            return mImapHostname;
        else if (NginxLookupExtension.NginxLookupHandler.IMAP_SSL.equalsIgnoreCase(proto))
            return mImapSSLHostname;
        else
            return null;
    }
    
    String getPort(String proto) {
        if (NginxLookupExtension.NginxLookupHandler.POP3.equalsIgnoreCase(proto))
            return mPop3Port;
        else if (NginxLookupExtension.NginxLookupHandler.POP3_SSL.equalsIgnoreCase(proto))
            return mPop3SSLPort;
        else if (NginxLookupExtension.NginxLookupHandler.IMAP.equalsIgnoreCase(proto))
            return mImapPort;
        else if (NginxLookupExtension.NginxLookupHandler.IMAP_SSL.equalsIgnoreCase(proto))
            return mImapSSLPort;
        else
            return null;
    }

}

