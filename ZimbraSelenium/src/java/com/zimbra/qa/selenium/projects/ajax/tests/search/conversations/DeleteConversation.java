package com.zimbra.qa.selenium.projects.ajax.tests.search.conversations;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByConversationTest;


public class DeleteConversation extends PrefGroupMailByConversationTest {

	public DeleteConversation() {
		logger.info("New "+ DeleteConversation.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefShowSelectionCheckbox", "TRUE");

	}
	
	@Bugs(	ids = "81074")
	@Test(	description = "From search: Delete a conversation using the Delete Toolbar button",
			groups = { "functional" })
	public void DeleteConversation_01() throws HarnessException {
		

		//-- Data
		
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);

		// Create the message data to be sent
		ConversationItem c = ConversationItem.createConversationItem(app.zGetActiveAccount());
		

		//-- GUI
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Remember to close the search view
		try {
			
			// Search for the message
			app.zPageSearch.zAddSearchQuery("subject:("+ c.getSubject() +")");
			app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
			

			// Select the item
			app.zPageSearch.zListItem(Action.A_LEFTCLICK, c.getSubject());
			
			// Click delete
			app.zPageSearch.zToolbarPressButton(Button.B_DELETE);
		
		} finally {
			// Remember to close the search view
			app.zPageSearch.zClose();
		}
		
		
		
		
		//-- Verification
		
		MailItem message = MailItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere from:("+ ZimbraAccount.AccountA().EmailAddress + ") subject:("+ c.getSubject() +")");
		ZAssert.assertNotNull(message, "Verify the message still exists in the mailbox");
		ZAssert.assertEquals(message.dFolderId, trash.getId(), "Verify the message exists in the trash");
		
	}

	
	
	

}
