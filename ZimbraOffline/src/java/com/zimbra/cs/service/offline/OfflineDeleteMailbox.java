/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/*
 * Created on Jul 30, 2010
 */
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.im.IMPersona;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineMailboxManager;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.service.admin.DeleteMailbox;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineDeleteMailbox extends DeleteMailbox {

    @Override
    public Element handle(Element request, Map<String, Object> context)
            throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);

        Element mreq = request.getElement(AdminConstants.E_MAILBOX);
        String accountId = mreq.getAttribute(AdminConstants.A_ACCOUNTID);
        
        Account account = Provisioning.getInstance().get(AccountBy.id, accountId, zsc.getAuthToken());
        if (account == null) {
            // Note: isDomainAdminOnly *always* returns false for pure ACL based AccessManager 
            if (isDomainAdminOnly(zsc)) {
                throw ServiceException.PERM_DENIED("account doesn't exist, unable to determine authorization");
            }
            
            // still need to check right, since we don't have an account, the 
            // last resort is checking the global grant.  Do this for now until 
            // there is complain.
            checkRight(zsc, context, null, Admin.R_deleteAccount); 
            
            ZimbraLog.account.warn("DeleteMailbox: account doesn't exist: "+accountId+" (still deleting mailbox)");

        } else {
            checkAccountRight(zsc, account, Admin.R_deleteAccount);   
        }

        if (account != null)
            IMPersona.deleteIMPersona(account.getName());
        //test if the mbox is fetchable; if it is not (e.g. due to corrupt db files) then we want to force-remove it from mgr so next req makes new db files. 
        try {
            MailboxManager.getInstance().getMailboxByAccountId(accountId, false);
            return super.handle(request, context);
        }
        catch (Exception e) {
            ZimbraLog.account.warn("DeleteMailbox: failed to retrieve mailbox due to exception ",e);
            OfflineMailboxManager omgr = (OfflineMailboxManager) MailboxManager.getInstance();
            omgr.purgeBadMailboxByAccountId(accountId);
            if (account instanceof OfflineAccount) {
                ((OfflineAccount) account).setDisabledDueToError(false);
                OfflineSyncManager.getInstance().clearErrorCode(account);
            }
            Map<String,Object> attrs = account.getAttrs();
            attrs.remove(OfflineConstants.A_offlineSyncStatusErrorCode);
            attrs.remove(OfflineConstants.A_offlineSyncStatusErrorMsg);
            attrs.remove(OfflineConstants.A_offlineSyncStatusException);
            account.setAttrs(attrs);
        }
        
        String idString = "<no mailbox for account " + accountId + ">";
        ZimbraLog.security.info(ZimbraLog.encodeAttrs(
            new String[] {"cmd", "DeleteMailbox","id", idString}));
        
        Element response = zsc.createElement(AdminConstants.DELETE_MAILBOX_RESPONSE);
        return response;
    }

}
