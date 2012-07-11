package com.zimbra.qa.selenium.projects.octopus.tests.favorites;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.DisplayFilePreview;
import com.zimbra.qa.selenium.projects.octopus.ui.PageOctopus;


public class AddRemoveFavorites extends OctopusCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;
	private boolean _fileAttached = false;
	private String _fileId = null;
	private List<String> fileIdList = null;

	public AddRemoveFavorites() {
		logger.info("New " + AddRemoveFavorites.class.getCanonicalName());

		// test starts at the Favorites tab
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

	@Test(description = "Mark file as Favorite through SOAP - verify file was added to Favorites using SOAP", groups = { "functional" })
	public void AddRemoveFavorites_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		FolderItem subFolderItem=createFolderViaSoap(account, briefcaseRootFolder);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
		+ "/data/public/other/testsoundfile.wav";

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP

		saveDocumentRequestViaSoap(account, subFolderItem, attachmentId);

		_fileAttached = true;
		_fileId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// Add file to the Favorites
		markFileFavoriteViaSoap(account,_fileId);

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
		saveDocumentRequestViaSoap(account, briefcaseRootFolder, attachmentId);

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
				DisplayFilePreview.Locators.zFileWatchIcon.locator
				+ " span[class^=watched-icon]", "3000");

		// click on the Favorites tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_FAVORITES);

		// Verify the file appears in the Favorites tab
		boolean present = app.zPageOctopus.zWaitForElementPresent(PageOctopus.Locators.zMyFilesListViewItems.locator + ":contains(" + fileName + ")", "3000");

		ZAssert.assertTrue(present,	"Verify is present in the Favorites tab");

		ZAssert.assertTrue(app.zPageOctopus.zIsItemInCurentListView(fileName),	"Verify the file appears in the Favorites tab");
	}

	@Test(description = "Mark file as Not Favorite using Context menu - verify favorite file dissapears from the Favorites tab", groups = { "functional1" })
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

		saveDocumentRequestViaSoap(account, briefcaseRootFolder, attachmentId1);

		_fileAttached = true;
		_fileId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// add item to the list
		fileIdList = new ArrayList<String>();
		fileIdList.add(_fileId);

		// Add file to the Favorites
		markFileFavoriteViaSoap(account, _fileId);

		SleepUtil.sleepSmall();

		// Verify the file was added to the Favorites using SOAP
		account.soapSend("<GetWatchingItemsRequest xmlns='urn:zimbraMail'>"
				+ "</GetWatchingItemsRequest>");

		ZAssert.assertTrue(account.soapMatch(
				"//mail:GetWatchingItemsResponse//mail:item", "id", _fileId),
		"Verify file is added to Favorites");

		app.zPageMyFiles.zRefresh();

		//TO DO: remove refresh call when bug HS-1778 is fixed.

		/*

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
				DisplayFilePreview.Locators.zFileWatchIcon.locator
						+ " span[class^=watched-icon]", "3000");

		// click on the Favorites tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_FAVORITES);

		// Verify the file appears in the Favorites tab
		ZAssert.assertTrue(app.zPageOctopus.zIsItemInCurentListView(fileName),
				"Verify the file appears in the Favorites tab");

		 */

		// click on the My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// mark file as NOT favorite using drop down menu
		app.zPageMyFiles.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
				Button.O_NOT_FAVORITE, fileName);

		/*

		// Wait for Watch icon become disabled
		app.zPageMyFiles.zWaitForElementPresent(
				DisplayFilePreview.Locators.zFileWatchIcon.locator
						+ " span[class^=unwatched-icon]", "3000");

		// click on the Favorites tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_FAVORITES);

		// Verify the file marked as Not Favorite disappears from the Favorites
		// tab
		ZAssert.assertTrue(app.zPageFavorites.zWaitForElementDeleted(
				PageFavorites.Locators.zFavoritesItemsView.locator
						+ ":contains(" + fileName + ")", "3000"),
				"Verify the file marked as Not Favorite disappears from the Favorites tab");

		 */

		// Verify the file was removed from the Favorites using SOAP
		account.soapSend("<GetWatchingItemsRequest xmlns='urn:zimbraMail'>"
				+ "</GetWatchingItemsRequest>");

		ZAssert.assertFalse(account.soapMatch(
				"//mail:GetWatchingItemsResponse//mail:item", "id", _fileId),
		"Verify file is removed frrom the Favorites");

	}

	@AfterMethod(groups = { "always" })
	public void testCleanup() {
		if (_fileAttached && fileIdList != null) {
			try {
				for (String id : fileIdList) {
					// Delete it from Server
					app.zPageOctopus.deleteItemUsingSOAP(id,
							app.zGetActiveAccount());
				}
			} catch (Exception e) {
				logger.warn("Failed while deleting the file", e);
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
				logger.warn("Failed while removing the folder.", e);
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
			logger.info("Failed while emptying Trash", e);
		}
	}
}
