package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.files;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageOctopus;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles.Locators;

public class FileContextMenu extends OctopusCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;
	private boolean _fileAttached = false;
	private String _fileId = null;

	public FileContextMenu() {
		logger.info("New " + FileContextMenu.class.getCanonicalName());

		// test starts at the My Files tab
		super.startingPage = app.zPageMyFiles;
		super.startingAccountPreferences = null;
	}

	@BeforeMethod(groups = { "always" })
	public void testReset() {
		_folderName = null;
		_folderIsCreated = false;
		_fileId = null;
		_fileAttached = false;
	}

	@Test(description = "Verify the Context menu items in the File drop down menu", groups = { "sanity" })
	public void FileContextMenu_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/putty.log";

		FileItem file = new FileItem(filePath);

		String fileName = file.getName();

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to the root folder through SOAP
		account.soapSend(

		"<SaveDocumentRequest xmlns='urn:zimbraMail'>" +

		"<doc l='" + briefcaseRootFolder.getId() + "'>" +

		"<upload id='" + attachmentId + "'/>" +

		"</doc>" +

		"</SaveDocumentRequest>");

		_fileAttached = true;
		_fileId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// verify the file is uploaded
		ZAssert.assertNotNull(_fileId, "Verify file is uploaded");

		// click on My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Click on list item drop down menu
		app.zPageMyFiles.zListItem(Action.A_LEFTCLICK,
				Button.B_MY_FILES_LIST_ITEM, fileName);

		// Verify the items in the list file context menu

		// Verify Share item is present
		ZAssert.assertTrue(
				app.zPageMyFiles.sIsElementPresent(PageOctopus.Locators.zShareItem.locator),
				"Verify Share item is present");

		// Verify Favorite item is present
		ZAssert.assertTrue(app.zPageMyFiles
				.sIsElementPresent(PageOctopus.Locators.zFavoriteItem.locator),
				"Verify Favorite item is present");

		// Verify Download item is present
		ZAssert.assertTrue(app.zPageMyFiles
				.sIsElementPresent(Locators.zDownloadItem.locator),
				"Verify Download item is present");

		// Verify Rename item is present
		ZAssert.assertTrue(app.zPageMyFiles
				.sIsElementPresent(PageOctopus.Locators.zRenameItem.locator),
				"Verify Rename item is present");

		// Verify Move item is present
		ZAssert.assertTrue(
				app.zPageMyFiles.sIsElementPresent(PageOctopus.Locators.zMoveItem.locator),
				"Verify Move item is present");

		// Verify Delete item is present
		ZAssert.assertTrue(app.zPageMyFiles
				.sIsElementPresent(PageOctopus.Locators.zDeleteItem.locator),
				"Verify Delete item is present");

		// click on My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);
	}

	@AfterMethod(groups = { "always" })
	public void testCleanup() {
		if (_fileAttached && _fileId != null) {
			try {
				// Delete it from Server
				app.zPageOctopus.deleteItemUsingSOAP(_fileId,
						app.zGetActiveAccount());
			} catch (Exception e) {
				logger.info("Failed while deleting the file", e);
			} finally {
				_fileId = null;
				_fileAttached = false;
			}
		}
		if (_folderIsCreated) {
			try {
				// Delete it from Server
				FolderItem
						.deleteUsingSOAP(app.zGetActiveAccount(), _folderName);
			} catch (Exception e) {
				logger.info("Failed while removing the folder.", e);
			} finally {
				_folderName = null;
				_folderIsCreated = false;
			}
		}
	}
}
