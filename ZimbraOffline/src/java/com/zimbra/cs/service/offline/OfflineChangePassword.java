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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineChangePassword extends DocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context)
                    throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        String accountId = request.getAttribute(AccountConstants.E_ID);
        Element password = request.getElement(AccountConstants.E_PASSWORD);
        String newPass = password.getText();
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        
        Account acct = prov.getAccount(accountId);
        if (acct == null) {
            throw ServiceException.INVALID_REQUEST("no account found with ID "+accountId, null);
        }
        
        Map<String, Object> attrs = new HashMap<String, Object>();
        String status = "fail";
        if (prov.isZcsAccount(acct)) {
            attrs.put(OfflineConstants.A_offlineAccountSetup, Provisioning.TRUE); //flag so modify validates
            attrs.put(OfflineConstants.A_offlineRemotePassword, newPass);
            prov.modifyAttrs(acct, attrs);
            status = "success";
        } else {
            List<DataSource> dataSources = acct.getAllDataSources();
            if (dataSources != null) {
                for (DataSource ds : dataSources) {
                    boolean needModify = false;
                    if (ds.getAttr(Provisioning.A_zimbraDataSourcePassword) != null) {
                        attrs.put(Provisioning.A_zimbraDataSourcePassword, newPass);
                        needModify = true;
                    }
                    if (ds.getAttr(OfflineConstants.A_zimbraDataSourceSmtpAuthPassword) != null) {
                        attrs.put(OfflineConstants.A_zimbraDataSourceSmtpAuthPassword, newPass);
                        needModify = true;
                    }
                    if (needModify) {
                        String domain = ds.getAttr(Provisioning.A_zimbraDataSourceDomain);
                        if ("yahoo.com".equals(domain)) {
                            OfflineYAuth.removeToken(ds);
                        }
                        prov.modifyDataSource(acct, ds.getId(), attrs);
                        prov.testDataSource((OfflineDataSource) ds);
                        status = "success";
                    }
                }
            }
        }
        Element response = getResponseElement(zsc);
        response.addAttribute(AccountConstants.A_STATUS, status);
        return response;
    }
}
