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
package com.zimbra.qa.selenium.projects.ajax.tests.tasks.tags;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;

public class DeleteTag extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public DeleteTag() {
		logger.info("New " + DeleteTag.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageTasks;
	//	super.startingAccountPreferences = null;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefTasksReadingPaneLocation", "bottom");
				put("zimbraPrefShowSelectionCheckbox", "TRUE");
			}
		};

	}

	@Test(description = "Delete a tag - Right click, Delete", groups = { "smoke" })
	public void DeleteTag_01() throws HarnessException {

		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);

		// Create the tag to delete
		String name = "tag" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" + "<tag name='"
				+ name + "' color='1' />" + "</CreateTagRequest>");

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		TagItem tag = TagItem.importFromSOAP(app.zGetActiveAccount(), name);
		ZAssert.assertNotNull(tag, "Verify the tag was created");

		// Click on Task explicitly to refresh the tag list
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		// Delete the tag using context menu

		DialogWarning dialog = (DialogWarning) app.zTreeTasks.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, tag);
		ZAssert.assertNotNull(dialog, "Verify the warning dialog opened");

		// Click "Yes" to confirm
		dialog.zClickButton(Button.B_YES);

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		// To check whether deleted tag is exist
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");

		String tagname = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='" + name + "']","name");
		ZAssert.assertNull(tagname, "Verify the tag is deleted");

	}

}
