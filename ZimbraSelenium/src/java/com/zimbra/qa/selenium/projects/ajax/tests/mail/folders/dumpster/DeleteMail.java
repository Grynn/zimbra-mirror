package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders.dumpster;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;


public class DeleteMail extends PrefGroupMailByMessageTest {

	public DeleteMail() {
		logger.info("New "+ DeleteMail.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraDumpsterEnabled", "TRUE");
	
	}
	
	@Bugs( ids = "65915")
	@Test(	description = "Delete a mail with zimbraDumpsterEnabled=TRUE",
			groups = { "smoke" })
	public void DeleteMail_01() throws HarnessException {
		
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
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
		
		// Click delete
		app.zPageMail.zToolbarPressButton(Button.B_DELETE);
		
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		MailItem found = null;
		for (MailItem m : messages) {
			logger.info("Subject: looking for "+ mail.dSubject +" found: "+ m.gSubject);
			if ( mail.dSubject.equals(m.gSubject) ) {
				found = m;
				break;
			}
		}
		ZAssert.assertNull(found, "Verify the message is no longer in the inbox");
	
		
		MailItem deleted = MailItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:("+ subject +")");
		ZAssert.assertNotNull(deleted, "Verify the deleted message exists");

		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Trash);
		ZAssert.assertEquals(deleted.dFolderId, trash.getId(), "Verify the deleted message exists in the trash folder");

	}




}
