package com.zimbra.qa.selenium.projects.zcs.tests.addressbook.contacts;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.util.RetryFailedTests;
import com.zimbra.qa.selenium.projects.zcs.Locators;
import com.zimbra.qa.selenium.projects.zcs.tests.CommonTest;
import com.zimbra.qa.selenium.projects.zcs.ui.ActionMethod;


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
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="address book";
		super.zLogin();
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

}
