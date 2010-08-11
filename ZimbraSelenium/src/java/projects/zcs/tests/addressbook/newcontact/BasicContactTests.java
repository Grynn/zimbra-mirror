package projects.zcs.tests.addressbook.newcontact;


import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import framework.items.ContactItem;
import framework.util.RetryFailedTests;

/**
 * This covers some high priority test cases related to address book
 * 
 * @written by Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class BasicContactTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "ABDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		String itemsDisabled = "";
		String targetAddressBookFolder = localize(locator.emailedContacts);
		String itemsEnabled = localize(locator.AB_EDIT_CONTACT) + ","
				+ localize(locator.AB_TAG_CONTACT) + ","
				+ localize(locator.del) + "," + localize(locator.move) + ","
				+ localize(locator.print) + "," + localize(locator.search)
				+ "," + localize(locator.advancedSearch) + ","
				+ localize(locator.newEmail);

		if (test.equals("createBasicContact")
				|| test.equals("deleteContactAndVerify")
				|| test.equals("rghtClkDeleteContactAndVerify")
				|| test.equals("negativeTestCreateContact")) {
			return new Object[][] { { "lastName:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "" } };
		} else if (test.equals("editNameAndVerify")
				|| test.equals("rghtClickEditNameAndVerify")) {
			return new Object[][] { { "lastName:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "",
					"NewLastName:" + getLocalizedData(1),
					"NewMiddleName:" + getLocalizedData(1) } };
		} else if (test.equals("rtClickContactAndVerify")) {
			return new Object[][] { { "lastName:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "", itemsEnabled,
					itemsDisabled, "false" } };
		} else if (test.equals("moveContactAndVerify")
				|| test.equals("rghtClickMoveContactAndVerify")) {
			return new Object[][] { { "lastNameForMove:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "",
					targetAddressBookFolder } };
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
		page.zABCompose.zNavigateToContact();
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
	 * Enters some basic fields to create a contact in address book and verifies
	 * the contact exist or not
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createBasicContact(String cnLastName, String cnMiddleName,
			String cnFirstname) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstname;
		contact.middleName = cnMiddleName;
		contact.lastName = cnLastName;

		page.zABCompose.zCreateBasicContact(contact);
		obj.zContactListItem.zExists(contact.lastName);

		needReset = false;
	}

	/**
	 * Creates a contact with basic fields,edits the contacts using the ToolBar
	 * delete last name and verifies the change
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editNameAndVerify(String cnLastName, String cnMiddleName,
			String cnFirstname, String newcnLastName, String newMiddleName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstname;
		contact.middleName = cnMiddleName; 
		contact.lastName = cnLastName;

		page.zABCompose.zCreateBasicContact(contact);
		page.zABCompose.zModifyContact(cnLastName, newcnLastName,
				newMiddleName, "", "", "ToolbarEdit");

		Assert
				.assertTrue(page.zABCompose.zVerifyEditContact(newcnLastName,
						newMiddleName, "", ""),
						"The Contact is not modified correctly");
		needReset = false;

	}

	/**
	 * Creates a contact with basic fields,edits the contacts using Right Click
	 * last name and verifies the change
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rghtClickEditNameAndVerify(String cnLastName,
			String cnMiddleName, String cnFirstname, String newcnLastName,
			String newMiddleName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstname;
		contact.middleName = cnMiddleName; 
		contact.lastName = cnLastName;

		page.zABCompose.zCreateBasicContact(contact);
		page.zABCompose.zModifyContact(cnLastName, newcnLastName,
				newMiddleName, "", "", "RightClickEdit");
		Assert
				.assertTrue(page.zABCompose.zVerifyEditContact(newcnLastName,
						newMiddleName, "", ""),
						"The Contact is not modified correctly");

		needReset = false;
	}

	/**
	 * Creates a contact with basic fields,deletes the contact and verifies the
	 * contact does not exist after ToolBar delete
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteContactAndVerify(String cnLastName, String cnMiddleName,
			String cnFirstname) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstname;
		contact.middleName = cnMiddleName; 
		contact.lastName = cnLastName;

		page.zABCompose.zCreateBasicContact(contact);
		if (obj.zContactListItem.zExistsDontWait(cnLastName).equals("true"))
			page.zABApp.zDeleteContactAndVerify(cnLastName, "ToolbarDelete");

		needReset = false;
	}

	/**
	 * Creates a contact with basic fields,deletes the contact and verifies the
	 * contact gets moved to target AB folder using tool bar move button
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveContactAndVerify(String cnLastName, String cnMiddleName,
			String cnFirstname, String targetAB) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstname;
		contact.middleName = cnMiddleName; 
		contact.lastName = cnLastName;

		page.zABCompose.zCreateBasicContact(contact);
		if (obj.zContactListItem.zExistsDontWait(cnLastName).equals("true"))
			page.zABApp.zMoveContactAndVerify(cnLastName, targetAB,
					"ToolbarMove");

		needReset = false;
	}

	/**
	 * Creates a contact with basic fields,deletes the contact and verifies the
	 * contact gets moved to target AB folder using tool bar move button
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rghtClickMoveContactAndVerify(String cnLastName,
			String cnMiddleName, String cnFirstname, String targetAB)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstname;
		contact.middleName = cnMiddleName; 
		contact.lastName = cnLastName;

		page.zABCompose.zCreateBasicContact(contact);
		if (obj.zContactListItem.zExistsDontWait(cnLastName).equals("true"))
			page.zABApp.zMoveContactAndVerify(cnLastName, targetAB,
					"RightClickMove");

		needReset = false;
	}

	/**
	 * Creates a contact with basic fields,deletes the contact and verifies the
	 * contact does not exist after Right Click delete
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rghtClkDeleteContactAndVerify(String cnLastName,
			String cnMiddleName, String cnFirstname) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstname;
		contact.middleName = cnMiddleName; 
		contact.lastName = cnLastName;

		page.zABCompose.zCreateBasicContact(contact);
		if (obj.zContactListItem.zExistsDontWait(cnLastName).equals("true"))
			page.zABApp.zDeleteContactAndVerify(cnLastName, "RightClickDelete");

		needReset = false;
	}

	/**
	 *Test to verify the contact right click menus exits and are enabled or not
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickContactAndVerify(String cnLastName, String cnMiddleName,
			String cnFirstname, String enabledItemsSeparatedByComma,
			String disabledItemsSeparatedByComma, String ignoreContext)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		ContactItem contact = new ContactItem();
		contact.firstName = cnFirstname;
		contact.middleName = cnMiddleName; 
		contact.lastName = cnLastName;

		page.zABCompose.zCreateBasicContact(contact);
		obj.zContactListItem.zRtClick(cnLastName);
		page.zABApp.zVerifyAllMenuItems(enabledItemsSeparatedByComma,
				disabledItemsSeparatedByComma, ignoreContext);

		needReset = false;
	}

	/**
	 *Test to verify the contact right click menus exits and are enabled or not
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void negativeTestCreateContact(String cnLastName,
			String cnMiddleName, String cnFirstName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zABCompose.zContactsFolder));
		obj.zButton.zClick(page.zABCompose.zNewContactMenuIconBtn);
		page.zABCompose.zEnterBasicABData("", cnLastName, cnMiddleName,
				cnFirstName);
		obj.zButton.zClick(page.zABCompose.zCancelContactMenuIconBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));
		Thread.sleep(500);
		obj.zContactListItem.zNotExists(cnLastName);

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