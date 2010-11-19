package projects.mobile.tests.addressbook.contacts;

import java.util.List;

import org.testng.annotations.Test;

import projects.mobile.core.MobileCommonTest;
import projects.mobile.ui.FormContactNew;
import framework.items.ContactItem;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraSeleniumProperties;

public class CreateContact extends MobileCommonTest {

	public CreateContact() {
		logger.info("New "+ CreateContact.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageContacts;
		super.startingAccount = null;
		
	}
	
	@Test(	description = "Create a new contact",
			groups = { "sanity" })
	public void CreateContact_01() throws HarnessException, InterruptedException {

		// Define a new contact
		ContactItem contact = new ContactItem();
		contact.lastName = "last" + ZimbraSeleniumProperties.getUniqueString();
		contact.firstName = "first" + ZimbraSeleniumProperties.getUniqueString();
		contact.email = "email" + ZimbraSeleniumProperties.getUniqueString() + "@example.com";

		// Get the "new contact" page and fill it out (automatically)
		FormContactNew form = (FormContactNew) app.zPageContacts.zToolbarPressButton(Button.B_NEW);
		form.fill(contact);
		form.submit();
		
		// Reset the contacts list
		app.zPageContacts.refresh();
		
		// Get the list of contacts
		List<ContactItem> contacts = app.zPageContacts.getContactList();

		// Verify that the sent mail is in the list
		boolean found = false;
		for (ContactItem c : contacts) {
			if ( c.email.equals(contact.email)) {
				found = true;		// Found the message!
				break;
			}
		}
		
		ZAssert.assertTrue(found, "Verify that the newly created contact is in the contacts folder");

		
	}

}
