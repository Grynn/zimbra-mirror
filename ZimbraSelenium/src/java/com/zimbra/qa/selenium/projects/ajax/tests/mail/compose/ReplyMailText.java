package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;


public class ReplyMailText extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public ReplyMailText() {
		logger.info("New "+ ReplyMailText.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = new HashMap<String, String>() {{
				    put("zimbraPrefComposeFormat", "text");
				}};
		
	}
	
	@Test(	description = "Reply a plain text mail using Text editor",
			groups = { "smoke" })
	public void replyPlainTextMail() throws HarnessException {

		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();

		// Send a message to the account
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		app.zPageMail.zSyncDesktopToZcs();

		// Get the mail item for the new message
		Object[] params = {app.zGetActiveAccount(), "subject:("+ subject +")"};
      MailItem mail = (MailItem)GeneralUtility.waitFor("com.zimbra.qa.selenium.framework.items.MailItem", null, true,
            "importFromSOAP", params, WAIT_FOR_OPERAND.NEQ, null, 30000, 1000);

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);

		// Forward the item
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLY);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");

		// Send the message
		mailform.zSubmit();

		app.zPageMail.zSyncDesktopToZcs();

		// From the receiving end, verify the message details
		// Need 'in:inbox' to seprate the message from the sent message
		Object[] params2 = {ZimbraAccount.AccountA(), "in:inbox subject:("+ mail.dSubject +")"};
      MailItem received = (MailItem)GeneralUtility.waitFor("com.zimbra.qa.selenium.framework.items.MailItem", null, true,
            "importFromSOAP", params2, WAIT_FOR_OPERAND.NEQ, null, 30000, 1000);

		ZAssert.assertEquals(received.dFromRecipient.dEmailAddress, app.zGetActiveAccount().EmailAddress, "Verify the from field is correct");
		ZAssert.assertEquals(received.dToRecipients.get(0).dEmailAddress, ZimbraAccount.AccountA().EmailAddress, "Verify the to field is correct");
		ZAssert.assertStringContains(received.dSubject, mail.dSubject, "Verify the subject field is correct");
		ZAssert.assertStringContains(received.dSubject, "Re", "Verify the subject field contains the 'Re' prefix");
		
	}

}
