/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.clientuploader;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.extension.ExtensionDispatcherServlet;
import com.zimbra.cs.extension.ZimbraExtension;

public class ZClientUploaderExt implements ZimbraExtension {
    public static final String EXTENTION_NAME = "clientUploader";
    public void init() {
        /*
         * content handler
         */
        try {
            ExtensionDispatcherServlet.register(this, new ClientUploadHandler());
        } catch (ServiceException e) {
            Log.clientUploader.fatal("caught exception while registering ClientUploadHandler");
        }
    }

    public void destroy() {
        ExtensionDispatcherServlet.unregister(this);
    }

    public String getName() {
        return EXTENTION_NAME;
    }
}
