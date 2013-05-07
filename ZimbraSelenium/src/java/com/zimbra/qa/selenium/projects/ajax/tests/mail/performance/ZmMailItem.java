/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.mail.performance;

import java.io.File;
import java.util.HashMap;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.performance.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.LmtpInject;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.*;


public class ZmMailItem extends AjaxCommonTest {

	
	@SuppressWarnings("serial")
	public ZmMailItem() throws HarnessException {
		logger.info("New "+ ZmMailItem.class.getCanonicalName());

		super.startingPage = app.zPageMail;


		super.startingAccountPreferences = new HashMap<String, String>() {{
			put("zimbraPrefGroupMailBy", "message");
			put("zimbraPrefMessageViewHtmlPreferred", "FALSE");
		}};


	}
	
	
	@Test(	description = "Measure the performance for preview pane, text message, initial load",
			groups = { "performance" })
	public void ZmMailItem_01() throws HarnessException {
		
		String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email02/mime01.txt";
		String subject = "Subject13155016716713";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailItem, "Load preview pane, text message, initial load");

		// Select the message so that it shows in the reading pane
	//	css=ul[id='zl__TV-main__rows'] li[id^='zli__TV-main__'] [id$='__su']:contains('23')
		//app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		app.zPageMail.zClickAt("css=ul[id='zl__TV-main__rows'] li[id^='zli__TV-main__']  div span[id$='__su']:contains('"+subject+"')","");

		PerfMetrics.waitTimestamp(token);

	}

	@Test(	description = "Measure the performance for preview pane, text message, 1 message",
			groups = { "performance" })
	public void ZmMailItem_02() throws HarnessException {
		
		String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email02/mime01.txt";
		String subject = "Subject13155016716713";

		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));


		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailItem, "Load preview pane, text message, 1 message");

		// Select the message so that it shows in the reading pane
		//app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		app.zPageMail.zClickAt("css=ul[id='zl__TV-main__rows'] li[id^='zli__TV-main__']  div span[id$='__su']:contains('"+subject+"')","");

		PerfMetrics.waitTimestamp(token);

	}
	
	@Test(	description = "Measure the performance for preview pane,  message with 1 attachment",
			groups = { "performance" })
	public void ZmMailItem_03() throws HarnessException {
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email05/mime01.txt";
		final String subject = "subject151615738";
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailItem, "Load preview pane, text message, 1 attachment");

		// Select the message so that it shows in the reading pane
	//	app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		app.zPageMail.zClickAt("css=ul[id='zl__TV-main__rows'] li[id^='zli__TV-main__']  div span[id$='__su']:contains('"+subject+"')","");

		PerfMetrics.waitTimestamp(token);
	}
	
	
	@Test(	description = "Measure the performance for preview pane,  message with 3 attachment",
			groups = { "performance" })
	public void ZmMailItem_04() throws HarnessException {
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email05/mime02.txt";
		final String subject = "subject151111738";
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailItem, "Load preview pane, text message, 3 attachments");

		// Select the message so that it shows in the reading pane
		//app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		app.zPageMail.zClickAt("css=ul[id='zl__TV-main__rows'] li[id^='zli__TV-main__']  div span[id$='__su']:contains('"+subject+"')","");

		PerfMetrics.waitTimestamp(token);
	}

}
