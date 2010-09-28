/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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

import javax.servlet.http.HttpServlet;

import com.zimbra.common.localconfig.LC;
import com.zimbra.cs.datasource.DataSourceManager;

public class ConfigServlet extends HttpServlet {

    private static final long serialVersionUID = 8124246834674440988L;

    private static final String LOCALHOST_URL_PREFIX = "http://127.0.0.1:";

    public static String LOCALHOST_SOAP_URL;
    public static String LOCALHOST_ADMIN_URL;

    @Override
    public void init() {
        String port = LC.zimbra_admin_service_port.value();

        //setting static variables
        LOCALHOST_SOAP_URL = LOCALHOST_URL_PREFIX + port + "/service/soap/";
        LOCALHOST_ADMIN_URL = LOCALHOST_URL_PREFIX + port + "/service/admin/soap/";

        try {
            DataSourceManager.init();
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }
}
