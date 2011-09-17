package com.zimbra.qa.selenium.projects.ajax.tests.mail.gui.features;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;


public class ZimbraFeatureMailEnabled extends PrefGroupMailByMessageTest {

	
	public ZimbraFeatureMailEnabled() {
		logger.info("New "+ ZimbraFeatureMailEnabled.class.getCanonicalName());
		
		
		

		super.startingAccountPreferences.put("zimbraFeatureMailEnabled", "TRUE");
		super.startingAccountPreferences.put("zimbraFeatureContactsEnabled", "FALSE");
		super.startingAccountPreferences.put("zimbraFeatureCalendarEnabled", "FALSE");
		super.startingAccountPreferences.put("zimbraFeatureTasksEnabled", "FALSE");
		super.startingAccountPreferences.put("zimbraFeatureBriefcasesEnabled", "FALSE");


	}
	
	/**
	 * @throws HarnessException
	 */
	@Test(	description = "Load the mail tab with just Mail enabled",
			groups = { "functional" })
	public void ZimbraFeatureMailEnabled_01() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		// Send the message from AccountA to the ZWC user
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");


		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		// Make sure the message appears in the list
		MailItem found = null;
		for (MailItem m : messages) {
			logger.info("Subject: looking for "+ subject +" found: "+ m.gSubject);
			if ( mail.dSubject.equals(m.gSubject) ) {
				found = m;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the message is in the inbox");

		
	}


}
