package projects.ajax.tests.mail.compose;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.mail.FormMailNew;
import framework.items.MailItem;
import framework.items.RecipientItem;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;
import framework.util.ZimbraSeleniumProperties;

public class CreateMail extends AjaxCommonTest {

	public CreateMail() {
		logger.info("New "+ CreateMail.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccount = null;
		
	}
	
	@Test(	description = "Send a mail",
			groups = { "sanity" })
	public void CreateMail_01() throws HarnessException {
		
		
		// Create the message data to be sent
		MailItem mail = new MailItem();
		mail.dToRecipients.add(new RecipientItem(ZimbraAccount.AccountA()));
		mail.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		mail.dBodyText = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFill(mail);
		
		// Send the message
		mailform.zSubmit();
				
		
		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ mail.dSubject +")");
		
		
		// TODO: add checks for TO, Subject, Body
		ZAssert.assertEquals(received.dFromRecipient.dEmailAddress, app.zGetActiveAccount().EmailAddress, "Verify the from field is correct");
		ZAssert.assertEquals(received.dToRecipients.get(0).dEmailAddress, ZimbraAccount.AccountA().EmailAddress, "Verify the to field is correct");
		ZAssert.assertEquals(received.dSubject, mail.dSubject, "Verify the subject field is correct");
		ZAssert.assertStringContains(received.dBodyText, mail.dBodyText, "Verify the body field is correct");
		
	}

}
