package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.files;

import java.util.regex.Pattern;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.DisplayFilePreview;
import com.zimbra.qa.selenium.projects.octopus.ui.DisplayFilePreview.Field;

public class FilePreview extends OctopusCommonTest{
	private boolean _folderIsCreated = false;
	private String _folderName = null;
	private boolean _fileAttached = false;
	private String _fileId = null;

	public FilePreview() {
		logger.info("New " + FilePreview.class.getCanonicalName());

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

	public DisplayFilePreview openFilePreview() throws HarnessException
	{
		//get Active account
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);
		// Create file item
		String fileName=XML_FILE;

		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
		+ "/data/public/other/"+fileName;

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to the root folder through SOAP
		account.soapSend(
				"<SaveDocumentRequest xmlns='urn:zimbraMail'>" +
				"<doc l='" + briefcaseRootFolder.getId() + "'>" +
				"<upload id='" + attachmentId + "'/>" +
				"</doc>" +
				"</SaveDocumentRequest>"
		);

		_fileAttached = true;
		_fileId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");
		//Check if file is uploaded.
		ZAssert.assertNotNull(_fileId, "Verify file is uploaded");

		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Select file in the list view
		DisplayFilePreview filePreview = (DisplayFilePreview) app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, fileName);

		app.zPageMyFiles.zWaitForBusyOverlayOctopus();

		return filePreview;

	}

	@Test(description = "Ensure No Preview available text is displayed when preview is not shown", groups = { "smoke" })
	public void FilePreview_01() throws HarnessException
	{

		DisplayFilePreview previewFile =openFilePreview();

		String bodyText =previewFile.zGetFileProperty(Field.Body);

		ZAssert.assertTrue(bodyText.contains("No preview"), "Check if no preview available text is present");

	}
	@Test(description = "Ensure file properties are present in toolbar", groups = { "smokeT" })
	public void FilePreview_02() throws HarnessException
	{
		DisplayFilePreview previewFile =openFilePreview();

		String version = previewFile.zGetFileProperty(Field.Version);
		String size = previewFile.zGetFileProperty(Field.Size);
		String filename= previewFile.zGetFileProperty(Field.Name);

		Pattern versionRegex = Pattern.compile("Version [0-9]");
		Pattern sizeRegex = Pattern.compile("[0-9]* [a-zA-Z]");
		Pattern nameRegex = Pattern.compile(REGEXP_FILENAME);


		ZAssert.assertNotNull(version, "Check if version value is not null");
		ZAssert.assertMatches(versionRegex, version,"Check regex for version value");
		ZAssert.assertNotNull(size, "Check if size property is not null");
		ZAssert.assertMatches(sizeRegex, size, "Check regex for Size value");
		ZAssert.assertNotNull(filename, "Check if fileName exists");
		ZAssert.assertMatches(nameRegex, filename, "Check regex for filename value");

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
			app.zPageTrash.emptyTrashUsingSOAP(app.zGetActiveAccount());

			app.zPageOctopus.zLogout();

		} catch (Exception e) {
			logger.info("Failed while emptying Trash", e);
		}
	}
}
