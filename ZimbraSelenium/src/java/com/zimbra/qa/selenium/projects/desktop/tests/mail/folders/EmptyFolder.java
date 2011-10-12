package com.zimbra.qa.selenium.projects.desktop.tests.mail.folders;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.DialogWarning;


public class EmptyFolder extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public EmptyFolder() {
		logger.info("New "+ EmptyFolder.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageMail;
		//super.startingAccountPreferences = null;
		super.startingAccountPreferences = new HashMap<String, String>() {{
			put("zimbraPrefGroupMailBy", "message");
		}};

	}

	@Test(description = "Empty a folder (context menu)", groups = { "smoke" })
	public void EmptyFolder_01() throws HarnessException {

		String foldername = "folder"
				+ ZimbraSeleniumProperties.getUniqueString();
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();

		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(),
				FolderItem.SystemFolder.Inbox);

		// Create a subfolder in Inbox
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
						+ "<folder name='" + foldername + "' l='"
						+ inbox.getId() + "'/>" + "</CreateFolderRequest>");

		// Click on Get Mail to refresh the folder list
      app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

      // Make sure the folder was created on the server
		FolderItem subfolder = FolderItem.importFromSOAP(
		      app.zGetActiveAccount(),
		      foldername,
		      SOAP_DESTINATION_HOST_TYPE.CLIENT,
		      app.zGetActiveAccount().EmailAddress);

		ZAssert.assertNotNull(subfolder,
				"Verify the folder exists on the server");

		// Add an message to the new subfolder
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" + "<m l='"
						+ subfolder.getId() + "'>"
						+ "<content>From: foo@foo.com\n" + "To: foo@foo.com \n"
						+ "Subject: " + subject + "\n" + "MIME-Version: 1.0 \n"
						+ "Content-Type: text/plain; charset=utf-8 \n"
						+ "Content-Transfer-Encoding: 7bit\n" + "\n"
						+ "simple text string in the body\n" + "</content>"
						+ "</m>" + "</AddMsgRequest>");

		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(),"subject:(" + subject + ")");

		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Right click on folder, select "Mark all as read"
		DialogWarning dialog = (DialogWarning) app.zTreeMail.zTreeItem(
				Action.A_RIGHTCLICK, Button.B_TREE_FOLDER_EMPTY, subfolder);
		ZAssert.assertNotNull(dialog,"Verify the warning dialog pops up - Are you sure you want to delete all items?");

		// Dismiss it
		dialog.zClickButton(Button.B_OK);
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		MailItem found = null;
		for (MailItem m : messages) {
			logger.info("Subject: looking for " + mail.dSubject + " found: "+ m.gSubject);
			if (mail.dSubject.equals(m.gSubject)) {
				found = m;
				break;
			}
		}
		ZAssert.assertNull(found, "Verify the message is no longer exist");

		// Make sure the folder was created on the server
		// MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(),
		// "subject:("+ subject +")");
		// ZAssert.assertNull(mail, "Verify the message no longer exists");

	}
}
