/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.service.offline;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.util.ZimbraApplication;
import com.zimbra.soap.DocumentHandler;


public class OfflineGetExtensions extends DocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        List<String> extensionNames = ZimbraApplication.getInstance().getExtensionNames();
        Element response = getZimbraSoapContext(context).createElement(OfflineConstants.GET_EXTENSIONS_RESPONSE);
        if (extensionNames != null) {
            for (String ext : extensionNames)
                response.addElement(OfflineConstants.EXTENSION).addAttribute(OfflineConstants.EXTENSION_NAME, ext);
        }
        return response;
    }

    @Override
    public boolean needsAuth(Map<String, Object> context) {
        return false;
    }

    @Override
    public boolean needsAdminAuth(Map<String, Object> context) {
        return false;
    }
}
