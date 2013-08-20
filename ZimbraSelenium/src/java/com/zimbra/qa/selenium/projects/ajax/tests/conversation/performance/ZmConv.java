/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.projects.ajax.tests.conversation.performance;

import java.io.File;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.performance.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.LmtpInject;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.*;


public class ZmConv extends AjaxCommonTest {

	
	@SuppressWarnings("serial")
	public ZmConv() throws HarnessException {
		logger.info("New "+ ZmConv.class.getCanonicalName());
		
		super.startingPage = app.zPageMail;

		
		super.startingAccountPreferences = new HashMap<String, String>() {{
					put("zimbraPrefGroupMailBy", "conversation");
				    put("zimbraPrefMessageViewHtmlPreferred", "FALSE");
				}};


	}
	
	
	@Test(	description = "Measure the performance for conversation view, preview pane, text message, initial load",
			groups = { "performance" })
	public void ZmMailItem_01() throws HarnessException {
		
		String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/conversation02";
		String subject = "Conversation13155016716714";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));


		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmConv, "Load preview pane, text message, initial load");

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		PerfMetrics.waitTimestamp(token);

	}


}
