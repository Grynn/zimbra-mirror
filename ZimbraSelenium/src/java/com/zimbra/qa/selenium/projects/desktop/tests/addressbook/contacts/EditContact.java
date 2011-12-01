package com.zimbra.qa.selenium.projects.desktop.tests.addressbook.contacts;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.Toaster;
import com.zimbra.qa.selenium.projects.desktop.ui.addressbook.*;

public class EditContact extends AjaxCommonTest  {
	public EditContact() {
		logger.info("New "+ EditContact.class.getCanonicalName());

		// All tests start at the Address page
		super.startingPage =  app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		

	}

	private ContactItem _createSelectContactItem() throws HarnessException {
	   return _createSelectContactItem(SOAP_DESTINATION_HOST_TYPE.SERVER, null);
	}

	private ContactItem _createSelectContactItem(SOAP_DESTINATION_HOST_TYPE destType,
         String accountName) throws HarnessException {
      // Create a contact 
      ContactItem contactItem = ContactItem.generateContactItem(GenerateItemType.Basic);

      app.zGetActiveAccount().soapSend(
            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
            "<cn fileAsStr='" + contactItem.lastName + "," + contactItem.firstName + "' >" +
            "<a n='firstName'>" + contactItem.firstName +"</a>" +
            "<a n='lastName'>" + contactItem.lastName +"</a>" +
            "<a n='email'>" + contactItem.email + "</a>" +
            "</cn>" +
            "</CreateContactRequest>",
            destType,
            accountName);

      app.zGetActiveAccount().soapSelectNode("//mail:CreateContactResponse", 1);

      // Refresh the view, to pick up the new contact
      FolderItem contactFolder = FolderItem.importFromSOAP(
            app.zGetActiveAccount(),
            "Contacts",
            destType,
            accountName);
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageAddressbook.zWaitForDesktopLoadingSpinner(5000);
      app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);

      // Select the contact
      app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs);

      return contactItem;
   }

	private void _editAndVerify(FormContactNew formContactNew, ContactItem contactItem, ContactItem newContact)
	throws HarnessException {
	   _editAndVerify(formContactNew, contactItem, newContact, SOAP_DESTINATION_HOST_TYPE.SERVER, null);
	}

	private void _editAndVerify(FormContactNew formContactNew, ContactItem contactItem,
         ContactItem newContact, SOAP_DESTINATION_HOST_TYPE destType, String accountName) 
   throws HarnessException {
      //clear the form, 
      formContactNew.zReset();

      // Fill in the form
      formContactNew.zFill(newContact);
      
      // Save the contact
      formContactNew.zSubmit();

      //verify toasted message Contact Saved
      Toaster toast = app.zPageMain.zGetToaster();
      String toastMsg = toast.zGetToastMessage();
      ZAssert.assertStringContains(toastMsg, "Contact Saved", "Verify toast message 'Contact Saved'");

      //verify new contact item is displayed
      List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();   

      boolean isFileAsEqual=false;
      for (ContactItem ci : contacts) {
         logger.debug("===>" + ci.fileAs);
         if (ci.fileAs.equals(newContact.fileAs)) {
            isFileAsEqual = true;    
            break;
         }
      }

      ZAssert.assertTrue(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") existed ");

      //verify old contact not displayed
      isFileAsEqual=false;
      for (ContactItem ci : contacts) {
         if (ci.fileAs.equals(contactItem.fileAs)) {
            isFileAsEqual = true;    
            break;
         }
      }

      

      ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") deleted");

      if (destType == SOAP_DESTINATION_HOST_TYPE.SERVER) {
         GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      }

      ContactItem newImportedContact = ContactItem.importFromSOAP(
            app.zGetActiveAccount(),
            newContact.fileAs,
            destType,
            accountName);
      ZAssert.assertEquals(newImportedContact.fileAs, newContact.fileAs,
            "New imported contact has the correct file as information");
      ZAssert.assertEquals(newImportedContact.email, newContact.email,
            "New imported contact has the correct email information");

   }

   @Test(   description = "Edit a contact item, click Edit on toolbar",
   groups = { "smoke"})
   public void ClickToolbarEdit() throws HarnessException {
      ContactItem contactItem = _createSelectContactItem();

      //Click Edit contact
      FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);

      //generate the new contact
      ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);

      _editAndVerify(formContactNew, contactItem, newContact);
   }

   @Test(   description = "Edit a contact item, Right click then click Edit",
   groups = { "functional" })
   public void ClickContextMenuEdit() throws HarnessException {
      ContactItem contactItem = _createSelectContactItem();

      //Click Edit contact 
      FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, contactItem.fileAs);        

      //generate the new contact
      ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);

      _editAndVerify(formContactNew, contactItem, newContact);

   }

   @Test(   description = "Edit a local contact item, click Edit on toolbar",
   groups = { "smoke"})
   public void ClickToolbarEditLocalContact() throws HarnessException {
      ContactItem contactItem = _createSelectContactItem(SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

      //Click Edit contact 
      FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);

      //generate the new contact
      ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);

      _editAndVerify(formContactNew, contactItem, newContact, SOAP_DESTINATION_HOST_TYPE.CLIENT, ZimbraAccount.clientAccountName);     
   }

   @Test(   description = "Edit a local contact item, Right click then click Edit",
   groups = { "functional" })
   public void ClickContextMenuEditLocalContact() throws HarnessException {
      ContactItem contactItem = _createSelectContactItem(SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

      //Click Edit contact 
      FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, contactItem.fileAs);        

      //generate the new contact
      ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);

      _editAndVerify(formContactNew, contactItem, newContact, SOAP_DESTINATION_HOST_TYPE.CLIENT, ZimbraAccount.clientAccountName);

   }
}

