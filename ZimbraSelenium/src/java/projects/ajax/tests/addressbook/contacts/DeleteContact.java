package projects.ajax.tests.addressbook.contacts;


import java.util.List;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import framework.items.ContactItem;
import framework.items.FolderItem;
import framework.items.ContactItem.GenerateItemType;
import framework.ui.Action;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZAssert;

public class DeleteContact extends AjaxCommonTest  {
	public DeleteContact() {
		logger.info("New "+ DeleteContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccount = null;		
		
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
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        // Select the item
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs);


        //delete contact
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
        SleepUtil.sleepSmall();
        
        //verify toasted message 1 contact moved to Trash
        ZAssert.assertStringContains(app.zPageAddressbook.sGetText("xpath=//div[@id='z_toast_text']"), "1 contact moved to Trash", "Verify toast message '1 contact moved to Trash'");

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
