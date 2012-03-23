/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2011 Zimbra, Inc.  All Rights Reserved.
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
