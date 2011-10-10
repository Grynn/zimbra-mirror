package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;

public class RenameFolder extends OctopusCommonTest {

	public RenameFolder() {
		logger.info("New " + RenameFolder.class.getCanonicalName());

		// All tests start at the Briefcase page
		super.startingPage = app.zPageMyFiles;
		super.startingAccountPreferences = null;

	}

	@Test(description = "Rename folder using context menu", groups = { "smoke" })
	public void RenameFolder_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		ZAssert.assertNotNull(briefcaseRootFolder,
				"Verify the Briefcase root folder is available");

		// Create the sub-folder
		String subFolderName = "folder"
				+ ZimbraSeleniumProperties.getUniqueString();

		account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + subFolderName + "' l='"
				+ briefcaseRootFolder.getId() + "' view='document'/>"
				+ "</CreateFolderRequest>");

		// Verify the sub-folder exists on the server
		FolderItem subFolder = FolderItem
				.importFromSOAP(account, subFolderName);
		ZAssert.assertNotNull(subFolder, "Verify the subfolder is available");

		// click on My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		SleepUtil.sleepVerySmall();

		// rename folder using context menu
		app.zPageMyFiles.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
				Button.O_RENAME, subFolderName);

		String newSubFolderName = "newSubFolderName"
				+ ZimbraSeleniumProperties.getUniqueString();

		app.zPageOctopus.rename(newSubFolderName);

		// click on My Files tab
		PageMyFiles pageMyFiles = (PageMyFiles) app.zPageOctopus
				.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Verify the new folder in the My Files list view
		ZAssert.assertTrue(pageMyFiles.zWaitForElementPresent(
				PageMyFiles.Locators.zMyFilesListViewItems.locator
						+ ":contains(" + newSubFolderName + ")", "3000"),
				"Verify the new folder is displayed in the My Files list view");
	}
}
