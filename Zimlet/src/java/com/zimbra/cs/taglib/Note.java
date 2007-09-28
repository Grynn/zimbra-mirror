/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
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
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;

public class Note extends ZimbraTag {
    private static final long serialVersionUID = -3525900802675257570L;

    private String mId;

    @Override
    public void setId(String val) {
        mId = val;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public String getContentStart(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mId == null) {
            throw ZimbraTagException.MISSING_ATTR("id");
        }
        int mid = Integer.parseInt(mId);
//        String id = acct.getId();
        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(id);
        com.zimbra.cs.mailbox.Note note = mbox.getNoteById(octxt, mid);

        return note.getText();
    }
}
