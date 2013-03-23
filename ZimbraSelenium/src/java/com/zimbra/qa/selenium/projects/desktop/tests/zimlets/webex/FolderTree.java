/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.desktop.tests.zimlets.webex;

import java.util.List;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.ZimletItem;
import com.zimbra.qa.selenium.framework.items.ZimletItem.CoreZimletItem;
import com.zimbra.qa.selenium.framework.items.ZimletItem.CoreZimletItem.CoreZimletName;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.TreeMail.FolderSection;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.TreeMail.FolderSectionAction;


public class FolderTree extends AjaxCommonTest {

	public FolderTree() {
		logger.info("New "+ FolderTree.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Verify the WebEx zimlet appears in the folder tree",
			groups = { "smoke" })
	public void FolderTree_01() throws HarnessException {
		ZimletItem webex = CoreZimletItem.getCoreZimlet(CoreZimletName.com_zimbra_webex, app);
		
		// Expand the zimlets section
		app.zTreeMail.zSectionAction(FolderSectionAction.Expand, FolderSection.Zimlets);
		
		// Get the list of zimlets
		List<ZimletItem> zimlets = app.zTreeMail.zListGetZimlets();
		
		// Find out if WebEx is listed
		ZimletItem found = null;
		for (ZimletItem zimlet : zimlets) {
			if ( webex.equals(zimlet) ) {
				found = zimlet;
			}
		}
		
		ZAssert.assertNotNull(found, "Verify the Webex Zimlet was found");
				
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
