package com.zimbra.qa.selenium.projects.desktop.tests.mail.bugs;

import java.io.File;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DisplayMail.Field;


public class Bug21415 extends AjaxCommonTest {


	
	@SuppressWarnings("serial")
	public Bug21415() {
		logger.info("New "+ Bug21415.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
			put("zimbraPrefGroupMailBy", "message");
			put("zimbraPrefMessageViewHtmlPreferred", "TRUE");
		}};


	}

	@Test(	description = "Verify bug 21415",
			groups = { "functional" })
	public void Test21415_01() throws HarnessException {

		String subject1 = "subject12998858731253";
		String beginningContent = "Uso Interno";
		String endingContent = "Esta mensagem";

		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/private/mime/Bugs/Bug21415";
		LmtpInject.injectFile(ZimbraAccount.AccountZDC().EmailAddress, new File(MimeFolder));

		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject1);
		
		// Get the body
		String body = display.zGetMailProperty(Field.Body);
		
		// Make sure both the beginning and ending text appear
		ZAssert.assertStringContains(body, beginningContent, "Verify the ending text appears");
		ZAssert.assertStringContains(body, endingContent, "Verify the ending text appears");
		

	}

	@Test(	description = "Verify bug 21415",
			groups = { "functional" })
	public void Test21415_02() throws HarnessException {

		String subject1 = "subject12998912514374";
		String beginningContent = "Change 77406";
		String endingContent = "SkinResources.java";

		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/private/mime/Bugs/Bug21415";
		LmtpInject.injectFile(ZimbraAccount.AccountZDC().EmailAddress, new File(MimeFolder));

		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject1);
		
		// Get the body
		String body = display.zGetMailProperty(Field.Body);
		
		// Make sure both the beginning and ending text appear
		ZAssert.assertStringContains(body, beginningContent, "Verify the ending text appears");
		ZAssert.assertStringContains(body, endingContent, "Verify the ending text appears");
		

	}



}
