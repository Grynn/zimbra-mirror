/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
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
package com.zimbra.cs.session;

import java.util.Iterator;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.HeaderConstants;
import com.zimbra.common.soap.ZimbraNamespace;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.service.util.ItemIdFormatter;

public class OfflineSoapSession extends SoapSession {

    public OfflineSoapSession(String authenticatedId) {
        super(authenticatedId);
    }

    @Override
    protected void handleRemoteNotifications(Server server, Element context, boolean ignoreRefresh, boolean isPing) {
        if (context == null)
            return;

        boolean refreshExpected = true;

        // remember the remote session ID for the server
        Element eSession = context.getOptionalElement(HeaderConstants.E_SESSION);
        boolean isSoap = eSession != null && eSession.getAttribute(HeaderConstants.A_TYPE, null) == null;
        String sessionId = eSession == null ? null : eSession.getAttribute(HeaderConstants.A_ID, null);
        if (isSoap && sessionId != null && !sessionId.equals(""))
            refreshExpected = registerRemoteSessionId(server, sessionId);

        // remote refresh should cause overall refresh
        if (!ignoreRefresh && !refreshExpected && context.getOptionalElement(ZimbraNamespace.E_REFRESH) != null)
            mForceRefresh = getCurrentNotificationSequence();

        Element eNotify = context.getOptionalElement(ZimbraNamespace.E_NOTIFY);
        if (eNotify != null) {
            RemoteNotifications rns = new RemoteNotifications(eNotify);
            synchronized (mSentChanges) {
                if (!skipNotifications(rns.getNotificationCount(), !isPing)) {
                    removeUnqualifiedRemoteNotifications(rns);
                    mChanges.addNotification(rns);
                }
            }
        }
    }
    
    private void removeUnqualifiedRemoteNotifications(RemoteNotifications rns) {
        if (rns == null || rns.count == 0) {
            return;
        }
        removeUnqualifiedRemoteNotifications(rns.created);
        removeUnqualifiedRemoteNotifications(rns.modified);
    }

    private void removeUnqualifiedRemoteNotifications(List<Element> notifs) {
        if (notifs == null) {
            return;
        }
        //we don't want to add acct id, just want to see if formatting *requires* id to be added
        ItemIdFormatter ifmt = new ItemIdFormatter("any","any", false); 
        Iterator<Element> it = notifs.iterator();
        while (it.hasNext()) {
            Element elt = it.next();
            String itemIdStr = null;
            try {
                itemIdStr = elt.getAttribute(A_ID);
            } catch (ServiceException se) {
                continue;
            }
            ItemId item = null;
            try {
                item = new ItemId(itemIdStr,ifmt.getAuthenticatedId());
            } catch (ServiceException e) {
                continue;
            }
            if (item != null && !item.toString().equals(itemIdStr)) {
                it.remove();
            }
        }
    }

}
