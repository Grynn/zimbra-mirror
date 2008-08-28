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
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.common.service.ServiceException;

import javax.mail.internet.MimeMessage;
import java.io.IOException;

public class YMailSender extends MailSender {
    private final YMailClient ymc;
    private IOException error;

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
    protected void sendMessage(MimeMessage mm,
                               boolean ignoreFailedAddresses,
                               RollbackData[] rollback) throws IOException {
        try {
            ymc.sendMessage(mm);
        } catch (IOException e) {
            error = e;
            throw e;
        }
    }

    public boolean sendFailed() {
        return error != null;
    }

    public IOException getError() {
        return error;
    }
}
