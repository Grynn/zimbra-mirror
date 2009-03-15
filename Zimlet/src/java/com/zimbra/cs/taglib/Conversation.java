/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib;

import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;

public class Conversation extends Message {
    private static final long serialVersionUID = -2306183433671648674L;

    String mIndex;

    public void setIndex(String val) {
        mIndex = val;
    }

    public String getIndex() {
        return mIndex;
    }

    public String getContentStart(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mId == null) {
            throw ZimbraTagException.MISSING_ATTR("id");
        }
        if (mField == null) {
            throw ZimbraTagException.MISSING_ATTR("field");
        }
        if (mIndex == null) {
            throw ZimbraTagException.MISSING_ATTR("index");
        }
        int cid = Integer.parseInt(mId);
        int index = Integer.parseInt(mIndex);
        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(acct.getId());
        List<com.zimbra.cs.mailbox.Message> msgs = mbox.getMessagesByConversation(octxt, cid);
        return getMessageContent(msgs.get(index));
    }
}
