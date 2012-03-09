package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.*;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.Locators.*;


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


	private void refresh() 
		throws HarnessException
	{
 		// Click on MyFiles tab
		// this extra click makes the history text displayed
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);	
	}

	
	@BeforeMethod(groups = { "always" })
	public void setup() 
	    throws HarnessException
	{		
		 // upload file before running test
		if (fileId == null) {		 
	 	    fileId = uploadFileViaSoap(app.zGetActiveAccount(),fileName);    
		
            refresh();	
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
        //TODO: verify there is no other message existed 
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
		VerifyCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileName));
		
		// verify uncheck action for 'new version' 
		VerifyUnCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileName));
		
	}
	
	@Test(description = "Functional test for check/uncheck 'favorite' checkbox with favorite/unfavorite actions", groups = { "functional" })
	public void RefineFavorite() throws HarnessException {
		
        // mark file as favorite via soap
		MarkFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
		
		// mark file as unfavorite via soap
		UnMarkFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
		refresh();
		
		// verify favorite text present
		VerifyCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.favorite(fileName));											
	
		// verify unfavorite text present
		VerifyCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.unfavorite(fileName));											
			
		// verify favorite text not present
		VerifyUnCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.favorite(fileName));											
	
		// verify unfavorite text not present
		VerifyUnCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.unfavorite(fileName));											
	
	}
	
	@Test(description = "Functional test for check/uncheck 'comment' checkbox", groups = { "functional" })
	public void RefineComment() throws HarnessException {
	   String comment = "Comment" + ZimbraSeleniumProperties.getUniqueString();

       MakeACommentViaSoap(app.zGetActiveAccount(), fileId, comment);
       refresh();
		
       // verify check action for 'comment' 
	   VerifyCheckAction(Locators.zHistoryFilterComment.locator,
				GetText.comment(fileName));
		
		// verify uncheck action for 'comment' 
	   VerifyUnCheckAction(Locators.zHistoryFilterComment.locator,
				GetText.comment(fileName));
		

	}

	@Test(description = "Functional test for check/uncheck 'rename' checkbox", groups = { "functional" })
	public void RefineRename() throws HarnessException {
	   String newName = "New Name " + ZimbraSeleniumProperties.getUniqueString() +
	                    fileName.substring(fileName.indexOf("."),fileName.length());

       RenameViaSoap(app.zGetActiveAccount(), fileId, newName);
       refresh();
		  
       // verify check action for 'rename' 
	   VerifyCheckAction(Locators.zHistoryFilterRename.locator,
				GetText.rename(fileName,newName));
		
		// verify uncheck action for 'rename' 
	   VerifyUnCheckAction(Locators.zHistoryFilterRename.locator,
				GetText.rename(fileName,newName));
		
       fileName= newName;
	}

	@Test(description = "Functional test for simultaneously check/uncheck 'new version' & 'favorite' checkbox", groups = { "functional" })
	public void RefineNewVersionFavorite() throws HarnessException {

        // mark file as favorite via soap
		MarkFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
		refresh();
		
		// Make checks for 'new version' & 'favorite'
		app.zPageHistory.zToolbarCheckMark(Locators.zHistoryFilterNewVersion.locator, true);
		app.zPageHistory.zToolbarCheckMark(Locators.zHistoryFilterFavorites.locator, true);
		
		// Get the text 
		HistoryItem newversionItem = app.zPageHistory.isTextPresentInGlobalHistory(GetText.newVersion(fileName));
		HistoryItem favoriteItem = app.zPageHistory.isTextPresentInGlobalHistory(GetText.favorite(fileName));
					
		// Verify the text present
		ZAssert.assertNotNull(newversionItem, "Verify " +  GetText.newVersion(fileName) + " displayed");		
		ZAssert.assertNotNull(favoriteItem, "Verify " +  GetText.favorite(fileName) + " displayed");		
			
		// Verify the text matched
		ZAssert.assertEquals(newversionItem.getHistoryText(), GetText.newVersion(fileName), "Verify " + PageHistory.GetText.newVersion(fileName) + " matched");
		ZAssert.assertEquals(favoriteItem.getHistoryText(), GetText.favorite(fileName), "Verify " + PageHistory.GetText.favorite(fileName) + " matched");

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
