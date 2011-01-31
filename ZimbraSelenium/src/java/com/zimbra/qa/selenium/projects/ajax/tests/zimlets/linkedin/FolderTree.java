package com.zimbra.qa.selenium.projects.ajax.tests.zimlets.linkedin;

import java.util.List;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.ZimletItem;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class FolderTree extends AjaxCommonTest {

	public FolderTree() {
		logger.info("New "+ FolderTree.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Verify the LinkedIn zimlet appears in the folder tree",
			groups = { "sanity" })
	public void FolderTree_01() throws HarnessException {
		
		// Expand the zimlets section
		app.zTreeMail.zExpandZimlets();
		
		// Get the list of zimlets
		List<ZimletItem> zimlets = app.zTreeMail.zListGetZimlets();
		
		// Find out if LinkedIn is listed
		ZimletItem found = null;
		for (ZimletItem z : zimlets) {
			if ( ZimletItem.getLinkedinZimlet().getName().equals(z.getName()) ) {
				found = z;
			}
		}
		
		ZAssert.assertNotNull(found, "Verify the LinkedIn Zimlet was found");
				
	}


	// All these tests require the Folder tree to be fully loaded
	@BeforeMethod( groups = { "always" } )
	public void folderTreeBeforeMethod() throws HarnessException {
		logger.info("folderTreeBeforeMethod: start");
		
		for (int i = 0; i < 10; i++) {
			
			if ( app.zTreeMail.zIsActive() ) {
				return; // Done!
			}
			
			SleepUtil.sleep(1000);
			
		}
		logger.info("folderTreeBeforeMethod: finish");

	}


}
