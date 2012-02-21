package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory;


public class RefineHistory extends OctopusCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;
	private boolean _fileAttached = false;
	private String _fileId = null;

	@BeforeMethod(groups = { "always" })
	public void testReset() {
		_folderName = null;
		_folderIsCreated = false;
		_fileId = null;
		_fileAttached = false;
	}

	public RefineHistory() {
		logger.info("New " + RefineHistory.class.getCanonicalName());

		// test starts at the History tab
		super.startingPage = app.zPageHistory;
		super.startingAccountPreferences = null;
	}

	private void VerifyCheckboxAction(Button button, String historyText) 
	    throws HarnessException
	{		
		// Check new version check box
		app.zPageHistory.zToolbarCheckMark(button);
		
		// check if the text present
		HistoryItem found = app.zPageHistory.isTextPresentInGlobalHistory(historyText);
			
		// verification
		ZAssert.assertNotNull(found, "Verify " +  historyText + " is found");		
		ZAssert.assertEquals(found.getHistoryText(), historyText, "Verify the history text matches");
		
		
		// UnCheck new version check box
		app.zPageHistory.zToolbarCheckMark(button);
		
		// check if the text present
		found = app.zPageHistory.isTextPresentInGlobalHistory(historyText);
			
		// verification
		ZAssert.assertNull(found, "Verify " +  historyText + " not found");		
		
	}
	
	
	@Test(description = "Functional test for check/uncheck 'new version' checkbox", groups = { "smoke" })
	public void RefineNewVersion() throws HarnessException {
		String fileName=JPG_FILE;
		
		// new version
		uploadFileViaSoap(app.zGetActiveAccount(),fileName);
        
		// Click on MyFiles tab 
		// this makes the history text displayed
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);
	
		// form the text
		String historyText = "You created version 1 of file " +  fileName +".";
						
		// verify check/uncheck action 
		VerifyCheckboxAction(Button.O_NEW_VERSION, historyText);
		
						
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
}
