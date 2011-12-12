package com.zimbra.qa.selenium.projects.desktop.tests.addressbook.contacts;


import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.*;


public class MoveContact extends AjaxCommonTest  {
	public MoveContact() {
		logger.info("New "+ MoveContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
   private void MoveAndVerify(FolderItem folder, ContactItem contactItem, DialogMove dialogContactMove) throws HarnessException {
      
      //enter the moved folder
       dialogContactMove.zClickTreeFolder(folder);
       dialogContactMove.zClickButton(Button.B_OK);
      
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

  @Test(   description = "Move a contact item to folder Emailed Contacts by click shortcut m",
        groups = { "functional" })
  public void MoveToEmailedContactsClickShortcutm() throws HarnessException {
     
      // Create a contact via Soap then select
     ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
  
  
     FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);

     
      //click shortcut m
      DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zKeyboardShortcut(Shortcut.S_MOVE);
     
       //Move contact and verify
       MoveAndVerify(emailedContacts,contactItem,dialogContactMove);
       
     }

  @Test(   description = "Move a contact item to folder Emailed Contacts  by click Move on context menu",
        groups = { "functional" })
  public void MoveToEmailedContactsClickMoveOnContextmenu() throws HarnessException {
     
      // Create a contact via Soap then select
     ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
  
  
     FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);

     
      //click Move icon on context menu
      DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_MOVE, contactItem.fileAs);
   
       //Move contact and verify
       MoveAndVerify(emailedContacts,contactItem,dialogContactMove);
       
     }

  @Test(   description = "Move a contact item to folder Emailed Contacts by click tool bar Move",
        groups = { "smoke" })
  public void MoveToEmailedContactsClickMoveOnToolbar() throws HarnessException {
     
      // Create a contact via Soap then select
     ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
  
  
     FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
     
      //click Move icon on toolbar
       DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zToolbarPressButton(Button.B_MOVE);
   
       //Move contact and verify
       MoveAndVerify(emailedContacts,contactItem,dialogContactMove);
       
     }
  
  @Test(   description = "Move a contact item to trash folder by click tool bar Move",
        groups = { "functional" })
  public void MoveToTrashClickMoveOnToolbar() throws HarnessException {
     
      // Create a contact via Soap then select
     ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
  
  
     FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
     
      //click Move icon on toolbar
       DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zToolbarPressButton(Button.B_MOVE);
   
       //Move contact and verify
       MoveAndVerify(folder,contactItem,dialogContactMove);
       
     }

   @Test(   description = "Move a contact item to folder Emailed Contacts by click shortcut m",
         groups = { "functional" })
   public void LocalMoveToEmailedContactsClickShortcutm() throws HarnessException {
     
      // Create a contact via Soap then select
      ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectLocalContact(
            app,
            Action.A_LEFTCLICK);

      FolderItem emailedContacts = FolderItem.importFromSOAP(
            app.zGetActiveAccount(),
            SystemFolder.EmailedContacts,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

      //click shortcut m
      DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zKeyboardShortcut(
            Shortcut.S_MOVE);

      //Move contact and verify
      MoveAndVerify(emailedContacts, contactItem, dialogContactMove);

   }

   @Test(   description = "Move a contact item to folder Emailed Contacts  by click Move on context menu",
         groups = { "functional" })
   public void LocalMoveToEmailedContactsClickMoveOnContextmenu() throws HarnessException {
     
      // Create a contact via Soap then select
      ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectLocalContact(
            app,
            Action.A_LEFTCLICK);

      FolderItem emailedContacts = FolderItem.importFromSOAP(
            app.zGetActiveAccount(),
            SystemFolder.EmailedContacts,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

      //click Move icon on context menu
      DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zListItem(
            Action.A_RIGHTCLICK,
            Button.B_MOVE,
            contactItem.fileAs);

      //Move contact and verify
      MoveAndVerify(emailedContacts,
            contactItem,
            dialogContactMove);

   }

   @Test(   description = "Move a contact item to folder Emailed Contacts by click tool bar Move",
         groups = { "smoke" })
   public void LocalMoveToEmailedContactsClickMoveOnToolbar() throws HarnessException {

      // Create a contact via Soap then select
      ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectLocalContact(
            app,
            Action.A_LEFTCLICK);

      FolderItem emailedContacts = FolderItem.importFromSOAP(
            app.zGetActiveAccount(),
            SystemFolder.EmailedContacts,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

      //click Move icon on toolbar
      DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zToolbarPressButton(
            Button.B_MOVE);

      //Move contact and verify
      MoveAndVerify(emailedContacts,contactItem, dialogContactMove);

   }

   @Test(   description = "Move a contact item to trash folder by click tool bar Move",
         groups = { "functional" })
   public void LocalMoveToTrashClickMoveOnToolbar() throws HarnessException {

      // Create a contact via Soap then select
      ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectLocalContact(
            app,
            Action.A_LEFTCLICK);

      FolderItem folder = FolderItem.importFromSOAP(
            app.zGetActiveAccount(),
            SystemFolder.Trash,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

      //click Move icon on toolbar
      DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zToolbarPressButton(
            Button.B_MOVE);

      //Move contact and verify
      MoveAndVerify(folder, contactItem, dialogContactMove);

   }

}

