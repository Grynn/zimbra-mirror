package com.zimbra.qa.selenium.projects.ajax.tests.tasks.mountpoints;


import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderMountpointItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder.FolderColor;


public class ChangeColorMountpoint extends PrefGroupMailByMessageTest {

	private ZimbraAccount Owner = null;
	
	
	@SuppressWarnings("serial")
	public ChangeColorMountpoint() {
		logger.info("New " + ChangeColorMountpoint.class.getCanonicalName());

		super.startingPage = app.zPageTasks;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefReadingPaneLocation", "bottom");
				
			}
		};

		Owner = new ZimbraAccount();
		Owner.provision();
		Owner.authenticate();

	}
	
	@Test(	description = "Edit a tasklist, change the color (Context menu -> Edit)",
			groups = { "functional" })
	public void ChangeColorMountpoint_01() throws HarnessException {
		
		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerfolder"+ ZimbraSeleniumProperties.getUniqueString();

		
		FolderItem ownerTask = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);
		ZAssert.assertNotNull(ownerTask, "Verify the new owner folder exists");

		Owner.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + ownerFoldername + "' l='"
				+ ownerTask.getId() + "'/>" + "</CreateFolderRequest>");

		FolderItem ownerFolder = FolderItem.importFromSOAP(Owner,ownerFoldername);
		ZAssert.assertNotNull(ownerFolder,"Verify the new owner folder exists");

		Owner.soapSend("<FolderActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + ownerFolder.getId() + "' op='grant'>"
				+ "<grant d='" + app.zGetActiveAccount().EmailAddress
				+ "' gt='usr' perm='r'/>" + "</action>"
				+ "</FolderActionRequest>");

		// Current user creates the mountpoint that points to the share
		String mountpointFoldername = "mountpoint"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
						+ "<link l='1' name='" + mountpointFoldername
						+ "' view='task' rid='" + ownerFolder.getId()
						+ "' zid='" + Owner.ZimbraId + "'/>"
						+ "</CreateMountpointRequest>");

		FolderMountpointItem mountpoint = FolderMountpointItem.importFromSOAP(
				app.zGetActiveAccount(), mountpointFoldername);
		ZAssert.assertNotNull(mountpoint, "Verify the subfolder is available");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Rename the folder using context menu
		DialogEditFolder dialog = 
			(DialogEditFolder) app.zTreeTasks.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, mountpoint);
		ZAssert.assertNotNull(dialog, "Verify the dialog opened");

		// Change the color, click OK
		dialog.zSetNewColor(FolderColor.Gray);
		dialog.zClickButton(Button.B_OK);

		// Check the color
		app.zGetActiveAccount().soapSend("<GetFolderRequest xmlns='urn:zimbraMail'/>");

		String color = app.zGetActiveAccount().soapSelectValue("//mail:link[@name='" + mountpoint.getName() + "']", "color");
		ZAssert.assertEquals(color, "8", "Verify the color of the folder is set to gray (8)");
		
	}	

}
