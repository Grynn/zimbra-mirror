package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.*;
import java.util.ArrayList;

public class RefineHistory extends OctopusCommonTest {
 
	String fileName=JPG_FILE;
	String fileId  = null;
	String comment = null;
	String newName = null;
	
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
    
    
    ZimbraAccount readGrantee = getNewAccount();;
	ZimbraAccount readWriteGrantee = getNewAccount();;
	ZimbraAccount adminGrantee = getNewAccount();;
	   
	FolderItem folder = null; 
	
	public RefineHistory() {
		logger.info("New " + RefineHistory.class.getCanonicalName());

		// test starts at the History tab
		super.startingPage = app.zPageHistory;
		super.startingAccountPreferences = null;
	}


	private void refresh() 
		throws HarnessException
	{
		//work around for bug #
		//TODO: remove after bug fixed 
		app.zPageOctopus.zRefresh();

 		// Click on MyFiles tab
		// this extra click makes the history text displayed
		//app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);
		
	   
		// Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);
		
	}

	
	@BeforeMethod(groups = { "always" })
	public void setup() 
	    throws HarnessException
	{		
		// upload file,folder 
		// share revoke folder
		// comment, rename, favorite/unfavorite file
		// before running test
		if (fileId == null) {		 
	 	   fileId = uploadFileViaSoap(app.zGetActiveAccount(),fileName);    	 	  	
           folder = createFolderViaSoap(app.zGetActiveAccount());	 	  	            		
           comment = "Comment " + ZimbraSeleniumProperties.getUniqueString();
    	   newName = "New Name " + ZimbraSeleniumProperties.getUniqueString() 
           		   + fileName.substring(fileName.indexOf("."),fileName.length());

           
           
          // mark file as favorite via soap
   		   markFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
   		
           // unmark file as favorite via soap
   		   unMarkFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
   		 
   		   
   		   // make comment via soap
   	       makeCommentViaSoap(app.zGetActiveAccount(), fileId, comment);
   	       
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
		   
		   //rename via soap
		   renameViaSoap(app.zGetActiveAccount(), fileId, newName);
		   
		}

		refresh();

			
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
	
		// Verify only history texts associated with check boxes present
		// not check for "all types"
		for (int i=1; i<checkboxes.length; i++) {
			// if checkbox is checked
			if (app.zPageHistory.sIsChecked(checkboxes[i])) {
				// verify if and only if the corresponding history text present
				for (HistoryItem item:currHistoryArray) {		
					
					 ZAssert.assertTrue(item.getHistoryText().matches(historyRegexps[i]), 
							 "Verify " +  item.getHistoryText() + " matches with " + historyRegexps[i] + " displayed");
					 
					 logger.info(item.getHistoryText());
					 if (item.getHistoryText().equals(historyText)) {
						 found = true; 
					 }
					 
				}
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
	
	@Test(description = "Verify check 'new version' checkbox", groups = { "smoke" })
	public void RefineCheckNewVersion() throws HarnessException {
										
		// verify check action for 'new version' 
		verifyCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileName));
				
	}
	
	@Test(description = "Verify uncheck 'new version' checkbox", groups = { "functional" })
	public void RefineUnCheckNewVersion() throws HarnessException {
										
		// verify uncheck action for 'new version' 
		verifyUnCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileName));
				
	}
	
	@Test(description = "Verify check 'favorite' checkbox with favorite action", groups = { "smoke" })
	public void RefineCheckFavorite() throws HarnessException {
		
        // verify favorite text present
		verifyCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.favorite(fileName));											

	}
	
	@Test(description = "Verify uncheck 'favorite' checkbox with favorite action", groups = { "functional" })
	public void RefineUnCheckFavorite() throws HarnessException {
		
		// verify favorite text not present
		verifyUnCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.favorite(fileName), PageHistory.GetText.REGEXP.FAVORITE);											

	}
	
	@Test(description = "Verify check 'favorite' checkbox with non favorite action", groups = { "smoke" })
	public void RefineCheckNonFavorite() throws HarnessException {
		        
		// verify non favorite text present
		verifyCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.favorite(fileName));											

	}
	

	@Test(description = "Verify uncheck 'favorite' checkbox with non favorite action", groups = { "functional" })
	public void RefineUnCheckNonFavorite() throws HarnessException {
		
		// verify non favorite text not present
		verifyUnCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.favorite(fileName));											

	}
		
		
	@Test(description = "Verify check 'comment' checkbox", groups = { "smoke" })
	public void RefineCheckComment() throws HarnessException {
    		
       // verify check action for 'comment' 
	   verifyCheckAction(Locators.zHistoryFilterComment.locator,
				GetText.comment(fileName));
		
	}

	@Test(description = "Verify uncheck 'comment' checkbox", groups = { "functional" })
	public void RefineUnCheckComment() throws HarnessException {
			
       // verify uncheck action for 'comment' 
	   verifyCheckUnCheckAction(Locators.zHistoryFilterComment.locator,
				GetText.comment(fileName));
		
	}

	@Test(description = "Verify check 'rename' checkbox", groups = { "smoke" })
	public void RefineCheckRename() throws HarnessException {
	   		  
       // verify check action for 'rename' 
	   verifyCheckAction(Locators.zHistoryFilterRename.locator,
				GetText.rename(fileName,newName));
		       
	}


	@Test(description = "Verify uncheck 'rename' checkbox", groups = { "functional" })
	public void RefineUnCheckRename() throws HarnessException {
    		  
       // verify uncheck action for 'rename' 
	   verifyUnCheckAction(Locators.zHistoryFilterRename.locator,
				GetText.rename(fileName,newName));
		       
	}

	
	@Test(description = "Verify check 'sharing' checkbox for sharing action ", groups = { "smoke" })
	public void RefineCheckSharingShareAction() throws HarnessException {
	   	   
       // verify check action for 'sharing' 
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_READ,folder.getName(),readGrantee));
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),readWriteGrantee));
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_ADMIN,folder.getName(),adminGrantee));
	}
	   
	@Test(description = "Verify uncheck 'sharing' checkbox for sharing action", groups = { "functional" })
	public void RefineUnCheckSharingShareAction() throws HarnessException {
	   
	   	   
	 // verify uncheck action for 'sharing' 
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_READ,folder.getName(),readGrantee));
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),readWriteGrantee));
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_ADMIN,folder.getName(),adminGrantee));

	}
	
	
	@Test(description = "Verify check 'sharing' checkbox for revoke action", groups = { "smoke" })
	public void RefineCheckSharingRevokeAction() throws HarnessException {
	   
       // verify check action for 'revoke' 
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_READ,folder.getName(),readGrantee));
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),readWriteGrantee));
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_ADMIN,folder.getName(),adminGrantee));
	}
	
	@Test(description = "Verify uncheck 'sharing' checkbox for revoke action", groups = { "functional" })
	public void RefineUnCheckSharingRevokeAction() throws HarnessException {
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

		// verify check action for 'new version' 
		verifyCheckAction(Locators.zHistoryFilterNewVersion.locator,
				GetText.newVersion(fileName));

        // verify favorite text present
		verifyCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.favorite(fileName));											

	}

	@Test(description = "Functional test for simultaneouly check all boxes", groups = { "functional" })
	public void RefineCheckAll() throws HarnessException {
        // since class variable fileName should be changed following any rename test case run,
		// local variables are used here to overwrite them 
		String fileName = PPT_FILE;
		String  fileId = uploadFileViaSoap(app.zGetActiveAccount(), fileName);    	
		  
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
