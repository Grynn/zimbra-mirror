/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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

import java.util.HashMap;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.mailbox.Mailbox.MailboxData;

public class MockOfflineMailboxManager extends MailboxManager {

    public enum Type { ZCS, DESKTOP};

    private Type mboxType = Type.DESKTOP;

    public MockOfflineMailboxManager(Type type) throws ServiceException {
        super(true);
        mailboxes = new HashMap<String,DesktopMailbox>();
        mboxType = type;
    }

    @Override
    public Mailbox getMailboxByAccountId(String accountId)
        throws ServiceException {

        DesktopMailbox mbox = mailboxes.get(accountId);
        if (mbox != null)
            return mbox;
        Account account = Provisioning.getInstance().getAccount(accountId);
        switch (mboxType) {
            case DESKTOP :  mbox = new MockDesktopMailbox();
                            break;
            case ZCS     :  MailboxData data = new MailboxData();
                            data.accountId = account.getId();
                            mbox = new MockZcsMailbox(account, data);
                            break;
        }
        mailboxes.put(accountId, mbox);
        return mbox;
    }

    private HashMap<String,DesktopMailbox> mailboxes;
}
