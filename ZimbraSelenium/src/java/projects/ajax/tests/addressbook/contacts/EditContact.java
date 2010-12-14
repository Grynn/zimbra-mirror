package projects.ajax.tests.addressbook.contacts;
import java.util.List;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.addressbook.*;
import framework.items.ContactItem;
import framework.items.FolderItem;
import framework.items.ContactItem.GenerateItemType;
import framework.ui.Action;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZAssert;


public class EditContact extends AjaxCommonTest  {
	public EditContact() {
		logger.info("New "+ EditContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage =  app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccount = null;		
		
	}
	
	@Test(	description = "Edit a contact item",
			groups = { "smoke"})
	public void EditContact_01() throws HarnessException {
		
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

        // Select the contact
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs);
		
		//Click Edit contact	
        FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
	    SleepUtil.sleepSmall();
	        
		ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);
							
		
		//clear the form, 
		formContactNew.zReset();
		
        // Fill in the form
	    formContactNew.zFill(newContact);
	    
		// Save the contact
        formContactNew.zSubmit();
		
        
        //verify toasted message Contact Saved
        ZAssert.assertStringContains(app.zPageAddressbook.sGetText("xpath=//div[@id='z_toast_text']"), "Contact Saved", "Verify toast message 'Contact Saved'");

        //verify new contact item is displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();   
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
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
      
        	    
	}

}

