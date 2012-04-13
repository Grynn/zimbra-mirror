package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.*;
import java.util.ArrayList;

public class HistoryCommonTest extends OctopusCommonTest {
	protected static String fileName=JPG_FILE;
	protected static String fileId  = null;
	protected static String comment = null;
	protected static String newName = null;
	protected static FolderItem folder = null; 
	protected static FolderItem mainFolder = null;
	
	protected static FolderItem readFolder = null;
	protected static FolderItem readWriteFolder = null;	
	protected static FolderItem adminFolder = null;
	
	protected static String mountReadFolderName = null;
	protected static String mountReadWriteFolderName = null;
	protected static String mountAdminFolderName = null;
	
	protected static String[] checkboxes = {
		PageHistory.Locators.zHistoryFilterAllTypes.locator,
		PageHistory.Locators.zHistoryFilterFavorites.locator,
		PageHistory.Locators.zHistoryFilterComment.locator,
		PageHistory.Locators.zHistoryFilterSharing.locator,
		PageHistory.Locators.zHistoryFilterNewVersion.locator,
		PageHistory.Locators.zHistoryFilterRename.locator    		
    }; 

    //parallel array with checkboxes[]
	protected static String[] historyRegexps = {
		"*",
		PageHistory.GetText.REGEXP.FAVORITE,
		PageHistory.GetText.REGEXP.COMMENT,
		PageHistory.GetText.REGEXP.SHARE,
		PageHistory.GetText.REGEXP.NEWVERSION,
		PageHistory.GetText.REGEXP.RENAME
    };
    
    
	protected static ZimbraAccount readGrantee = null;
	protected static ZimbraAccount readWriteGrantee = null;
	protected static ZimbraAccount adminGrantee = null;
	   

	protected static ZimbraAccount readGranter = null;
	protected static ZimbraAccount readWriteGranter = null;
	protected static ZimbraAccount adminGranter = null;

	public HistoryCommonTest() {
		// test starts at the History tab
		super.startingPage = app.zPageHistory;
		super.startingAccountPreferences = null;
	}


	protected void refresh() 
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
	protected void setup() 
	    throws HarnessException
	{		
		// upload file,folder 
		// share revoke folder
		// comment, rename, favorite/unfavorite file
		// before running test
		if (fileId == null) {		 
	 	   fileId = uploadFileViaSoap(app.zGetActiveAccount(),fileName);    	 	  	
           folder = createFolderViaSoap(app.zGetActiveAccount());	 	  	            		
           mainFolder = FolderItem.importFromSOAP(
        		   app.zGetActiveAccount(), SystemFolder.Briefcase);
           
		           comment = "Comment " + ZimbraSeleniumProperties.getUniqueString();
    	   newName = "New Name " + ZimbraSeleniumProperties.getUniqueString() 
           		   + fileName.substring(fileName.indexOf("."),fileName.length());

    		   
    	   
          // mark file as favorite via soap
   		   markFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
   		
           // unmark file as favorite via soap
   		   unMarkFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
   		 
   		   
   		   // make comment via soap
   	       makeCommentViaSoap(app.zGetActiveAccount(), fileId, comment);
   	       
		   
		   //rename via soap
		   renameViaSoap(app.zGetActiveAccount(), fileId, newName);
		   
		   //shere revoke
		   setUpShareRevoke();
		   
		   //setup mount point
		   setUpMountPoint();   
		}

		refresh();

			
		// reset - uncheck all check boxes
		for (int i=0; i<checkboxes.length; i++) {
			if (app.zPageHistory.sIsChecked(checkboxes[i])) {
				app.zPageHistory.zToolbarCheckMark(checkboxes[i],false);
			}
		}


	 }

	private void setUpMountPoint() 
	  throws HarnessException
	{
 	   readGranter = getNewAccount();;
	   readWriteGranter = getNewAccount();;
	   adminGranter = getNewAccount();;

       readFolder = createFolderViaSoap(readGranter);	 	  	            		
       readWriteFolder = createFolderViaSoap(readWriteGranter);	 	  	            		
       adminFolder = createFolderViaSoap(adminGranter);	 	  	            		

	   mountReadFolderName = "mount read " + ZimbraSeleniumProperties.getUniqueString();
	   mountReadWriteFolderName = "mount read write " + ZimbraSeleniumProperties.getUniqueString();
	   mountAdminFolderName = "mount admin " + ZimbraSeleniumProperties.getUniqueString();

	   mountFolderViaSoap(readGranter, app.zGetActiveAccount(), readFolder, SHARE_AS_READ, mainFolder, mountReadFolderName);
	   SleepUtil.sleepSmall();
	   
	   mountFolderViaSoap(readWriteGranter, app.zGetActiveAccount(), readWriteFolder, SHARE_AS_READWRITE, mainFolder, mountReadWriteFolderName);
	   SleepUtil.sleepSmall();
	   
	   mountFolderViaSoap(adminGranter, app.zGetActiveAccount(), adminFolder, SHARE_AS_ADMIN, mainFolder, mountAdminFolderName);
		   
	   
	}
	private void setUpShareRevoke() 
	   throws HarnessException
	{
 	   readGrantee = getNewAccount();;
	   readWriteGrantee = getNewAccount();;
	   adminGrantee = getNewAccount();;

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

	}
	protected void verifyCheckAction(String locator, String historyText, String... regExpArray) 
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
	
	protected void verifyUnCheckAction(String locator, String historyText, String... regExpArray) 
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
	
	protected void verifyCheckUnCheckAction(String locator, String historyText) 
	    throws HarnessException
	{
		verifyCheckAction(locator,historyText);
		verifyUnCheckAction(locator,historyText);		
	}
	
	@AfterClass(groups = { "always" })
	public void teardown() 
	    throws HarnessException
	{		
		//TODO: ?  
	}
	

}
