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

import com.zimbra.common.soap.AdminConstants;
import com.zimbra.soap.DocumentService;
import com.zimbra.soap.DocumentDispatcher;

import org.dom4j.Namespace;
import org.dom4j.QName;

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: Sep 11, 2008
 * Time: 10:59:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class ZimbraBulkProvisionService  implements DocumentService {
    public static final String NAMESPACE_STR = "urn:zimbraAdminExt";
    public static final Namespace NAMESPACE = Namespace.get(NAMESPACE_STR);

    public static final QName GET_BULK_PROVISION_ACCOUNTS_REQUEST = QName.get("GetBulkProvisionAccountsRequest", NAMESPACE) ;
    public static final QName GET_BULK_PROVISION_ACCOUNTS_RESPONSE = QName.get("GetBulkProvisionAccountsResponse", NAMESPACE) ;

    public static final QName UPDATE_BULK_PROVISION_STATUS_REQUEST = QName.get("UpdateBulkProvisionStatusRequest", NAMESPACE) ;
    public static final QName UPDATE_BULK_PROVISION_STATUS_RESPONSE = QName.get("UpdateBulkProvisionStatusResponse", NAMESPACE) ;
    
    public static final QName BULK_IMPORT_ACCOUNTS_REQUEST = QName.get("BulkImportAccountsRequest", NAMESPACE) ;
    public static final QName BULK_IMPORT_ACCOUNTS_RESPONSE = QName.get("BulkImportAccountsResponse", NAMESPACE) ;

    public static final QName GENERATE_BULK_PROV_FROM_LDAP_REQUEST = QName.get("GenerateBulkProvisionFileFromLDAPRequest", NAMESPACE) ;
    public static final QName GENERATE_BULK_PROV_FROM_LDAP_RESPONSE = QName.get("GenerateBulkProvisionFileFromLDAPResponse", NAMESPACE) ;
	
    public static final QName BULK_IMAP_DATA_IMPORT_REQUEST = QName.get("BulkIMAPDataImportRequest", NAMESPACE) ;
    public static final QName BULK_IMAP_DATA_IMPORT_RESPONSE = QName.get("BulkIMAPDataImportResponse", NAMESPACE) ;
    
    public static final QName GET_BULK_IMAP_IMPORT_TASK_REQUEST = QName.get("GetBulkIMAPImportTaskRequest", NAMESPACE) ;
    public static final QName GET_BULK_IMAP_IMPORT_TASK_RESPONSE = QName.get("GetBulkIMAPImportTaskResponse", NAMESPACE) ;

    public static final QName GET_BULK_IMAP_IMPORT_TASKLIST_REQUEST = QName.get("GetBulkIMAPImportTaskListRequest", NAMESPACE) ;
    public static final QName GET_BULK_IMAP_IMPORT_TASKLIST_RESPONSE = QName.get("GetBulkIMAPImportTaskListResponse", NAMESPACE) ;

    public static final QName PURGE_BULK_IMAP_IMPORT_TASKS_REQUEST = QName.get("PurgeBulkIMAPImportTasksRequest", NAMESPACE) ;
    public static final QName PURGE_BULK_IMAP_IMPORT_TASKS_RESPONSE = QName.get("PurgeBulkIMAPImportTasksResponse", NAMESPACE) ;
    
    public void registerHandlers(DocumentDispatcher dispatcher) {
        dispatcher.registerHandler(GET_BULK_PROVISION_ACCOUNTS_REQUEST, new GetBulkProvisionAccounts());
        dispatcher.registerHandler(UPDATE_BULK_PROVISION_STATUS_REQUEST, new UpdateBulkProvisionStatus());
        dispatcher.registerHandler(BULK_IMPORT_ACCOUNTS_REQUEST, new BulkImportAccounts());
        dispatcher.registerHandler(GENERATE_BULK_PROV_FROM_LDAP_REQUEST, new GenerateBulkProvisionFileFromLDAP());
        dispatcher.registerHandler(BULK_IMAP_DATA_IMPORT_REQUEST, new BulkIMAPDataImport());
        dispatcher.registerHandler(GET_BULK_IMAP_IMPORT_TASKLIST_REQUEST, new GetBulkIMAPImportTaskList());
        dispatcher.registerHandler(PURGE_BULK_IMAP_IMPORT_TASKS_REQUEST, new PurgeIMAPImportTasks());
    }
}
