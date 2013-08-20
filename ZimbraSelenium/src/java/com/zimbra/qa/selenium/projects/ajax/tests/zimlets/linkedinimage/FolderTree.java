/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.zimlets.linkedinimage;

import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ZimletItem.*;
import com.zimbra.qa.selenium.framework.items.ZimletItem.CoreZimletItem.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.TreeMail.*;


public class FolderTree extends AjaxCommonTest {

	public FolderTree() {
		logger.info("New "+ FolderTree.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
		
	}
	
	@Bugs(	ids = "81078")
	@Test(	description = "Verify the LinkedIn zimlet appears in the folder tree",
			groups = { "functional" })
	public void FolderTree_01() throws HarnessException {
		
		
		//-- DATA
		
		ZimletItem linkedin = CoreZimletItem.getCoreZimlet(CoreZimletName.com_zimbra_linkedinimage, app);
		
		
		
		
		
		//-- GUI
		
		
		// Expand the zimlets section
		app.zTreeMail.zSectionAction(FolderSectionAction.Expand, FolderSection.Zimlets);
		
		// Get the list of zimlets
		List<ZimletItem> zimlets = app.zTreeMail.zListGetZimlets();
		
		
		//-- VERIFICATION
		
		
		// Find out if LinkedIn is listed
		ZimletItem found = null;
		for (ZimletItem zimlet : zimlets) {
			if (linkedin.equals(zimlet) ) {
				found = zimlet;
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
