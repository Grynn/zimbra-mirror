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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.deeplink.compose;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;


public class CreateMailText extends PrefGroupMailByMessageTest {

	public CreateMailText() {
		logger.info("New "+ CreateMailText.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		
	}
	
	@Test(	description = "Create a text mail using the deep link URL",
			groups = { "functional" })
	public void CreateMailText_01() throws HarnessException {
		
		
		
		//-- DATA
		
		
		// Create the message data to be sent
		final String to = ZimbraAccount.AccountA().EmailAddress;
		final String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		final String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		
		
		
		//-- GUI
		
		// The account is already authenticated
		// However, we need to change the URL and open
		// the deep link form.
		
		ZimbraURI uri = new ZimbraURI(ZimbraURI.getCurrentURI());
		uri.addQuery("view", "compose");	// Setting view=compose will make PageMain return a FormMailNew object
		uri.addQuery("to", to);
		uri.addQuery("subject", subject);
		uri.addQuery("body", body);

		// Open the Deep Link URL: http://server.com/?view=compose&to=addy&subject=text&body=value
		//
		FormMailNew mailform = (FormMailNew)app.zPageMain.zOpenDeeplink(uri);
		ZAssert.assertNotNull(mailform, "Verify the deeplink page opens");
		ZAssert.assertTrue(mailform.zIsActive(), "Verify the deeplink page opens");
		
		// The form should be filled out, so just submit
		mailform.zSubmit();
		
		
		//-- VERIFICATION
		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}





}
