package projects.zcs.tests.addressbook.contactgroups;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
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
	@DataProvider(name = "ABDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("createContactGroupAndVerify")
				|| test.equals("updateContactGroupPaneWhenNoResult_Bug44331")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar() } };
		} else {

			return new Object[][] { { "" } };
		}
	}

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
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createContactGroupAndVerify(String groupName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		obj.zButtonMenu.zClick(page.zABCompose.zNewMenuDropdownIconBtn);

		obj.zMenuItem.zClick(localize(locator.group));
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.groupNameLabel)),
				groupName);
		for (int i = 1; i <= 2; i++) {
			ProvZCS.createAccount("acc" + i + "@testdomain.com");
			obj.zEditField.zType(localize(locator.findLabel), "acc" + i
					+ "@testdomain.com");
			obj.zButton.zClick(localize(locator.search), "2");
			Thread.sleep(2500);
			if (currentBrowserName.contains("Safari")) {
				obj.zButton.zClick(localize(locator.search), "2");
				obj.zButton.zClick(localize(locator.search), "2");
				Thread.sleep(1000);
			}

			obj.zListItem.zDblClickItemInSpecificList("acc" + i
					+ "@testdomain.com", "2");

			obj.zButton.zClick(localize(locator.add));
		}
		obj.zButton.zClick(localize(locator.save), "2");
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.groupCreated),
				"Group Created message should be shown");
		obj.zContactListItem.zExists(groupName);

		needReset = false;
	}

	/**
	 * Test case:-Previously selected contact group details are shown when no
	 * results found in search
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void updateContactGroupPaneWhenNoResult_Bug44331(String groupName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		obj.zButtonMenu.zClick(page.zABCompose.zNewMenuDropdownIconBtn);
		obj.zMenuItem.zClick(localize(locator.group));
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.groupNameLabel)),
				groupName);
		for (int i = 1; i <= 2; i++) {
			ProvZCS.createAccount("acc" + i + "@testdomain.com");
			obj.zEditField.zType(localize(locator.findLabel), "acc" + i
					+ "@testdomain.com");
			obj.zButton.zClick(localize(locator.search), "2");
			Thread.sleep(2500);
			if (currentBrowserName.contains("Safari")) {
				obj.zButton.zClick(localize(locator.search), "2");
				obj.zButton.zClick(localize(locator.search), "2");
				Thread.sleep(1000);
			}

			obj.zListItem.zDblClickItemInSpecificList("acc" + i
					+ "@testdomain.com", "2");

			obj.zButton.zClick(localize(locator.add));
		}
		obj.zButton.zClick(localize(locator.save), "2");
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.groupCreated),
				"Group Created message should be shown");
		obj.zContactListItem.zExists(groupName);

		selenium.type("xpath=//input[@class='search_input']", "abc");
		obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
		obj.zContactListItem.zNotExists(groupName);
		Assert
				.assertFalse(selenium
						.isElementPresent("xpath=//div[contains(@class,'contactHeader') and contains(text(),'"
								+ groupName + "')]"));

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