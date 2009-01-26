/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.OfflineMailboxManager;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;
import com.zimbra.cs.service.mail.FolderAction;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineFolderAction extends FolderAction {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException, SoapFaultException {
        MailboxManager mmgr = MailboxManager.getInstance();
        if (!(mmgr instanceof OfflineMailboxManager))
            return super.handle(request, context);
        
        Element action = request.getElement(MailConstants.E_ACTION);
        String operation = action.getAttribute(MailConstants.A_OPERATION).toLowerCase();           
        if (!operation.equals(OP_REFRESH) && !operation.equals(OP_IMPORT) && !operation.equals(OP_GRANT) && !operation.equals(OP_REVOKE))
            return super.handle(request, context);        
        
        String folderId = action.getAttribute(MailConstants.A_ID);
        int pos = folderId.indexOf(':');
        if (pos >= 0)
            folderId = folderId.substring(pos + 1);
        int fid = Integer.parseInt(folderId);
        
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(zsc);
        OperationContext octxt = getOperationContext(zsc, context);
        Folder folder = mbox.getFolderById(octxt, fid);
        if (operation.equals(OP_REFRESH) && !folder.getUrl().equals("")) //e.g. load rss feed
            return super.handle(request, context);

        // Operations below only apply to OfflineMailbox
        if (!(mbox instanceof OfflineMailbox))
            throw OfflineServiceException.MISCONFIGURED("incorrect mailbox class: " + mbox.getClass().getSimpleName());
        OfflineMailbox ombx = (OfflineMailbox) mbox;
        boolean traceOn = ombx.getOfflineAccount().isDebugTraceEnabled();
        
        Element response;
        if (operation.equals(OP_GRANT) || operation.equals(OP_REVOKE)) {
            Element parent = request.getParent();
            boolean fromBatch = parent != null && parent.getName().equals("BatchRequest");            
            response = ombx.proxyRequest(request, zsc.getResponseProtocol(), false, operation);
            if (fromBatch)
                response.detach();
            ombx.sync(true, traceOn);
        } else { 
            // before doing anything, make sure all data sources are pushed to the server
            ombx.sync(true, traceOn);
            // proxy this operation to the remote server
            response = ombx.proxyRequest(request, zsc.getResponseProtocol(), true, operation);
            // and get a head start on the sync of the newly-pulled-in messages
            ombx.sync(true, traceOn);
        }
        return response;
    }
}
