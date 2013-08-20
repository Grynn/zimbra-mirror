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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.bugs;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;


public class Bug76547 extends PrefGroupMailByMessageTest {

	public Bug76547() {
		logger.info("New "+ Bug76547.class.getCanonicalName());
		
		
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		super.startingAccountPreferences.put("zimbraPrefUseKeyboardShortcuts", "FALSE");
		
	}
	
	@Bugs(ids = "76547,80740")
	@Test(	description = "Send a mail using Text editor - zimbraPrefUseKeyboardShortcuts = FALSE",
			groups = { "functional" })
	public void Bug76547_01() throws HarnessException {
		
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(FormMailNew.Field.Subject, subject);
		mailform.zFillField(FormMailNew.Field.Body, "body" + ZimbraSeleniumProperties.getUniqueString());

		// Selenium JS can't repro the bug.  I suppose the To: field is still enabled
		// at the JS level.  But, clicking manually into the field doesn't work.
		//
		// Maybe WebDriver will be better at repro, since it mimics real usage better.
		//
		mailform.sFocus(FormMailNew.Locators.zToField);
		mailform.zClick(FormMailNew.Locators.zToField);
		mailform.zWaitForBusyOverlay();
		mailform.zKeyboard.zTypeCharacters(ZimbraAccount.AccountA().EmailAddress);
		
		// Send the message
		mailform.zSubmit();

		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received by Account A");
		
		
	}



}
