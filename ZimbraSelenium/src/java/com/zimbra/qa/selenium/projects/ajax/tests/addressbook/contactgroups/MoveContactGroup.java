package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;




import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogMove;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactGroupNew;


public class MoveContactGroup extends AjaxCommonTest  {
	public MoveContactGroup() {
		logger.info("New "+ MoveContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	
	@Test(	description = "Move a contact group to folder Emailed Contacts by click Move dropdown on toolbar",
			groups = { "smoke" })
	public void MoveToEmailedContactsFromMoveDropdownOnToolbar() throws HarnessException {


		//--  Data
		
		// Create the sub addressbook
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		String foldername = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
					"<folder name='" + foldername +"' l='"+ root.getId() +"' view='contact'/>" +
				"</CreateFolderRequest>");
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the group
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

		//delete contact group by click Delete on Context menu
        app.zPageAddressbook.zToolbarPressPulldown(Button.B_MOVE, folder);

        
        //-- Verification
        
        ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
        ZAssert.assertNotNull(actual, "Verify the contact group exists");
        
        // Verify the contact group is in the trash
        ZAssert.assertEquals(actual.getFolderId(), folder.getId(), "Verify the contact group is in the sub addressbook");
        
           

   	}

	

	@Test(	description = "Move a contact group to folder Emailed Contacts by click Move on Context menu",
			groups = { "functional" })
	public void MoveToEmailedContactsClickMoveOnContextmenu() throws HarnessException {


		//--  Data
		
		// Create the sub addressbook
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		String foldername = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
					"<folder name='" + foldername +"' l='"+ root.getId() +"' view='contact'/>" +
				"</CreateFolderRequest>");
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
	    DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_MOVE, group.getName());
	     
	    //enter the moved folder
        dialogContactMove.zClickTreeFolder(folder);
        dialogContactMove.zClickButton(Button.B_OK);

        
        //-- Verification
        
        ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
        ZAssert.assertNotNull(actual, "Verify the contact group exists");
        
        // Verify the contact group is in the trash
        ZAssert.assertEquals(actual.getFolderId(), folder.getId(), "Verify the contact group is in the sub addressbook");
        
           
    
   	}

	@Test(	description = "Move a contact group to folder Emailed Contacts with shortcut m",
			groups = { "functional" })
	public void MoveToEmailedContactsClickShortcutm() throws HarnessException {


		//--  Data
		
		// Create the sub addressbook
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		String foldername = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
					"<folder name='" + foldername +"' l='"+ root.getId() +"' view='contact'/>" +
				"</CreateFolderRequest>");
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
	    DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zKeyboardShortcut(Shortcut.S_MOVE);
	     
	    //enter the moved folder
        dialogContactMove.zClickTreeFolder(folder);
        dialogContactMove.zClickButton(Button.B_OK);

        
        //-- Verification
        
        ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
        ZAssert.assertNotNull(actual, "Verify the contact group exists");
        
        // Verify the contact group is in the trash
        ZAssert.assertEquals(actual.getFolderId(), folder.getId(), "Verify the contact group is in the sub addressbook");
        
           
    
 
   	}


	@Test(	description = "Move a group to folder Emailed Contacts by click toolbar Edit then open folder dropdown",
			groups = { "functional" })
	public void MoveToEmailedContactsClickToolbarEditThenFolderDropdown() throws HarnessException {


		//--  Data
		
		// Create the sub addressbook
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		String foldername = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
					"<folder name='" + foldername +"' l='"+ root.getId() +"' view='contact'/>" +
				"</CreateFolderRequest>");
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		//Click Edit 	
        FormContactGroupNew formContactGroupNew = (FormContactGroupNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
	  
        //click location's folder 
        DialogMove dialogMove = (DialogMove) formContactGroupNew.zToolbarPressButton(Button.B_CHOOSE_ADDRESSBOOK);
        
	    //enter the moved folder
        dialogMove.zClickTreeFolder(folder);
        dialogMove.zClickButton(Button.B_OK);
        
        //click  Save button
        formContactGroupNew.zToolbarPressButton(Button.B_SAVE);


        
        //-- Verification
        
        ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
        ZAssert.assertNotNull(actual, "Verify the contact group exists");
        
        // Verify the contact group is in the trash
        ZAssert.assertEquals(actual.getFolderId(), folder.getId(), "Verify the contact group is in the sub addressbook");
        

	}

}

