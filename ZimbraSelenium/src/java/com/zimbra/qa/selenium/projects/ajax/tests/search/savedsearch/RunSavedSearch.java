package com.zimbra.qa.selenium.projects.ajax.tests.search.savedsearch;

import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


//TODO: add more in ContactItem.java

public class RunSavedSearch extends AjaxCommonTest  {

	@SuppressWarnings("serial")
	public RunSavedSearch() {
		logger.info("New "+ RunSavedSearch.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = new HashMap<String, String>() {{
			put("zimbraPrefGroupMailBy", "message");
		}};
		
	}
	
	
	@Test(	description = "Run a saved search",
			groups = { "smoke" })
	public void RunSavedSearch_01() throws HarnessException {				
				
			
		// Create the message data to be sent
		String name = "search" + ZimbraSeleniumProperties.getUniqueString();
		String subject1 = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String subject2 = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String query = "subject:(" + subject1 + ")";
		

		// Send two messages with different subjects to the account
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject1 +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>content1"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject2 +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>content1"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		

		// Create the saved search
		app.zGetActiveAccount().soapSend(
				"<CreateSearchFolderRequest xmlns='urn:zimbraMail'>" +
					"<search name='"+ name +"' query='"+ query +"' l='1'/>" +
				"</CreateSearchFolderRequest>");
		
		// Get the item
		SavedSearchFolderItem item = SavedSearchFolderItem.importFromSOAP(app.zGetActiveAccount(), name);
		
		// Refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Left click on the search
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, item);
		
		
		// Verify the correct messages appear
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		MailItem found1 = null;
		MailItem found2 = null;
		for (MailItem m : messages) {
			logger.info("Subject: looking for "+ subject1 +" found: "+ m.gSubject);
			if ( subject1.equals(m.gSubject) ) {
				found1 = m;
				break;
			}
			logger.info("Subject: looking for "+ subject2 +" found: "+ m.gSubject);
			if ( subject2.equals(m.gSubject) ) {
				found2 = m;
				break;
			}
		}
		ZAssert.assertNotNull(found1, "Verify the matched message exists in the inbox");
		ZAssert.assertNull(found2, "Verify the un-match message does not exist in the inbox");

	}
}
