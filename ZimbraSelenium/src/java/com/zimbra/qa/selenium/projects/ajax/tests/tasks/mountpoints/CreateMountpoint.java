package com.zimbra.qa.selenium.projects.ajax.tests.tasks.mountpoints;

import java.util.HashMap;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogShareAccept;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;

public class CreateMountpoint extends AjaxCommonTest{
private ZimbraAccount Owner = null;
	
	
	@SuppressWarnings("serial")
	public CreateMountpoint() {
		logger.info("New "+ CreateMountpoint.class.getCanonicalName());
		super.startingPage = app.zPageTasks;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefReadingPaneLocation", "bottom");
				put("zimbraPrefGroupMailBy", "message");
			}
		};		
		
		Owner = new ZimbraAccount();
		Owner.provision();
		Owner.authenticate();
		
	}
	
	@Test(	description = "Receive an invitation to a shared folder, accept it.",
			groups = { "smoke" })
	public void CreateMountpoint_01() throws HarnessException {
		
		FolderItem ownerTask = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);

		// Create the subTaskList
		String ownerFoldername = "ownertaskList" + ZimbraSeleniumProperties.getUniqueString();
		
		// Owner creates a folder, shares it with current user, and sends invitation
		Owner.soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + ownerFoldername +"' l='" + ownerTask.getId() +"'/>"
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
				+		"<link id='%s' name='%s' view='task' perm='r' />"
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



		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		app.zPageMail.zNavigateTo();

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Click the inbox
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);
				
		// Select the item
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, shareMessageSubject);
		
		// Verify that the A/D buttons are displayed
		ZAssert.assertTrue(display.zHasShareADButtons(), "Verify that the Accept/Decline share buttons are present");
		
		// Accept the share, which opens a dialog
		DialogShareAccept dialog = (DialogShareAccept)display.zPressButton(Button.B_ACCEPT_SHARE);
		ZAssert.assertNotNull(dialog, "Verify that the accept share dialog opens");
		
		// Click OK on the dialog
		dialog.zClickButton(Button.B_YES);
		
				
		// Verify that the new mountpoint is present
		FolderItem folder = FolderItem.importFromSOAP(Owner,ownerFoldername);
		logger.info("Looking for mountpoint containing text: "+ folder.getName());
		
		ZAssert.assertNotNull(folder, "Verify the mountpoint is in the folder list");
		ZAssert.assertEquals(folder.getName(), ownerFoldername,"Verify the server and client folder names match");
		
	}


}
