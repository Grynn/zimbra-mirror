/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose.attachments;

import java.io.File;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;


public class Reply extends PrefGroupMailByMessageTest {

	public Reply() {
		logger.info("New "+ Reply.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");

	}
	
	@Test(	description = "Reply to a mail with attachment - Verify no attachment sent",
			groups = { "functional" })
	public void Reply_01() throws HarnessException {
		

		//-- DATA
		final String mimeSubject = "subject03431362517016470";
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email09/mime.txt";

		final String subject = "subject13625192398933";

		// Send a message to Account A, so that it can be forwarded (with attachment)
		// to the test account
		//
		LmtpInject.injectFile(ZimbraAccount.AccountA().EmailAddress, new File(mimeFile));

		MailItem original = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ mimeSubject +")");
		ZAssert.assertNotNull(original, "Verify the message is received correctly");

		// Get the part ID
		ZimbraAccount.AccountA().soapSend(
					"<GetMsgRequest xmlns='urn:zimbraMail'>"
				+		"<m id='"+ original.getId() +"'/>"
				+	"</GetMsgRequest>");

		String partID = ZimbraAccount.AccountA().soapSelectValue("//mail:mp[@cd='attachment']", "part");

		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
						"<attach>" +
							"<mp mid='"+ original.getId() +"' part='"+ partID +"'/>" +
						"</attach>" +
					"</m>" +
				"</SendMsgRequest>");

		
		
		//-- GUI

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
						
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Forward the item
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLY);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Send the message
		mailform.zSubmit();



		//-- Verification
		
		// From the receiving end, verify the message details
		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "from:("+ app.zGetActiveAccount().EmailAddress +") subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
		// Verify the attachment exists in the forwarded mail
		//  <mp s="1339" filename="screenshot.JPG" part="2" ct="image/jpeg" cd="attachment"/>
		//
		ZimbraAccount.AccountA().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>"
				+		"<m id='"+ received.getId() +"'/>"
				+	"</GetMsgRequest>");

		Element[] nodes = ZimbraAccount.AccountA().soapSelectNodes("//mail:mp[@cd='attachment']");
		ZAssert.assertEquals(nodes.length, 0, "Verify the attachment does not exist in the replied mail");
	}

}
