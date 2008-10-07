/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
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
package com.zimbra.cs.offline.util;

import com.zimbra.cs.util.yauth.RawAuthManager;
import com.zimbra.cs.util.yauth.MetadataTokenStore;
import com.zimbra.cs.util.yauth.Authenticator;
import com.zimbra.cs.util.yauth.TokenStore;
import com.zimbra.cs.util.yauth.RawAuth;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.common.service.ServiceException;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public final class OfflineYAuth {
    private static final String APPID = OfflineLC.zdesktop_yauth_appid.value();
    
    private static final Map<Integer, RawAuthManager> rams =
        new HashMap<Integer, RawAuthManager>();

    public static Authenticator newAuthenticator(DataSource ds)
        throws ServiceException {
        RawAuthManager ram = getRawAuthManager(ds.getMailbox());
        return ram.newAuthenticator(
            APPID, ds.getUsername(), ds.getDecryptedPassword());
    }

    public static RawAuth authenticate(DataSource ds)
        throws ServiceException {
        try {
            return newAuthenticator(ds).authenticate();
        } catch (IOException e) {
            throw ServiceException.FAILURE("Authentication failed", e);
        }
    }

    public static void removeToken(DataSource ds) throws ServiceException {
        TokenStore store = getRawAuthManager(ds.getMailbox()).getTokenStore();
        store.removeToken(APPID, ds.getUsername());
    }
    
    public static void newToken(DataSource ds, String password)
        throws ServiceException {
        TokenStore store = getRawAuthManager(ds.getMailbox()).getTokenStore();
        try {
            store.newToken(APPID, ds.getUsername(), password);
        } catch (IOException e) {
            throw ServiceException.FAILURE("Token request failed", e);
        }
    }
    
    public static RawAuthManager getRawAuthManager(Mailbox mb)
        throws ServiceException {
        synchronized (rams) {
            int id = mb.getId();
            RawAuthManager ram = rams.get(id);
            if (ram == null) {
                ram = new RawAuthManager(new MetadataTokenStore(mb));
                rams.put(id, ram);
            }
            return ram;
        }
    }

    public static void deleteRawAuthManager(Mailbox mb) {
        synchronized (rams) {
            rams.remove(mb.getId());
        }
    }
}
