package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;


public class CancelComposeHtml extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public CancelComposeHtml() {
		logger.info("New "+ CancelComposeHtml.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccount = new ZimbraAccount();
		super.startingAccount.provision();
		super.startingAccount.authenticate();
		super.startingAccount.modifyPreferences(
				new HashMap<String , String>() {{
				    put("zimbraPrefComposeFormat", "html");
				}});

		
	}
	
	@Test(	description = "Verify message dialog, when cancel a html draft (body filled)",
			groups = { "functional" })
	public void CancelComposeHtml_01() throws HarnessException {
		
		
		// Create the message data to be sent
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		

		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertTrue(mailform.zIsVisible(), "Verify the new form opened");
		
		
		
		// Fill out the form with the data
		mailform.zFillField(Field.Body, body);
		
		// Cancel the message
		// A warning dialog should appear regarding losing changes
		AbsDialog warning = (AbsDialog)mailform.zToolbarPressButton(Button.B_CANCEL);
		ZAssert.assertNotNull(warning, "Verify the dialog is returned");
		
		// Dismiss the dialog
		warning.zClickButton(Button.B_NO);
		warning.zWaitForClose(); // Make sure the dialog is dismissed
		

	}

}
