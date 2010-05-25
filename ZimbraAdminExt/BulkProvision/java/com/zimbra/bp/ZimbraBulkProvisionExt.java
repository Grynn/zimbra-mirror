/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.bp;

import com.zimbra.common.service.ServiceException;
import com.zimbra.soap.SoapServlet;
import com.zimbra.cs.extension.ZimbraExtension;

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: Sep 11, 2008
 * Time: 10:56:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class ZimbraBulkProvisionExt implements ZimbraExtension {

    public static final String EXTENSION_NAME_BULKPROVISION = "com_zimbra_bulkprovision";
	public static final String FILE_FORMAT_BULK_LDAP = "ldap";
	public static final String A_password = "password" ;
	public static final String A_generatePassword = "generatePassword";
	public static final String A_genPasswordLength = "genPasswordLength";
	public static final String A_fileFormat = "fileFormat";
	public static final String A_maxResults = "maxResults";
	public static final String A_setMustChangePwd = "setMustChangePwd";
	public static final String E_User = "User";
	public static final String E_ExchangeMail = "ExchangeMail";
	public static final String E_ZCSImport = "ZCSImport";
	public static final String E_ImportUsers = "ImportUsers";

    public void destroy() {
    }

    public String getName() {
        return EXTENSION_NAME_BULKPROVISION ;
    }

    public void init() throws ServiceException {
        //need to add the service calls to the admin soap calls
        SoapServlet.addService("AdminServlet", new ZimbraBulkProvisionService());
    }
               
    
}
