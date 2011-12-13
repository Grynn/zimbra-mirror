package com.zimbra.qa.selenium.projects.desktop.tests.addressbook.contacts;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.*;

public class TagContact extends AjaxCommonTest  {
	public TagContact() {
		logger.info("New "+ TagContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	@Test(	description = "Tag a contact",
			groups = { "smoke" })
	public void TagContact_01() throws HarnessException {

		String firstName = "first" + ZimbraSeleniumProperties.getUniqueString();		
		String lastName = "last" + ZimbraSeleniumProperties.getUniqueString();
	    String email = "email" +  ZimbraSeleniumProperties.getUniqueString() + "@zimbra.com";
		//default value for file as is last, first
		String fileAs = lastName + ", " + firstName;
	
        app.zGetActiveAccount().soapSend(
                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
                "<cn fileAsStr='" + fileAs + "' >" +
                "<a n='firstName'>" + firstName +"</a>" +
                "<a n='lastName'>" + lastName +"</a>" +
                "<a n='email'>" + email + "</a>" +               
                "</cn>" +            
                "</CreateContactRequest>");

        
        ContactItem contactItem = ContactItem.importFromSOAP(app.zGetActiveAccount(), "FIELD[lastname]:" + lastName + "");
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
                 
        // Select the item
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs); // contactItem.fileAs);

	    String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
		
		// Click new tag
		DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_NEWTAG);
		dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);		
				
	
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
	
	@Test(   description = "Tag a local contact",
	      groups = { "smoke" })
	public void TagLocalContact() throws HarnessException {

	   String firstName = "first" + ZimbraSeleniumProperties.getUniqueString();      
	   String lastName = "last" + ZimbraSeleniumProperties.getUniqueString();
	   String email = "email" +  ZimbraSeleniumProperties.getUniqueString() + "@zimbra.com";
	   //default value for file as is last, first
	   String fileAs = lastName + ", " + firstName;

	   app.zGetActiveAccount().soapSend(
	         "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	         "<cn fileAsStr='" + fileAs + "' >" +
	         "<a n='firstName'>" + firstName +"</a>" +
	         "<a n='lastName'>" + lastName +"</a>" +
	         "<a n='email'>" + email + "</a>" +               
	         "</cn>" +            
	         "</CreateContactRequest>",
	         SOAP_DESTINATION_HOST_TYPE.CLIENT,
	         ZimbraAccount.clientAccountName);

	        
	   ContactItem contactItem = ContactItem.importFromSOAP(
	         app.zGetActiveAccount(),
	         "FIELD[lastname]:" + lastName + "",
	         SOAP_DESTINATION_HOST_TYPE.CLIENT,
	         ZimbraAccount.clientAccountName);

	   // Refresh the view, to pick up the new contact
	   FolderItem contactFolder = FolderItem.importFromSOAP(
	         app.zGetActiveAccount(),
	         "Contacts",
	         SOAP_DESTINATION_HOST_TYPE.CLIENT,
	         ZimbraAccount.clientAccountName);

	   app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);

	   // Select the item
	   app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs); // contactItem.fileAs);

	   String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();

	   // Click new tag
	   DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zToolbarPressPulldown(
	         Button.B_TAG, Button.O_TAG_NEWTAG);
	   dialogTag.zSetTagName(tagName);
	   dialogTag.zClickButton(Button.B_OK);      

	   // Make sure the tag was created on the server (get the tag ID)
	   app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
	   String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tagName +"']", "id");

	   // Make sure the tag was applied to the contact
	   app.zGetActiveAccount().soapSend(
	         "<GetContactsRequest xmlns='urn:zimbraMail'>" +
	         "<cn id='"+ contactItem.getId() +"'/>" +
	         "</GetContactsRequest>",
	         SOAP_DESTINATION_HOST_TYPE.CLIENT,
	         ZimbraAccount.clientAccountName);

	   String contactTags = app.zGetActiveAccount().soapSelectValue(
	         "//mail:GetContactsResponse//mail:cn",
	         "t");

	   ZAssert.assertEquals(contactTags, tagID,
	         "Verify the tag appears on the contact id=" +  contactItem.getId());

	   //verify toasted message '1 contact tagged ...'
	   Toaster toast = app.zPageMain.zGetToaster();
	   String toastMsg = toast.zGetToastMessage();
	   ZAssert.assertStringContains(toastMsg,
	         "1 contact tagged \"" + tagName + "\"",
	         "Verify toast message '" + "1 contact tagged \"" + tagName + "\"'" );

	}
  	
}

