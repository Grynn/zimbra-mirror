/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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

import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.service.account.GetInfo;
import com.zimbra.cs.session.Session;
import com.zimbra.cs.session.SoapSession;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineGetInfo extends GetInfo {
    @Override
    protected Element encodeChildAccount(Element parent, Account child,
        boolean isVisible) {
        String accountName = child.getAttr(Provisioning.A_zimbraPrefLabel);
        Element elem = super.encodeChildAccount(parent, child, isVisible);
        
        accountName = accountName != null ? accountName : child.getAttr(
            OfflineConstants.A_offlineAccountName);
        if (elem != null && accountName != null) {
            Element attrsElem = elem.addUniqueElement(AccountConstants.E_ATTRS);
            
            if (accountName != null)
                attrsElem.addKeyValuePair(Provisioning.A_zimbraPrefLabel,
                    accountName, AccountConstants.E_ATTR, AccountConstants.A_NAME);
        }
        return elem;
    }
    
    @Override
    protected Session getSession(ZimbraSoapContext zsc, Session.Type stype) {
        Session s = super.getSession(zsc, stype);
        if (!s.isDelegatedSession())
            ((SoapSession)s).setOfflineSoapSession();
        return s;
    }
}
