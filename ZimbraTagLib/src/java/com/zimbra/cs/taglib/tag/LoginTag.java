/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.zclient.ZAuthToken;
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
	private String mRequestedSkin;

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

    public void setRequestedSkin(String skin) { this.mRequestedSkin = skin; }

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

            options.setClientIp(pageContext.getRequest().getRemoteAddr());

            options.setNoSession(true);
            
            if (mPrefs != null && mPrefs.length() >0)
                options.setPrefs(Arrays.asList(mPrefs.split(",")));

            if (mAttrs != null && mAttrs.length() > 0)
                options.setAttrs(Arrays.asList(mAttrs.split(",")));

            if (mAuthToken != null) {
                options.setAuthToken(mAuthToken);
                options.setAuthAuthToken(true);
            } else {
                options.setAccount(mUsername);
                options.setPassword(mPassword);
                options.setVirtualHost(getVirtualHost(request));
                if (mNewPassword != null && mNewPassword.length() > 0)
                    options.setNewPassword(mNewPassword);
            }
            options.setUri(mUrl == null ? ZJspSession.getSoapURL(pageContext): mUrl);
			options.setRequestedSkin(mRequestedSkin);

			ZMailbox mbox = ZMailbox.getMailbox(options);
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();

            String refer = mbox.getAuthResult().getRefer();
            boolean needRefer = (refer != null && !refer.equalsIgnoreCase(serverName));

            if ((mAuthToken == null || mAuthTokenInUrl) && !needRefer) {
                // Cookie authTokenCookie = new Cookie(ZJspSession.COOKIE_NAME, mbox.getAuthToken().getValue());
                ZAuthToken zat = mbox.getAuthToken();
                Map<String, String> cookieMap = zat.toCookieMap(false);
                Integer maxAge = null;
                if (mRememberMe) {
                    ZAuthResult authResult = mbox.getAuthResult();
                    long timeLeft = authResult.getExpires() - System.currentTimeMillis();
                    if (timeLeft > 0) maxAge = new Integer((int)(timeLeft/1000));
                } else {
                    maxAge = new Integer(-1);
                }
                for (Map.Entry<String, String> ck : cookieMap.entrySet()) {
                    Cookie authTokenCookie = new Cookie(ck.getKey(), ck.getValue());
                    if (maxAge != null)
                        authTokenCookie.setMaxAge(maxAge.intValue());
                    authTokenCookie.setPath("/");
                    response.addCookie(authTokenCookie);
                }
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
