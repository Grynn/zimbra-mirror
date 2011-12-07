package com.zimbra.qa.selenium.projects.octopus.tests.favorites;

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.FilePreview;
import com.zimbra.qa.selenium.projects.octopus.ui.PageFavorites;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;

public class AddRemoveFavorites extends OctopusCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;
	private boolean _fileAttached = false;
	private String _fileId = null;
	private List<String> fileIdList = null;

	public AddRemoveFavorites() {
		logger.info("New " + AddRemoveFavorites.class.getCanonicalName());

		// test starts at the My Files tab
		super.startingPage = app.zPageFavorites;
		super.startingAccountPreferences = null;
	}

	@BeforeMethod(groups = { "always" })
	public void testReset() {
		_folderName = null;
		_folderIsCreated = false;
		_fileId = null;
		fileIdList = null;
		_fileAttached = false;
	}

	@Test(description = "Mark file as Favorite through SOAP - verify file was added to Favorites using SOAP", groups = { "sanity" })
	public void AddRemoveFavorites_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		String subFolderName = "folder"
				+ ZimbraSeleniumProperties.getUniqueString();

		account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + subFolderName + "' l='"
				+ briefcaseRootFolder.getId() + "' view='document'/>"
				+ "</CreateFolderRequest>");

		// Verify the sub-folder exists on the server
		FolderItem subFolderItem = FolderItem.importFromSOAP(account,
				subFolderName);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/testsoundfile.wav";

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + subFolderItem.getId() + "'><upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");

		_fileAttached = true;
		_fileId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// Add file to the Favorites
		account.soapSend("<DocumentActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + _fileId + "' op='watch'/>"
				+ "</DocumentActionRequest>");

		SleepUtil.sleepSmall();

		// Verify the file was added to the Favorites using SOAP
		account.soapSend("<GetWatchingItemsRequest xmlns='urn:zimbraMail'>"
				+ "</GetWatchingItemsRequest>");

		ZAssert.assertTrue(account.soapMatch(
				"//mail:GetWatchingItemsResponse//mail:item", "id", _fileId),
				"Verify file is added to Favorites");
	}

	@Test(description = "Mark file as Favorite using Context menu - verify file appears in the Favorites tab", groups = { "smoke" })
	public void AddRemoveFavorites_02() throws HarnessException {
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

		// add item to the list
		fileIdList = new ArrayList<String>();
		fileIdList.add(_fileId);

		// click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// mark file as favorite using drop down menu
		app.zPageMyFiles.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
				Button.O_FAVORITE, fileName);

		// SleepUtil.sleepSmall();

		// Wait for Watch icon become enabled
		app.zPageMyFiles.zWaitForElementPresent(
				FilePreview.Locators.zFileWatchIcon.locator
						+ " span[class^=watched-icon]", "3000");

		// click on the Favorites tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_FAVORITES);

		// Verify the file appears in the Favorites tab
		ZAssert.assertTrue(app.zPageOctopus.zIsItemInCurentListView(fileName),
				"Verify the file appears in the Favorites tab");
	}

	@Test(description = "Mark file as Favorite / Not Favorite using Context menu - verify file appears / dissapears in the Favorites tab", groups = { "functional" })
	public void AddRemoveFavorites_03() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create first file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/structure.jpg";

		FileItem file = new FileItem(filePath);
		String fileName = file.getName();

		// Upload first file to the server through RestUtil
		String attachmentId1 = account.uploadFile(filePath);

		// Save uploaded files through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + briefcaseRootFolder.getId() + "'><upload id='"
				+ attachmentId1 + "'/></doc></SaveDocumentRequest>");

		_fileAttached = true;
		_fileId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// add item to the list
		fileIdList = new ArrayList<String>();
		fileIdList.add(_fileId);

		// click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Verify file exists in My Files view
		ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementPresent(
				PageMyFiles.Locators.zMyFilesListViewItems.locator
						+ ":contains(" + fileName + ")", "3000"),
				"Verify file appears in My Files view");

		// mark file as favorite using drop down menu
		app.zPageMyFiles.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
				Button.O_FAVORITE, fileName);

		// Wait for Watch icon become enabled
		app.zPageMyFiles.zWaitForElementPresent(
				FilePreview.Locators.zFileWatchIcon.locator
						+ " span[class^=watched-icon]", "3000");

		// click on the Favorites tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_FAVORITES);

		// Verify the file appears in the Favorites tab
		ZAssert.assertTrue(app.zPageOctopus.zIsItemInCurentListView(fileName),
				"Verify the file appears in the Favorites tab");

		// click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// mark first file as NOT favorite using drop down menu
		app.zPageMyFiles.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
				Button.O_NOT_FAVORITE, fileName);

		// Wait for Watch icon become disabled
		app.zPageMyFiles.zWaitForElementPresent(
				FilePreview.Locators.zFileWatchIcon.locator
						+ " span[class^=unwatched-icon]", "3000");

		// click on the Favorites tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_FAVORITES);

		// Verify the file marked as Not Favorite disappears from the Favorites
		// tab
		ZAssert.assertTrue(app.zPageFavorites.zWaitForElementDeleted(
				PageFavorites.Locators.zFavoritesItemsView.locator
						+ ":contains(" + fileName + ")", "3000"),
				"Verify the file marked as Not Favorite disappears from the Favorites tab");
	}

	@AfterMethod(groups = { "always" })
	public void testCleanup() {
		if (_fileAttached && fileIdList != null) {
			try {
				for (String id : fileIdList) {
					// Delete it from Server
					app.zPageOctopus.trashItemUsingSOAP(id,
							app.zGetActiveAccount());
				}
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
		} catch (Exception e) {
			logger.info("Failed while emptying Trash");
			e.printStackTrace();
		}
	}
}
