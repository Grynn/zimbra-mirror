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
package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.tags;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.FeatureBriefcaseTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

public class RenameTag extends FeatureBriefcaseTest {

	public RenameTag() {
		logger.info("New " + RenameTag.class.getCanonicalName());

		// All tests start at the Briefcase page
		super.startingPage = app.zPageBriefcase;	
	}

	@Test(description = "Rename a tag - Right click, Rename", groups = { "functional" })
	public void RenameTag_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create the tag to rename
		String name1 = "tag" + ZimbraSeleniumProperties.getUniqueString();
		String name2 = "tag" + ZimbraSeleniumProperties.getUniqueString();

		account.soapSend("<CreateTagRequest xmlns='urn:zimbraMail'>"
				+ "<tag name='" + name1 + "' color='1' />"
				+ "</CreateTagRequest>");

		// Get the tag
		TagItem tag = TagItem.importFromSOAP(account, name1);
		ZAssert.assertNotNull(tag, "Verify the tag was created");

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Rename the tag using the context menu
		DialogRenameTag dialog = (DialogRenameTag) app.zTreeBriefcase
				.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_RENAMETAG, tag);
		ZAssert.assertNotNull(dialog, "Verify the Rename Tag dialog opened");

		// Set the new name, click OK
		SleepUtil.sleepMedium();
		dialog.zSetNewName(name2);
		dialog.zClickButton(Button.B_OK);

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Verify the tag is no longer found
		account.soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");

		Element[] eTag1 = account.soapSelectNodes("//mail:tag[@name='" + name1
				+ "']");
		ZAssert.assertEquals(eTag1.length, 0,
				"Verify the old tag name no longer exists");

		Element[] eTag2 = account.soapSelectNodes("//mail:tag[@name='" + name2
				+ "']");
		ZAssert.assertEquals(eTag2.length, 1, "Verify the new tag name exists");
	}
}
