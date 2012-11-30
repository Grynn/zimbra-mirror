package com.zimbra.qa.selenium.projects.ajax.tests.mail.mail.headers;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.Field;


public class ExpandDL extends PrefGroupMailByMessageTest {

	
	public ExpandDL() throws HarnessException {
		logger.info("New "+ ExpandDL.class.getCanonicalName());
		
		
	}
	
	
	@Test(	description = "Verify the Expand icon a DL in the To field",
			groups = { "smoke" })
	public void ExpandDL_01() throws HarnessException {
		
		//-- DATA
		
		// Create a DL with a couple of members
		ZimbraAccount account1 = (new ZimbraAccount()).provision().authenticate();
		ZimbraAccount account2 = (new ZimbraAccount()).provision().authenticate();
		ZimbraDistributionList distribution = (new ZimbraDistributionList()).provision();
		distribution.addMember(account1);
		distribution.addMember(account2);

		// Send a message to the DL with the test account in the CC
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ distribution.EmailAddress +"'/>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		
		
		//-- GUI
		
		// Refresh to get the new message
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		List<AbsBubble> bubbles = actual.zListGetBubbles(Field.To);
		
		
		
		//-- Verification
		
		// Verify the DL is in the To field and has a expand/+ icon
		AbsBubble found = null;
		for (AbsBubble b : bubbles) {
			if ( distribution.EmailAddress.equalsIgnoreCase(b.getMyDisplayText()) ) {
				found = b;
				break;
			}
		}
		
		ZAssert.assertNotNull(found, "Verify the DL bubble is found");
		ZAssert.assertNotNull(found instanceof BubbleEmailAddress, "Verify the bubble is a DL bubble");
		ZAssert.assertTrue(((BubbleEmailAddress)found).zHasExpandIcon(), "Verify the DL buble has a expand/+ icon");
		
		
		
		
		
	}

	
	@Test(	description = "Expand a DL in the To field",
			groups = { "smoke" })
	public void ExpandDL_02() throws HarnessException {
		
		//-- DATA
		
		// Create a DL with a couple of members
		ZimbraAccount account1 = (new ZimbraAccount()).provision().authenticate();
		ZimbraAccount account2 = (new ZimbraAccount()).provision().authenticate();
		ZimbraDistributionList distribution = (new ZimbraDistributionList()).provision();
		distribution.addMember(account1);
		distribution.addMember(account2);

		// Send a message to the DL with the test account in the CC
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ distribution.EmailAddress +"'/>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		
		
		
		
		//-- GUI
		
		// Refresh to get the new message
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Find the DL bubble
		List<AbsBubble> bubbles = actual.zListGetBubbles(Field.To);
		AbsBubble found = null;
		for (AbsBubble b : bubbles) {
			if ( distribution.EmailAddress.equalsIgnoreCase(b.getMyDisplayText()) ) {
				found = b;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the DL bubble is found");
		ZAssert.assertNotNull(found instanceof BubbleEmailAddress, "Verify buble is a DL bubble");

		BubbleEmailAddress bubble = (BubbleEmailAddress)found;
		
		

		//-- Verification
		
		// Expand the bubble
		bubble.zItem(Action.A_EXPAND);

		// Get the list of members that are displayed
		List<AutocompleteEntry> members = bubble.zAutocompleteListGetEntries();
		
		// Verify that the two members show up
		boolean found1 = false;
		boolean found2 = false;
		for ( AutocompleteEntry entry : members ) {
			if ( entry.getAddress().equals(account1.EmailAddress) ) {
				found1 = true;
				continue;
			}
			if ( entry.getAddress().equals(account2.EmailAddress) ) {
				found2 = true;
				continue;
			}
		}
		
		ZAssert.assertTrue(found1, "Verify the DL expanded list contains account1's email "+ account1.EmailAddress);
		ZAssert.assertTrue(found2, "Verify the DL expanded list contains account2's email "+ account2.EmailAddress);



	}


}
