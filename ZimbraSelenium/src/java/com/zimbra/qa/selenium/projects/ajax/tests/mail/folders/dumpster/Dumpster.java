package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders.dumpster;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogMove;
import com.zimbra.qa.selenium.projects.ajax.ui.FormRecoverDeletedItems;
import com.zimbra.qa.selenium.projects.ajax.ui.FormRecoverDeletedItems.Field;


public class Dumpster extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public Dumpster() {
		logger.info("New "+ Dumpster.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = new HashMap<String, String>() {{
			put("zimbraDumpsterEnabled", "TRUE");
		}};

	}

	@Test(
			description = "Verify the Trash folder's context menu does not contain dumpster",
			groups = { "functional" })
	public void RecoverItems_01() throws HarnessException {

		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		String foldername = "subfolder" + ZimbraSeleniumProperties.getUniqueString();

		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Trash);

		// Create a subfolder (to recover the dumpster item to)
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
					"<folder name='" + foldername +"' l='"+ inbox.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		// Add an message 
		app.zGetActiveAccount().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>" 
				+		"<m l='" + inbox.getId() + "'>"
				+			"<content>"
				+				"From: foo@foo.com\n" + "To: foo@foo.com \n"
				+				"Subject: " + subject + "\n" + "MIME-Version: 1.0 \n"
				+				"Content-Type: text/plain; charset=utf-8 \n"
				+				"Content-Transfer-Encoding: 7bit\n" 
				+				"\n"
				+				body +" \n"
				+				"\n"
				+				"\n"
				+			"</content>"
				+		"</m>"
				+	"</AddMsgRequest>");
		
		String messageID = app.zGetActiveAccount().soapSelectValue("//mail:AddMsgResponse//mail:m", "id");
		ZAssert.assertNotNull(messageID, "Verify the messageID exists");
		
		// Delete the message, putting it in the dumpster
		app.zGetActiveAccount().soapSend(
				"<MsgActionRequest xmlns='urn:zimbraMail'>" 
			+		"<action id='"+ messageID +"' op='delete'/>"
			+	"</MsgActionRequest>");
		

		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		
		// Right click on Trash, select "Recover Deleted Items"
		FormRecoverDeletedItems form = 
			(FormRecoverDeletedItems) app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_RECOVER_DELETED_ITEMS, trash);
		ZAssert.assertNotNull(form, "Verify the 'recover deleted items' dialog pops up");

		// Search for the message
		// https://bugzilla.zimbra.com/show_bug.cgi?id=62029
		//
//		form.zFillField(Field.Search, body);
//		form.zToolbarPressButton(Button.B_SEARCH);

		// Click on the message
		form.zListItem(Action.A_LEFTCLICK, subject);
		
		// Click "Recover To" subfolder
		DialogMove dialog = (DialogMove) form.zToolbarPressButton(Button.B_RECOVER_TO);
		dialog.zClickTreeFolder(subfolder);
		dialog.zClickButton(Button.B_OK);

		// Dismiss the 'Recover deleted items' dialog
		form.zToolbarPressButton(Button.B_CLOSE);

		// Verify the message is back
		MailItem message = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertNotNull(message, "Verify the message is returned to the mailbox");


		

	}
}
