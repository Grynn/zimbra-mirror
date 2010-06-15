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
public class CalendarPreferencesSetTrueTest extends CommonTest {

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

		String accountName = selfAccountName;

		ProvZCS.modifyAccount(accountName, "zimbraPrefCalendarInitialView",
				"week");

		ProvZCS.modifyAccount(accountName,
				"zimbraPrefCalendarAlwaysShowMiniCal", "TRUE");

		ProvZCS.modifyAccount(accountName, "zimbraPrefCalendarUseQuickAdd",
				"TRUE");

		ProvZCS.modifyAccount(accountName,
				"zimbraPrefUseTimeZoneListInCalendar", "TRUE");

		ProvZCS.modifyAccount(accountName,
				"zimbraPrefCalendarApptReminderWarningTime", "60");

		zReloginToAjax();
//		selenium.refresh();

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
	public void zCalendarPrefShowMiniCal() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		obj.zMiscObj.zExists("DwtCalendar");

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

		zKillBrowsers();

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

	@Test(groups = { "smoke", "full","enabled" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefQuickAdd() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		if(currentBrowserName.indexOf("FF 3")>=0){
			obj.zMiscObj.zActivateByDoubleClick("ZmCalViewMgr/ImgCalendarDayGrid");
		} else {
			obj.zMiscObj.zDblClickXY("ZmCalViewMgr/ImgCalendarDayGrid", "50,50");
		}
		Thread.sleep(1000);

		obj.zDialog.zExists(localize(locator.quickAddAppt));
		
		obj.zButton.zClickInDlg(localize(locator.cancel));

		needReset = false;
	}

	@Test(groups = { "smoke", "full"}, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefShowTimeZone() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		page.zCalApp.zNavigateToApptCompose();
		
		Thread.sleep(1000);

		String timeZone = obj.zMiscObj.zExistsDontWait("ZmApptComposeView ZWidget/*tzoneSelect/ZSelectAutoSizingContainer ZHasDropDown");
		
		Assert.assertEquals(timeZone, "true", "Timezone menu doesn't exist on setting the ShowTimeZone pref to True");
		
		needReset = false;
	}
	
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarPrefShowReminders() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		page.zCalCompose.zCreateSimpleAppt(getLocalizedData_NoSpecialChar(),
				getLocalizedData_NoSpecialChar(), "",
				getLocalizedData_NoSpecialChar());

		Thread.sleep(2000);

		obj.zDialog.zExists(localize(locator.apptReminders));
		
		obj.zButton.zClickInDlg(localize(locator.dismissAll));
		
		needReset = false;
	}


	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}

}
