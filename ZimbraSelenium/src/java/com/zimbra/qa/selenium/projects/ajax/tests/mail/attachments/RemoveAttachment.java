package com.zimbra.qa.selenium.projects.ajax.tests.mail.attachments;

import java.io.File;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;


public class RemoveAttachment extends PrefGroupMailByMessageTest {

	
	public RemoveAttachment() throws HarnessException {
		logger.info("New "+ RemoveAttachment.class.getCanonicalName());
		
	}
	
	
	@Test(	description = "Remove an attachment from a mail",
			groups = { "functional" })
	public void RemoveAttachment_01() throws HarnessException {

		//-- Data Setup
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email05/mime01.txt";
		final String subject = "subject151615738";
		final String attachmentname = "file.txt";
		
		// Inject the message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Double check that there is an attachment
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
		String id = app.zGetActiveAccount().soapSelectValue("//mail:m", "id");
		
		app.zGetActiveAccount().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail' >"
			+		"<m id='"+ id +"'/>"
			+	"</GetMsgRequest>");
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:mp[@cd='attachment']");
		ZAssert.assertGreaterThan(nodes.length, 0, "Verify the message has the attachment");


		//-- GUI actions
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		AttachmentItem item = null;
		List<AttachmentItem> items = display.zListGetAttachments();
		for (AttachmentItem i : items) {
			if ( i.getAttachmentName().equals(attachmentname)) {
				item = i;
				break;
			}
		}
		ZAssert.assertNotNull(item, "Verify one attachment is in the message");
		
		// Click remove
		DialogWarning dialog = (DialogWarning)display.zListAttachmentItem(Button.B_REMOVE, item);
		dialog.zClickButton(Button.B_YES);

		
		//-- Verification

		// Verify the message no longer has an attachment
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:("+ subject +")</query>"
			+	"</SearchRequest>");
		id = app.zGetActiveAccount().soapSelectValue("//mail:m", "id");
		
		app.zGetActiveAccount().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail' >"
			+		"<m id='"+ id +"'/>"
			+	"</GetMsgRequest>");
		nodes = app.zGetActiveAccount().soapSelectNodes("//mail:mp[@cd='attachment']");
		ZAssert.assertEquals(nodes.length, 0, "Verify the message no longer has the attachment");
		

	}


}
