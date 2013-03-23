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

import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;


public class Forward extends PrefGroupMailByMessageTest {

	public Forward() {
		logger.info("New "+ Forward.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");

	}
	
	@Test(	description = "Forward a mail with attachment - Verify attachment sent",
			groups = { "functional" })
	public void Forward_01() throws HarnessException {
		

		//-- DATA
		final String mimeSubject = "subject03431362517016470";
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email09/mime.txt";
		final String mimeAttachmentName = "screenshot.JPG";

		// Send the message to the test account
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));



		//-- GUI

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
						
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mimeSubject);
		
		// Forward the item
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_FORWARD);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.To, ZimbraAccount.AccountB().EmailAddress);
		
		// Send the message
		mailform.zSubmit();



		//-- Verification
		
		// From the receiving end, verify the message details
		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountB(), "subject:("+ mimeSubject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
		// Verify the attachment exists in the forwarded mail
		//  <mp s="1339" filename="screenshot.JPG" part="2" ct="image/jpeg" cd="attachment"/>
		//
		ZimbraAccount.AccountB().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>"
				+		"<m id='"+ received.getId() +"'/>"
				+	"</GetMsgRequest>");

		String filename = ZimbraAccount.AccountB().soapSelectValue("//mail:mp[@cd='attachment']", "filename");
		ZAssert.assertEquals(filename, mimeAttachmentName, "Verify the attachment exists in the forwarded mail");
	}

}
