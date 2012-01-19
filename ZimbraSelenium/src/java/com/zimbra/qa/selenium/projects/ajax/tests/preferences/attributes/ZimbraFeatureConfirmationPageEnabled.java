/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.attributes;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;



public class ZimbraFeatureConfirmationPageEnabled extends PrefGroupMailByMessageTest {
	
	public ZimbraFeatureConfirmationPageEnabled() {
		logger.info("New "+ ZimbraFeatureConfirmationPageEnabled.class.getCanonicalName());

		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		super.startingAccountPreferences.put("ZimbraFeatureConfirmationPageEnabled", "TRUE");
	}
	
	@Bugs(ids = "21979")
	@Test(	description = "Send a message and confirm the confirmation page",
			groups = { "functional" })
	public void ZimbraFeatureConfirmationPageEnabled01() throws HarnessException {
		
		// Create the message data to be sent
		MailItem mail = new MailItem();
		mail.dToRecipients.add(new RecipientItem(ZimbraAccount.AccountA()));
		mail.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		mail.dBodyText = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFill(mail);
		
		// Send the message
		mailform.zSubmit();

		// Verify the confirmation pops up
		DialogSendConfirmation dialog = new DialogSendConfirmation(app, app.zPageMail);
		ZAssert.assertTrue(dialog.zIsActive(), "Verify the confirmation pops up");
		
		// Dismiss the dialog
		dialog.zClickButton(Button.B_CLOSE);
		

		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ mail.dSubject +")");
		ZAssert.assertNotNull(received, "Verify the message is received");

		
	}


}
