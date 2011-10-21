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

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.continuation.ContinuationSupport;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.service.mail.NoOp;
import com.zimbra.soap.SoapServlet;

public class OfflineNoOp extends NoOp {

    private static final String TIME_KEY = "ZDNoOpStartTime";

	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        HttpServletRequest servletRequest = (HttpServletRequest) context.get(SoapServlet.SERVLET_REQUEST);
        boolean isResumed = !ContinuationSupport.getContinuation(servletRequest).isInitial();
        if (!isResumed) {
			OfflineSyncManager.getInstance().clientPing();
        }
        boolean wait = request.getAttributeBool(MailConstants.A_WAIT, false);
        long start = System.currentTimeMillis();
        if (!isResumed) {
            servletRequest.setAttribute(TIME_KEY, start);
        }
		Element response = super.handle(request, context);
		response.addAttribute(MailConstants.A_WAIT, wait);
		response.addAttribute(MailConstants.A_TIME, System.currentTimeMillis()-(Long) servletRequest.getAttribute(TIME_KEY));
		return response;
	}
}
