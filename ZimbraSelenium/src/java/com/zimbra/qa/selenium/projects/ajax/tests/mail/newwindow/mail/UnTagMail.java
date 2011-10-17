package com.zimbra.qa.selenium.projects.ajax.tests.mail.newwindow.mail;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.SeparateWindowDisplayMail;

public class UnTagMail extends PrefGroupMailByMessageTest {

	public UnTagMail() {
		logger.info("New " + UnTagMail.class.getCanonicalName());


	}
	@Bugs(ids="65769")
	@Test(	description = "Un-Tag a message using Toolbar -> Tag -> Remove Tag - in a separate window", 
			groups = { "deprecated" })
	public void UnTagMail_01() throws HarnessException {

		// Create the tag to delete
		String tagname = "tag" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
					"<CreateTagRequest xmlns='urn:zimbraMail'>"
				+	  	"<tag name='"+ tagname +"' color='1' />"
				+	"</CreateTagRequest>");

		TagItem tag = TagItem.importFromSOAP(app.zGetActiveAccount(), tagname);
		ZAssert.assertNotNull(tag, "Verify the tag was created");

		
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();

		// Add a message to the mailbox
		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		app.zGetActiveAccount().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
				+		"<m l='" + inboxFolder.getId() + "' t='"+ tag.getId() +"'>"
				+			"<content>"
				+				"From: foo@foo.com\n"
				+				"To: foo@foo.com \n"
				+				"Subject: " + subject + "\n"
				+				"MIME-Version: 1.0 \n"
				+				"Content-Type: text/plain; charset=utf-8 \n"
				+				"Content-Transfer-Encoding: 7bit\n"
				+				"\n"
				+				"simple text string in the body\n" 
				+			"</content>"
				+		"</m>"
				+	"</AddMsgRequest>");

		// Get the message data from SOAP
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:(" + subject + ")");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);

		SeparateWindowDisplayMail window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (SeparateWindowDisplayMail)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			window.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG);
			
			window.zCloseWindow();
			window = null;

		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}
		


		// Make sure the tag was applied to the message
		app.zGetActiveAccount().soapSend(
						"<GetMsgRequest xmlns='urn:zimbraMail'>"
				+			"<m id='" + mail.getId() + "'/>"
				+		"</GetMsgRequest>");
		String mailTags = app.zGetActiveAccount().soapSelectValue("//mail:m", "t");

		ZAssert.assertEquals(mailTags, "", "Verify the tag appears on the message");

	}

}
