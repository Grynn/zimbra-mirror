package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.*;

public class RefineHistory extends HistoryCommonTest {
 
	
	public RefineHistory() {
		logger.info("New " + RefineHistory.class.getCanonicalName());

		// test starts at the History tab
		super.startingPage = app.zPageHistory;
		super.startingAccountPreferences = null;
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
