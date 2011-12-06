package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;




import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactGroupNew;


public class MoveContactGroup extends AjaxCommonTest  {
	public MoveContactGroup() {
		logger.info("New "+ MoveContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	private void Verify(FolderItem folder, ContactGroupItem group) throws HarnessException {
	     
        //verify toasted message 1 contact group moved to target folder
        String expectedMsg = "1 contact group moved to";
        String toastMessage = app.zPageMain.zGetToaster().zGetToastMessage();
        ZAssert.assertStringContains(toastMessage, expectedMsg , "Verify toast message '" + expectedMsg + "'");
        ZAssert.assertStringContains(toastMessage,folder.getName() , "Verify toast message '" + folder.getName() + "'");

        
        //verify moved contact group not displayed in Contact folder
        ZAssert.assertFalse(app.zPageAddressbook.zIsContactDisplayed(group), "Verify contact group fileAs (" + group.fileAs + ") not displayed in folder Contacts");
	
        //verify moved contact displayed in target folder
        // refresh target folder
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, folder);
   	 
        ZAssert.assertTrue(app.zPageAddressbook.zIsContactDisplayed(group), "Verify contact group fileAs (" + group.fileAs + ") displayed in folder " + folder.getName());
	
	}
	
	
	@Test(	description = "Move a contact group to folder Emailed Contacts by click Move dropdown on toolbar",
			groups = { "smoke" })
	public void MoveToEmailedContactsFromMoveDropdownOnToolbar() throws HarnessException {
		        
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
 		
	    // Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
	
        //click Move dropdown on toolbar then select emailed contact
        app.zPageAddressbook.zToolbarPressPulldown(Button.B_MOVE,emailedContacts);
        
        //move group to different folder
        Verify(emailedContacts, group);    
 
   	}

	

	@Test(	description = "Move a contact group to folder Emailed Contacts by click Move on Context menu",
			groups = { "functional" })
	public void MoveToEmailedContactsClickMoveOnContextmenu() throws HarnessException {
		        
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
 		
	    	// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
	
        //click Move icon on context menu
	    DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_MOVE, group.fileAs);
	     
	    //enter the moved folder
        dialogContactMove.zClickTreeFolder(emailedContacts);
        dialogContactMove.zClickButton(Button.B_OK);
  
        //move group to different folder
        Verify(emailedContacts, group);    
 
   	}

	@Test(	description = "Move a contact group to folder Emailed Contacts with shortcut m",
			groups = { "functional" })
	public void MoveToEmailedContactsClickShortcutm() throws HarnessException {
		        
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
 		
	    // Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
	 
        //click shortcut m
	    DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zKeyboardShortcut(Shortcut.S_MOVE);
    
	    //enter the moved folder
        dialogContactMove.zClickTreeFolder(emailedContacts);
        dialogContactMove.zClickButton(Button.B_OK);
  
	    //move group to different folder
        Verify(emailedContacts, group);    
 
   	}


	@Test(	description = "Move a contact group to folder Emailed Contacts by drag and drop",
			groups = { "functional" })
	public void DnDToEmailedContacts() throws HarnessException {
		
		   // Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
	
	
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
		
    
		app.zPageAddressbook.zDragAndDrop(
				"css=td#zlif__CNS-main__" + group.getId() + "__fileas:contains("+ group.fileAs + ")",
				"css=td#zti__main_Contacts__" + emailedContacts.getId() + "_textCell:contains("+ emailedContacts.getName() + ")");
			
	  
        //verify
        Verify(emailedContacts,group);
        
   	}
	
	@Test(	description = "Move a group to folder Emailed Contacts by click toolbar Edit then open folder dropdown",
			groups = { "functional" })
	public void MoveToEmailedContactsClickToolbarEditThenFolderDropdown() throws HarnessException {
		
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
	
	    // Get emailed contacts folder
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
	
		//Click Edit 	
        FormContactGroupNew formContactGroupNew = (FormContactGroupNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
	  
        //click location's folder 
        DialogMove dialogMove =  formContactGroupNew.clickFolder();
        
	    //enter the moved folder
        dialogMove.zClickTreeFolder(emailedContacts);
        dialogMove.zClickButton(Button.B_OK);
        
        //click  Save button
        formContactGroupNew.save();
        
        // verify toasted message 'Group Saved'
        String expectedMsg = "Group Saved";
        String toastMessage = app.zPageMain.zGetToaster().zGetToastMessage();
        ZAssert.assertStringContains(toastMessage, expectedMsg , "Verify toast message '" + expectedMsg + "'");
       
        // verify group moved to different folder
        //verify moved contact group not displayed in Contact folder
        ZAssert.assertFalse(app.zPageAddressbook.zIsContactDisplayed(group), "Verify contact group fileAs (" + group.fileAs + ") not displayed in folder Contacts");
	
        //verify moved contact displayed in target folder
        // refresh target folder
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, emailedContacts);
   	 
        ZAssert.assertTrue(app.zPageAddressbook.zIsContactDisplayed(group), "Verify contact group fileAs (" + group.fileAs + ") displayed in folder " + emailedContacts.getName());
	           
	}
}

