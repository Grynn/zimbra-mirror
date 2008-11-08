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
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineGal;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineSearchGal extends DocumentHandler {

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext ctxt = getZimbraSoapContext(context);
        Account account = getRequestedAccount(getZimbraSoapContext(context));
        if (!(account instanceof OfflineAccount))
            throw OfflineServiceException.MISCONFIGURED("incorrect account class: " + account.getClass().getSimpleName());
        
        if (!account.getBooleanAttr(Provisioning.A_zimbraFeatureGalEnabled , false))
            throw ServiceException.PERM_DENIED("GAL disabled");
        
        Mailbox mbox = getRequestedMailbox(ctxt);
        if (!(mbox instanceof OfflineMailbox))
            return getResponseElement(ctxt);
        
        Element response;
        if (account.getBooleanAttr(Provisioning.A_zimbraFeatureGalSyncEnabled , false)) {
            response = ctxt.createElement(AccountConstants.SEARCH_GAL_RESPONSE);
            
            String name = request.getAttribute(AccountConstants.E_NAME);
            while (name.endsWith("*"))
                name = name.substring(0, name.length() - 1);            

            (new OfflineGal((OfflineAccount)account)).searchAccounts(response, name);                  
        } else { // proxy mode
            response = ((OfflineMailbox)mbox).proxyRequest(request, ctxt.getResponseProtocol(), true, "search GAL");
            if (response == null) {
                response = ctxt.createElement(AccountConstants.SEARCH_GAL_RESPONSE);
                response.addAttribute(AccountConstants.A_MORE, false);
            }
        }        
        return response;        
    }   
}