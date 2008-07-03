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
package com.zimbra.cs.offline;

import com.zimbra.common.util.Log;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.offline.util.yauth.RawAuth;
import com.zimbra.cs.offline.util.yauth.AuthenticationException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.mailbox.MetadataList;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class OfflineYAuth {
    private final Mailbox mbox;
    private final Map<String, String> tokens;

    private static final Log LOG = OfflineLog.offline;

    private static final String YAUTH_KEY = "YAUTH";
    private static final String VERSION_KEY = "VERSION";
    private static final String APPID_KEY = "APPID";
    private static final String TOKENS_KEY = "TOKENS";

    private static final long VERSION = 1;

    private static final String APP_ID = OfflineLC.zdesktop_yauth_appid.value();

    public static OfflineYAuth getInstance(Mailbox mbox) throws ServiceException {
        return new OfflineYAuth(mbox);
    }

    public static OfflineYAuth getInstance() throws ServiceException {
        return new OfflineYAuth(null);
    }
    
    private OfflineYAuth(Mailbox mbox) throws ServiceException {
        this.mbox = mbox;
        tokens = new HashMap<String, String>();
        if (mbox != null) {
            loadTokens();
        }
    }

    public RawAuth authenticate(String user, String pass)
        throws ServiceException {
        try {
            String token = getToken(user);
            if (token != null) {
                try {
                    return RawAuth.authenticate(APP_ID, token);
                } catch (AuthenticationException e) {
                    // Token possibly expired...
                }
            }
            return RawAuth.authenticate(APP_ID, newToken(user, pass));
        } catch (IOException e) {
            throw ServiceException.FAILURE(
                "I/O error while authenticating user " + user, e);
        } catch (AuthenticationException e) {
            throw ServiceException.FAILURE(
                "Authentication failed for user " + user, e);
        }
    }

    private String getToken(String user) {
        return tokens.get(user);
    }

    private String newToken(String user, String pass)
        throws ServiceException, AuthenticationException, IOException {
        String token = RawAuth.getToken(APP_ID, user, pass);
        tokens.put(user, token);
        if (mbox != null) {
            saveTokens();
        }
        return token;
    }

    private void loadTokens() throws ServiceException {
        Metadata md = mbox.getConfig(new Mailbox.OperationContext(mbox), YAUTH_KEY);
        if (md != null) {
            long version = md.getLong(VERSION_KEY, 0);
            String appId = md.get(APPID_KEY);
            if (version == VERSION && APP_ID.equals(appId)) {
                MetadataList ml = md.getList(TOKENS_KEY);
                if (ml != null) {
                    loadTokens(ml);
                }
            }
        }
    }

    private void loadTokens(MetadataList ml) throws ServiceException {
        LOG.debug("Loading yauth tokens for account '%s'", mbox.getAccountId());
        int size = ml.size();
        for (int i = 0; i < size; i++) {
            String[] parts = ml.get(i).split(" ");
            if (parts.length == 2) {
                tokens.put(parts[0], parts[1]);
            }
        }
    }

    private void saveTokens() throws ServiceException {
        LOG.debug("Saving yauth tokens for account '%s'", mbox.getAccountId());
        Metadata md = new Metadata();
        md.put(VERSION_KEY, VERSION);
        md.put(APPID_KEY, APP_ID);
        md.put(TOKENS_KEY, saveTokens(tokens));
        mbox.setConfig(new Mailbox.OperationContext(mbox), YAUTH_KEY, md);
    }

    private MetadataList saveTokens(Map<String, String> tokens) {
        MetadataList ml = new MetadataList();
        for (Map.Entry<String, String> e : tokens.entrySet()) {
            ml.add(e.getKey() + " " + e.getValue());
        }
        return ml;
    }
}

