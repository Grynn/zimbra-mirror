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
}
