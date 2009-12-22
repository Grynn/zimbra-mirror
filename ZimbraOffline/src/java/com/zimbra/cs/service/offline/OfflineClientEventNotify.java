/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009 Zimbra, Inc.
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
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.soap.DocumentHandler;

public class OfflineClientEventNotify extends DocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context)
    throws ServiceException {

        String event = request.getAttribute(OfflineConstants.A_Event);
        if (event.equals(OfflineConstants.EVENT_UI_LOAD_BEGIN))
            OfflineSyncManager.getInstance().setUILoading(true);
        else if (event.equals(OfflineConstants.EVENT_UI_LOAD_END))
            OfflineSyncManager.getInstance().setUILoading(false);
        else if (event.equals(OfflineConstants.EVENT_NETWORK_DOWN))
            OfflineSyncManager.getInstance().setConnectionDown(true);
        else if (event.equals(OfflineConstants.EVENT_NETWORK_UP))
            OfflineSyncManager.getInstance().setConnectionDown(false);
        else if (event.equals(OfflineConstants.EVENT_SHUTTING_DOWN))
            OfflineSyncManager.getInstance().shutdown();
        else
            throw OfflineServiceException.UNKNOWN_CLIENT_EVENT(event); 

        return getZimbraSoapContext(context).createElement(OfflineConstants.CLIENT_EVENT_NOTIFY_RESPONSE);
    }

    @Override
    public boolean needsAuth(Map<String, Object> context) {
        return false;
    }

    @Override
    public boolean needsAdminAuth(Map<String, Object> context) {
        return false;
    }
}
