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
package com.zimbra.cs.offline;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import com.zimbra.cs.mailbox.MailSender;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;

public class LMailSender extends MailSender {
    private Transport transport;
    
    public static LMailSender newInstance(OfflineDataSource ds) throws
        ServiceException {
        if (ds.isSaveToSent() || !ds.isLive())
            throw new IllegalArgumentException("Must be Live data source");
        return new LMailSender(ds);
    }
    
    private LMailSender(OfflineDataSource ds) throws ServiceException {
        Properties props = new Properties();
        Long timeout = LC.javamail_smtp_timeout.longValue() * Constants.MILLIS_PER_SECOND;

        props.setProperty("mail.davmail.from", ds.getEmailAddress());
        props.setProperty("mail.davmail.saveinsent", "f");
        if (timeout > 0) {
            props.setProperty("mail.davmail.timeout", timeout.toString());
            props.setProperty("mail.davmail.connectiontimeout", timeout.toString());
        }
        Session ses = Session.getInstance(props);
        try {
            transport = ses.getTransport("davmail_xmit");
            transport.connect(null, ds.getUsername(), ds.getDecryptedPassword());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Live data source");
        }
    }

    @Override
    protected Collection<Address> sendMessage(MimeMessage mm, boolean ignoreFailedAddresses,
        RollbackData[] rollback) throws IOException, SafeMessagingException {
        try {
        	Address[] rcpts = mm.getAllRecipients();
            transport.sendMessage(mm, rcpts);
            return Arrays.asList(rcpts);
        } catch (MessagingException e) {
            for (RollbackData rdata : rollback)
                if (rdata != null)
                    rdata.rollback();
            throw new SafeMessagingException(e);
        } catch (Exception e) {
            for (RollbackData rdata : rollback)
                if (rdata != null)
                    rdata.rollback();
            throw new IOException(e.toString());
        }
    }
}
