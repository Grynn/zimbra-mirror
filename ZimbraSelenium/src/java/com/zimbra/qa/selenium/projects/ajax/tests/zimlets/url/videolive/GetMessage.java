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
