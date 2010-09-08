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

import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;


import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class CalendarPreferencesSetTrue extends CommonTest {

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

		Stafzmprov.modifyAccount(accountName, "zimbraPrefCalendarInitialView",
				"week");

		Stafzmprov.modifyAccount(accountName,
				"zimbraPrefCalendarAlwaysShowMiniCal", "TRUE");

		Stafzmprov.modifyAccount(accountName, "zimbraPrefCalendarUseQuickAdd",
				"TRUE");

		Stafzmprov.modifyAccount(accountName,
				"zimbraPrefUseTimeZoneListInCalendar", "TRUE");

		Stafzmprov.modifyAccount(accountName,
				"zimbraPrefCalendarApptReminderWarningTime", "60");

		super.zLogin();
//		selenium.refresh();
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefShowMiniCal() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		obj.zMiscObj.zExists("DwtCalendar");

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "calendarPreferencesDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefDeleteInvite(String subject, String location,
			String body, String deleteInvite) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String accountName = Stafzmprov.getRandomAccount();

		Stafzmprov.modifyAccount(accountName, "zimbraPrefDeleteInviteOnReply",
				deleteInvite);
		
		page.zCalApp.zNavigateToCalendar();

		page.zCalCompose
				.zCreateSimpleAppt(subject, location, accountName, body);

		SleepUtil.sleep(500);

		resetSession();

		SleepUtil.sleep(1000);

		SelNGBase.selfAccountName.set(accountName);
		page.zLoginpage.zLoginToZimbraAjax(accountName);

		MailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.inbox),
						subject);

		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.replyAccept));
		SleepUtil.sleep(1000);

		if (deleteInvite.equals("TRUE"))
			obj.zMessageItem.zNotExists(subject);
		else
			obj.zMessageItem.zExists(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full","enabled" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefQuickAdd() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		if(currentBrowserName.indexOf("FF 3")>=0){
			obj.zMiscObj.zActivateByDoubleClick("ZmCalViewMgr/ImgCalendarDayGrid");
		} else {
			obj.zMiscObj.zDblClickXY("ZmCalViewMgr/ImgCalendarDayGrid", "50,50");
		}
		SleepUtil.sleep(1000);

		obj.zDialog.zExists(localize(locator.quickAddAppt));
		
		obj.zButton.zClickInDlg(localize(locator.cancel));

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full"}, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefShowTimeZone() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		page.zCalApp.zNavigateToApptCompose();

		SleepUtil.sleep(1000);

		// String timeZone =
		// obj.zMiscObj.zExistsDontWait("ZmApptComposeView ZWidget/*tzoneSelect/ZSelectAutoSizingContainer ZHasDropDown");

		// Assert.assertEquals(timeZone, "true",
		// "Timezone menu doesn't exist on setting the ShowTimeZone pref to True");
		Assert
				.assertTrue(ClientSessionFactory.session().selenium()
						.isElementPresent("xpath=//td[contains(@id,'_tzoneSelect')]/div[contains(@class,'ZSelectAutoSizingContainer')]"));
		SelNGBase.needReset.set(false);
	}
	
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefShowReminders() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("all", "na", "34080", " Timezone options changed in mainline so reminder doesn't popluates. Need proper method to get current timezone.");

		page.zCalApp.zNavigateToCalendar();

		page.zCalCompose.zCreateSimpleAppt(getLocalizedData_NoSpecialChar(),
				getLocalizedData_NoSpecialChar(), "",
				getLocalizedData_NoSpecialChar());

		SleepUtil.sleep(2000);

		obj.zDialog.zExists(localize(locator.apptReminders));
		
		obj.zButton.zClickInDlg(localize(locator.dismissAll));
		
		SelNGBase.needReset.set(false);
	}

}
