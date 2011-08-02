package com.zimbra.qa.selenium.projects.desktop.tests.addressbook.contacts;



import java.util.List;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.addressbook.*;


public class ViewContact extends AjaxCommonTest  {
	public ViewContact() {
		logger.info("New "+ ViewContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	private ContactItem createContact (String firstLetterOfFirstName) throws HarnessException {
		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);
	    contact.firstName = firstLetterOfFirstName + contact.firstName;	    
        contact.fileAs = contact.firstName + " " + contact.lastName;
        
        app.zGetActiveAccount().soapSend(
                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
                "<cn >" +
                "<a n='firstName'>" + contact.firstName +"</a>" +
                "<a n='lastName'>" + contact.lastName +"</a>" +
                "<a n='email'>" + contact.email + "</a>" +
                "<a n='fileAs'>2</a>" +
                "</cn>" +
                "</CreateContactRequest>");
	       
	       return contact;
		}
		
	
	@Test(	description = "View a contact  created via soap",
			groups = { "functional" })
	public void DisplayContact_01() throws HarnessException {
		         		
	    // Create a contact via Soap then select
		ContactItem contact = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
	
	    // Select the contact 
		DisplayContact contactView = (DisplayContact) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.fileAs);
	  
		ZAssert.assertStringContains(contactView.zGetContactProperty(DisplayContact.Field.FileAs), contact.firstName + " " + contact.lastName, "Verify contact fileAs (" + contact.fileAs + ") displayed");	
		
	    ZAssert.assertStringContains(contactView.zGetContactProperty(DisplayContact.Field.Email), contact.email, "Verify contact email (" + contact.email + ") displayed");	
		           
	    //TODO: add more verification
   	}

	@Test(	description = "Click Alphabetbar button All: Verify contact started with digit and A-Z listed ",
			groups = { "functional" })
	public void DisplayContact_02() throws HarnessException {
	
		 // Create  contacts  
		ContactItem contact1 = createContact("B");
		ContactItem contact2 = createContact("5");
		ContactItem contact3 = createContact("b");
		
    	    
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        //click All       
		app.zPageAddressbook.zToolbarPressButton(Button.B_AB_ALL);
		
		
		//verify all contact name are listed
		List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
		int countContact=0;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contact1.fileAs) ||
			    ci.fileAs.equals(contact2.fileAs) ||
			    ci.fileAs.equals(contact3.fileAs) ) 
			{
				countContact++;
			}			
		}
	
		ZAssert.assertTrue(countContact==3, "Verify contact (" + contact1.fileAs + "," + contact2.fileAs + " and "+ contact3.fileAs + ") displayed ");
	}

	@Test(	description = "Click Alphabetbar button 123: Verify contact started with digit listed and A-Z not-listed ",
			groups = { "functional" })
	public void DisplayContact_03() throws HarnessException {
	
		 // Create  contacts  
		ContactItem contact1 = createContact("B");
		ContactItem contact2 = createContact("5");
		ContactItem contact3 = createContact("b");
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        //click 123      
		app.zPageAddressbook.zToolbarPressButton(Button.B_AB_123);
		
		//verify all contacts started with 123 listed, contacts started with alphabet are not listed
		List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
		int countContact= 0;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contact2.fileAs)) 
			{
	            countContact++;
			}
			if (ci.fileAs.equals(contact1.fileAs)  ||
				ci.fileAs.equals(contact3.fileAs)) {
				countContact--; 
			}
				
		}
	
		ZAssert.assertTrue(countContact==1, "Verify contact  " + contact1.fileAs + "," + contact3.fileAs + " not displayed, and "+ contact2.fileAs + " displayed ");
	}

	@Test(	description = "Click Alphabetbar button B: Verify only contact started with B|b is listed ",
			groups = { "functional" })
	public void DisplayContact_04() throws HarnessException {
	
		 // Create  contacts  
		ContactItem contact1 = createContact("B");
		ContactItem contact2 = createContact("5");
		ContactItem contact3 = createContact("V");
		ContactItem contact4 = createContact("b");

      
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        //click 123      
		app.zPageAddressbook.zToolbarPressButton(Button.B_AB_B);
		
		//verify all contact started with B listed, others not listed
		List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
		int countContact= 0;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contact1.fileAs)  || ci.fileAs.equals(contact4.fileAs) ) 
			{
				countContact++;
			}
			if (ci.fileAs.equals(contact2.fileAs) || ci.fileAs.equals(contact3.fileAs) )  {
				countContact--; 
			}
				
		}
	
		ZAssert.assertTrue(countContact==2, "Verify contact " + contact1.fileAs + "," + contact4.fileAs + " displayed, and " + contact2.fileAs + "," + contact3.fileAs + " not displayed ");
	}

}

