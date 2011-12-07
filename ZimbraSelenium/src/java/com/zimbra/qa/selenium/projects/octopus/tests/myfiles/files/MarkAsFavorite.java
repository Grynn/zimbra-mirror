package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.files;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.FilePreview;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;


public class MarkAsFavorite extends OctopusCommonTest {

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

	public MarkAsFavorite() {
		logger.info("New " + MarkAsFavorite.class.getCanonicalName());

		// test starts at the My Files tab
		super.startingPage = app.zPageMyFiles;
		super.startingAccountPreferences = null;
	}

	@Test(description = "Mark file as Favorite using Context menu - verify favorite icon becomes enabled in the preview panel", groups = { "smoke" })
	public void MarkAsFavorite_01() throws HarnessException {
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

		// Save uploaded file to briefcase through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + briefcaseRootFolder.getId() + "'><upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");

		_fileAttached = true;
		_fileId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// mark file as favorite using drop down menu
		app.zPageMyFiles.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
				Button.O_FAVORITE, fileName);

		// Verify Watch icon becomes enabled
		ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementPresent(
				FilePreview.Locators.zFileWatchIcon.locator
						+ " span[class^=watched-icon]", "3000"),
				"Verify the favorite icon becomes enabled in the preview panel");

		// Verify the file was added to the Favorites using SOAP
		account.soapSend("<GetWatchingItemsRequest xmlns='urn:zimbraMail'>"
				+ "</GetWatchingItemsRequest>");

		ZAssert.assertTrue(account.soapMatch(
				"//mail:GetWatchingItemsResponse//mail:item", "id", _fileId),
				"Verify file is added to Favorites");
	}

	@Test(description = "Mark file as Favorite / Not Favorite using Context menu - verify watch icon becomes enabled / disabled in the preview panel", groups = { "functional" })
	public void MarkAsFavorite_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/testtextfile.txt";

		FileItem file = new FileItem(filePath);
		String fileName = file.getName();

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + briefcaseRootFolder.getId() + "'><upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");

		_fileAttached = true;
		_fileId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// mark file as favorite using drop down menu
		app.zPageMyFiles.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
				Button.O_FAVORITE, fileName);

		// Verify Watch icon becomes enabled
		ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementPresent(
				FilePreview.Locators.zFileWatchIcon.locator
						+ " span[class^=watched-icon]", "3000"),
				"Verify the favorite icon becomes enabled in the preview panel");

		// Verify the file was added to the Favorites using SOAP
		account.soapSend("<GetWatchingItemsRequest xmlns='urn:zimbraMail'>"
				+ "</GetWatchingItemsRequest>");

		ZAssert.assertTrue(account.soapMatch(
				"//mail:GetWatchingItemsResponse//mail:item", "id", _fileId),
				"Verify file is added to Favorites");

		// mark file as Not Favorite using drop down menu
		app.zPageMyFiles.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
				Button.O_NOT_FAVORITE, fileName);

		// Verify Watch icon becomes disabled
		ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementPresent(
				FilePreview.Locators.zFileWatchIcon.locator
						+ " span[class^=unwatched-icon]", "3000"),
				"Verify the favorite icon becomes disabled in the preview panel");
	}

	@Test(description = "Mark file as Favorite / Not Favorite clicking on watch icon - verify watch icon becomes enabled / disabled in the preview panel", groups = { "functional" })
	public void MarkAsFavorite_03() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/testtextfile.txt";

		FileItem file = new FileItem(filePath);
		String fileName = file.getName();

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to My Files through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + briefcaseRootFolder.getId() + "'><upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");

		_fileAttached = true;
		_fileId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Verify file exists in My Files view
		ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementPresent(
				PageMyFiles.Locators.zMyFilesListViewItems.locator
				+ ":contains(" + fileName + ")", "3000"),
				"Verify file appears in My Files view");
		
		// Select file in the list view
		FilePreview filePreview = (FilePreview) app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, fileName);
		
		// mark file as favorite clicking on watch icon
		filePreview.zPressButton(Button.B_WATCH);		

		// Verify Watch icon becomes enabled
		ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementPresent(
				FilePreview.Locators.zFileWatchIcon.locator
						+ " span[class^=watched-icon]", "3000"),
				"Verify the favorite icon becomes enabled in the preview panel");

		// Verify the file was added to the Favorites using SOAP
		account.soapSend("<GetWatchingItemsRequest xmlns='urn:zimbraMail'>"
				+ "</GetWatchingItemsRequest>");

		ZAssert.assertTrue(account.soapMatch(
				"//mail:GetWatchingItemsResponse//mail:item", "id", _fileId),
				"Verify file is added to Favorites");

		//Select file
		filePreview = (FilePreview) app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, fileName);
		
		// unmark file as favorite clicking on unwatch icon
		filePreview.zPressButton(Button.B_UNWATCH);	

		// Verify Watch icon becomes disabled
		ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementPresent(
				FilePreview.Locators.zFileWatchIcon.locator
						+ " span[class^=unwatched-icon]", "3000"),
				"Verify the favorite icon becomes disabled in the preview panel");
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
