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
package com.zimbra.qa.selenium.projects.touch.tests.mail.compose;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.touch.core.TouchCommonTest;
import com.zimbra.qa.selenium.projects.touch.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.touch.ui.mail.FormMailNew.Field;

public class ForwardHtmlMail extends TouchCommonTest {

	public ForwardHtmlMail() {
		logger.info("New "+ ForwardHtmlMail.class.getCanonicalName());
	}
	
	@Test( description = "Forward a html mail and verify body content",
			groups = { "sanity" })
			
	public void ForwardHtmlMail_01() throws HarnessException {
		
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "text <strong>bold"+ ZimbraSeleniumProperties.getUniqueString() +"</strong> text";
		String modifiedContent = " modified body" + ZimbraSeleniumProperties.getUniqueString();
		
		String htmlBody = XmlStringUtil.escapeXml(
				"<html>" +
					"<head></head>" +
					"<body>"+ body +"</body>" +
				"</html>");

		// Send a message to the account
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='multipart/alternative'>" +
								"<mp ct='text/plain'>" +
									"<content>"+ body +"</content>" +
								"</mp>" +
								"<mp ct='text/html'>" +
									"<content>"+ htmlBody +"</content>" +
								"</mp>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");
		
		// Select the mail from inbox
		app.zPageMail.zToolbarPressButton(Button.B_FOLDER_TREE);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Forward the mail
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_FORWARD);
		mailform.zFillField(Field.To, ZimbraAccount.AccountB().EmailAddress);
		mailform.zFillField(Field.Body, modifiedContent);
		mailform.zSubmit();

		// Verify received mail
		ZimbraAccount.AccountB().soapSend(
				"<SearchRequest types='message' xmlns='urn:zimbraMail'>"
						+ "<query>subject:(" + subject + ")</query>"
						+ "</SearchRequest>");
		String toid = ZimbraAccount.AccountB().soapSelectValue("//mail:m", "id");
		
		ZimbraAccount.AccountB().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" + "<m id='" + toid
						+ "' html='1'/>" + "</GetMsgRequest>");

		String tobody = ZimbraAccount.AccountB().soapSelectValue("//mail:content", null);
		ZAssert.assertStringContains(tobody, body, "Verify the body content");
		ZAssert.assertStringContains(tobody, modifiedContent, "Verify the modified content");
		ZAssert.assertStringContains(tobody, "----- Forwarded Message -----", "Verify the body content");
		
	}
}
