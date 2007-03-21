/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.service.offline;

import org.dom4j.Namespace;
import org.dom4j.QName;

import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentService;
import com.zimbra.common.soap.MailConstants;

public class OfflineService implements DocumentService {

    public static final String NAMESPACE_STR = "urn:zimbraOffline";
    public static final Namespace NAMESPACE = Namespace.get(NAMESPACE_STR);

    // sync
    public static final QName SYNC_REQUEST = QName.get("SyncRequest", NAMESPACE);
    public static final QName SYNC_RESPONSE = QName.get("SyncResponse", NAMESPACE);

    public void registerHandlers(DocumentDispatcher dispatcher) {
        // sync
        dispatcher.registerHandler(SYNC_REQUEST, new OfflineSync());

        // fetching external data
        dispatcher.registerHandler(MailConstants.FOLDER_ACTION_REQUEST, new OfflineFolderAction());
        dispatcher.registerHandler(MailConstants.GET_IMPORT_STATUS_REQUEST, new OfflineGetImportStatus());
        dispatcher.registerHandler(MailConstants.IMPORT_DATA_REQUEST, new OfflineImportData());
    }
}
