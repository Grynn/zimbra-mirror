/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.cs.service.mail.GetFreeBusy;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.cs.account.Account;
import com.zimbra.common.soap.MailConstants;

public class OfflineGetFreeBusy extends GetFreeBusy {
    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext ctxt = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(ctxt);
        if (mbox instanceof ZcsMailbox) {
            Account acct = ((ZcsMailbox)mbox).getAccount();
            String uid = request.getAttribute(MailConstants.A_UID);
            if (uid.equals(acct.getName())) // no need to do proxy for organizer
                return super.handle(request, context);          
        }
        
        return OfflineServiceProxy.GetFreeBusy().handle(request, context);
    }
}
