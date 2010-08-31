package projects.zcs.tests.addressbook.contactgroups;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
import framework.core.SelNGBase;
import framework.items.ContactGroupItem;
import framework.util.RetryFailedTests;

/**
 * This covers some high priority test cases related to address book
 * 
 * @written by Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class CreateContactGroup extends CommonTest {
	
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
	 * Test to create Contact Group and to verify
	 */
	@Test(
			description = "Test to create Contact Group and to verify",
			groups = { "sanity", "smoke", "full" },
			retryAnalyzer = RetryFailedTests.class)
	public void createContactGroupAndVerify() throws Exception {
		
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

		ContactGroupItem group = new ContactGroupItem();
		group.nickname = getLocalizedData_NoSpecialChar();
		for (int i = 1; i <= 2; i++) {
			String email = "acc" + i + "@testdomain.com";
			ProvZCS.createAccount(email);
			group.addDListMember(email);
			
		}

		page.zABCompose.createContactGroupItem(ActionMethod.DEFAULT, group);
		obj.zContactListItem.zExists(group.nickname);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test case:-Previously selected contact group details are shown when no
	 * results found in search
	 */
	@Test(
			description = "Previously selected contact group details are shown when no results found in search",
			groups = { "smoke", "full" },
			retryAnalyzer = RetryFailedTests.class)
	public void updateContactGroupPaneWhenNoResult_Bug44331() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		ContactGroupItem group = new ContactGroupItem();
		group.nickname = getLocalizedData_NoSpecialChar();
		for (int i = 1; i <= 2; i++) {
			String email = "acc" + i + "@testdomain.com";
			ProvZCS.createAccount(email);
			group.addDListMember(email);
			
		}
		
		page.zABCompose.createContactGroupItem(ActionMethod.DEFAULT, group);
		obj.zContactListItem.zExists(group.nickname);
		

		SelNGBase.selenium.get().type("xpath=//input[@class='search_input']", "abc");
		obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
		obj.zContactListItem.zNotExists(group.nickname);
		
		Assert.assertFalse(
				SelNGBase.selenium.get().isElementPresent("xpath=//div[contains(@class,'contactHeader') and contains(text(),'" + group.nickname + "')]"),
				"Verify that the group does not display if no search results are found");

		SelNGBase.needReset.set(false);
	}

}