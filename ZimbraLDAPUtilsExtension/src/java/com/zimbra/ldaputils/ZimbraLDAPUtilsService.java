/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011 Zimbra, Inc.
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

import com.zimbra.cs.account.NamedEntry;
import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentService;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.LDAPUtilsConstants;

/**
 * @author Greg Solovyev
 */
public class ZimbraLDAPUtilsService implements DocumentService {

    public void registerHandlers(DocumentDispatcher dispatcher) {
        dispatcher.registerHandler(LDAPUtilsConstants.GET_LDAP_ENTRIES_REQUEST, new GetLDAPEntries());
        dispatcher.registerHandler(LDAPUtilsConstants.CREATE_LDAP_ENTRIY_REQUEST, new CreateLDAPEntry());
        dispatcher.registerHandler(LDAPUtilsConstants.MODIFY_LDAP_ENTRIY_REQUEST, new ModifyLDAPEntry());
        dispatcher.registerHandler(LDAPUtilsConstants.RENAME_LDAP_ENTRIY_REQUEST, new RenameLDAPEntry());
        dispatcher.registerHandler(LDAPUtilsConstants.DELETE_LDAP_ENTRIY_REQUEST, new DeleteLDAPEntry());
    }

    public static Element encodeLDAPEntry(Element parent, NamedEntry ld) {
        Element LDAPEntryEl = parent.addElement(LDAPUtilsConstants.E_LDAPEntry);
        LDAPEntryEl.addAttribute(AdminConstants.A_NAME, ld.getName());
        Map<String, Object> attrs = ld.getAttrs(false);
        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String[]) {
                String sv[] = (String[]) value;
                for (int i = 0; i < sv.length; i++)
                    LDAPEntryEl.addElement(AdminConstants.E_A).addAttribute(AdminConstants.A_N, name).setText(sv[i]);
            } else if (value instanceof String)
                LDAPEntryEl.addElement(AdminConstants.E_A).addAttribute(AdminConstants.A_N, name).setText((String) value);
        }
        return LDAPEntryEl;
    }
}
