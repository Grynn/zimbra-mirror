package com.zimbra.qa.selenium.projects.ajax.tests.mail.newwindow.mountpoints;


import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.SeparateWindowDialog;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.SeparateWindowDisplayMail;


public class CreateMountpoint extends PrefGroupMailByMessageTest {

	private ZimbraAccount Owner = null;
	
	
	public CreateMountpoint() {
		logger.info("New "+ CreateMountpoint.class.getCanonicalName());
		
		
		Owner = new ZimbraAccount();
		Owner.provision();
		Owner.authenticate();
		
	}
	
	@Test(	description = "Receive an invitation to a shared folder, accept it - in a separate window",
			groups = { "smoke" })
	public void CreateMountpoint_01() throws HarnessException {
		
		// Owner creates a folder, shares it with current user, and sends invitation
		String ownerFoldername = "ownerfolder"+ ZimbraSeleniumProperties.getUniqueString();
		
		FolderItem ownerInbox = FolderItem.importFromSOAP(Owner, FolderItem.SystemFolder.Inbox);
		ZAssert.assertNotNull(ownerInbox, "Verify the new owner folder exists");

		Owner.soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + ownerFoldername +"' l='" + ownerInbox.getId() +"'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem ownerFolder = FolderItem.importFromSOAP(Owner, ownerFoldername);
		ZAssert.assertNotNull(ownerFolder, "Verify the new owner folder exists");
		
		Owner.soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ ownerFolder.getId() +"' op='grant'>"
				+			"<grant d='" + app.zGetActiveAccount().EmailAddress + "' gt='usr' perm='r'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		

		String shareMessageSubject = "shared"+ ZimbraSeleniumProperties.getUniqueString();
		String shareElement = String.format(
					"<share xmlns='urn:zimbraShare' version='0.1' action='new' >"
				+		"<grantee id='%s' email='%s' name='%s' />"
				+		"<grantor id='%s' email='%s' name='%s' />"
				+		"<link id='%s' name='%s' view='message' perm='r' />"
				+		"<notes/>"
				+	"</share>",
					app.zGetActiveAccount().ZimbraId, app.zGetActiveAccount().EmailAddress, app.zGetActiveAccount().EmailAddress,
					Owner.ZimbraId, Owner.EmailAddress, Owner.EmailAddress,
					ownerFolder.getId(), ownerFolder.getName());
					
		Owner.soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>"
				+			"<su>"+ shareMessageSubject +"</su>"
				+			"<mp ct='multipart/alternative'>"
				+				"<mp ct='text/plain'>"
				+					"<content>shared</content>"
				+				"</mp>"
				+				"<mp ct='xml/x-zimbra-share'>"
				+					"<content>"+ XmlStringUtil.escapeXml(shareElement) +"</content>"
				+				"</mp>"
				+			"</mp>"
				+		"</m>"
				+	"</SendMsgRequest>");




		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, shareMessageSubject);

		
		
		SeparateWindowDisplayMail window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (SeparateWindowDisplayMail)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(shareMessageSubject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			// Verify that the A/D buttons are displayed
			ZAssert.assertTrue(window.zHasShareADButtons(), "Verify that the Accept/Decline share buttons are present");
			
			// Accept the share, which opens a dialog
			SeparateWindowDialog dialog = (SeparateWindowDialog)window.zPressButton(Button.B_ACCEPT_SHARE);
			ZAssert.assertNotNull(dialog, "Verify that the accept share dialog opens");
			
			// Click OK on the dialog
			dialog.zClickButton(Button.B_YES);

			// The dialog will send a message, so wait for delivery
			Stafpostqueue sp = new Stafpostqueue();
			sp.waitForPostqueue();

		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}
		
		
		
		FolderItem found = null;
		
		// Verify that the new mountpoint is present
		logger.info("Looking for mountpoint containing text: "+ ownerFoldername);

		List<FolderItem> folders = app.zTreeMail.zListGetFolders();
		for (FolderItem f : folders) {
			if ( f.getName().contains(ownerFoldername) ) {
				logger.info("Found folder item: "+ f.getName());
				found = f;
				break;
			}
		}
		
		ZAssert.assertNotNull(found, "Verify the mountpoint is in the folder list");
		
	}

	
	

	

}
