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

import com.google.common.base.Objects;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineGal;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.ContactAutoComplete;
import com.zimbra.cs.mailbox.ContactAutoComplete.AutoCompleteResult;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.cs.service.mail.AutoComplete;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.soap.type.GalSearchType;

public class OfflineAutoComplete extends AutoComplete {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        OfflineAccount reqAccount = (OfflineAccount) getRequestedAccount(getZimbraSoapContext(context));
        Mailbox reqMbox = getRequestedMailbox(zsc);
        OperationContext octxt = getOperationContext(zsc, context);

        String name = request.getAttribute(MailConstants.A_NAME);
        String typeStr = request.getAttribute(MailConstants.A_TYPE, "account");
        GalSearchType stype = GalSearchType.fromString(typeStr);
        final int limit = reqAccount.getContactAutoCompleteMaxResults();
        int floatingLimit = 0;
        // if GAL sync feature is diabled (A_zimbraFeatureGalEnabled is false), it will enter "proxy mode"
        boolean isAccountGalActive = (reqAccount instanceof OfflineAccount) && (reqMbox instanceof ZcsMailbox)
                && reqAccount.isFeatureGalEnabled() && reqAccount.isFeatureGalAutoCompleteEnabled();
        // search contact
        AutoCompleteResult result = query(request, zsc, reqAccount, true, name, limit, stype, true, octxt);
        floatingLimit = limit - result.entries.size();
        if (isAccountGalActive) {
            if (reqAccount.isFeatureGalSyncEnabled()) {
                (new OfflineGal((OfflineAccount) reqAccount)).search(result, name, floatingLimit, stype.name());
                floatingLimit = limit - result.entries.size();
            } else { // proxy mode, search contacts and proxy GAL request to server
                if (limit > 0) {
                    ContactAutoComplete ac = new ContactAutoComplete(reqAccount, octxt);
                    ac.setNeedCanExpand(true);
                    XMLElement req = new XMLElement(AccountConstants.AUTO_COMPLETE_GAL_REQUEST);
                    req.addAttribute(AccountConstants.A_NAME, name);
                    req.addAttribute(AccountConstants.A_LIMIT, limit);
                    req.addAttribute(AccountConstants.A_TYPE, typeStr);

                    Element resp = ((ZcsMailbox) reqMbox).proxyRequest(req, zsc.getResponseProtocol(), true,
                            "auto-complete GAL");
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
                            floatingLimit = limit - result.entries.size();
                        }
                    }
                }
            }
        }
        if (floatingLimit > 0) {
            autoCompleteFromOtherAccountsContacts(request, zsc, reqAccount, name, floatingLimit, stype, result, octxt,
                    typeStr);
            floatingLimit = limit - result.entries.size();
        }
        if (floatingLimit > 0) {
            autoCompleteFromOtherAccountsGal(request, zsc, reqAccount, name, floatingLimit, stype, result, octxt,
                    typeStr);
            floatingLimit = limit - result.entries.size();
        }
        if (result.entries.size() > limit) {
            result.canBeCached = false;
        }

        Element response = zsc.createElement(MailConstants.AUTO_COMPLETE_RESPONSE);
        toXML(response, result, zsc.getAuthtokenAccountId());
        return response;
    }

    // search local account, zcs accounts, data source accounts in order
    private void autoCompleteFromOtherAccountsContacts(Element request, ZimbraSoapContext ctxt, OfflineAccount reqAcct,
            String name, final int limit, Provisioning.GalSearchType stype, AutoCompleteResult result,
            OperationContext octxt, String typeStr) throws ServiceException {
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        Account localAccount = prov.getLocalAccount();
        AutoCompleteResult res = query(request, ctxt, localAccount, true, name, limit, stype, true, octxt);
        if (res != null) {
            result.appendEntries(res);
        }
        int floatingLimit = limit - result.entries.size();
        if (floatingLimit > 0) {
            List<Account> zcsAccounts = prov.getAllZcsAccounts();
            for (Account zcsAccount : zcsAccounts) {
                if (Objects.equal(zcsAccount, reqAcct)
                        || !zcsAccount.getBooleanAttr(OfflineProvisioning.A_zimbraPrefShareContactsInAutoComplete,
                                false)) {
                    continue;
                }
                res = query(request, ctxt, zcsAccount, true, name, floatingLimit, stype, true, octxt);
                floatingLimit = limit - result.entries.size();
                if (floatingLimit <= 0) {
                    break;
                }
            }
        }
        if (floatingLimit > 0) {
            List<Account> dsAccounts = prov.getAllDataSourceAccounts();
            for (Account dsAccount : dsAccounts) {
                if (!dsAccount.getBooleanAttr(OfflineProvisioning.A_zimbraPrefShareContactsInAutoComplete, false)) {
                    continue;
                }
                res = query(request, ctxt, dsAccount, true, name, floatingLimit, stype, true, octxt);
                floatingLimit = limit - result.entries.size();
                if (floatingLimit <= 0) {
                    break;
                }
            }
        }
    }

    private void autoCompleteFromOtherAccountsGal(Element request, ZimbraSoapContext ctxt, Account reqAcct,
            String name, final int limit, Provisioning.GalSearchType stype, AutoCompleteResult result,
            OperationContext octxt, String typeStr) throws ServiceException {
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        List<Account> zcsAccounts = prov.getAllZcsAccounts();
        int floatingLimit = limit;
        for (Account zcsAccount : zcsAccounts) {
            if (Objects.equal(zcsAccount, reqAcct) || !zcsAccount.isFeatureGalEnabled()
                    || !zcsAccount.isFeatureGalSyncEnabled()) {
                continue;
            }
            (new OfflineGal((OfflineAccount) zcsAccount)).search(result, name, floatingLimit, stype.name());
            floatingLimit = limit - result.entries.size();
            if (floatingLimit <= 0) {
                break;
            }
        }
    }
}
