package com.zimbra.qa.selenium.projects.octopus.tests.history;

import java.util.ArrayList;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.DisplayFilePreview;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.GetText;


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

	@Test(description = "Upload file through RestUtil - verify account email in the history through SOAP", groups = { "functional" })
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

	@Test(description = "Upload file through RestUtil - verify history text + user email appeared in History List view", groups = { "smoke" })
	public void UploadFileVerifyTextUseremailInGlobalHistory() throws HarnessException {
		String fileName=JPG_FILE;
		
		_fileId=uploadFileViaSoap(app.zGetActiveAccount(),fileName);

		// Click on MyFiles tab 
		// this makes the history text displayed
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		
		// form the text
		String historyText = "You created version 1 of file " +  fileName +".";
		
		// check if the text present
		HistoryItem found = app.zPageHistory.isTextPresentInGlobalHistory(historyText);
			
		// verification
		ZAssert.assertNotNull(found, "Verify " +  historyText + " is found");		
		ZAssert.assertEquals(found.getHistoryText(), historyText, "Verify the history text matches");
			
	}

	@Test(description = "Open History tab - verify Activity Type filter controls", groups = { "functional" })
	public void VerifyActivityTypeFilterControls() throws HarnessException {
	    String fileName=JPG_FILE;
		
		_fileId=uploadFileViaSoap(app.zGetActiveAccount(),fileName);	

	
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
   
	@Bugs(ids="71867")
	@Test(description="Open history tab - Verify if history for comments added is present", groups={"functional"})
	public void VerifyActivityForComments()throws HarnessException
	{
		// get current Active account
		ZimbraAccount act = app.zGetActiveAccount();
		//Select a file type to upload
		String fileName = TEXT_FILE;
		//Upload a file using Soap
		_fileId = uploadFileViaSoap(act, fileName);
	
		
		String Comment = "comment"+ ZimbraSeleniumProperties.getUniqueString();
		//Add comments to a file using SOAP AddCommentRequest 
		act.soapSend("<AddCommentRequest xmlns='urn:zimbraMail'> <comment parentId='"
				+ _fileId + "' text='" + Comment + "'/></AddCommentRequest>");
		
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);
				
		String requiredHistory = (GetText.comment(fileName));
		
		boolean found = false;
		
		ArrayList<HistoryItem> historyItems;
		
		historyItems = app.zPageHistory.zListItem();
		
		for (HistoryItem historyItem : historyItems) {
		
			if(historyItem.getHistoryText().contains(requiredHistory))
			{
				found = true;
				
			}
			
		}
		//Assert if found history matches with Add comment history
		ZAssert.assertTrue(found, "Verify if history found");
		
		//Logout and Login to application again with same account
		app.zPageOctopus.zLogout();
		
		app.zPageLogin.zLogin(act);
		
		
	
	}
	
	@Bugs(ids = "71692")
	@Test(description = "History must not be empty", groups = { "functional" })
	public void VerifyHistoryIsNotEmpty() throws HarnessException
    {
		// Get current Active account
		ZimbraAccount act = app.zGetActiveAccount();
		//Select a file type to upload
		String fileName = TEXT_FILE;
		//Upload a file using Soap
		_fileId = uploadFileViaSoap(act, fileName);
	 	 
	 	// Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);
		String requiredHistory = "You created version 1 of file "+fileName+".";
		
		//Assert if found history matches with upload file history
		ZAssert.assertEquals(GetText.newVersion(fileName), app.zPageHistory.isTextPresentInGlobalHistory(requiredHistory).getHistoryText(), "Verify if required history matches with found history");
	}
	
	@Test(description ="Ensure version link in history opens that version" ,groups = { "smoke" })
	public void VerifyVersionLinkNameFromHistory() throws HarnessException
	{
		ZimbraAccount account = app.zGetActiveAccount();
		
		// Create file item
		String fileName = TEXT_FILE;
		uploadFileViaSoap(app.zGetActiveAccount(), fileName);
		
		String name = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "name");
		
		// verify the file is uploaded
		ZAssert.assertEquals(fileName, name, "Verify file is uploaded");

		// Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		String requiredHistory = "You created version 1 of file " + fileName + ".";

		//Assert if found history matches with upload file history
		ZAssert.assertEquals(GetText.newVersion(fileName), app.zPageHistory.isTextPresentInGlobalHistory(requiredHistory).getHistoryText(), "Verify if required history matches with found history");

		// Click on version link and verify the file.
		app.zPageHistory.sClickAt(PageHistory.Locators.zHistoryVersionLink.locator, "0,0");

		//Get filename from preview panel.
		String expectedResult=app.zPageOctopus.sGetText(DisplayFilePreview.Locators.zPreviewFileName.locator);

		// Verify File name from preview panel and file selected from version link should be same .
		ZAssert.assertEquals(fileName, expectedResult, "Verify file names are same");
	}

		
	@AfterMethod(groups = { "always" })
	public void testCleanup() {
		if (_fileAttached || _fileId != null) {
			try {
				// Delete it from Server
				app.zPageOctopus.deleteItemUsingSOAP(_fileId,
						app.zGetActiveAccount());
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
			logger.info("Failed while emptying Trash", e);
		}
	}

}
