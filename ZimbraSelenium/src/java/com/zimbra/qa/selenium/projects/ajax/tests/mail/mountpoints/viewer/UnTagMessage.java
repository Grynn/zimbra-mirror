package com.zimbra.qa.selenium.projects.ajax.tests.mail.mountpoints.viewer;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class UnTagMessage extends PrefGroupMailByMessageTest {

	public UnTagMessage() {
		logger.info("New "+ UnTagMessage.class.getCanonicalName());
		
		
		

		
	}
	
	@Test(	description = "Verify Permission Denied on Un Tag a shared mail (read-only share)",
			groups = { "functional" })
	public void UnTagMessage_01() throws HarnessException {
		
		
		//-- DATA Setup
		//
		
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String mountpointname = "mountpoint" + ZimbraSeleniumProperties.getUniqueString();
		
		// Create Tag
		TagItem tag = TagItem.CreateUsingSoap(ZimbraAccount.AccountA());
		ZAssert.assertNotNull(tag, "Verify the tag was created");
		

		FolderItem inbox = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), FolderItem.SystemFolder.Inbox);
		
		// Create a folder to share
		ZimbraAccount.AccountA().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" + inbox.getId() + "'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem folder = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), foldername);
		
		// Share it
		ZimbraAccount.AccountA().soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ folder.getId() +"' op='grant'>"
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='r'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		
		// Add a message to it
		ZimbraAccount.AccountA().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
        		+		"<m l='"+ folder.getId() +"' t='"+ tag.getId() +"' >"
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
		
		MailItem mail = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject +")");

		
		// Mount it
		app.zGetActiveAccount().soapSend(
					"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointname +"'  rid='"+ folder.getId() +"' zid='"+ ZimbraAccount.AccountA().ZimbraId +"'/>"
				+	"</CreateMountpointRequest>");
		
		FolderMountpointItem mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointname);

		
		//-- GUI Actions
		//
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Click on the mountpoint
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, mountpoint);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
		
		
		
		//-- VERIFICATION
		//
		
		// Make sure the tag icon is disabled
		String locator = "css=div#zb__TV-main__TAG_MENU div.ImgTag.ZDisabledImage";
		boolean disabled = app.zPageMail.sIsElementPresent(locator);
		ZAssert.assertTrue(disabled, "Verify that the tag menu is disabled: "+ locator);
		
	}

	
	@Test(	description = "Verify Permission Denied on Un Tag (keyboard='u') a shared mail (read-only share)",
			groups = { "functional" })
	public void UnTagMessage_02() throws HarnessException {
		
		
		//-- DATA Setup
		//
		
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String mountpointname = "mountpoint" + ZimbraSeleniumProperties.getUniqueString();
		
		// Create Tag
		TagItem tag = TagItem.CreateUsingSoap(ZimbraAccount.AccountA());
		ZAssert.assertNotNull(tag, "Verify the tag was created");
		
		FolderItem inbox = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), FolderItem.SystemFolder.Inbox);
		
		// Create a folder to share
		ZimbraAccount.AccountA().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" + inbox.getId() + "'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem folder = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), foldername);
		
		// Share it
		ZimbraAccount.AccountA().soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ folder.getId() +"' op='grant'>"
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='r'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		
		// Add a message to it
		ZimbraAccount.AccountA().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
        		+		"<m l='"+ folder.getId() +"' t='" + tag.getId() +"'>"
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
		
		MailItem mail = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject +")");

		// Mount it
		app.zGetActiveAccount().soapSend(
					"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointname +"'  rid='"+ folder.getId() +"' zid='"+ ZimbraAccount.AccountA().ZimbraId +"'/>"
				+	"</CreateMountpointRequest>");
		
		FolderMountpointItem mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointname);

		
		
		//-- GUI Actions
		//
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Click on the mountpoint
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, mountpoint);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
		
		// Un Tag the item (shortcut)
		app.zPageMail.zKeyboardShortcut(Shortcut.S_MAIL_REMOVETAG);


		
		//-- VERIFICATION
		//
		
		// A "Permission Denied" error popup should occur
		DialogError dialog = app.zPageMain.zGetErrorDialog(DialogError.DialogErrorID.Zimbra);
		ZAssert.assertNotNull(dialog, "Verify the PERM DENIED Error Dialog is created");
		ZAssert.assertTrue(dialog.zIsActive(), "Verify the PERM DENIED Error Dialog is active");
		dialog.zClickButton(Button.B_OK);

				

		
	}


}
