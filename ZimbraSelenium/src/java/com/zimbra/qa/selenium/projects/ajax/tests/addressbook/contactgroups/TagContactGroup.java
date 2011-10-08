package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

public class TagContactGroup extends AjaxCommonTest  {
	public TagContactGroup() {
		logger.info("New "+ TagContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	private void Verify(ContactGroupItem group, String tagName) throws HarnessException {
	
					
		// Make sure the tag was created on the server (get the tag ID)
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tagName +"']", "id");

		// Make sure the tag was applied to the contact
		app.zGetActiveAccount().soapSend(
					"<GetContactsRequest xmlns='urn:zimbraMail'>" +
						"<cn id='"+ group.getId() +"'/>" +
					"</GetContactsRequest>");
		
		String contactTags = app.zGetActiveAccount().soapSelectValue("//mail:GetContactsResponse//mail:cn", "t");
		 
		//if multi-tagged
		if (contactTags.contains(",")) {
			ZAssert.assertStringContains(contactTags, tagID, "Verify the tag appears on the contact id=" +  group.getId());			
		}
		else {
			ZAssert.assertEquals(contactTags, tagID, "Verify the tag appears on the contact id=" +  group.getId());
		}
		//verify toasted message '1 contact group tagged ...'
        String expectedMsg = "1 contact group tagged \"" + tagName + "\"";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
		        expectedMsg , "Verify toast message '" + expectedMsg + "'");

	}
	
	@Test(	description = "Tag a contact group, click pulldown menu Tag->New Tag",
			groups = { "smoke" })
	public void  ClickPulldownMenuTagNewTag() throws HarnessException {
	
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
	           
		// Click Tag->New Tag on toolbar
		DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_NEWTAG);
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();			
		dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);		
		
		Verify(group, tagName);	  
   	}
	
		
	@Test(	description = "Right click then click Tag Contact->New Tag",
			groups = { "functional" })
	public void ClickContextMenuTagGroupNewTag() throws HarnessException {
	
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
	           
		// Click Tag Group on context menu
        DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_NEWTAG , group.fileAs);        
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();			
		dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);		
		
		Verify(group, tagName);	  
   	}
	
	@Test(	description = "Right click then click Tag Contact Group->a tag name",
			groups = { "functional" })	
	public void ClickContextMenuTagContactExistingTag() throws HarnessException {
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app);		

		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
			
		//click Tag Contact->the tag name
		app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, tagItem, group.fileAs);        
    	
		Verify(group, tagItem.getName()); 
	}

	@Test(	description = "click pulldown menu Tag->A tag name",
			groups = { "smoke" })	
	public void ClickPulldownMenuTagExistingTag() throws HarnessException {
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);

		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app);
		
		
		// select the tag
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, tagItem);

		Verify(group, tagItem.getName()); 
	}
	
	@Test(	description = "Double tag a group ",
			groups = { "functional" })	
	public void DoubleTag() throws HarnessException {
		// Create a new tag
		TagItem tagItem = TagItem.CreateUsingSoap(app);

		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);

		// select the tag
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, tagItem);

		Verify(group, tagItem.getName()); 

		// create a new tag name 		
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
			
		//click Tag Contact->New Tag	
        DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_NEWTAG , group.fileAs);        
    	dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);		

		Verify(group, tagName); 
		
	}
	@Test(	description = "Tag a contact group by dnd on an existing tag",
			groups = { "functional" })
	public void DnDOnExistingTag() throws HarnessException {
	
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
	           
		// Create a new tag via soap
	    TagItem tagItem = TagItem.CreateUsingSoap(app);
		
	    // Dnd on the new tag
		app.zPageAddressbook.zDragAndDrop(
				"css=td#zlif__CNS__" + group.getId() + "__fileas:contains("+ group.fileAs + ")",
				"css=div#zov__main_Contacts td#zti__main_Contacts__" + tagItem.getId() + "_textCell:contains("+ tagItem.getName() + ")");
			
		// Make sure the tag was applied to the contact
		app.zGetActiveAccount().soapSend(
					"<GetContactsRequest xmlns='urn:zimbraMail'>" +
						"<cn id='"+ group.getId() +"'/>" +
					"</GetContactsRequest>");
		
		String contactTags = app.zGetActiveAccount().soapSelectValue("//mail:GetContactsResponse//mail:cn", "t");
		 
		ZAssert.assertEquals(contactTags, tagItem.getId(), "Verify the tag appears on the contact id=" +  group.getId());
			
		//verify toasted message '1 contact group tagged ...'
        String expectedMsg = "1 contact group tagged \"" + tagItem.getName() + "\"";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
		        expectedMsg , "Verify toast message '" + expectedMsg + "'");

			  
   	}
}

