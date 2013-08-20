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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.newwindow.compose;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.SeparateWindowFormMailNew;

public class CreateMailHtml extends PrefGroupMailByMessageTest {

	public CreateMailHtml() {
		logger.info("New "+ CreateMailHtml.class.getCanonicalName());

		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "html");
		super.startingAccountPreferences.put("zimbraPrefComposeInNewWindow", "TRUE");

	}

	@Test(	description = "Send a mail using HTML editor - in a separate window",
			groups = { "sanity" })
			public void CreateMailHtml_01() throws HarnessException {


		// Create the message data to be sent
		MailItem mail = new MailItem();
		mail.dToRecipients.add(new RecipientItem(ZimbraAccount.AccountA()));
		mail.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		/* TODO: ... debugging to be removed */ 
		//mail.dBodyHtml = "body" + ZimbraSeleniumProperties.getUniqueString();


		// Open the new mail form
		SeparateWindowFormMailNew window = null;

		try {

			window = (SeparateWindowFormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW_IN_NEW_WINDOW);

			window.zSetWindowTitle("Compose");
			window.zWaitForActive();		// Make sure the window is there

			window.waitForComposeWindow();
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");

			// Fill out the form with the data
			window.zFill(mail);
			
			/* TODO: ... debugging to be removed */ 
			mail.dBodyHtml = "body" + ZimbraSeleniumProperties.getUniqueString();
			window.sSelectWindow("Zimbra: Compose");
			String locator = "css=iframe[id*=ifr]";
			window.zWaitForElementPresent(locator, "5000");
			window.sClickAt(locator,"");
			window.zTypeFormattedText(locator, mail.dBodyHtml);
			
			// Send the message
			window.zToolbarPressButton(Button.B_SEND);
			
			if(window.zWaitForWindowClosed("Zimbra: Compose")){
			    // Window closes automatically
			    window = null;
			}
		} finally {

			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}

		}

		// Sometimes, the harness is too fast for the client.
		// Since we are composing in a new window, there is no
		// busy overlay to block.
		//
		// Add a loop, while waiting for the message
		//
		for (int i = 0; i < 30; i++) {

			ZimbraAccount.AccountA().soapSend(
					"<SearchRequest types='message' xmlns='urn:zimbraMail'>"
			+			"<query>subject:("+ mail.dSubject +")</query>"
			+		"</SearchRequest>");
			com.zimbra.common.soap.Element node = ZimbraAccount.AccountA().soapSelectNode("//mail:m", 1);
			if ( node != null ) {
				// found the message
				break;
			}
			
			SleepUtil.sleep(1000);

		}

		// Can't use importFromSOAP, since that only parses the text part
		// MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ mail.dSubject +")");

		ZimbraAccount.AccountA().soapSend(
						"<SearchRequest types='message' xmlns='urn:zimbraMail'>"
				+			"<query>subject:("+ mail.dSubject +")</query>"
				+		"</SearchRequest>");
		String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");
		
		ZimbraAccount.AccountA().soapSend(
						"<GetMsgRequest xmlns='urn:zimbraMail'>"
				+			"<m id='"+ id +"' html='1'/>"
				+		"</GetMsgRequest>");

		String from = ZimbraAccount.AccountA().soapSelectValue("//mail:e[@t='f']", "a");
		String to = ZimbraAccount.AccountA().soapSelectValue("//mail:e[@t='t']", "a");
		String subject = ZimbraAccount.AccountA().soapSelectValue("//mail:su", null);
		String html = ZimbraAccount.AccountA().soapSelectValue("//mail:mp[@ct='text/html']//mail:content", null);
		
		ZAssert.assertEquals(from, app.zGetActiveAccount().EmailAddress, "Verify the from field is correct");
		ZAssert.assertEquals(to, ZimbraAccount.AccountA().EmailAddress, "Verify the to field is correct");
		ZAssert.assertEquals(subject, mail.dSubject, "Verify the subject field is correct");
		ZAssert.assertStringContains(html, mail.dBodyHtml, "Verify the html content");

	}

}
