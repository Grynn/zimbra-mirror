/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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
package com.zimbra.ldaputils;

import java.util.Map;

import org.dom4j.Namespace;
import org.dom4j.QName;
import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.service.account.AccountService;
import com.zimbra.cs.service.admin.AdminService;
import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentService;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.AdminConstants;

/**
 * @author Greg Solovyev
 */
public class ZimbraLDAPUtilsService implements DocumentService {

	public static final String NAMESPACE_STR = "urn:zimbraAdmin";
	public static final Namespace NAMESPACE = Namespace.get(NAMESPACE_STR);
		
    public static final QName GET_LDAP_ENTRIES_REQUEST = QName.get("GetLDAPEntriesRequest", NAMESPACE);
    public static final QName GET_LDAP_ENTRIES_RESPONSE = QName.get("GetLDAPEntriesResponse", NAMESPACE);
    
    public static final QName CREATE_LDAP_ENTRIY_REQUEST = QName.get("CreateLDAPEntryRequest", NAMESPACE);
    public static final QName CREATE_LDAP_ENTRY_RESPONSE = QName.get("CreateLDAPEntryResponse", NAMESPACE);

    public static final QName MODIFY_LDAP_ENTRIY_REQUEST = QName.get("ModifyLDAPEntryRequest", NAMESPACE);
    public static final QName MODIFY_LDAP_ENTRY_RESPONSE = QName.get("ModifyLDAPEntryResponse", NAMESPACE);

    public static final QName RENAME_LDAP_ENTRIY_REQUEST = QName.get("RenameLDAPEntryRequest", NAMESPACE);
    public static final QName RENAME_LDAP_ENTRY_RESPONSE = QName.get("RenameLDAPEntryResponse", NAMESPACE);
    
    public static final QName DELETE_LDAP_ENTRIY_REQUEST = QName.get("DeleteLDAPEntryRequest", NAMESPACE);
    public static final QName DELETE_LDAP_ENTRY_RESPONSE = QName.get("DeleteLDAPEntryResponse", NAMESPACE);
    
    public static final String E_LDAPEntry = "LDAPEntry";
    public static final String E_DN = "dn";
    public static final String E_NEW_DN = "new_dn";    
    public static final String E_LDAPSEARCHBASE = "ldapSearchBase";    
    

    public void registerHandlers(DocumentDispatcher dispatcher) {
		dispatcher.registerHandler(GET_LDAP_ENTRIES_REQUEST, new GetLDAPEntries());
		dispatcher.registerHandler(CREATE_LDAP_ENTRIY_REQUEST, new CreateLDAPEntry());		
		dispatcher.registerHandler(MODIFY_LDAP_ENTRIY_REQUEST, new ModifyLDAPEntry());		
		dispatcher.registerHandler(RENAME_LDAP_ENTRIY_REQUEST, new RenameLDAPEntry());
		dispatcher.registerHandler(DELETE_LDAP_ENTRIY_REQUEST, new DeleteLDAPEntry());	
    }

    public static Element encodeLDAPEntry(Element parent, NamedEntry ld) {
        Element LDAPEntryEl = parent.addElement(ZimbraLDAPUtilsService.E_LDAPEntry);
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
