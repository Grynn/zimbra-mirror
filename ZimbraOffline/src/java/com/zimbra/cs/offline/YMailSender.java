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

import com.zimbra.cs.mailbox.MailSender;
import com.zimbra.cs.offline.util.ymail.YMailClient;
import com.zimbra.cs.offline.util.ymail.YMailException;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.common.service.ServiceException;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class YMailSender extends MailSender {
    private final YMailClient ymc;

    public static YMailSender newInstance(OfflineDataSource ds)
        throws ServiceException {
        if (ds.isSaveToSent() || !ds.isYahoo()) {
            throw new IllegalArgumentException("Must be yahoo data source");
        }
        YMailClient ymc = new YMailClient(OfflineYAuth.authenticate(ds));
        if (ds.isDebugTraceEnabled()) {
            ymc.enableTrace(System.out);
        }
        return new YMailSender(ymc);
    }
    
    private YMailSender(YMailClient ymc) throws ServiceException {
        this.ymc = ymc;
    }

    @Override
    protected Collection<Address> sendMessage(MimeMessage mm,
                               boolean ignoreFailedAddresses,
                               RollbackData[] rollback) throws IOException {
        try {
        	Address[] rcpts = mm.getAllRecipients();
            ymc.sendMessage(mm);
            return Arrays.asList(rcpts);
        } catch (MessagingException e) {
            throw new YMailException("Unable get recipient list", e);
        }
    }
}
