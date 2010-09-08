package projects.zcs.tests.addressbook.contactgroups;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import framework.core.*;
import framework.items.ContactGroupItem;
import framework.util.RetryFailedTests;
import framework.util.Stafzmprov;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;

public class DeleteContactGroup extends CommonTest {

	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB = "address book";
		super.zLogin();
	}

	@SuppressWarnings("static-access")
	@Test(description = "Delete a contact group - Delete button", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteContactGroup() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		ContactGroupItem group = new ContactGroupItem();
		group.nickname = getLocalizedData_NoSpecialChar();
		for (int i = 1; i <= 2; i++) {
			String email = "acc" + i + "@testdomain.com";
			Stafzmprov.createAccount(email);
			group.addDListMember(email);

		}
		page.zABCompose.createContactGroupItem(ActionMethod.DEFAULT, group);
		obj.zContactListItem.zExists(group.nickname);
		obj.zButton.zClick(page.zABApp.zDeleteContactIconBtn);
		obj.zContactListItem.zNotExists(group.nickname);

		SelNGBase.needReset.set(false);
	}

	@SuppressWarnings("static-access")
	@Test(description = "Delete a contact group - Right click", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rghtClkDeleteContactGroupAndVerify() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		ContactGroupItem group = new ContactGroupItem();
		group.nickname = getLocalizedData_NoSpecialChar();
		for (int i = 1; i <= 2; i++) {
			String email = "acc" + i + "@testdomain.com";
			Stafzmprov.createAccount(email);
			group.addDListMember(email);

		}
		page.zABCompose.createContactGroupItem(ActionMethod.DEFAULT, group);
		obj.zContactListItem.zExists(group.nickname);
		obj.zContactListItem.zRtClick(group.nickname);
		obj.zMenuItem.zClick(page.zABApp.zRtClickContactDeleteMenuIconBtn);
		obj.zContactListItem.zNotExists(group.nickname);

		SelNGBase.needReset.set(false);
	}

	@SuppressWarnings("static-access")
	@Test(description = "Delete a contact group - Delete button", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteContactGroupUsingDeleteKey() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		ContactGroupItem group = new ContactGroupItem();
		group.nickname = getLocalizedData_NoSpecialChar();
		for (int i = 1; i <= 2; i++) {
			String email = "acc" + i + "@testdomain.com";
			Stafzmprov.createAccount(email);
			group.addDListMember(email);

		}
		page.zABCompose.createContactGroupItem(ActionMethod.DEFAULT, group);
		obj.zContactListItem.zExists(group.nickname);
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_DELETE);
		robot.keyRelease(KeyEvent.VK_DELETE);
		obj.zContactListItem.zNotExists(group.nickname);

		SelNGBase.needReset.set(false);
	}

}
