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
	protected static String fileInFolderName = PPT_FILE;
	protected static String fileInSubFolderName = TEXT_FILE;    
	protected static String fileInReadFolderName = WORD_FILE;
	protected static String fileInReadWriteFolderName = EXCEL_FILE;
	protected static String fileInAdminFolderName = LOG_FILE;


	protected static String fileId  = null;
	protected static String fileIdInFolder  = null;
	protected static String fileIdInSubFolder  = null;
	protected static String fileIdInReadFolder  = null;
	protected static String fileIdInReadWriteFolder  = null;
	protected static String fileIdInAdminFolder  = null;

	protected static String comment = null;
	protected static String newName = null;
	protected static FolderItem folder = null; 
	protected static FolderItem mainFolder = null;
	protected static FolderItem subFolder = null;

	
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
	 	   SleepUtil.sleepSmall();
   	   
	 	   folder = createFolderViaSoap(app.zGetActiveAccount());	 	  	            		
           SleepUtil.sleepSmall();
    	   
           mainFolder = FolderItem.importFromSOAP(
        		   app.zGetActiveAccount(), SystemFolder.Briefcase);
           
           SleepUtil.sleepSmall();
    	   comment = "Comment " + ZimbraSeleniumProperties.getUniqueString();
    	   newName = "New Name " + ZimbraSeleniumProperties.getUniqueString() 
           		   + fileName.substring(fileName.indexOf("."),fileName.length());

    		   
    	   
          // mark file as favorite via soap
   		   markFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
   		   SleepUtil.sleepSmall();
           
   		   // unmark file as favorite via soap
   		   unMarkFileFavoriteViaSoap(app.zGetActiveAccount(), fileId);
   		   SleepUtil.sleepSmall();
   		   
   		   // make comment via soap
   	       makeCommentViaSoap(app.zGetActiveAccount(), fileId, comment);
   	       SleepUtil.sleepSmall();
		   
		   //rename via soap
		   renameViaSoap(app.zGetActiveAccount(), fileId, newName);
		   SleepUtil.sleepSmall();
		   
		   //create the sub folder
		   subFolder = createFolderViaSoap(app.zGetActiveAccount(),folder);	 	  	            		
		   SleepUtil.sleepSmall();
	           	   
		   //upload file to subfolder
		   fileIdInSubFolder=uploadFileViaSoap(app.zGetActiveAccount(),fileInSubFolderName,subFolder);    	 	  	
		   SleepUtil.sleepSmall();
	           
		      	   
		   //upload file to folder
		   fileIdInFolder =uploadFileViaSoap(app.zGetActiveAccount(),fileInFolderName, folder);    	 	  	
		   SleepUtil.sleepSmall();
		   
		

		   //share revoke
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

	   mountReadFolderName = "Mount Read " + ZimbraSeleniumProperties.getUniqueString();
	   mountReadWriteFolderName = "Mount Read Write " + ZimbraSeleniumProperties.getUniqueString();
	   mountAdminFolderName = "Mount Admin " + ZimbraSeleniumProperties.getUniqueString();

	   mountFolderViaSoap(readGranter, app.zGetActiveAccount(), readFolder, SHARE_AS_READ, mainFolder, mountReadFolderName);
	   SleepUtil.sleepSmall();
	   
	   mountFolderViaSoap(readWriteGranter, app.zGetActiveAccount(), readWriteFolder, SHARE_AS_READWRITE, mainFolder, mountReadWriteFolderName);
	   SleepUtil.sleepSmall();
	   
	   mountFolderViaSoap(adminGranter, app.zGetActiveAccount(), adminFolder, SHARE_AS_ADMIN, mainFolder, mountAdminFolderName);
	   SleepUtil.sleepSmall();
	   
	   
	   //upload file to read folder		      
	   fileIdInReadFolder = uploadFileViaSoap(readGranter,fileInReadFolderName, 
			             FolderItem.importFromSOAP(readGranter,readFolder.getName()));    	 	  	
	   SleepUtil.sleepSmall();
	  
	   
	   //upload file to read write folder		      
	   fileIdInReadWriteFolder = uploadFileViaSoap(app.zGetActiveAccount(),fileInReadWriteFolderName, 
			                 FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(),mountReadWriteFolderName));    	 	  	
	   SleepUtil.sleepSmall();
	  
	   
	  //upload file to Admin folder		      		   
	   fileIdInAdminFolder = uploadFileViaSoap(app.zGetActiveAccount(),fileInAdminFolderName, 
			             FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(),mountAdminFolderName));    	 	  	
	   SleepUtil.sleepSmall();
	   

	   app.zPageOctopus.zRefresh();   
	   
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


	}
	
	
	private boolean isHistoryTextPresent(ArrayList<HistoryItem> currHistoryArray, String historyText) 
	throws HarnessException
	{
		
		boolean found = false;
			
		// checking all history records in the stream
		// verify if and only if the corresponding history text present
		for (HistoryItem item:currHistoryArray) {
			
			logger.info(item.getHistoryText());
			boolean isMatched = false;
	        
			// not check for "all types"
			for (int i=1; i<checkboxes.length; i++) {
				
				// if checkbox is checked
				if (app.zPageHistory.sIsChecked(checkboxes[i])) {					
					 isMatched |= item.getHistoryText().matches(historyRegexps[i]);					 					 
				}
			}
	
		   ZAssert.assertTrue(isMatched , "Verify " +  item.getHistoryText() + " is displayed if associated checkbox is checked");
	
		   if (item.getHistoryText().equals(historyText)) {
				 found = true; 
		   }					 			
		}			
	
		return found;
	}
	
	
	protected void verifyCheckAction(String locator, String historyText) 
	    throws HarnessException
	{		
		// Make a check
		app.zPageHistory.zToolbarCheckMark(locator, true);
	     	
		
		ZAssert.assertTrue(isHistoryTextPresent(app.zPageHistory.zListItem(), historyText)
				        , "Verify " + historyText + " is displayed");
	}
	
	protected void verifyUnCheckAction(String locator, String historyText) 
	    throws HarnessException
	{					
		// UnCheck the check box
		app.zPageHistory.zToolbarCheckMark(locator, false);

		ArrayList<HistoryItem> currHistoryArray = app.zPageHistory.zListItem();

        boolean allNotChecked=true;
		for (int i=0; i<checkboxes.length; i++) {
			allNotChecked &= (!app.zPageHistory.sIsChecked(checkboxes[i]));
		}


		//if all checkboxes not checked, the history text should present
		if (allNotChecked) {
            boolean found=false;
			for (int i=0; i< currHistoryArray.size()& !found; i++) {
				HistoryItem item = currHistoryArray.get(i);
			
				logger.info(item.getHistoryText());
	        
				found = item.getHistoryText().equals(historyText); 			
			}			

			ZAssert.assertTrue(found, "Verify " + historyText + " is displayed");
		} 
		
  		//otherwise, refine is active, the text should not be present
		else {
			ZAssert.assertFalse(isHistoryTextPresent(currHistoryArray, historyText)
					    , "Verify " + historyText + " should not be displayed");
		}
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
