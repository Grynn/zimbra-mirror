package com.zimbra.qa.selenium.projects.desktop.tests.mail.bugs;

import java.io.File;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DisplayMail.Field;


public class Bug13911 extends AjaxCommonTest {


	
	@SuppressWarnings("serial")
	public Bug13911() {
		logger.info("New "+ Bug13911.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
			put("zimbraPrefGroupMailBy", "message");
			put("zimbraPrefMessageViewHtmlPreferred", "TRUE");
		}};


	}

	@Test(	description = "Verify bug 13911",
			groups = { "functional" })
	public void Bug_13911() throws HarnessException {

		
		String subject = "subject13010064065623";
		String bodyBeforeImage = "K\u00e6re alle"; // Kære alle
		String bodyAfterImage = "Problemet best\u00E5r"; // Problemet består

		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/private/mime/Bugs/Bug13911";
		LmtpInject.injectFile(ZimbraAccount.AccountZDC().EmailAddress, new File(MimeFolder));

		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		String body = display.zGetMailProperty(Field.Body);
		
		ZAssert.assertStringContains(body, bodyBeforeImage, "Verify the text before the image");
		ZAssert.assertStringContains(body, bodyAfterImage, "Verify the text after the image");

	}



}
