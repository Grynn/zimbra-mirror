package projects.ajax.tests.mail.compose;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.mail.FormMailNew;
import projects.ajax.ui.mail.FormMailNew.Field;
import framework.ui.AbsDialog;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;
import framework.util.ZimbraSeleniumProperties;

public class CancelComposeHtml extends AjaxCommonTest {

	public CancelComposeHtml() {
		logger.info("New "+ CancelComposeHtml.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccount = new ZimbraAccount();
		super.startingAccount.provision();
		super.startingAccount.authenticate();
		super.startingAccount.modifyPreference("zimbraPrefComposeFormat", "html");
		
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
