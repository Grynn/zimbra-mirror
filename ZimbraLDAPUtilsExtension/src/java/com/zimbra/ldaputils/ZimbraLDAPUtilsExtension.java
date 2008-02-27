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

import com.zimbra.common.service.ServiceException;
import com.zimbra.soap.SoapServlet;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.account.AttributeManager;
/**
 * @author Greg Solovyev
 */
public class ZimbraLDAPUtilsExtension implements ZimbraExtension {
    public static final String EXTENSION_NAME_ZIMBRASAMBA = "zimbrasamba";
    
    public void init() throws ServiceException {
    	AttributeManager.getInstance().makeDomainAdminModifiable("isSpecialNTAccount");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaSID");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaAcctFlags");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaBadPasswordCount");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaBadPasswordTime");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaDomainName");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaDomainSID");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaHomeDrive");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaHomePath");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaKickoffTime");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaLMPassword");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaLogoffTime");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaLogonHours");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaLogonScript");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaLogonTime");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaMungedDial");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaNTPassword");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaPasswordHistory");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaPrimaryGroupSID");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaProfilePath");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaPwdCanChange");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaPwdLastSet");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaPwdMustChange");
    	AttributeManager.getInstance().makeDomainAdminModifiable("sambaUserWorkstations");

    	AttributeManager.getInstance().makeDomainAdminModifiable("gidNumber");
    	AttributeManager.getInstance().makeDomainAdminModifiable("homeDirectory");
    	AttributeManager.getInstance().makeDomainAdminModifiable("uidNumber");
    	AttributeManager.getInstance().makeDomainAdminModifiable("gecos");
    	AttributeManager.getInstance().makeDomainAdminModifiable("loginShell");
    	AttributeManager.getInstance().makeDomainAdminModifiable("userPassword");   	
    	
        SoapServlet.addService("AdminServlet", new ZimbraLDAPUtilsService());
    }

    public void destroy() {
        
    }
    
    public String getName() {
        return EXTENSION_NAME_ZIMBRASAMBA;
    }

}
