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
