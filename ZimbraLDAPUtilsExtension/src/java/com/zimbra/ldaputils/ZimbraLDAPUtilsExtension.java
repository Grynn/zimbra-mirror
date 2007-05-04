/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.ldaputils;

import com.zimbra.common.service.ServiceException;
import com.zimbra.soap.SoapServlet;
import com.zimbra.cs.extension.ZimbraExtension;
/**
 * @author Greg Solovyev
 */
public class ZimbraLDAPUtilsExtension implements ZimbraExtension {
    public static final String EXTENSION_NAME_ZIMBRASAMBA = "zimbrasamba";
    
    public void init() throws ServiceException {
        SoapServlet.addService("AdminServlet", new ZimbraLDAPUtilsService());
    }

    public void destroy() {
        
    }
    
    public String getName() {
        return EXTENSION_NAME_ZIMBRASAMBA;
    }

}
