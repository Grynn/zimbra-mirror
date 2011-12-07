package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.folders;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogMove;
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
		// One folder to Move & Another folder to move into
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
		app.zPageOctopus.moveItemUsingSOAP(subFolderItem1.getId(),
				subFolderItem2.getId(), account);

		// click on destination sub-folder
		app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, subFolderName2);

		// Verify the first sub-folder is now in the destination folder
		ZAssert.assertTrue(
				app.zPageOctopus.zIsItemInCurentListView(subFolderName1),
				"Verify the first sub-folder was moved to the destination folder");
	}

	@Test(description = "Move folder using context menu - verify folder is moved", groups = { "smoke" })
	public void MoveFolder_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create two briefcase sub-folders:
		// One folder to Move & Another folder to move into
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

		// move file using right click context menu
		DialogMove chooseFolder = (DialogMove) app.zPageMyFiles
				.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
						Button.O_MOVE, subFolderName1);

		// Double click to choose folder
		chooseFolder.zDoubleClickTreeFolder(subFolderName2);

		// Verify the moved file disappears from My Files tab
		ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementDeleted(
				PageMyFiles.Locators.zMyFilesListView.locator + ":contains("
						+ subFolderName1 + ")", "3000"),
				"Verify the moved file disappears from My Files tab");

		// click on destination sub-folder
		app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, subFolderName2);

		// Verify the first sub-folder is now in the destination folder
		ZAssert.assertTrue(
				app.zPageOctopus.zIsItemInCurentListView(subFolderName1),
				"Verify the first sub-folder was moved to the destination folder");
	}

	@AfterMethod(groups = { "always" })
	public void testCleanup() {
		if (_folderIsCreated) {
			try {
				// Delete it from Server
				FolderItem
						.deleteUsingSOAP(app.zGetActiveAccount(), _folderName);
			} catch (Exception e) {
				logger.info("Failed while removing the folder.");
				e.printStackTrace();
			} finally {
				_folderName = null;
				_folderIsCreated = false;
			}
			try {
				// Refresh view 
				//ZimbraAccount account = app.zGetActiveAccount();
				//FolderItem item = FolderItem.importFromSOAP(account,SystemFolder.Briefcase);
				//account.soapSend("<GetFolderRequest xmlns='urn:zimbraMail'><folder l='1' recursive='0'/>" + "</GetFolderRequest>");
				//account.soapSend("<GetFolderRequest xmlns='urn:zimbraMail' requestId='folders' depth='1' tr='true' view='document'><folder l='" + item.getId() + "'/></GetFolderRequest>");
				//account.soapSend("<GetActivityStreamRequest xmlns='urn:zimbraMail' id='16'/>");
				//app.zGetActiveAccount().accountIsDirty = true;
				//app.zPageOctopus.sRefresh();
													
				// Empty trash
				app.zPageTrash.emptyTrashUsingSOAP(app.zGetActiveAccount());
				
				app.zPageOctopus.zLogout();
			} catch (Exception e) {
				logger.info("Failed while emptying Trash");
				e.printStackTrace();
			}
		}
	}
}
