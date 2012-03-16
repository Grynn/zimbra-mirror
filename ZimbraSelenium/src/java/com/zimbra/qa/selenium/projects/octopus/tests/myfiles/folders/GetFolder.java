package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.folders;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.IOctListViewItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;

public class GetFolder extends OctopusCommonTest {

	public GetFolder() {
		logger.info("New " + GetFolder.class.getCanonicalName());

		// All tests start at the Briefcase page
		super.startingPage = app.zPageMyFiles;
		super.startingAccountPreferences = null;

	}

	@Test(
			description = "Get a folder in the app",
			groups = { "smoke" } )
	public void GetFolder_01() throws HarnessException {

		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();

		
		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Briefcase);
		ZAssert.assertNotNull(briefcaseRootFolder, "Verify the Briefcase root folder is available");


		// Create the sub-folder

		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
					"<folder name='" + foldername + "' l='" + briefcaseRootFolder.getId() + "' view='document'/>" +
				"</CreateFolderRequest>");
		
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		// click on My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);
		SleepUtil.sleepVerySmall();

		// Verify the folder appears in the list
		boolean found = false;
		List<IOctListViewItem> items = app.zPageOctopus.zGetListViewItems();
		for (IOctListViewItem item : items) {
			if ( item.getListViewName().equals(foldername) ) {
				found = true;
				break;
			}
		}
		
		ZAssert.assertTrue(found, "Verify the folder appears in the list");
		
		
	}

	@Test(
			description = "View 100 folders in the app",
			groups = { "functional" } )
	public void GetFolder_02() throws HarnessException {


		
		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Briefcase);
		ZAssert.assertNotNull(briefcaseRootFolder, "Verify the Briefcase root folder is available");


		// Create the sub-folder

		String foldername = null;
		
		for (int i = 0; i < 100; i++) {
			
			foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
			
			app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='" + foldername + "' l='" + briefcaseRootFolder.getId() + "' view='document'/>" +
					"</CreateFolderRequest>");
		}
		
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		// click on My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);
		SleepUtil.sleepVerySmall();

		// Verify the folder appears in the list
		boolean found = false;
		List<IOctListViewItem> items = app.zPageOctopus.zGetListViewItems();
		for (IOctListViewItem item : items) {
			if ( item.getListViewName().equals(foldername) ) {
				found = true;
				break;
			}
		}
		
		ZAssert.assertTrue(found, "Verify the folder appears in the list");

	}


}
