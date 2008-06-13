/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */

/*
 * Created on Jun 17, 2004
 */
package com.zimbra.yahoosmb;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AttributeManager;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminService;
import com.zimbra.cs.service.account.ToXML;
import com.zimbra.soap.ZimbraSoapContext;
import java.util.Map;

/**
 * @author ccao
 */
public class CreateSmbAccount extends AdminDocumentHandler {

    /**
     * must be careful and only create accounts for the domain admin!
     */
    public boolean domainAuthSufficient(Map context) {
        return true;
    }

	public Element handle(Element request, Map<String, Object> context) throws ServiceException {

        ZimbraSoapContext lc = getZimbraSoapContext(context);
	    Provisioning prov = Provisioning.getInstance();

	    String name = request.getAttribute(AdminConstants.E_NAME).toLowerCase();
	    String password = request.getAttribute(AdminConstants.E_PASSWORD, null);
	    Map<String, Object> attrs = AdminService.getAttrs(request, true);
        Map<String, Object> y_attrs = ZimbraYahooSmbService.getYahooAttrs (request, true) ;
        
        if (!canAccessEmail(lc, name))
            throw ServiceException.PERM_DENIED("can not access account:"+name);

        if (isDomainAdminOnly(lc)) {
            //TODO: need to allow some special cases for the smb account attributes
            for (String attrName : attrs.keySet()) {
                if (attrName.charAt(0) == '+' || attrName.charAt(0) == '-')
                    attrName = attrName.substring(1);

                if ((!AttributeManager.getInstance().isDomainAdminModifiable(attrName)) &&
                        (!ZimbraYahooSmbService.isSmbDomainAdminModifiable(attrName))) {
                    throw ServiceException.PERM_DENIED("can not modify attr: "+attrName);
                }
            }
        }

        Account account = prov.createAccount(name, password, attrs);
        ZimbraLog.security.info(ZimbraLog.encodeAttrs(
                new String[] {"cmd", "CreateSmbAccount","name", name}, attrs));

        //send the notification email
        String notification_email = (String) y_attrs.get(ZimbraYahooSmbService.A_NOTIFICATION_EMAIL) ;
        ZimbraLog.security.info("Sending the notification email to " + notification_email);

        Element response = lc.createElement(ZimbraYahooSmbService.CREATE_SMB_ACCOUNT_RESPONSE);

        ToXML.encodeAccountOld(response, account);

	    return response;
	}
}