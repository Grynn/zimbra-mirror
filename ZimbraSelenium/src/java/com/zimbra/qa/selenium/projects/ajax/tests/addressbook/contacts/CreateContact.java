package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactNew.Field;


//TODO: add more in ContactItem.java

public class CreateContact extends AjaxCommonTest  {

	public CreateContact() {
		logger.info("New "+ CreateContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		
		
	}
	
	

	
	@Test(	description = "Create a basic contact item by click New in page Addressbook ",
			groups = { "sanity" })
	public void ClickNew() throws HarnessException {
		
		//-- DATA
		
		String contactFirst = "First" + ZimbraSeleniumProperties.getUniqueString();
		String contactLast = "Last"+ ZimbraSeleniumProperties.getUniqueString();
		String contactEmail = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@domain.com";
		
		
		
		//-- GUI Action
		
		// app.zPageAddressbook.zRefresh();
		
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);
		
        // Fill in the form
		formContactNew.zFillField(Field.FirstName, contactFirst);
		formContactNew.zFillField(Field.LastName, contactLast);
		formContactNew.zFillField(Field.Email, contactEmail);
		formContactNew.zSubmit();

		
		//-- Data Verification
		
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
				+		"<query>#firstname:"+ contactFirst +"</query>"
				+	"</SearchRequest>");
		String contactId = app.zGetActiveAccount().soapSelectValue("//mail:cn", "id");
		
		ZAssert.assertNotNull(contactId, "Verify the contact is returned in the search");
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail'>"
			+		"<cn id='"+ contactId +"'/>"
			+	"</GetContactsRequest>");
	
		String lastname = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='lastName']", null);
		String firstname = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='firstName']", null);
		String email = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='email']", null);
		
		ZAssert.assertEquals(lastname, contactLast, "Verify the last name was saved correctly");
		ZAssert.assertEquals(firstname, contactFirst, "Verify the first name was saved correctly");
		ZAssert.assertEquals(email, contactEmail, "Verify the email was saved correctly");
		
		
	}
	
	@Test(	description = "Create a basic contact item by use PullDown Menu->Contacts",
			groups = { "functional" })
	public void CreateContactFromPulldownMenu() throws HarnessException {				
		
		//-- DATA
		ContactItem contact = new ContactItem();
		contact.firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		contact.lastName = "Last"+ ZimbraSeleniumProperties.getUniqueString();
		contact.email = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@domain.com";
		
		
		
		//-- GUI Action
		
		// app.zPageAddressbook.zRefresh();
		
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_CONTACT);
		
        // Fill in the form
		formContactNew.zFill(contact);

		// Save it
		formContactNew.zSubmit();

		
		
		//-- Data Verification
		
		ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
		
		ZAssert.assertEquals(actual.lastName, contact.lastName, "Verify the last name was saved correctly");
		ZAssert.assertEquals(actual.firstName, contact.firstName, "Verify the last name was saved correctly");
		ZAssert.assertEquals(actual.email, contact.email, "Verify the last name was saved correctly");

	}
	
	@Test(	description = "Cancel creating a contact item - Click Yes",
			groups = { "functional" })
	public void CancelCreateContactClickYes() throws HarnessException {
		
		//-- DATA

		ContactItem contact = new ContactItem();
		contact.firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		contact.lastName = "Last"+ ZimbraSeleniumProperties.getUniqueString();
		contact.email = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@domain.com";

		
		//-- GUI action
		
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);
		
		// Fill the fields
		formContactNew.zFill(contact);
		
		// Click Cancel
		DialogWarning dialogWarning = (DialogWarning)formContactNew.zToolbarPressButton(Button.B_CANCEL);
		
	    // Click Yes in popup dialog 
	    dialogWarning.zClickButton(Button.B_YES);
	    
	    
		//-- Data Verification
		
	    //verify toasted message 'contact created'
        Toaster toast = app.zPageMain.zGetToaster();
        String toastMsg = toast.zGetToastMessage();
        ZAssert.assertStringContains(toastMsg, "Contact Created", "Verify toast message 'Contact Created'");
  
	    // Verify contact  created  	    
		ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
		
		ZAssert.assertEquals(actual.lastName, contact.lastName, "Verify the last name was saved correctly");
		ZAssert.assertEquals(actual.firstName, contact.firstName, "Verify the last name was saved correctly");
		ZAssert.assertEquals(actual.email, contact.email, "Verify the last name was saved correctly");


	}

	@Test(	description = "Cancel creating a contact item - Click No",
			groups = { "functional" })
	public void CancelCreateContactClickNo() throws HarnessException {	
		
		//-- DATA

		ContactItem contact = new ContactItem();
		contact.firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		contact.lastName = "Last"+ ZimbraSeleniumProperties.getUniqueString();
		contact.email = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@domain.com";

		
		//-- GUI action
		
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);
		
		// Fill the fields
		formContactNew.zFill(contact);
		
		// Click Cancel
		DialogWarning dialogWarning = (DialogWarning)formContactNew.zToolbarPressButton(Button.B_CANCEL);
		
	    // Click Yes in popup dialog 
	    dialogWarning.zClickButton(Button.B_NO);
	    
	    
		//-- Data Verification
		
	    // Verify contact  created  	    
		ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
		ZAssert.assertNull(actual, "Verify the contact is not created");

	}

	@Test(	description = "Cancel creating a contact item - Click Cancel",
			groups = { "functional" })
	public void CancelCreateContactClickCancel() throws HarnessException {		

		//-- DATA

		ContactItem contact = new ContactItem();
		contact.firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		contact.lastName = "Last"+ ZimbraSeleniumProperties.getUniqueString();
		contact.email = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@domain.com";

		
		//-- GUI action
		
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);
		
		// Fill the fields
		formContactNew.zFill(contact);
		
		// Click Cancel
		DialogWarning dialogWarning = (DialogWarning)formContactNew.zToolbarPressButton(Button.B_CANCEL);
		
	    // Click Yes in popup dialog 
	    dialogWarning.zClickButton(Button.B_CANCEL);
	    
	    // Verify the contact form comes back
	    ZAssert.assertTrue(formContactNew.zIsActive(), "Verify the contact form comes back");
	    
	    // Save the contact
	    formContactNew.zSubmit();
	    
	    
		//-- Data Verification
		
		ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
		
		ZAssert.assertEquals(actual.lastName, contact.lastName, "Verify the last name was saved correctly");
		ZAssert.assertEquals(actual.firstName, contact.firstName, "Verify the last name was saved correctly");
		ZAssert.assertEquals(actual.email, contact.email, "Verify the last name was saved correctly");

	}

	@Test(	description = "create a contact item with all attribute",
			groups = { "smoke" })
	public void CreateContactWithAllAttributes() throws HarnessException {
		
		//-- Data
		
		// Create a contact Item
		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.AllAttributes);

		
		//-- GUI
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);
				
		// show all hidden field for names:
		formContactNew.zDisplayHiddenName();
		
		// fill items
		formContactNew.zFill(contact);
		
		// Save the contact
        formContactNew.zSubmit();
		
        
        //-- Verificaiton
        
        //verify toasted message 'contact created'  
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(), "Contact Created", "Verify toast message 'Contact Created'");

        
		//-- Data Verification
		
		ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
		
		ZAssert.assertEquals(actual.lastName, contact.lastName, "Verify the last name was saved correctly");
		ZAssert.assertEquals(actual.firstName, contact.firstName, "Verify the last name was saved correctly");
		ZAssert.assertEquals(actual.email, contact.email, "Verify the last name was saved correctly");

	}

}
