package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.*;


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

	private void verifyCheckAction(String locator, String historyText) 
	    throws HarnessException
	{			
        //TODO: verify there is no other message existed 
		// Make a check
		app.zPageHistory.zToolbarCheckMark(locator, true);
		
		// check if the text present
		HistoryItem found = app.zPageHistory.isTextPresentInGlobalHistory(historyText);
			
		// verification
		ZAssert.assertNotNull(found, "Verify " +  historyText + " displayed");		
		
	}
	
	private void verifyUnCheckAction(String locator, String historyText) 
	    throws HarnessException
	{						
		// UnCheck the check box
		app.zPageHistory.zToolbarCheckMark(locator, false);
					
		// verification
		ZAssert.assertNull(app.zPageHistory.isTextPresentInGlobalHistory(historyText)
				, "Verify " +  historyText + " not found");		
		
	}
	
	private void verifyCheckUnCheckAction(String locator, String historyText) 
	    throws HarnessException
	{
		verifyCheckAction(locator,historyText);
		verifyUnCheckAction(locator,historyText);
		
	}
	
	@Test(description = "Verify check/uncheck 'new version' checkbox", groups = { "functional" })
	public void RefineNewVersion() throws HarnessException {
										
		// verify check|uncheck action for 'new version' 
		verifyCheckUnCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileName));
				
	}
	
	@Test(description = "Verify test for check/uncheck 'favorite' checkbox with favorite/unfavorite actions", groups = { "functional" })
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
	
	@Test(description = "Verify check/uncheck 'comment' checkbox", groups = { "functional" })
	public void RefineComment() throws HarnessException {
	   String comment = "Comment" + ZimbraSeleniumProperties.getUniqueString();

       makeCommentViaSoap(app.zGetActiveAccount(), fileId, comment);
       refresh();
		
       // verify check|uncheck action for 'comment' 
	   verifyCheckUnCheckAction(Locators.zHistoryFilterComment.locator,
				GetText.comment(fileName));
		
	}

	@Test(description = "Verify check/uncheck 'rename' checkbox", groups = { "functional" })
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

	
	@Test(description = "Verify check/uncheck 'sharing' checkbox", groups = { "functional" })
	public void RefineSharing() throws HarnessException {
	   // create 3  grantees' accounts
	   ZimbraAccount readGrantee = getNewAccount();
	   ZimbraAccount readWriteGrantee = getNewAccount();
	   ZimbraAccount adminGrantee = getNewAccount();
	   
	   // create a folder
	   FolderItem folder = createFolderViaSoap(app.zGetActiveAccount());
	   
	   // share read|readWrite|admin for the folder with grantees
	   shareFolderViaSoap(app.zGetActiveAccount(), readGrantee, folder,SHARE_AS_READ);
	   shareFolderViaSoap(app.zGetActiveAccount(), readWriteGrantee, folder,SHARE_AS_READWRITE);
	   shareFolderViaSoap(app.zGetActiveAccount(), adminGrantee, folder, SHARE_AS_ADMIN); 
	   refresh();

	   
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

	   
	  // revoke sharing the folder with grantees
	   revokeShareFolderViaSoap(app.zGetActiveAccount(), readGrantee, folder);
	   revokeShareFolderViaSoap(app.zGetActiveAccount(), readWriteGrantee, folder);
	   revokeShareFolderViaSoap(app.zGetActiveAccount(), adminGrantee, folder); 
       refresh();
       
	   
       // verify check action for 'revoke' 
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_READ,folder.getName(),readGrantee));
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),readWriteGrantee));
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_ADMIN,folder.getName(),adminGrantee));

	 // verify uncheck action for 'revoke' 
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_READ,folder.getName(),readGrantee));
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),readWriteGrantee));
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_ADMIN,folder.getName(),adminGrantee));
	   
	}

	@Test(description = "Functional test for simultaneously check/uncheck 'new version' & 'favorite' checkbox", groups = { "functional" })
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

	@Test(description = "Functional test for simultaneouly check all boxes", groups = { "functional" })
	public void RefineCheckAll() throws HarnessException {
	
		// mark|unmark file as favorite via soap
		markFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
		unMarkFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
		
		// share|revoke folder via soap
		ZimbraAccount readWriteGrantee = getNewAccount();
        FolderItem folder = createFolderViaSoap(app.zGetActiveAccount());		  
		shareFolderViaSoap(app.zGetActiveAccount(), readWriteGrantee, folder,SHARE_AS_READWRITE);
		revokeShareFolderViaSoap(app.zGetActiveAccount(), readWriteGrantee, folder);
		
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
		ZAssert.assertNotNull(app.zPageHistory.isTextPresentInGlobalHistory(GetText.share(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),readWriteGrantee)),
				"Verify history text for share displayed");		
		ZAssert.assertNotNull(app.zPageHistory.isTextPresentInGlobalHistory(GetText.revoke(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),readWriteGrantee)),
				"Verify history text for revoke displayed");		
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
