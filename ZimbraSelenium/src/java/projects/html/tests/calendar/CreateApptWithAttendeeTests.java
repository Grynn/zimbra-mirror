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
import com.zimbra.common.util.HttpUtil.Browser;

import framework.core.SelNGBase;
import framework.util.BrowserUtil;
import framework.util.RetryFailedTests;

import projects.html.tests.CommonTest;
import projects.html.clients.MiscObject;
import projects.html.clients.ProvZCS;

@SuppressWarnings( { "static-access", "unused" })
public class CreateApptWithAttendeeTests extends CommonTest {

	@DataProvider(name = "CreateApptsWithAttendeeDataProvider")
	public Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		String targetAddressBookFolder = localize(locator.emailedContacts);
		// final String lastName = getLocalizedData_NoSpecialChar();
		if (test.equals("zVerifyApptTimeInAttendeesCalendar")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					ProvZCS.getRandomAccount(), getLocalizedData(3), "9:00:AM",
					"11:00:AM", "12/01/2008", "12/01/2008", "day:20081201", "2" } };
		} else if (test.equals("zVerifyRecurringApptTimeInAttendeesCalendar")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					ProvZCS.getRandomAccount(),
					getLocalizedData_NoSpecialChar(), "7:00:AM", "9:00:AM",
					"07/01/2006", "07/01/2006",
					localize(locator.weekdayWedLong), "1", "", "",
					"week:20080705", "2" } };
		} else if (test.equals("zVerifyAllDayApptInAttendeesCalendar")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					ProvZCS.getRandomAccount(), getLocalizedData(3),
					"01/01/2008", "01/01/2008", "day:20080101" } };
		} else if (test.equals("zVerifyOpenInstanceMessage")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					ProvZCS.getRandomAccount(),
					getLocalizedData_NoSpecialChar(), "03/03/2007",
					"03/03/2007", "10:00:AM", "11:00:AM", "day:20070309" } };
		} else if (test.equals("zModifyApptInstanceAndVerify")
				|| test.equals("zCancelApptInstanceAndVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					ProvZCS.getRandomAccount(),
					getLocalizedData_NoSpecialChar(), "6:00:AM", "8:00:AM",
					"01/01/2008", "01/01/2008", "", "20", "", "8:00:AM",
					"11:00:AM", "day:20080102", "3" } };
		} else if (test.equals("zAddAttendeesToInstanceAndVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					ProvZCS.getRandomAccount(),
					getLocalizedData_NoSpecialChar(), "1:00:PM", "3:00:PM",
					"01/01/2008", "01/01/2008", "1", "", "",
					ProvZCS.getRandomAccount(), "day:20080102" } };
		} else if (test.equals("zModifyApptSeriesAndVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					ProvZCS.getRandomAccount(),
					getLocalizedData_NoSpecialChar(), "8:00:AM", "9:00:AM",
					"01/01/2009", "01/01/2009",
					localize(locator.weekdayMonLong), "1", "", "",
					localize(locator.weekdayTueLong), "9:00:AM", "11:00:AM",
					"day:20090105", "day:20090106", "2" } };
		} else
			return new Object[][] { {} };
	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {

		zLoginIfRequired();

		page.zCalendarApp.zSetCalPrefInitialView(localize(locator.calViewWeek));
		Thread.sleep(SMALL_WAIT);
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
	 *             Creates appointment with attendees and verifies the
	 *             appointment in the attendee calendar
	 */
	@Test(dataProvider = "CreateApptsWithAttendeeDataProvider", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zVerifyApptTimeInAttendeesCalendar(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate,
			String viewAndDateToNavigate, String apptDurationHrs)
			throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateAppt(subject, location, attendees, body, "",
				"", "", "", startDate, endDate, startTime, endTime);

		zKillBrowsers();

		Thread.sleep(SMALL_WAIT);

		SelNGBase.selfAccountName = attendees;

		page.zLoginpage.zLoginToZimbraHTML(attendees);

		
		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		obj.zAppointment.zExists(subject);

		String apptDetails = obj.zCalendarGrid.zGetApptDateTime(subject);

		String apptDetailsToVerify = apptDurationHrs + "_"
				+ page.zCalendarApp.zTimeShortFormat(startTime);

		Assert.assertTrue(apptDetails.replace(" ", "").contains(
				apptDetailsToVerify.replace(" ", "")),
				"Appt details in attendees calendar are incorrect");

		needReset = false;

	}

	@Test(dataProvider = "CreateApptsWithAttendeeDataProvider", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zVerifyRecurringApptTimeInAttendeesCalendar(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate, String day,
			String noEndDate, String endAfterNOccur, String endByDate,
			String viewAndDateToNavigate, String apptDurationHrs)
			throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateApptRepeatDayOfWeek(subject, location,
				attendees, body, startTime, endTime, startDate, endDate, day,
				noEndDate, endAfterNOccur, endByDate);

		zKillBrowsers();

		Thread.sleep(SMALL_WAIT);

		SelNGBase.selfAccountName = attendees;

		page.zLoginpage.zLoginToZimbraHTML(attendees);

		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		obj.zAppointment.zExists(subject);
		obj.zAppointment.zExists(location);

		String apptDetails = obj.zCalendarGrid.zGetApptDateTime(subject);

		String apptDetailsToVerify = apptDurationHrs + "_"
				+ page.zCalendarApp.zTimeShortFormat(startTime);

		Assert.assertTrue(apptDetails.replace(" ", "").contains(
				apptDetailsToVerify.replace(" ", "")),
				"Appt details in attendees calendar are incorrect");

		needReset = false;

	}

	@Test(dataProvider = "CreateApptsWithAttendeeDataProvider", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zVerifyAllDayApptInAttendeesCalendar(String subject,
			String location, String attendees, String body, String startDate,
			String endDate, String viewAndDateToNavigate) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String organizer = selfAccountName;

		page.zCalendarApp.zCreateAllDayAppt(subject, location, attendees, body,
				startDate, endDate);

		zKillBrowsers();

		Thread.sleep(SMALL_WAIT);

		SelNGBase.selfAccountName = attendees;

		page.zLoginpage.zLoginToZimbraHTML(attendees);

		Thread.sleep(MEDIUM_WAIT);

		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);

		String[] itemsToVerify = { subject, organizer.toLowerCase(), location,
				attendees, body };

		page.zCalendarApp.zVerifyInviteContent(localize(locator.apptNew),
				itemsToVerify);

		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		obj.zAppointment.zExists(subject);

		needReset = false;
	}

	@Test(dataProvider = "CreateApptsWithAttendeeDataProvider", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zVerifyOpenInstanceMessage(String subject, String location,
			String attendees, String body, String startDate, String endDate,
			String apptStartTime, String apptEndTime,
			String viewAndDateToNavigate) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalendarApp.zCreateRepeatApptBasicNoEndDate(subject, location,
				attendees, body, startDate, endDate, apptStartTime,
				apptEndTime, localize(locator.everyDay));

		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		Thread.sleep(LONG_WAIT);

		obj.zAppointment.zClick(subject);

		Thread.sleep(MEDIUM_WAIT);

		String messageDisplayed = obj.zMiscObj.zGetInnerText("");

		Assert.assertTrue(messageDisplayed
				.contains(localize(locator.apptInstNote)),
				"Appointment instance note is not displayed");

		Assert.assertTrue(messageDisplayed
				.contains(localize(locator.apptInstEditSeries)),
				"Appointment instance edit series link is not present");

		needReset = false;
	}

	@Test(dataProvider = "CreateApptsWithAttendeeDataProvider", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zModifyApptInstanceAndVerify(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String startDate, String endDate, String noEndDate,
			String endAfterNOccur, String endByDate, String newStartTime,
			String newEndTime, String viewAndDateToNavigate,
			String apptDurationHrs) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String organizer = selfAccountName;

		page.zCalendarApp.zCreateApptRepeatEveryDay(subject, location,
				attendees, body, startTime, endTime, startDate, endDate,
				noEndDate, endAfterNOccur, endByDate);

		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		page.zCalendarApp.zEditAppointment(subject, "", "", "", "", "", "", "",
				"", "", "", newStartTime, newEndTime);

		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		Thread.sleep(VERY_LONG_WAIT); // test continuously fails here. Earlier wait was 7 seconds.

		String apptDetails = obj.zCalendarGrid.zGetApptDateTime(subject);

		String apptDetailsToVerify = apptDurationHrs + "_"
				+ page.zCalendarApp.zTimeShortFormat(newStartTime);

		Assert
				.assertTrue(apptDetails.replace(" ", "").contains(
						apptDetailsToVerify.replace(" ", "")),
						"Single instance modified for time shows the appointment at incorrect time");

		SelNGBase.selfAccountName = attendees;

		zKillBrowsers();

		Thread.sleep(MEDIUM_WAIT);

		page.zLoginpage.zLoginToZimbraHTML(attendees);

		Thread.sleep(MEDIUM_WAIT);

		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);

		obj.zMessageItem.zClick(subject);

		String[] itemsToVerify = { subject, organizer.toLowerCase(), location,
				attendees, body };

		page.zCalendarApp.zVerifyInviteContent(
				localize(locator.apptInstanceModified), itemsToVerify);

		needReset = false;
	}

	@Test(dataProvider = "CreateApptsWithAttendeeDataProvider", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCancelApptInstanceAndVerify(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String startDate, String endDate, String noEndDate,
			String endAfterNOccur, String endByDate, String newStartTime,
			String newEndTime, String viewAndDateToNavigate,
			String apptDurationHrs) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String organizer = selfAccountName;

		page.zCalendarApp.zCreateApptRepeatEveryDay(subject, location,
				attendees, body, startTime, endTime, startDate, endDate,
				noEndDate, endAfterNOccur, endByDate);

		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		obj.zAppointment.zClick(subject);

		obj.zButton.zClick(page.zCalendarApp.apptCancelButton);

		SelNGBase.selfAccountName = attendees;

		zKillBrowsers();

		Thread.sleep(MEDIUM_WAIT);

		page.zLoginpage.zLoginToZimbraHTML(attendees);

		Thread.sleep(MEDIUM_WAIT);

		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);

		String[] itemsToVerify = { subject, organizer.toLowerCase(), location,
				attendees, body };

		page.zCalendarApp.zVerifyInviteContent(
				localize(locator.apptInstanceCancelled), itemsToVerify);

		needReset = false;
	}

	@Test(dataProvider = "CreateApptsWithAttendeeDataProvider", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zAddAttendeesToInstanceAndVerify(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate, String noEndDate,
			String endAfterNOccur, String endByDate, String newAttendees,
			String viewAndDateToNavigate) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String organizer = selfAccountName;

		page.zCalendarApp.zCreateApptRepeatEveryDay(subject, location,
				attendees, body, startTime, endTime, startDate, endDate,
				noEndDate, endAfterNOccur, endByDate);

		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		page.zCalendarApp.zEditAppointment(subject, "", "", attendees + ","
				+ newAttendees, "", "", "", "", "", "", "", "", "");

		SelNGBase.selfAccountName = attendees;

		zKillBrowsers();

		Thread.sleep(MEDIUM_WAIT);

		page.zLoginpage.zLoginToZimbraHTML(attendees);

		Thread.sleep(MEDIUM_WAIT);

		
		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);

		String[] itemsToVerify = { subject, organizer.toLowerCase(), location,
				attendees, newAttendees, body };

		page.zCalendarApp.zVerifyInviteContent(
				localize(locator.apptInstanceModified), itemsToVerify);

		needReset = false;
	}

	@Test(dataProvider = "CreateApptsWithAttendeeDataProvider", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zModifyApptSeriesAndVerify(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String startDate, String endDate, String day, String noEndDate,
			String endAfterNOccur, String endByDate, String newDay,
			String newStartTime, String newEndTime,
			String viewAndDateToNavigate, String newViewAndDateToNavigate,
			String apptDurationHrs) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String organizer = selfAccountName;

		page.zCalendarApp.zCreateApptRepeatDayOfWeek(subject, location,
				attendees, body, startTime, endTime, startDate, endDate, day,
				noEndDate, endAfterNOccur, endByDate);
		Thread.sleep(LONG_WAIT);
		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		obj.zButton.zClick(page.zCalendarApp.calRefresh);
		Thread.sleep(LONG_WAIT);

		obj.zAppointment.zClick(subject);

		Thread.sleep(VERY_LONG_WAIT); // for below statement. Earlier wait was 6 seconds.

		selenium.click("link=" + localize(locator.apptInstEditSeries));

		Thread.sleep(LONG_WAIT); // for below statement

		page.zCalendarApp.zEnterApptDetails("", "", "", "", "", "", "", "", "",
				"", newStartTime, newEndTime);

		page.zCalendarApp.zRepeatSetDayOfWeek(newDay);

		page.zCalendarApp.zRepeatSetEndDate(noEndDate, endAfterNOccur,
				endByDate);
		Thread.sleep(MEDIUM_WAIT);
		obj.zButton.zClick(page.zCalendarApp.apptComposeSaveBtn);

		Thread.sleep(LONG_WAIT);

		page.zCalendarApp.zNavigateToViewAndDate(newViewAndDateToNavigate);
		Thread.sleep(LONG_WAIT);
		String apptDetails = obj.zCalendarGrid.zGetApptDateTime(subject);

		String apptDetailsToVerify = apptDurationHrs + "_"
				+ page.zCalendarApp.zTimeShortFormat(newStartTime);

		Assert.assertTrue(apptDetails.replace(" ", "").contains(
				apptDetailsToVerify.replace(" ", "")),
				"Modified series time shows incorrect time details");

		SelNGBase.selfAccountName = attendees;

		zKillBrowsers();

		Thread.sleep(MEDIUM_WAIT);

		page.zLoginpage.zLoginToZimbraHTML(attendees);

		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);

		Thread.sleep(MEDIUM_WAIT);
		


		obj.zMessageItem.zClick(subject);
		Thread.sleep(MEDIUM_WAIT);
		String[] itemsToVerify = { subject, organizer.toLowerCase(), location,
				attendees, body };

		page.zCalendarApp.zVerifyInviteContent(localize(locator.apptModified),
				itemsToVerify);

		needReset = false;
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}

}
