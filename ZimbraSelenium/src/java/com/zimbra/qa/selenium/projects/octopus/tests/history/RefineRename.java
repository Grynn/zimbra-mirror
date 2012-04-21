package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.*;

public class RefineRename extends HistoryCommonTest {
 
	
	public RefineRename() {
		super();
		logger.info("New " + RefineRename.class.getCanonicalName());
	}

	 @BeforeMethod(groups= ("always")) 
	 public void setup()
	 throws HarnessException {
	    	super.setup();
	 }
			
	@Test(description = "Verify check 'rename' checkbox for rename file", groups = { "functional" })
	public void RefineCheckRenameFile() throws HarnessException {
	   		  
       // verify check action for 'rename' 
	   verifyCheckAction(Locators.zHistoryFilterRename.locator,
				GetText.rename(fileName,newName));

	}


	@Test(description = "Verify check 'rename' checkbox for rename folder", groups = { "functional" })
	public void RefineCheckRenameFolder() throws HarnessException {
	   		  
       // verify check action for 'rename' 
	   verifyCheckAction(Locators.zHistoryFilterRename.locator,
			GetText.rename(folderOldName,folderNewName,"folder"));

	}



	@Test(description = "Verify uncheck 'rename' checkbox for rename file", groups = { "smoke" })
	public void RefineUnCheckRenameFile() throws HarnessException {
    		  
       // verify uncheck action for 'rename' 
	   verifyUnCheckAction(Locators.zHistoryFilterRename.locator,
				GetText.rename(fileName,newName));
	}
		
	@Test(description = "Verify uncheck 'rename' checkbox for rename folder", groups = { "smoke" })
	public void RefineUnCheckRenameFolder() throws HarnessException {
	   
	   verifyUnCheckAction(Locators.zHistoryFilterRename.locator,
				GetText.rename(folderOldName,folderNewName,"folder"));

	}

	



	
}
