/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.service.offline;

import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.service.ServiceException;

public class OfflineProxyHelper {
    public static void uploadAttachments(Element request, String acctId) throws ServiceException {
        Element eAttach = request.getElement(MailConstants.E_MSG).getOptionalElement(MailConstants.E_ATTACH);
        if (eAttach != null) {
            String aid = eAttach.getAttribute(MailConstants.A_ATTACHMENT_ID, null);
            if (aid == null)
                return;
            
            String[] ids = aid.split(",");
            String newAid = "";
            for (String id : ids) {
                 String newId = OfflineDocumentHandlers.uploadOfflineDocument(id, acctId);
                 if (newAid.length() > 0)
                     newAid += ",";
                 newAid += newId;
            }
            eAttach.addAttribute(MailConstants.A_ATTACHMENT_ID, newAid);
        }
    }
}
