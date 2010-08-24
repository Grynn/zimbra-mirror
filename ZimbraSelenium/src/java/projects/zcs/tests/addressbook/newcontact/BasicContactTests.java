package projects.zcs.tests.addressbook.newcontact;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
import projects.zcs.ui.ABCompose.ABComposeActionMethod;
import framework.core.SelNGBase;
import framework.items.ContactItem;
import framework.items.FolderItem;
import framework.items.ContactItem.GenerateItemType;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;



/**
 * This covers some high priority test cases related to address book
 * 
 * @written by Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class BasicContactTests extends CommonTest {
	
	private String itemsEnabled = null;
	private String itemsDisabled = null;

	private FolderItem EmailedContacts;
	
	public BasicContactTests() {
		
		EmailedContacts = new FolderItem();
		EmailedContacts.name = localize(locator.emailedContacts);
		
		itemsEnabled = localize(locator.AB_EDIT_CONTACT) + ","
						+ localize(locator.AB_TAG_CONTACT) + ","
						+ localize(locator.del) + "," 
						+ localize(locator.move) + ","
						+ localize(locator.print) + "," 
						+ localize(locator.search) + ","
						+ localize(locator.advancedSearch) + ","
						+ localize(locator.newEmail);

		itemsDisabled = "";
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
		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
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
	 * Enters some basic fields to create a contact in address book and verifies
	 * the contact exist or not
	 */
	@Test(
			description = "Enters some basic fields to create a contact in address book and verifies the contact exist or not",
			groups = { "sanity", "smoke", "full" },
			retryAnalyzer = RetryFailedTests.class)
	public void createBasicContact() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);

		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		obj.zContactListItem.zExists(contact.lastName);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Creates a contact with basic fields,edits the contacts using the ToolBar
	 * delete last name and verifies the change
	 */
	@Test(
			description = "Creates a contact with basic fields,edits the contacts using the ToolBar delete last name and verifies the change",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void editNameAndVerify() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		ContactItem oldContact = ContactItem.generateContactItem(GenerateItemType.Basic);
		
		ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);
		

		page.zABCompose.createItem(ActionMethod.DEFAULT, oldContact);
		page.zABCompose.modifyItem(ABComposeActionMethod.ToolbarEdit, oldContact, newContact);

		Assert.assertTrue(
				page.zABCompose.zVerifyEditContact(newContact),
				"Verify the contact fields match the correct values");
		
		SelNGBase.needReset.set(false);

	}

	/**
	 * Creates a contact with basic fields,edits the contacts using Right Click
	 * last name and verifies the change
	 */
	@Test(
			description = "Creates a contact with basic fields,edits the contacts using Right Click last name and verifies the change",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void rghtClickEditNameAndVerify() throws Exception {
		
		
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		
		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);

		ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);
		
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		page.zABCompose.modifyItem(ABComposeActionMethod.RightClickEdit, contact, newContact);
		
		Assert.assertTrue(
				page.zABCompose.zVerifyEditContact(newContact),
				"Verify the contact fields match the correct values");

		SelNGBase.needReset.set(false);
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
	 * contact gets moved to target AB folder using tool bar move button
	 */
	@Test(
			description = "Creates a contact with basic fields,deletes the contact and verifies the contact gets moved to target AB folder using tool bar move button",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void moveContactAndVerify() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

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

		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);

		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		if (obj.zContactListItem.zExistsDontWait(contact.lastName).equals("true")) {
			page.zABApp.zMoveContactAndVerify(contact.lastName, EmailedContacts.name,
					"RightClickMove");
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

		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);

		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		if (obj.zContactListItem.zExistsDontWait(contact.lastName).equals("true")) {
			page.zABApp.zDeleteContactAndVerify(contact.lastName, "RightClickDelete");
		}

		SelNGBase.needReset.set(false);
	}

	/**
	 *Test to verify the contact right click menus exits and are enabled or not
	 */
	@Test(
			description = "Test to verify the contact right click menus exits and are enabled or not",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void rtClickContactAndVerify() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);

		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		obj.zContactListItem.zRtClick(contact.lastName);
		page.zABApp.zVerifyAllMenuItems(itemsEnabled, itemsDisabled, "false");

		SelNGBase.needReset.set(false);
	}

	/**
	 *Test to verify the contact right click menus exits and are enabled or not
	 */
	@Test(
			description = "Test to verify the contact right click menus exits and are enabled or not",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void negativeTestCreateContact() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);
		
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zABCompose.zContactsFolder));
		obj.zButton.zClick(page.zABCompose.zNewContactMenuIconBtn);
		page.zABCompose.zEnterBasicABData(contact);
		obj.zButton.zClick(page.zABCompose.zCancelContactMenuIconBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no), localize(locator.warningMsg));
		SleepUtil.sleep(500);
		obj.zContactListItem.zNotExists(contact.lastName);

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