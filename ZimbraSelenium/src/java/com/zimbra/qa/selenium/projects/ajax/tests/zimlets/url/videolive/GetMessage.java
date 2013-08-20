/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.projects.ajax.tests.zimlets.url.videolive;

import java.io.File;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class GetMessage extends AjaxCommonTest {

	
	public GetMessage() {
		logger.info("New "+ GetMessage.class.getCanonicalName());


	}
	
	@Test(	description = "Receive a mail with embedded video",
			groups = { "functional" })
	public void GetMail_01() throws HarnessException {
		
		//-- Data
		
		// Inject the sample message
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email07/mime.txt";
		final String subject = "subject135232705018411";
		
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));


		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		
		//-- Verification
		
		// Verify the thumbnail appears
		
		String locator = "css=div[id^='YOUTUBE_'] img";
		boolean present = app.zPageMail.sIsElementPresent(locator);
		
		ZAssert.assertTrue(present, "Verify the youtube thumbnail is present");
		
		// Click on the image
		app.zPageMail.zClickAt(locator, "");
		app.zPageMail.zWaitForBusyOverlay();
		
		SleepUtil.sleep(5000);
		
		// Verify the iframe appears with the you tube player
		locator = "css=iframe[id^='youtube-iframe_']";
		present = app.zPageMail.sIsElementPresent(locator);

		ZAssert.assertTrue(present, "Verify the youtube iframe is present");

	}


}
