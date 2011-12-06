package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import java.awt.event.KeyEvent;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogMove;


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
        ZAssert.assertFalse(app.zPageAddressbook.zIsContactDisplayed(contactItem), "Verify contact fileAs (" + contactItem.fileAs + ") deleted");
    

        // refresh Trash folder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, trash);
   	 		
        //verify deleted contact displayed in trash folder        
        ZAssert.assertTrue(app.zPageAddressbook.zIsContactDisplayed(contactItem), "Verify contact fileAs (" + contactItem.fileAs + ") displayed in Trash folder");
     
	}
	@Test(	description = "Delete a contact item",
			groups = { "smoke" })
	public void ClickDeleteOnToolbar() throws HarnessException {

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
	public void UseShortcutDel() throws HarnessException {

		// Create a contact via soap 
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
 
        //delete contact
        app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_DELETE);
       
        //verify contact deleted
        VerifyContactDeleted(contactItem);    
   	}
	
	@Test(	description = "Delete a contact item using keyboard short cut backspace",
			groups = { "functional" })
	public void UseShortcutBackspace() throws HarnessException {

		// Create a contact via soap 
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
 
        //delete contact
        app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_BACK_SPACE);
       
        //verify contact deleted
        VerifyContactDeleted(contactItem);    
   	}
	

	@Test(	description = "Right click then click delete",
			groups = { "smoke" })
	public void DeleteFromContextMenu() throws HarnessException {
		
		// Create a contact via soap 
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
 
		//select delete option
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE, contactItem.fileAs);

        //verify contact deleted
        VerifyContactDeleted(contactItem);    
                 
   	}

	@Test(	description = "Delete multiple contact items",
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
	    FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
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
	@Test(	description = "Move a contact item to trash folder by expand Move dropdown on toolbar, then select Trash",
			groups = { "functional" })
	public void MoveToTrashFromMoveDropdownOnToolbar() throws HarnessException {
		
		 // Create a contact via Soap then select
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
	
	
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		
	    //click Move dropdown on toolbar then select Trash
        app.zPageAddressbook.zToolbarPressPulldown(Button.B_MOVE,folder);
  
     
        //verify
        VerifyContactDeleted(contactItem);
        
   	}
	
	@Test(	description = "Move a contact item to trash folder by drag and drop",
			groups = { "functionaly" })
	public void DnDToTrash() throws HarnessException {
		
		 // Create a contact via Soap then select
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
	
	
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		
	    
		app.zPageAddressbook.zDragAndDrop(
				"css=td#zlif__CNS-main__" + contactItem.getId() + "__fileas:contains("+ contactItem.fileAs + ")",
				"css=td#zti__main_Contacts__" + folder.getId() + "_textCell:contains("+ folder.getName() + ")");
			
	
	     //verify
        VerifyContactDeleted(contactItem);
   
         
   	}
}
