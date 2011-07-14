/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.ldaputils;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminService;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.LDAPUtilsConstants;
import com.zimbra.soap.ZimbraSoapContext;
/**
 * @author Greg Solovyev
 */
public class ModifyLDAPEntry extends AdminDocumentHandler {

    public Element handle(Element request, Map<String, Object> context)
            throws ServiceException {

        ZimbraSoapContext lc = getZimbraSoapContext(context);
        String dn = request.getAttribute(LDAPUtilsConstants.E_DN);
        if(dn==null)
            throw ServiceException.INVALID_REQUEST("Missing request parameter: "+LDAPUtilsConstants.E_DN, null);

        Map<String, Object> attrs = AdminService.getAttrs(request);

        NamedEntry newNe = LDAPUtilsHelper.getInstance().modifyLDAPEntry(dn,  attrs);

        ZimbraLog.security.info(ZimbraLog.encodeAttrs(new String[] { "cmd",
                    "SaveLDAPEntry", "dn", dn }, attrs));

        Element response = lc.createElement(LDAPUtilsConstants.MODIFY_LDAP_ENTRY_RESPONSE);
        ZimbraLDAPUtilsService.encodeLDAPEntry(response, newNe);

        return response;

    }
}
