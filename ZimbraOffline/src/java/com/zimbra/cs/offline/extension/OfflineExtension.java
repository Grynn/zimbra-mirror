/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.offline.extension;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.convert.OfflineConvertHandler;
import com.zimbra.cs.extension.ExtensionDispatcherServlet;
import com.zimbra.cs.extension.ExtensionException;
import com.zimbra.cs.extension.ZimbraExtension;

public class OfflineExtension implements ZimbraExtension {

    @Override
    public String getName() {
        return "offline-ext";
    }

    @Override
    public void init() throws ExtensionException, ServiceException {
        ExtensionDispatcherServlet.register(this, new OfflineConvertHandler());
    }

    @Override
    public void destroy() {
        ExtensionDispatcherServlet.unregister(this);
    }

}
