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

import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.common.account.Key.AccountBy;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.index.SearchParams;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Mountpoint;
import com.zimbra.cs.service.mail.Search;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineSearch extends Search {

    
    @Override
    protected List<String> getFolderIdListIfSimpleAppointmentsQuery(
                    SearchParams params, ZimbraSoapContext zsc) throws ServiceException {
        List<String> folderIds = super.getFolderIdListIfSimpleAppointmentsQuery(params, zsc);
        if (folderIds != null) {
            Account authAcct = getAuthenticatedAccount(zsc);
            for (String folderId : folderIds) {
                ItemId folderIid = new ItemId(folderId,authAcct.getId());
                if (!folderIid.belongsTo(authAcct)) {
                    Account targetAcct = Provisioning.getInstance().get(AccountBy.id, folderIid.getAccountId());
                    //if one or more accounts/folders are mountpoints in other accounts it's not a simple query. let Mailbox.search() handle the proxying required.
                    if (targetAcct == null || OfflineProvisioning.getOfflineInstance().isMountpointAccount(targetAcct)) {
                        return null;
                    }
                    Mailbox mbox = MailboxManager.getInstance().getMailboxByAccount(targetAcct);
                    if (mbox == null || mbox.getFolderById(null, folderIid.getId()) instanceof Mountpoint) {
                        return null;
                    }
                }
            }
        }
        return folderIds;
    }

}
