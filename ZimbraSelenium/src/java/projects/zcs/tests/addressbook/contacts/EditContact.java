package projects.zcs.tests.addressbook.contacts;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import projects.zcs.Locators;
import projects.zcs.PageObjects;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
import projects.zcs.ui.ABCompose.ABComposeActionMethod;
import framework.core.SelNGBase;
import framework.items.ContactItem;
import framework.items.FolderItem;
import framework.items.ContactItem.GenerateItemType;
import framework.util.RetryFailedTests;

public class EditContact extends CommonTest {
	

	private FolderItem EmailedContacts;
	
	public EditContact() {
		
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


	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);// reset this to false
		zLogin();
	}

}