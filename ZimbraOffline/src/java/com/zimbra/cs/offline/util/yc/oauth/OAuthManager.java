/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.offline.util.yc.oauth;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.util.yc.YContactException;
import com.zimbra.cs.util.Zimbra;

/**
 * every yahoo mailbox should have one corresponding OAuthCredential
 * 
 */
public class OAuthManager {

    private Map<String, OAuthCredential> mboxOAuthCredentials = new ConcurrentHashMap<String, OAuthCredential>();

    private OAuthManager() {
    }

    private static final class LazyHolder {
        private static final OAuthManager instance = new OAuthManager();
    }

    private OAuthCredential getMboxAuthCredential(String accountId) {
        if (!this.mboxOAuthCredentials.containsKey(accountId)) {
            OAuthCredential cred = new OAuthCredential(accountId);
            this.mboxOAuthCredentials.put(accountId, cred);
            return cred;
        }
        OAuthCredential credential = this.mboxOAuthCredentials.get(accountId);
        if (Zimbra.started() && credential.authToken.isNew()) {
            try {
                credential.load();
            } catch (YContactException e) {
                OfflineLog.yab.warn("load credential/token failed", e);
            }
        }
        return credential;
    }

    public static boolean hasOAuthToken(String accountId) throws YContactException {
        if (StringUtil.isNullOrEmpty(accountId)) {
            return false;
        }
        OAuthCredential oauth = LazyHolder.instance.getMboxAuthCredential(accountId);
        return (oauth != null && !oauth.authToken.isNew());
    }

    public static OAuthToken getOAuthToken(String accountId) throws YContactException {
        OAuthCredential oauth = LazyHolder.instance.getMboxAuthCredential(accountId);
        if (oauth.authToken.isExpired()) {
            return getRetryToken(accountId);
        }
        return oauth.authToken;
    }

    public static OAuthToken getRetryToken(String accountId) throws YContactException {
        OAuthCredential oauth = LazyHolder.instance.getMboxAuthCredential(accountId);
        oauth.load();
        oauth.refreshToken();
        oauth.persist();
        return oauth.authToken;
    }

    public static void persistCredential(String accountId, String token, String tokenSecret, String sessionHandle,
            String guid, String tokenTimestamp, String verifier) throws YContactException {
        if (StringUtil.isNullOrEmpty(verifier) && StringUtil.isNullOrEmpty(sessionHandle)) {
            return;
        }
        OAuthCredential oauth = LazyHolder.instance.mboxOAuthCredentials.get(accountId);
        if ((oauth != null) && (verifier != null && verifier.equals(oauth.verifier))) {
            // might be called by OfflineProvisioning modifyDataSource right after createDataSource
            return;
        }
        OAuthToken t = new OAuthToken(token, tokenSecret);
        t.setSessionHandle(sessionHandle);
        t.setGuid(guid);
        t.setLastAccessTime(Long.parseLong((tokenTimestamp)));
        oauth = new OAuthCredential(accountId, t, verifier);
        LazyHolder.instance.mboxOAuthCredentials.put(accountId, oauth);
        oauth.persist();
    }

    /**
     * first method to be called (during account setup), returns access url
     * 
     * @param accountId
     *            accountId
     * @return url for user to grant access
     * @throws YContactException
     */
    public static String[] getAccessTokenURL() throws YContactException {
        String tmpId = UUID.randomUUID().toString();
        String url = LazyHolder.instance.getMboxAuthCredential(tmpId).getAccessURL();
        return new String[] { url, tmpId };
    }

    /**
     * second method to be called, already have auth token at this point, together with user's verifier, ready to make api calls
     * 
     * @param accountId
     *            Account id
     * @param verifier
     *            user get it back by granting access using url we provided earlier
     * @throws YContactException
     */
    public static OAuthToken getTokenUsingVerifier(String accountId, String verifier) throws YContactException {
        assert LazyHolder.instance.mboxOAuthCredentials.get(accountId) != null;
        OAuthCredential oauth = LazyHolder.instance.getMboxAuthCredential(accountId);
        oauth.setVerifier(verifier);
        oauth.genNewToken();
        return oauth.authToken;
    }

    private static final class OAuthCredential {

        private static final String OFFLINE_YC_OAUTH = "offline_yahoo_contacts_sync";
        private OAuthToken authToken = OAuthToken.newToken();
        private String verifier = "-1";
        private String accountId;

        OAuthCredential(String accountId) {
            this.accountId = accountId;
        }

        OAuthCredential(String accountId, OAuthToken token, String verif) {
            this.accountId = accountId;
            this.authToken = token;
            this.verifier = verif;
        }

        private String getAccessURL() throws YContactException {
            OAuthRequest req = new OAuthGetRequestTokenRequest(new OAuthToken());
            String resp = req.send();
            OAuthResponse response = new OAuthGetRequestTokenResponse(resp);
            this.authToken = response.getToken();
            return this.authToken.getNextUrl();
        }

        private void genNewToken() throws YContactException {
            OAuthRequest req = new OAuthGetTokenRequest(this.authToken, this.verifier);
            String resp = req.send();
            OAuthResponse response = new OAuthGetTokenResponse(resp);
            this.authToken = response.getToken();
            this.authToken.setLastAccessTime(System.currentTimeMillis());
            OfflineLog.yab.debug("[OAuth] get new token {%s}", this.authToken);
        }

        private void refreshToken() throws YContactException {
            OAuthRequest req = new OAuthGetTokenRefreshRequest(this.authToken);
            OAuthResponse resp = new OAuthGetTokenResponse(req.send());
            this.authToken = resp.getToken();
            OfflineLog.yab.debug("[OAuth] refreshed token {%s}", this.authToken);
        }

        private void setVerifier(String verif) throws YContactException {
            this.verifier = verif;
        }

        // format is: verifier,token,tokenSecret,sessionHandler,timestamp
        private void persist() throws YContactException {
            try {
                Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(this.accountId);
                Metadata md = mbox.getConfig(null, OFFLINE_YC_OAUTH);
                if (md == null) {
                    md = new Metadata();
                }
                md.put(this.accountId, this.toString());
                mbox.setConfig(null, OFFLINE_YC_OAUTH, md);
            } catch (Exception e) {
                throw new YContactException("yahoo contact persist token error", "", false, e, null);
            }
        }

        private void load() throws YContactException {
            try {
                Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(this.accountId);
                Metadata md = mbox.getConfig(null, OFFLINE_YC_OAUTH);
                if (md == null && this.authToken != null && !this.authToken.isNew()) {
                    OfflineLog.yab.debug("account might be reseted, persists in memory token.");
                    persist();
                    return;
                }
                OfflineLog.yab.debug("loaded OAuth credential, [verifier:tokeen:token_secret:lastsynctime]: %s",
                        md.get(this.accountId));
                String[] tokens = md.get(this.accountId).split(",");
                this.verifier = tokens[0];
                this.authToken = new OAuthToken(tokens[1], tokens[2]);
                this.authToken.setSessionHandle(tokens[3]);
                this.authToken.setLastAccessTime(Long.parseLong(tokens[4]));
            } catch (Exception e) {
                throw new YContactException("yahoo contact persist token error", e.getMessage(), false, e, null);
            }
        }

        public String toString() {
            return this.verifier + "," + this.authToken;
        }
    }
}
