package com.zimbra.qa.selenium.projects.ajax.tests.mail.bugs;

import java.io.File;
import java.util.HashMap;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.LmtpInject;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.Field;


public class Bug16213 extends AjaxCommonTest {


	

	@SuppressWarnings("serial")
	public Bug16213() {
		logger.info("New "+ Bug16213.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
			put("zimbraPrefGroupMailBy", "message");
			put("zimbraPrefMessageViewHtmlPreferred", "TRUE");
		}};


	}
	
	@Test(	description = "Verify bug 16213 - Message display should show From=Unknown",
			groups = { "functional" })
	public void Bug_16213MV() throws HarnessException {

		String subject = "Encoding test";
		String from = "Unknown";

		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/private/mime/Bugs/Bug16213";
		LmtpInject.injectFile(ZimbraAccount.AccountZWC().EmailAddress, new File(MimeFolder));

		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		ZAssert.assertEquals(display.zGetMailProperty(Field.From), from, "Verify the default string for 'From' is 'Unknown'");

	}

}
