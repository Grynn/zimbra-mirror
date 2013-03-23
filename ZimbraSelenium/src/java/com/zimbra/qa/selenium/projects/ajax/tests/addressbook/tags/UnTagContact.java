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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.tags;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class UnTagContact extends AjaxCommonTest  {
	public UnTagContact() {
		logger.info("New "+ UnTagContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	@Test(	description = "Untag a contact by click Toolbar Tag, then select Remove Tag",
			groups = { "smoke" })
	public void ClickToolbarTagRemoveTag() throws HarnessException {
		
		
		
		//-- Data
		
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact via Soap then select
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());

		// Tag the contact
		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ contact.getId() +"' op='tag' tag='"+ tagItem.getId() +"'/>" +
				"</ContactActionRequest>");

		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);

    	// Untag it
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG);
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ contact.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNull(t, "Verify the contact has no tags");

   	}

	   
	@Test(	description = "Untag a contact by click Tag->Remove Tag on context menu",
				groups = { "smoke" })
	public void ClickContextMenuTagRemoveTag() throws HarnessException {
		
		
		
		//-- Data
		
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact via Soap then select
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());

		// Tag the contact
		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ contact.getId() +"' op='tag' tag='"+ tagItem.getId() +"'/>" +
				"</ContactActionRequest>");

		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
    	// Untag it
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_REMOVETAG , contact.fileAs);
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ contact.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNull(t, "Verify the contact has no tags");

	}
	
}

