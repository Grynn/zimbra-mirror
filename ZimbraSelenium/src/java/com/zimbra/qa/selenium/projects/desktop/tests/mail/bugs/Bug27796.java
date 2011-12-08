package com.zimbra.qa.selenium.projects.desktop.tests.mail.bugs;

import java.io.File;
import java.util.HashMap;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DisplayMail.Field;


public class Bug27796 extends AjaxCommonTest {


	
	@SuppressWarnings("serial")
	public Bug27796() {
		logger.info("New "+ Bug27796.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
			put("zimbraPrefGroupMailBy", "message");
			put("zimbraPrefMessageViewHtmlPreferred", "TRUE");
		}};


	}

	@Test(	description = "Verify bug 25624",
			groups = { "functional" })
	public void Bug_27796() throws HarnessException {

		String subject = "subject13001430504374";

		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/private/mime/Bugs/Bug27796";
		LmtpInject.injectFile(ZimbraAccount.AccountZDC().EmailAddress, new File(MimeFolder));

		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		String body = display.zGetMailProperty(Field.Body);
		ZAssert.assertStringContains(body, "I realized that may be the best place to post this question", "Verify the message content is not blank");
		ZAssert.assertStringContains(body, "http://twiki.corp.yahoo.com/view/Devel/DevelRandom", "Verify the message content contains the footer");

	}



}
