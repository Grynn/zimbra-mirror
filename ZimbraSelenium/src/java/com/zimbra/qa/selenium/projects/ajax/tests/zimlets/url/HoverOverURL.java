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
package com.zimbra.qa.selenium.projects.ajax.tests.zimlets.url;

import java.io.*;
import java.util.*;

import org.openqa.selenium.*;
import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class HoverOverURL extends AjaxCommonTest {

	
	public HoverOverURL() {
		logger.info("New "+ HoverOverURL.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Basic settings
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -3888082425397157889L;
		{
		    put("zimbraPrefGroupMailBy", "message");
		}};



	}
	
	@Test(	description = "Hover over a URL",
			groups = { "functional" })
	public void HoverOverURL_01() throws HarnessException {

		
		//-- DATA
		
		
		
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String server = "server" + ZimbraSeleniumProperties.getUniqueString();
		String body = "http://www."+ server +".com";
		
		// Send the message from AccountA to the ZWC user
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ body +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		

		//-- GUI
		
		
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Wait for a bit so the zimlet can take affect
		SleepUtil.sleep(5000);
		
		// Hover over the URL
		String locator = "css=span[id$='_com_zimbra_url']:contains("+ server +")";
		app.zPageMail.sMouseOver(locator, (WebElement[]) null);

		//-- VERIFICATION
		
		// Verify the contact tool tip opens
		TooltipContact tooltip = new TooltipContact(app);
		tooltip.zWaitForActive();
		
		ZAssert.assertTrue(tooltip.zIsActive(), "Verify the tooltip shows");
	}
	
	
	@Test(	description = "Hover over a URL - verify tooltip content",
			groups = { "functional" })
	public void HoverOverURL_02() throws HarnessException {

		
		//-- DATA
		
		
		
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String server = "server" + ZimbraSeleniumProperties.getUniqueString();
		String body = "http://www."+ server +".com";
		
		// Send the message from AccountA to the ZWC user
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ body +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		

		//-- GUI
		
		
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Wait for a bit so the zimlet can take affect
		SleepUtil.sleep(5000);
		
		// Hover over the URL
		String locator = "css=span[id$='_com_zimbra_url']:contains("+ server +")";
		app.zPageMail.sMouseOver(locator, (WebElement[]) null);

		//-- VERIFICATION
		
		// Verify the contact tool tip opens
		TooltipContact tooltip = new TooltipContact(app);
		tooltip.zWaitForActive();

		// Get the tooltip contents
		String content = tooltip.zGetContents();
		
		ZAssert.assertStringContains(content, server, "Verify basic tooltip content");

	}



	
	@Bugs(ids = "82303")
	@Test(	description = "Hover over a URL with 'javascript' in the path.  Verify no change to JAVASCRIPT-BLOCKED",
			groups = { "functional" })
	public void HoverOverURL_03() throws HarnessException {

		
		//-- DATA
		
		
		
		// Create the message data to be sent
		String subject = "bug82303";
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Bugs/Bug82303/mime.txt";
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		

		//-- GUI
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Wait for a bit so the zimlet can take affect
		SleepUtil.sleep(5000);
		
		
		//-- VERIFICATION
		
		// Hover over each URL, verify no 'blocked'
		for (String link : "Link1,Link2,Link3".split(",") ) {
		
			// Link1: http://www.zimbra.com/foo/javascript/foo
			// Link2: http://www.zimbra.com/foo/Javascript/foo
			// Link3: http://www.zimbra.com/foo/JAVASCRIPT/foo

			
			// Hover over the URL
			String locator = "css=span[id$='_com_zimbra_url']:contains("+ link +")";
			app.zPageMail.sMouseOver(locator, (WebElement[]) null);
	
			//-- VERIFICATION
			
			// Verify the contact tool tip opens
			TooltipContact tooltip = new TooltipContact(app);
			tooltip.zWaitForActive();
	
			// Get the tooltip contents
			String content = tooltip.zGetContents();
			
			ZAssert.assertStringDoesNotContain(content.toLowerCase(), "blocked", "Verify 'javascript' not changed to 'javascript-BLOCKED'");

		}

	}

}
