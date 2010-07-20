package projects.zcs.tests.preferences.calendar;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import org.testng.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

//import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class CalendarPreferencesTestsUI extends CommonTest {

	//--------------------------------------------------------------------------
	@DataProvider(name = "calendarPreferencesDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();

		if (test.equals("zCalendarPrefDeleteInvite")) {
			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							getLocalizedData(3), "TRUE" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							getLocalizedData(3), "FALSE" } };
		} else {
			return new Object[][] { { "localize(locator.GAL)" } };
		}

	}

	// Before Class
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();

		String accountName = selfAccountName;

		ProvZCS.modifyAccount(accountName,
				"zimbraPrefCalendarAlwaysShowMiniCal", "TRUE");
		ProvZCS.modifyAccount(accountName, "zimbraPrefDeleteInviteOnReply",
				"TRUE");

		ProvZCS.modifyAccount(accountName, "zimbraPrefCalendarUseQuickAdd",
				"TRUE");

		ProvZCS.modifyAccount(accountName,
				"zimbraPrefUseTimeZoneListInCalendar", "TRUE");

		ProvZCS.modifyAccount(accountName,
				"zimbraPrefCalendarReminderSoundsEnabled", "TRUE");
		ProvZCS.modifyAccount(accountName,
				"zimbraPrefCalendarReminderFlashTitle", "TRUE");

//		selenium.refresh();
		zReloginToAjax();

		Thread.sleep(5000);
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

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefDefaultViewUITest() throws Exception {
		
		if (isExecutionARetry)
			handleRetry();

		String actualVal;
		String accountName = selfAccountName;

		page.zCalApp.zNavigateToCalendarPreferences();

		/**
		 * Added following check for bug 36480 : Support calenar invite forwarding address preference
		 *
		 */
		selenium.isElementPresent("DWT43_CAL_INV_FORWARDING_ADDRESS");
		
		obj.zFeatureMenu.zClick(localize(locator.defaultViewLabel));
		obj.zMenuItem.zClick(localize(locator.calViewMonth));
		
		waitForIE();
		
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefCalendarInitialView");

		Assert.assertEquals(actualVal, "month",
				"Default view set to Month is not set in db. Actual value is "
						+ actualVal);

		page.zCalApp.zNavigateToCalendarPreferences();

		obj.zFeatureMenu.zClick(localize(locator.defaultViewLabel));
		obj.zMenuItem.zClick(localize(locator.calViewWeek));
		
		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefCalendarInitialView");

		Assert.assertEquals(actualVal, "week",
				"Default view set to Month is not set in db. Actual value is "
						+ actualVal);

		Thread.sleep(500);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefStartWeekOnUITest() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String actualVal;
		String accountName = selfAccountName;

		page.zCalApp.zNavigateToCalendarPreferences();

		obj.zFeatureMenu.zClick(localize(locator.calendarStartWeekLabel));
		obj.zMenuItem.zClick(localize(locator.weekdayThuLong));
		
		waitForIE();
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefCalendarFirstDayOfWeek");

		Assert.assertEquals(actualVal, "4",
				"Start week on is not set to 4 in db for Thursday. Actual value is "
						+ actualVal);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefShowMiniCalUITest() throws Exception {

		String actualVal;

		if (isExecutionARetry)
			handleRetry();

		String accountName = selfAccountName;

		page.zCalApp.zNavigateToCalendarPreferences();

//		obj.zCheckbox.zActivate(localize(locator.alwaysShowMiniCal));

		obj.zCheckbox.zClick(localize(locator.alwaysShowMiniCal));
		
		waitForIE();
		
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(1000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefCalendarAlwaysShowMiniCal");

		Assert
				.assertEquals(
						actualVal,
						"FALSE",
						"Always show minical is not set to FALSE in db when unchecked. Actual value is "
								+ actualVal);

		page.zCalApp.zNavigateToCalendarPreferences();

//		obj.zCheckbox.zActivate(localize(locator.alwaysShowMiniCal));
		
		obj.zCheckbox.zClick(localize(locator.alwaysShowMiniCal));
		
		waitForIE();
		
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(1000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefCalendarAlwaysShowMiniCal");

		Assert.assertEquals(actualVal, "TRUE",
				"Always show minical is not set to TRUE in db when checked. Actual value is "
						+ actualVal);

		needReset = false;

	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefDeleteInviteUITest() throws Exception {

		String actualVal;

		if (isExecutionARetry)
			handleRetry();

		String accountName = selfAccountName;

		page.zCalApp.zNavigateToCalendarPreferences();
		
		obj.zCheckbox.zClick(getNameWithoutSpace(localize(locator.deleteInviteOnReplyLabel)));
		
		waitForIE();
		
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(1000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefDeleteInviteOnReply");

		Assert
				.assertEquals(
						actualVal,
						"FALSE",
						"Delete invite on reply not set to FALSE in db when unchecked. Actual value is "
								+ actualVal);

		page.zCalApp.zNavigateToCalendarPreferences();

//		obj.zCheckbox.zActivate(localize(locator.deleteInviteOnReplyLabel));
		obj.zCheckbox.zClick(getNameWithoutSpace(localize(locator.deleteInviteOnReplyLabel)));
		
		waitForIE();
		
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(1000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefDeleteInviteOnReply");

		Assert
				.assertEquals(
						actualVal,
						"TRUE",
						"Delete invite on reply is not set to TRUE in db when checked. Actual value is "
								+ actualVal);

		needReset = false;

	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefUseQuickAddUITest() throws Exception {

		String actualVal;

		if (isExecutionARetry)
			handleRetry();

		String accountName = selfAccountName;

		page.zCalApp.zNavigateToCalendarPreferences();
		selenium.uncheck("//input[contains(@id,'_CAL_USE_QUICK_ADD')]");

	//	obj.zCheckbox.zClick(localize(locator.useQuickAdd));

		waitForIE();
		
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefCalendarUseQuickAdd");

		Assert.assertEquals(actualVal, "FALSE",
				"Use quickadd dialog not set to FALSE in db when unchecked. Actual value is "
						+ actualVal);

		page.zCalApp.zNavigateToCalendarPreferences();

		//obj.zCheckbox.zClick(localize(locator.useQuickAdd));
		selenium.check("//input[contains(@id,'_CAL_USE_QUICK_ADD')]");
		
		waitForIE();
		
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefCalendarUseQuickAdd");

		Assert.assertEquals(actualVal, "TRUE",
				"Use quick add dialog is not set to TRUE in db when checked. Actual value is "
						+ actualVal);

		needReset = false;

	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefShowTimezoneListUITest() throws Exception {

		String actualVal;

		if (isExecutionARetry)
			handleRetry();

		String accountName = selfAccountName;

		page.zCalApp.zNavigateToCalendarPreferences();

		obj.zCheckbox.zClick(localize(locator.shouldShowTimezone));

		waitForIE();
		
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefUseTimeZoneListInCalendar");

		Assert
				.assertEquals(
						actualVal,
						"FALSE",
						"Show timezone list in appointment compose not set to FALSE in db when unchecked. Actual value is "
								+ actualVal);

		page.zCalApp.zNavigateToCalendarPreferences();

		obj.zCheckbox.zClick(localize(locator.shouldShowTimezone));
		
		waitForIE();
		
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefUseTimeZoneListInCalendar");

		Assert
				.assertEquals(
						actualVal,
						"TRUE",
						"Show timezone list in appointment compose is not set to TRUE in db when checked. Actual value is "
								+ actualVal);

		needReset = false;

	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefRemindersUITest() throws Exception {

		String actualVal;

		if (isExecutionARetry)
			handleRetry();

		String accountName = selfAccountName;

		page.zCalApp.zNavigateToCalendarPreferences();

		obj.zFeatureMenu.zClick(localize(locator.apptReminderLabel));
		obj.zMenuItem.zClick(localize(locator.apptRemindNever));

		obj.zCheckbox.zClick(localize(locator.playSound));
		obj.zCheckbox.zClick(localize(locator.flashBrowser));

		waitForIE();
		
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefCalendarApptReminderWarningTime");

		Assert.assertEquals(actualVal, "0",
				"Show Reminders is not set to 0 in db when selected Never. Actual value is "
						+ actualVal);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefCalendarReminderSoundsEnabled");

		Assert.assertEquals(actualVal, "FALSE",
				"Play a sound is not set to FALSE in db when unchecked. Actual value is "
						+ actualVal);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefCalendarReminderFlashTitle");

		Assert
				.assertEquals(
						actualVal,
						"FALSE",
						"Flash the browser title is not set to FALSE in db when unchecked. Actual value is "
								+ actualVal);

		page.zCalApp.zNavigateToCalendarPreferences();

		obj.zCheckbox.zClick(localize(locator.playSound));
		obj.zCheckbox.zClick(localize(locator.flashBrowser));
		
		waitForIE();
		
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		Thread.sleep(2000);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefCalendarReminderSoundsEnabled");

		Assert.assertEquals(actualVal, "TRUE",
				"Play a sound is not set to TRUE in db when checked. Actual value is "
						+ actualVal);

		actualVal = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefCalendarReminderFlashTitle");

		Assert
				.assertEquals(
						actualVal,
						"TRUE",
						"Flash the browser title is not set to TRUE in db when checked. Actual value is "
								+ actualVal);

		needReset = false;

	}

	@Test(dataProvider = "calendarPreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefDeleteInvite(String subject, String location,
			String body, String deleteInvite) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String accountName = ProvZCS.getRandomAccount();

		ProvZCS.modifyAccount(accountName, "zimbraPrefDeleteInviteOnReply",
				deleteInvite);

		page.zCalApp.zNavigateToCalendar();

		page.zCalCompose
				.zCreateSimpleAppt(subject, location, accountName, body);

		Thread.sleep(500);

		resetSession();

		Thread.sleep(1000);

		SelNGBase.selfAccountName = accountName;
		page.zLoginpage.zLoginToZimbraAjax(accountName);

		MailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.inbox),
						subject);

		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.replyAccept));
		Thread.sleep(1000);

		if (deleteInvite.equals("TRUE"))
			obj.zMessageItem.zNotExists(subject);
		else
			obj.zMessageItem.zExists(subject);

		needReset = false;
	}

	private void waitForIE() throws Exception {
		String browser = config.getString("browser");
		
		if (browser.equals("IE"))
			Thread.sleep(1000);
		
	}
	
	
//	private static String getNameWithoutSpace(String key) {
//		if (config.getString("browser").equals("IE"))
//			return key.replace(" :", "");
//		else
//			return key;
//	}

	
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}

}
