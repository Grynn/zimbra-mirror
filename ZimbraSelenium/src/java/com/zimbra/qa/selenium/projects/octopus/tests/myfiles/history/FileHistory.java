package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.history;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogFileHistory;
import com.zimbra.qa.selenium.projects.octopus.ui.DisplayFilePreview;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;

public class FileHistory extends OctopusCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;
	private boolean _fileAttached = false;
	private String _fileId = null;

	@BeforeMethod(groups = { "always" })
	public void testReset() {
		_folderName = null;
		_folderIsCreated = false;
		_fileId = null;
		_fileAttached = false;
	}

	public FileHistory() {
		logger.info("New " + FileHistory.class.getCanonicalName());

		// test starts at the My Files tab
		super.startingPage = app.zPageMyFiles;
		super.startingAccountPreferences = null;
	}

	@Test(description = "Upload file through RestUtil - verify file name in the history through SOAP", groups = { "sanity" })
	public void FileHistory_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem rootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/testpptfile.ppt";

		FileItem fileItem = new FileItem(filePath);

		String fileName = fileItem.getName();

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to My Files through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + rootFolder.getId() + "'><upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");

		_fileAttached = true;
		_fileId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");
		String name = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "name");

		// verify the file is uploaded
		ZAssert.assertEquals(fileName, name, "Verify file is uploaded");

		// Verify file activity appears in the History
		account.soapSend("<GetActivityStreamRequest xmlns='urn:zimbraMail' offset='0' limit='250' id='"
				+ rootFolder.getId() + "'/>");

		// Verify file name appears in the activity history
		ZAssert.assertTrue(app.zPageOctopus.zVerifyElementText(account,
				"//mail:GetActivityStreamResponse//mail:arg", fileName),
				"Verify file name appears in the activity history");
	}

	@Test(description = "Upload file through RestUtil - verify file history in the History dialog", groups = { "smoke" })
	public void FileHistory_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/samplejpg.jpg";

		FileItem file = new FileItem(filePath);

		String fileName = file.getName();

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to the root folder through SOAP
		account.soapSend(

		"<SaveDocumentRequest xmlns='urn:zimbraMail'>" + "<doc l='"
				+ briefcaseRootFolder.getId() + "'>" + "<upload id='"
				+ attachmentId + "'/>" + "</doc></SaveDocumentRequest>");

		// account.soapSelectNode("//mail:SaveDocumentResponse", 1);

		_fileAttached = true;
		_fileId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// Click on My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Verify file exists in My Files view
		ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementPresent(
				PageMyFiles.Locators.zMyFilesListViewItems.locator
						+ ":contains(" + fileName + ")", "3000"),
				"Verify file appears in My Files view");

		// Select file in the list view
		DisplayFilePreview filePreview = (DisplayFilePreview) app.zPageMyFiles.zListItem(
				Action.A_LEFTCLICK, fileName);

		DialogFileHistory fileHistory = (DialogFileHistory) filePreview
				.zPressButton(Button.B_HISTORY);

		// Verify file name appears in the history dialog
		ZAssert.assertTrue(app.zPageOctopus.zWaitForElementPresent(
				DialogFileHistory.Locators.zFileHistoryMenuBar.locator
						+ ":contains(" + fileName + ")", "3000"),
				"Verify file name appears in the history dialog");

		// Verify Close button in the history dialog
		ZAssert.assertTrue(app.zPageOctopus.zWaitForElementPresent(
				DialogFileHistory.Locators.zFileHistoryCloseBtn.locator, "3000"),
				"Verify Close button in the history dialog");
		
		// dismiss the history dialog
		fileHistory.zClickButton(Button.B_CLOSE);
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
			// Refresh view
			// ZimbraAccount account = app.zGetActiveAccount();
			// FolderItem item =
			// FolderItem.importFromSOAP(account,SystemFolder.Briefcase);
			// account.soapSend("<GetFolderRequest xmlns='urn:zimbraMail'><folder l='1' recursive='0'/>"
			// + "</GetFolderRequest>");
			// account.soapSend("<GetFolderRequest xmlns='urn:zimbraMail' requestId='folders' depth='1' tr='true' view='document'><folder l='"
			// + item.getId() + "'/></GetFolderRequest>");
			// account.soapSend("<GetActivityStreamRequest xmlns='urn:zimbraMail' id='16'/>");
			// app.zGetActiveAccount().accountIsDirty = true;
			// app.zPageOctopus.sRefresh();

			// Empty trash
			app.zPageTrash.emptyTrashUsingSOAP(app.zGetActiveAccount());
		} catch (Exception e) {
			logger.info("Failed while emptying Trash");
			e.printStackTrace();
		}
	}
}
