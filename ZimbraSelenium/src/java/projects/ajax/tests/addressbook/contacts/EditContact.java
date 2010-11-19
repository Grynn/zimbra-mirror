package projects.ajax.tests.addressbook.contacts;
import org.testng.annotations.Test;

import framework.items.ContactItem;
import framework.items.ContactItem.GenerateItemType;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;
import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.Addressbook.*;


public class EditContact extends AjaxCommonTest  {
	public EditContact() {
		logger.info("New "+ EditContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		ZimbraAccount account = new ZimbraAccount();
		account.provision();
		account.authenticate();
			
		super.startingAccount = account;		
		
	}
	
	@Test(	description = "Edit a contact item",
			groups = { "sanity" })
	public void EditContact_01() throws HarnessException {
		
		// Create a contact 
		ContactItem contactItem=CreateContact.createBasicContact(app);
				
		//Edit contact
		app.zPageAddressbook.zClick(PageAddressbook.Toolbar.EDIT);
		
		ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);
							
		//TODO new FormContactNew(app.zPageAddressBook)??????
		FormContactNew formContactNew = new FormContactNew(app);
			
        // Fill in the form
	    formContactNew.fill(newContact);
	    
		// Save the contact
        formContactNew.submit();
		
		// Verify the first/last name exists		        
		ZAssert.assertTrue(PageAddressbook.LeftPanel.isContained(newContact.firstName,newContact.lastName), "First/Last name not existed on the left panel");
		ZAssert.assertTrue(PageAddressbook.RightPanel.isContained(newContact.firstName,newContact.lastName), "First/Last name not existed on the right panel");
		
		// TODO: Verify Addressbook page displayed
	    
	}

}

