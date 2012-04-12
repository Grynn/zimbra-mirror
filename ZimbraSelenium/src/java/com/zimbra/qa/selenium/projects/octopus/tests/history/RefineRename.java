package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.*;

public class RefineRename extends HistoryCommonTest {
 
	
	public RefineRename() {
		super();
		logger.info("New " + RefineRename.class.getCanonicalName());
	}


			
	@Test(description = "Verify check 'rename' checkbox", groups = { "functional" })
	public void RefineCheckRename() throws HarnessException {
	   		  
       // verify check action for 'rename' 
	   verifyCheckAction(Locators.zHistoryFilterRename.locator,
				GetText.rename(fileName,newName));
		       
	}


	@Test(description = "Verify uncheck 'rename' checkbox", groups = { "smoke" })
	public void RefineUnCheckRename() throws HarnessException {
    		  
       // verify uncheck action for 'rename' 
	   verifyUnCheckAction(Locators.zHistoryFilterRename.locator,
				GetText.rename(fileName,newName));
		       
	}

	



	
}
