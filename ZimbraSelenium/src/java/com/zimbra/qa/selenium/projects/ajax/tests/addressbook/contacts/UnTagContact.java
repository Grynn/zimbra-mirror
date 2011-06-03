package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class UnTagContact extends AjaxCommonTest  {
	public UnTagContact() {
		logger.info("New "+ UnTagContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	private ContactItem CreateContactWithTag(String tagName) throws HarnessException{
		
		// Create a tag via soap
	    app.zGetActiveAccount().soapSend(
			"<CreateTagRequest xmlns='urn:zimbraMail'>" +
            	"<tag name='"+ tagName +"' color='1' />" +
            "</CreateTagRequest>");
	    String tagid = app.zGetActiveAccount().soapSelectValue("//mail:CreateTagResponse/mail:tag", "id");

	    String tagParam = " t='" + tagid + "'";;
	    String firstName = "first" + ZimbraSeleniumProperties.getUniqueString();		
	    String lastName = "last" + ZimbraSeleniumProperties.getUniqueString();
        String email = "email" +  ZimbraSeleniumProperties.getUniqueString() + "@zimbra.com";
	
        //default value for file as is last, first
	    String fileAs = lastName + ", " + firstName;

        app.zGetActiveAccount().soapSend(
            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
            "<cn " + tagParam + " fileAsStr='" + fileAs + "' >" +
            "<a n='firstName'>" + firstName +"</a>" +
            "<a n='lastName'>" + lastName +"</a>" +
            "<a n='email'>" + email + "</a>" +               
            "</cn>" +            
            "</CreateContactRequest>");

			        
        ContactItem contactItem = ContactItem.importFromSOAP(app.zGetActiveAccount(), "FIELD[lastname]:" + lastName + "");
    
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
           
        // Select the item
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs);

        return contactItem;
	}

	private void VerifyTagRemove(String tagName, ContactItem contactItem) throws HarnessException{
	    
		//verify toasted message Tag xxxxxx removed from 1 contact
        String expectedMsg = "Tag \"" + tagName + "\" removed from 1 contact";		
	    ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
	        expectedMsg , "Verify toast message '" + expectedMsg + "'");

	
	    app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail'>" +
					"<cn id='"+ contactItem.getId() +"'/>" +
				"</GetContactsRequest>");
	    String contactTag = app.zGetActiveAccount().soapSelectValue("//mail:GetContactsResponse//mail:cn", "t");

	    ZAssert.assertNull(contactTag, "Verify that the tag is removed from the contact");
	}

	
	@Test(	description = "Untag a contact by click Toolbar Tag, then select Remove Tag",
			groups = { "smoke" })
	public void ClickToolbarTagRemoveTag() throws HarnessException {
	    String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
		
        ContactItem contactItem = CreateContactWithTag(tagName);

    	// Untag it
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG);

        VerifyTagRemove(tagName, contactItem);	 
   	}

	   
	@Test(	description = "Untag a contact by click Tag->Remove Tag on context menu",
				groups = { "smoke" })
	public void ClickContextMenuTagRemoveTag() throws HarnessException {
		    String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
			
	        ContactItem contactItem = CreateContactWithTag(tagName);

	    	// Untag it
	        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_REMOVETAG , contactItem.fileAs);
	    	
	        VerifyTagRemove(tagName, contactItem);	 
	}
	
}

