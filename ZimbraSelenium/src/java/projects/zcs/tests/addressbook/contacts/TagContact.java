package projects.zcs.tests.addressbook.contacts;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.items.ContactItem;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;

/**
 * @author Jitesh Sojitra
 */

@SuppressWarnings("static-access")
public class TagContact extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "tagDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("createRenameDeleteTagForContactAndVerify")
				|| test.equals("verifyTagFunctionalityFor2ContactAndRemoveTag")
				|| test.equals("applyMutlipleTagToContactAndVerify")
				|| test.equals("editContactAndVerifyAddRemoveTag")
				|| test.equals("editContactGroupAndVerifyAddRemoveTag")
				|| test
						.equals("verifyTagFunctionalityFor2ContactGroupAndRemoveTag")
				|| test.equals("applyTagByDnDTagToContactAndViceVersa")
				|| test.equals("tryToCreateDuplicateTagInAddressBook")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="address book";
		super.zLogin();
	}
	
	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------

	/**
	 * Verify create, rename & delete functionality for tag for contacts
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createRenameDeleteTagForContactAndVerify(String firstName,
			String lastName) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String tag1, newTag1;
		
		ContactItem contact = new ContactItem();
		contact.firstName = firstName;
		contact.lastName = lastName;

		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);

		tag1 = getLocalizedData_NoSpecialChar();
		newTag1 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zContactListItem.zClick(lastName);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(tag1);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(lastName);
		SleepUtil.sleep(1000);

		zRenameTag(tag1, newTag1);
		obj.zFolder.zNotExists(tag1);
		obj.zFolder.zClick(newTag1);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(lastName);

		zDeleteTag(newTag1);
		obj.zContactListItem.zClick(lastName);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		SelNGBase.needReset.set(false);
	}

	/**
	 * Create 2 tag, apply 1 tag to each contact and verify contact exist / not
	 * exist by clicking to tag
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTagFunctionalityFor2ContactAndRemoveTag(String firstName,
			String lastName) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String lastName2, tag1, tag2;
		
		ContactItem contact1 = new ContactItem();
		contact1.firstName = firstName;
		contact1.lastName = lastName;

		ContactItem contact2 = new ContactItem();
		contact2.firstName = firstName;
		contact2.lastName = getLocalizedData_NoSpecialChar();

		page.zABCompose.createItem(ActionMethod.DEFAULT, contact1);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact2);

		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zContactListItem.zClick(lastName);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(tag1);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(lastName);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zClick(contact2.lastName);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(contact2.lastName);
		obj.zFolder.zClick(tag1);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zExists(lastName);
		assertReport("false", obj.zContactListItem.zExistsDontWait(contact2.lastName),
				"Verify contact2 not exists");
		obj.zFolder.zClick(tag2);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zExists(contact2.lastName);
		assertReport("false", obj.zContactListItem.zExistsDontWait(lastName),
				"Verify contact1 not exists");
		obj.zFolder.zClick(localize(locator.contacts));
		SleepUtil.sleep(1000);
		assertReport("true", obj.zContactListItem.zExistsDontWait(lastName),
				"Verify contact1 not exists");
		assertReport("true", obj.zContactListItem.zExistsDontWait(contact2.lastName),
				"Verify contact2 not exists");
		obj.zFolder.zClick(localize(locator.emailedContacts));
		SleepUtil.sleep(1000);
		assertReport("false", obj.zContactListItem.zExistsDontWait(lastName),
				"Verify contact1 not exists");
		assertReport("false", obj.zContactListItem.zExistsDontWait(contact2.lastName),
				"Verify contact2 not exists");

		obj.zFolder.zClick(localize(locator.contacts));
		obj.zContactListItem.zClick(contact2.lastName);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(localize(locator.removeTag));
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		SelNGBase.needReset.set(false);
	}

	/**
	 * Create 2 tag, apply both tag to contact and verify both contact exists
	 * after clicking to tag
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void applyMutlipleTagToContactAndVerify(String firstName,
			String lastName) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String tag1, tag2;
		
		ContactItem contact = new ContactItem();
		contact.lastName = lastName;
		contact.firstName = firstName;
		
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		obj.zContactListItem.zClick(lastName);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag1);
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(lastName);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zNotExists(tag1);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(lastName);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zNotExists(tag1);
		obj.zMenuItem.zNotExists(tag2);
		obj.zFolder.zClick(tag1);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zExists(lastName);
		obj.zFolder.zClick(tag2);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zExists(lastName);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Edit contact and verify add, remove tag functionality
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editContactAndVerifyAddRemoveTag(String firstName,
			String lastName) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String tag1, tag2;
		ContactItem contact = new ContactItem();
		contact.firstName = firstName;
		contact.lastName = lastName;
		
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zContactListItem.zClick(lastName);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(tag1);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(lastName);
		SleepUtil.sleep(1000);

		obj.zButton.zClick(page.zABApp.zEditContactIconBtn);
		obj.zButton.zClick(page.zABApp.zTagContactBtn_EditContact);
		obj.zMenuItem.zClick(localize(locator.removeTag));
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactBtn_EditContact);
		obj.zMenuItem.zExists(tag1);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactBtn_EditContact);
		obj.zMenuItem.zExists(tag1);
		obj.zMenuItem.zNotExists(tag2);
		obj.zButton.zClick(localize(locator.close));
		obj.zFolder.zClick(tag1);
		assertReport("false", obj.zContactListItem.zExistsDontWait(lastName),
				"Verify contact not exists");
		obj.zFolder.zClick(tag2);
		obj.zContactListItem.zExists(lastName);

		SelNGBase.needReset.set(false);
	}
}