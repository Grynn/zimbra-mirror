package com.zimbra.qa.selenium.projects.octopus.tests.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;

public class DeleteFolder extends OctopusCommonTest {

	public DeleteFolder() {
		logger.info("New " + DeleteFolder.class.getCanonicalName());

		// All tests start at the Briefcase page
		super.startingPage = app.zPageOctopus;
		super.startingAccountPreferences = null;

	}

	@Test(description = "Delete a sub-folder using drop down list option", groups = { "smoke" })
	public void DeleteFolder_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		ZAssert.assertNotNull(briefcaseRootFolder,
				"Verify the Briefcase root folder is available");

		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(),
				SystemFolder.Trash);
		ZAssert.assertNotNull(trash, "Verify the trash is available");

		// Create the sub-folder
		String briefcaseSubFolderName = "folder"
				+ ZimbraSeleniumProperties.getUniqueString();

		account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + briefcaseSubFolderName + "' l='"
				+ briefcaseRootFolder.getId() + "' view='document'/>"
				+ "</CreateFolderRequest>");

		FolderItem briefcaseSubFolder = FolderItem.importFromSOAP(account,
				briefcaseSubFolderName);
		ZAssert.assertNotNull(briefcaseSubFolder,
				"Verify the subfolder is available");
		
		// Delete the folder using drop down list option
		app.zPageOctopus.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM, Button.O_DELETE, briefcaseSubFolder);

		// Verify the folder is now in the trash
		for(int i = 0; i<5; i++){
			briefcaseSubFolder = FolderItem.importFromSOAP(account,
					briefcaseSubFolderName);
			if(trash.getId().contentEquals(briefcaseSubFolder.getParentId()))
				break;
			SleepUtil.sleepVerySmall();
		}
		
		ZAssert.assertNotNull(briefcaseSubFolder,
				"Verify the subfolder is again available");
		ZAssert.assertEquals(trash.getId(), briefcaseSubFolder.getParentId(),
				"Verify the subfolder's parent is now the trash folder ID");
	}	
}
