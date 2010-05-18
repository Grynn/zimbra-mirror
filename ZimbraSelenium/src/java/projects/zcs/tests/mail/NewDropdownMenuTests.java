package projects.zcs.tests.mail;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.Test;

import framework.util.RetryFailedTests;

import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings( { "static-access" })
public class NewDropdownMenuTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		page.zMailApp.zNavigateToMailApp();
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	//--------------------------------------------------------------------------
	// SECTION 2: TEST-METHODS
	//--------------------------------------------------------------------------
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyNewDropdownMenusInAllApptab() throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		String[] menuItemArray = { localize(locator.message),
				localize(locator.contact), localize(locator.group),
				localize(locator.appointment), localize(locator.task),
				localize(locator.page), localize(locator.uploadNewFile),
				localize(locator.folder), localize(locator.tag),
				localize(locator.addressBook), localize(locator.calendar),
				localize(locator.tasksFolder), localize(locator.notebook),
				localize(locator.briefcase) };

		// verifying menus exists in Mail
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		for (int i = 0; i <= 13; i++) {
			obj.zMenuItem.zExists(menuItemArray[i]);
		}

		// verifying menus exists in Address Book
		zGoToApplication("Address Book");
		obj.zButtonMenu.zClick("id=zb__CNS__NEW_MENU_dropdown");
		for (int i = 0; i <= 13; i++) {
			obj.zMenuItem.zExists(menuItemArray[i]);
		}

		// verifying menus exists in Calendar
		zGoToApplication("Calendar");
		obj.zButtonMenu.zClick("id=zb__CLD__NEW_MENU_dropdown");
		for (int i = 0; i <= 13; i++) {
			obj.zMenuItem.zExists(menuItemArray[i]);
		}

		// verifying menus exists in Tasks
		zGoToApplication("Tasks");
		obj.zButtonMenu.zClick("id=zb__TKL__NEW_MENU_dropdown");
		for (int i = 0; i <= 13; i++) {
			obj.zMenuItem.zExists(menuItemArray[i]);
		}

		// verifying menus exists in Documents
		zGoToApplication("Documents");
		obj.zButtonMenu.zClick("id=zb__NBP__NEW_MENU_dropdown");
		for (int i = 0; i <= 13; i++) {
			obj.zMenuItem.zExists(menuItemArray[i]);
		}

		// verifying menus exists in Briefcase
		zGoToApplication("Briefcase");
		obj.zButtonMenu.zClick("id=zb__BCC__NEW_MENU_dropdown");
		for (int i = 0; i <= 13; i++) {
			obj.zMenuItem.zExists(menuItemArray[i]);
		}
		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyNewDropdownMenusFunctionality() throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		String[] menuItemArray = { localize(locator.message),
				localize(locator.contact), localize(locator.group),
				localize(locator.appointment), localize(locator.task),
				localize(locator.page), localize(locator.uploadNewFile),
				localize(locator.folder), localize(locator.tag),
				localize(locator.addressBook), localize(locator.calendar),
				localize(locator.tasksFolder), localize(locator.notebook),
				localize(locator.briefcase) };

		// Message
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		obj.zMenuItem.zClick(menuItemArray[0]);
		obj.zTextAreaField.zExists(page.zComposeView.zToField);
		obj.zTextAreaField.zExists(page.zComposeView.zSubjectField);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		Thread.sleep(1000);
		obj.zTextAreaField.zNotExists(page.zComposeView.zSubjectField);
		obj.zButton.zNotExists(page.zComposeView.zCancelIconBtn);

		// Contact
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		obj.zMenuItem.zClick(menuItemArray[1]);
		obj.zButton.zClick(page.zABCompose.zCancelContactMenuIconBtn);
		Thread.sleep(1000);
		obj.zButton.zNotExists(page.zABCompose.zCancelContactMenuIconBtn);
		// add after new contact UI code get update

		// Contact group
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		obj.zMenuItem.zClick(menuItemArray[2]);
		obj.zEditField.zExists(localize(locator.groupNameLabel));
		obj.zButton.zClick(page.zABCompose.zCancelContactMenuIconBtn);
		Thread.sleep(1000);
		obj.zEditField.zNotExists(localize(locator.groupNameLabel));
		obj.zButton.zNotExists(page.zABCompose.zCancelContactMenuIconBtn);

		// Appointment
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		obj.zMenuItem.zClick(menuItemArray[3]);
		obj.zEditField.zExists(localize(locator.subject));
		if (config.getString("locale").equals("zh_CN")) {
			obj.zTextAreaField.zExists(localize(locator.attendeesLabel));
		} else {
			obj.zTextAreaField.zExists(localize(locator.attendees));
		}
		obj.zButton.zClick(page.zCalCompose.zApptCancelBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));
		Thread.sleep(1000);
		obj.zTextAreaField.zNotExists(localize(locator.attendees));
		obj.zButton.zNotExists(page.zCalCompose.zApptCancelBtn);

		// Task
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		obj.zMenuItem.zClick(menuItemArray[4]);
		obj.zEditField.zExists(localize(locator.subject));
		obj.zEditField.zExists(localize(locator.location));
		obj.zButton.zClick(page.zTaskApp.zTasksCancelBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));
		Thread.sleep(1000);
		obj.zEditField.zNotExists(localize(locator.subject));
		obj.zButton.zNotExists(page.zTaskApp.zTasksCancelBtn);

		// Page
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		obj.zMenuItem.zClick(menuItemArray[5]);
		if (config.getString("locale").equals("sv")) {
			obj.zEditField.zExists(localize(locator.pageLabel));
		} else {
			obj.zEditField.zExists(localize(locator.page));
		}
		obj.zButton.zClick(page.zDocumentCompose.zClosePageIconBtn);
		Thread.sleep(1000);
		obj.zEditField.zNotExists(localize(locator.page));
		obj.zButton.zNotExists(page.zDocumentCompose.zClosePageIconBtn);

		// Upload file
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		obj.zMenuItem.zClick(menuItemArray[6]);
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.uploadFileToBriefcase));
		Thread.sleep(1000);
		obj.zDialog.zNotExists(localize(locator.uploadFileToBriefcase));

		// Folder
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		obj.zMenuItem.zClick(menuItemArray[7]);
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewFolder));
		Thread.sleep(1000);
		obj.zDialog.zNotExists(localize(locator.createNewFolder));

		// Tag
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		obj.zMenuItem.zClick(menuItemArray[8]);
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewTag));
		Thread.sleep(1000);
		obj.zDialog.zNotExists(localize(locator.createNewTag));

		// Address book
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		obj.zMenuItem.zClick(menuItemArray[9]);
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewAddrBook));
		Thread.sleep(1000);
		obj.zDialog.zNotExists(localize(locator.createNewAddrBook));

		// Calendar
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		obj.zMenuItem.zClick(menuItemArray[10]);
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewCalendar));
		Thread.sleep(1000);
		obj.zDialog.zNotExists(localize(locator.createNewCalendar));

		// Task folder
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		obj.zMenuItem.zClick(menuItemArray[11]);
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewTaskFolder));
		Thread.sleep(1000);
		obj.zDialog.zNotExists(localize(locator.createNewTaskFolder));

		// Notebook
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		obj.zMenuItem.zClick(menuItemArray[12]);
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewNotebook));
		Thread.sleep(1000);
		obj.zDialog.zNotExists(localize(locator.createNewNotebook));

		// Briefcase
		obj.zButtonMenu.zClick(page.zMailApp.zNewMenuDropDown);
		obj.zMenuItem.zClick(menuItemArray[13]);
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewBriefcaseItem));
		Thread.sleep(1000);
		obj.zDialog.zNotExists(localize(locator.createNewBriefcaseItem));

		needReset = false;
	}

	//--------------------------------------------------------------------------
	// SECTION 3: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}