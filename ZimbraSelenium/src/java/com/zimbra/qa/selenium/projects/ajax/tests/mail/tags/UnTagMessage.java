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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.tags;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;

public class UnTagMessage extends PrefGroupMailByMessageTest {

	public UnTagMessage() {
		logger.info("New " + UnTagMessage.class.getCanonicalName());


	}

	@Test(description = "Remove a tag from a message using Toolbar -> Tag -> New Tag", groups = { "smoke" })
	public void UnTagMessage_01() throws HarnessException {

		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String tagname = "tag" + ZimbraSeleniumProperties.getUniqueString();

		// Create a tag
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" + "<tag name='"
						+ tagname + "' color='1' />" + "</CreateTagRequest>");
		String tagid = app.zGetActiveAccount().soapSelectValue(
				"//mail:CreateTagResponse/mail:tag", "id");

		// Add a message to the mailbox
		FolderItem inboxFolder = FolderItem.importFromSOAP(app
				.zGetActiveAccount(), SystemFolder.Inbox);
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" + "<m l='"
						+ inboxFolder.getId() + "' t='" + tagid + "'>"
						+ "<content>From: foo@foo.com\n" + "To: foo@foo.com \n"
						+ "Subject: " + subject + "\n" + "MIME-Version: 1.0 \n"
						+ "Content-Type: text/plain; charset=utf-8 \n"
						+ "Content-Transfer-Encoding: 7bit\n" + "\n"
						+ "simple text string in the body\n" + "</content>"
						+ "</m>" + "</AddMsgRequest>");

		// Get the message data from SOAP
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(),
				"subject:(" + subject + ")");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);

		// Untag it
		app.zPageMail.zToolbarPressPulldown(Button.B_TAG,
				Button.O_TAG_REMOVETAG);

		app.zGetActiveAccount().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" + "<m id='"
						+ mail.getId() + "'/>" + "</GetMsgRequest>");
		String mailTags = app.zGetActiveAccount().soapSelectValue(
				"//mail:GetMsggResponse//mail:m", "t");

		ZAssert.assertNull(mailTags,
				"Verify that the tag is removed from the message");

	}

}
