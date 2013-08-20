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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.tags;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;


public class DeleteTag extends PrefGroupMailByMessageTest {

	public DeleteTag() {
		logger.info("New "+ DeleteTag.class.getCanonicalName());
		
		
		// All tests start at the addressbook page
		super.startingPage = app.zPageAddressbook;
		super.startingAccountPreferences = null;

	}
	
	@Test(	description = "Delete a tag - Right click, Delete",
			groups = { "smoke" })
	public void DeleteTag_01() throws HarnessException {
		
		

		
		// Create the tag to delete
		TagItem tag = TagItem.CreateUsingSoap(app.zGetActiveAccount());
		ZAssert.assertNotNull(tag, "Verify the tag was created");
		
		
		// Click on Get Mail to refresh the folder list
		app.zPageAddressbook.zRefresh();

		// Delete the tag using context menu
		DialogWarning dialog = (DialogWarning) app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_DELETE, tag);
		ZAssert.assertNotNull(dialog, "Verify the warning dialog opened");
		
		
		// Click "Yes" to confirm
		dialog.zClickButton(Button.B_YES);


		// To check whether deleted tag is exist
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");

		String tagname = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='" + tag.getName() + "']","name");
		ZAssert.assertNull(tagname, "Verify the tag is deleted");


		
	}

	


}
