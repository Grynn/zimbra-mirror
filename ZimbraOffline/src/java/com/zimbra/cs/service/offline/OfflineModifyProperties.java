/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.service.account.ModifyProperties;
import com.zimbra.cs.zimlet.ZimletUserProperties;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineModifyProperties extends ModifyProperties {

    private static final Set<String> PROP_UNDER_LOCAL_ACCT_ZIMLETS = new HashSet<String>(3);//change number if more zimlets are added
    static {
        PROP_UNDER_LOCAL_ACCT_ZIMLETS.add("com_zimbra_apptsummary");
        PROP_UNDER_LOCAL_ACCT_ZIMLETS.add("com_zimbra_social");
        PROP_UNDER_LOCAL_ACCT_ZIMLETS.add("com_zdesktop_survey");
    }

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account account = getRequestedAccount(zsc);

        if (!canModifyOptions(zsc, account))
            throw ServiceException.PERM_DENIED("can not modify options");

        ZimletUserProperties props = ZimletUserProperties.getProperties(account);
        Account localAccount = OfflineProvisioning.getOfflineInstance().getLocalAccount();
        ZimletUserProperties localAcctProps = ZimletUserProperties.getProperties(localAccount);

        for (Element e : request.listElements(AccountConstants.E_PROPERTY)) {
            String zimlet = e.getAttribute(AccountConstants.A_ZIMLET);
            if (PROP_UNDER_LOCAL_ACCT_ZIMLETS.contains(zimlet)) {
                localAcctProps.setProperty(zimlet, e.getAttribute(AccountConstants.A_NAME), e.getText());
            } else {
                props.setProperty(zimlet, e.getAttribute(AccountConstants.A_NAME), e.getText());
            }
        }
        props.saveProperties(account);
        localAcctProps.saveProperties(localAccount);
        Element response = zsc.createElement(AccountConstants.MODIFY_PROPERTIES_RESPONSE);
        return response;
    }
}