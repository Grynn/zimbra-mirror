package com.zimbra.qa.selenium.projects.ajax.tests.mail.tags;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

public class TagMessage extends PrefGroupMailByMessageTest {

	public TagMessage() {
		logger.info("New " + TagMessage.class.getCanonicalName());


	}

	@Test(
			description = "Tag a message using Toolbar -> Tag -> New Tag", 
			groups = { "smoke" })
	public void TagMessage_01() throws HarnessException {

		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();

		// Add a message to the mailbox
		FolderItem inboxFolder = FolderItem.importFromSOAP(app
				.zGetActiveAccount(), SystemFolder.Inbox);
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" + "<m l='"
						+ inboxFolder.getId() + "'>"
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

		String tagName = "tag" + ZimbraSeleniumProperties.getUniqueString();

		// Click new tag
		DialogTag dialogTag = (DialogTag) app.zPageMail.zToolbarPressPulldown(
				Button.B_TAG, Button.O_TAG_NEWTAG);
		dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);

		// Make sure the tag was created on the server (get the tag ID)
		app.zGetActiveAccount().soapSend(
				"<GetTagRequest xmlns='urn:zimbraMail'/>");
		;
		String tagID = app.zGetActiveAccount().soapSelectValue(
				"//mail:GetTagResponse//mail:tag[@name='" + tagName + "']",
				"id");

		// Make sure the tag was applied to the message
		app.zGetActiveAccount().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" + "<m id='"
						+ mail.getId() + "'/>" + "</GetMsgRequest>");
		String mailTags = app.zGetActiveAccount().soapSelectValue(
				"//mail:GetMsgResponse//mail:m", "t");

		ZAssert.assertEquals(mailTags, tagID,
				"Verify the tag appears on the message");

	}

	@Test(
			description = "Tag a message using Toolbar -> Tag -> Existing Tag", 
			groups = { "smoke" })
	public void TagMessage_02() throws HarnessException {

		// Add a message to the mailbox
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		String tagname = "tag" + ZimbraSeleniumProperties.getUniqueString();
		
		// Create a tag
		app.zGetActiveAccount().soapSend(
					"<CreateTagRequest xmlns='urn:zimbraMail'>"
			+			"<tag name='" + tagname + "' color='1' />"
			+		"</CreateTagRequest>");
		TagItem tag = TagItem.importFromSOAP(app.zGetActiveAccount(), tagname);
		
		// Create a message
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" 
			+		"<m l='" + inbox.getId() + "'>"
			+			"<content>"
			+				"From: foo@foo.com\n" 
			+ 				"To: foo@foo.com \n"
			+				"Subject: " + subject + "\n" 
			+ 				"MIME-Version: 1.0 \n"
			+				"Content-Type: text/plain; charset=utf-8 \n"
			+				"Content-Transfer-Encoding: 7bit\n" 
			+				"\n"
			+				"content \n"
			+				"\n"
			+				"\n"
			+			"</content>"
			+		"</m>"
			+	"</AddMsgRequest>");

		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:(" + subject + ") not tag:("+ tagname +")");
		ZAssert.assertNotNull(mail, "Verify the message exists");

		
		

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Tag it
		app.zPageMail.zToolbarPressPulldown(Button.B_TAG, tag);
		

		
		
		// Verify the message is tagged
		// Make sure the tag was applied to the message
		app.zGetActiveAccount().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" 
			+		"<m id='" + mail.getId() + "'/>"
			+	"</GetMsgRequest>");
		String mailTags = app.zGetActiveAccount().soapSelectValue("//mail:m", "t");

		ZAssert.assertEquals(mailTags, tag.getId(), "Verify the tag appears on the message");

	}

}
