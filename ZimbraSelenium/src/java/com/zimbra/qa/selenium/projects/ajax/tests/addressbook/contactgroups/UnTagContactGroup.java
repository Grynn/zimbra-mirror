package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;


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

	//verify a contact group is untaged via UI & soap
	private void VerifyContactGroupUntag(ContactGroupItem group, String tagName, String ... isRemoveAll) throws HarnessException {
		String expectedMsg = "Tag \"" + tagName + "\" removed from 1 contact group";
		
		if (isRemoveAll.length >0) {
			expectedMsg = "All tags removed from 1 contact group";
		}
		ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
		        expectedMsg , "Verify toast message '" + expectedMsg + "'");
 
	    GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

	    app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail'>" +
					"<cn id='"+ group.getId() +"'/>" +
				"</GetContactsRequest>");
	    String contactTag = app.zGetActiveAccount().soapSelectValue("//mail:GetContactsResponse//mail:cn", "t");
	   
	    if (contactTag != null) {
	    	ZAssert.assertStringDoesNotContain(contactTag, tagName, "Verify that the tag is removed from the contact");
	    }
    }

	private void VerifyGroupIsTagged(ContactGroupItem group, String tagName) throws HarnessException {
		
		
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
	
	private ContactGroupItem DoubleTagAGroup(TagItem tagItem_2) throws HarnessException {
        TagItem tagItem_1 = TagItem.CreateUsingSoap(app);
				
		// Create a contact group with tag via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK, tagItem_1.getId());
	  
				
		// select the tag
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, tagItem_2);

		VerifyGroupIsTagged(group, tagItem_2.getName());
		
		return group;
	}
	
	@Test(	description = "Untag a contact group by click Tag->Remove tag on toolbar ",
			groups = { "smoke" })
	public void RemoveTagFromSingledTaggedGroupUsingToolbar() throws HarnessException {
		// Create a tag					
		TagItem tagItem = TagItem.CreateUsingSoap(app);
				
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK, tagItem.getId());
	  
    	// Untag it by click Tag->Remove Tag on toolbar 
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG);
    
        // Verify contact group untagged
		VerifyContactGroupUntag(group, tagItem.getName());
	 
   	}

	@Test(	description = "Untag a contact group by click Tag->Remove tag on Context Menu",
			groups = { "smoke" })
	public void RemoveTagFromSingledTaggedGroupUsingContextMenu() throws HarnessException {
		// Create a tag			
		TagItem tagItem = TagItem.CreateUsingSoap(app);
		
	
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK, tagItem.getId());
	  
    	// Untag it by click Tag->Remove Tag on context menu
		app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_REMOVETAG , group.fileAs);

	     
        // Verify contact group untagged
		VerifyContactGroupUntag(group, tagItem.getName());
	 
   	}

	@Test(	description = "Untag a double-tagged-contact group by click Tag->Remove tag->tag name on toolbar ",
			groups = { "functional" })
	public void RemoveOneTagFromDoubleTaggedGroupUsingToolbar() throws HarnessException {			
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app);
	
		// Create a contact group with 2 tags
		ContactGroupItem group = DoubleTagAGroup(tagItem);
	  
    	// Untag one tag by click Tag->Remove Tag->A Tag name on toolbar 
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG, tagItem);
    
        // Verify contact group untagged
		VerifyContactGroupUntag(group, tagItem.getName());
	 
   	}



	@Test(	description = "Untag a double-tagged-contact group by right click on group, click Tag ->Remove tag->tag name on context menu ",
			groups = { "functional" })
	public void RemoveOneTagFromDoubleTaggedGroupUsingContextmenu() throws HarnessException {			
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app);
	
		// Create a contact group with 2 tags
		ContactGroupItem group = DoubleTagAGroup(tagItem);
	  
    	// Untag one tag by click Tag->Remove Tag->A Tag name on context menu
		app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_REMOVETAG , tagItem.getName(), group.fileAs);

        // Verify contact group untagged
		VerifyContactGroupUntag(group, tagItem.getName());
	 
   	}
	
	@Test(	description = "remove all tags from a double-tagged-contact group by click Tag->Remove tag->All Tags on toolbar ",
			groups = { "smoke" })
	public void RemoveAllTagsFromDoubleTaggedGroupUsingToolbar() throws HarnessException {			
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app);
	
		// Create a contact group with 2 tags
		ContactGroupItem group = DoubleTagAGroup(tagItem);
	  
    	// Untag one tag by click Tag->Remove Tag->All Tags on toolbar 
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG, TagItem.Remove_All_Tags);
    
        // Verify contact group untagged
		VerifyContactGroupUntag(group, tagItem.getName(),"all tags");
	 
   	}
	
	@Test(	description = "Remove all tags from a double-tagged-contact group by right click on group, click Tag ->Remove tag->All Tags on context menu ",
			groups = { "smoke" })
	public void RemoveAllTagsFromDoubleTaggedGroupUsingContextmenu() throws HarnessException {			
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app);
	
		// Create a contact group with 2 tags
		ContactGroupItem group = DoubleTagAGroup(tagItem);
	  
    	// Untag one tag by click Tag->Remove Tag->All Tags on context menu
		app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_REMOVETAG , "All Tags", group.fileAs);

        // Verify contact group untagged
		VerifyContactGroupUntag(group, tagItem.getName(),"all tags");
	 
   	}

	@Test(	description = "Remove all tags from a double-tagged-contact group by click short cut u ",
			groups = { "functional" })
	public void RemoveAllTagsFromDoubleTaggedGroupUsingShortcutu() throws HarnessException {			
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app);
	
		// Create a contact group with 2 tags
		ContactGroupItem group = DoubleTagAGroup(tagItem);
	
		//Click shortcut u
	    app.zPageAddressbook.zKeyboardShortcut(Shortcut.S_MAIL_REMOVETAG);
		
	    // Verify contact group untagged
		VerifyContactGroupUntag(group, tagItem.getName(),"all tags");	 
	}
	
	
	@Test(	description = "Remove all tags from a single-tagged-contact group by click short cut u ",
			groups = { "functional" })
	public void RemoveTagFromSingleTaggedGroupUsingShortcutu() throws HarnessException {			
		// Create a tag
		TagItem tagItem = TagItem.CreateUsingSoap(app);
	
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK, tagItem.getId());
		
		//Click shortcut u
	    app.zPageAddressbook.zKeyboardShortcut(Shortcut.S_MAIL_REMOVETAG);
		
	    // Verify contact group untagged
		VerifyContactGroupUntag(group, tagItem.getName(), "all tags");
	 
	}
	
	
}

