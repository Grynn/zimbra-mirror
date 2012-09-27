package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.tags;

import java.util.HashMap;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

public class TagContactGroup extends AjaxCommonTest  {
	
	
	public TagContactGroup() {
		logger.info("New "+ TagContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = new HashMap<String , String>() {
		private static final long serialVersionUID = 1L;

		{
		    put("zimbraPrefShowSelectionCheckbox", "TRUE");		         
		}};			
					
		
	}
	
	@Test(	description = "Tag a contact group, click pulldown menu Tag->New Tag",
			groups = { "smoke", "matt" })
	public void  ClickPulldownMenuTagNewTag() throws HarnessException {
	
		//-- Data
		
		// Create a tag
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();

		// Create a contact group via Soap then select
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount(), GenerateItemType.Basic);


		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.fileAs);
		
		//click Tag Contact->New Tag	
        DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_NEWTAG , group.fileAs);        
    	dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);		
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ group.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String tn = app.zGetActiveAccount().soapSelectValue("//mail:cn", "tn");
		ZAssert.assertNotNull(tn, "Verify the contact has tags");
		ZAssert.assertStringContains(tn, tagName, "Verify the contact is tagged with the correct tag");
		


	           
   	}
	
		
	@Test(	description = "Right click then click Tag Contact->New Tag",
			groups = { "functional" })
	public void ClickContextMenuTagGroupNewTag() throws HarnessException {
		
		//-- Data
		
		// Create a tag
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();

		// Create a contact group via Soap then select
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount(), GenerateItemType.Basic);


		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Click Tag Group on context menu
        DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_NEWTAG , group.fileAs);        
		dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);		
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ group.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String tn = app.zGetActiveAccount().soapSelectValue("//mail:cn", "tn");
		ZAssert.assertNotNull(tn, "Verify the contact has tags");
		ZAssert.assertStringContains(tn, tagName, "Verify the contact is tagged with the correct tag");
		

   	}
	
	@Test(	description = "Right click then click Tag Contact Group->a tag name",
			groups = { "functional" })	
	public void ClickContextMenuTagContactExistingTag() throws HarnessException {
		
		//-- Data
		
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact group via Soap then select
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount(), GenerateItemType.Basic);


		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Right Click -> Tag -> Existing Tag
		app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, tagItem, group.fileAs);        
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ group.getId() +"'/>" +
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

		// Create a contact group via Soap then select
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount(), GenerateItemType.Basic);


		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.fileAs);

		// Tag -> Existing Tag
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, tagItem);
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ group.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNotNull(t, "Verify the contact has tags");
		ZAssert.assertStringContains(t, tagItem.getId(), "Verify the contact is tagged with the correct tag");
		

	}
	
	@Test(	description = "Double tag a group ",
			groups = { "functional" })	
	public void DoubleTag() throws HarnessException {
		
		//-- Data
		
		// Create a tag
		TagItem tag1 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		
		TagItem tag2 = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact group via Soap then select
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount(), GenerateItemType.Basic);


		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.fileAs);

		// Tag -> Existing Tag
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, tag1);
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, tag2);
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ group.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNotNull(t, "Verify the contact has tags");
		ZAssert.assertStringContains(t, tag1.getId(), "Verify the contact is tagged with the correct tag");
		ZAssert.assertStringContains(t, tag2.getId(), "Verify the contact is tagged with the correct tag");
		
	}
	@Test(	description = "Tag a contact group by dnd on an existing tag",
			groups = { "functional" })
	public void DnDOnExistingTag() throws HarnessException {
		
		//-- Data
		
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app.zGetActiveAccount());		

		// Create a contact group via Soap then select
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount(), GenerateItemType.Basic);


		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
	    // Dnd on the new tag
		app.zPageAddressbook.zDragAndDrop(
				"css=td#zlif__CNS-main__" + group.getId() + "__fileas:contains("+ group.fileAs + ")",
				"css=div[id=main_Contacts-parent-TAG] div[id=ztih__main_Contacts__TAG] td[id^=zti__main_Contacts__][id$=_textCell]:contains("+ tagItem.getName() + ")");
    	
		
		
		//-- Verification
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail' >" +
						"<cn id='"+ group.getId() +"'/>" +
				"</GetContactsRequest>");
		
		String t = app.zGetActiveAccount().soapSelectValue("//mail:cn", "t");
		ZAssert.assertNotNull(t, "Verify the contact has tags");
		ZAssert.assertStringContains(t, tagItem.getId(), "Verify the contact is tagged with the correct tag");
		


			  
   	}
}

