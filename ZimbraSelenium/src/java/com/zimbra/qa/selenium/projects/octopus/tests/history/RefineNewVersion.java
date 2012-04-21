package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.*;

public class RefineNewVersion extends HistoryCommonTest {
    
	public RefineNewVersion() {
		super();
		logger.info("New " + RefineNewVersion.class.getCanonicalName());
	}

	 @BeforeMethod(groups= ("always")) 
	 public void setup()
	 throws HarnessException {
	    	super.setup();
	 }
			
	@Test(description = "Verify check 'new version' checkbox", groups = { "functional" })
	public void RefineCheckNewVersion() throws HarnessException {
										
		// verify check action for 'new version' 
		verifyCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileName));
	}
		
	@Test(description = "Verify check 'new version' checkbox for file in folder & subfolder", groups = { "functional" })
	public void RefineCheckNewVersionFileInFolder() throws HarnessException {

		verifyCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileInFolderName));

		verifyCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileInSubFolderName));

	}
		
	@Test(description = "Verify check 'new version' checkbox for file in read share folder", groups = { "functional" })
	public void RefineCheckNewVersionFileInReadShareFolder() throws HarnessException {

		verifyCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileInReadFolderName, readGranter.getPref("displayName")));
	}

	@Test(description = "Verify check 'new version' checkbox for file in read writeshare folder", groups = { "functional" })
	public void RefineCheckNewVersionFileInReadWriteShareFolder() throws HarnessException {

		verifyCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileInReadWriteFolderName));
	}

	@Test(description = "Verify check 'new version' checkbox for file in admin share folder", groups = { "functional" })
	public void RefineCheckNewVersionFileInAdminShareFolder() throws HarnessException {

		verifyCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileInAdminFolderName));

	}
	
	@Test(description = "Verify uncheck 'new version' checkbox", groups = { "smoke" })
	public void RefineUnCheckNewVersion() throws HarnessException {
										
		// verify uncheck action for 'new version' 
		verifyUnCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileName));
	}

	@Test(description = "Verify uncheck 'new version' checkbox for file in folder & subfolder", groups = { "smoke" })
	public void RefineUnCheckNewVersionFileInFolder() throws HarnessException {

		verifyUnCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileInFolderName));
		verifyUnCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileInSubFolderName));

	}

	@Test(description = "Verify uncheck 'new version' checkbox for file in read shared folder", groups = { "smoke" })
	public void RefineUnCheckNewVersionFileInReadShareFolder() throws HarnessException {

		verifyUnCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileInReadFolderName, readGranter.getPref("displayName")));
	}

	@Test(description = "Verify uncheck 'new version' checkbox for file in readwrite shared folder", groups = { "smoke" })
	public void RefineUnCheckNewVersionFileInReadWriteShareFolder() throws HarnessException {

		verifyUnCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileInReadWriteFolderName));
	}

	@Test(description = "Verify uncheck 'new version' checkbox for file in admin share folder", groups = { "smoke" })
	public void RefineUnCheckNewVersionFileInAdminShareFolder() throws HarnessException {

		verifyUnCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileInAdminFolderName));

	}

	
}
