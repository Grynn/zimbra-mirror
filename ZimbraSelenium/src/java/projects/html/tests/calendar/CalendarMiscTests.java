//Test cases related to calendar appointments
//Krishna Kumar Sure

package projects.html.tests.calendar;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.tools.ant.taskdefs.WaitFor;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.html.tests.CommonTest;
import projects.html.clients.ProvZCS;

@SuppressWarnings( { "static-access", "unused" })
public class CalendarMiscTests extends CommonTest {

	@DataProvider(name = "CreateApptsDataProvider")
	public Object[][] createData(Method method) throws Exception {
		String test = method.getName();

		// final String lastName = getLocalizedData_NoSpecialChar();
		if (test.equals("zNavigateToToday")) {
			return new Object[][] {
					{ "day", getLocalizedData_NoSpecialChar() },
					{ "week", getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("zVerifyPaginationButtons")) {
			return new Object[][] {
					{
							"day",
							"http://"
									+ config.getString("server")
									+ "/zimbra/h/calendar?view=day&date=20081015",
							"http://"
									+ config.getString("server")
									+ "/zimbra/h/calendar?view=day&date=20081016",
							"http://"
									+ config.getString("server")
									+ "/zimbra/h/calendar?view=day&date=20081014" },
					{
							"workWeek",
							"http://"
									+ config.getString("server")
									+ "/zimbra/h/calendar?view=workWeek&date=20081015",
							"http://"
									+ config.getString("server")
									+ "/zimbra/h/calendar?view=workWeek&date=20081022",
							"http://"
									+ config.getString("server")
									+ "/zimbra/h/calendar?view=workWeek&date=20081008" },
					{
							"month",
							"http://"
									+ config.getString("server")
									+ "/zimbra/h/calendar?view=month&date=20081015",
							"http://"
									+ config.getString("server")
									+ "/zimbra/h/calendar?view=month&date=20081115",
							"http://"
									+ config.getString("server")
									+ "/zimbra/h/calendar?view=month&date=20080915" },
					{
							"week",
							"http://"
									+ config.getString("server")
									+ "/zimbra/h/calendar?view=week&date=20081015",
							"http://"
									+ config.getString("server")
									+ "/zimbra/h/calendar?view=week&date=20081022",
							"http://"
									+ config.getString("server")
									+ "/zimbra/h/calendar?view=week&date=20081008" } };
		} else if (test.equals("zCalendarScheduleView")) {
			return new Object[][] { { "check" }, { "uncheck" } };
		} else if (test.equals("zCalPrefInitialView")) {
			return new Object[][] { { localize(locator.calViewDay) },
					{ localize(locator.calViewWorkWeek) },
					{ localize(locator.calViewMonth) },
					{ localize(locator.calViewWeek) } };
		} else
			return new Object[][] { {} };
	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();

		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Verifies the free busy link formed for a calendar
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendFreeBusyLink() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String expectedMsg;
		expectedMsg = "http://" + config.getString("server") + ":80/home/"
				+ selfAccountName + "?fmt=freebusy";

		page.zCalFolderApp.zNavigateToCalendarFoldersPage();

		obj.zButton.zClick(page.zCalFolderApp.calFoldersSendFreeBusyLinkBtn);

		String actualMsg = obj.zTextAreaField.zGetInnerText("id=body");

		Assert.assertTrue(actualMsg.toLowerCase().contains(
				expectedMsg.toLowerCase()),
				"Incorrect free busy link is formed");

		needReset = false;
	}

	/**
	 * Verifies that today button works properly
	 * 
	 * @param view
	 * @param subject
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zNavigateToToday(String view, String subject) throws Exception {

		if (isExecutionARetry)
			handleRetry();
		// ////////////////////////////////////////////
		// Gets the current system date and adds 10 days to it.
		// ///////////////////////////////////////////
		String DATE_FORMAT = "yyyyMMdd";

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

		Calendar testcal = Calendar.getInstance();

		testcal.add(Calendar.DATE, -59);

		String dateToNavigate = sdf.format(testcal.getTime());

		String urlToNavigate = config.getString("mode") + "://"
				+ config.getString("server") + "/zimbra/h/calendar?view="
				+ view + "&date=" + dateToNavigate;

		page.zCalendarApp.zCreateSimpleAppt(subject, "", "", "");

		selenium.open(urlToNavigate);

		Thread.sleep(MEDIUM_WAIT);

		obj.zButton.zClick(page.zCalendarApp.calTodayBtn);

		Thread.sleep(MEDIUM_WAIT);

		obj.zAppointment.zExists(subject);

		needReset = false;
	}

	/**
	 * Verifies the next/prev pagination buttons in calendar app
	 * 
	 * @param view
	 * @param url
	 * @param nextUrl
	 * @param prevUrl
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zVerifyPaginationButtons(String view, String url,
			String nextUrl, String prevUrl) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String browserTitle;
		String browserTitle2;

		selenium.open(url);
		Thread.sleep(MEDIUM_WAIT);

		obj.zButton.zClick(page.zCalendarApp.calNextPageBtn);

		Thread.sleep(MEDIUM_WAIT);

		browserTitle = selenium.getTitle();

		selenium.open(nextUrl);

		Thread.sleep(MEDIUM_WAIT);

		browserTitle2 = selenium.getTitle();

		Assert.assertTrue(browserTitle.equals(browserTitle2),
				"Next pagination button works fine");

		selenium.open(url);
		Thread.sleep(MEDIUM_WAIT);
		obj.zButton.zClick(page.zCalendarApp.calPrevPageBtn);

		Thread.sleep(MEDIUM_WAIT);

		browserTitle = selenium.getTitle();

		selenium.open(prevUrl);

		Thread.sleep(MEDIUM_WAIT);

		browserTitle2 = selenium.getTitle();

		Assert.assertTrue(browserTitle.equals(browserTitle2),
				"Prev pagination button works fine");

		obj.zButton.zClick(page.zCalendarApp.calTodayBtn);

		needReset = false;
	}

	/**
	 * Verifies that calendar editfield is disabled for "Calendar" and enabled
	 * for other calendar folders
	 * 
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zVerifyCalendarEditField() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String calName = getLocalizedData_NoSpecialChar();

		page.zCalFolderApp.zCreateNewCalendar(calName, "");

		// page.zCalFolderApp.zNavigateToCalendarProperties(localize(locator.
		// calendar));
		//		
		// obj.zEditField.zIsDisabled(page.zCalFolderApp.calNameField);

		// String kk =
		// obj.zEditField.zGetInnerHTML(page.zCalFolderApp.calNameField);

		page.zCalFolderApp.zNavigateToCalendarProperties(calName);

		obj.zEditField.zIsEnabled(page.zCalFolderApp.calNameField);

		needReset = false;
	}

	/**
	 * Verifies the schedule view in calendar app
	 * 
	 * @param calCheck
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarScheduleView(String calCheck) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String calName = getLocalizedData_NoSpecialChar();
		String subject1 = getLocalizedData_NoSpecialChar();
		String subject2 = getLocalizedData_NoSpecialChar();

		page.zCalFolderApp.zCreateNewCalendar(calName, "");

		page.zCalendarApp.zCreateSimpleAppt(subject1, "", "", "");

		page.zCalendarApp.zCreateApptInCalendar(subject2, "", "", "", calName);

		Thread.sleep(SMALL_WAIT);

		obj.zButton.zClick(page.zCalendarApp.calScheduleViewBtn);
		Thread.sleep(LONG_WAIT); //timing issue
		obj.zButton.zClick(page.zCalendarApp.calTodayBtn);
		Thread.sleep(SMALL_WAIT);
		
		if (calCheck.equals("uncheck")) {
			obj.zCalendarFolder.zClick(calName);
			Thread.sleep(MEDIUM_WAIT);

			obj.zAppointment.zExists(subject1);
			obj.zAppointment.zNotExists(subject2);

		} else {
			obj.zAppointment.zExists(subject1);
			obj.zAppointment.zExists(subject2);
		}
		needReset = false;
	}

	/**
	 * Verifies the calendar preference of initial view
	 * 
	 * @param initialView
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalPrefInitialView(String initialView) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String browserTitle1, browserTitle2;

		page.zCalendarApp.zSetCalPrefInitialView(initialView);

		page.zCalendarApp.zNavigateToCalendar();

		Thread.sleep(SMALL_WAIT);

		browserTitle1 = selenium.getTitle();

		if (initialView.equals(localize(locator.calViewDay)))
			obj.zButton.zClick(page.zCalendarApp.calDayViewBtn);
		else if (initialView.equals(localize(locator.calViewWorkWeek)))
			obj.zButton.zClick(page.zCalendarApp.calWorkWeekViewBtn);
		else if (initialView.equals(localize(locator.calViewWeek)))
			obj.zButton.zClick(page.zCalendarApp.calWeekViewBtn);
		else if (initialView.equals(localize(locator.calViewMonth)))
			obj.zButton.zClick(page.zCalendarApp.calMonthViewBtn);

		Thread.sleep(MEDIUM_WAIT);

		browserTitle2 = selenium.getTitle();

		Assert.assertTrue(browserTitle1.equals(browserTitle2),
				"Initial view calendar preference doesn't work");

		needReset = false;
	}

	/**
	 * Verifies manager calendars link goes to calendar edit page
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalPrefManageCalendars() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String calName = getLocalizedData_NoSpecialChar();

		page.zCalFolderApp.zCreateNewCalendar(calName, "");
		page.zCalendarApp.zCalPrefManageCalendars();

		obj.zCalendarFolder.zExists(calName);

		obj.zButton.zExists(page.zCalFolderApp.calSaveChangesBtn);

		needReset = false;
	}

	/**
	 * Verifies show timeZone in appointment compose calendar preference
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalPrefShowTimezone() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zSetCalPrefShowTimezone();

		page.zCalendarApp.zNavigateToCalendar();

		page.zCalendarApp.zNavigateToCalendarCompose();

		obj.zHtmlMenu.zExists(page.zCalendarApp.apptComposeTimezoneDropdown);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptSubjectNegativeTest() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateSimpleAppt("", "", "", "");

		obj.zToastAlertMessage
				.zAlertMsgExists(localize(locator.errorMissingSubject),
						"Correct toast message is not thrown on entering blank subject");

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptAttendeeNegativeTest() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateSimpleAppt(getLocalizedData_NoSpecialChar(),
				"", "invalid-address", "");

		Thread.sleep(SMALL_WAIT);

		String ToastMsg = obj.zToastAlertMessage.zGetMsg();

		Thread.sleep(SMALL_WAIT);

		// there is no localize key for 'invalid attendees' string so running
		// only for english as of now
		if (config.getString("locale").equals("en_US")) {
			Assert
					.assertTrue(ToastMsg.trim().contains("Invalid attendees"),
							"Correct toast message is not thrown on entering invalid attendees");
		}

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptDateNegativeTest() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateAppt("test", "", "", "", "", "", "", "",
				"10/09/2008", "10/08/2008", "", "");

		obj.zToastAlertMessage
				.zAlertMsgExists(
						localize(locator.errorInvalidApptEndBeforeStart),
						"Correct toast message is not thrown on entering invalid dates");

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptTimeNegativeTest() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateAppt("test", "", "", "", "", "", "", "",
				"10/09/2008", "10/09/2008", "11:00:AM", "10:00:AM");

		obj.zToastAlertMessage
				.zAlertMsgExists(
						localize(locator.errorInvalidApptEndBeforeStart),
						"Correct toast message is not thrown on entering invalid dates");

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptAddAttendeesTest() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zNavigateToCalendar();
		page.zCalendarApp.zNavigateToCalendarCompose();

		obj.zButton.zClick(page.zCalendarApp.apptComposeAddAttendeesBtn);

		obj.zEditField.zType(
				page.zCalendarApp.apptComposeAddAttendeesFindField, "ccuser");
		obj.zHtmlMenu.zClick(
				page.zCalendarApp.apptComposeAddAttendeesFindDropdown,
				localize(locator.GAL));
		obj.zButton.zClick(page.zCalendarApp.apptComposeAddAttendeesSearchBtn);

		obj.zCheckbox.zClick("name=addAttendees", "1");
		obj.zCheckbox.zClick("name=addAttendees", "2");
		obj.zButton.zClick(page.zCalendarApp.apptComposeAddAttendeesDoneBtn);

		String addAttendees = obj.zTextAreaField
				.zGetInnerText(page.zCalendarApp.apptComposeAttendeesField);

		Assert.assertTrue(addAttendees.contains("ccuser"),
				"Add attendees doesn't add attendees properly");
		Assert.assertTrue(addAttendees.contains("bccuser"),
				"Add attendees doesn't add attendees properly");

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zImportICSTest() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String calName = getLocalizedData_NoSpecialChar();

		String ICSFile = "CalendarToImport.ics";

		page.zCalFolderApp.zCreateNewCalendar(calName, "");

		page.zCalFolderApp.zImportFromICSToCalendar(calName, ICSFile);

		page.zCalendarApp.zNavigateToViewAndDate("day:20090101");
		obj.zAppointment.zExists("jan appt - imported from ICS");

		page.zCalendarApp.zNavigateToViewAndDate("day:20090601");
		obj.zAppointment.zExists("june appt - imported from ICS");

		page.zCalendarApp.zNavigateToViewAndDate("day:20091201");
		obj.zAppointment.zExists("dec appt - imported from ICS");

		needReset = false;
	}

	//--------------------------------------------------------------------------
	// Private functions
	private static void waitForIE() throws Exception {

		if (config.getString("browser").equals("IE"))
			Thread.sleep(MEDIUM_WAIT);
	}

	private static void waitForSF() throws Exception {

		if (config.getString("browser").equals("SF"))
			Thread.sleep(SMALL_WAIT);
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}

}
