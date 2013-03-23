/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.folders;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.FeatureBriefcaseTest;

public class DragAndDropFolder extends FeatureBriefcaseTest {

	public DragAndDropFolder() {
		logger.info("New " + DragAndDropFolder.class.getCanonicalName());

		// All tests start at the Briefcase page
		super.startingPage = app.zPageBriefcase;		
	}

	@Test(description = "Drag one briefcase sub-folder and Drop into other", groups = { "functional" })
	public void DragAndDropFolder_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		ZAssert.assertNotNull(briefcaseRootFolder,
				"Verify the Briefcase root folder is available");

		// Create two briefcase sub-folders:One folder to Drag & Another folder
		// to drop into
		String briefcaseSubFolderName1 = "folder1"
				+ ZimbraSeleniumProperties.getUniqueString();
		String briefcaseSubFolderName2 = "folder2"
				+ ZimbraSeleniumProperties.getUniqueString();

		account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + briefcaseSubFolderName1 + "' l='"
				+ briefcaseRootFolder.getId() + "'/>"
				+ "</CreateFolderRequest>");

		FolderItem briefcaseSubFolder1 = FolderItem.importFromSOAP(account,
				briefcaseSubFolderName1);
		ZAssert.assertNotNull(briefcaseSubFolder1,
				"Verify the first subfolder is available");

		account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + briefcaseSubFolderName2 + "' l='"
				+ briefcaseRootFolder.getId() + "'/>"
				+ "</CreateFolderRequest>");

		FolderItem briefcaseSubFolder2 = FolderItem.importFromSOAP(account,
				briefcaseSubFolderName2);
		ZAssert.assertNotNull(briefcaseSubFolder2,
				"Verify the second subfolder is available");

		// refresh the Briefcase tree folder list
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseRootFolder,
				false);

		// Perform DND action
		app.zPageBriefcase.zDragAndDrop("css=td#zti__main_Briefcase__"
				+ briefcaseSubFolder1.getId() + "_textCell:contains("
				+ briefcaseSubFolder1.getName() + ")",
				"css=td#zti__main_Briefcase__" + briefcaseSubFolder2.getId()
						+ "_textCell:contains(" + briefcaseSubFolder2.getName()
						+ ")");

		// Verify the folder is now in the other sub-folder
		briefcaseSubFolder1 = FolderItem.importFromSOAP(account,
				briefcaseSubFolderName1);
		ZAssert.assertNotNull(briefcaseSubFolder1,
				"Verify the subfolder is still available");
		ZAssert.assertEquals(briefcaseSubFolder2.getId(), briefcaseSubFolder1
				.getParentId(),
				"Verify the subfolder's parent is now the other subfolder");
	}
}
