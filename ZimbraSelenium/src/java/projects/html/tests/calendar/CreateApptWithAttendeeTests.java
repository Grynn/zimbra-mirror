//Test cases related to calendar appointments
//Krishna Kumar Sure

package projects.html.tests.calendar;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.html.tests.CommonTest;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;

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
					Stafzmprov.getRandomAccount(), getLocalizedData(3), "9:00:AM",
					"11:00:AM", "12/01/2008", "12/01/2008", "day:20081201", "2" } };
		} else if (test.equals("zVerifyRecurringApptTimeInAttendeesCalendar")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					Stafzmprov.getRandomAccount(),
					getLocalizedData_NoSpecialChar(), "7:00:AM", "9:00:AM",
					"07/01/2006", "07/01/2006",
					localize(locator.weekdayWedLong), "1", "", "",
					"week:20080705", "2" } };
		} else if (test.equals("zVerifyAllDayApptInAttendeesCalendar")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					Stafzmprov.getRandomAccount(), getLocalizedData(3),
					"01/01/2008", "01/01/2008", "day:20080101" } };
		} else if (test.equals("zVerifyOpenInstanceMessage")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					Stafzmprov.getRandomAccount(),
					getLocalizedData_NoSpecialChar(), "03/03/2007",
					"03/03/2007", "10:00:AM", "11:00:AM", "day:20070309" } };
		} else if (test.equals("zModifyApptInstanceAndVerify")
				|| test.equals("zCancelApptInstanceAndVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					Stafzmprov.getRandomAccount(),
					getLocalizedData_NoSpecialChar(), "6:00:AM", "8:00:AM",
					"01/01/2008", "01/01/2008", "", "20", "", "8:00:AM",
					"11:00:AM", "day:20080102", "3" } };
		} else if (test.equals("zAddAttendeesToInstanceAndVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					Stafzmprov.getRandomAccount(),
					getLocalizedData_NoSpecialChar(), "1:00:PM", "3:00:PM",
					"01/01/2008", "01/01/2008", "1", "", "",
					Stafzmprov.getRandomAccount(), "day:20080102" } };
		} else if (test.equals("zModifyApptSeriesAndVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					Stafzmprov.getRandomAccount(),
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
		SleepUtil.sleepSmall();
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
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

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalendarApp.zCreateAppt(subject, location, attendees, body, "",
				"", "", "", startDate, endDate, startTime, endTime);

		resetSession();

		SleepUtil.sleepSmall();

		SelNGBase.selfAccountName.set(attendees);

		page.zLoginpage.zLoginToZimbraHTML(attendees);

		
		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		obj.zAppointment.zExists(subject);

		String apptDetails = obj.zCalendarGrid.zGetApptDateTime(subject);

		String apptDetailsToVerify = apptDurationHrs + "_"
				+ page.zCalendarApp.zTimeShortFormat(startTime);

		Assert.assertTrue(apptDetails.replace(" ", "").contains(
				apptDetailsToVerify.replace(" ", "")),
				"Appt details in attendees calendar are incorrect");

		SelNGBase.needReset.set(false);

	}

	@Test(dataProvider = "CreateApptsWithAttendeeDataProvider", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zVerifyRecurringApptTimeInAttendeesCalendar(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate, String day,
			String noEndDate, String endAfterNOccur, String endByDate,
			String viewAndDateToNavigate, String apptDurationHrs)
			throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalendarApp.zCreateApptRepeatDayOfWeek(subject, location,
				attendees, body, startTime, endTime, startDate, endDate, day,
				noEndDate, endAfterNOccur, endByDate);

		resetSession();

		SleepUtil.sleepSmall();

		SelNGBase.selfAccountName.set(attendees);

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

		SelNGBase.needReset.set(false);

	}

	@Test(dataProvider = "CreateApptsWithAttendeeDataProvider", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zVerifyAllDayApptInAttendeesCalendar(String subject,
			String location, String attendees, String body, String startDate,
			String endDate, String viewAndDateToNavigate) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String organizer = SelNGBase.selfAccountName.get();

		page.zCalendarApp.zCreateAllDayAppt(subject, location, attendees, body,
				startDate, endDate);

		resetSession();

		SleepUtil.sleepSmall();

		SelNGBase.selfAccountName.set(attendees);

		page.zLoginpage.zLoginToZimbraHTML(attendees);

		SleepUtil.sleepMedium();

		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);

		String[] itemsToVerify = { subject, organizer.toLowerCase(), location,
				attendees, body };

		page.zCalendarApp.zVerifyInviteContent(localize(locator.apptNew),
				itemsToVerify);

		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		obj.zAppointment.zExists(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "CreateApptsWithAttendeeDataProvider", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zVerifyOpenInstanceMessage(String subject, String location,
			String attendees, String body, String startDate, String endDate,
			String apptStartTime, String apptEndTime,
			String viewAndDateToNavigate) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalendarApp.zCreateRepeatApptBasicNoEndDate(subject, location,
				attendees, body, startDate, endDate, apptStartTime,
				apptEndTime, localize(locator.everyDay));

		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		SleepUtil.sleepLong();

		obj.zAppointment.zClick(subject);

		SleepUtil.sleepMedium();
		
		SelNGBase.selenium.get().isElementPresent(localize(locator.apptInstEditSeries));
		SelNGBase.selenium.get().isElementPresent(localize(locator.apptInstNote));

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "CreateApptsWithAttendeeDataProvider", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zModifyApptInstanceAndVerify(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String startDate, String endDate, String noEndDate,
			String endAfterNOccur, String endByDate, String newStartTime,
			String newEndTime, String viewAndDateToNavigate,
			String apptDurationHrs) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String organizer = SelNGBase.selfAccountName.get();

		page.zCalendarApp.zCreateApptRepeatEveryDay(subject, location,
				attendees, body, startTime, endTime, startDate, endDate,
				noEndDate, endAfterNOccur, endByDate);

		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		page.zCalendarApp.zEditAppointment(subject, "", "", "", "", "", "", "",
				"", "", "", newStartTime, newEndTime);

		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		SleepUtil.sleepVeryLong(); // test continuously fails here. Earlier wait was 7 seconds.

		String apptDetails = obj.zCalendarGrid.zGetApptDateTime(subject);

		String apptDetailsToVerify = apptDurationHrs + "_"
				+ page.zCalendarApp.zTimeShortFormat(newStartTime);

		Assert
				.assertTrue(apptDetails.replace(" ", "").contains(
						apptDetailsToVerify.replace(" ", "")),
						"Single instance modified for time shows the appointment at incorrect time");

		SelNGBase.selfAccountName.set(attendees);

		resetSession();

		SleepUtil.sleepMedium();

		page.zLoginpage.zLoginToZimbraHTML(attendees);

		SleepUtil.sleepMedium();

		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);

		obj.zMessageItem.zClick(subject);

		String[] itemsToVerify = { subject, organizer.toLowerCase(), location,
				attendees, body };

		page.zCalendarApp.zVerifyInviteContent(
				localize(locator.apptInstanceModified), itemsToVerify);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "CreateApptsWithAttendeeDataProvider", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCancelApptInstanceAndVerify(String subject, String location,
			String attendees, String body, String startTime, String endTime,
			String startDate, String endDate, String noEndDate,
			String endAfterNOccur, String endByDate, String newStartTime,
			String newEndTime, String viewAndDateToNavigate,
			String apptDurationHrs) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String organizer = SelNGBase.selfAccountName.get();

		page.zCalendarApp.zCreateApptRepeatEveryDay(subject, location,
				attendees, body, startTime, endTime, startDate, endDate,
				noEndDate, endAfterNOccur, endByDate);

		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		obj.zAppointment.zClick(subject);

		obj.zButton.zClick(page.zCalendarApp.apptCancelButton);

		SelNGBase.selfAccountName.set(attendees);

		resetSession();

		SleepUtil.sleepMedium();

		page.zLoginpage.zLoginToZimbraHTML(attendees);

		SleepUtil.sleepMedium();

		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);

		String[] itemsToVerify = { subject, organizer.toLowerCase(), location,
				attendees, body };

		page.zCalendarApp.zVerifyInviteContent(
				localize(locator.apptInstanceCancelled), itemsToVerify);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "CreateApptsWithAttendeeDataProvider", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zAddAttendeesToInstanceAndVerify(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate, String noEndDate,
			String endAfterNOccur, String endByDate, String newAttendees,
			String viewAndDateToNavigate) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String organizer = SelNGBase.selfAccountName.get();

		page.zCalendarApp.zCreateApptRepeatEveryDay(subject, location,
				attendees, body, startTime, endTime, startDate, endDate,
				noEndDate, endAfterNOccur, endByDate);

		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		page.zCalendarApp.zEditAppointment(subject, "", "", attendees + ","
				+ newAttendees, "", "", "", "", "", "", "", "", "");

		SelNGBase.selfAccountName.set(attendees);

		resetSession();

		SleepUtil.sleepMedium();

		page.zLoginpage.zLoginToZimbraHTML(attendees);

		SleepUtil.sleepMedium();

		
		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);

		String[] itemsToVerify = { subject, organizer.toLowerCase(), location,
				attendees, newAttendees, body };

		page.zCalendarApp.zVerifyInviteContent(
				localize(locator.apptInstanceModified), itemsToVerify);

		SelNGBase.needReset.set(false);
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

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String organizer = SelNGBase.selfAccountName.get();

		page.zCalendarApp.zCreateApptRepeatDayOfWeek(subject, location,
				attendees, body, startTime, endTime, startDate, endDate, day,
				noEndDate, endAfterNOccur, endByDate);
		SleepUtil.sleepLong();
		page.zCalendarApp.zNavigateToViewAndDate(viewAndDateToNavigate);

		obj.zButton.zClick(page.zCalendarApp.calRefresh);
		SleepUtil.sleepLong();

		obj.zAppointment.zClick(subject);

		SleepUtil.sleepVeryLong(); // for below statement. Earlier wait was 6 seconds.

		SelNGBase.selenium.get().click("link=" + localize(locator.apptInstEditSeries));

		SleepUtil.sleepLong(); // for below statement

		page.zCalendarApp.zEnterApptDetails("", "", "", "", "", "", "", "", "",
				"", newStartTime, newEndTime);

		page.zCalendarApp.zRepeatSetDayOfWeek(newDay);

		page.zCalendarApp.zRepeatSetEndDate(noEndDate, endAfterNOccur,
				endByDate);
		SleepUtil.sleepMedium();
		obj.zButton.zClick(page.zCalendarApp.apptComposeSaveBtn);

		SleepUtil.sleepLong();

		page.zCalendarApp.zNavigateToViewAndDate(newViewAndDateToNavigate);
		SleepUtil.sleepLong();
		String apptDetails = obj.zCalendarGrid.zGetApptDateTime(subject);

		String apptDetailsToVerify = apptDurationHrs + "_"
				+ page.zCalendarApp.zTimeShortFormat(newStartTime);

		Assert.assertTrue(apptDetails.replace(" ", "").contains(
				apptDetailsToVerify.replace(" ", "")),
				"Modified series time shows incorrect time details");

		SelNGBase.selfAccountName.set(attendees);

		resetSession();

		SleepUtil.sleepMedium();

		page.zLoginpage.zLoginToZimbraHTML(attendees);

		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);

		SleepUtil.sleepMedium();
		


		obj.zMessageItem.zClick(subject);
		SleepUtil.sleepMedium();
		String[] itemsToVerify = { subject, organizer.toLowerCase(), location,
				attendees, body };

		page.zCalendarApp.zVerifyInviteContent(localize(locator.apptModified),
				itemsToVerify);

		SelNGBase.needReset.set(false);
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}

}
