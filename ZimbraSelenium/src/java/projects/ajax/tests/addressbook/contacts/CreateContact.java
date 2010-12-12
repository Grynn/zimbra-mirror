package projects.ajax.tests.addressbook.contacts;

import java.util.List;

import org.testng.annotations.Test;

import framework.items.*;
import framework.items.ContactItem.GenerateItemType;
import framework.ui.Button;
import framework.util.*;
import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.AppAjaxClient;
import projects.ajax.ui.addressbook.*;

//TODO: add more in ContactItem.java

public class CreateContact extends AjaxCommonTest  {

	public CreateContact() {
		logger.info("New "+ CreateContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccount = null;		
		
	}
	
	
	@Test(	description = "Create a basic contact item",
			groups = { "sanity" })
	public void CreateContact_01() throws HarnessException {				
		createBasicContact(app);		
	}

	//used for other class such as DeleteContact, MoveContact
	public static ContactItem createBasicContact(AppAjaxClient app)throws HarnessException {
		// Create a contact Item
		ContactItem contactItem = ContactItem.generateContactItem(GenerateItemType.Basic);
			
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);
				
			
		//verify form contact new page is displayed
		ZAssert.assertTrue(app.zPageAddressbook.sIsElementPresent("xpath=//div[@id='editcontactform']"),"new contact form not displayed");
		
        // Fill in the form
	    formContactNew.zFill(contactItem);
	    
		// Save the contact
        formContactNew.save();
		
        SleepUtil.sleepMedium();
		  
        //verify contact "file as" is displayed
		List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contactItem.fileAs)) {
	            isFileAsEqual = true;	
				break;
			}
		}
		
        ZAssert.assertTrue(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") existed ");

		return contactItem;
	}
}
