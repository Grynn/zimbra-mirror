/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.account.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Identity;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;

class OfflineIdentity extends Identity {
    private final String mAccountZID;

    OfflineIdentity(Account acct, String name, Map<String,Object> attrs) {
        super(name, (String) attrs.get(Provisioning.A_zimbraPrefIdentityId), attrs);
        mAccountZID = acct.getId();
    }

    Account getAccount() throws ServiceException {
        return Provisioning.getInstance().get(AccountBy.id, mAccountZID);
    }

    void setName(String name) {
        mName = name;
    }
}
