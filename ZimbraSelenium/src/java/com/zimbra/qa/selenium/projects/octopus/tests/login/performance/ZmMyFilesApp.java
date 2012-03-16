package com.zimbra.qa.selenium.projects.octopus.tests.login.performance;

import java.io.File;
import java.io.IOException;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.ui.PageLogin.Locators;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;

public class ZmMyFilesApp extends OctopusCommonTest {

	public ZmMyFilesApp() {
		logger.info("New " + ZmMyFilesApp.class.getCanonicalName());

		// test starts at the My Files tab
		super.startingPage = app.zPageLogin;
		super.startingAccountPreferences = null;
	}


	@Test(	description = "Measure the time to load the ajax client",
			groups = { "performance" })
	public void ZmMyFilesApp_01() throws HarnessException, IOException {
		
		/**
		 * Load all the sample files into the account
		 */
		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(ZimbraAccount.AccountZWC(), SystemFolder.Briefcase);
		String filepath = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/Files/Basic01";
		
		File directory = new File(filepath);
		ZAssert.assertTrue(directory.exists(), "Verify the sample files exist");
		
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			
			String aid = ZimbraAccount.AccountZWC().uploadFile(files[i].getCanonicalPath());
			
			ZimbraAccount.AccountZWC().soapSend(
					"<SaveDocumentRequest xmlns='urn:zimbraMail'>" +
						"<doc l='" + briefcaseRootFolder.getId() + "'>" +
							"<upload id='" + aid + "'/>" +
						"</doc>" +
					"</SaveDocumentRequest>");

		}
		

		app.zPageLogin.zNavigateTo();

		app.zPageLogin.zSetLoginName(ZimbraAccount.AccountZWC().EmailAddress);
		app.zPageLogin.zSetLoginPassword(ZimbraAccount.AccountZWC().Password);

		// PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailApp, "Login to the ajax client (mail app)");

		// Click the Login button
		app.zPageLogin.sClick(Locators.zBtnLogin);

		// PerfMetrics.waitTimestamp(token);
				
		// Wait for the app to load
		app.zPageOctopus.zWaitForActive();

		
		// Add perf checks here
		throw new HarnessException("Implement perf verification here.  See http://bugzilla.zimbra.com/show_bug.cgi?id=65989");
	}
	
	@Test(	description = "Measure the time to load the ajax client with 100 folders",
		groups = { "performance" })
	public void ZmMyFilesApp_02() throws HarnessException {
			
			FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(ZimbraAccount.AccountZWC(), SystemFolder.Briefcase);
			ZAssert.assertNotNull(briefcaseRootFolder, "Verify the Briefcase root folder is available");


			// Create 100 sub-folders

			String foldername = null;
			
			for (int i = 0; i < 100; i++) {
				
				foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
				
				ZimbraAccount.AccountZWC().soapSend(
						"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
							"<folder name='" + foldername + "' l='" + briefcaseRootFolder.getId() + "' view='document'/>" +
						"</CreateFolderRequest>");
			}
			
			FolderItem folder = FolderItem.importFromSOAP(ZimbraAccount.AccountZWC(), foldername);
			ZAssert.assertNotNull(folder, "Verify the subfolder is available");

			
			
			app.zPageLogin.zNavigateTo();

			app.zPageLogin.zSetLoginName(ZimbraAccount.AccountZWC().EmailAddress);
			app.zPageLogin.zSetLoginPassword(ZimbraAccount.AccountZWC().Password);

			// PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailApp, "Login to the ajax client (mail app)");

			// Click the Login button
			app.zPageLogin.sClick(Locators.zBtnLogin);

			// PerfMetrics.waitTimestamp(token);
					
			// Wait for the app to load
			app.zPageOctopus.zWaitForActive();

			
			// Add perf checks here
			throw new HarnessException("Implement perf verification here.  See http://bugzilla.zimbra.com/show_bug.cgi?id=65989");
			
			
	}
	
}

	
