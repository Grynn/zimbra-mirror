package projects.zcs.tests.addressbook.contactgroups;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;

import com.zimbra.common.service.ServiceException;

import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;

/**
 * @author Jitesh Sojitra
 */

@SuppressWarnings("static-access")
public class TagContactGroup extends CommonTest {
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
	 * Edit contact group and verify add, remove tag functionality
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editContactGroupAndVerifyAddRemoveTag(String firstName,
			String groupName) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String tag1, tag2;
		page.zABApp.zCreateContactGroup(groupName, "bccuser@testdomain.com");
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zContactListItem.zClick(groupName);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(tag1);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(groupName);
		SleepUtil.sleep(1000);

		obj.zButton.zClick(page.zABApp.zEditContactIconBtn);
		obj.zButton.zClick(page.zABApp.zTagGroupBtn_EditGroup);
		obj.zMenuItem.zClick(localize(locator.removeTag));
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagGroupBtn_EditGroup);
		obj.zMenuItem.zExists(tag1);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagGroupBtn_EditGroup);
		obj.zMenuItem.zExists(tag1);
		obj.zMenuItem.zNotExists(tag2);
		obj.zButton.zClick(localize(locator.close));
		obj.zFolder.zClick(tag1);
		assertReport("false", obj.zContactListItem.zExistsDontWait(groupName),
				"Verify contact group not exists");
		obj.zFolder.zClick(tag2);
		obj.zContactListItem.zExists(groupName);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Create 2 tag, apply 1 tag to each contact group and verify contact group
	 * exist / not exist by clicking to tag
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTagFunctionalityFor2ContactGroupAndRemoveTag(
			String firstName, String group1) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String group2, tag1, tag2;
		group2 = getLocalizedData_NoSpecialChar();
		page.zABApp.zCreateContactGroup(group1, "bccuser@testdomain.com");
		page.zABApp.zCreateContactGroup(group2, "bccuser@testdomain.com");
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zContactListItem.zClick(group1);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(tag1);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(group1);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zClick(group2);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(1000);
		obj.zContactListItem.zVerifyIsTagged(group2);
		obj.zFolder.zClick(tag1);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zExists(group1);
		assertReport("false", obj.zContactListItem.zExistsDontWait(group2),
				"Verify contact group2 not exists");
		obj.zFolder.zClick(tag2);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zExists(group2);
		assertReport("false", obj.zContactListItem.zExistsDontWait(group1),
				"Verify contact group1 not exists");
		obj.zFolder.zClick(localize(locator.contacts));
		SleepUtil.sleep(1000);
		assertReport("true", obj.zContactListItem.zExistsDontWait(group1),
				"Verify contact group1 not exists");
		assertReport("true", obj.zContactListItem.zExistsDontWait(group2),
				"Verify contact group2 not exists");
		obj.zFolder.zClick(localize(locator.emailedContacts));
		SleepUtil.sleep(1000);
		assertReport("false", obj.zContactListItem.zExistsDontWait(group1),
				"Verify contact group1 not exists");
		assertReport("false", obj.zContactListItem.zExistsDontWait(group2),
				"Verify contact group2 not exists");

		obj.zFolder.zClick(localize(locator.contacts));
		obj.zContactListItem.zClick(group2);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zClick(localize(locator.removeTag));
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABApp.zTagContactMenuIconBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		SelNGBase.needReset.set(false);
	}

}