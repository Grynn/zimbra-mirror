package com.zimbra.qa.selenium.projects.ajax.tests.mail.mail;

import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogLaunchInSeparateWindow;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogLaunchInSeparateWindow.Field;


public class OpenSeparateWindow extends AjaxCommonTest {
	protected static Logger logger = LogManager.getLogger(OpenSeparateWindow.class);

	boolean injected = false;
	final String mimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email00";
	
	@SuppressWarnings("serial")
	public OpenSeparateWindow() throws HarnessException {
		logger.info("New "+ OpenSeparateWindow.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
				    put("zimbraPrefGroupMailBy", "message");
				}};


	}
	
	
	@Test(	description = "Open message in separate window",
			groups = { "smoke" })
	public void OpenSeparateWindow_01() throws HarnessException {
		
		final String subject = "subject12996131112962";
		final String from = "from13149322103433@testdomain.com";
		final String to = "to13149344503433@testdomain.com";
		final String content = "content13147814503433";


		// Add a message to the inbox
		app.zGetActiveAccount().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
        		+		"<m l='"+ FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox).getId() +"'>"
            	+			"<content>From: "+ from + "\n"
            	+				"To: "+ to +"\n"
            	+				"Subject: "+ subject +"\n"
            	+				"MIME-Version: 1.0 \n"
            	+				"Content-Type: text/plain; charset=utf-8 \n"
            	+				"Content-Transfer-Encoding: 7bit\n"
            	+				"\n"
            	+				content +"\n"
            	+			"</content>"
            	+		"</m>"
				+	"</AddMsgRequest>");

		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		DialogLaunchInSeparateWindow window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (DialogLaunchInSeparateWindow)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}
		
		
	}

	@Test(	description = "Open message in separate window - check the display",
			groups = { "smoke" })
	public void OpenSeparateWindow_02() throws HarnessException {
		
		final String subject = "subject1291234112962";
		final String from = "from13142352103433@testdomain.com";
		final String to = "to13141111503433@testdomain.com";
		final String content = "content13147988703433";


		// Add a message to the inbox
		app.zGetActiveAccount().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
        		+		"<m l='"+ FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox).getId() +"'>"
            	+			"<content>From: "+ from + "\n"
            	+				"To: "+ to +"\n"
            	+				"Subject: "+ subject +"\n"
            	+				"MIME-Version: 1.0 \n"
            	+				"Content-Type: text/plain; charset=utf-8 \n"
            	+				"Content-Transfer-Encoding: 7bit\n"
            	+				"\n"
            	+				content +"\n"
            	+			"</content>"
            	+		"</m>"
				+	"</AddMsgRequest>");

		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		DialogLaunchInSeparateWindow window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (DialogLaunchInSeparateWindow)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			ZAssert.assertEquals(window.zGetMailProperty(Field.Subject), subject, "Verify the 'Subject' matches");
			ZAssert.assertEquals(window.zGetMailProperty(Field.From), from, "Verify the 'From' matches");
			ZAssert.assertEquals(window.zGetMailProperty(Field.To), to, "Verify the 'To' matches");
			
		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}
		
		
	}


}
