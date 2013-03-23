/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.sharing;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.PagePreferences.ShareItem;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class FindSharesUnaccepted extends AjaxCommonTest {

	
	
	public FindSharesUnaccepted() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
		
	}


	@Test(
			description = "View the sharing preference page - show unaccepted shares",
			groups = { "functional" }
			)
	public void FindSharesUnaccepted_01() throws HarnessException {

		ZimbraAccount Owner = (new ZimbraAccount()).provision().authenticate();

		//*** Test Data
		String ownerFoldername = "ownerfolder"+ ZimbraSeleniumProperties.getUniqueString();
		
		FolderItem ownerInbox = FolderItem.importFromSOAP(Owner, FolderItem.SystemFolder.Inbox);
		ZAssert.assertNotNull(ownerInbox, "Verify the new owner folder exists");

		Owner.soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + ownerFoldername +"' l='" + ownerInbox.getId() +"'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem ownerFolder = FolderItem.importFromSOAP(Owner, ownerFoldername);
		ZAssert.assertNotNull(ownerFolder, "Verify the new owner folder exists");
		
		Owner.soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ ownerFolder.getId() +"' op='grant'>"
				+			"<grant d='" + app.zGetActiveAccount().EmailAddress + "' gt='usr' perm='r'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		

		
		
		//*** Test Steps
		
		// Refresh
		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);
		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Sharing);

		// Add username to the search box
		app.zPagePreferences.sType("css=input[id$='_owner_input']", Owner.EmailAddress);
		
		// Click "Find Shares"
		app.zPagePreferences.zClick("css=div[id$='_findButton'] td[id$='_title']");
		
		// Wait for the list to be populated
		app.zPagePreferences.zWaitForBusyOverlay();
		
		
		//*** Test Verification
		ShareItem found = null;
		List<ShareItem> items = app.zPagePreferences.zSharesGetUnaccepted();
		for (ShareItem item : items) {
			if (Owner.EmailAddress.contains(item.name)) {
				found = item;
				break;
			}
		}
		
		// ITEM: name:enus13186366449214 item:/Inbox/ownerfolder13186366576406 type:Folder role:Viewer folder:null emailenus13186366505625@testdomain.com


		ZAssert.assertNotNull(found, "verify the shared item appears in the list");
		ZAssert.assertStringContains(found.item, ownerFoldername, "Verify the owner foldername");
		ZAssert.assertEquals(found.type, "Folder", "Verify the owner item type"); // TODO: I18N
		ZAssert.assertEquals(found.email, app.zGetActiveAccount().EmailAddress, "Verify the share email destination");
		
		
	}
}
