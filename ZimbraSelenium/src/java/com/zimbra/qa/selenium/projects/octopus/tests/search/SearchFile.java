package com.zimbra.qa.selenium.projects.octopus.tests.search;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;

public class SearchFile extends OctopusCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;
	private boolean _fileAttached = false;
	private String _fileId = null;

	public SearchFile() {
		logger.info("New " + SearchFile.class.getCanonicalName());

		// test starts at the Search tab
		super.startingPage = app.zPageSearch;
		super.startingAccountPreferences = null;
	}

	@BeforeMethod(groups = { "always" })
	public void testReset() {
		_folderName = null;
		_folderIsCreated = false;
		_fileId = null;
		_fileAttached = false;
	}

	@Test(description = "Upload file through RestUtil - verify by searching file through the SOAP", groups = { "smoke" })
	public void SearchFile_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/testexcelfile.xls";

		FileItem fileItem = new FileItem(filePath);
		String fileName = fileItem.getName();

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + briefcaseRootFolder.getId() + "'><upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");

		_fileAttached = true;
		_fileId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// Verify file by searching it through the SOAP
		ZAssert.assertEquals(_fileId, app.zPageOctopus.searchFile(fileName),
				"Verify file is returned by search");
	}

	@Test(description = "" +
			"Delete file - Search file in the Trash through the SOAP", groups = { "functional" })
	public void SearchFile_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		FolderItem trash = FolderItem.importFromSOAP(account,
				SystemFolder.Trash);

		ZAssert.assertNotNull(trash, "Verify the trash is available");

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/putty.log";

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

		// delete file using SOAP
		app.zPageOctopus.deleteItemUsingSOAP(_fileId, account);

		SleepUtil.sleepSmall();

		// Verify by searching file in the Trash using SOAP
		ZAssert.assertEquals(_fileId,
				app.zPageOctopus.searchFileIn(fileName, trash.getName()),
				"Verify file is returned by search");
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
