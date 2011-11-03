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

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.cs.service.FeedManager;
import com.zimbra.cs.service.mail.CreateFolder;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineCreateFolder extends CreateFolder {
    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(zsc);
        if (!(mbox instanceof ZcsMailbox))
            return super.handle(request, context);
            
        Element t = request.getElement(MailConstants.E_FOLDER);
        String url = t.getAttribute(MailConstants.A_URL, null);

        if (url != null && !url.equals("")) {
            FeedManager.retrieveRemoteDatasource(mbox.getAccount(), url, null);
            t.addAttribute(MailConstants.A_SYNC, false); // for zimbra accounts don't load rss on folder creation
        }
        return super.handle(request, context);
    }
}
