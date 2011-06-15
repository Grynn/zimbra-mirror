/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.offline.jsp;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.offline.util.yc.YContactException;
import com.zimbra.cs.offline.util.yc.oauth.OAuthManager;
import com.zimbra.cs.offline.util.yc.oauth.OAuthToken;

public class YmailBean extends ImapBean {

    public static final String Domain = "yahoo.com";

    public YmailBean() {
    }

    @Override
    protected void doRequest() {
        domain = Domain;
        if (verb != null && (verb.isAdd() || verb.isModify())) {
            if (!isEmpty(email)) {
                if (email.indexOf('@') < 0)
                    email += '@' + domain;

                int atSign = email.indexOf("@yahoo.");

                if (atSign > 0) // username of yahoo.* email is without @domain
                    username = email.substring(0, atSign);
                else
                    username = email;
            }
        }
        host = email.endsWith("@yahoo.co.jp") ? "zimbra.imap.mail.yahoo.co.jp" : "zimbra.imap.mail.yahoo.com";
        connectionType = DataSource.ConnectionType.ssl;
        port = "993";
        if (verb == null) {
            try {
                String[] strs = OAuthManager.getAccessTokenURL();
                this.oauthURL = strs[0];
                this.oauthTmpId = strs[1];
            } catch (YContactException e) {
                setYContactVerifyError("YContactVerifyErr");
            }
        } else if (this.contactSyncEnabled && (verb.isAdd() || verb.isModify())) {
            try {
                if (accountId == null && !OAuthManager.hasOAuthToken(accountId)) {
                    OAuthToken token = OAuthManager.getTokenUsingVerifier(this.oauthTmpId, this.oauthVerifier);
                    this.ycontactToken = token.getToken();
                    this.ycontactTokenSecret = token.getTokenSecret();
                    this.ycontactSessionHandle = token.getSessionHandle();
                    this.ycontactTokenTimestamp = token.getLastAccessTime();
                    this.ycontactGuid = token.getGuid();
                    this.ycontactVerfier = this.oauthVerifier;    
                }
            } catch (ServiceException e) {
                if (e.getCode().equals(OfflineServiceException.YCONTACT_NEED_VERIFY)) {
                    setYContactVerifyError("YContactVerifyErr");
                }
            }
        }
        super.doRequest();
    }

    public boolean isCalendarSyncSupported() {
        return true;
    }

    public boolean isContactSyncSupported() {
        return true;
    }

    public boolean isServerConfigSupported() {
        return false;
    }

    public boolean isSmtpConfigSupported() {
        return false;
    }

    public boolean isUsernameRequired() {
        return false;
    }
}
