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

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OperationContext;

public class Contact extends ZimbraTag {
    private static final long serialVersionUID = 4310265594871660074L;

    private String mContactId;
    private String mField;

    public void setId(String val) {
        mContactId = val;
    }

    public String getId() {
        return mContactId;
    }

    public void setField(String val) {
        mField = val;
    }

    public String getField() {
        return mField;
    }

    public String getContentStart(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mContactId == null) {
            throw ZimbraTagException.MISSING_ATTR("id");
        }
        if (mField == null) {
            throw ZimbraTagException.MISSING_ATTR("field");
        }
        int cid = Integer.parseInt(mContactId);
        String id = acct.getId();
        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(id);
        com.zimbra.cs.mailbox.Contact con = mbox.getContactById(octxt, cid);
        Map fields = con.getFields();
        String val = (String)fields.get(mField);
        if (val == null) {
        	return "";
        }
        return val;
    }
}
