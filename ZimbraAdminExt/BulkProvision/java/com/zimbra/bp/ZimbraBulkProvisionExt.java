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
    public static final String FILE_FORMAT_BULK_AD = "ad";
    public static final String FILE_FORMAT_ZIMBRA = "zimbra";
    public static final String EXCHANGE_IMAP = "EXCHANGE_IMAP";
    public static final String DEFAULT_INDEX_BATCH_SIZE = "40";
    
    public static final String OP_GET_STATUS = "getStatus";
    public static final String OP_PREVIEW = "preview";
    public static final String OP_PREVIEW_ACTIVE_IMPORTS = "previewActiveImports";
    public static final String OP_START_IMPORT = "startImport";
    public static final String OP_ABORT_IMPORT = "abortImport";
    public static final String OP_DISMISS_IMPORT = "dismissImport";
    public static final String IMAP_IMPORT_DS_NAME = "__imap_import__";

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
