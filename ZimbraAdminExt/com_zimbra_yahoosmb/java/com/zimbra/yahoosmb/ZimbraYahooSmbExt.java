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

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: May 1, 2008
 * Time: 3:40:25 PM
 * To change this template use File | Settings | File Templates.
 */

package com.zimbra.yahoosmb;

import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.soap.SoapServlet;
import com.zimbra.cert.ZimbraCertMgrService;

public class ZimbraYahooSmbExt  implements ZimbraExtension {
    public static final String EXTENSION_NAME_YAHOOSMB = "com_zimbra_yahoosmb";

    public void destroy() {
    }

    public String getName() {
        return EXTENSION_NAME_YAHOOSMB ;
    }

    public void init() throws ServiceException {
        SoapServlet.addService("AdminServlet", new ZimbraYahooSmbService());
    }

}