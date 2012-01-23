package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.folders;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;

public class CreateFolder extends OctopusCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;

	public CreateFolder() {
		logger.info("New " + CreateFolder.class.getCanonicalName());

		// test starts at the My Files tab
		super.startingPage = app.zPageMyFiles;
		super.startingAccountPreferences = null;
	}

	@Test(description = "Create a new folder using drop down list option", groups = { "smoke" })
	public void CreateFolder_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Make sure size of the client and server subfolders match
		int clientsize = app.zPageOctopus.zGetListViewItems().size();
		int serversize = briefcaseRootFolder.getSubfolders().size();
		ZAssert.assertEquals(clientsize, serversize,
				"Verify size of the client and server subfolders match");

		// Set the new folder name
		// _folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		app.zPageMyFiles.zToolbarPressPulldown(Button.B_MY_FILES,
				Button.O_NEW_FOLDER);

		// refresh Octopus page
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		SleepUtil.sleepSmall();

		// Make sure the folder was created on client
		int newsize = app.zPageOctopus.zGetListViewItems().size();
		ZAssert.assertTrue(newsize > clientsize,
				"Verify the new folder created");

		// Make sure the new size of the client and server subfolders match
		briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);
		serversize = briefcaseRootFolder.getSubfolders().size();
		ZAssert.assertEquals(newsize, serversize,
				"Verify size of the client and server subfolders match");
	}

	@AfterMethod(groups = { "always" })
	public void createFolderTestCleanup() {
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
	}
}
