package projects.ajax.tests.addressbook.contacts;

import java.util.List;

import org.testng.annotations.Test;

import framework.items.*;
import framework.items.ContactItem.GenerateItemType;
import framework.ui.Actions;
import framework.ui.Buttons;
import framework.util.*;
import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.AppAjaxClient;
import projects.ajax.ui.Addressbook.*;

//TODO: add more in ContactItem.java

public class CreateContact extends AjaxCommonTest  {

	public CreateContact() {
		logger.info("New "+ CreateContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		ZimbraAccount account = new ZimbraAccount();
		account.provision();
		account.authenticate();
			
		super.startingAccount = account;		
		
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

			
		app.zPageAddressbook.zClick(PageAddressbook.NewDropDown.NEW);
		
		//TODO new FormContactNew(app.zPageAddressBook)
		FormContactNew formContactNew = new FormContactNew(app);
			
		//TODO: verify formcontactnew page is displayed
		
        // Fill in the form
	    formContactNew.fill(contactItem);
	    
		// Save the contact
        formContactNew.save();
		
        SleepUtil.sleepMedium();
        String firstName =  contactItem.firstName;
        String lastName  =  contactItem.lastName;
        
    	// Verify Addressbook page displayed

        // Verify the first/last name exists		        
		ZAssert.assertTrue(PageAddressbook.LeftPanel.isContained(firstName,lastName), "First/Last name not existed on the left panel");
		ZAssert.assertTrue(PageAddressbook.RightPanel.isContained(firstName,lastName), "First/Last name not existed on the right panel");
		
		return contactItem;
	}
}
