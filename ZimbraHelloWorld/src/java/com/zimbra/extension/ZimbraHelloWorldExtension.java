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

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.extension.ExtensionException;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.soap.SoapServlet;
/**
 * The main entry point for extensions
 * @author gsolovyev
 *
 */
public class ZimbraHelloWorldExtension implements ZimbraExtension {
	public static String ZAS_EXTENSION_NAME = "com_zimbra_appointment_summary";
	public static final String APPOINTMENT_SUMMARY_TASK_NAME = "SendAppointmentSummary";
	public static final String E_helloWorld = "HelloWorld";
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return ZAS_EXTENSION_NAME;
	}

	@Override
	public void init() throws ExtensionException, ServiceException {
		SoapServlet.addService("SoapServlet", new ZimbraHelloWorldService());
	}

}
