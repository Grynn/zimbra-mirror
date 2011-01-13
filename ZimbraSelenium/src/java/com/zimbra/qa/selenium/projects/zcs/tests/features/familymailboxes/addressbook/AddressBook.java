package com.zimbra.qa.selenium.projects.zcs.tests.features.familymailboxes.addressbook;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.SelNGBase;
import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.util.RetryFailedTests;
import com.zimbra.qa.selenium.projects.zcs.tests.features.familymailboxes.FamilyMailboxCommonTest;
import com.zimbra.qa.selenium.projects.zcs.ui.ActionMethod;


public class AddressBook extends FamilyMailboxCommonTest {

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="addressbook";
		super.zLogin();
	}


	@SuppressWarnings("static-access")
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createContact_parent_and_child() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		/*
		 * Create contact test for parent account.
		 */
		addChildAccount();
		obj.zButton.zClick(localize(locator.addressBook));
		zWaitTillObjectExist("xpath", createXpath(PARENT_ACCOUNT, localize(locator.addressBook)));
		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);

		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		obj.zContactListItem.zExists(contact.lastName);

		/*
		 * Create contact test for child account.
		 */
		clickAt(CHILD_ACCOUNT, localize(locator.contacts));
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		clickAt(CHILD_ACCOUNT, localize(locator.contacts));
		obj.zContactListItem.zExists(contact.lastName);

		SelNGBase.needReset.set(false);
	}
}