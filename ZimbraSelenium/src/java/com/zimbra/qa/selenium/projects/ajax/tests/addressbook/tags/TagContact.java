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


import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.ContactsPrefShowSelectionCheckbox;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;

public class TagContact extends  ContactsPrefShowSelectionCheckbox  {
	public TagContact() {
		logger.info("New "+ TagContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences.put("zimbraPrefShowSelectionCheckbox", "FALSE");		
		
	}
	
	/**
	 * @throws HarnessException
	 */
	@BeforeClass( groups = { "always" } )
	public void TagContactBeforeClass() throws HarnessException {
		logger.info("TagContactBeforeClass: start");
		
		// Rest the ZWC user
		ZimbraAccount.ResetAccountZWC();
		
		logger.info("TagContactBeforeClass: finish");
	}
	
	@Test(	description = "Tag a contact, click pulldown menu Tag->New Tag",
			groups = { "smoke" })
	public void ClickPulldownMenuTagNewTag() throws HarnessException {
		
		
		//-- Data
		
		// Tag Name
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();

		// Create a contact via Soap then select
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());


		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);
		
		//click Tag Contact->New Tag	
		DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_NEWTAG);
    	dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);		
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ contact.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String tn = app.zGetActiveAccount().soapSelectValue("//mail:cn", "tn");
		ZAssert.assertNotNull(tn, "Verify the contact has tags");
		ZAssert.assertStringContains(tn, tagName, "Verify the contact is tagged with the correct tag");
		
   	}
	
	@Test(	description = "Right click then click Tag Contact->New Tag",
			groups = { "smoke" })	
	public void ClickContextMenuTagContactNewTag() throws HarnessException {
		
		
		//-- Data
		
		// Tag Name
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();

		// Create a contact via Soap then select
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());


		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);
		
		//click Tag Contact->New Tag	
        DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_NEWTAG , contact.fileAs);        
    	dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);		
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ contact.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String tn = app.zGetActiveAccount().soapSelectValue("//mail:cn", "tn");
		ZAssert.assertNotNull(tn, "Verify the contact has tags");
		ZAssert.assertStringContains(tn, tagName, "Verify the contact is tagged with the correct tag");
		

	}

	@Test(	description = "Right click then click Tag Contact->a tag name",
			groups = { "functional" })	
	public void ClickContextMenuTagContactExistingTag() throws HarnessException {
		
		
		//-- Data
		
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact via Soap then select
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());


		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);
		
		//click Tag Contact->the tag name
		app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, tagItem, contact.fileAs);        
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ contact.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNotNull(t, "Verify the contact has tags");
		ZAssert.assertStringContains(t, tagItem.getId(), "Verify the contact is tagged with the correct tag");
		
	}

	@Test(	description = "click pulldown menu Tag->A tag name",
			groups = { "smoke" })	
	public void ClickPulldownMenuTagExistingTag() throws HarnessException {
		
		
		//-- Data
		
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact via Soap then select
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());


		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);
		
		// select the tag
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, tagItem);
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ contact.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNotNull(t, "Verify the contact has tags");
		ZAssert.assertStringContains(t, tagItem.getId(), "Verify the contact is tagged with the correct tag");
		
	}

	@Test(	description = "Double tag a contact ",
			groups = { "functional" })	
	public void DoubleTag() throws HarnessException {
		
		
		//-- Data
		
		// Create a tag
		TagItem tag1 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		
		TagItem tag2 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact via Soap then select
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());


		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);
		
		// select the tag
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, tag1);
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, tag2);
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ contact.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNotNull(t, "Verify the contact has tags");
		ZAssert.assertStringContains(t, tag1.getId(), "Verify the contact is tagged with the correct tag");
		ZAssert.assertStringContains(t, tag2.getId(), "Verify the contact is tagged with the correct tag");
		
	}

	
	@Test(	description = "Tag a contact by dnd on an existing tag",
			groups = { "functional" })
	public void DnDOnExistingTag() throws HarnessException {
		
		
		//-- Data
		
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact via Soap then select
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());


		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
	    // Dnd on the new tag
		app.zPageAddressbook.zDragAndDrop(
				"css=[id=zlif__CNS-main__" + contact.getId() + "__fileas]:contains("+ contact.fileAs + ")",
				"css=div[id=main_Contacts-parent-TAG] div[id=ztih__main_Contacts__TAG] td[id^=zti__main_Contacts__][id$=_textCell]:contains("+ tagItem.getName() + ")");
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ contact.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNotNull(t, "Verify the contact has tags");
		ZAssert.assertStringContains(t, tagItem.getId(), "Verify the contact is tagged with the correct tag");
		
   	}



}

