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

package com.zimbra.cs.offline;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.zclient.ZMailbox;


public class OfflineServlet extends HttpServlet {

    private static final String LOCALHOST_URL_PREFIX = "http://127.0.0.1:";

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
            ZimbraLog.addContextFilters(OfflineLC.zdesktop_log_context_filter.value());
            String port = LC.zimbra_admin_service_port.value();

            //setting static variables
            LOCALHOST_SOAP_URL = LOCALHOST_URL_PREFIX + port + AccountConstants.USER_SERVICE_URI;
            LOCALHOST_ADMIN_URL = LOCALHOST_URL_PREFIX + port + AdminConstants.ADMIN_SERVICE_URI;
            LOCALHOST_MAIL_URL = LOCALHOST_URL_PREFIX + port + "/zimbra/mail";

            DataSourceManager.init();
            OfflineProvisioning.getOfflineInstance().getLocalAccount();
            OfflineSyncManager.getInstance().init();
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }
}
