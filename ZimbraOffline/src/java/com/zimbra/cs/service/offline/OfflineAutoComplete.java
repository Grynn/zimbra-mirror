/*
 * ***** BEGIN LICENSE BLOCK *****
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
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.service.offline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineGal;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.mailbox.ContactAutoComplete.AutoCompleteResult;
import com.zimbra.cs.mailbox.ContactAutoComplete;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.service.mail.AutoComplete;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineAutoComplete extends AutoComplete {

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext ctxt = getZimbraSoapContext(context);
        Account account = getRequestedAccount(getZimbraSoapContext(context));
        if (!(account instanceof OfflineAccount))
            throw OfflineServiceException.MISCONFIGURED("incorrect account class: " + account.getClass().getSimpleName());
        
        if (!account.getBooleanAttr(Provisioning.A_zimbraFeatureGalEnabled , false) ||
            !account.getBooleanAttr(Provisioning.A_zimbraFeatureGalAutoCompleteEnabled , false))
            throw ServiceException.PERM_DENIED("auto complete GAL disabled");
        
        Mailbox mbox = getRequestedMailbox(ctxt);
        if (!(mbox instanceof ZcsMailbox))
            throw OfflineServiceException.MISCONFIGURED("incorrect mailbox class: " + mbox.getClass().getSimpleName());
                        
        String name = request.getAttribute(MailConstants.A_NAME);
        while (name.endsWith("*"))
            name = name.substring(0, name.length() - 1);
        
        int limit = account.getContactAutoCompleteMaxResults();        
        AutoCompleteResult result = query(request, ctxt, account, true, name, limit);
        
        if (result.entries.size() < limit) {
            int galLimit = limit - result.entries.size();
            
            if (account.getBooleanAttr(Provisioning.A_zimbraFeatureGalSyncEnabled , false)) {            
                (new OfflineGal((OfflineAccount)account)).search(result, name, galLimit);                        
            } else { // proxy mode
                XMLElement req = new XMLElement(AccountConstants.AUTO_COMPLETE_GAL_REQUEST);
                req.addAttribute(AccountConstants.A_NAME, name);
                req.addAttribute(AccountConstants.A_LIMIT, galLimit);
                req.addAttribute(AccountConstants.A_TYPE, "account");
                
                Element resp = ((ZcsMailbox)mbox).proxyRequest(req, ctxt.getResponseProtocol(), true, "auto-complete GAL");
                if (resp != null) {
                    List<Element> contacts = resp.listElements(MailConstants.E_CONTACT);
                    for (Element elt : contacts) {
                        Map<String, String> fields = new HashMap<String, String>();
                        for (Element eField : elt.listElements()) {
                            String n = eField.getAttribute(Element.XMLElement.A_ATTR_NAME);
                            if (!n.equals("objectClass"))
                                fields.put(n, eField.getText());
                        }
                        ContactAutoComplete.addMatchedContacts(name, fields, OfflineGal.EMAIL_KEYS, ContactAutoComplete.FOLDER_ID_GAL, null, result);
                    }      
                }
            }
        }

        Element response = ctxt.createElement(MailConstants.AUTO_COMPLETE_RESPONSE);
        toXML(response, result, ctxt.getAuthtokenAccountId());
        return response;        
    }   
}
