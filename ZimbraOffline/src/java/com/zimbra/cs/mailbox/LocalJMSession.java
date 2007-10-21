/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
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

package com.zimbra.cs.mailbox;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.CustomSSLSocketFactory;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.OfflineLog;


/**
 * @author jjzhuang
 */
public class LocalJMSession {

	private static class SMTPAuthenticator extends javax.mail.Authenticator {
		
		private String username;
		private String password;
		
		public SMTPAuthenticator(String username, String password) {
			this.username = username;
			this.password = password;
		}
		
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
	}
	
    static {
        // Assume that most malformed base64 errors occur due to incorrect delimiters,
        // as opposed to errors in the data itself.  See bug 11213 for more details.
        System.setProperty("mail.mime.base64.ignoreerrors", "true");
    }
    
    private static Session getSession(String smtpHost, int smtpPort, boolean isAuthRequired, String smtpUser, String smtpPass,
    		                         boolean useSSL, boolean useProxy, String proxyHost, int proxyPort) throws ServiceException {
    	String timeout = "20000";
    	String localhost = LC.zimbra_server_hostname.value();
    	if (smtpHost == null || smtpHost.length() == 0)
    		ServiceException.FAILURE("null smtp host", null);
    	if (smtpPort <= 0)
    		ServiceException.FAILURE("invalid smtp port", null);
    	if (isAuthRequired && (smtpUser == null || smtpUser.length() == 0 || smtpPass == null || smtpPass.length() == 0))
    		ServiceException.FAILURE("missing smtp username or password", null);
    	if (useProxy && (proxyHost == null || proxyHost.length() == 0 || proxyPort <= 0))
    		ServiceException.FAILURE("invalid proxy settings", null);
    	
        Properties props = new Properties();
        Session session = null;
        props.setProperty("mail.mime.address.strict", "false");
    	props.put("mail.debug", "true");
    	
    	if (useProxy) {
    	  	 props.setProperty("proxySet", "true");
             props.setProperty("socksProxyHost", proxyHost);
             props.setProperty("socksProxyPort", proxyPort + "");
    	}
    	
    	if (useSSL) {
    		props.setProperty("mail.transport.protocol", "smtps");
            props.setProperty("mail.smtps.connectiontimeout", timeout);
            props.setProperty("mail.smtps.timeout", timeout);
            props.setProperty("mail.smtps.localhost", localhost);
            props.setProperty("mail.smtps.sendpartial", "true");
    		
    		props.put("mail.smtps.starttls.enable","true");
    		props.put("mail.smtps.socketFactory.class", CustomSSLSocketFactory.class.getName());
    		props.put("mail.smtps.socketFactory.fallback", "false");
    		
            props.setProperty("mail.smtps.host", smtpHost);
            props.setProperty("mail.smtps.port",  smtpPort + "");
            if (isAuthRequired) {
                props.setProperty("mail.smtps.auth", "true");
                props.setProperty("mail.smtps.user", smtpUser);
                props.setProperty("mail.smtps.password", smtpPass);
                session = Session.getInstance(props, new SMTPAuthenticator(smtpUser, smtpPass));
            } else {
            	session = Session.getInstance(props);
            }
            session.setProtocolForAddress("rfc822", "smtps");
    	} else {
    		props.setProperty("mail.transport.protocol", "smtp");
            props.setProperty("mail.smtp.connectiontimeout", timeout);
            props.setProperty("mail.smtp.timeout", timeout);
            props.setProperty("mail.smtp.localhost", localhost);
            props.setProperty("mail.smtp.sendpartial", "true");
    		
            props.setProperty("mail.smtp.host", smtpHost);
            props.setProperty("mail.smtp.port",  smtpPort + "");
            if (isAuthRequired) {
                props.setProperty("mail.smtp.auth", "true");
                props.setProperty("mail.smtp.user", smtpUser);
                props.setProperty("mail.smtp.password", smtpPass);
                session = Session.getInstance(props, new SMTPAuthenticator(smtpUser, smtpPass));
            } else {
            	session = Session.getInstance(props);
            }
            session.setProtocolForAddress("rfc822", "smtp");
    	}
        
        session.setDebug(true);
        
        return session;
    }
    
    public static Session getSession(NamedEntry e) {
    	try {
	    	String smtpHost = e.getAttr(OfflineProvisioning.A_zimbraDataSourceSmtpHost, null);
	    	int smtpPort = e.getIntAttr(OfflineProvisioning.A_zimbraDataSourceSmtpPort, 0);
	    	boolean isAuthRequired = e.getBooleanAttr(OfflineProvisioning.A_zimbraDataSourceSmtpAuthRequired, false);
	    	String smtpUser = e.getAttr(OfflineProvisioning.A_zimbraDataSourceSmtpAuthUsername, null);
	    	String smtpPass = e.getAttr(OfflineProvisioning.A_zimbraDataSourceSmtpAuthPassword, null);
	    	boolean useSSL = "ssl".equals(e.getAttr(OfflineProvisioning.A_zimbraDataSourceSmtpConnectionType, null));
	    	boolean useProxy = e.getBooleanAttr(OfflineProvisioning.A_zimbraDataSourceUseProxy, false);
	    	String proxyHost = e.getAttr(OfflineProvisioning.A_zimbraDataSourceProxyHost, null);
	    	int proxyPort = e.getIntAttr(OfflineProvisioning.A_zimbraDataSourceProxyPort, 0);
	    	return getSession(smtpHost, smtpPort, isAuthRequired, smtpUser, smtpPass, useSSL, useProxy, proxyHost, proxyPort);
    	} catch (ServiceException x) {
    		OfflineLog.offline.warn(x.getMessage());
    		return null;
    	}
    }
    
    public static Session getSession() throws ServiceException {
    	OfflineAccount account = (OfflineAccount)OfflineProvisioning.getOfflineInstance().getLocalAccount();
    	return getSession(account);
    }
}