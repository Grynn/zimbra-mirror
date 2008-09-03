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
import java.io.IOException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.index.ZimbraQueryResults;
import com.zimbra.cs.index.MailboxIndex.SortBy;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.LocalMailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.cs.index.queryparser.ParseException;

public class OfflineAutoCompleteGal extends DocumentHandler {

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext ctxt = getZimbraSoapContext(context);
        Account account = getRequestedAccount(getZimbraSoapContext(context));
        if (!(account instanceof OfflineAccount))
            throw OfflineServiceException.MISCONFIGURED("incorrect account class: " + account.getClass().getSimpleName());
        
        if (!account.getBooleanAttr(Provisioning.A_zimbraFeatureGalEnabled , false) ||
            !account.getBooleanAttr(Provisioning.A_zimbraFeatureGalAutoCompleteEnabled , false))
            throw ServiceException.PERM_DENIED("auto complete GAL disabled");
        
        Mailbox mbox = getRequestedMailbox(ctxt);
        if (!(mbox instanceof OfflineMailbox))
            throw OfflineServiceException.MISCONFIGURED("incorrect mailbox class: " + mbox.getClass().getSimpleName());
        
        Element response;
        if (account.getBooleanAttr(Provisioning.A_zimbraFeatureGalSyncEnabled , false)) {
            response = ctxt.createElement(AccountConstants.AUTO_COMPLETE_GAL_RESPONSE);
            
            String name = request.getAttribute(AccountConstants.E_NAME);
            while (name.endsWith("*"))
                name = name.substring(0, name.length() - 1);            
            int limit = (int) request.getAttributeLong(AccountConstants.A_LIMIT);
            
            searchAndAddContacts(response, (OfflineAccount)account, name, limit);                                   
        } else { // proxy mode
            response = ((OfflineMailbox)mbox).sendRequest(request);
        }        
        return response;        
    }
    
    private void searchAndAddContacts(Element response, OfflineAccount account, String name, int limit)
        throws ServiceException {
        String galAcctId = account.getAttr(OfflineConstants.A_offlineGalAccountId, false);
        LocalMailbox galMbox = null;
        
        if (galAcctId != null && galAcctId.length() > 0)
            galMbox = (LocalMailbox)MailboxManager.getInstance().getMailboxByAccountId(galAcctId, false);
        if (galMbox == null)
            throw OfflineServiceException.MISSING_GAL_MAILBOX(account.getName());
       
        byte[] types = new byte[1];
        types[0] = MailItem.TYPE_CONTACT;
        Mailbox.OperationContext context = new Mailbox.OperationContext(galMbox);
        ZimbraQueryResults zqr;
        try {
            String query = "#" + Contact.A_firstName + ":\"" + name + "*\" OR #" + Contact.A_lastName + ":\"" + name +
                "*\" OR #" + Contact.A_fullName + ":\"" + name + "*\" OR #" + Contact.A_email + ":\"" + name + "*\"";
            
            // set max to be limit + 1 so that we know when to set AccountConstants.A_MORE
            zqr = galMbox.search(context, query, types, SortBy.SCORE_DESCENDING, limit + 1); 
        } catch (ParseException e) {
            OfflineLog.offline.debug("gal mailbox parse error (" + account.getName() + "): " + e.getMessage());
            return;
        } catch (IOException e) {
            OfflineLog.offline.debug("gal mailbox IO error (" + account.getName() + "): " + e.getMessage());
            return;
        }
        
        int c = 0;
        while (c++ < limit && zqr.hasNext()) {
            int id = zqr.getNext().getItemId();
            
            Contact contact = (Contact) galMbox.getItemById(context, id, MailItem.TYPE_CONTACT);
            Element cn = response.addElement(MailConstants.E_CONTACT);
            cn.addAttribute(MailConstants.A_ID, id);
            
            String val;
            if ((val = contact.get(Contact.A_firstName)) != null)
                cn.addKeyValuePair(Contact.A_firstName, val, MailConstants.E_ATTRIBUTE, MailConstants.A_ATTRIBUTE_NAME);
            if ((val = contact.get(Contact.A_lastName)) != null)
                cn.addKeyValuePair(Contact.A_lastName, val, MailConstants.E_ATTRIBUTE, MailConstants.A_ATTRIBUTE_NAME);
            if ((val = contact.get(Contact.A_fullName)) != null)
                cn.addKeyValuePair(Contact.A_fullName, val, MailConstants.E_ATTRIBUTE, MailConstants.A_ATTRIBUTE_NAME);
            if ((val = contact.get(Contact.A_email)) != null)
                cn.addKeyValuePair(Contact.A_email, val, MailConstants.E_ATTRIBUTE, MailConstants.A_ATTRIBUTE_NAME);
        }
        
        response.addAttribute(AccountConstants.A_MORE, zqr.hasNext());
    }    
}
