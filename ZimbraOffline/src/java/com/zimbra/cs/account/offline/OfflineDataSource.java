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
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;

class OfflineDataSource extends DataSource {
    private final String mAccountZID;

    OfflineDataSource(Account acct, DataSource.Type type, String name, String id, Map<String,Object> attrs) {
        super(type, name, id, attrs);
        mAccountZID = acct.getId();
    }

    Account getAccount() throws ServiceException {
        return Provisioning.getInstance().get(AccountBy.id, mAccountZID);
    }

    void setName(String name) {
        mName = name;
    }
}
