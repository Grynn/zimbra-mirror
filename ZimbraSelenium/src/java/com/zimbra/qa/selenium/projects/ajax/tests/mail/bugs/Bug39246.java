package com.zimbra.qa.selenium.projects.ajax.tests.mail.bugs;

import java.io.File;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.Field;


public class Bug39246 extends PrefGroupMailByMessageTest {

	public Bug39246() {
		logger.info("New "+ Bug39246.class.getCanonicalName());
		
		
		
		
	}
	
	@Bugs( ids = "39246")
	@Test(	description = "Verify bug 39246",
			groups = { "functional" })
	public void Bug39246_01() throws HarnessException  {
					
			
		final String subject = "Bug39246";
		final String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/private/mime/viewEntireMessage_Bug39246.txt";
		
		
		// Inject the example message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));

		// Get Mail
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the message
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// In the preview pane, click "View Entire Message"
		display.zPressButton(Button.B_VIEW_ENTIRE_MESSAGE);

		// 7.X behavior opened a new window
		// 8.X behavior seems to open the entire message in the preview pane
		
//		String windowTitle = "Zimbra: "+ subject;
//
//		try {
//			
//			// Focus on the separate window
//			app.zPageMail.zSeparateWindowFocus(windowTitle);
//			
//			// TODO: add all other verification from bug 39246 test case
//			
//		} finally {
//			
//			app.zPageMail.zSeparateWindowClose(windowTitle);
//						
//		}			
			
		// Wait 30 seconds for the end of the message to display
		String body = "";
		for (int i = 0; i < 30; i++) {
			
			// Get the body
			body = display.zGetMailProperty(Field.Body);
			
			// If the body contains the last few words from the MIME message, assume the full body loaded
			logger.info("Waiting for 'CLICK ON THE TEST SCRIPT NAMES' to appear ...");
			if ( body.contains("CLICK ON THE TEST SCRIPT NAMES") )
				break;
			
			SleepUtil.sleep(1000);
		}

		ZAssert.assertStringContains(body, "CLICK ON THE TEST SCRIPT NAMES", "Verify the last words from the mime are displayed in the body");
		
	}

}
