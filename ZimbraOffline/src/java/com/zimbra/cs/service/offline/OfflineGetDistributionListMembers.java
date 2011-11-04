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

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.gal.GalGroupMembers.ContactDLMembers;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.GalSyncUtil;
import com.zimbra.cs.service.account.GetDistributionListMembers;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineGetDistributionListMembers extends GetDistributionListMembers {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account account = getRequestedAccount(getZimbraSoapContext(context));

        int limit = (int) request.getAttributeLong(AdminConstants.A_LIMIT, 0);
        if (limit < 0) {
            throw ServiceException.INVALID_REQUEST("limit" + limit + " is negative", null);
        }

        int offset = (int) request.getAttributeLong(AdminConstants.A_OFFSET, 0);
        if (offset < 0) {
            throw ServiceException.INVALID_REQUEST("offset" + offset + " is negative", null);
        }

        Element d = request.getElement(AdminConstants.E_DL);
        String dlName = d.getText();
        Contact con = GalSyncUtil.getGalDlistContact((OfflineAccount) account, dlName);
        ContactDLMembers dlMembers = new ContactDLMembers(con);
        return processDLMembers(zsc, dlName, account, limit, offset, dlMembers);
    }
}
