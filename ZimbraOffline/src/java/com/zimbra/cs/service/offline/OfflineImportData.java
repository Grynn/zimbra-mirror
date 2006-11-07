/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.OfflineMailboxManager;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.service.ServiceException;
import com.zimbra.cs.service.mail.ImportData;
import com.zimbra.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineImportData extends ImportData {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        MailboxManager mmgr = MailboxManager.getInstance();
        if (!(mmgr instanceof OfflineMailboxManager))
            return super.handle(request, context);

        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(zsc);
        if (!(mbox instanceof OfflineMailbox))
            throw OfflineServiceException.MISCONFIGURED("incorrect mailbox class: " + mbox.getClass().getSimpleName());

        // before doing anything, make sure all data sources are pushed to the server
        ((OfflineMailboxManager) mmgr).sync();
        // proxy this operation to the remote server
        Element response = ((OfflineMailbox) mbox).sendRequest(request);
        // and get a head start on the sync of the newly-pulled-in messages
        ((OfflineMailboxManager) mmgr).sync();

        return response;
    }
}
