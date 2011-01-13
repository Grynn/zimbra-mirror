package projects.ajax.tests.addressbook.contacts;


import java.util.List;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import framework.items.*;
import framework.ui.*;
import framework.util.*;
import projects.ajax.ui.*;
import projects.ajax.ui.addressbook.*;
import projects.ajax.ui.mail.DialogTag;
import framework.items.ContactItem.GenerateItemType;
import framework.items.FolderItem.SystemFolder;

public class UnTagContact extends AjaxCommonTest  {
	public UnTagContact() {
		logger.info("New "+ UnTagContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccount = null;		
		
	}
	
	@Test(	description = "Untag a contact",
			groups = { "smoke" })
	public void UnTagContact_01() throws HarnessException {


	    String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
		
			// Create a tag via soap
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" +
                	"<tag name='"+ tagName +"' color='1' />" +
                "</CreateTagRequest>");
		String tagid = app.zGetActiveAccount().soapSelectValue("//mail:CreateTagResponse/mail:tag", "id");

		 // Create a contact with tag
		ContactItem contactItem = ContactItem.generateContactItem(GenerateItemType.Basic);
		contactItem.setId(tagid);
		
        app.zGetActiveAccount().soapSend(
                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
                "<cn t='" + tagid + "' fileAsStr='" + contactItem.lastName + "," + contactItem.firstName + "' >" +
                "<a n='firstName'>" + contactItem.firstName +"</a>" +
                "<a n='lastName'>" + contactItem.lastName +"</a>" +
                "<a n='email'>" + contactItem.email + "</a>" +               
                "</cn>" +            
                "</CreateContactRequest>");
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
               
        // Select the item
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs);


    	// Untag it
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG);
		
		app.zGetActiveAccount().soapSend(
					"<GetContactsRequest xmlns='urn:zimbraMail'>" +
						"<m id='"+ contactItem.getId() +"'/>" +
					"</GetContactsRequest>");
		String contactTag = app.zGetActiveAccount().soapSelectValue("//mail:GetContactsResponse//mail:cn", "t");
		
		ZAssert.assertNull(contactTag, "Verify that the tag is removed from the contact");
	      
		//verify toasted message 'contact created'
        ZAssert.assertStringContains(app.zPageAddressbook.sGetText("xpath=//div[@id='z_toast_text']"), "All tags removed from 1 contact", "Verify toast message 'All tags removed from 1 contact'");
	 
   	}
	
	
}

