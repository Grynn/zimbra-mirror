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
package com.zimbra.cs.mailbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.DataSource.Type;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineGal;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.db.DbDataSource;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;
import com.zimbra.cs.index.ZimbraHit;
import com.zimbra.cs.index.ZimbraQueryResults;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants;

/**
 * Utility class for common gal sync operations
 *
 */
public class GalSyncUtil {

    /**
     * Find contact id from data source database
     * @param id
     * @param dsource
     * @return contact id, or -1 if not found
     * @throws ServiceException
     */
    public static int findContact(String id, DataSource dsource) throws ServiceException {
        DataSourceItem dsItem = DbDataSource.getReverseMapping(dsource, id);
        if (dsItem.itemId > 0)
            return dsItem.itemId;
        return -1;
    }

    /**
     * Fetch distribution list members from ZCS
     * @param dlName
     * @param mbox
     * @return JSON serialization of the members, or null if list has none
     * @throws ServiceException
     */
    public static String fetchDlMembers(String dlName, ZcsMailbox mbox) throws ServiceException {
        try {
            XMLElement req = new XMLElement(AccountConstants.GET_DISTRIBUTION_LIST_MEMBERS_REQUEST);
            req.addElement(AdminConstants.E_DL).setText(dlName);
            Element response = mbox.sendRequest(req, true, true, OfflineLC.zdesktop_gal_sync_request_timeout.intValue(), SoapProtocol.Soap12);
            int total = response.getAttributeInt(AccountConstants.A_TOTAL);
            if (total < 1) {
                return null;
            }
            List<String> members = new ArrayList<String>(); 
            for (Element member : response.listElements(AccountConstants.E_DLM)) {
                members.add(member.getText());
            }
            return Contact.encodeMultiValueAttr(members.toArray(new String[members.size()]));
        } catch (JSONException e) {
            throw ServiceException.FAILURE("Unable to encode dlist members", e);
        } catch (ServiceException e) {
            if (e.getCode().equals(ServiceException.PERM_DENIED)) {
                OfflineLog.offline.debug("Permission denied fetching dlist members for %s",dlName);
                return null;
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Create a DataSource instance for gal account
     * @param galAccount
     * @return DataSource for the account
     * @throws ServiceException
     */
    public static DataSource createDataSourceForAccount(Account galAccount) throws ServiceException {
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        String dsId = galAccount.getAttr(OfflineConstants.A_offlineGalAccountDataSourceId, false);
        if (dsId == null) {
            dsId = UUID.randomUUID().toString();
            prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountDataSourceId, dsId);
        }
        return new DataSource(galAccount, Type.gal, galAccount.getName(), dsId,
            new HashMap<String, Object>(), prov);
    }
    
    /**
     * Retrieve a contact specified by email address from OfflineGal
     * @param requestedAcct
     * @param addr
     * @return Contact for the address or null if it does not exist
     * @throws ServiceException
     */
    public static Contact getGalContact(Account requestedAcct, String addr) throws ServiceException {
        Contact con = null;
        ZimbraQueryResults dlResult = (new OfflineGal((OfflineAccount)requestedAcct)).search(addr, "all", "", 0, 0, null);
        if (dlResult != null) {
            try {
                if (dlResult.hasNext()) {
                    ZimbraHit hit = dlResult.getNext();
                    con = (Contact) hit.getMailItem();
                }
            } finally {
                dlResult.doneWithSearchResults();
            }
        }
        return con;
    }
}
