package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory;
import java.util.*;

public class ActivityHistory extends OctopusCommonTest {

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

	public ActivityHistory() {
		logger.info("New " + ActivityHistory.class.getCanonicalName());

		// test starts at the History tab
		super.startingPage = app.zPageHistory;
		super.startingAccountPreferences = null;
	}

	@Test(description = "Upload file through RestUtil - verify account email in the history through SOAP", groups = { "sanity" })
	public void ActivityHistory_01() throws HarnessException {
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

		// Click on MyFiles tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		// Verify file activity appears in the History
		account.soapSend("<GetActivityStreamRequest xmlns='urn:zimbraMail' offset='0' limit='250' id='"
				+ rootFolder.getId() + "'/>");

		// Verify account email appears in the activity history
		ZAssert.assertTrue(account.soapMatch(
				"//mail:GetActivityStreamResponse//mail:user", "email",
				account.EmailAddress),
				"Verify account email appears in the activity history");	
	}

	@Test(description = "Upload file through RestUtil - verify file history in History List view", groups = { "smoke" })
	public void ActivityHistory_02() throws HarnessException {
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

		// Click on MyFiles tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		ArrayList<HistoryItem> historyItems= app.zPageHistory.zListItem();
		
		app.zPageHistory.zWaitForElementPresent(
				PageHistory.Locators.zHistoryItemRow.locator + ":contains("
						+ fileName + ")", "3000");
		
		String historyText= app.zGetActiveAccount().EmailAddress + " created version 1 of file " +  fileName +".";
		                           
		HistoryItem found = null;
		
		// Verify history item appears in the activity history
		for ( HistoryItem item : historyItems ) {
			
			// Verify the history is found
			if (item.getHistoryText().equals(historyText)) {
				logger.debug(item.getHistoryText());
				found = item;
				break;
			}
			
		}
		ZAssert.assertNotNull(found, "Verify the history is found");
		
		ZAssert.assertEquals(found.getHistoryText(), historyText, "Verify the history text matches");
		ZAssert.assertEquals(found.getHistoryUser(), app.zGetActiveAccount().EmailAddress , "Verify the user matches");
	
		
	}

	@Test(description = "Open History tab - verify Activity Type filter controls", groups = { "functional" })
	public void ActivityHistory_03() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/samplejpg.jpg";

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

		// Click on MyFiles tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		// Verify All Types check box is present
		app.zPageHistory.zToolbarCheckMark(Button.O_ALL_TYPES);

		// Verify All Types check box state has changed
		ZAssert.assertTrue(
				app.zPageHistory
						.sIsChecked(PageHistory.Locators.zHistoryFilterAllTypes.locator),
				"Verify All Types check box is checked");

		// Verify Favorites check box is present
		app.zPageHistory.zToolbarCheckMark(Button.O_FAVORITES);

		// Verify Favorites check box state has changed
		ZAssert.assertTrue(
				app.zPageHistory
						.sIsChecked(PageHistory.Locators.zHistoryFilterFavorites.locator),
				"Verify Favorites check box is checked");

		// Verify Comment check box is present
		app.zPageHistory.zToolbarCheckMark(Button.O_COMMENT);

		// Verify Comment check box state has changed
		ZAssert.assertTrue(
				app.zPageHistory
						.sIsChecked(PageHistory.Locators.zHistoryFilterComment.locator),
				"Verify Comment check box is checked");

		// Verify Sharing check box is present
		app.zPageHistory.zToolbarCheckMark(Button.O_SHARING);

		// Verify Sharing check box state has changed
		ZAssert.assertTrue(
				app.zPageHistory
						.sIsChecked(PageHistory.Locators.zHistoryFilterSharing.locator),
				"Verify Sharing check box is checked");
		
		// Verify New Version check box is present
		app.zPageHistory.zToolbarCheckMark(Button.O_NEW_VERSION);
		
		// Verify New Version check box state has changed
		ZAssert.assertTrue(
				app.zPageHistory
						.sIsChecked(PageHistory.Locators.zHistoryFilterNewVersion.locator),
				"Verify New Version check box is checked");
		
		// Verify Rename check box is present
		app.zPageHistory.zToolbarCheckMark(Button.O_RENAME);
		
		// Verify Rename check box state has changed
		ZAssert.assertTrue(
				app.zPageHistory
						.sIsChecked(PageHistory.Locators.zHistoryFilterRename.locator),
				"Verify Rename check box is checked");
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

			app.zPageOctopus.zLogout();
		} catch (Exception e) {
			logger.info("Failed while emptying Trash");
			e.printStackTrace();
		}
	}
}
