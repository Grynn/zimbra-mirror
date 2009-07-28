/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.zclient.ZChangePasswordResult;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class ChangePasswordTag extends ZimbraSimpleTag {

    private String mUsername;
    private String mPassword;
    private String mNewPassword;
    private String mUrl = null;
    private boolean mSecure;
    private boolean mRememberMe;

    public void setUsername(String username) { this.mUsername = username; }

    public void setPassword(String password) { this.mPassword = password; }

    public void setNewpassword(String password) { this.mNewPassword = password; }

    public void setUrl(String url) { this.mUrl = url; }

    public void setSecure(boolean secure) { this.mSecure = secure; }

    public void setRememberme(boolean rememberMe) { this.mRememberMe = rememberMe; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            PageContext pageContext = (PageContext) jctxt;
            ZMailbox.Options options = new ZMailbox.Options();
            options.setAccount(mUsername);
            options.setPassword(mPassword);
            options.setNewPassword(mNewPassword);
            options.setUri(mUrl == null ? ZJspSession.getSoapURL(pageContext): mUrl);
            ZChangePasswordResult cpr = ZMailbox.changePassword(options);

            LoginTag.setCookie((HttpServletResponse)pageContext.getResponse(),
                    cpr.getAuthToken(),
                    mSecure,
                    mRememberMe,
                    cpr.getExpires());
 
        } catch (ServiceException e) {

            throw new JspTagException(e.getMessage(), e);
        }
    }
}
