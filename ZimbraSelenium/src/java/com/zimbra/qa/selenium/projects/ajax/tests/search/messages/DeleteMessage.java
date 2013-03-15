package com.zimbra.qa.selenium.projects.ajax.tests.search.messages;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;


public class DeleteMessage extends PrefGroupMailByMessageTest {

	public DeleteMessage() {
		logger.info("New "+ DeleteMessage.class.getCanonicalName());
		
		
		super.startingAccountPreferences.put("zimbraPrefShowSelectionCheckbox", "TRUE");

		
	}
	
	@Test(	description = "From search: Delete a mail using toolbar delete button",
			groups = { "smoke" })
	public void DeleteMail_01() throws HarnessException {
		
		
		//-- Data
		
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);

		// Create the message data to be sent
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
				
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

		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		
		
		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Remember to close the search view
		try {
			
			// Search for the message
			app.zPageSearch.zAddSearchQuery("subject:("+ subject +")");
			app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
			
				// Select the item
			app.zPageSearch.zListItem(Action.A_LEFTCLICK, mail.dSubject);
			
			// Click delete
			app.zPageSearch.zToolbarPressButton(Button.B_DELETE);
		
		} finally {
			// Remember to close the search view
			app.zPageSearch.zClose();
		}


		//-- Verification
		
		MailItem message = MailItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:("+ subject +")");
		ZAssert.assertNotNull(message, "Verify the message still exists in the mailbox");
		ZAssert.assertEquals(message.dFolderId, trash.getId(), "Verify the message exists in the correct folder");

	}


}
