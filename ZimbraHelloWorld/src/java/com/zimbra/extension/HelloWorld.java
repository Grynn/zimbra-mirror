/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
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
package com.zimbra.extension;

import java.util.Map;

import com.zimbra.common.account.Key.AccountBy;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.ScheduledTask;
import com.zimbra.cs.mailbox.ScheduledTaskManager;
import com.zimbra.cs.service.account.AccountDocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class HelloWorld extends AccountDocumentHandler {
/**
 * Process the SOAP request (XML of the request in in the request argument). Return the response element.
 */
	@Override
	public Element handle(Element request, Map<String, Object> context)
			throws ServiceException {
		ZimbraSoapContext zsc = getZimbraSoapContext(context);
		Element response = zsc.createElement(ZimbraHelloWorldService.HELLO_WORLD_RESPONSE);
		response.addElement(ZimbraHelloWorldExtension.E_helloWorld);
		response.addAttribute(ZimbraHelloWorldExtension.E_helloWorld, "hellow");
		return response;
	}

}
