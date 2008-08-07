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

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.servlet.ZimbraServlet;
import com.zimbra.cs.wiki.WikiUtil;
import com.zimbra.cs.zclient.ZMailbox;


public class OfflineServlet extends HttpServlet {

    private static final String LOCALHOST_URL_PREFIX = "http://localhost:";
    
    private static String LOCALHOST_SOAP_URL;
    private static String LOCALHOST_ADMIN_URL;
    private static String LOCALHOST_MAIL_URL;
	
    private ZMailbox.Options getMailboxOptions(String username, String password) {
        ZMailbox.Options options = new ZMailbox.Options(username, Provisioning.AccountBy.name, password, LOCALHOST_SOAP_URL);
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
			int port = Integer.parseInt(getServletConfig().getInitParameter("port"));
			int adminPort = Integer.parseInt(getServletConfig().getInitParameter("adminPort"));
			
			//setting static variables
			LOCALHOST_SOAP_URL = LOCALHOST_URL_PREFIX + port + ZimbraServlet.USER_SERVICE_URI;
			LOCALHOST_ADMIN_URL = LOCALHOST_URL_PREFIX + adminPort + ZimbraServlet.ADMIN_SERVICE_URI;
			LOCALHOST_MAIL_URL = LOCALHOST_URL_PREFIX + port + "/zimbra/mail";
			
			OfflineDataSource.init();
			OfflineProvisioning.getOfflineInstance().getLocalAccount();
			OfflineSyncManager.getInstance().init();
			WikiUtil wu = WikiUtil.getInstance();
			wu.initDefaultWiki("local@host.local");
			String templatePath = LC.zimbra_home.value() + File.separator + "wiki" + File.separator + "Templates";
			wu.startImport("local@host.local", "Template", new File(templatePath));
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
    }
}