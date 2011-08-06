package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;



import java.util.List;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactNew.Locators;


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
		
	
	

	@Test(	description = "Click Alphabetbar button All: Verify contact started with digit and A-Z listed ",
			groups = { "functional" })
	public void ClickAllVerifyDisplayAllContacts() throws HarnessException {
	
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
	public void Click123VerifyDisplayContactStartWithDigit() throws HarnessException {
	
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
	public void ClickBVerifyDisplayContactStartWithBb() throws HarnessException {
	
		 // Create  contacts  
		ContactItem contact1 = createContact("B");
		ContactItem contact2 = createContact("5");
		ContactItem contact3 = createContact("V");
		ContactItem contact4 = createContact("b");

      
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        //click B
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

	
	@Test(	description = "Click all Alphabetbar buttons: Verify only contact started with the alphabet is listed ",
			groups = { "functional" })
	public void ClickAllAlphabetBarButtons() throws HarnessException {
	
		 // Create  contact 
		ContactItem[]    cgiArray = new ContactItem[26];
		Button[]           buttonArray = 
		 {Button.B_AB_A,Button.B_AB_B,Button.B_AB_C,Button.B_AB_D,Button.B_AB_E,Button.B_AB_F,Button.B_AB_G,
		  Button.B_AB_H,Button.B_AB_I,Button.B_AB_J,Button.B_AB_K,Button.B_AB_L,Button.B_AB_M,Button.B_AB_N,
		  Button.B_AB_O,Button.B_AB_P,Button.B_AB_Q,Button.B_AB_R,Button.B_AB_S,Button.B_AB_T,Button.B_AB_U,
		  Button.B_AB_V,Button.B_AB_W,Button.B_AB_X,Button.B_AB_Y,Button.B_AB_Z};
		
		for (int i=0; i<26; i++) {
			cgiArray[i] =  createContact(Character.toString((char)((int)'a' + i)));
		}		
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        for (int i=0; i<26; i++) {
          //click button      
		  app.zPageAddressbook.zToolbarPressButton(buttonArray[i]);
		
		  //verify contact started with button name listed
		  List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
		  int countGroup= 0;
		  for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(cgiArray[i].fileAs) || ci.fileAs.equals(cgiArray[i].fileAs)) 
			{
	            countGroup++;
			}
	
		}
	
		ZAssert.assertTrue(countGroup==1, "Verify contact " + cgiArray[i].fileAs + " displayed");
       

		// Delete the verified contact to save time going through it in contact list
        // for subsequent test cases.
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, cgiArray[i].fileAs);
		
		app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

		String expectedMsg = "1 contact moved to Trash";
		ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
				        expectedMsg , "Verify toast message '" + expectedMsg + "'");

        }
	}   
	
	private void FileAs(String fileAsOption) throws HarnessException{
		   // Create a contact via Soap then select
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
	
		// Open the contact in Edit view
        FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
	
        // open FileAs menu
        formContactNew.zClick(Button.B_FILEAS,app.zPageAddressbook);
        
		// select option Last, First 
        formContactNew.selectFileAs(fileAsOption);

        contactItem.fileAs = formContactNew.contactFullName(contactItem,fileAsOption);

        // verify fullname display correctly        
        ZAssert.assertEquals(
        	formContactNew.getDisplayedContactHeader(),
            contactItem.fileAs,
            "Verify fullname displayed correctly"
        );
        
        // Click Save
        formContactNew.zSubmit();        
        
    	//verify contact is displayed as FileAs
		List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
		boolean foundContact=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contactItem.fileAs)) 
			{
				foundContact=true;
				break;
			}			
		}
	
		ZAssert.assertTrue(foundContact, "Verify contact (" + contactItem.fileAs + ") displayed ");

        
	}
	
	@Test(	description = "View a contact, display view should be First Last",
			groups = { "functional" })
	public void DisplayContactInfo_FileAsEmail() throws HarnessException {		         	
	    // Create a contact via Soap then select
		ContactItem contact = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
	
	    // Select the contact  
		DisplayContact contactView = (DisplayContact) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.fileAs);
	  
		ZAssert.assertStringContains(contactView.zGetContactProperty(DisplayContact.Field.FileAs), contact.fileAs, "Verify contact fileAs (" + contact.fileAs + ") displayed");	
		
	    ZAssert.assertStringContains(contactView.zGetContactProperty(DisplayContact.Field.Email), contact.email, "Verify contact email (" + contact.email + ") displayed");			           
	}

	//First Last 
	@Test(	description = "View a contact, file as First Last",
			groups = { "functional" })
	public void FileAsFirstLast() throws HarnessException {		         		
	     FileAs(FormContactNew.Locators.zFileAsFirstLast); 		           
   	}

	//Last, First
	@Test(	description = "View a contact, file as Last, First",
			groups = { "functional" })
	public void FileAsLastCommaFirst() throws HarnessException {		         		
	     FileAs(FormContactNew.Locators.zFileAsLastCommaFirst); 		           
   	}
	
    //Company(Last, First)
	@Test(	description = "View a contact, file as Company(Last, First)",
			groups = { "functional" })
	public void FileAsCompanyLastCommaFirst() throws HarnessException {		         		
	     FileAs(FormContactNew.Locators.zFileAsCompanyLastCommaFirst); 		           
   	}

	//Company
	@Test(	description = "View a contact, file as Company",
			groups = { "functional" })
	public void FileAsCompany() throws HarnessException {		         		
	     FileAs(FormContactNew.Locators.zFileAsCompany); 		           
   	}

	//Last, First (Company)
	@Test(	description = "View a contact, file as Last, First (Company)",
			groups = { "functional" })
	public void FileAsLastCommaFirstCompany() throws HarnessException {		         		
	     FileAs(FormContactNew.Locators.zFileAsLastCommaFirstCompany); 		           
   	}

	
	//First Last (Company)
	@Test(	description = "View a contact, file as First Last (Company)",
			groups = { "functional" })
	public void FileAsFirstLastCompany() throws HarnessException {		         		
	     FileAs(FormContactNew.Locators.zFileAsFirstLastCompany); 		           
   	}

	//Company (First Last)
	@Test(	description = "View a contact, file as Company (First Last)",
			groups = { "functional" })
	public void FileAsCompanyFirstLast() throws HarnessException {		         		
	     FileAs(FormContactNew.Locators.zFileAsCompanyFirstLast); 		           
   	}


}

