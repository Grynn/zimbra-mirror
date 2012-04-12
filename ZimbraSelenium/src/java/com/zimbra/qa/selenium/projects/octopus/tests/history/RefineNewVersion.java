package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.*;

public class RefineNewVersion extends HistoryCommonTest {
 
	
	public RefineNewVersion() {
		super();
		logger.info("New " + RefineNewVersion.class.getCanonicalName());
	}


			
	@Test(description = "Verify check 'new version' checkbox", groups = { "functional" })
	public void RefineCheckNewVersion() throws HarnessException {
										
		// verify check action for 'new version' 
		verifyCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileName));
				
	}
	
	@Test(description = "Verify uncheck 'new version' checkbox", groups = { "smoke" })
	public void RefineUnCheckNewVersion() throws HarnessException {
										
		// verify uncheck action for 'new version' 
		verifyUnCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileName));
				
	}


	
}
