package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;


public class DeleteContact extends AjaxCommonTest  {
	public DeleteContact() {
		logger.info("New "+ DeleteContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	@Test(	description = "Delete a contact item",
			groups = { "smoke" })
	public void DeleteContact_01() throws HarnessException {

		 // Create a contact 
		ContactItem contactItem = ContactItem.generateContactItem(GenerateItemType.Basic);
 
        app.zGetActiveAccount().soapSend(
                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
                "<cn fileAsStr='" + contactItem.lastName + "," + contactItem.firstName + "' >" +
                "<a n='firstName'>" + contactItem.firstName +"</a>" +
                "<a n='lastName'>" + contactItem.lastName +"</a>" +
                "<a n='email'>" + contactItem.email + "</a>" +
                "</cn>" +
                "</CreateContactRequest>");

        app.zGetActiveAccount().soapSelectNode("//mail:CreateContactResponse", 1);
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        app.zPageAddressbook.zSyncDesktopToZcs();
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        // Select the item
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs);


        //delete contact
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
       
        
        //verify toasted message 1 contact moved to Trash
        Toaster toast = app.zPageMain.zGetToaster();
        String toastMsg = toast.zGetToastMessage();
        ZAssert.assertStringContains(toastMsg, "1 contact moved to Trash", "Verify toast message '1 contact moved to Trash'");

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

}
