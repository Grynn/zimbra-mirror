package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory;


public class RefineHistory extends OctopusCommonTest {
	String fileName=JPG_FILE;
	String fileId  =null;
    String[] checkboxes = {
    		PageHistory.Locators.zHistoryFilterAllTypes.locator,
    		PageHistory.Locators.zHistoryFilterFavorites.locator,
    		PageHistory.Locators.zHistoryFilterComment.locator,
    		PageHistory.Locators.zHistoryFilterSharing.locator,
    		PageHistory.Locators.zHistoryFilterNewVersion.locator,
    		PageHistory.Locators.zHistoryFilterRename.locator    		
    }; 
	
	public RefineHistory() {
		logger.info("New " + RefineHistory.class.getCanonicalName());

		// test starts at the History tab
		super.startingPage = app.zPageHistory;
		super.startingAccountPreferences = null;
	}


	@BeforeMethod(groups = { "always" })
	public void setup() 
	    throws HarnessException
	{		
		 // upload file before running test
		if (fileId == null) {		 
	 	    fileId = uploadFileViaSoap(app.zGetActiveAccount(),fileName);    
		
	 		// Click on MyFiles tab 
			// this extra click makes the history text displayed
			app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

			// Click on History tab
			app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);
		
		}
		
		// reset - uncheck all check boxes
		for (int i=0; i<checkboxes.length; i++) {
			if (app.zPageHistory.sIsChecked(checkboxes[i])) {
				app.zPageHistory.zToolbarCheckMark(checkboxes[i],false);
			}
		}

	 }

	private void VerifyCheckboxAction(String locator, String historyText) 
	    throws HarnessException
	{		
		// Make a check
		app.zPageHistory.zToolbarCheckMark(locator, true);
		
	
		// check if the text present
		HistoryItem found = app.zPageHistory.isTextPresentInGlobalHistory(historyText);
			
		// verification
		ZAssert.assertNotNull(found, "Verify " +  historyText + " is found");		
		ZAssert.assertEquals(found.getHistoryText(), historyText, "Verify the history text matches");
		
		
		// UnCheck the check box
		app.zPageHistory.zToolbarCheckMark(locator, false);
		
		// check if the text present
		found = app.zPageHistory.isTextPresentInGlobalHistory(historyText);
			
		// verification
		ZAssert.assertNull(found, "Verify " +  historyText + " not found");		
		
	}
	
	
	@Test(description = "Functional test for check/uncheck 'new version' checkbox", groups = { "smoke" })
	public void RefineNewVersion() throws HarnessException {
										
		// verify check/uncheck action for 'new version' 
		VerifyCheckboxAction(PageHistory.Locators.zHistoryFilterNewVersion.locator, 
				PageHistory.CONSTANTS.YOU +  PageHistory.CONSTANTS.NEW_VERSION_PREFIX 
			+   fileName + PageHistory.CONSTANTS.NEW_VERSION_POSTFIX);								
	}
	
	@Test(description = "Functional test for check/uncheck 'favorite' checkbox", groups = { "smoke" })
	public void RefineFavorite() throws HarnessException {
		
        // mark file as favorite via soap
		MarkFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
	

		// verify check/uncheck action for 'favorite'
		VerifyCheckboxAction(PageHistory.Locators.zHistoryFilterFavorites.locator, 
				PageHistory.CONSTANTS.YOU + PageHistory.CONSTANTS.FAVORITE_PREFIX 
			+   fileName + PageHistory.CONSTANTS.FAVORITE_POSTFIX);								
	}
	
	/*
	@Test(description = "", groups = { "smoke" })
	public void CreateAllActivitiesViaSoapCheckHistory() throws HarnessException {
		String fileName=JPG_FILE;
		
		// new version
		uploadFileViaSoap(app.zGetActiveAccount(),fileName);
        // rename
		
		// favorites
		// comment
		// sharing
		
		// Click on MyFiles tab 
		// this makes the history text displayed
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);

		// Check Favorites check box 
		app.zPageHistory.zToolbarCheckMark(Button.O_FAVORITES);

		// Check comment check box
		app.zPageHistory.zToolbarCheckMark(Button.O_COMMENT);

		// Check sharing check box
		app.zPageHistory.zToolbarCheckMark(Button.O_SHARING);

		// Check new version check box
		app.zPageHistory.zToolbarCheckMark(Button.O_NEW_VERSION);
		
		// Check rename check box	
		app.zPageHistory.zToolbarCheckMark(Button.O_RENAME);
		
	}
*/
	
	@AfterClass(groups = { "always" })
	public void teardown() 
	    throws HarnessException
	{		
		//TODO: delete fileName		    
	}

}
