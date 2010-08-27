package projects.zcs.tests.addressbook.contacts;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.items.ContactItem;
import framework.items.ContactItem.GenerateItemType;
import framework.util.RetryFailedTests;
import projects.zcs.Locators;
import projects.zcs.PageObjects;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;

public class ContactContextMenu extends CommonTest {

	private String itemsEnabled = null;
	private String itemsDisabled = null;


	public ContactContextMenu() {
		
		itemsEnabled = localize(Locators.AB_EDIT_CONTACT) + ","
		+ localize(Locators.AB_TAG_CONTACT) + ","
		+ localize(Locators.del) + "," 
		+ localize(Locators.move) + ","
		+ localize(Locators.print) + "," 
		+ localize(Locators.search) + ","
		+ localize(Locators.advancedSearch) + ","
		+ localize(Locators.newEmail);

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
	 *Test to verify the contact right click menus exits and are enabled or not
	 */
	@Test(
			description = "Test to verify the contact right click menus exits and are enabled or not",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
			public void rtClickContactAndVerify() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);

		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		obj.zContactListItem.zRtClick(contact.lastName);
		page.zABApp.zVerifyAllMenuItems(itemsEnabled, itemsDisabled, "false");

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
