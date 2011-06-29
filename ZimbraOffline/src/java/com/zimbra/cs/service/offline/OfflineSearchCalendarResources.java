/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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

import java.util.Iterator;
import java.util.Map;

import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.service.account.SearchCalendarResources;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineSearchCalendarResources extends SearchCalendarResources {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        Element response = OfflineServiceProxy.SearchCalendarResources().handle(request, context);
        ZimbraSoapContext ctxt = getZimbraSoapContext(context);
        Account acct = getRequestedAccount(ctxt);
        if (acct instanceof OfflineAccount) {
            OfflineAccount oAcct = (OfflineAccount) acct;
            if (!oAcct.getRemoteServerVersion().isAtLeast7xx())
            {
                //if email is requested, make sure response includes it
                boolean needEmail = false;
                String attrs = request.getAttribute(AccountConstants.E_ATTRS, null);
                if (attrs != null) {
                    String[] attrArr = attrs.split(",");
                    for (String attr : attrArr) {
                        if (attr.equals(ContactConstants.A_email)) {
                            needEmail = true;
                            break;
                        }
                    }
                }
                if (needEmail) {
                    Iterator<Element> calResources = response.elementIterator(AccountConstants.E_CALENDAR_RESOURCE); 
                        while (calResources.hasNext()) {
                            Element calResource = calResources.next();
                            //if email not in response from ZCS even when requested; use name instead 
                            if (calResource.getAttribute(ContactConstants.A_email, null) == null && calResource.getAttribute(AccountConstants.A_NAME, null) != null) {
                                calResource.addAttribute(ContactConstants.A_email, calResource.getAttribute(AccountConstants.A_NAME));
                            }
                        }
                    }
            }
        }
        return response;
    }
}
