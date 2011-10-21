package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.mountpoints;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class DeleteMountpoint extends AjaxCommonTest {

	private ZimbraAccount Owner = null;
	
	
	public DeleteMountpoint() {
		logger.info("New "+ DeleteMountpoint.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;

		
		Owner = new ZimbraAccount();
		Owner.provision();
		Owner.authenticate();
		
	}
	
	@Test(	description = "Delete a mountpoint to a shared addressbook (right click -> Delete)",
			groups = { "smoke" })
	public void DeleteMountpoint_01() throws HarnessException {
		
		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerfolder"+ ZimbraSeleniumProperties.getUniqueString();
		
		Owner.soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + ownerFoldername +"' l='1' view='contact'/>"
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
				+		"<link l='1' name='"+ mountpointFoldername +"' view='contact' rid='"+ ownerFolder.getId() +"' zid='"+ Owner.ZimbraId +"'/>"
				+	"</CreateMountpointRequest>");
		
		FolderMountpointItem mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointFoldername);
		ZAssert.assertNotNull(mountpoint, "Verify the subfolder is available");


		
		
		
		// Click Get Mail button
		app.zPageAddressbook.zToolbarPressButton(Button.B_REFRESH);
		
		// Delete the folder using context menu
		app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, mountpoint);
		
		
		// Verify the folder is now in the trash
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointFoldername);
		ZAssert.assertNotNull(mountpoint, "Verify the subfolder is again available");
		ZAssert.assertEquals(trash.getId(), mountpoint.getParentId(), "Verify the subfolder's parent is now the trash folder ID");
		
	}

	
	

	

}
