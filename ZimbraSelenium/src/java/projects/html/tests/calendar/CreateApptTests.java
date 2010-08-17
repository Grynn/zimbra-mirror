//Test cases related to calendar appointments
//Krishna Kumar Sure

package projects.html.tests.calendar;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import framework.util.SleepUtil;
import framework.util.RetryFailedTests;
import framework.util.ZimbraSeleniumProperties;

import projects.html.tests.CommonTest;
import projects.html.clients.ProvZCS;

@SuppressWarnings( { "static-access", "unused" })
public class CreateApptTests extends CommonTest {

	@DataProvider(name = "CreateApptsDataProvider")
	public Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		String targetAddressBookFolder = localize(locator.emailedContacts);
		// final String lastName = getLocalizedData_NoSpecialChar();
		if (test.equals("createSimpleApptAndVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					ProvZCS.getRandomAccount(), getLocalizedData(3), "8:00:AM",
					"9:00:AM", "1" } };
		} else if (test.equals("verifyApptTimeInAllViews")) {
			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), "",
							getLocalizedData_NoSpecialChar(), "8:00:AM",
							"10:00:AM", "2", "day" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), "",
							getLocalizedData_NoSpecialChar(), "10:00:AM",
							"11:00:AM", "1", "week" } };
		} else if (test.equals("zEditAppointmentVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar(), "1:00:PM", "2:00:PM",
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar(), "2:00:PM", "3:00:PM", "1" } };
		} else if (test.equals("zCreateRepeatAppt")) {
			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), "",
							getLocalizedData(3), "01/01/2008", "01/01/2008",
							"8:00:AM", "9:00:AM",
							localize(locator.recurBasicSelectDaily),
							"day:20091001" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), "",
							getLocalizedData(3), "01/05/2008", "01/05/2008",
							"9:00:AM", "10:00:AM",
							localize(locator.recurBasicSelectWeekly),
							"week:20100105" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), "",
							getLocalizedData(3), "01/10/2008", "01/10/2008",
							"10:00:AM", "11:00:AM",
							localize(locator.recurBasicSelectMonthly),
							"week:20100310" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), "",
							getLocalizedData(3), "01/15/2008", "01/15/2008",
							"1:00:PM", "2:00:PM",
							localize(locator.recurBasicSelectYearly),
							"week:20110115" } };
		} else if (test.equals("zApptEveryWeekDayVerifyMultipleInstances")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar(), "1:00:PM", "2:00:PM",
					"01/01/2008", "01/01/2008", "1", "", "", "week:20091010",
					"day:20091004" } };
		} else if (test.equals("zApptEveryNDaysVerifyMultipleInstances")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar(), "3:00:PM", "4:00:PM",
					"12/31/2006", "12/31/2006", "2", "", "4", "",
					"week:20070101", "day:20070109" } };
		} else if (test.equals("zApptEveryDayEndByDate")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar(), "3:00:PM", "4:00:PM",
					"01/01/2006", "01/01/2006", "", "", "01/03/2006",
					"week:20060101", "day:20060104" } };
		} else if (test.equals("zApptEveryDayOfWeek")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar(), "3:00:PM", "4:00:PM",
					"05/01/2006", "05/01/2006",
					localize(locator.weekdayFriLong), "1", "", "",
					"day:20060804", "day:20060811" } };
		} else if (test.equals("zApptEveryDayOfWeekInterval")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar(), "10:00:AM", "11:00:AM",
					"10/02/2006", "10/02/2006", "2", "Mon", "", "2", "",
					"day:20061016", "day:20061030" } };
		} else if (test.equals("zApptEveryDayOfMonth")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar(), "10:00:AM", "11:00:AM",
					"10/05/2007", "10/05/2007", "5", "1", "1", "", "",
					"day:20071105", "day:20071205" } };
		} else if (test.equals("zApptEveryNthDayOfMonth")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar(), "9:00:AM", "10:00:AM",
					"01/03/2008", "01/03/2008", localize(locator.recurFirst),
					localize(locator.weekdayThuLong), "1", "1", "", "",
					"day:20080207", "day:20080306" } };
		} else if (test.equals("zApptEveryYearlyDay")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar(), "1:00:PM", "2:00:PM",
					"05/01/2005", "05/01/2005", localize(locator.monthMayLong),
					"1", "1", "", "", "day:20090501", "day:20100501" } };
		} else if (test.equals("zApptEveryYearlyRelative")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar(), "9:00:AM", "10:00:AM",
					"06/01/2002", "06/01/2002", localize(locator.recurSecond),
					localize(locator.weekdayWedLong),
					localize(locator.monthJunLong), "1", "", "",
					"day:20080611", "day:20090610" } };
		} else
			return new Object[][] { {} };
	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {

		zLoginIfRequired();

		SleepUtil.sleepSmall();

		page.zCalendarApp.zSetCalPrefInitialView(localize(locator.calViewWeek));
		SleepUtil.sleepSmall();
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
	 * @throws Exception
	 *             Verify creation of simple appointment, verify correct time
	 *             and other details
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createSimpleApptAndVerify(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String apptDurationHrs) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String[] startTimeArr = startTime.split(":");

		page.zCalendarApp.zCreateAppt(subject, location, attendees, body, "",
				"", "", "", "", "", startTime, endTime);

		obj.zAppointment.zExists(subject);

		String apptDetails = obj.zCalendarGrid.zGetApptDateTime(subject);

		String apptDetailsToVerify = apptDurationHrs + "_"
				+ page.zCalendarApp.zTimeShortFormat(startTime);

		Assert.assertTrue(apptDetails.replace(" ", "").contains(
				apptDetailsToVerify.replace(" ", "")),
				"Appt details are incorrect");

		page.zCalendarApp.zVerifySimpleApptDetails(subject, location,
				attendees, body);

		needReset = false;

	}

	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full"}, retryAnalyzer = RetryFailedTests.class)
	public void verifyApptTimeInAllViews(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String apptDurationHrs, String view) throws Exception {

		String apptDetailsToVerify = "";
		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateAppt(subject, location, attendees, body, "",
				"", "", "", "", "", startTime, endTime);

		if (view.equals("day")) {
			obj.zButton.zClick(page.zCalendarApp.calDayViewBtn);
			apptDetailsToVerify = apptDurationHrs + "_"
					+ page.zCalendarApp.zTimeShortFormat(startTime);
		} else if (view.equals("week")) {
			obj.zButton.zClick(page.zCalendarApp.calWeekViewBtn);
			apptDetailsToVerify = apptDurationHrs + "_"
					+ page.zCalendarApp.zTimeShortFormat(startTime);
		} else if (view.equals("month")) {
			obj.zButton.zClick(page.zCalendarApp.calMonthViewBtn);
			apptDetailsToVerify = "N/A_N/A";
		}

		obj.zButton.zClick(page.zCalendarApp.calTodayBtn);

		SleepUtil.sleepSmall();

		String apptDetails = obj.zCalendarGrid.zGetApptDateTime(subject);

		// String apptDetailsToVerify = apptDurationHrs + "_"
		// + page.zCalendarApp.zTimeShortFormat(startTime);

		Assert.assertTrue(apptDetails.replace(" ", "").contains(
				apptDetailsToVerify.replace(" ", "")),
				"Appt details are incorrect");

		needReset = false;
	}

	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zEditAppointmentVerify(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String newSubject, String newLocation, String newAttendees,
			String newBody, String newStartTime, String newEndTime,
			String apptDurationHrs) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateAppt(subject, location, attendees, body, "",
				"", "", "", "", "", startTime, endTime);

		obj.zAppointment.zExists(subject);

		page.zCalendarApp.zEditAppointment(subject, newSubject, newLocation,
				newAttendees, newBody, "", "", "", "", "", "", newStartTime,
				newEndTime);

		String apptDetails = obj.zCalendarGrid.zGetApptDateTime(newSubject);

		String apptDetailsToVerify = apptDurationHrs + "_"
				+ page.zCalendarApp.zTimeShortFormat(newStartTime);

		Assert.assertTrue(apptDetails.replace(" ", "").contains(
				apptDetailsToVerify.replace(" ", "")),
				"Appt details are incorrect");

		page.zCalendarApp.zVerifySimpleApptDetails(newSubject, newLocation,
				newAttendees, newBody);

		needReset = false;

	}

	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full",
			"" }, retryAnalyzer = RetryFailedTests.class)
	public void zCreateAllDayAppt() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String subject = getLocalizedData_NoSpecialChar();
		String location = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData(3);

		page.zCalendarApp
				.zCreateAllDayAppt(subject, location, "", body, "", "");

		obj.zAppointment.zExists(subject);
	}

	// Creates basic repeats with no end dates and verifies at different url's
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCreateRepeatAppt(String subject, String location,
			String attendees, String body, String apptStartDate,
			String apptEndDate, String apptStartTime, String apptEndTime,
			String repeatType, String viewAndDateToVerify) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateRepeatApptBasicNoEndDate(subject, location,
				attendees, body, apptStartDate, apptEndDate, apptStartTime,
				apptEndTime, repeatType);

		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToVerify);

		obj.zAppointment.zExists(subject);

		needReset = false;
	}

	// Creates Everyday appointment with no end date and verifies multiple
	// instances of the appointment
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptEveryDayVerifyMultipleInstances() throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String subject = getLocalizedData_NoSpecialChar();
		String location = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData(2);
		String apptStartDate = "05/05/2009";
		String apptEndDate = "05/05/2009";
		String startTime = "1:00:PM";
		String endTime = "2:00:PM";

		page.zCalendarApp.zCreateRepeatApptBasicNoEndDate(subject, location,
				"", body, apptStartDate, apptEndDate, startTime, endTime,
				localize(locator.recurBasicSelectDaily));

		selenium.open("http://" + ZimbraSeleniumProperties.getStringProperty("server")
				+ "/zimbra/h/calendar?view=day&date=20090601");

		SleepUtil.sleepMedium();

		obj.zAppointment.zExists(subject);

		selenium.open("http://" + ZimbraSeleniumProperties.getStringProperty("server")
				+ "/zimbra/h/calendar?view=day&date=20090702");

		SleepUtil.sleepMedium();

		obj.zAppointment.zExists(subject);

		needReset = false;
	}

	// Creates a recurring appointment repeating every weekday
	// Verifies appointments are not created on sunday
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptEveryWeekDayVerifyMultipleInstances(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate, String noEndDate,
			String endAfterNOccur, String endByDate, String existsViewAndDate,
			String notExistViewAndDate) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateApptRepeatEveryWeekDay(subject, location,
				attendees, body, startTime, endTime, startDate, endDate,
				noEndDate, endAfterNOccur, endByDate);

		page.zCalendarApp.zNavigateToViewAndDate(existsViewAndDate);

		Assert.assertTrue(obj.zCalendarGrid.zGetApptCount(subject).equals("5"),
				"Every weekday appoinment shows wrong number of appointments");

		page.zCalendarApp.zNavigateToViewAndDate(notExistViewAndDate);

		obj.zAppointment.zNotExists(subject);

		needReset = false;
	}

	// Creates appointment with repeat at some interval of days
	// Verifies correct number of instances are created
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptEveryNDaysVerifyMultipleInstances(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate,
			String daysInterval, String noEndDate, String endAfterNOccur,
			String endByDate, String existsViewAndDate,
			String notExistViewAndDate) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateApptRepeatEveryDaysInterval(subject, location,
				attendees, body, startTime, endTime, startDate, endDate,
				daysInterval, noEndDate, endAfterNOccur, endByDate);

		page.zCalendarApp.zNavigateToViewAndDate(existsViewAndDate);

		Assert.assertTrue(obj.zCalendarGrid.zGetApptCount(subject).equals("4"),
				"Every N days appoinment shows wrong number of appointments");

		page.zCalendarApp.zNavigateToViewAndDate(notExistViewAndDate);

		obj.zAppointment.zNotExists(subject);

		needReset = false;
	}

	// Creates a recurring appointment that ends by a particular date and
	// verifies the number of instances
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptEveryDayEndByDate(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String startDate, String endDate, String noEndDate,
			String endAfterNOccur, String endByDate, String existsViewAndDate,
			String notExistViewAndDate) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateApptRepeatEveryDay(subject, location,
				attendees, body, startTime, endTime, startDate, endDate,
				noEndDate, endAfterNOccur, endByDate);

		page.zCalendarApp.zNavigateToViewAndDate(existsViewAndDate);

		obj.zButton.zClick(page.zCalendarApp.calRefresh);

		Assert
				.assertTrue(obj.zCalendarGrid.zGetApptCount(subject)
						.equals("3"),
						"Every day appointment with end by date shows incorrect number of appointments");

		page.zCalendarApp.zNavigateToViewAndDate(notExistViewAndDate);

		obj.zAppointment.zNotExists(subject);

		needReset = false;
	}

	// Creates a recurring appointment that ends by a particular date and
	// verifies the number of instances
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptEveryDayOfWeek(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String startDate, String endDate, String day, String noEndDate,
			String endAfterNOccur, String endByDate, String existsViewAndDate,
			String existsViewAndDate2) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateApptRepeatDayOfWeek(subject, location,
				attendees, body, startTime, endTime, startDate, endDate, day,
				noEndDate, endAfterNOccur, endByDate);
		page.zCalendarApp.zNavigateToViewAndDate(existsViewAndDate);
		obj.zAppointment.zExists(subject);
		page.zCalendarApp.zNavigateToViewAndDate(existsViewAndDate2);
		obj.zAppointment.zExists(subject);

		needReset = false;
	}

	// Creates appointment on specified day of every week n weeks
	// Verifies correct number of instances
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptEveryDayOfWeekInterval(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String startDate, String endDate, String weekInterval,
			String repeatDayInEnglishShort, String noEndDate,
			String endAfterNOccur, String endByDate, String existsViewAndDate,
			String notExistsViewAndDate) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateApptRepeatWeeklyInterval(subject, location,
				attendees, body, startTime, endTime, startDate, endDate,
				weekInterval, repeatDayInEnglishShort, noEndDate,
				endAfterNOccur, endByDate);

		page.zCalendarApp.zNavigateToViewAndDate(existsViewAndDate);

		obj.zAppointment.zExists(subject);

		page.zCalendarApp.zNavigateToViewAndDate(notExistsViewAndDate);

		obj.zAppointment.zNotExists(subject);

		needReset = false;
	}

	// Creates appointment on every particular day of given month interval
	// Verifies two instances of the appointment
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptEveryDayOfMonth(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String startDate, String endDate, String dayNumber,
			String monthInterval, String noEndDate, String endAfterNOccur,
			String endByDate, String existsViewAndDate,
			String existsViewAndDate2) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateApptRepeatDayOfMonthInterval(subject,
				location, attendees, body, startTime, endTime, startDate,
				endDate, dayNumber, monthInterval, noEndDate, endAfterNOccur,
				endByDate);

		page.zCalendarApp.zNavigateToViewAndDate(existsViewAndDate);

		obj.zAppointment.zExists(subject);

		page.zCalendarApp.zNavigateToViewAndDate(existsViewAndDate2);

		obj.zAppointment.zExists(subject);

		needReset = false;
	}

	// Creates appointment on every Nth particular day of month
	// Verifies multiple instances
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptEveryNthDayOfMonth(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String startDate, String endDate, String NthValue, String day,
			String monthInterval, String noEndDate, String endAfterNOccur,
			String endByDate, String existsViewAndDate,
			String existsViewAndDate2) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateApptRepeatEveryNthDayOfMonthInterval(subject,
				location, attendees, body, startTime, endTime, startDate,
				endDate, NthValue, day, monthInterval, noEndDate,
				endAfterNOccur, endByDate);

		page.zCalendarApp.zNavigateToViewAndDate(existsViewAndDate);

		obj.zAppointment.zExists(subject);

		page.zCalendarApp.zNavigateToViewAndDate(existsViewAndDate2);

		obj.zAppointment.zExists(subject);

		needReset = false;
	}

	// Creates appointment on every particular day of year
	// Verifies multiple instances
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptEveryYearlyDay(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String startDate, String endDate, String repeatMonth,
			String dayValue, String noEndDate, String endAfterNOccur,
			String endByDate, String existsViewAndDate,
			String existsViewAndDate2) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateApptRepeatYearlyDay(subject, location,
				attendees, body, startTime, endTime, startDate, endDate,
				repeatMonth, dayValue, noEndDate, endAfterNOccur, endByDate);

		page.zCalendarApp.zNavigateToViewAndDate(existsViewAndDate);

		obj.zAppointment.zExists(subject);

		page.zCalendarApp.zNavigateToViewAndDate(existsViewAndDate2);

		obj.zAppointment.zExists(subject);

		needReset = false;
	}

	// Creates appointment on every Nth particular day of given month of every
	// year
	// Verifies multiple instances
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptEveryYearlyRelative(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String startDate, String endDate, String nthDayOfMonth, String day,
			String repeatMonth, String noEndDate, String endAfterNOccur,
			String endByDate, String existsViewAndDate,
			String existsViewAndDate2) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateApptRepeatYearlyRelative(subject, location,
				attendees, body, startTime, endTime, startDate, endDate,
				nthDayOfMonth, day, repeatMonth, noEndDate, endAfterNOccur,
				endByDate);

		page.zCalendarApp.zNavigateToViewAndDate(existsViewAndDate);

		obj.zAppointment.zExists(subject);

		page.zCalendarApp.zNavigateToViewAndDate(existsViewAndDate2);

		obj.zAppointment.zExists(subject);

		needReset = false;
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}

}
