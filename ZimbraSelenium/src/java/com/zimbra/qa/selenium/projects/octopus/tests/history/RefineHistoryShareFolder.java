package com.zimbra.qa.selenium.projects.octopus.tests.history;

import java.util.ArrayList;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.*;


public class RefineHistoryShareFolder extends OctopusCommonTest {
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
    
    //parallel array with checkboxes[]
    String[] historyRegexps = {
		"*",
		PageHistory.GetText.REGEXP.FAVORITE,
		PageHistory.GetText.REGEXP.COMMENT,
		PageHistory.GetText.REGEXP.SHARE,
		PageHistory.GetText.REGEXP.NEWVERSION,
		PageHistory.GetText.REGEXP.RENAME
    };
    
    
    //created 3 new accounts
    ZimbraAccount granter = getNewAccount();
    ZimbraAccount readGrantee = getNewAccount();
    ZimbraAccount readWriteGrantee = getNewAccount();
    ZimbraAccount adminGrantee = getNewAccount();
    
    FolderItem folder = null;	 
    
	        
	public RefineHistoryShareFolder() {
		logger.info("New " + RefineHistoryShareFolder.class.getCanonicalName());

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
		if (fileId == null) {
			//create a new granter's folder
			folder = createFolderViaSoap(granter);
		
			//granter share the folder with admin access 
			shareFolderViaSoap(granter,app.zGetActiveAccount(), folder,SHARE_AS_ADMIN);		   
			SleepUtil.sleepSmall();
		
			//accpet and mount the shared folder
			
			//upload file to the shared folder
		    fileId = uploadFileViaSoap(app.zGetActiveAccount(),fileName, folder);    
			   		
	        // mark file as favorite via soap
   		    markFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
   		
           // unmark file as favorite via soap
   		   unMarkFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
   		 
		   // share read|readWrite|admin for the folder with grantees
		   shareFolderViaSoap(app.zGetActiveAccount(), readGrantee, folder,SHARE_AS_READ);		   
		   SleepUtil.sleepSmall();
		   
		   shareFolderViaSoap(app.zGetActiveAccount(), readWriteGrantee, folder,SHARE_AS_READWRITE);
		   SleepUtil.sleepSmall();
		   
		   shareFolderViaSoap(app.zGetActiveAccount(), adminGrantee, folder, SHARE_AS_ADMIN); 		   
		   app.zPageOctopus.zRefresh();

		   // revoke sharing the folder with grantees
		   revokeShareFolderViaSoap(app.zGetActiveAccount(), readGrantee, folder);
		   SleepUtil.sleepSmall();
		   
		   revokeShareFolderViaSoap(app.zGetActiveAccount(), readWriteGrantee, folder);
		   SleepUtil.sleepSmall();
		   
		   revokeShareFolderViaSoap(app.zGetActiveAccount(), adminGrantee, folder); 
		   app.zPageOctopus.zRefresh();

		}
		   
		// reset - uncheck all check boxes
		for (int i=0; i<checkboxes.length; i++) {
			if (app.zPageHistory.sIsChecked(checkboxes[i])) {
				app.zPageHistory.zToolbarCheckMark(checkboxes[i],false);
			}
		}

	 }

	private void verifyCheckAction(String locator, String historyText, String... regExpArray) 
    throws HarnessException
{		
	String historyRegexp = historyText;
	if (regExpArray.length > 0) {
		historyRegexp = regExpArray[0];
	}

	
	
	// Make a check
	app.zPageHistory.zToolbarCheckMark(locator, true);
	
	
    
	//get current history items
	ArrayList<HistoryItem> currHistoryArray = app.zPageHistory.zListItem();
	
	
	boolean found = false;

	
	// verify if and only if the corresponding history text present
	for (HistoryItem item:currHistoryArray) {
		
		logger.info(item.getHistoryText());
		boolean isMatched = false;
        
		// Verify only history texts associated with check boxes present
		// not check for "all types"
		for (int i=1; i<checkboxes.length; i++) {
			
			// if checkbox is checked
			if (app.zPageHistory.sIsChecked(checkboxes[i])) {					
				 isMatched |= item.getHistoryText().matches(historyRegexps[i]);					 					 
			}
		}
						
		 ZAssert.assertTrue(isMatched , "Verify " +  item.getHistoryText() + " displayed");

		 if (item.getHistoryText().equals(historyText)) {
			 found = true; 
		 }					 

		
	}			
	
	ZAssert.assertTrue(found, "Verify " + historyText + " is displayed");
}

private void verifyUnCheckAction(String locator, String historyText, String... regExpArray) 
    throws HarnessException
{					
	String historyRegexp = historyText;
	if (regExpArray.length > 0) {
		historyRegexp = regExpArray[0];
	}
			
	// UnCheck the check box
	app.zPageHistory.zToolbarCheckMark(locator, false);

	//get current history items
	ArrayList<HistoryItem> currHistoryArray = app.zPageHistory.zListItem();

	//count the associated history texts
	int total=0;
	for (HistoryItem item:currHistoryArray) {
		if (item.getHistoryText().matches(historyRegexp)) 
			total++;					 
	}
				
	// verification 		
	ZAssert.assertGreaterThan(total,0, "Verify " + historyText + " present");
	ZAssert.assertGreaterThanEqualTo( currHistoryArray.size(), total, 
			             "Verify " +  historyRegexp + " not refined");		
	
}

	private void verifyCheckUnCheckAction(String locator, String historyText) 
	    throws HarnessException
	{
		verifyCheckAction(locator,historyText);
		verifyUnCheckAction(locator,historyText);
		
	}
	
	@Test(description = "Verify check 'new version' checkbox", groups = { "skip" })
	public void RefineCheckNewVersion() throws HarnessException {
										
		// verify check action for 'new version' 
		verifyCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileName));
				
	}
	
	@Test(description = "Verify check/uncheck 'new version' checkbox", groups = { "skip" })
	public void RefineNewVersion() throws HarnessException {
										
		// verify check|uncheck action for 'new version' 
		verifyCheckUnCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileName));
				
	}
	
	@Test(description = "Verify test for check/uncheck 'favorite' checkbox with favorite/unfavorite actions", groups = { "skip" })
	public void RefineFavorite() throws HarnessException {
		
        // mark file as favorite via soap
		markFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
		
		// mark file as unfavorite via soap
		unMarkFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
		refresh();
		
		// verify favorite text present
		verifyCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.favorite(fileName));											
	
		// verify unfavorite text present
		verifyCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.unfavorite(fileName));											
			
		// verify favorite text not present
		verifyUnCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.favorite(fileName));											
	
		// verify unfavorite text not present
		verifyUnCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.unfavorite(fileName));											
	
	}
	
	@Test(description = "Verify check/uncheck 'comment' checkbox", groups = { "skip" })
	public void RefineComment() throws HarnessException {
	   String comment = "Comment" + ZimbraSeleniumProperties.getUniqueString();

       makeCommentViaSoap(app.zGetActiveAccount(), fileId, comment);
       refresh();
		
       // verify check|uncheck action for 'comment' 
	   verifyCheckUnCheckAction(Locators.zHistoryFilterComment.locator,
				GetText.comment(fileName));
		
	}

	@Test(description = "Verify check/uncheck 'rename' checkbox", groups = { "skip" })
	public void RefineRename() throws HarnessException {
	   String newName = "New Name " + ZimbraSeleniumProperties.getUniqueString() +
	                    fileName.substring(fileName.indexOf("."),fileName.length());

       renameViaSoap(app.zGetActiveAccount(), fileId, newName);
       refresh();
		  
       // verify check|uncheck action for 'rename' 
	   verifyCheckUnCheckAction(Locators.zHistoryFilterRename.locator,
				GetText.rename(fileName,newName));
		
       fileName= newName;
	}

	
	@Test(description = "Verify check/uncheck 'sharing' checkbox", groups = { "skip" })
	public void RefineSharing() throws HarnessException {

       // verify check action for 'sharing' 
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_READ,folder.getName(),readGrantee));
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),readWriteGrantee));
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_ADMIN,folder.getName(),adminGrantee));

	 // verify uncheck action for 'sharing' 
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_READ,folder.getName(),readGrantee));
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),readWriteGrantee));
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_ADMIN,folder.getName(),adminGrantee));

	   	   
	}

	@Test(description = "Functional test for simultaneously check/uncheck 'new version' & 'favorite' checkbox", groups = { "skip" })
	public void RefineNewVersionFavorite() throws HarnessException {

        // mark file as favorite via soap
		markFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
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

	@Test(description = "Functional test for simultaneouly check all boxes", groups = { "skip" })
	public void RefineCheckAll() throws HarnessException {
	
		// mark|unmark file as favorite via soap
		markFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
		unMarkFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
				
		//make comment via soap
		String comment = "Comment" + ZimbraSeleniumProperties.getUniqueString();
		makeCommentViaSoap(app.zGetActiveAccount(), fileId, comment);
	
		//rename via soap
		String newName = "New Name " + ZimbraSeleniumProperties.getUniqueString() +
        fileName.substring(fileName.indexOf("."),fileName.length());
		renameViaSoap(app.zGetActiveAccount(), fileId, newName);

		refresh();
	
        // check boxes
		for (int i=0; i<checkboxes.length; i++) {
				app.zPageHistory.zToolbarCheckMark(checkboxes[i],true);
		}

		// verification
		ZAssert.assertNotNull(app.zPageHistory.isTextPresentInGlobalHistory(GetText.newVersion(fileName)),
				"Verify history text for new verstion displayed");		
		ZAssert.assertNotNull(app.zPageHistory.isTextPresentInGlobalHistory(GetText.favorite(fileName)),
				"Verify history text for favorite displayed");		
		ZAssert.assertNotNull(app.zPageHistory.isTextPresentInGlobalHistory(GetText.unfavorite(fileName)),
				"Verify history text for unfavorite displayed");		
		ZAssert.assertNotNull(app.zPageHistory.isTextPresentInGlobalHistory(GetText.comment(fileName)),
				"Verify history text for comment displayed");		
		ZAssert.assertNotNull(app.zPageHistory.isTextPresentInGlobalHistory(GetText.rename(fileName,newName)),
				"Verify history text for rename displayed");		

			
		
	}
	
	@AfterClass(groups = { "always" })
	public void teardown() 
	    throws HarnessException
	{		
		//TODO: ?  
	}

}
