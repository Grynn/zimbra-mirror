package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.folders;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;

public class MoveFolder extends OctopusCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;

	public MoveFolder() {
		logger.info("New " + MoveFolder.class.getCanonicalName());

		// All tests start at the Briefcase page
		super.startingPage = app.zPageMyFiles;
		super.startingAccountPreferences = null;

	}

	@BeforeMethod(groups = { "always" })
	public void testReset() {
		_folderName = null;
		_folderIsCreated = false;
	}

	@Test(description = "Move folder using soap - verify folder is moved", groups = { "sanity" })
	public void MoveFolder_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create two briefcase sub-folders:
		//One folder to Move & Another folder to move into
		String subFolderName1 = "folder1"
				+ ZimbraSeleniumProperties.getUniqueString();
		String subFolderName2 = "folder2"
				+ ZimbraSeleniumProperties.getUniqueString();

		account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + subFolderName1 + "' l='"
				+ briefcaseRootFolder.getId() + "' view='document'/>"
				+ "</CreateFolderRequest>");

		// Verify the sub-folder exists on the server
		FolderItem subFolderItem1 = FolderItem.importFromSOAP(account,
				subFolderName1);
		ZAssert.assertNotNull(subFolderItem1,
				"Verify the first subfolder is available");

		account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + subFolderName2 + "' l='"
				+ briefcaseRootFolder.getId() + "' view='document'/>"
				+ "</CreateFolderRequest>");

		// Verify the destination sub-folder exists on the server
		FolderItem subFolderItem2 = FolderItem.importFromSOAP(account,
				subFolderName2);
		ZAssert.assertNotNull(subFolderItem2,
				"Verify the second subfolder is available");
		
		_folderName = subFolderName2;
		_folderIsCreated = true;

		// move folder using SOAP
		app.zPageOctopus.moveItemUsingSOAP(subFolderItem1.getId(),subFolderItem2.getId(), account);

		// click on destination sub-folder
		app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, subFolderName2);

		// Verify the first sub-folder is now in the destination folder
		ZAssert.assertTrue(app.zPageOctopus.zIsItemInCurentListView(subFolderName1),
				"Verify the first sub-folder was moved to the destination folder");
	}

	@Test(description = "Move folder using context menu - verify folder is moved", groups = { "oke" })
	public void MoveFolder_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		ZAssert.assertNotNull(briefcaseRootFolder,
				"Verify the Briefcase root folder is available");

		FolderItem trash = FolderItem.importFromSOAP(account,
				SystemFolder.Trash);
		ZAssert.assertNotNull(trash, "Verify the trash is available");

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

		// Delete the folder using drop down list option
		app.zPageMyFiles.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
				Button.O_DELETE, subFolderName);

		// Verify the deleted folder disappears from My Files tab
		ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementDeleted(
				PageMyFiles.Locators.zMyFilesListView.locator + ":contains("
						+ subFolderName + ")", "3000"),
				"Verify the deleted file disappears from My Files tab");

		// Verify the folder is now in the trash
		ZAssert.assertTrue(
				app.zPageOctopus.zIsFolderChild(subFolder, trash.getName()),
				"Verify the deleted folder moved to the trash");

		ZAssert.assertTrue(
				app.zPageOctopus.zIsFolderParent(trash, subFolderName),
				"Verify the subfolder's parent id matches trash folder id");
	}

}
