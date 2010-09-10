/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.cs.mailbox.OfflineMailboxManager;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.service.mail.FolderAction;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.cs.session.PendingModifications.Change;

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
        
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(zsc);
        OperationContext octxt = getOperationContext(zsc, context);
        String zid = "";
        String folderId = action.getAttribute(MailConstants.A_ID);
        int pos = folderId.indexOf(':');
        if (pos > 0) {
            zid = folderId.substring(0, pos);
            folderId = folderId.substring(pos + 1);
        }
        int id = Integer.parseInt(folderId);
        Folder folder = mbox.getFolderById(octxt, id);
        
        if (!(mbox instanceof ZcsMailbox)) {
            // load rss feed locally for non-zimbra accounts        
            if ((operation.equals(OP_REFRESH) || operation.equals(OP_IMPORT)) && !folder.getUrl().equals(""))
                return super.handle(request, context);
            else
                throw OfflineServiceException.MISCONFIGURED("incorrect mailbox class: " + mbox.getClass().getSimpleName());
        }

        ZcsMailbox ombx = (ZcsMailbox) mbox;
        boolean traceOn = ombx.getOfflineAccount().isDebugTraceEnabled();
        boolean quietWhenOffline = !operation.equals(OP_GRANT) && !operation.equals(OP_REVOKE);
 
        boolean isNew = ombx.pushNewFolder(octxt, id);       
        if (operation.equals(OP_REFRESH) || operation.equals(OP_IMPORT)) {
            // before doing anything, make sure all data sources are pushed to the server
            ombx.sync(true, traceOn);
        }
        
        //even if folder is not new, it might have been renumbered by a background sync. getFolderById() checks renumbered so we are covered in either case
        folder = mbox.getFolderById(octxt, id);
        String renumFolderId = Integer.toString(folder.getId());
        if (!folderId.equals(renumFolderId)) {
            action.addAttribute(MailConstants.A_ID, zid.equals("") ? renumFolderId : zid + ":" + renumFolderId);
        }
        // proxy this operation to the remote server
        Element response = ombx.proxyRequest(request, zsc.getResponseProtocol(), quietWhenOffline, operation);
        if (response != null)
            response.detach();
        
        if (operation.equals(OP_REFRESH) || operation.equals(OP_IMPORT)) {
            // and get a head start on the sync of the newly-pulled-in messages
            ombx.sync(true, traceOn);
        }
        
        return response;
    }
}
