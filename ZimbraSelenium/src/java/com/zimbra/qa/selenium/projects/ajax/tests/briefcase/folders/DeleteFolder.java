package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class DeleteFolder extends AjaxCommonTest {

	public DeleteFolder() {
		logger.info("New "+ DeleteFolder.class.getCanonicalName());
		
		// All tests start at the Briefcase page
		super.startingPage = app.zPageBriefcase;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Delete a folder - Right click, Delete",
			groups = { "smoke" })
	public void DeleteFolder_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);
		
		ZAssert.assertNotNull(briefcaseRootFolder, "Verify the Briefcase root folder is available");
		
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		ZAssert.assertNotNull(trash, "Verify the trash is available");
		
		// Create the sub-folder
		String briefcaseSubFolderName = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ briefcaseSubFolderName +"' l='"+ briefcaseRootFolder.getId() +"'/>" +
                "</CreateFolderRequest>");

		FolderItem briefcaseSubFolder = FolderItem.importFromSOAP(account, briefcaseSubFolderName);
		ZAssert.assertNotNull(briefcaseSubFolder, "Verify the subfolder is available");
		
		
		// refresh the Briefcase tree folder list
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseRootFolder,
				false);

		// Delete the folder using context menu
		app.zTreeBriefcase.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_DELETE, briefcaseSubFolder);
		
		
		// Verify the folder is now in the trash
		briefcaseSubFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), briefcaseSubFolderName);
		ZAssert.assertNotNull(briefcaseSubFolder, "Verify the subfolder is again available");
		ZAssert.assertEquals(trash.getId(), briefcaseSubFolder.getParentId(), "Verify the subfolder's parent is now the trash folder ID");		
	}	


}
