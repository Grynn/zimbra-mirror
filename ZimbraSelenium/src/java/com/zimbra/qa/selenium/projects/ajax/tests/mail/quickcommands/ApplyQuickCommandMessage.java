package com.zimbra.qa.selenium.projects.ajax.tests.mail.quickcommands;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxQuickCommandTest;


public class ApplyQuickCommandMessage extends AjaxQuickCommandTest {

	@SuppressWarnings("serial")
	public ApplyQuickCommandMessage() {
		logger.info("New "+ ApplyQuickCommandMessage.class.getCanonicalName());

		
		

		
		super.startingAccountPreferences = new HashMap<String, String>() {{
			put("zimbraPrefGroupMailBy", "message");
		}};

	}

	@Bugs(ids = "71389")	// Hold off on GUI implementation of Quick Commands in 8.X
	@Test(	description = "Apply a Quick Command to a message",
			groups = { "deprectated" })
	public void ApplyQuickCommandMessage_01() throws HarnessException {

		// Create the message data to be sent
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
				+		"<m l='"+ FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox).getId() +"' >"
				+			"<content>From: foo@foo.com\n"
				+				"To: foo@foo.com \n"
				+				"Subject: "+ subject +"\n"
				+				"MIME-Version: 1.0 \n"
				+				"Content-Type: text/plain; charset=utf-8 \n"
				+				"Content-Transfer-Encoding: 7bit\n"
				+				"\n"
				+				"simple text string in the body\n"
				+			"</content>"
				+		"</m>"
				+	"</AddMsgRequest>");

		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertNotNull(mail, "Verify other account's mail is created");

		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);

		// Apply Quick Command #1 to the message
		app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.O_QUICK_COMMANDS_MENU, this.getQuickCommand01().getName());

		
		
		// Make sure the message is flagged, filed, tagged
		mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertStringContains(mail.getFlags(), "f", "Verify the message is flagged in the server");

		// TODO: add tags, folder, unread checks
	}


}
