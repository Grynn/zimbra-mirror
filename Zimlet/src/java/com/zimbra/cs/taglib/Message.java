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

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;
import com.zimbra.cs.mime.Mime;

public class Message extends ZimbraTag {
    private static final long serialVersionUID = -6527940306669140648L;

    String mId;
    String mField;

    public void setId(String val) {
        mId = val;
    }

    public String getId() {
        return mId;
    }

    public void setField(String val) {
        mField = val;
    }

    public String getField() {
        return mField;
    }

    String getAddressHeader(com.zimbra.cs.mailbox.Message msg, String hdr) throws ServiceException {
        MimeMessage mm = msg.getMimeMessage();
        InternetAddress[] addrs = Mime.parseAddressHeader(mm, hdr);
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < addrs.length; i++) {
        	if (buf.length() > 0) buf.append(", ");
        	String str = addrs[i].getPersonal();
        	if (str != null) {
        		buf.append("\"");
        		buf.append(str);
        		buf.append("\"");
        	}
        	str = addrs[i].getAddress();
        	if (str != null) {
        		buf.append(" &lt;");
        		buf.append(str);
        		buf.append("&gt;");
        	}
        }
        return buf.toString();
    }
    
    String getMessageContent(com.zimbra.cs.mailbox.Message msg) throws ServiceException {
        if (mField.equals("subject")) {
        	return msg.getSubject();
        } else if (mField.equals("from") ||
        		mField.equals("to") ||
        		mField.equals("cc") ||
        		mField.equals("bcc")) {
        	return getAddressHeader(msg, mField);
        } else if (mField.equals("raw")) {
        	return new String(msg.getContent());
        }
    	
        return "unknown";
    }
    
    public String getContentStart(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mId == null) {
            throw ZimbraTagException.MISSING_ATTR("id");
        }
        if (mField == null) {
            throw ZimbraTagException.MISSING_ATTR("field");
        }
        int mid = Integer.parseInt(mId);
        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(acct.getId());
        return getMessageContent(mbox.getMessageById(octxt, mid));
    }
}
