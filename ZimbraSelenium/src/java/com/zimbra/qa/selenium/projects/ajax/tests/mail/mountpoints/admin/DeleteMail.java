package com.zimbra.qa.selenium.projects.ajax.tests.mail.mountpoints.admin;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;


public class DeleteMail extends PrefGroupMailByMessageTest {

	
	public DeleteMail() {
		logger.info("New "+ DeleteMail.class.getCanonicalName());
		
		
	}
	
	@Bugs(ids = "66525, 26103")
	@Test(	description = "Delete a message from a mountpoint folder",
			groups = { "functional" })
	public void DeleteMail_01() throws HarnessException {
		
		
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String mountpointname = "mountpoint" + ZimbraSeleniumProperties.getUniqueString();
		
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
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='rwidxa'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		
		// Add a message to it
		ZimbraAccount.AccountA().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
        		+		"<m l='"+ folder.getId() +"' f='u'>"
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
		
		// Mount it
		app.zGetActiveAccount().soapSend(
					"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointname +"'  rid='"+ folder.getId() +"' zid='"+ ZimbraAccount.AccountA().ZimbraId +"'/>"
				+	"</CreateMountpointRequest>");
		
		FolderMountpointItem mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointname);
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Click on the mountpoint
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, mountpoint);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Click delete
		app.zPageMail.zToolbarPressButton(Button.B_DELETE);

//		// A warning dialog will appear
//		DialogWarning dialog = app.zPageMain.zGetWarningDialog(DialogWarning.DialogWarningID.EmptyFolderWarningMessage);
//		ZAssert.assertNotNull(dialog, "Verify the dialog pops up");
//		dialog.zClickButton(Button.B_OK);

		// Verify the message is now in the local trash
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Trash);
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +") is:anywhere");
		ZAssert.assertNotNull(mail, "Verify the message exists in the mailbox");
		ZAssert.assertEquals(mail.dFolderId, trash.getId(), "Verify the message exists in the local trash folder");
		
		// Verify the message is now in the ownser's trash
		trash = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), FolderItem.SystemFolder.Trash);
		mail = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject +") is:anywhere");
		ZAssert.assertNotNull(mail, "Verify the message exists in the mailbox");
		ZAssert.assertEquals(mail.dFolderId, trash.getId(), "Verify the message exists in the owner's trash folder");

		
	}

	@Bugs(ids = "66525, 26103")
	@Test(	description = "Delete multiple messages from a mountpoint folder",
			groups = { "functional" })
	public void DeleteMail_02() throws HarnessException {
		
		
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		String subject1 = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String subject2 = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String mountpointname = "mountpoint" + ZimbraSeleniumProperties.getUniqueString();
		
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
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='rwidxa'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		
		// Add a message to it
		ZimbraAccount.AccountA().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='"+ folder.getId() +"' f='u'>"
        	+			"<content>From: foo@foo.com\n"
        	+				"To: foo@foo.com \n"
        	+				"Subject: "+ subject1 +"\n"
        	+				"MIME-Version: 1.0 \n"
        	+				"Content-Type: text/plain; charset=utf-8 \n"
        	+				"Content-Transfer-Encoding: 7bit\n"
        	+				"\n"
        	+				"simple text string in the body\n"
        	+			"</content>"
        	+		"</m>"
			+	"</AddMsgRequest>");
	
		ZimbraAccount.AccountA().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='"+ folder.getId() +"' f='u'>"
        	+			"<content>From: foo@foo.com\n"
        	+				"To: foo@foo.com \n"
        	+				"Subject: "+ subject2 +"\n"
        	+				"MIME-Version: 1.0 \n"
        	+				"Content-Type: text/plain; charset=utf-8 \n"
        	+				"Content-Transfer-Encoding: 7bit\n"
        	+				"\n"
        	+				"simple text string in the body\n"
        	+			"</content>"
        	+		"</m>"
			+	"</AddMsgRequest>");
	
		// Mount it
		app.zGetActiveAccount().soapSend(
					"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointname +"'  rid='"+ folder.getId() +"' zid='"+ ZimbraAccount.AccountA().ZimbraId +"'/>"
				+	"</CreateMountpointRequest>");
		
		FolderMountpointItem mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointname);
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Click on the mountpoint
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, mountpoint);

		// Select the item
		app.zPageMail.zListItem(Action.A_MAIL_CHECKBOX, subject1);
		app.zPageMail.zListItem(Action.A_MAIL_CHECKBOX, subject2);

		// Click delete
		app.zPageMail.zToolbarPressButton(Button.B_DELETE);

//		// A warning dialog will appear
//		DialogWarning dialog = app.zPageMain.zGetWarningDialog(DialogWarning.DialogWarningID.EmptyFolderWarningMessage);
//		ZAssert.assertNotNull(dialog, "Verify the dialog pops up");
//		dialog.zClickButton(Button.B_OK);

		// Verify the message is now in the local trash
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Trash);
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject1 +") is:anywhere");
		ZAssert.assertNotNull(mail, "Verify the message exists in the mailbox");
		ZAssert.assertEquals(mail.dFolderId, trash.getId(), "Verify the message exists in the local trash folder");
		mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject2 +") is:anywhere");
		ZAssert.assertNotNull(mail, "Verify the message exists in the mailbox");
		ZAssert.assertEquals(mail.dFolderId, trash.getId(), "Verify the message exists in the local trash folder");
		
		// Verify the message is now in the ownser's trash
		trash = FolderItem.importFromSOAP(ZimbraAccount.AccountA(), FolderItem.SystemFolder.Trash);
		mail = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject1 +") is:anywhere");
		ZAssert.assertNotNull(mail, "Verify the message exists in the mailbox");
		ZAssert.assertEquals(mail.dFolderId, trash.getId(), "Verify the message exists in the owner's trash folder");
		mail = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject2 +") is:anywhere");
		ZAssert.assertNotNull(mail, "Verify the message exists in the mailbox");
		ZAssert.assertEquals(mail.dFolderId, trash.getId(), "Verify the message exists in the owner's trash folder");

		
	}


}
