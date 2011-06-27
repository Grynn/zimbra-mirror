/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010, 2011 Zimbra, Inc.
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

import com.zimbra.common.soap.AdminExtConstants;
import com.zimbra.soap.DocumentService;
import com.zimbra.soap.DocumentDispatcher;

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: Sep 11, 2008
 * Time: 10:59:29 AM
 */
public class ZimbraBulkProvisionService  implements DocumentService {

    public void registerHandlers(DocumentDispatcher dispatcher) {
        dispatcher.registerHandler(AdminExtConstants.BULK_IMPORT_ACCOUNTS_REQUEST, new BulkImportAccounts());
        dispatcher.registerHandler(AdminExtConstants.GENERATE_BULK_PROV_FROM_LDAP_REQUEST, new GenerateBulkProvisionFileFromLDAP());
        dispatcher.registerHandler(AdminExtConstants.BULK_IMAP_DATA_IMPORT_REQUEST, new BulkIMAPDataImport());
        dispatcher.registerHandler(AdminExtConstants.GET_BULK_IMAP_IMPORT_TASKLIST_REQUEST, new GetBulkIMAPImportTaskList());
        dispatcher.registerHandler(AdminExtConstants.PURGE_BULK_IMAP_IMPORT_TASKS_REQUEST, new PurgeIMAPImportTasks());
    }
}
