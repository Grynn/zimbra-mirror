/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineServiceProxy extends DocumentHandler {

    private String mOp;
    private boolean mQuiet;
    private boolean mHandleLocal;
    
    public OfflineServiceProxy(String op, boolean quiet, boolean handleLocal) {
        mOp = op;
        mQuiet = quiet;
        mHandleLocal = handleLocal;
    }
    
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext ctxt = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(ctxt);
        if (!(mbox instanceof ZcsMailbox)) {
            if (mHandleLocal)
                return getResponseElement(ctxt);
            else
                throw OfflineServiceException.MISCONFIGURED("incorrect mailbox class: " + mbox.getClass().getSimpleName());
        }
                
        Element response = ((ZcsMailbox)mbox).proxyRequest(request, ctxt.getResponseProtocol(), mQuiet, mOp);
        if (response != null)
            response.detach();
        
        if (mQuiet && response == null)
            return getResponseElement(ctxt);
        
        return response;
    }
    
    public static OfflineServiceProxy GetFreeBusy() {
        return new OfflineServiceProxy("get free/busy", false, true);
    }
    
    public static OfflineServiceProxy SearchCalendarResources() {
        return new OfflineServiceProxy("search cal resources", false, true);
    }
    
    public static OfflineServiceProxy GetPermission() {
        return new OfflineServiceProxy("get permission", true, true);
    }
    
    public static OfflineServiceProxy GrantPermission() {
        return new OfflineServiceProxy("grant permission", false, false);
    }
    
    public static OfflineServiceProxy RevokePermission() {
        return new OfflineServiceProxy("revoke permission", false, false);
    }
    
    public static OfflineServiceProxy CheckPermission() {
        return new OfflineServiceProxy("check permission", false, false);
    }
    
    public static OfflineServiceProxy GetShareInfoRequest() {
        return new OfflineServiceProxy("get share info", false, false);
    }
    
    public static OfflineServiceProxy AutoCompleteGalRequest() {
        return new OfflineServiceProxy("auto-complete gal", true, true);
    }
}



