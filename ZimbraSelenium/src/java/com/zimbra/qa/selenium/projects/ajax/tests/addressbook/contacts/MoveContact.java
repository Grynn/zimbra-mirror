package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogMove;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactNew;


public class MoveContact extends AjaxCommonTest  {
	public MoveContact() {
		logger.info("New "+ MoveContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}

	@Test(	description = "Move a contact item to sub addressbook by click tool bar Move",
			groups = { "smoke" })
	public void MoveContact_01() throws HarnessException {
 

		//-- Data
		
		// Create the sub addressbook
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		String foldername = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
					"<folder name='" + foldername +"' l='"+ root.getId() +"' view='contact'/>" +
				"</CreateFolderRequest>");
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		 // Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount(), GenerateItemType.Basic);
		
	
		//-- GUI
	
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);
		
		 // Click Move -> addressbook
        app.zPageAddressbook.zToolbarPressPulldown(Button.B_MOVE, folder);
      
        
        
        //-- Verification
        
        // Verify
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
        ZAssert.assertNotNull(actual, "Verify the contact still exists");
        ZAssert.assertEquals(actual.getFolderId(), folder.getId(), "Verify the contact exists in the addressbook folder");
        
        

   	}
	


	@Test(	description = "Move a contact item to sub addressbook by click shortcut m",
			groups = { "functional" })
	public void MoveContact_02() throws HarnessException {

		//-- Data
		
		// Create the sub addressbook
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		String foldername = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
					"<folder name='" + foldername +"' l='"+ root.getId() +"' view='contact'/>" +
				"</CreateFolderRequest>");
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		 // Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount(), GenerateItemType.Basic);
		
	
		//-- GUI
	
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);
		
		 //click shortcut m
	    DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zKeyboardShortcut(Shortcut.S_MOVE);
      
	    //enter the moved folder
        dialogContactMove.zClickTreeFolder(folder);
        dialogContactMove.zClickButton(Button.B_OK);
     
        
        
        //-- Verification
        
        // Verify
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
        ZAssert.assertNotNull(actual, "Verify the contact still exists");
        ZAssert.assertEquals(actual.getFolderId(), folder.getId(), "Verify the contact exists in the addressbook folder");
        
        
   	}

	@Test(	description = "Move a contact item to sub addressbook by click Move on context menu",
			groups = { "functional" })
	public void MoveContact_03() throws HarnessException {
		


		//-- Data
		
		// Create the sub addressbook
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		String foldername = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
					"<folder name='" + foldername +"' l='"+ root.getId() +"' view='contact'/>" +
				"</CreateFolderRequest>");
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		 // Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount(), GenerateItemType.Basic);
		
	
		//-- GUI
	
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);

		 //click shortcut m
	    DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_MOVE, contact.fileAs);
      
	    //enter the moved folder
        dialogContactMove.zClickTreeFolder(folder);
        dialogContactMove.zClickButton(Button.B_OK);
     
        
        
        //-- Verification
        
        // Verify
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
        ZAssert.assertNotNull(actual, "Verify the contact still exists");
        ZAssert.assertEquals(actual.getFolderId(), folder.getId(), "Verify the contact exists in the addressbook folder");
        

   	}


	
	
	@Test(	description = "Move a contact item to trash folder by expand Move dropdown on toolbar, then select Trash",
			groups = { "functional" })
	public void MoveContact_04() throws HarnessException {
	

		//-- Data
		
        // The trash folder
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);

		 // Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount(), GenerateItemType.Basic);


		//-- GUI
		
		// Refresh to get the contact into the client
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);

		//select move option
        app.zPageAddressbook.zToolbarPressPulldown(Button.B_MOVE,folder);
       
        
        //-- Verification
        
        //verify contact deleted
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #firstname:"+ contact.firstName);
        ZAssert.assertNotNull(actual, "Verify the contact exists in the trash folder");
        ZAssert.assertEquals(actual.getFolderId(), folder.getId(), "Verify the contact is in the trash folder");
        



   	}
	
	@Test(	description = "Move a contact item to Emailed Contacts by expand Move dropdown on toolbar, then select Trash",
			groups = { "functional" })
	public void MoveContact_05() throws HarnessException {
	

		//-- Data
		
        // The Emailed Contacts folder
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);

		 // Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount(), GenerateItemType.Basic);


		//-- GUI
		
		// Refresh to get the contact into the client
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);

		//select move option
        app.zPageAddressbook.zToolbarPressPulldown(Button.B_MOVE,folder);
       
        
        //-- Verification
        
        //verify contact deleted
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #firstname:"+ contact.firstName);
        ZAssert.assertNotNull(actual, "Verify the contact exists in the trash folder");
        ZAssert.assertEquals(actual.getFolderId(), folder.getId(), "Verify the contact is in the trash folder");
        



   	}
	

	
	@Test(	
		description = "Move a contact item to sub addressbook.  Click toolbar Edit then Location",
		groups = { "functional" })
	public void MoveContact_06() throws HarnessException {
		
	
		//-- Data
		
		// Create the sub addressbook
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
		String foldername = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
					"<folder name='" + foldername +"' l='"+ root.getId() +"' view='contact'/>" +
				"</CreateFolderRequest>");
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		
		 // Create a contact via Soap then select
		ContactItem contact = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
	
		
		//-- GUI
		
		// Refresh to get the contact into the client
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);

		//Click Edit contact	
        FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
	  
        // Click Location
        DialogMove dialogContactMove = (DialogMove) formContactNew.zToolbarPressButton(Button.B_MOVE);
        
        //enter the moved folder
        dialogContactMove.zClickTreeFolder(folder);
        dialogContactMove.zClickButton(Button.B_OK);
      
        //Click Save
        formContactNew.zSubmit();
        
        
        
        //-- Verification
        
        // Verify
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
        ZAssert.assertNotNull(actual, "Verify the contact still exists");
        ZAssert.assertEquals(actual.getFolderId(), folder.getId(), "Verify the contact exists in the addressbook folder");
        
   	}

}

