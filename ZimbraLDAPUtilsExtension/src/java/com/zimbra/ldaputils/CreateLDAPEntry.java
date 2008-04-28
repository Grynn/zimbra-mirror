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

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.InvalidAttributesException;
import javax.naming.directory.SchemaViolationException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.AttributeManager;

import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.account.ldap.ZimbraLdapContext;

import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminService;

import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.cs.account.NamedEntry;
/**
 * @author Greg Solovyev
 */
public class CreateLDAPEntry extends AdminDocumentHandler {

	public Element handle(Element request, Map<String, Object> context)
			throws ServiceException {
       
		ZimbraSoapContext lc = getZimbraSoapContext(context);
	    	    
	    String dn = request.getAttribute(ZimbraLDAPUtilsService.E_DN);
	    Map<String, Object> attrs = AdminService.getAttrs(request, true);
	    
	    
	    NamedEntry ne = createLDAPEntry(dn,  attrs);

        ZimbraLog.security.info(ZimbraLog.encodeAttrs(
                new String[] {"cmd", "CreateLDAPEntry","dn", dn}, attrs));

	    Element response = lc.createElement(ZimbraLDAPUtilsService.CREATE_LDAP_ENTRY_RESPONSE);
	    ZimbraLDAPUtilsService.encodeLDAPEntry(response,ne);
	    

	    return response;
	}
	

    public NamedEntry createLDAPEntry(String dn, Map<String, Object> entryAttrs) throws ServiceException {
        HashMap attrManagerContext = new HashMap();
        AttributeManager.getInstance().preModify(entryAttrs, null, attrManagerContext, true, true);

        ZimbraLdapContext zlc = null;
        try {
            zlc = new ZimbraLdapContext(true);

            Attributes attrs = new BasicAttributes(true);
            LdapUtil.mapToAttrs(entryAttrs, attrs);

            zlc.createEntry(dn, attrs, "createLDAPEntry");

            NamedEntry entry = GetLDAPEntries.getObjectByDN(dn, zlc);
            AttributeManager.getInstance().postModify(entryAttrs, entry, attrManagerContext, true);
            return entry;

        } catch (NameAlreadyBoundException nabe) {
            throw ZimbraLDAPUtilsServiceException.DN_EXISTS(dn);
        } finally {
            ZimbraLdapContext.closeContext(zlc);
        }
    }
    
}
