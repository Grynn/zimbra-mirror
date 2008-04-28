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

import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.account.ldap.ZimbraLdapContext;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;
/**
 * @author Greg Solovyev
 */
public class RenameLDAPEntry extends AdminDocumentHandler {

	public Element handle(Element request, Map<String, Object> context)
			throws ServiceException {

		ZimbraSoapContext lc = getZimbraSoapContext(context);
		String dn = request.getAttribute(ZimbraLDAPUtilsService.E_DN);
		String new_dn = request.getAttribute(ZimbraLDAPUtilsService.E_NEW_DN);

		ZimbraLdapContext zlc = null;
		try {
		    zlc = new ZimbraLdapContext(true);
		    zlc.renameEntry(dn, new_dn);
    		NamedEntry ne = GetLDAPEntries.getObjectByDN(new_dn, zlc);
    		ZimbraLog.security.info(ZimbraLog.encodeAttrs(new String[] { "cmd",
    				"RenameLDAPEntry", "dn", dn,"new_dn",new_dn }, null));
    		
    		
    		Element response = lc
    				.createElement(ZimbraLDAPUtilsService.RENAME_LDAP_ENTRY_RESPONSE);
    		ZimbraLDAPUtilsService.encodeLDAPEntry(response, ne);

    		return response;

		} catch (NameAlreadyBoundException nabe) {
            throw ZimbraLDAPUtilsServiceException.DN_EXISTS(new_dn);            
        } catch (NamingException e) {
            throw ServiceException.FAILURE("unable to rename dn: "+dn+ "to " +new_dn, e);
        } finally {
            ZimbraLdapContext.closeContext(zlc);
        }
	}

}
