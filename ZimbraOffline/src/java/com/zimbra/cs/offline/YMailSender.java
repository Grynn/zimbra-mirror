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
package com.zimbra.cs.offline;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.mailbox.MailSender;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.offline.util.ymail.YMailClient;
import com.zimbra.cs.offline.util.ymail.YMailException;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class YMailSender extends MailSender {
    private final YMailClient ymc;
    private final boolean saveCopy;

    public static YMailSender newInstance(OfflineDataSource ds)
        throws ServiceException {
        try {
            YMailClient ymc = new YMailClient(OfflineYAuth.authenticate(ds));
            ymc.setTrace(ds.isDebugTraceEnabled());
            // Have YMail save a copy of the message to Sent folder if user
            // has chosen this option and we don't save it ourselves.
            boolean saveCopy = !ds.isSaveToSent() && ds.getAccount().isPrefSaveToSent();
            return new YMailSender(ymc, saveCopy);
        } catch (Exception e) {
            throw ServiceException.FAILURE("Unable to create initialize YMail client", e);
        }
    }

    private YMailSender(YMailClient ymc, boolean saveCopy) {
        this.ymc = ymc;
        this.saveCopy = saveCopy;
        setTrackBadHosts(false);
    }

    @Override
    protected Collection<Address> sendMessage(Mailbox mbox,
                               MimeMessage mm,
                               boolean ignoreFailedAddresses,
                               Collection<RollbackData> rollbacks) throws IOException {
        try {
        	Address[] rcpts = mm.getAllRecipients();
            ymc.sendMessage(mm, saveCopy);
            return Arrays.asList(rcpts);
        } catch (MessagingException e) {
            throw new YMailException("Unable get recipient list", e);
        }
    }
}
