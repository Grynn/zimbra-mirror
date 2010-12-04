package projects.ajax.tests.addressbook.contacts;
import org.testng.annotations.Test;


import framework.items.*;
import framework.ui.*;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;
import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.Addressbook.*;

import framework.items.ContactItem.GenerateItemType;
import java.util.*;

public class DeleteContact extends AjaxCommonTest  {
	public DeleteContact() {
		logger.info("New "+ DeleteContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		ZimbraAccount account = new ZimbraAccount();
		account.provision();
		account.authenticate();
			
		super.startingAccount = account;		
		
	}
	
	@Test(	description = "Delete a contact item",
			groups = { "smoke" })
	public void DeleteContact_01() throws HarnessException {

		 // Create a contact 
		ContactItem contactItem = ContactItem.generateContactItem(GenerateItemType.Basic);
 
        app.getActiveAccount().soapSend(
                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
                "<cn fileAsStr='" + contactItem.lastName + "," + contactItem.firstName + "' >" +
                "<a n='firstName'>" + contactItem.firstName +"</a>" +
                "<a n='lastName'>" + contactItem.lastName +"</a>" +
                "<a n='email'>" + contactItem.email + "</a>" +
                "</cn>" +
                "</CreateContactRequest>");

        app.getActiveAccount().soapSelectNode("//mail:CreateContactResponse", 1);

        // Select the item
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs);


        //delete contact
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
        SleepUtil.sleepSmall();
        

        //verify deleted contact not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();   
        ZAssert.assertNotContainsContactItem(contacts, contactItem, "Verify contact "+ contactItem.firstName +" is deleted");

   	}

}
