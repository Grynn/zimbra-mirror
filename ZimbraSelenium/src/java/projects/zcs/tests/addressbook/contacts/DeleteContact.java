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

public class DeleteContact extends CommonTest {
	

	private FolderItem EmailedContacts;
	
	public DeleteContact() {
		
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
	private void zLogin() throws Exception {
		zLoginIfRequired();
		PageObjects.zABCompose.navigateTo(ActionMethod.DEFAULT);
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	/**
	 * Creates a contact with basic fields,deletes the contact and verifies the
	 * contact does not exist after ToolBar delete
	 */
	@Test(
			description = "Creates a contact with basic fields,deletes the contact and verifies the contact does not exist after ToolBar delete",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void deleteContactAndVerify() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);

		// Create the contact
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		
		if (obj.zContactListItem.zExistsDontWait(contact.lastName).equals("true")) {
			page.zABApp.zDeleteContactAndVerify(contact.lastName, "ToolbarDelete");
			obj.zContactListItem.zNotExists(contact.lastName);
		}


		SelNGBase.needReset.set(false);
	}

	/**
	 * Creates a contact with basic fields,deletes the contact and verifies the
	 * contact does not exist after Right Click delete
	 */
	@Test(
			description = "Creates a contact with basic fields,deletes the contact and verifies the contact does not exist after Right Click delete",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void rghtClkDeleteContactAndVerify() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);

		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		if (obj.zContactListItem.zExistsDontWait(contact.lastName).equals("true")) {
			page.zABApp.zDeleteContactAndVerify(contact.lastName, "RightClickDelete");
		}

		SelNGBase.needReset.set(false);
	}


	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);// reset this to false
		zLogin();
	}

}