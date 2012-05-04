package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.files;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.DisplayFilePreview;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles;

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

	@Test(description = "Upload file through RestUtil - Rename File using Right Click Context Menu", groups = { "sanity" })
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
	
	@Bugs(ids = "69347")
	@Test(description = "After renaming a file, the file list must be refreshed", groups = { "functional2" })
	public void RefreshRenamefile() throws HarnessException
    {
		 String fileName=JPG_FILE;
		 String newNameForFirstFile = "1_file.jpg";
		 String newNameForSecondFile = "2_file.jpg";
		 String newName = "3_file.jpg";
		
		 //Upload 1st file
		 _fileId = uploadFileViaSoap(app.zGetActiveAccount(),fileName);  
		 
		 //rename first file via soap
		 renameViaSoap(app.zGetActiveAccount(), _fileId, newNameForFirstFile);
		 		
		 //Upload 2nd file
		 String _fileId1=uploadFileViaSoap(app.zGetActiveAccount(),fileName); 
		
	     //rename second file via soap
		 renameViaSoap(app.zGetActiveAccount(), _fileId1, newNameForSecondFile);
		 
		 //rename again first file via soap
		 renameViaSoap(app.zGetActiveAccount(), _fileId, newName);
		 
		 // Verify Renamed file exists in My Files view
		 ZAssert.assertTrue(app.zPageMyFiles.zWaitForElementPresent(PageMyFiles.Locators.zMyFilesListViewItems.locator
							+ ":contains(" + newName + ")", "3000"),"Verify file appears in My Files view");
			
	     // Select renamed file in the list view for preview
		 DisplayFilePreview filePreviewForFirstFile = (DisplayFilePreview) app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, newName);
		 SleepUtil.sleepSmall();

		 //Get filename from preview panel.
		 String expectedResult=app.zPageOctopus.sGetText(DisplayFilePreview.Locators.zPreviewFileName.locator);
		
		
		 // Verify File name from preview panel and file list should be same .
		 ZAssert.assertEquals(newName, expectedResult, "Verify file names are same");
		
		 // Select file in the list view.
		 DisplayFilePreview filePreviewForSecondFile = (DisplayFilePreview) app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, newNameForSecondFile);
		 SleepUtil.sleepSmall();
		
		 ZAssert.assertEquals(newNameForSecondFile, expectedResult, "Verify file names are same");
		 
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
			logger.info("Failed while emptying Trash", e);
		}
	}
	
}
