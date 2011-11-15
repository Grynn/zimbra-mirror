package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.files;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;

public class DeleteFile extends OctopusCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;
	private boolean _fileAttached = false;
	private String _fileId = null;

	public DeleteFile() {
		logger.info("New " + DeleteFile.class.getCanonicalName());

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

	@Test(description = "Delete file through SOAP - verify deleted file in the Trash tab", groups = { "sanity" })
	public void DeleteFile_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		FolderItem trash = FolderItem.importFromSOAP(account,
				SystemFolder.Trash);
		ZAssert.assertNotNull(trash, "Verify the trash is available");

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/testtextfile.txt";

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

		// trash file using SOAP
		app.zPageOctopus.trashItemUsingSOAP(_fileId, account);

		// click on Trash tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_TRASH);

		// Verify the file is now in the trash
		ZAssert.assertTrue(app.zPageOctopus.zIsItemInCurentListView(fileName),
				"Verify the file was moved to the trash folder");
	}

	@Test(description = "Delete file using drop down menu - - verify deleted file in the Trash tab", groups = { "smoke" })
	public void DeleteFile_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		FolderItem trash = FolderItem.importFromSOAP(account,
				SystemFolder.Trash);
		ZAssert.assertNotNull(trash, "Verify the trash is available");

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/testwordfile.doc";

		FileItem file = new FileItem(filePath);
		String fileName = file.getName();

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to the root folder through SOAP
		account.soapSend(

		"<SaveDocumentRequest xmlns='urn:zimbraMail'>" + "<doc l='"
				+ briefcaseRootFolder.getId() + "'>" + "<upload id='"
				+ attachmentId + "'/></doc>" + "</SaveDocumentRequest>");

		_fileAttached = true;
		_fileId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// verify the file is uploaded
		ZAssert.assertNotNull(_fileId, "Verify file is uploaded");

		// delete file using right click context menu
		app.zPageMyFiles.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
				Button.O_DELETE, fileName);

		// Verify the deleted file disappears from My Files tab
		ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementDeleted(
				PageMyFiles.Locators.zMyFilesListView.locator + ":contains("
						+ fileName + ")", "3000"),
				"Verify the deleted file disappears from My Files tab");

		// click on Trash tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_TRASH);

		// Verify the file is now in the trash
		ZAssert.assertTrue(app.zPageOctopus.zIsItemInCurentListView(fileName),
				"Verify the file was moved to the trash folder");
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
	}
}
