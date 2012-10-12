package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.tags;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;




public class UnTagContactGroup extends AjaxCommonTest  {
	public UnTagContactGroup() {
		logger.info("New "+ UnTagContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}


	@Test(	description = "Untag a contact group by click Tag->Remove tag on toolbar ",
			groups = { "smoke" })
	public void UnTagContactGroup_01() throws HarnessException {
		
		
		//-- Data
		
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact group via Soap then select
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Tag the contact
		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ group.getId() +"' op='tag' tag='"+ tagItem.getId() +"'/>" +
				"</ContactActionRequest>");

		

		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

    	// Untag it by click Tag->Remove Tag on toolbar 
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG);
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ group.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNull(t, "Verify the contact has tags");

	}

	@Test(	description = "Untag a contact group by click Tag->Remove tag on Context Menu",
			groups = { "smoke" })
	public void UnTagContactGroup_02() throws HarnessException {
		
		
		//-- Data
		
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact group via Soap then select
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Tag the contact
		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ group.getId() +"' op='tag' tag='"+ tagItem.getId() +"'/>" +
				"</ContactActionRequest>");

		

		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
    	// Untag it by click Tag->Remove Tag on context menu
		app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_REMOVETAG , group.getName());
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ group.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNull(t, "Verify the contact has tags");

   	}

	@Test(	description = "Untag a double-tagged-contact group by click Tag->Remove tag->tag name on toolbar ",
			groups = { "functional" })
	public void UnTagContactGroup_03() throws HarnessException {			
		
		
		//-- Data
		
		// Create a tag
		TagItem tag1 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		
		TagItem tag2 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact group via Soap then select
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Tag the contact
		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ group.getId() +"' op='tag' tag='"+ tag1.getId() +"'/>" +
				"</ContactActionRequest>");

		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ group.getId() +"' op='tag' tag='"+ tag2.getId() +"'/>" +
				"</ContactActionRequest>");

		

		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

    	// Untag one tag by click Tag->Remove Tag->A Tag name on toolbar 
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG, tag1);
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ group.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNotNull(t, "Verify the contact has tags");
		ZAssert.assertStringDoesNotContain(t, tag1.getId(), "Verify the contact has one tag");
		ZAssert.assertStringContains(t, tag2.getId(), "Verify the contact has one tag");


   	}



	@Test(	description = "Untag a double-tagged-contact group by right click on group, click Tag ->Remove tag->tag name on context menu ",
			groups = { "functional" })
	public void UnTagContactGroup_04() throws HarnessException {			
		
		
		//-- Data
		
		// Create a tag
		TagItem tag1 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		
		TagItem tag2 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact group via Soap then select
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Tag the contact
		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ group.getId() +"' op='tag' tag='"+ tag1.getId() +"'/>" +
				"</ContactActionRequest>");

		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ group.getId() +"' op='tag' tag='"+ tag2.getId() +"'/>" +
				"</ContactActionRequest>");

		

		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
    	// Untag one tag by click Tag->Remove Tag->A Tag name on context menu
		app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_REMOVETAG , tag1.getName(), group.getName());
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ group.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNotNull(t, "Verify the contact has tags");
		ZAssert.assertStringDoesNotContain(t, tag1.getId(), "Verify the contact has one tag");
		ZAssert.assertStringContains(t, tag2.getId(), "Verify the contact has one tag");


   	}
	
	@Test(	description = "remove all tags from a double-tagged-contact group by click Tag->Remove tag->All Tags on toolbar ",
			groups = { "smoke" })
	public void UnTagContactGroup_05() throws HarnessException {			
		
		
		//-- Data
		
		// Create a tag
		TagItem tag1 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		
		TagItem tag2 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact group via Soap then select
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Tag the contact
		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ group.getId() +"' op='tag' tag='"+ tag1.getId() +"'/>" +
				"</ContactActionRequest>");

		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ group.getId() +"' op='tag' tag='"+ tag2.getId() +"'/>" +
				"</ContactActionRequest>");

		

		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

    	// Untag one tag by click Tag->Remove Tag->All Tags on toolbar 
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG, TagItem.Remove_All_Tags);
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ group.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNull(t, "Verify the contact has 0 tags");


   	}
	
	@Test(	description = "Remove all tags from a double-tagged-contact group by right click on group, click Tag ->Remove tag->All Tags on context menu ",
			groups = { "smoke" })
	public void UnTagContactGroup_06() throws HarnessException {			
		
		
		//-- Data
		
		// Create a tag
		TagItem tag1 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		
		TagItem tag2 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact group via Soap then select
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Tag the contact
		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ group.getId() +"' op='tag' tag='"+ tag1.getId() +"'/>" +
				"</ContactActionRequest>");

		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ group.getId() +"' op='tag' tag='"+ tag2.getId() +"'/>" +
				"</ContactActionRequest>");

		

		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

    	// Untag one tag by click Tag->Remove Tag->All Tags on context menu
		app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_REMOVETAG , "All Tags", group.getName());
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ group.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNull(t, "Verify the contact has tags");


   	}

	
	@Test(	description = "Remove all tags from a double-tagged-contact group by click short cut u ",
			groups = { "functional" })
	public void UnTagContactGroup_07() throws HarnessException {			
		
		
		//-- Data
		
		// Create a tag
		TagItem tag1 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		
		TagItem tag2 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact group via Soap then select
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Tag the contact
		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ group.getId() +"' op='tag' tag='"+ tag1.getId() +"'/>" +
				"</ContactActionRequest>");

		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ group.getId() +"' op='tag' tag='"+ tag2.getId() +"'/>" +
				"</ContactActionRequest>");

		

		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

    	// Untag one tag by click Tag->Remove Tag->All Tags on context menu
	    app.zPageAddressbook.zKeyboardShortcut(Shortcut.S_MAIL_REMOVETAG);
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ group.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNull(t, "Verify the contact has tags");


	}
	
	
	@Test(	description = "Remove all tags from a single-tagged-contact group by click short cut u ",
			groups = { "functional" })
	public void UnTagContactGroup_08() throws HarnessException {			
		
		
		//-- Data
		
		// Create a tag
		TagItem tag1 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact group via Soap then select
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Tag the contact
		app.zGetActiveAccount().soapSend(
				"<ContactActionRequest xmlns='urn:zimbraMail'>" +
					"<action id='"+ group.getId() +"' op='tag' tag='"+ tag1.getId() +"'/>" +
				"</ContactActionRequest>");


		

		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

    	// Untag one tag by click Tag->Remove Tag->All Tags on context menu
	    app.zPageAddressbook.zKeyboardShortcut(Shortcut.S_MAIL_REMOVETAG);
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ group.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNull(t, "Verify the contact has tags");


	}
	
	
}

