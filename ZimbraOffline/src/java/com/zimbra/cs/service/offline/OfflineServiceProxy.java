/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008 Zimbra, Inc.
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
import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineServiceProxy extends DocumentHandler {

    private String mOp;
    private boolean mQuiet;
    
    public OfflineServiceProxy(String op, boolean quiet) {
        mOp = op;
        mQuiet = quiet;
    }
    
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext ctxt = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(ctxt);
        if (!(mbox instanceof OfflineMailbox))
            throw OfflineServiceException.MISCONFIGURED("incorrect mailbox class: " + mbox.getClass().getSimpleName());
        
        Element parent = request.getParent();
        boolean fromBatch = parent != null && parent.getName().equals("BatchRequest");
        
        Element response = ((OfflineMailbox)mbox).proxyRequest(request, ctxt.getResponseProtocol(), mQuiet, mOp);
        if (fromBatch)
            response.detach();
        
        return response;
    }
    
    public static OfflineServiceProxy GetFreeBusy() {
        return new OfflineServiceProxy("get free/busy", false);
    }
    
    public static OfflineServiceProxy SearchCalendarResources() {
        return new OfflineServiceProxy("search cal resources", false);
    }
}



