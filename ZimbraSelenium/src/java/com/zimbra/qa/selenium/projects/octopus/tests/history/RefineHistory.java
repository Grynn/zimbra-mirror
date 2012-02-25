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

	private void VerifyCheckAction(String locator, String historyText) 
	    throws HarnessException
	{			
		// Make a check
		app.zPageHistory.zToolbarCheckMark(locator, true);
		
	
		// check if the text present
		HistoryItem found = app.zPageHistory.isTextPresentInGlobalHistory(historyText);
			
		// verification
		ZAssert.assertNotNull(found, "Verify " +  historyText + " displayed");		
		ZAssert.assertEquals(found.getHistoryText(), historyText, "Verify " +  historyText + " matched");
		
	}
	
	private void VerifyUnCheckAction(String locator, String historyText) 
	    throws HarnessException
	{						
		// UnCheck the check box
		app.zPageHistory.zToolbarCheckMark(locator, false);
					
		// verification
		ZAssert.assertNull(app.zPageHistory.isTextPresentInGlobalHistory(historyText)
				, "Verify " +  historyText + " not found");		
		
	}
	
	
	@Test(description = "Functional test for check/uncheck 'new version' checkbox", groups = { "functional" })
	public void RefineNewVersion() throws HarnessException {
										
		// verify check action for 'new version' 
		VerifyCheckAction(PageHistory.Locators.zHistoryFilterNewVersion.locator,
				PageHistory.GetText.newVersion(fileName));
		
		// verify uncheck action for 'new version' 
		VerifyUnCheckAction(PageHistory.Locators.zHistoryFilterNewVersion.locator,
				PageHistory.GetText.newVersion(fileName));
		
	}
	
	@Test(description = "Functional test for check/uncheck 'favorite' checkbox", groups = { "functional" })
	public void RefineFavorite() throws HarnessException {
		
        // mark file as favorite via soap
		MarkFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
	
		// verify check action for 'favorite'
		VerifyCheckAction(PageHistory.Locators.zHistoryFilterFavorites.locator, 
				PageHistory.GetText.favorite(fileName));											
	
		// verify uncheck action for 'favorite'
		VerifyUnCheckAction(PageHistory.Locators.zHistoryFilterFavorites.locator, 
				PageHistory.GetText.favorite(fileName));											
	
	}
	
	@Test(description = "Functional test for simultaneously check/uncheck 'new version' & 'favorite' checkbox", groups = { "functional" })
	public void RefineNewVersionFavorite() throws HarnessException {

        // mark file as favorite via soap
		MarkFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);

		// Make checks for 'new version' & 'favorite'
		app.zPageHistory.zToolbarCheckMark(PageHistory.Locators.zHistoryFilterNewVersion.locator, true);
		app.zPageHistory.zToolbarCheckMark(PageHistory.Locators.zHistoryFilterFavorites.locator, true);
		
		// Get the text 
		HistoryItem newversionItem = app.zPageHistory.isTextPresentInGlobalHistory(PageHistory.GetText.newVersion(fileName));
		HistoryItem favoriteItem = app.zPageHistory.isTextPresentInGlobalHistory(PageHistory.GetText.favorite(fileName));
					
		// Verify the text present
		ZAssert.assertNotNull(newversionItem, "Verify " +  PageHistory.GetText.newVersion(fileName) + " displayed");		
		ZAssert.assertNotNull(favoriteItem, "Verify " +  PageHistory.GetText.favorite(fileName) + " displayed");		
			
		// Verify the text matched
		ZAssert.assertEquals(newversionItem.getHistoryText(), PageHistory.GetText.newVersion(fileName), "Verify " + PageHistory.GetText.newVersion(fileName) + " matched");
		ZAssert.assertEquals(favoriteItem.getHistoryText(), PageHistory.GetText.favorite(fileName), "Verify " + PageHistory.GetText.favorite(fileName) + " matched");

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
		//TODO: ?  
	}

}
