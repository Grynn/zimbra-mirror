/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.zclient.ZAuthResult;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Arrays;

public class LoginTag extends ZimbraSimpleTag {
    
    private String mUsername;
    private String mPassword;
    private String mNewPassword;
    private String mAuthToken;
    private boolean mAuthTokenInUrl;
    private boolean mRememberMe;
    private String mUrl = null;
    private String mPath = null;
    private String mVarRedirectUrl = null;
    private String mVarAuthResult = null;
    private String mAttrs;
    private String mPrefs;
    private boolean mIsOffline;
    
    public void setVarRedirectUrl(String varRedirectUrl) { this.mVarRedirectUrl = varRedirectUrl; }

    public void setVarAuthResult(String varAuthResult) { this.mVarAuthResult = varAuthResult; }

    public void setUsername(String username) { this.mUsername = username; }

    public void setPassword(String password) { this.mPassword = password; }

    public void setNewpassword(String password) { this.mNewPassword = password; }
    
    public void setRememberme(boolean rememberMe) { this.mRememberMe = rememberMe; }

    public void setAuthtoken(String authToken) { this.mAuthToken = authToken; }

    public void setAuthtokenInUrl(boolean authTokenInUrl) { this.mAuthTokenInUrl = authTokenInUrl; }
    
    public void setUrl(String url) { this.mUrl = url; }

    public void setPrefs(String prefs) { this.mPrefs = prefs; }

    public void setAttrs(String attrs) { this.mAttrs = attrs; }

    public void setIsOffline(boolean isOffline) { this.mIsOffline = isOffline; }
    
    private String getVirtualHost(HttpServletRequest request) {
        return request.getServerName();
        /*
        String virtualHost = request.getHeader("Host");
        if (virtualHost != null) {
            int i = virtualHost.indexOf(':');
            if (i != -1) virtualHost = virtualHost.substring(0, i);
        }
        return virtualHost;
        */
    }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            PageContext pageContext = (PageContext) jctxt;
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

            String serverName = request.getServerName();

            ZMailbox.Options options = new ZMailbox.Options();

            options.setNoSession(true);
            
            if (mPrefs != null && mPrefs.length() >0)
                options.setPrefs(Arrays.asList(mPrefs.split(",")));

            if (mAttrs != null && mAttrs.length() > 0)
                options.setAttrs(Arrays.asList(mAttrs.split(",")));

            if (mAuthToken != null) {
                options.setAuthToken(mAuthToken);
                options.setAuthAuthToken(true);
            } else {

                if(!mIsOffline) {
                    if (mUsername != null && mUsername.contains("@zimbra.com")) {
                        mUrl = "https://dogfood.zimbra.com/service/soap";
                    } else if (mUsername != null && mUsername.contains("@roadshow.zimbra.com")) {
                        mUrl = "http://roadshow.zimbra.com/service/soap";
                    }
                }
                
                options.setAccount(mUsername);
                options.setPassword(mPassword);
                options.setVirtualHost(getVirtualHost(request));
                if (mNewPassword != null && mNewPassword.length() > 0)
                    options.setNewPassword(mNewPassword);
            }
            options.setUri(mUrl == null ? ZJspSession.getSoapURL(pageContext): mUrl);

            ZMailbox mbox = ZMailbox.getMailbox(options);
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();

            String refer = mbox.getAuthResult().getRefer();
            boolean needRefer = (refer != null && !refer.equalsIgnoreCase(serverName));

            if ((mAuthToken == null || mAuthTokenInUrl) && !needRefer) {
                Cookie authTokenCookie = new Cookie(ZJspSession.COOKIE_NAME, mbox.getAuthToken());
                if (mRememberMe) {
                    ZAuthResult authResult = mbox.getAuthResult();
                    long timeLeft = authResult.getExpires() - System.currentTimeMillis();
                    if (timeLeft > 0) authTokenCookie.setMaxAge((int) (timeLeft/1000));
                } else {
                    authTokenCookie.setMaxAge(-1);
                }
                authTokenCookie.setPath("/");
                response.addCookie(authTokenCookie);
            }

            //if (!needRefer)
            //    ZJspSession.setSession((PageContext)jctxt, mbox);

            if (mVarRedirectUrl != null)
                jctxt.setAttribute(mVarRedirectUrl,
                        ZJspSession.getPostLoginRedirectUrl(pageContext, mPath, mbox.getAuthResult(), mRememberMe, needRefer),  PageContext.REQUEST_SCOPE);

            if (mVarAuthResult != null)
                jctxt.setAttribute(mVarAuthResult, mbox.getAuthResult(), PageContext.REQUEST_SCOPE);

        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
