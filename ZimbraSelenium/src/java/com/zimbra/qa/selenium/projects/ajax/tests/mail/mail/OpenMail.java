package com.zimbra.qa.selenium.projects.ajax.tests.mail.mail;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.Field;


public class OpenMail extends AjaxCommonTest {
	
	@SuppressWarnings("serial")
	public OpenMail() {
		logger.info("New "+ OpenMail.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
				    put("zimbraPrefGroupMailBy", "message");
				}};


	}
	
	@Test(	description = "Double click a mail",
			groups = { "smoke" })
	public void OpenMail_01() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		// Send the message from AccountA to the ZWC user
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<e t='c' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");


		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
		
		// Double click the message so that it opens
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_DOUBLECLICK, mail.dSubject);
	
		// Verify the To, From, Subject, Body
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.Subject), mail.dSubject, "Verify the subject matches");
		ZAssert.assertNotNull(	actual.zGetMailProperty(Field.ReceivedDate), "Verify the date is displayed");
		ZAssert.assertNotNull(	actual.zGetMailProperty(Field.ReceivedTime), "Verify the time is displayed");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.From), ZimbraAccount.AccountA().EmailAddress, "Verify the From matches");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.Cc), ZimbraAccount.AccountB().EmailAddress, "Verify the Cc matches");
		ZAssert.assertEquals(	actual.zGetMailProperty(Field.To), app.zGetActiveAccount().EmailAddress, "Verify the To matches");
		
		// The body could contain HTML, even though it is only displaying text (e.g. <br> may be present)
		// do a contains, rather than equals.
		ZAssert.assertStringContains(	actual.zGetMailProperty(Field.Body), mail.dBodyText, "Verify the body matches");
		
	}


}
