package com.zimbra.qa.selenium.projects.ajax.tests.preferences.addressbook;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;

public class ZimbraPrefAutoAddAddressEnabledFalse extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public ZimbraPrefAutoAddAddressEnabledFalse() {
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{				
				put("zimbraPrefAutoAddAddressEnabled", "FALSE");
			}
		};
	}

	/**
	 * Test case : Opt-out Add New Contacts To emailed contact
	 * Verify receiver' addresses of out-going mails not added to "Emailed Contacts" folder automatically 
	 * @throws HarnessException
	 */
	@Test(description = " send message to 1 receiver, the address should not be added into Emailed Contact", groups = { "smoke" })
	public void SendEmailTo1Receiver() throws HarnessException {

		ZimbraAccount receiver = new ZimbraAccount();
		receiver.provision();
		receiver.authenticate();
		
		// Create the message data to be sent
		MailItem mail = new MailItem();
		mail.dToRecipients.add(new RecipientItem(receiver));
		mail.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		mail.dBodyText = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFill(mail);
		
		// Send the message
		mailform.zSubmit();

		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
				+		"<query>"+ receiver.EmailAddress +"</query>"
				+	"</SearchRequest>");
		
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:cn");
		ZAssert.assertEquals(nodes.length, 0, "Verify that no contact is created in the mailbox");

	}


	/**
	 * Test case : Opt-out Add New Contacts To emailed contact
	 * Verify receiver' addresses of out-going mails not added to "Emailed Contacts" folder automatically 
	 * @throws HarnessException
	 */
	@Test(description = " send message to 2 receivers, the address should not be added into Emailed Contact", groups = { "functional" })
	public void SendEmailTo2Receivers() throws HarnessException {

		ZimbraAccount receiver1 = new ZimbraAccount();
		receiver1.provision();
		receiver1.authenticate();
		
		ZimbraAccount receiver2 = new ZimbraAccount();
		receiver1.provision();
		receiver1.authenticate();
		
		// Create the message data to be sent
		MailItem mail = new MailItem();
		mail.dToRecipients.add(new RecipientItem(receiver1));
		mail.dToRecipients.add(new RecipientItem(receiver2));
		mail.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		mail.dBodyText = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFill(mail);
		
		// Send the message
		mailform.zSubmit();

		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
			+		"<query>"+ receiver1.EmailAddress +"</query>"
			+	"</SearchRequest>");

		Element[] nodes1 = app.zGetActiveAccount().soapSelectNodes("//mail:cn");
		ZAssert.assertEquals(nodes1.length, 0, "Verify that no contact is created in the mailbox");

		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
			+		"<query>"+ receiver2.EmailAddress +"</query>"
			+	"</SearchRequest>");

		Element[] nodes2 = app.zGetActiveAccount().soapSelectNodes("//mail:cn");
		ZAssert.assertEquals(nodes2.length, 0, "Verify that no contact is created in the mailbox");

	}

	
}
