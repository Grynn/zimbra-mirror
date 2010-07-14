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
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.service.util.ItemId;

public class OfflineSaveDocument extends OfflineDocumentHandlers.SaveDocument {
    
    @Override
    protected Element proxyRequest(Element request, Map<String, Object> context, ItemId iidRequested, ItemId iidResolved)
        throws ServiceException {
        Element eUpload = request.getElement(MailConstants.E_DOC).getOptionalElement(MailConstants.E_UPLOAD);
        if (eUpload != null) {
            String id = eUpload.getAttribute(MailConstants.A_ID);
            String acctId = iidRequested.getAccountId();        
            eUpload.addAttribute(MailConstants.A_ID, OfflineDocumentHandlers.uploadOfflineDocument(id, acctId)); 
        }
        return super.proxyRequest(request, context, iidRequested, iidResolved);
    }
}
