/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
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
 * 
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.offline;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.zclient.ZAuthToken;
import com.zimbra.cs.servlet.ZimbraServlet;
import com.zimbra.cs.zclient.ZMailbox;


public class OfflineServlet extends HttpServlet {

    private final String LOCALHOST_URL = "http://localhost:7633";
    private final String LOCALHOST_ADMIN_URL = "http://localhost:7634" + ZimbraServlet.ADMIN_SERVICE_URI;
    private final String LOCALHOST_MAIL_URL = LOCALHOST_URL + "/zimbra/mail";
	
    private ZMailbox.Options getMailboxOptions(String username, String password) {
        ZMailbox.Options options = new ZMailbox.Options(username, Provisioning.AccountBy.name, password, LOCALHOST_URL + ZimbraServlet.USER_SERVICE_URI);
        options.setNoSession(false);
        return options;
    }

    private void setAuthCookie(String username, String password, HttpServletResponse response) throws ServiceException {
        String auth = ZMailbox.getMailbox(getMailboxOptions(username, password)).getAuthToken().getValue();
        Cookie cookie = new Cookie("ZM_AUTH_TOKEN", auth);
        cookie.setPath("/");
        cookie.setMaxAge(31536000);
        response.addCookie(cookie);

        Cookie zmapps = new Cookie("ZM_APPS", "mcaoinbtx");
        zmapps.setPath("/");
        zmapps.setMaxAge(31536000);
        response.addCookie(zmapps);
    }

    private void clearAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("ZM_AUTH_TOKEN", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
		    SoapProvisioning prov = new SoapProvisioning();
		    prov.soapSetURI(LOCALHOST_ADMIN_URL);
		    prov.soapZimbraAdminAuthenticate();
	
		    setAuthCookie("local_account@host.local", "test123", resp);
		    resp.sendRedirect(LOCALHOST_MAIL_URL);
		} catch (ServiceException x) {
			throw new ServletException(x);
		}
	}

	private static final long serialVersionUID = 901093939836074611L;

	@Override
	public void init() {
		try {
			OfflineDataSource.init();
			OfflineProvisioning.getOfflineInstance().getLocalAccount();
			OfflineSyncManager.getInstance().init();
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
    }
	
	
}