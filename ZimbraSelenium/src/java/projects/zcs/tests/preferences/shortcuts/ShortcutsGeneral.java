package projects.zcs.tests.preferences.shortcuts;

import java.awt.Robot;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import javax.swing.text.html.parser.TagElement;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

//import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.Selenium;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.redolog.op.CreateTag;
import com.zimbra.cs.zclient.ZFolder;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class ShortcutsGeneral extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "shortcutsDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();

		if (test.equals("shortcutsGoToApp")) {
			return new Object[][] {
					{ KeyEvent.VK_A, localize(locator.contacts) },
					{ KeyEvent.VK_C, localize(locator.calendar) },
					{ KeyEvent.VK_D, localize(locator.notebook) },
					{ KeyEvent.VK_T, localize(locator.tasks) },
					{ KeyEvent.VK_P, "" },
					{ KeyEvent.VK_M, localize(locator.inbox) },
					{ KeyEvent.VK_B, localize(locator.briefcase) } };
		} else if (test.equals("newItem_N")) {
			return new Object[][] {
					{ localize(locator.mail), localize(locator.subjectLabel) },
					{ localize(locator.addressBook),
							localize(locator.addressLabel) },
					{ localize(locator.calendar),
							localize(locator.locationLabel) },
					{ localize(locator.tasks), localize(locator.locationLabel) } };
		} else if (test.equals("shortcutsNew")) {

			return new Object[][] {

					{ "", KeyEvent.VK_M, localize(locator.subjectLabel), "edit" },
					{ "", KeyEvent.VK_C, localize(locator.addressLabel), "edit" },
					{ "", KeyEvent.VK_P, localize(locator.pageLabel), "edit" },
					{ "", KeyEvent.VK_A, localize(locator.locationLabel),
							"edit" },
					{ "", KeyEvent.VK_K, localize(locator.locationLabel),
							"edit" },
					{ "", KeyEvent.VK_W, localize(locator.createNewNotebook),
							"dialog" },
					{ "", KeyEvent.VK_L, localize(locator.createNewCalendar),
							"dialog" },
					{ "", KeyEvent.VK_T, localize(locator.createNewTag),
							"dialog" } };

		} else if (test.equals("deleteItem_Delete_And_Backspace")) {
			return new Object[][] { { KeyEvent.VK_DELETE },
					{ KeyEvent.VK_BACK_SPACE } };
		} else {

			return new Object[][] { { "test" } };
		}
	}

	// Before Class
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		Thread.sleep(2000);
		isExecutionARetry = false;
	}

	// Before method
	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shortcutsGoToApp(int keyToPress, String toVerify)
			throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		Robot zRobot = new Robot();

		selenium.windowFocus();

		zRobot.keyPress(KeyEvent.VK_G);
		zRobot.keyPress(keyToPress);

		zRobot.keyRelease(KeyEvent.VK_G);
		zRobot.keyRelease(keyToPress);

		Thread.sleep(3000);

		if (toVerify.equals("")) {
			obj.zButton.zExists(page.zCalApp.zPreferencesSaveIconBtn);
		} else {
			obj.zFolder.zExists(toVerify);
		}

		needReset = false;
	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void shortcutsNew(String isFirst, int keyToPress, String toVerify,
			String objType) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		if (!isFirst.equals("")) {
			selenium.refresh();
			Thread.sleep(5000);
		}

		selenium.windowFocus();

		Robot zRobot = new Robot();

		Thread.sleep(3000);
		System.out.println(keyToPress);
		zRobot.keyPress(KeyEvent.VK_N);
		zRobot.keyPress(keyToPress);

		zRobot.keyRelease(KeyEvent.VK_N);
		zRobot.keyRelease(keyToPress);

		Thread.sleep(4000);

		if (objType.equals("dialog"))
			obj.zDialog.zExists(toVerify);
		else if (objType.equals("edit"))
			obj.zEditField.zExists(toVerify);

		zRobot.keyPress(KeyEvent.VK_ESCAPE);
		zRobot.keyRelease(KeyEvent.VK_ESCAPE);
		if (keyToPress == 65 || keyToPress == 75) {
			obj.zButton.zClickInDlgByName(localize(locator.no),
					localize(locator.warningMsg));
		}

		Thread.sleep(2000);

		needReset = false;
	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void newItem_N(String tabName, String toVerify) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		selenium.windowFocus();

		obj.zTab.zClick(tabName);

		Robot zRobot = new Robot();

		Thread.sleep(3000);
		zRobot.keyPress(KeyEvent.VK_N);
		zRobot.keyRelease(KeyEvent.VK_N);
		Thread.sleep(4000);

		if (tabName.equals(localize(locator.briefcase)))
			obj.zDialog.zExists(localize(locator.uploadFileToBriefcase));
		else
			obj.zEditField.zExists(toVerify);

		zRobot.keyPress(KeyEvent.VK_ESCAPE);
		zRobot.keyRelease(KeyEvent.VK_ESCAPE);

		if (tabName.equals(localize(locator.calendar))
				|| tabName.equals(localize(locator.tasks))) {
			obj.zButton.zClickInDlgByName(localize(locator.no),
					localize(locator.warningMsg));
		}

		Thread.sleep(2000);

		needReset = false;
	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void visit_Folder_V(String test) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		Robot zRobot = new Robot();

		String subject = "Test For visit Folder";
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(selfAccountName, "", "",
				subject, "test content", "");

		selenium.windowFocus();

		zRobot.keyPress(KeyEvent.VK_V);
		zRobot.keyRelease(KeyEvent.VK_V);
		Thread.sleep(2000);

		obj.zFolder.zClickInDlg(localize(locator.sent));
		obj.zButton.zClickInDlg(localize(locator.ok));
		obj.zMessageItem.zExists(subject);

		needReset = false;
	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void visitTag_VV_And_ApplyTag_T(String test) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String subject = getLocalizedData_NoSpecialChar();
		String subject2 = getLocalizedData_NoSpecialChar();

		String tagName = getLocalizedData_NoSpecialChar();
		page.zMailApp.zCreateTag(tagName);

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(selfAccountName, "", "",
				subject, "test content", "");

		Robot zRobot = new Robot();
		Thread.sleep(3000);
		zRobot.keyPress(KeyEvent.VK_T);
		zRobot.keyRelease(KeyEvent.VK_T);
		Thread.sleep(4000);
		obj.zFolder.zClickInDlgByName(tagName, localize(locator.pickATag));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.pickATag));

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(selfAccountName, "", "",
				subject2, "test content", "");

		selenium.windowFocus();
		Robot zRobot2 = new Robot();
		Thread.sleep(3000);

		zRobot2.keyPress(KeyEvent.VK_V);
		zRobot2.keyRelease(KeyEvent.VK_V);
		zRobot2.keyPress(KeyEvent.VK_V);
		zRobot2.keyRelease(KeyEvent.VK_V);
		Thread.sleep(4000);

		obj.zFolder.zClickInDlgByName(tagName, localize(locator.pickATag));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.pickATag));
		obj.zMessageItem.zExists(subject);
		obj.zMessageItem.zNotExists(subject2);

		needReset = false;
	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void removeTag_U(String test) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String tagName = getLocalizedData_NoSpecialChar();
		page.zMailApp.zCreateTag(tagName);
		String subject = getLocalizedData_NoSpecialChar();
		String subject2 = getLocalizedData_NoSpecialChar();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(selfAccountName, "", "",
				subject, "test content", "");

		Robot zRobot = new Robot();
		Thread.sleep(3000);
		zRobot.keyPress(KeyEvent.VK_T);
		zRobot.keyRelease(KeyEvent.VK_T);
		Thread.sleep(4000);
		obj.zFolder.zClickInDlgByName(tagName, localize(locator.pickATag));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.pickATag));

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(selfAccountName, "", "",
				subject2, "test content", "");

		selenium.windowFocus();
		Robot zRobot3 = new Robot();
		Thread.sleep(3000);

		obj.zMessageItem.zClick(subject);
		zRobot3.keyPress(KeyEvent.VK_U);
		zRobot3.keyRelease(KeyEvent.VK_U);
		Thread.sleep(4000);

		selenium.windowFocus();
		Robot zRobot4 = new Robot();
		Thread.sleep(3000);

		zRobot4.keyPress(KeyEvent.VK_V);
		zRobot4.keyRelease(KeyEvent.VK_V);
		zRobot4.keyPress(KeyEvent.VK_V);
		zRobot4.keyRelease(KeyEvent.VK_V);
		Thread.sleep(4000);

		obj.zFolder.zClickInDlgByName(tagName, localize(locator.pickATag));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.pickATag));
		obj.zMessageItem.zNotExists(subject);
		obj.zMessageItem.zNotExists(subject2);

		needReset = false;
	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void newfFolder_NF(String test) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String folderName = getLocalizedData_NoSpecialChar();
		String subject = getLocalizedData_NoSpecialChar();

		Robot zRobot = new Robot();
		Thread.sleep(3000);
		zRobot.keyPress(KeyEvent.VK_N);
		zRobot.keyRelease(KeyEvent.VK_N);
		zRobot.keyPress(KeyEvent.VK_F);
		zRobot.keyRelease(KeyEvent.VK_F);
		Thread.sleep(4000);

		obj.zEditField.zTypeInDlgByName(localize(locator.name), folderName,
				localize(locator.createNewFolder));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewFolder));

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt(selfAccountName, "", "",
				subject, "test content", "");

		obj.zButton.zClick(page.zMailApp.zMoveBtn);
		obj.zFolder
				.zClickInDlgByName(folderName, localize(locator.moveMessage));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.moveMessage));

		obj.zFolder.zClick(folderName);
		obj.zMessageItem.zExists(subject);

		needReset = false;
	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void runSavedSearch_S(String test) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String searchName = getLocalizedData_NoSpecialChar();

		selenium.type("//input[@type='text']", "asdfg");
		obj.zButton.zClick(localize(locator.save));
		obj.zEditField.zTypeInDlgByName(localize(locator.name), searchName,
				localize(locator.saveSearch));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.saveSearch));

		String subject = getLocalizedData_NoSpecialChar()
				+ " zimbra saved search subject asdfg";
		String subject2 = getLocalizedData_NoSpecialChar();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(selfAccountName, "", "",
				subject, "test content", "");

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(selfAccountName, "", "",
				subject2, "test content", "");

		Robot zRobot = new Robot();
		Thread.sleep(3000);
		zRobot.keyPress(KeyEvent.VK_S);
		zRobot.keyRelease(KeyEvent.VK_S);
		Thread.sleep(4000);
		obj.zFolder.zClickInDlg(searchName);
		obj.zButton.zClickInDlg(localize(locator.ok));

		obj.zMessageItem.zExists(subject);
		obj.zMessageItem.zNotExists(subject2);

		needReset = false;
	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void composeInNewWindow_Shift_C(String test) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		Robot zRobot = new Robot();
		Thread.sleep(3000);
		selenium.windowFocus();
		zRobot.keyPress(KeyEvent.VK_SHIFT);
		zRobot.keyPress(KeyEvent.VK_C);
		zRobot.keyRelease(KeyEvent.VK_SHIFT);
		zRobot.keyRelease(KeyEvent.VK_C);
		Thread.sleep(3000);
		selenium.selectWindow("_blank");
		zWaitTillObjectExist("button", page.zMailApp.zSendBtn_newWindow);
		obj.zButton.zClick(page.zMailApp.zCancelBtn_newWindow);
		selenium.selectWindow(null);

		needReset = false;
	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteItem_Delete_And_Backspace(int keyToPress)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject = "Test For Delete Item";
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt(selfAccountName, "", "",
				subject, "test content", "");

		Robot zRobot = new Robot();
		Thread.sleep(3000);
		selenium.windowFocus();

		zRobot.keyPress(keyToPress);
		zRobot.keyRelease(keyToPress);
		Thread.sleep(4000);

		obj.zMessageItem.zNotExists(subject);
		obj.zFolder.zClick(localize(locator.trash));
		obj.zMessageItem.zExists(subject);

		needReset = false;
	}

	@Test(dataProvider = "shortcutsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void listAppointments_L(String test) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		
		/**
		 * Press L to switch to list view of appointments.
		 */
		Robot zRobot = new Robot();
		Thread.sleep(3000);
		zRobot.keyPress(KeyEvent.VK_L);
		zRobot.keyRelease(KeyEvent.VK_L);
		Thread.sleep(4000);
		
		/**
		 * Verification : Check all the headers in list view of appointments are present.
		 */
		Assert.assertTrue(selenium.isElementPresent("zlhi__CLL__se"));
		Assert.assertTrue(selenium.isElementPresent("zlhi__CLL__tg"));
		Assert.assertTrue(selenium.isElementPresent("zlhi__CLL__at"));
		Assert.assertTrue(selenium.isElementPresent("zlhl__CLL__su"));
		Assert.assertTrue(selenium.isElementPresent("zlhl__CLL__lo"));
		Assert.assertTrue(selenium.isElementPresent("zlhl__CLL__st"));
		Assert.assertTrue(selenium.isElementPresent("zlhl__CLL__fo"));
		Assert.assertTrue(selenium.isElementPresent("zlhi__CLL__re"));
		Assert.assertTrue(selenium.isElementPresent("zlhl__CLL__dt"));
		
		needReset = false;
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}
