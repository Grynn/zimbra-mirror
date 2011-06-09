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
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.util.yc.YContactException;

/**
 * every yahoo mailbox should have one corresponding OAuthCredential
 * 
 */
public class OAuthManager {

    private Map<String, OAuthCredential> mboxOAuthCredentials = new ConcurrentHashMap<String, OAuthCredential>();

    private OAuthManager() {
    }

    private static final class LazyHolder {
        static OAuthManager instance = new OAuthManager();
    }

    private synchronized OAuthCredential getMboxAuthCredential(String accountId) {
        if (!this.mboxOAuthCredentials.containsKey(accountId)) {
            OAuthCredential cred = new OAuthCredential(accountId);
            this.mboxOAuthCredentials.put(accountId, cred);
        }
        return this.mboxOAuthCredentials.get(accountId);
    }

    public static OAuthToken getOAuthToken(String accountId) throws YContactException {
        return LazyHolder.instance.getMboxAuthCredential(accountId).getAuthToken();
    }
    
    public static OAuthToken getRetryToken(String accountId) throws YContactException {
        OAuthCredential oauth = LazyHolder.instance.getMboxAuthCredential(accountId);
        oauth.load();
        oauth.refreshToken();
        return oauth.authToken;
    }

    /**
     * first method to be called (during account setup), returns access url
     * 
     * @param accountId accountId
     * @return url for user to grant access
     * @throws YContactException
     */
    public static String getAccessTokenUrl(String accountId) throws YContactException {
        return getOAuthToken(accountId).getNextUrl();
    }

    /**
     * second method to be called, already have auth token at this point, together with user's verifier, ready to make api calls
     * 
     * @param accountId Account id
     * @param verifier user get it back by granting access using url we provided earlier
     * @throws YContactException
     */
    public static void setVerifier(String accountId, String verifier) throws YContactException {
        assert LazyHolder.instance.mboxOAuthCredentials.size() > 0;
        try {
            OAuthCredential oauth = LazyHolder.instance.getMboxAuthCredential(accountId);
            oauth.setVerifier(verifier);
            oauth.persist();
        } catch (ServiceException e) {
            e.printStackTrace();
            throw new YContactException("set verifier error", "", false, e, null);
        }
    }

    private static final class OAuthCredential {

        private static final String OFFLINE_YC_OAUTH = "offline_yahoo_contacts_sync";
        private OAuthToken authToken = new OAuthToken("-1", "-1");
        private String verifier = "-1";
        private String accountId;
        private boolean isLoaded = false;

        OAuthCredential(String accountId) {
            this.accountId = accountId;
        }

        public OAuthToken getAuthToken() throws YContactException {
            try {
                if (!this.isLoaded) {
                    this.load();
                    this.isLoaded = true;
                }
                if (this.authToken.getTokenSecret().equals("-1")) {
                    getNewToken();
                    this.persist();
                } else if (this.authToken.isExpired()) {
                    refreshToken();
                    this.persist();
                }
            } catch (ServiceException e) {
                e.printStackTrace();
                throw new YContactException("persist verifier error", "", false, e, null);
            }
            return authToken;
        }

        /**
         * should be called once per mailbox per life time
         * 
         * @throws YContactException
         */
        private void getNewToken() throws YContactException {
            OAuthRequest req = new OAuthGetRequestTokenRequest(new OAuthToken());
            String resp = req.send();
            OAuthResponse response = new OAuthGetRequestTokenResponse(resp);
//            System.out.println("paste the highlighted codes below: " + response.getToken().getNextUrl());
//            System.out.print("Verifier: ");
//            Scanner scan = new Scanner(System.in);
//            this.setVerifier(scan.nextLine());
            req = new OAuthGetTokenRequest(response.getToken(), this.verifier);
            resp = req.send();
            response = new OAuthGetTokenResponse(resp);
            this.authToken = response.getToken();
            this.authToken.setLastAccessTime(System.currentTimeMillis());
        }

        private void refreshToken() throws YContactException {
            OAuthRequest req = new OAuthGetTokenRefreshRequest(this.authToken);
            OAuthResponse resp = new OAuthGetTokenResponse(req.send());
            this.authToken = resp.getToken();
        }

        private void setVerifier(String verif) throws YContactException {
            this.verifier = verif;
        }

        //format is: verifier,token,tokenSecret,sessionHandler,timestamp
        private void persist() throws YContactException {
            try {
                Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(this.accountId);
                Metadata md = mbox.getConfig(null, OFFLINE_YC_OAUTH);
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
                if (md == null) {
                    md = new Metadata();
                    md.put(this.accountId, this);
                    mbox.setConfig(null, OFFLINE_YC_OAUTH, md);
                }
                OfflineLog.yab.debug("[verifier:tokeen:token_secret:lastsynctime]: %s", md.get(this.accountId));
                String[] tokens = md.get(this.accountId).split(",");
                this.verifier = tokens[0];
                this.authToken = new OAuthToken(tokens[1], tokens[2]);
                this.authToken.setSessionHandle(tokens[3]);
                this.authToken.setLastAccessTime(Long.parseLong(tokens[4]));
            } catch (Exception e) {
                throw new YContactException("yahoo contact persist token error", "", false, e, null);
            }
        }

        public String toString() {
            return this.verifier + "," + this.authToken;
        }
    }
}
