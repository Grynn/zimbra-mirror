/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008 Zimbra, Inc.
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
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.service.mail.ConvAction;
import com.zimbra.cs.service.mail.ItemAction;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineConvAction extends ConvAction {
    @Override
    public void postProxy(Element request, Element response, Map<String, Object> context) throws ServiceException {
        Element act = request.getOptionalElement(MailConstants.E_ACTION);
        String op;
        if (act == null || (op = act.getAttribute(MailConstants.A_OPERATION, null)) == null || !op.equals(ItemAction.OP_MOVE))
            return;
        
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();        
        String id = act.getAttribute(MailConstants.A_FOLDER, null);
        int pos;
        if (id == null || (pos = id.indexOf(":")) <= 0)
            return;
        String toAcctId = id.substring(0, pos);
        Account toAcct = prov.get(Provisioning.AccountBy.id, toAcctId);
        if (toAcct == null || !prov.isSyncAccount(toAcct))
            return;
        
        act = response.getOptionalElement(MailConstants.E_ACTION);
        if (act == null || (id = act.getAttribute(MailConstants.A_ID, null)) == null || (pos = id.indexOf(":")) <= 0)
            return;
        String fromAcctId = id.substring(0, pos);
        Account fromAcct = prov.get(Provisioning.AccountBy.id, fromAcctId);
        if (fromAcct == null || !prov.isMountpointAccount(fromAcct))
            return;            
     
        ZimbraSoapContext ctxt = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(ctxt);
        if (!(mbox instanceof OfflineMailbox))
            throw OfflineServiceException.MISCONFIGURED("incorrect mailbox class: " + mbox.getClass().getSimpleName());
        ((OfflineMailbox)mbox).sync(true, ((OfflineAccount)toAcct).isDebugTraceEnabled());
    }
}
