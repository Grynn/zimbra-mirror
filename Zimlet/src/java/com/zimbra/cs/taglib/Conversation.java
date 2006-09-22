/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;
import com.zimbra.cs.service.ServiceException;

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
        com.zimbra.cs.mailbox.Message[] msgs = mbox.getMessagesByConversation(octxt, cid);
        return getMessageContent(msgs[index]);
    }
}
