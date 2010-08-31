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
import framework.util.SleepUtil;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class CalendarPreferencesSetFalse extends CommonTest {

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
	public void zLogin() throws Exception {
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
		super.zLogin();
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

		SleepUtil.sleep(1000);

		obj.zDialog.zNotExists(localize(locator.quickAddAppt));

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefShowTimeZone() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		page.zCalApp.zNavigateToApptCompose();

		SleepUtil.sleep(500);

		obj.zMiscObj
				.zNotExists("ZmApptComposeView ZWidget/*tzoneSelect/ZSelectAutoSizingContainer ZHasDropDown");

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefShowReminders() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		
		SleepUtil.sleep(1000);

		page.zCalCompose.zCreateSimpleAppt(getLocalizedData_NoSpecialChar(),
				getLocalizedData_NoSpecialChar(), "",
				getLocalizedData_NoSpecialChar());

		SleepUtil.sleep(1000);

		obj.zDialog.zNotExists(localize(locator.apptReminders));
		SelNGBase.needReset.set(false);
	}

}
