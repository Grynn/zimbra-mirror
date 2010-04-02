/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.service.offline;

import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentService;
import com.zimbra.soap.SoapContextExtension;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.offline.common.OfflineConstants;

public class OfflineService implements DocumentService {

    public void registerHandlers(DocumentDispatcher dispatcher) {
        // sync
        dispatcher.registerHandler(OfflineConstants.SYNC_REQUEST, new OfflineSync());
        
        dispatcher.registerHandler(MailConstants.NO_OP_REQUEST, new OfflineNoOp());

        // fetching external data
        dispatcher.registerHandler(MailConstants.FOLDER_ACTION_REQUEST, new OfflineFolderAction());
        dispatcher.registerHandler(MailConstants.CREATE_FOLDER_REQUEST, new OfflineCreateFolder());
        dispatcher.registerHandler(MailConstants.CREATE_MOUNTPOINT_REQUEST, new OfflineCreateMountpoint());
        dispatcher.registerHandler(MailConstants.GET_IMPORT_STATUS_REQUEST, new OfflineGetImportStatus());
        dispatcher.registerHandler(MailConstants.IMPORT_DATA_REQUEST, new OfflineImportData());
        dispatcher.registerHandler(AccountConstants.GET_INFO_REQUEST, new OfflineGetInfo());
        
        dispatcher.registerHandler(MailConstants.DIFF_DOCUMENT_REQUEST, new OfflineDocumentHandlers.DiffDocument());
        dispatcher.registerHandler(MailConstants.LIST_DOCUMENT_REVISIONS_REQUEST, new OfflineDocumentHandlers.ListDocumentRevisions());
        dispatcher.registerHandler(MailConstants.GET_WIKI_REQUEST, new OfflineDocumentHandlers.GetWiki());
        dispatcher.registerHandler(MailConstants.SAVE_DOCUMENT_REQUEST, new OfflineSaveDocument());
        dispatcher.registerHandler(MailConstants.SAVE_WIKI_REQUEST, new OfflineDocumentHandlers.SaveWiki());
        dispatcher.registerHandler(MailConstants.WIKI_ACTION_REQUEST, new OfflineDocumentHandlers.WikiAction());
        
        dispatcher.registerHandler(MailConstants.AUTO_COMPLETE_REQUEST, new OfflineAutoComplete());        
        dispatcher.registerHandler(AccountConstants.SEARCH_GAL_REQUEST, new OfflineSearchGal());
        dispatcher.registerHandler(MailConstants.GET_FREE_BUSY_REQUEST, new OfflineGetFreeBusy());
        dispatcher.registerHandler(AccountConstants.SEARCH_CALENDAR_RESOURCES_REQUEST, OfflineServiceProxy.SearchCalendarResources());
        dispatcher.registerHandler(MailConstants.CREATE_APPOINTMENT_REQUEST, new OfflineCreateAppointment());
        dispatcher.registerHandler(MailConstants.MODIFY_APPOINTMENT_REQUEST, new OfflineModifyAppointment());
        dispatcher.registerHandler(MailConstants.CREATE_TASK_REQUEST, new OfflineCreateTask());
        dispatcher.registerHandler(MailConstants.MODIFY_TASK_REQUEST, new OfflineModifyTask());
        dispatcher.registerHandler(MailConstants.CONV_ACTION_REQUEST, new OfflineConvAction());
        dispatcher.registerHandler(MailConstants.GET_PERMISSION_REQUEST, OfflineServiceProxy.GetPermission());
        dispatcher.registerHandler(MailConstants.GRANT_PERMISSION_REQUEST, OfflineServiceProxy.GrantPermission());
        dispatcher.registerHandler(MailConstants.REVOKE_PERMISSION_REQUEST, OfflineServiceProxy.RevokePermission());
        dispatcher.registerHandler(MailConstants.CHECK_PERMISSION_REQUEST, OfflineServiceProxy.CheckPermission());
        dispatcher.registerHandler(AccountConstants.GET_SHARE_INFO_REQUEST, OfflineServiceProxy.GetShareInfoRequest());
        dispatcher.registerHandler(AccountConstants.AUTO_COMPLETE_GAL_REQUEST, OfflineServiceProxy.AutoCompleteGalRequest());
        dispatcher.registerHandler(MailConstants.SAVE_DRAFT_REQUEST, new OfflineSaveDraft());
        
        dispatcher.registerHandler(OfflineConstants.CLIENT_EVENT_NOTIFY_REQUEST, new OfflineClientEventNotify());
        dispatcher.registerHandler(OfflineConstants.GET_EXTENSIONS_REQUEST, new OfflineGetExtensions());
        
        // not the most suitable place to do this, but it's just too easy.
        SoapContextExtension.register(OfflineContextExtension.ZDSYNC, new OfflineContextExtension());
    }
}