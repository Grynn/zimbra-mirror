/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
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
import com.zimbra.cs.service.mail.ModifyContact;
import com.zimbra.cs.service.util.ItemId;

public class OfflineModifyContact extends ModifyContact {

    @Override
    protected Element proxyRequest(Element request, Map<String, Object> context, ItemId iidRequested, ItemId iidResolved)
            throws ServiceException {
        OfflineDocumentHandlers.uploadAttachmentToRemoteServer(request, iidRequested, iidResolved);
        return super.proxyRequest(request, context, iidRequested, iidResolved);
    }
}
