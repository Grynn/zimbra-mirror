/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders.retention;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder;

public class CreateDisposal extends PrefGroupMailByMessageTest {

	public CreateDisposal() {
		logger.info("New " + CreateDisposal.class.getCanonicalName());

	}

	@Test(
			description = "Save a new basic disposal on a folder (Context menu -> Edit -> Retention)", 
			groups = { "smoke" }
			)
	public void CreateDisposal_01() throws HarnessException {

		//-- Data
		
		// Create the subfolder
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" +  FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox).getId() + "'/>"
				+	"</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		
		//-- GUI
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Rename the folder using context menu
		DialogEditFolder dialog = (DialogEditFolder) app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folder);

		// Set to 2 years
		dialog.zDisposalEnable();
		dialog.zDisposalSetRange(
				DialogEditFolder.RetentionRangeType.Custom, 
				DialogEditFolder.RetentionRangeUnits.Years, 
				2);

		// Save
		dialog.zClickButton(Button.B_OK);
		
		
		//-- Verification
		
		// Verify the retention policy on the folder
		app.zGetActiveAccount().soapSend(
				"<GetFolderRequest xmlns='urn:zimbraMail'>"
			+		"<folder l='" + folder.getId() + "'/>"
			+	"</GetFolderRequest>");
		String lifetime = app.zGetActiveAccount().soapSelectValue("//mail:purge//mail:policy", "lifetime");
		String type = app.zGetActiveAccount().soapSelectValue("//mail:purge//mail:policy", "type");
		
		ZAssert.assertEquals(lifetime, "732d", "Verify the policy lifetime is set to 2 years");
		ZAssert.assertEquals(type, "user", "Verify the policy type is set to 'user'");
		
	}

	@DataProvider(name = "DataProviderRetentions")
	public Object[][] DataProviderRetentions() {
	  return new Object[][] {
			    new Object[] { DialogEditFolder.RetentionRangeUnits.Days, "2d" },
			    new Object[] { DialogEditFolder.RetentionRangeUnits.Weeks, "14d" },
			    new Object[] { DialogEditFolder.RetentionRangeUnits.Months, "62d" },
			    new Object[] { DialogEditFolder.RetentionRangeUnits.Years, "732d" },
	  };
	}
	

	@Test(
			description = "Create day, week, month, year disposals", 
			groups = { "functional" },
			dataProvider = "DataProviderRetentions"
				)
	public void CreateDisposal_02(DialogEditFolder.RetentionRangeUnits units, String expected) throws HarnessException {

		//-- Data
		
		// Create the subfolder
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" +  FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox).getId() + "'/>"
				+	"</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		
		//-- GUI
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Rename the folder using context menu
		DialogEditFolder dialog = (DialogEditFolder) app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folder);

		// Set to 2 years
		dialog.zDisposalEnable();
		dialog.zDisposalSetRange(
				DialogEditFolder.RetentionRangeType.Custom, 
				units, 
				2);

		// Save
		dialog.zClickButton(Button.B_OK);
		
		
		//-- Verification
		
		// Verify the retention policy on the folder
		app.zGetActiveAccount().soapSend(
				"<GetFolderRequest xmlns='urn:zimbraMail'>"
			+		"<folder l='" + folder.getId() + "'/>"
			+	"</GetFolderRequest>");
		String lifetime = app.zGetActiveAccount().soapSelectValue("//mail:purge//mail:policy", "lifetime");
		ZAssert.assertEquals(lifetime, expected, "Verify the policy lifetime is set correctly");
		

	}

}
