package projects.ajax.tests.mail.compose;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.Buttons;
import projects.ajax.ui.FormMailNew;
import framework.items.MailItem;
import framework.items.RecipientItem;
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
		mail.recipients.add(new RecipientItem(ZimbraAccount.AccountA()));
		mail.subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		mail.bodyText = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Buttons.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.fill(mail);
		
		// Send the message
		mailform.submit();
				
		
		// Verify the message is received at the destination
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>" +
					"<query>subject:("+ mail.subject +")</query>" +
				"</SearchRequest>");
		String messageID = ZimbraAccount.AccountA().soapSelectValue("//mail:SearchResponse//mail:m", "id");
		ZAssert.assertNotNull(messageID, "Verify the recipient can search for the message");
		
		ZimbraAccount.AccountA().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" +
                	"<m id='"+ messageID +"'/>" +
                "</GetMsgRequest>");
		String from = ZimbraAccount.AccountA().soapSelectValue("//mail:GetMsgResponse//mail:e[@t='f']", "a");
		String to = ZimbraAccount.AccountA().soapSelectValue("//mail:GetMsgResponse//mail:e[@t='t']", "a");
		String subject = ZimbraAccount.AccountA().soapSelectValue("//mail:GetMsgResponse//mail:su", null);
		
		// TODO: add checks for TO, Subject, Body
		ZAssert.assertEquals(from, app.getActiveAccount().EmailAddress, "Verify the from field is correct");
		ZAssert.assertEquals(to, ZimbraAccount.AccountA().EmailAddress, "Verify the to field is correct");
		ZAssert.assertEquals(subject, mail.subject, "Verify the subject field is correct");
		

		
	}

}
