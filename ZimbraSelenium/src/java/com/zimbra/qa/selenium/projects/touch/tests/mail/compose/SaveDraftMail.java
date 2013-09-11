/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.touch.tests.mail.compose;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.items.RecipientItem.RecipientType;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.touch.core.TouchCommonTest;
import com.zimbra.qa.selenium.projects.touch.ui.mail.FormMailNew;

public class SaveDraftMail extends TouchCommonTest {

	public SaveDraftMail() {
		logger.info("New "+ SaveDraftMail.class.getCanonicalName());

	}
	
	@Test( description = "Save a draft with To, Cc, Bcc, Subject & Body value",
			groups = { "sanity" })
			
	public void SaveDraftMail_01() throws HarnessException {
		
		// Create the message data to be sent
		MailItem mail = new MailItem();
		mail.dToRecipients.add(new RecipientItem(ZimbraAccount.AccountA(), RecipientType.To));
		mail.dCcRecipients.add(new RecipientItem(ZimbraAccount.AccountB(), RecipientType.Cc));
		mail.dBccRecipients.add(new RecipientItem(ZimbraAccount.AccountC(), RecipientType.Bcc));
		mail.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		mail.dBodyText = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		// Open new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		mailform.zFill(mail);
		mailform.zToolbarPressButton(Button.B_SAVE_DRAFT);
		mailform.zToolbarPressButton(Button.B_CANCEL);

		// Verify To: user doesn't receive any email
		ZimbraAccount.AccountA().soapSend(
				"<SearchRequest types='message' xmlns='urn:zimbraMail'>"
						+ "<query>subject:(" + mail.dSubject + ")</query>"
						+ "</SearchRequest>");
		String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		ZAssert.assertNull(id, "Verify To: user doesn't receive any email");
		
		// Get the message from server and verify draft data matches
		MailItem draft = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ mail.dSubject +")");
		FolderItem draftsFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Drafts);

		// To:
		boolean found = false;
		for (RecipientItem r : draft.dToRecipients) {
			if ( r.dEmailAddress.equals(ZimbraAccount.AccountA().EmailAddress) ) {
				found = true;
			}
		}
		ZAssert.assertTrue(found, "Verify the To field contains the correct address(es)");
		
		// Cc:
		found = false;
		for (RecipientItem r : draft.dCcRecipients) {
			if ( r.dEmailAddress.equals(ZimbraAccount.AccountB().EmailAddress) ) {
				found = true;
			}
		}
		ZAssert.assertTrue(found, "Verify the Cc field contains the correct address(es)");
		
		// Bcc:
		found = false;
		for (RecipientItem r : draft.dBccRecipients) {
			if ( r.dEmailAddress.equals(ZimbraAccount.AccountC().EmailAddress) ) {
				found = true;
			}
		}
		ZAssert.assertTrue(found, "Verify the Bcc field contains the correct address(es)");
		
		ZAssert.assertEquals(draft.dSubject, mail.dSubject, "Verify the subject field is correct");
		ZAssert.assertEquals(draft.dBodyText, mail.dBodyText, "Verify the body field is correct");
		ZAssert.assertEquals(draft.dFolderId, draftsFolder.getId(), "Verify the draft is saved in the drafts folder");

	}
	
}
