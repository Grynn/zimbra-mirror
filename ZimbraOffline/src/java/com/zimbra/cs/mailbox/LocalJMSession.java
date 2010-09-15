/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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

package com.zimbra.cs.mailbox;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.net.SocketFactories;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.util.JMSession;

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

    public static Session getSession(String smtpHost, int smtpPort,
            boolean isAuthRequired, String smtpUser, String smtpPass, boolean useSSL,
            boolean useProxy, String proxyHost, int proxyPort, boolean isDebugTraceEnabled) {
        long timeout = LC.javamail_smtp_timeout.longValue() * Constants.MILLIS_PER_SECOND;

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

        if (useProxy) {
            props.setProperty("proxySet", "true");
            props.setProperty("socksProxyHost", proxyHost);
            props.setProperty("socksProxyPort", proxyPort + "");
        }

        props.put("mail.smtp.socketFactory", SocketFactories.defaultSocketFactory());
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.ssl.socketFactory", SocketFactories.defaultSSLSocketFactory());
        props.setProperty("mail.smtp.ssl.socketFactory.fallback", "false");
        props.put("mail.smtps.ssl.socketFactory", SocketFactories.defaultSSLSocketFactory());
        props.setProperty("mail.smtps.ssl.socketFactory.fallback", "false");

        if (useSSL) {
            props.setProperty("mail.transport.protocol", "smtps");
            props.setProperty("mail.smtps.connectiontimeout", Long.toString(timeout));
            props.setProperty("mail.smtps.timeout", Long.toString(timeout));
            props.setProperty("mail.smtps.localhost", localhost);
            props.setProperty("mail.smtps.sendpartial", "true");
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
            props.setProperty("mail.smtp.connectiontimeout", Long.toString(timeout));
            props.setProperty("mail.smtp.timeout", Long.toString(timeout));
            props.setProperty("mail.smtp.localhost", localhost);
            props.setProperty("mail.smtp.sendpartial", "true");
            props.setProperty("mail.smtp.host", smtpHost);
            props.setProperty("mail.smtp.port",  smtpPort + "");
            if (LC.javamail_smtp_enable_starttls.booleanValue()) {
                props.setProperty("mail.smtp.starttls.enable","true");
                // props.put("mail.smtp.socketFactory.class", TlsSocketFactory.getInstance());
            }
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

        if (LC.javamail_smtp_debug.booleanValue() || isDebugTraceEnabled) {
            session.setDebug(true);
        }
        JMSession.setProviders(session);
        return session;
    }

    public static Session getSession(OfflineDataSource ds) {
        try {
            String smtpHost = ds.getAttr(OfflineProvisioning.A_zimbraDataSourceSmtpHost, null);
            int smtpPort = ds.getIntAttr(OfflineProvisioning.A_zimbraDataSourceSmtpPort, 0);
            boolean isAuthRequired = ds.getBooleanAttr(OfflineProvisioning.A_zimbraDataSourceSmtpAuthRequired, false);
            String smtpUser = ds.getAttr(OfflineProvisioning.A_zimbraDataSourceSmtpAuthUsername, null);

            String smtpPass = ds.getAttr(OfflineProvisioning.A_zimbraDataSourceSmtpAuthPassword, null);
            smtpPass = smtpPass == null ? null : DataSource.decryptData(ds.getId(), smtpPass);

            boolean useSSL = "ssl".equals(ds.getAttr(OfflineProvisioning.A_zimbraDataSourceSmtpConnectionType, null));
            boolean useProxy = ds.getBooleanAttr(OfflineProvisioning.A_zimbraDataSourceUseProxy, false);
            String proxyHost = ds.getAttr(OfflineProvisioning.A_zimbraDataSourceProxyHost, null);
            int proxyPort = ds.getIntAttr(OfflineProvisioning.A_zimbraDataSourceProxyPort, 0);
            return getSession(smtpHost, smtpPort, isAuthRequired, smtpUser, smtpPass, useSSL, useProxy, proxyHost, proxyPort, ds.isDebugTraceEnabled());
        } catch (ServiceException x) {
            OfflineLog.offline.warn(x.getMessage());
            return null;
        }
    }
}
