package com.zimbra.qa.selenium.projects.octopus.tests.login.performance;

import java.io.File;
import java.io.IOException;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;

public class ZmMyFilesApp extends OctopusCommonTest {

	public ZmMyFilesApp() {
		logger.info("New " + ZmMyFilesApp.class.getCanonicalName());

		// test starts at the My Files tab
		super.startingPage = app.zPageMyFiles;
		super.startingAccountPreferences = null;
	}


	@Test(	description = "Measure the time to load the ajax client",
			groups = { "performance" })
	public void FileContextMenu_01() throws HarnessException, IOException {
		
		/**
		 * Load all the sample files into the account
		 */
		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Briefcase);
		String filepath = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/Files/Basic01";
		
		File directory = new File(filepath);
		ZAssert.assertTrue(directory.exists(), "Verify the sample files exist");
		
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			
			String aid = app.zGetActiveAccount().uploadFile(files[i].getCanonicalPath());
			
			app.zGetActiveAccount().soapSend(
					"<SaveDocumentRequest xmlns='urn:zimbraMail'>" +
						"<doc l='" + briefcaseRootFolder.getId() + "'>" +
							"<upload id='" + aid + "'/>" +
						"</doc>" +
					"</SaveDocumentRequest>");

		}
		

		// click on My Files tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		
		// Add perf checks here
		throw new HarnessException("Implement perf verification here.  See http://bugzilla.zimbra.com/show_bug.cgi?id=65989");
	}
	
}
	
