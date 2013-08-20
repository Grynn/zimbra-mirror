/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2011, 2013 Zimbra Software, LLC.
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
package com.zimbra.bp;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminExtConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class PurgeIMAPImportTasks extends AdminDocumentHandler {

    public Element handle(Element request, Map<String, Object> context)
    throws ServiceException {
         
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Element response = zsc.createElement(AdminExtConstants.PURGE_BULK_IMAP_IMPORT_TASKS_RESPONSE);
        BulkIMAPImportTaskManager.purgeQueue(zsc.getAuthtokenAccountId());
        return response;
    }
}
