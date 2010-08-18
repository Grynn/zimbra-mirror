package projects.zcs.tests.preferences.calendar;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import junit.framework.Assert;

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
public class CalendarPreferencesSetFalseTest extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
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

		String accountName = SelNGBase.selfAccountName.get();

		ProvZCS.modifyAccount(accountName, "zimbraPrefCalendarInitialView",
				"workWeek");

		ProvZCS.modifyAccount(accountName,
				"zimbraPrefCalendarAlwaysShowMiniCal", "FALSE");

		ProvZCS.modifyAccount(accountName, "zimbraPrefCalendarUseQuickAdd",
				"FALSE");

		ProvZCS.modifyAccount(accountName,
				"zimbraPrefUseTimeZoneListInCalendar", "FALSE");

		ProvZCS.modifyAccount(accountName,
				"zimbraPrefCalendarApptReminderWarningTime", "0");

//		selenium.refresh();
		zReloginToAjax();

		Thread.sleep(5000);
		SelNGBase.isExecutionARetry.set(false);
	}

	// Before method
	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefShowMiniCal() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		obj.zMiscObj.zNotExists("DwtCalendar");

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefQuickAdd() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		obj.zMiscObj.zDblClickXY("ZmCalViewMgr/ImgCalendarDayGrid", "50,50");

		Thread.sleep(1000);

		obj.zDialog.zNotExists(localize(locator.quickAddAppt));

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefShowTimeZone() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		page.zCalApp.zNavigateToApptCompose();

		Thread.sleep(500);

		obj.zMiscObj
				.zNotExists("ZmApptComposeView ZWidget/*tzoneSelect/ZSelectAutoSizingContainer ZHasDropDown");

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefShowReminders() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		
		Thread.sleep(1000);

		page.zCalCompose.zCreateSimpleAppt(getLocalizedData_NoSpecialChar(),
				getLocalizedData_NoSpecialChar(), "",
				getLocalizedData_NoSpecialChar());

		Thread.sleep(1000);

		obj.zDialog.zNotExists(localize(locator.apptReminders));
		SelNGBase.needReset.set(false);
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}

}
