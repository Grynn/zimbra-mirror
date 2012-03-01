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

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.account.ldap.entry.LdapDomain;

import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.LDAPUtilsConstants;
import com.zimbra.soap.ZimbraSoapContext;

import com.zimbra.cs.service.admin.AdminDocumentHandler;



/**
 * @author Greg Solovyev
 */
public class GetLDAPEntries extends AdminDocumentHandler {
    public static final String C_LDAPEntry = "LDAPEntry";

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {

        ZimbraSoapContext lc = getZimbraSoapContext(context);

        Element b = request.getElement(LDAPUtilsConstants.E_LDAPSEARCHBASE);
        String ldapSearchBase;
        if(isDomainAdminOnly(lc)) {
            ldapSearchBase = ((LdapDomain)getAuthTokenAccountDomain(lc)).getDN();
        } else {
            ldapSearchBase = b.getText();
        }
        String sortBy = request.getAttribute(AdminConstants.A_SORT_BY, null);
        boolean sortAscending = request.getAttributeBool(AdminConstants.A_SORT_ASCENDING, true);
        int limit = (int) request.getAttributeLong(AdminConstants.A_LIMIT, Integer.MAX_VALUE);
        if (limit == 0)
            limit = Integer.MAX_VALUE;

        int offset = (int) request.getAttributeLong(AdminConstants.A_OFFSET, 0);
        String query = request.getAttribute(AdminConstants.E_QUERY);

        List LDAPEntrys;
        LDAPEntrys = LDAPUtilsHelper.getInstance().searchObjects(query,ldapSearchBase,sortBy,sortAscending);

        Element response = lc.createElement(LDAPUtilsConstants.GET_LDAP_ENTRIES_RESPONSE);
        int i, limitMax = offset+limit;
        for (i=offset; i < limitMax && i < LDAPEntrys.size(); i++) {
            NamedEntry entry = (NamedEntry) LDAPEntrys.get(i);
            ZimbraLDAPUtilsService.encodeLDAPEntry(response,entry);
        }

        return response;
    }

    /** Returns whether domain admin auth is sufficient to run this command.
     *  This should be overriden only on admin commands that can be run in a
     *  restricted "domain admin" mode. */
    public boolean domainAuthSufficient(Map<String, Object> context) {
        return true; 
    }


}
