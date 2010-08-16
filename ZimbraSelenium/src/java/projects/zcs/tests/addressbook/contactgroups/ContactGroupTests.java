package projects.zcs.tests.addressbook.contactgroups;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
import framework.items.ContactGroupItem;
import framework.util.RetryFailedTests;

/**
 * This covers some high priority test cases related to address book
 * 
 * @written by Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class ContactGroupTests extends CommonTest {
	
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
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Test to create Contact Group and to verify
	 */
	@Test(
			description = "Test to create Contact Group and to verify",
			groups = { "smoke", "full" },
			retryAnalyzer = RetryFailedTests.class)
	public void createContactGroupAndVerify() throws Exception {
		
		if (isExecutionARetry)
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

		needReset = false;
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

		if (isExecutionARetry)
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
		

		selenium.type("xpath=//input[@class='search_input']", "abc");
		obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
		obj.zContactListItem.zNotExists(group.nickname);
		
		Assert.assertFalse(
				selenium.isElementPresent("xpath=//div[contains(@class,'contactHeader') and contains(text(),'" + group.nickname + "')]"),
				"Verify that the group does not display if no search results are found");

		needReset = false;
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		isExecutionARetry = false;// reset this to false
		zLogin();
	}
}