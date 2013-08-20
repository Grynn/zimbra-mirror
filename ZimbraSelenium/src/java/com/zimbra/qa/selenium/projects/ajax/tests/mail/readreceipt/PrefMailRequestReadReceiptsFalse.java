/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.readreceipt;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;


public class PrefMailRequestReadReceiptsFalse extends PrefGroupMailByMessageTest {

	public PrefMailRequestReadReceiptsFalse() {
		logger.info("New "+ PrefMailRequestReadReceiptsFalse.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefMailRequestReadReceipts", "FALSE");
		
	}
	
	@Test(	description = "Send a text message requesting a read receipt",
			groups = { "functional" })
	public void CreateMailText_01() throws HarnessException {
		
		
		//-- DATA Setup
		
		// Create the message data to be sent
		MailItem mail = new MailItem();
		mail.dToRecipients.add(new RecipientItem(ZimbraAccount.AccountA()));
		mail.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		mail.dBodyText = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		
		//-- GUI Actions
		//
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFill(mail);
		
		// Send the message
		mailform.zSubmit();


		//-- VERIFICATION
		//
		
		// Verify the message is received with a read receipt request
		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ mail.dSubject +")");

		ZimbraAccount.AccountA().soapSend(
					"<GetMsgRequest  xmlns='urn:zimbraMail'>"
				+		"<m id='"+ received.getId() +"'/>"
				+	"</GetMsgRequest>");
		com.zimbra.common.soap.Element[] nodes = ZimbraAccount.AccountA().soapSelectNodes("//mail:e[@t='n']");

		ZAssert.assertEquals(nodes.length, 0, "Verify no requestor (t='n') nodes");
		
	}

	


}
