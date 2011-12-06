package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;


import java.awt.event.KeyEvent;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;




public class DeleteContactGroup extends AjaxCommonTest  {
	public DeleteContactGroup() {
		logger.info("New "+ DeleteContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	private void VerifyContactGroupDeleted(ContactGroupItem group  ) throws HarnessException {
		//verify toasted message 1 contact group moved to Trash
        String expectedMsg = "1 contact group moved to Trash";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
		        expectedMsg , "Verify toast message '" + expectedMsg + "'");

        //verify deleted contact group not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(group.groupName)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact group " + group.groupName + " deleted");        

    	FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);


        //verify deleted contact displayed in trash folder
        // refresh Trash folder
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, trash);
   	 
        contacts = app.zPageAddressbook.zListGetContacts(); 
         
		isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(group.groupName)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertTrue(isFileAsEqual, "Verify contact group (" + group.groupName + ") displayed in Trash folder");

	}
		  
		
	@Test(	description = "Delete a contact group by click Delete button on toolbar",
			groups = { "smoke" })
	public void ClickDeleteOnToolbar() throws HarnessException {

		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app,Action.A_LEFTCLICK);
	  
    
        //delete contact group by click Delete button on toolbar
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
       
        //verify contact group deleted
        VerifyContactGroupDeleted(group);
           
   	}

	@Test(	description = "Delete a contact group by click Delete on Context Menu",
			groups = { "functional" })
	public void ClickDeleteOnContextMenu() throws HarnessException {

		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app,Action.A_LEFTCLICK);
	  
    
        //delete contact group by click Delete on Context menu
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE, group.fileAs);
       
        //verify contact group deleted
        VerifyContactGroupDeleted(group);
           
   	}

	@Test(	description = "Delete a contact group selected by checkbox by click Delete button on toolbar",
			groups = { "functional" })
	public void DeleteContactGroupSelectedWithCheckbox() throws HarnessException {

		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app,Action.A_CHECKBOX);
	  
    
        //delete contact group by click Delete button on toolbar
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
       
        //verify contact group deleted
        VerifyContactGroupDeleted(group);
           
   	}

	@Test(	description = "Delete a contact group use shortcut Del",
			groups = { "functional" })
	public void UseShortcutDel() throws HarnessException {

		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app,Action.A_LEFTCLICK);
	      
        //delete contact group by click shortcut Del
		 app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_DELETE);
		 
        //verify contact group deleted
        VerifyContactGroupDeleted(group);           
   	}
	
	@Test(	description = "Delete a contact group use shortcut backspace",
			groups = { "functional" })
	public void  UseShortcutBackspace() throws HarnessException {

		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app,Action.A_LEFTCLICK);
	      
        //delete contact group by click shortcut backspace
		app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_BACK_SPACE);
		 
        //verify contact group deleted
        VerifyContactGroupDeleted(group);           
   	}

	@Test(	description = "Delete multiple contact groups at once",
			groups = { "functional" })
	public void DeleteMultipleContactGroups() throws HarnessException {
		
		 // Create a contact group via Soap
		  ContactGroupItem group1 = ContactGroupItem.createUsingSOAP(app);
			             		  
		  
		// Create a contact group via Soap
		  ContactGroupItem group2 = ContactGroupItem.createUsingSOAP(app);
		  
		// Create a contact group via Soap
		  ContactGroupItem group3 = ContactGroupItem.createUsingSOAP(app);
			             
		  
	      // Refresh the view, to pick up the new contact groups
	      FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");	      
	      app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
	    
	      // Select the items
	      app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group1.fileAs);		       
	      app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group2.fileAs);        
	      app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group3.fileAs);
	       
		
		  //delete multiple contact groups by click Delete button on toolbar
          app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

	      //verify toasted message 3 contacts moved to Trash
          String expectedMsg = "3 contacts moved to Trash";
          ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
	        expectedMsg , "Verify toast message '" + expectedMsg + "'");

          //verify deleted contact group not displayed
          List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
	           
	      int count=0;
	      for (ContactItem ci : contacts) {
		    if (ci.fileAs.equals(group1.groupName) ||
			  ci.fileAs.equals(group2.groupName) ||
			  ci.fileAs.equals(group3.groupName)
		      ) {
               count++;	 			
	  	    }
	      }
	
          ZAssert.assertTrue(count==0, "Verify contact groups " + group1.groupName + "," + group2.groupName + "," +  group3.groupName + " deleted");                  
	}
	

	@Test(	description = "Delete contact + contact group at once",
			groups = { "functional" })
	public void DeleteMixOfContactAndGroup() throws HarnessException {
		
		 // Create a contact group via Soap
		  ContactGroupItem group = ContactGroupItem.createUsingSOAP(app);
			             		  		  
		  // Create a contact via Soap
		  ContactItem contactItem = ContactItem.createUsingSOAP(app);			             
		  
		  
	      // Refresh the view, to pick up the newly created ones
	      FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");	      
	      app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
	  
	      // Select the items
	      app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group.fileAs);		       
	      app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contactItem.fileAs);        
	  
    	  //delete contact + group by click Delete button on toolbar
          app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

          //verify toasted message 2 contacts moved to Trash
          String expectedMsg = "2 contacts moved to Trash";
          ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
	        expectedMsg , "Verify toast message '" + expectedMsg + "'");

	      //verify deleted contact + group not displayed
          List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
	           
          
	      int count=0;
	      for (ContactItem ci : contacts) {
		    if (ci.fileAs.equals(group.groupName) ||			  
			    ci.fileAs.equals(contactItem.fileAs)
		      ) {
               count++;	 			
	  	    }
	      }
	
          ZAssert.assertTrue(count==0, "Verify contact + group " + contactItem.fileAs  + "," +  group.groupName + " deleted");                  

		  
	}
	
	@Test(	description = "Move a contact group to folder Trash by expand Move dropdown then select Trash",
			groups = { "functional" })
	public void MoveToTrashFromMoveDropdownOnToolbar() throws HarnessException {
		        
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
 		
	    // Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
	
        	
        //click Move dropdown on toolbar then select Trash
        app.zPageAddressbook.zToolbarPressPulldown(Button.B_MOVE,folder);
  
       
        //verify contact group deleted
        VerifyContactGroupDeleted(group);   
 
   	}
	
	@Test(	description = "Move a contact group to trash folder by drag and drop",
			groups = { "functional" })
	public void DnDToTrash() throws HarnessException {
		
		   // Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);

	
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		
	    
		app.zPageAddressbook.zDragAndDrop(
				"css=td#zlif__CNS-main__" + group.getId() + "__fileas:contains("+ group.fileAs + ")",
				"css=td#zti__main_Contacts__" + folder.getId() + "_textCell:contains("+ folder.getName() + ")");
			
		 //verify contact group deleted
        VerifyContactGroupDeleted(group);   
 
         
   	}

}
