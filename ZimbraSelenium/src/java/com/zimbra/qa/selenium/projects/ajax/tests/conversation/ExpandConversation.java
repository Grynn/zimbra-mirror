package com.zimbra.qa.selenium.projects.ajax.tests.conversation;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ConversationItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class ExpandConversation extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public ExpandConversation() {
		logger.info("New "+ ExpandConversation.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		
		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = new HashMap<String , String>() {{
				    put("zimbraPrefGroupMailBy", "conversation");
				}};
	
	}
	
	@Test(	description = "Expand a conversation",
			groups = { "smoke" })
	public void ExpandConversation01() throws HarnessException {
		
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String fragment1 = "fragment" + ZimbraSeleniumProperties.getUniqueString();
		String fragment2 = "fragment" + ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ fragment1 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");

		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>RE: "+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ fragment2 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Expand the item
		app.zPageMail.zListItem(Action.A_MAIL_EXPANDCONVERSATION, subject);

		// Verify the list shows: 1 conversation with 2 messages
		
		List<MailItem> items = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(items, "Verify the conversation list exists");

		boolean found = false;
		for (MailItem c : items) {
			logger.info("Subject: looking for "+ subject +" found: "+ c.gSubject);
			if ( subject.equals(c.gSubject) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertTrue(found, "Verify the conversation is in the inbox");
		
		int count = 0;
		for (MailItem m : items) {
			logger.info("Subject: looking for "+ fragment1 +" or "+ fragment2 +" found: "+ m.gFragment);

			if ( m instanceof ConversationItem ) {

				ConversationItem c = (ConversationItem)m;
				
				if ( !c.gIsConvExpanded ) {
					// Not a conversation member
					continue;
				}
				
					
				if ( fragment1.equals(c.gFragment) ) {
					logger.info("Subject: Found "+ fragment1);
					count++;
				}
				if ( fragment2.equals(c.gFragment) ) {
					logger.info("Subject: Found "+ fragment2);
					count++;
				}
				
			}
				
		}
		ZAssert.assertEquals(count, 2, "Verify two messages in the conversation");

	}

}
