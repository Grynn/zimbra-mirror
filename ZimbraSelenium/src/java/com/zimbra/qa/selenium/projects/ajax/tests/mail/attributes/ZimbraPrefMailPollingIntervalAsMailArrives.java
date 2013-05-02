/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.attributes;

import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;


public class ZimbraPrefMailPollingIntervalAsMailArrives extends PrefGroupMailByMessageTest {

	
	
	
	public ZimbraPrefMailPollingIntervalAsMailArrives() {
		logger.info("New "+ ZimbraPrefMailPollingIntervalAsMailArrives.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefMailPollingInterval", ""+ com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.displayingmessages.ZimbraPrefMailPollingIntervalAsMailArrives.AsMailArrives);



	}
	
	@Test(	description = "Receive a mail with - As Mail Arrives set",
			groups = { "functional" })
	public void ZimbraPrefMailPollingIntervalAsMailArrives_01() throws HarnessException {
		
		
		//-- DATA
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		

		

		//-- GUI
		
		// Refresh the client to sync up
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Send a new message
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Wait for at least a little time (i.e. harness may be faster than the client and network)
		SleepUtil.sleep(com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.displayingmessages.ZimbraPrefMailPollingIntervalAsMailArrives.AsMailArrivesDelay);
		
		
		
		//-- VERIFICATION
		
		
		// Get all the messages in the inbox
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		// Make sure the message appears in the list
		MailItem found = null;
		for (MailItem m : messages) {
			logger.info("Subject: looking for "+ subject +" found: "+ m.gSubject);
			if ( subject.equals(m.gSubject) ) {
				found = m;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the message is in the inbox");

		
	}



}
