/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.soap.DocumentHandler;

public class OfflineClientEventNotify extends DocumentHandler {

    private static final long DELAY = 30 * Constants.MILLIS_PER_SECOND;

    private static long lastUiLoadingBegin = 0L;
    private static long uiLoadingBegin = 0L;
    private static long uiLoadingEnd = 0L;

    private static Runnable task = new Runnable() {
        @Override
        public void run() {
            OfflineLog.offline.debug("setting uiloading to false by timer");
            OfflineSyncManager.getInstance().setUILoading(false);
        }
    };

    private static ScheduledThreadPoolExecutor uiLoadingEndTimer = new ScheduledThreadPoolExecutor(1);
    private static Future future = null;

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {

        String event = request.getAttribute(OfflineConstants.A_Event);
        if (event.equals(OfflineConstants.EVENT_UI_LOAD_BEGIN)) {
            lastUiLoadingBegin = uiLoadingBegin;
            uiLoadingBegin = System.currentTimeMillis();
            if (lastUiLoadingBegin > 0 && (uiLoadingBegin - lastUiLoadingBegin < DELAY)) {
                if (future != null) {
                    future.cancel(true);
                }
            }
            OfflineSyncManager.getInstance().setUILoading(true);
            future = uiLoadingEndTimer.schedule(task, 30, TimeUnit.SECONDS);
        } else if (event.equals(OfflineConstants.EVENT_UI_LOAD_END)) {
            uiLoadingEnd = System.currentTimeMillis();
            if (uiLoadingEnd > uiLoadingBegin) {
                future.cancel(true);
            }
            OfflineSyncManager.getInstance().setUILoading(false);
        } else if (event.equals(OfflineConstants.EVENT_NETWORK_DOWN)) {
            OfflineSyncManager.getInstance().setConnectionDown(true);
        } else if (event.equals(OfflineConstants.EVENT_NETWORK_UP)) {
            OfflineSyncManager.getInstance().setConnectionDown(false);
        } else if (event.equals(OfflineConstants.EVENT_SHUTTING_DOWN)) {
            OfflineSyncManager.getInstance().shutdown();
        } else {
            throw OfflineServiceException.UNKNOWN_CLIENT_EVENT(event);
        }

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
