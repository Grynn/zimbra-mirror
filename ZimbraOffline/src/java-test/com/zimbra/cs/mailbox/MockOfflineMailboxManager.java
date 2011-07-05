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

public class MockOfflineMailboxManager extends OfflineMailboxManager {

    public MockOfflineMailboxManager() throws ServiceException {
        super();
        mailboxes = new HashMap<String,DesktopMailbox>();
    }

    @Override
    public Mailbox getMailboxByAccountId(String accountId)
        throws ServiceException {

        DesktopMailbox mbox = mailboxes.get(accountId);
        if (mbox != null)
            return mbox;
        mbox = new MockDesktopMailbox();
        mailboxes.put(accountId, mbox);
        return mbox;
    }
    
    private HashMap<String,DesktopMailbox> mailboxes;
}
