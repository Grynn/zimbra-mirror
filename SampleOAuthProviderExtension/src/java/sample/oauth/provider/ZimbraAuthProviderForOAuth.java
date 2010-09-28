/*
 * Copyright 2009 Yutaka Obuchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//The original is modified for handling OAuth token in Zimbra

// Original's copyright and license terms
/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package sample.oauth.provider;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.AuthTokenException;
import com.zimbra.cs.account.ZimbraAuthToken;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.mailbox.MetadataList;
import com.zimbra.cs.service.AuthProvider;
import com.zimbra.cs.service.AuthProviderException;
import com.zimbra.soap.SoapServlet;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.server.OAuthServlet;
import sample.oauth.provider.core.SampleZmOAuthProvider;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

public class ZimbraAuthProviderForOAuth extends AuthProvider{

    ZimbraAuthProviderForOAuth() {
        super("oauth");
    }
    
    // AP-TODO-6: dup in ZAuthToken, move to common?
    //public static String cookieName(boolean isAdminReq) {
    //    return isAdminReq? ZimbraServlet.COOKIE_ZM_ADMIN_AUTH_TOKEN : ZimbraServlet.COOKIE_ZM_AUTH_TOKEN;
    //}
    
    protected AuthToken authToken(HttpServletRequest req, boolean isAdminReq) throws AuthProviderException, AuthTokenException {
        
    	ZimbraLog.extensions.debug("authToken(HttpServletRequest req, boolean isAdminReq) is requested.");
    	if(isAdminReq){
    		ZimbraLog.extensions.debug("isAdminReq"+isAdminReq);
        	return null;
    	}
    	
    	//String cookieName = cookieName(isAdminReq);
        String encodedAuthToken = null;
        //javax.servlet.http.Cookie cookies[] =  req.getCookies();
        //if (cookies != null) {
        //    for (int i = 0; i < cookies.length; i++) {
        //        if (cookies[i].getName().equals(cookieName)) {
        //            encodedAuthToken = cookies[i].getValue();
        //            break;
        //        }
        //    }
        //}
        OAuthMessage oAuthMessage = OAuthServlet.getMessage(req, null);

        try {
            if(oAuthMessage.getToken() == null){
                ZimbraLog.extensions.debug("no need for further oauth procesing");
                throw new AuthTokenException("Request does not contain an OAuth token");
            }
        } catch (IOException e) {
            throw new AuthTokenException("Error in getting OAuth token from request", e);
        }

        OAuthAccessor accessor = null;
        try {
            accessor = SampleZmOAuthProvider.getAccessor(oAuthMessage);
            SampleZmOAuthProvider.VALIDATOR.validateMessage(oAuthMessage, accessor);
        } catch (Exception e) {
            ZimbraLog.extensions.info("Error in validating OAuth token", e);
            throw AuthProviderException.FAILURE(e.getMessage());
        }

        // make sure token is authorized
        if (!Boolean.TRUE.equals(accessor.getProperty("authorized"))) {
            throw AuthProviderException.FAILURE("permission_denied");
        }
        
        checkConsumerKeyinMbox(accessor);

        encodedAuthToken = (String) accessor.getProperty("ZM_AUTH_TOKEN");

        ZimbraLog.extensions.debug("[oauth_token]"+accessor.accessToken+",[ZM_AUTH_TOKEN]"+encodedAuthToken);

        AuthToken authToken = genAuthToken(encodedAuthToken);
        if (authToken.isExpired()) {
            // renew the auth token
            try {
                authToken = AuthProvider.getAuthToken(authToken.getAccount());
            } catch (ServiceException e) {
                ZimbraLog.extensions.warn("Error in generating auth token", e);
                throw AuthProviderException.FAILURE(e.getMessage());
            }
            accessor.setProperty("ZM_AUTH_TOKEN", authToken.getEncoded());
        }

        return authToken;
    }

    private static void checkConsumerKeyinMbox(OAuthAccessor accessor) throws AuthTokenException, AuthProviderException {
        AuthToken userAuthToken = ZimbraAuthToken.getAuthToken((String) accessor.getProperty("ZM_AUTH_TOKEN"));
        try {
            Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(userAuthToken.getAccountId());
            Metadata oAuthConfig = mbox.getConfig(null, "zwc:oauth");
            if (oAuthConfig != null) {
                MetadataList authzedConsumers = oAuthConfig.getList("authorized_consumers", true);
                if (authzedConsumers != null && authzedConsumers.asList().contains(accessor.consumer.consumerKey)) {
                    return;
                }
            }
        } catch (ServiceException e) {
            ZimbraLog.extensions.warn("Error in reading mailbox metadata", e);
            throw AuthProviderException.FAILURE(e.getMessage());
        }
        throw AuthProviderException.FAILURE("permission_denied");
    }

    protected AuthToken authToken(Element soapCtxt, Map engineCtxt) throws AuthProviderException, AuthTokenException  {
    	HttpServletRequest hsr = (HttpServletRequest)engineCtxt.get(SoapServlet.SERVLET_REQUEST);
    	
    	//String encodedAuthToken = (soapCtxt == null ? null : soapCtxt.getAttribute(HeaderConstants.E_AUTH_TOKEN, null));
        
        // check for auth token in engine context if not in header  
        //if (encodedAuthToken == null)
        //    encodedAuthToken = (String) engineCtxt.get(SoapServlet.ZIMBRA_AUTH_TOKEN);
        
        //return genAuthToken(encodedAuthToken);
    	return authToken(hsr,false);
    }
    
    protected AuthToken authToken(String encoded) throws AuthProviderException, AuthTokenException {
        return genAuthToken(encoded);
    }
    
    private AuthToken genAuthToken(String encodedAuthToken) throws AuthProviderException, AuthTokenException {
        if (StringUtil.isNullOrEmpty(encodedAuthToken))
            throw AuthProviderException.NO_AUTH_DATA();
        
        return ZimbraAuthToken.getAuthToken(encodedAuthToken);
    }
    
    //protected AuthToken authToken(Account acct) {
    //    return new ZimbraAuthToken(acct);
    //}
    
    //protected AuthToken authToken(Account acct, boolean isAdmin) {
    //    return new ZimbraAuthToken(acct, isAdmin);
    //}
    
    //protected AuthToken authToken(Account acct, long expires) {
    //    return new ZimbraAuthToken(acct, expires);
    //}
    
    //protected AuthToken authToken(Account acct, long expires, boolean isAdmin, Account adminAcct) {
    //    return new ZimbraAuthToken(acct, expires, isAdmin, adminAcct);
    //}
    
}
