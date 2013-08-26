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
package com.zimbra.qa.selenium.projects.ajax.tests.zimlets.ymemoticons;

import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.*;


public class GetMessage extends AjaxCommonTest {

	public static final class Emoticons {
		public static final String HAPPY = ":)";
		public static final String SAD = ":(";
		// TODO: add all the emoticons
	}
	
	
	public GetMessage() {
		logger.info("New "+ GetMessage.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Basic settings
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 2239204417855001627L;
		{
		    put("zimbraPrefGroupMailBy", "message");
		}};



	}
	
	@Test(	description = "Receive a mail with a basic emoticon",
			groups = { "functional" })
	public void GetMessage_01() throws HarnessException {
		
		
		//-- DATA
		
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "text " + Emoticons.HAPPY + " text";
		
		// Send the message from AccountA to the ZWC user
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ body +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		
		//-- GUI
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Wait for a bit so the zimlet can take affect
		SleepUtil.sleep(5000);
		
		
		//-- VERIFICATION
		
		
		// Get the HTML of the body
		HtmlElement bodyElement = display.zGetMailPropertyAsHtml(Field.Body);
		
		// Verify that the phone zimlet has been applied
		//
		// <span style="height:18;width:18;padding:9px 18px 9px 0; 
		//			background:url(https://zqa-062.eng.zimbra.com/service/zimlet/com_zimbra_ymemoticons/img/1.gif) 
		//			no-repeat 0 50%;" title=":( - sad">
		//		<span style="visibility:hidden">a</span>
		//	</span>
		//
		
		// TODO: probably need to re-implement HtmlElement, since htmlcleaner doesn't
		// support 'contains()', which is required to verify this element.  For now,
		// just verify that the strings are contained in the body.
		// 
		ZAssert.assertStringContains(bodyElement.prettyPrint(), "com_zimbra_ymemoticons", "Verify the ymemoticons zimlet is applied to the body");
		ZAssert.assertStringContains(bodyElement.prettyPrint(), "1.gif", "Verify the 'happy' emoticon is displayed");
		


	}




}
