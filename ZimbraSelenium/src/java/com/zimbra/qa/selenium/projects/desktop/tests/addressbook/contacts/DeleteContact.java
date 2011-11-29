package com.zimbra.qa.selenium.projects.desktop.tests.addressbook.contacts;


import java.awt.event.KeyEvent;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;


public class DeleteContact extends AjaxCommonTest  {
	public DeleteContact() {
		logger.info("New "+ DeleteContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}

   @Test(   description = "Delete a contact item through toolbar",
         groups = { "smoke" })
   public void DeleteContactByClickDeleteOnToolbar() throws HarnessException {

      // Create a contact via soap 
      ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
 
      //delete contact
      app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageAddressbook.zWaitForDesktopLoadingSpinner(5000);

      //verify contact deleted
      _verifyContactDeleted(contactItem);    
   }

   @Test(   description = "Delete a contact item selected with checkbox",
         groups = { "functional" })
   public void DeleteContactSelectedWithCheckbox() throws HarnessException {

      // Create a contact via soap 
      ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_CHECKBOX);

      //delete contact
      app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageAddressbook.zWaitForDesktopLoadingSpinner(5000);

      //verify contact deleted
      _verifyContactDeleted(contactItem);       
   }

   @Test(   description = "Delete a contact item using keyboard short cut Del",
         groups = { "functional" })
   public void DeleteContactUseShortcutDel() throws HarnessException {

      // Create a contact via soap 
      ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
 
      //delete contact
      app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_DELETE);

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageAddressbook.zWaitForDesktopLoadingSpinner(5000);

      //verify contact deleted
      _verifyContactDeleted(contactItem);    
   }
   
   @Test(   description = "Delete a contact item using keyboard short cut backspace",
         groups = { "functional" })
   public void DeleteContactUseShortcutBackspace() throws HarnessException {

      // Create a contact via soap 
      ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
 
      //delete contact
      app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_BACK_SPACE);

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageAddressbook.zWaitForDesktopLoadingSpinner(5000);

      //verify contact deleted
      _verifyContactDeleted(contactItem);    
   }
   

   @Test(   description = "Right click then click delete",
         groups = { "smoke" })
   public void DeleteFromContextMenu() throws HarnessException {
      
      // Create a contact via soap 
      ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
 
      //select delete option
      app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE, contactItem.fileAs);

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageAddressbook.zWaitForDesktopLoadingSpinner(5000);

      //verify contact deleted
      _verifyContactDeleted(contactItem);    
                 
   }

   @Test(   description = "Delete multiple contact items",
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

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageAddressbook.zWaitForDesktopLoadingSpinner(5000);

      FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(),
            SystemFolder.Trash);

      // Verify document moved to Trash
      app.zGetActiveAccount()
            .soapSend("<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
                  + "<query>in:"
                  + trash.getName()
                  + " "
                  + contactItem1.fileAs
                  + "</query>" + "</SearchRequest>");

      String parentId = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:cn",
            "l");
      ZAssert.assertEquals(trash.getId(), parentId, "New parent ID of the contact item 1 matches");

      app.zGetActiveAccount()
      .soapSend("<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
            + "<query>in:"
            + trash.getName()
            + " "
            + contactItem2.fileAs
            + "</query>" + "</SearchRequest>");

      parentId = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:cn",
            "l");
      ZAssert.assertEquals(trash.getId(), parentId, "New parent ID of the contact item 2 matches");

      app.zGetActiveAccount()
      .soapSend("<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
            + "<query>in:"
            + trash.getName()
            + " "
            + contactItem3.fileAs
            + "</query>" + "</SearchRequest>");

      parentId = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:cn",
            "l");
      ZAssert.assertEquals(trash.getId(), parentId, "New parent ID of the contact item 3 matches");
   }

   private void _verifyContactDeleted(ContactItem contactItem) throws HarnessException {
      _verifyContactDeleted(contactItem, SOAP_DESTINATION_HOST_TYPE.SERVER, null);
   }

   private void _verifyContactDeleted(ContactItem contactItem,
         SOAP_DESTINATION_HOST_TYPE destType,
         String accountName) throws HarnessException {
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

      FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(),
            SystemFolder.Trash,
            destType,
            accountName);

      //verify deleted contact displayed in trash folder
      // refresh Trash folder
      app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, trash);

      contacts = app.zPageAddressbook.zListGetContacts(); 

      isFileAsEqual=false;
      for (ContactItem ci : contacts) {
         if (ci.fileAs.equals(contactItem.fileAs)) {
            isFileAsEqual = true;    
            break;
         }
      }

      ZAssert.assertTrue(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") displayed in Trash folder");

      // Verify document moved to Trash
      app.zGetActiveAccount()
            .soapSend("<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
                  + "<query>in:"
                  + trash.getName()
                  + " "
                  + contactItem.fileAs
                  + "</query>" + "</SearchRequest>",
                  destType,
                  accountName);

      String parentId = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:cn",
            "l");
      ZAssert.assertEquals(trash.getId(), parentId, "New parent ID of the contact item matches");
   }

   @Test(   description = "Delete a local contact item through toolbar",
         groups = { "smoke" })
   public void DeleteLocalContactByClickDeleteOnToolbar() throws HarnessException {

      // Create a contact via soap 
      ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectLocalContact(
            app, Action.A_LEFTCLICK);

      //delete contact
      app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

      //verify contact deleted
      _verifyContactDeleted(contactItem, SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);
   }

   @Test(   description = "Delete a local contact item selected with checkbox",
         groups = { "functional" })
   public void DeleteLocalContactSelectedWithCheckbox() throws HarnessException {

      // Create a contact via soap 
      ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectLocalContact(
            app, Action.A_CHECKBOX);

      //delete contact
      app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

      //verify contact deleted
      _verifyContactDeleted(contactItem, SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);
   }

   @Test(   description = "Delete a local contact item using keyboard short cut Del",
         groups = { "functional" })
   public void DeleteLocalContactUseShortcutDel() throws HarnessException {

      // Create a contact via soap 
      ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectLocalContact(
            app, Action.A_LEFTCLICK);
 
      //delete contact
      app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_DELETE);
       
      //verify contact deleted
      _verifyContactDeleted(contactItem, SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);    
   }

   @Test(   description = "Delete a local contact item using keyboard short cut backspace",
         groups = { "functional" })
   public void DeleteLocalContactUseShortcutBackspace() throws HarnessException {

      // Create a contact via soap 
      ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectLocalContact(
            app, Action.A_LEFTCLICK);
 
      //delete contact
      app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_BACK_SPACE);
       
      //verify contact deleted
      _verifyContactDeleted(contactItem, SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);    
   }

   @Test(   description = "Right click on local contact then click delete",
         groups = { "smoke" })
   public void DeleteLocalContactFromContextMenu() throws HarnessException {
      
      // Create a contact via soap 
      ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectLocalContact(
            app, Action.A_LEFTCLICK);
 
      //select delete option
      app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK,
            Button.B_DELETE,
            contactItem.fileAs);

      //verify contact deleted
      _verifyContactDeleted(contactItem, SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);    
   }

   @Test(   description = "Delete multiple local contact items",
         groups = { "functional" })
   public void DeleteMultipleLocalContacts() throws HarnessException {

      // Create a contact via Soap
      ContactItem contactItem1 = ContactItem.createLocalUsingSOAP(app, ZimbraAccount.clientAccountName);                      
      contactItem1.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));

      // Create a contact via Soap
      ContactItem contactItem2 = ContactItem.createLocalUsingSOAP(app, ZimbraAccount.clientAccountName);                      
      contactItem2.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));

      // Create a contact via Soap
      ContactItem contactItem3 = ContactItem.createLocalUsingSOAP(app, ZimbraAccount.clientAccountName);                      
      contactItem3.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));

      // Refresh the view, to pick up the new contact
      FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(),
            SystemFolder.Contacts,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

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

      SOAP_DESTINATION_HOST_TYPE destType = SOAP_DESTINATION_HOST_TYPE.CLIENT;
      String accountName = ZimbraAccount.clientAccountName;

      FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(),
            SystemFolder.Trash,
            destType,
            accountName);

      // Verify document moved to Trash
      app.zGetActiveAccount()
            .soapSend("<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
                  + "<query>in:"
                  + trash.getName()
                  + " "
                  + contactItem1.fileAs
                  + "</query>" + "</SearchRequest>",
                  destType,
                  accountName);

      String parentId = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:cn",
            "l");
      ZAssert.assertEquals(trash.getId(), parentId, "New parent ID of the contact item 1 matches");

      app.zGetActiveAccount()
      .soapSend("<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
            + "<query>in:"
            + trash.getName()
            + " "
            + contactItem2.fileAs
            + "</query>" + "</SearchRequest>",
            destType,
            accountName);

      parentId = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:cn",
            "l");
      ZAssert.assertEquals(trash.getId(), parentId, "New parent ID of the contact item 2 matches");

      app.zGetActiveAccount()
      .soapSend("<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
            + "<query>in:"
            + trash.getName()
            + " "
            + contactItem3.fileAs
            + "</query>" + "</SearchRequest>",
            destType,
            accountName);

      parentId = app.zGetActiveAccount().soapSelectValue("//mail:SearchResponse//mail:cn",
            "l");
      ZAssert.assertEquals(trash.getId(), parentId, "New parent ID of the contact item 3 matches");
   }

}
