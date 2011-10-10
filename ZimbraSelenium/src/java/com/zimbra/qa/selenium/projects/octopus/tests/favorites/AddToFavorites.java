package com.zimbra.qa.selenium.projects.octopus.tests.favorites;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.FilePreview;

public class AddToFavorites extends OctopusCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;
	private boolean _fileAttached = false;
	private String _fileId = null;

	public AddToFavorites() {
		logger.info("New " + AddToFavorites.class.getCanonicalName());

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

	@Test(description = "Mark file as Favorite through SOAP - verify file was added to Favorites using SOAP", groups = { "sanity" })
	public void AddToFavorites_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/testsoundfile.wav";

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + briefcaseRootFolder.getId() + "'><upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");

		_fileAttached = true;
		_fileId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// Add file to the Favorites
		account.soapSend("<DocumentActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + _fileId + "' op='watch'/>"
				+ "</DocumentActionRequest>");

		// Verify the file was added to the Favorites using SOAP
		account.soapSend("<GetWatchingItemsRequest xmlns='urn:zimbraMail'>"
				+ "</GetWatchingItemsRequest>");

		String id = account.soapSelectValue(
				"//mail:GetWatchingItemsResponse//mail:item", "id");

		ZAssert.assertEquals(_fileId, id, "Verify file is added to Favorites");
	}

	@Test(description = "Mark file as Favorite using Context menu - verify file appears in the Favorites tab", groups = { "smoke" })
	public void AddToFavorites_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/structure.jpg";

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

		//SleepUtil.sleepSmall();
		
		// Wait for Watch icon become enabled
		app.zPageMyFiles.zWaitForElementPresent(FilePreview.Locators.zFileWatchIcon.locator	+ " span[class^=watched-icon]", "3000");

		// click on the Favorites tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_FAVORITES);
		
		// Verify the file appears in the Favorites tab
		ZAssert.assertTrue(app.zPageOctopus.zIsItemInCurentListView(fileName),
				"Verify the file appears in the Favorites tab");		
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
		}
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

	}
}
