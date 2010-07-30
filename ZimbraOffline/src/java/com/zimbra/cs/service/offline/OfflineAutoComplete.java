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
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.mailbox.ContactAutoComplete.AutoCompleteResult;
import com.zimbra.cs.mailbox.ContactAutoComplete;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.cs.service.mail.AutoComplete;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineAutoComplete extends AutoComplete {

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext ctxt = getZimbraSoapContext(context);
        Account account = getRequestedAccount(getZimbraSoapContext(context));        
        Mailbox mbox = getRequestedMailbox(ctxt);
            
        boolean galAC = (account instanceof OfflineAccount) && (mbox instanceof ZcsMailbox) &&
            account.getBooleanAttr(Provisioning.A_zimbraFeatureGalEnabled , false) &&
            account.getBooleanAttr(Provisioning.A_zimbraFeatureGalAutoCompleteEnabled , false);
        
        String name = request.getAttribute(MailConstants.A_NAME);
        while (name.endsWith("*"))
            name = name.substring(0, name.length() - 1);
        
        String typeStr = request.getAttribute(MailConstants.A_TYPE, "account");
        Provisioning.GAL_SEARCH_TYPE stype = getSearchType(typeStr);
        int limit = account.getContactAutoCompleteMaxResults();        
        AutoCompleteResult result = query(request, ctxt, account, true, name, limit, stype);
        ContactAutoComplete ac = new ContactAutoComplete(account.getId());
        if (galAC && result.entries.size() < limit) {
            int galLimit = limit - result.entries.size();
            
            if (account.getBooleanAttr(Provisioning.A_zimbraFeatureGalSyncEnabled , false)) {            
                (new OfflineGal((OfflineAccount)account)).search(result, name, galLimit, typeStr);                        
            } else { // proxy mode
                XMLElement req = new XMLElement(AccountConstants.AUTO_COMPLETE_GAL_REQUEST);
                req.addAttribute(AccountConstants.A_NAME, name);
                req.addAttribute(AccountConstants.A_LIMIT, galLimit);
                req.addAttribute(AccountConstants.A_TYPE, typeStr);
                
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
                        ac.addMatchedContacts(name, fields, ContactAutoComplete.FOLDER_ID_GAL, null, result);
                    }      
                }
            }
        }

        if (result.entries.size() < limit)
            autoCompleteFromOtherAccounts(request, ctxt, account, name, limit, stype, result);
            
        Element response = ctxt.createElement(MailConstants.AUTO_COMPLETE_RESPONSE);
        toXML(response, result, ctxt.getAuthtokenAccountId());
        return response;        
    }
    
    public void autoCompleteFromOtherAccounts(Element request, ZimbraSoapContext ctxt, Account reqAcct,
        String name, int limit, Provisioning.GAL_SEARCH_TYPE stype, AutoCompleteResult result) throws ServiceException {
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        List<Account> accounts = prov.getAllAccounts();
        accounts.add(0, prov.getLocalAccount());
        String reqAcctId = reqAcct.getId();
        
        int lmt = limit - result.entries.size();
        for(Account account : accounts) {
            String acctId = account.getId();
            if (acctId.equals(reqAcctId) || (!acctId.equals(OfflineProvisioning.LOCAL_ACCOUNT_ID) &&
                !account.getBooleanAttr(OfflineProvisioning.A_zimbraPrefShareContactsInAutoComplete , false))) {
                continue;
            }
            
            AutoCompleteResult res = query(request, ctxt, account, true, name, lmt, stype);
            if (res != null)
                result.appendEntries(res);
            
            lmt = limit - result.entries.size();
            if (lmt <= 0)
                break;
        }
    }
}
