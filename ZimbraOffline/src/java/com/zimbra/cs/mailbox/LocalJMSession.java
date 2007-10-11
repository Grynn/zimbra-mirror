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
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;


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
    
    public static Session getSession() throws ServiceException {
    	String timeout = "20000";
    	String localhost = LC.zimbra_server_hostname.value();
    	OfflineAccount account = (OfflineAccount)OfflineProvisioning.getOfflineInstance().getLocalAccount();
		boolean smtpUserSSL = account.isDefaultSmtpSSL();
    	String smtpHost = account.getDefaultSmtpHost("localhost");
    	if (smtpHost == null)
    		ServiceException.FAILURE("null smtp host", null);
    	int smtpPort = account.getDefaultSmtpPort(7025);
    	if (smtpPort == 0)
    		ServiceException.FAILURE("invalid smtp port", null);
    	String smtpAuthUser = account.getDefaultSmtpAuthUsername();
    	String smtpAuthPass = account.getDefaultSmtpAuthPassword();
    	boolean smtpAuthRequired = account.isDefaultSmtpAuthRequired();
    	if (smtpAuthRequired && (smtpAuthUser == null || smtpAuthPass == null))
    		ServiceException.FAILURE("missing smtp username or password", null);
    	
        Properties props = new Properties();
        Session session = null;
        props.setProperty("mail.mime.address.strict", "false");
    	props.put("mail.debug", "true");
    	if (smtpUserSSL) {
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
            if (smtpAuthRequired) {
                props.setProperty("mail.smtps.auth", "true");
                props.setProperty("mail.smtps.user", smtpAuthUser);
                props.setProperty("mail.smtps.password", smtpAuthPass);
                session = Session.getInstance(props, new SMTPAuthenticator(smtpAuthUser, smtpAuthPass));
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
            if (smtpAuthRequired) {
                props.setProperty("mail.smtp.auth", "true");
                props.setProperty("mail.smtp.user", smtpAuthUser);
                props.setProperty("mail.smtp.password", smtpAuthPass);
                session = Session.getInstance(props, new SMTPAuthenticator(smtpAuthUser, smtpAuthPass));
            } else {
            	session = Session.getInstance(props);
            }
            session.setProtocolForAddress("rfc822", "smtp");
    	}
        
        session.setDebug(true);
        
        return session;
    }
}