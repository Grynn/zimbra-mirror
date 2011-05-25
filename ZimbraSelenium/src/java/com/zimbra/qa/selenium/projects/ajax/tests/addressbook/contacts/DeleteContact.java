package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import java.awt.event.KeyEvent;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class DeleteContact extends AjaxCommonTest  {
	public DeleteContact() {
		logger.info("New "+ DeleteContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	private void VerifyContactDeleted(ContactItem contactItem) throws HarnessException{
		  //verify toasted message 1 contact moved to Trash
		String expectedMsg = "1 contact moved to Trash";
	    ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
			        expectedMsg , "Verify toast message '" + expectedMsg + "'");

	      //verify deleted contact not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contactItem.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") deleted");
    
	}
	@Test(	description = "Delete a contact item",
			groups = { "smoke" })
	public void DeleteContactByClickDeleteOnToolbar() throws HarnessException {

		// Create a contact via soap 
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
 
        //delete contact
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
       
        //verify contact deleted
        VerifyContactDeleted(contactItem);    
   	}

	@Test(	description = "Delete a contact item selected with checkbox",
			groups = { "functional" })
	public void DeleteContactSelectedWithCheckbox() throws HarnessException {

		// Create a contact via soap 
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_CHECKBOX);
 

        //delete contact
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
       
        
        //verify contact deleted
        VerifyContactDeleted(contactItem);       
   	}

	@Test(	description = "Delete a contact item using keyboard short cut Del",
			groups = { "functional" })
	public void DeleteContactUseShortcutDel() throws HarnessException {

		// Create a contact via soap 
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
 
        //delete contact
        app.zPageAddressbook.zShortcut(KeyEvent.VK_DELETE);
       
        //verify contact deleted
        VerifyContactDeleted(contactItem);    
   	}
	
	@Test(	description = "Delete a contact item using keyboard short cut backspace",
			groups = { "functional" })
	public void DeleteContactUseShortcutBackspace() throws HarnessException {

		// Create a contact via soap 
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
 
        //delete contact
        app.zPageAddressbook.zShortcut(KeyEvent.VK_BACK_SPACE);
       
        //verify contact deleted
        VerifyContactDeleted(contactItem);    
   	}
	
	@Test(	description = "Delete multi contact items",
			groups = { "functional" })
	public void DeleteMultipleContacts() throws HarnessException {

		// Create multi contacts via soap 
		//ContactItem contactItem1 = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_CHECKBOX);
		//ContactItem contactItem2 = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_CHECKBOX);
		//ContactItem contactItem3 = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_CHECKBOX);
		
		  // Create a contact via Soap
		ContactItem contactItem1 = ContactItem.createUsingSOAP(app);			             
		contactItem1.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));
		  		  
		// Create a contact via Soap
		ContactItem contactItem2 = ContactItem.createUsingSOAP(app);			             
		contactItem2.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));
		
		// Create a contact via Soap
		ContactItem contactItem3 = ContactItem.createUsingSOAP(app);			             
		contactItem3.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));
		
		  // Refresh the view, to pick up the new contact
	    FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
	    GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
	    app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
	    
	    // Select the item
	    app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contactItem1.fileAs);
	    app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contactItem2.fileAs);
	    app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contactItem3.fileAs);
	   
		//delete 3 contacts
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
        
        
 
  	   //verify toasted message 3 contact moved to Trash
		String expectedMsg = "3 contacts moved to Trash";
	    ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
			        expectedMsg , "Verify toast message '" + expectedMsg + "'");

	      //verify deleted contact not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
        int count=0;
	      for (ContactItem ci : contacts) {
		    if (ci.fileAs.equals(contactItem1.fileAs) ||
			  ci.fileAs.equals(contactItem2.fileAs) ||
			  ci.fileAs.equals(contactItem3.fileAs)
		      ) {
             count++;	 			
	  	    }
	      }
			
        ZAssert.assertTrue(count==0, "Verify contact fileAs (" + contactItem1.fileAs + "," + contactItem2.fileAs + "," + contactItem3.fileAs + ") deleted");
            
   	}
}
