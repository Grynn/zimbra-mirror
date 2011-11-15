package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.files;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;
import com.zimbra.qa.selenium.projects.octopus.ui.PageTrash;

public class RenameFile extends OctopusCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;
	private boolean _fileAttached = false;
	private String _fileId = null;

	public RenameFile() {
		logger.info("New " + RenameFile.class.getCanonicalName());

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

	@Test(description = "Upload file through RestUtil - Rename File using Right Click Context Menu", groups = { "smoke" })
	public void RenameFile_01() throws HarnessException {
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

		// rename file using context menu
		app.zPageMyFiles.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
				Button.O_RENAME, fileName);

		String newFileName = "newFileName"
				+ ZimbraSeleniumProperties.getUniqueString();
		
		app.zPageOctopus.rename(newFileName);
		
		// click on My Files tab
		PageMyFiles pageMyFiles = (PageMyFiles) app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Verify the new file name in the My Files list view
		ZAssert
		.assertTrue(pageMyFiles.zWaitForElementPresent(
				PageMyFiles.Locators.zMyFilesListViewItems.locator
						+ ":contains(" + newFileName
						+ ")", "3000"),
				"Verify the new file is displayed in the My Files list view");

	}

	@AfterMethod(groups = { "always" })
	public void testCleanup() {
		if (_fileAttached && _fileId != null) {
			try {
				// Delete it from Server
				app.zPageOctopus.deleteItemUsingSOAP(_fileId,
						app.zGetActiveAccount());
			} catch (Exception e) {
				logger.info("Failed while deleting the file");
				e.printStackTrace();
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
				logger.info("Failed while removing the folder.");
				e.printStackTrace();
			} finally {
				_folderName = null;
				_folderIsCreated = false;
			}
		}
		try {
			// click on Trash tab to move out from the current view
			PageTrash pageTrash = (PageTrash) app.zPageOctopus.zToolbarPressButton(Button.B_TAB_TRASH);
			
			//Empty trash
			pageTrash.emptyTrashUsingSOAP(app.zGetActiveAccount());
		} catch (Exception e) {
			logger.info("Failed while opening Trash tab");
			e.printStackTrace();
		}
	}
}
