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

import java.io.*;
import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.*;

public class Bug82073 extends PrefGroupMailByMessageTest {

	public Bug82073() {
		logger.info("New " + Bug82073.class.getCanonicalName());

		
	}

	@Bugs(	ids = "Bug82073")
	@Test(	description = "Send a mail with the '&' character in the subject - verify no '&amp;'", 
			groups = { "functional" }
			)
	public void Bug82073_01() throws HarnessException {

		//-- DATA
		
		String subject = ZimbraSeleniumProperties.getUniqueString() + " & " + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();

		
		
		
		//-- GUI
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.To, ZimbraAccount.AccountA().EmailAddress);
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);
		
		// Send the message
		mailform.zSubmit();
		
		
		//-- VERIFIFICATION
		
		// Verify the retention policy on the folder
		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "content:("+ body +")");
		ZAssert.assertStringDoesNotContain(received.getSubject(), "amp", "Verify the subject does not contain '&amp;'");
		
	}

	@Bugs(	ids = "Bug82073")
	@Test(	description = "Receive a mail with the '&' character in the subject - verify no '&amp;'", 
			groups = { "functional" }
			)
	public void Bug82073_02() throws HarnessException {

		//-- DATA
		
		String subject = "13680547408344 & 13680547408345";
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Bugs/Bug82073/mime1.txt";
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));


		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		
		
		//-- VERIFIFICATION
		
		// Make sure the message appears in the list
		MailItem found = null;
		for (MailItem m : messages) {
			logger.info("Subject: looking for "+ subject +" found: "+ m.gSubject);
			if ( subject.equals(m.gSubject) ) {
				found = m;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the message is in the inbox (without the &amp;)");
		
	}


}
