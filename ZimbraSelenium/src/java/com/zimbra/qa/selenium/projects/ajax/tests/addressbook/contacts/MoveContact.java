package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactGroupNew;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactNew;


public class MoveContact extends AjaxCommonTest  {
	public MoveContact() {
		logger.info("New "+ MoveContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	private void  Verify(FolderItem folder, ContactItem contactItem) throws HarnessException {
		
	   
        //verify toasted message 1 contact moved to target folder
        String toastMessage = app.zPageMain.zGetToaster().zGetToastMessage();
        String expectedMsg = "1 contact moved to";
        ZAssert.assertStringContains(toastMessage, expectedMsg , "Verify toast message '" + expectedMsg + "'");
        ZAssert.assertStringContains(toastMessage,folder.getName() , "Verify toast message '" + folder.getName() + "'");

        
        //verify moved contact not displayed in folder Contacts
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contactItem.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") not displayed in folder Contacts");
        
 
        //verify moved contact displayed in target folder
        // refresh target folder
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, folder);
   	 
        contacts = app.zPageAddressbook.zListGetContacts(); 
         
		isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contactItem.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertTrue(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") displayed in folder Emailed Contacts");
                		
	}

	@Test(	description = "Move a contact item to folder Emailed Contacts by click shortcut m",
			groups = { "functional" })
	public void MoveToEmailedContactsClickShortcutm() throws HarnessException {
		
		 // Create a contact via Soap then select
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
	
	
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);

		
		 //click shortcut m
	    DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zKeyboardShortcut(Shortcut.S_MOVE);
      
	    //enter the moved folder
        dialogContactMove.zClickTreeFolder(emailedContacts);
        dialogContactMove.zClickButton(Button.B_OK);
     
        //Verify
        Verify(emailedContacts,contactItem);
        
   	}

	@Test(	description = "Move a contact item to folder Emailed Contacts  by click Move on context menu",
			groups = { "functional" })
	public void MoveToEmailedContactsClickMoveOnContextmenu() throws HarnessException {
		
		 // Create a contact via Soap then select
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
	
	
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);

		
	    //click Move icon on context menu
	    DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_MOVE, contactItem.fileAs);
	 
	    //enter the moved folder
        dialogContactMove.zClickTreeFolder(emailedContacts);
        dialogContactMove.zClickButton(Button.B_OK);
     
        //Verify
        Verify(emailedContacts,contactItem);
        
   	}

	@Test(	description = "Move a contact item to folder Emailed Contacts by click tool bar Move",
			groups = { "smoke" })
	public void MoveToEmailedContactsClickMoveOnToolbar() throws HarnessException {
		
		 // Create a contact via Soap then select
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
	
	
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
		
       //click Move dropdown on toolbar
        app.zPageAddressbook.zToolbarPressPulldown(Button.B_MOVE,emailedContacts);
    
   
        //Verify
        Verify(emailedContacts,contactItem);
        
   	}
	


	@Test(	description = "Move a contact item to folder Emailed Contacts by drag and drop",
			groups = { "functional" })
	public void DnDToEmailedContacts() throws HarnessException {
		
		 // Create a contact via Soap then select
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
	
	
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
		
    
		app.zPageAddressbook.zDragAndDrop(
				"css=td#zlif__CNS-main__" + contactItem.getId() + "__fileas:contains("+ contactItem.fileAs + ")",
				"css=td#zti__main_Contacts__" + emailedContacts.getId() + "_textCell:contains("+ emailedContacts.getName() + ")");
			
	
        //verify
        Verify(emailedContacts,contactItem);
        
   	}
	
	@Test(	description = "Move a contact item to folder Emailed Contacts by click toolbar Edit then Location",
			groups = { "functional" })
	public void MoveToEmailedContactsClickToolbarEditThenLocation() throws HarnessException {
		
		 // Create a contact via Soap then select
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
	
	
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
		
		//Click Edit contact	
        FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
	  
        //Click Location
        DialogMove dialogContactMove = (DialogMove) formContactNew.zClick(Button.B_MOVE, app.zPageAddressbook);
        
        //enter the moved folder
        dialogContactMove.zClickTreeFolder(emailedContacts);
        dialogContactMove.zClickButton(Button.B_OK);
      
        //Click Save
        formContactNew.zSubmit();
        
        //Verify
        Verify(emailedContacts,contactItem);
        
   	}
	
	

}

