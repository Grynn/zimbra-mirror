/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
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
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.offline.backup.AccountBackupProducer;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineAccountRestoreService extends DocumentHandler {
    
    @Override
    public Element handle(Element request, Map<String, Object> context)
            throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        String accountId = request.getAttribute(AccountConstants.E_ID, null);
        String timestamp = request.getAttribute(AdminConstants.A_TIME, null);
        String resolve = request.getAttribute(OfflineConstants.A_RESOLVE, null);
        Element response = zsc.createElement(OfflineConstants.ACCOUNT_RESTORE_RESPONSE);
        response.addAttribute(AccountConstants.A_ID, accountId);
        response.addAttribute(AccountConstants.A_STATUS, AccountBackupProducer.getInstance().restoreAccount(accountId, Long.parseLong(timestamp), resolve));
        return response;
    }
}
