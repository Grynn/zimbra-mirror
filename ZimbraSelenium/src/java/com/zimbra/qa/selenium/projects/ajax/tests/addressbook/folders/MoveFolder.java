/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class MoveFolder extends AjaxCommonTest {

	public MoveFolder() {
		logger.info("New "+ MoveFolder.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Drag one folder from top level and Drop into sub folder", groups = { "smoke" })
	public void DnDFromTopLevelToSubFolder() throws HarnessException {

		//-- Data
		
		// Root folder
		FolderItem userRoot= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		
		// Subfolders in root
		String name1 = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name1 + "' view='contact' l='"+ userRoot.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem addressbook1 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name1);

		String name2 = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name2 + "' view='contact' l='"+ userRoot.getId() +"'/>" +
				"</CreateFolderRequest>");
		FolderItem addressbook2 = FolderItem.importFromSOAP(app.zGetActiveAccount(), name2);


		//-- GUI
		
		// Refresh to get new addressbooks
		app.zPageAddressbook.zRefresh();
	

		
		app.zPageAddressbook.zDragAndDrop(
				"css=div#zov__main_Contacts td#zti__main_Contacts__" + addressbook1.getId() + "_textCell:contains("+ addressbook1.getName() + ")",
				"css=div#zov__main_Contacts td#zti__main_Contacts__" + addressbook2.getId() + "_textCell:contains("+ addressbook2.getName() + ")");
			

		
		//-- Verification
		
		// Verify the folder is now in the other subfolder
		FolderItem actual = FolderItem.importFromSOAP(app.zGetActiveAccount(), addressbook1.getName());
		ZAssert.assertNotNull(actual, "Verify the subfolder is again available");
		ZAssert.assertEquals(actual.getParentId(), addressbook2.getId(), "Verify the subfolder's parent is now the other subfolder");


	}




}
