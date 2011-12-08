package com.zimbra.qa.selenium.projects.desktop.tests.mail.bugs;

import java.io.File;
import java.util.HashMap;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DisplayMail.Field;


public class Bug31535 extends AjaxCommonTest {


	
	@SuppressWarnings("serial")
	public Bug31535() {
		logger.info("New "+ Bug31535.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
			put("zimbraPrefGroupMailBy", "message");
			put("zimbraPrefMessageViewHtmlPreferred", "TRUE");
		}};


	}

	@Test(	description = "Verify bug 34401",
			groups = { "functional" })
	public void Bug_34401() throws HarnessException {

		String subject = "subject13002239738283";

		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/private/mime/Bugs/Bug31535";
		LmtpInject.injectFile(ZimbraAccount.AccountZDC().EmailAddress, new File(MimeFolder));

		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		HtmlElement bodyElement = display.zGetMailPropertyAsHtml(Field.Body);
		
		HtmlElement.evaluate(bodyElement, "//body//img", "class", "InlineImage", 1);


	}



}
