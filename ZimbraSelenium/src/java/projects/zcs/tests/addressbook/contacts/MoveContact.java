package projects.zcs.tests.addressbook.contacts;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import projects.zcs.Locators;
import projects.zcs.PageObjects;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
import framework.core.SelNGBase;
import framework.items.ContactItem;
import framework.items.FolderItem;
import framework.items.ContactItem.GenerateItemType;
import framework.util.RetryFailedTests;

public class MoveContact extends CommonTest {
	

	private FolderItem EmailedContacts;
	
	public MoveContact() {
		
		EmailedContacts = new FolderItem();
		EmailedContacts.name = localize(Locators.emailedContacts);
		
	}
	
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------



	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="address book";
		super.zLogin();
	}
	
	/**
	 * Creates a contact with basic fields,deletes the contact and verifies the
	 * contact gets moved to target AB folder using tool bar move button
	 */
	@Test(
			description = "Creates a contact with basic fields,deletes the contact and verifies the contact gets moved to target AB folder using tool bar move button",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void moveContactAndVerify() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("all", "na", "43526", "EMailed Contacts & Trash folder is not visible in move contact dialog");
		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);
		contact.AddressBook = EmailedContacts;
		
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		if (obj.zContactListItem.zExistsDontWait(contact.lastName).equals("true")) {
			page.zABApp.zMoveContactAndVerify(contact.lastName, EmailedContacts.name, "ToolbarMove");
		}

		SelNGBase.needReset.set(false);
	}

	/**
	 * Creates a contact with basic fields,deletes the contact and verifies the
	 * contact gets moved to target AB folder using tool bar move button
	 */
	@Test(
			description = "Creates a contact with basic fields,deletes the contact and verifies the contact gets moved to target AB folder using tool bar move button",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void rghtClickMoveContactAndVerify()
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("all", "na", "43526", "EMailed Contacts & Trash folder is not visible in move contact dialog");
		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");
		
		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);

		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		if (obj.zContactListItem.zExistsDontWait(contact.lastName).equals("true")) {
			page.zABApp.zMoveContactAndVerify(contact.lastName, EmailedContacts.name,
					"RightClickMove");
		}

		SelNGBase.needReset.set(false);
	}
}