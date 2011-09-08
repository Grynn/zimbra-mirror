package com.zimbra.qa.selenium.projects.octopus.tests.folders;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;

public class CreateFolder extends OctopusCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;

	public CreateFolder() {
		logger.info("New " + CreateFolder.class.getCanonicalName());

		// test starts at the briefcase tab
		super.startingPage = app.zPageOctopus;
		super.startingAccountPreferences = null;
	}

	@Test(description = "Create a new folder using drop down list option", groups = { "sanity" })
	public void CreateFolder_01() throws HarnessException {
		int size = app.zPageOctopus.zGetListViewItems().size();
		
		// Set the new folder name
		_folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		app.zPageOctopus.zToolbarPressPulldown(Button.B_MY_FILES, Button.O_NEW_FOLDER, null);

		// refresh Octopus page
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Make sure the folder was created on the ZCS server
		int newsize = app.zPageOctopus.zGetListViewItems().size(); 
		ZAssert.assertTrue(newsize > size, "Verify the new folder created");
		
		//FolderItem folder = FolderItem.importFromSOAP(account, _folderName);
		//ZAssert.assertNotNull(folder, "Verify the new form opened");

		//ZAssert.assertEquals(folder.getName(), _folderName,
		//		"Verify the server and client folder names match");
	}

	@AfterMethod(groups = { "always" })
	public void createFolderTestCleanup() {
		if (_folderIsCreated) {
			try {
				app.zPageOctopus.zNavigateTo();
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
