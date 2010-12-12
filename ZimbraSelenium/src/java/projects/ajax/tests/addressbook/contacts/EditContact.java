package projects.ajax.tests.addressbook.contacts;
import java.util.List;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.Addressbook.FormContactNew;
import framework.items.ContactItem;
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
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccount = null;		
		
	}
	
	@Test(	description = "Edit a contact item",
			groups = { "smoke" })
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
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, app.zGetActiveAccount().getFolderByName("Contacts"));

        // Select the contact
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs);
		
		//Click Edit contact	
        FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
	    SleepUtil.sleepSmall();
	        
		ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);
							
			
        // Fill in the form
	    formContactNew.zFill(newContact);
	    
		// Save the contact
        formContactNew.zSubmit();
		
        
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();   
 	   
        
		// Verify new contact displayed	        
        ZAssert.assertContainsContactItem(contacts, contactItem, "contact "+ contactItem.fileAs +" is didplayed");

		//verify old contact not displayed
	    ZAssert.assertNotContainsContactItem(contacts, contactItem, "contact "+ contactItem.fileAs +" is not displayed");

	    
	}

}

