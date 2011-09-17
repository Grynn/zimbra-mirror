package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

public class TagContact extends AjaxCommonTest  {
	public TagContact() {
		logger.info("New "+ TagContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	// verify contact tagged with tag and toasted message
	private void Verify(ContactItem contactItem, String tagName)throws HarnessException {
		// Make sure the tag was created on the server (get the tag ID)
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tagName +"']", "id");

		// Make sure the tag was applied to the contact
		app.zGetActiveAccount().soapSend(
					"<GetContactsRequest xmlns='urn:zimbraMail'>" +
						"<cn id='"+ contactItem.getId() +"'/>" +
					"</GetContactsRequest>");
		
		String contactTags = app.zGetActiveAccount().soapSelectValue("//mail:GetContactsResponse//mail:cn", "t");
		 
		ZAssert.assertEquals(contactTags, tagID, "Verify the tag appears on the contact id=" +  contactItem.getId());
		
		//verify toasted message '1 contact tagged ...'
        Toaster toast = app.zPageMain.zGetToaster();
        String toastMsg = toast.zGetToastMessage();
        ZAssert.assertStringContains(toastMsg, "1 contact tagged \"" + tagName + "\"", "Verify toast message '" + "1 contact tagged \"" + tagName + "\"'" );
 
 		
	}
	@Test(	description = "Tag a contact, click pulldown menu Tag->New Tag",
			groups = { "smoke" })
	public void ClickPulldownMenuTagNewTag() throws HarnessException {

		  // Create a contact via Soap then select
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);

	    String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
		
		// Click new tag
		DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_NEWTAG);
		dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);		
				
	    Verify(contactItem, tagName);
   	}
	
	@Test(	description = "Right click then click Tag Contact->New Tag",
			groups = { "smoke" })	
	public void ClickContextMenuTagContactNewTag() throws HarnessException {
		  // Create a contact via Soap then select
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
			
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
			
		//click Tag Contact->New Tag	
        DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_NEWTAG , contactItem.fileAs);        
    	dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);		

		Verify(contactItem, tagName); 
	}

	
	@Test(	description = "Tag a contact by dnd on an existing tag",
			groups = { "functional" })
	public void DnDOnExistingTag() throws HarnessException {
	
		  // Create a contact via Soap then select
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
	          
		// Create a new tag via soap
	    TagItem tagItem = TagItem.CreateUsingSoap(app);
		
		// Refresh to display the new tag
		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);
		
	    // Dnd on the new tag
		app.zPageAddressbook.zDragAndDrop(
				"css=td#zlif__CNS__" + contactItem.getId() + "__fileas:contains("+ contactItem.fileAs + ")",
				"css=td#zti__main_Contacts__" + tagItem.getId() + "_textCell:contains("+ tagItem.getName() + ")");
			
		Verify(contactItem, tagItem.getName());
			  
   	}
  	
}

