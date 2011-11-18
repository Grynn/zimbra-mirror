package com.zimbra.qa.selenium.projects.ajax.tests.tasks.mountpoints;


import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderMountpointItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class MoveMountpoint extends AjaxCommonTest {

	private ZimbraAccount Owner = null;
	
	
	@SuppressWarnings("serial")
	public MoveMountpoint() {
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
	
	@Test(	description = "Move a mountpoint under a subfolder - Drag and Drop, Move",
			groups = { "smoke" })
	public void MoveMountpoint_01() throws HarnessException {
		
		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerfolder"+ ZimbraSeleniumProperties.getUniqueString();
		
	//	FolderItem ownerInbox = FolderItem.importFromSOAP(Owner, FolderItem.SystemFolder.Inbox);
		FolderItem ownerTask = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		ZAssert.assertNotNull(ownerTask, "Verify the new owner folder exists");

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
		

		// Current user creates the mountpoint that points to the share
		String mountpointFoldername = "mountpoint"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
					"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointFoldername +"' view='task' rid='"+ ownerFolder.getId() +"' zid='"+ Owner.ZimbraId +"'/>"
				+	"</CreateMountpointRequest>");
		
		FolderMountpointItem mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointFoldername);
		ZAssert.assertNotNull(mountpoint, "Verify the subfolder is available");

		// Current user creates a subfolder for the move destination
		FolderItem task = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);

		String foldername = "subfolder" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ foldername +"' l='"+ task.getId() +"'/>" +
                "</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Move the folder using drag and drop
		
		app.zPageMail.zDragAndDrop(
				"//td[contains(@id, 'zti__main_Tasks__" + mountpoint.getId() + "_textCell') and contains(text(), '"+ mountpointFoldername + "')]",
				"//td[contains(@id, 'zti__main_Tasks__" + folder.getId() + "_textCell') and contains(text(),'"+ foldername + "')]");

		
		// Verify the mountpoint is now in the other subfolder
		mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointFoldername);
		ZAssert.assertNotNull(mountpoint, "Verify the mountpoint is again available");
		ZAssert.assertEquals(folder.getId(), mountpoint.getParentId(), "Verify the mountpoint's parent is now the other subfolder");
		
	}	
	

	

}
