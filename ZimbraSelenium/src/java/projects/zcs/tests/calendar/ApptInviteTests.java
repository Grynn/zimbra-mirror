package projects.zcs.tests.calendar;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.tools.ant.taskdefs.WaitFor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.service.admin.GetConfig;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class ApptInviteTests extends CommonTest {

	@DataProvider(name = "apptInviteTestDataProvider")
	private Object[][] createData(Method method) throws Exception {

		String test = method.getName();

		if (test.equals("acceptDeclineTentativeAppt_Bug42832")) {

			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(1),
							ProvZCS.getRandomAccount(), getLocalizedData(3),
							"accept" },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(1),
							ProvZCS.getRandomAccount(), getLocalizedData(3),
							"decline" },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(1),
							ProvZCS.getRandomAccount(), getLocalizedData(3),
							"tentative" } };

		} else if (test.equals("apptInviteContentVerify")) {

			return new Object[][] {

			{ getLocalizedData_NoSpecialChar(), getLocalizedData(1),
					ProvZCS.getRandomAccount(), getLocalizedData(3) } };

		} else if (test.equals("apptRecurringInviteContentVerify")) {

			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), localize(locator.everyWeek),
					ProvZCS.getRandomAccount(), getLocalizedData(3) } };

		} else if (test.equals("apptModifyInviteCheckContent")) {
			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							ProvZCS.getRandomAccount(), getLocalizedData(3),
							"10:00:" + localize(locator.periodAm),
							"11:00:" + localize(locator.periodAm),
							getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), "", "" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							ProvZCS.getRandomAccount(), getLocalizedData(3),
							"10:00:" + localize(locator.periodAm),
							"11:00:" + localize(locator.periodAm), "", "",
							"11:00:" + localize(locator.periodAm),
							"12:00:" + localize(locator.periodPm) } };
		} else if (test.equals("deleteAppt_bug38150")) {
			return new Object[][] { { "single",
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					ProvZCS.getRandomAccount(), getLocalizedData(3), "" } };
		} else if (test.equals("apptModifyRecurringCheckContent")) {

			return new Object[][] {
					{ "instance", getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							ProvZCS.getRandomAccount(), getLocalizedData(3),
							localize(locator.everyWeek),
							"10:00:" + localize(locator.periodAm),
							"11:00:" + localize(locator.periodAm),
							getLocalizedData_NoSpecialChar(), "",
							"11:00:" + localize(locator.periodAm),
							"12:00:" + localize(locator.periodPm) },
					{ "series", getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							ProvZCS.getRandomAccount(), getLocalizedData(3),
							localize(locator.everyWeek),
							"10:00:" + localize(locator.periodAm),
							"11:00:" + localize(locator.periodAm),
							getLocalizedData_NoSpecialChar(), "",
							"11:00:" + localize(locator.periodAm),
							"12:00:" + localize(locator.periodPm) }, };

		} else if (test.equals("apptDeleteInviteCheckContent")) {
			return new Object[][] {
					{ "single", getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							ProvZCS.getRandomAccount(), getLocalizedData(3), "" },
					{ "instance", getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							ProvZCS.getRandomAccount(), getLocalizedData(3),
							localize(locator.everyDay) },
					{ "series", getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							ProvZCS.getRandomAccount(), getLocalizedData(3),
							localize(locator.everyWeek) } };
		} else if (test.equals("apptAddRemoveAttendeeCheckContent")) {

			return new Object[][] {
					{ "single", getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							ProvZCS.getRandomAccount(), getLocalizedData(3),
							"", ProvZCS.getRandomAccount() },
					{ "instance", getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							ProvZCS.getRandomAccount(), getLocalizedData(3),
							localize(locator.everyDay),
							ProvZCS.getRandomAccount() },
					{ "series", getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							ProvZCS.getRandomAccount(), getLocalizedData(3),
							localize(locator.everyWeek),
							ProvZCS.getRandomAccount() } };

		} else if (test.equals("verifyNumberOfRecurringItems")) {

			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					ProvZCS.getRandomAccount(), getLocalizedData(3),
					localize(locator.everyDay) } };

		} else {
			return new Object[][] { { getLocalizedData(1), getLocalizedData(1),
					localize(locator.low), getLocalizedData(3), "", } };
		}

	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		Thread.sleep(2000);
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Sends meeting invite to attendees and verifies that attendee can
	 * Accept/Decline/Tentative the appointment
	 */
	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void acceptDeclineTentativeAppt_Bug42832(String subject,
			String location, String attendees, String body, String action)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);

		zKillBrowsers();
		Thread.sleep(1000);
		SelNGBase.selfAccountName = attendees;
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		if (action.equals("accept"))
			page.zCalApp.zAcceptInvite(subject);
		Thread.sleep(1500);
		obj.zMessageItem.zNotExists(subject);
		obj.zButton.zNotExists(localize(locator.replyAccept));
		if (action.equals("decline"))
			page.zCalApp.zDeclineInvite(subject);
		Thread.sleep(1500);
		obj.zMessageItem.zNotExists(subject);
		obj.zButton.zNotExists(localize(locator.replyDecline));
		if (action.equals("tentative"))
			page.zCalApp.zTentativeInvite(subject);
		Thread.sleep(1500);
		obj.zMessageItem.zNotExists(subject);
		obj.zButton.zNotExists(localize(locator.replyTentative));

		needReset = false;
	}

	@Test(dataProvider = "apptInviteTestDataProvider", groups = {
			"amitwilltake", "amitwilltake" }, retryAnalyzer = RetryFailedTests.class)
	public void openApptViaReminder(String subject, String location,
			String attendees, String body, String action) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);

		zKillBrowsers();
		Thread.sleep(1000);
		SelNGBase.selfAccountName = attendees;
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		/*
		 * MailApp.ClickCheckMailUntilMailShowsUp(subject); if
		 * (action.equals("accept")) page.zCalApp.zAcceptInvite(subject);
		 * Thread.sleep(1500); obj.zMessageItem.zNotExists(subject);
		 * obj.zButton.zNotExists(localize(locator.replyAccept)); if
		 * (action.equals("decline")) page.zCalApp.zDeclineInvite(subject);
		 * Thread.sleep(1500); obj.zMessageItem.zNotExists(subject);
		 * obj.zButton.zNotExists(localize(locator.replyDecline)); if
		 * (action.equals("tentative")) page.zCalApp.zTentativeInvite(subject);
		 * Thread.sleep(1500); obj.zMessageItem.zNotExists(subject);
		 * obj.zButton.zNotExists(localize(locator.replyTentative));
		 */
		needReset = false;
	}

	/**
	 * Sends a meeting invite to some user Verifies that the invite content has
	 * all the information
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptInviteContentVerify(String subject, String location,
			String attendees, String body) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String organizer;

		organizer = SelNGBase.selfAccountName;

		page.zCalApp.zNavigateToCalendar();

		// obj.zButton.zClick(page.zCalApp.zCalWeekBtn);

		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);

		obj.zAppointment.zExists(subject);

		zKillBrowsers();

		Thread.sleep(500);

		SelNGBase.selfAccountName = attendees;
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);

		obj.zMessageItem.zClick(subject);

		String[] itemsToVerify = { subject, organizer.toLowerCase(), location,
				attendees, body };

		page.zCalApp.zVerifyInviteContent(localize(locator.apptNew),
				itemsToVerify);
		needReset = false;

	}

	/**
	 * Sends a meeting invite to some user Verifies that the recurring
	 * appointment invite content has all the information
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptRecurringInviteContentVerify(String subject,
			String location, String recurring, String attendees, String body)
			throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String organizer;

		organizer = SelNGBase.selfAccountName;

		page.zCalApp.zNavigateToCalendar();

		// obj.zButton.zClick(page.zCalApp.zCalWeekBtn);

		page.zCalCompose.zCreateAppt(subject, location, "", "", "", "", "", "",
				"", "", recurring, "", attendees, body);

		obj.zAppointment.zExists(subject);

		zKillBrowsers();

		Thread.sleep(1000);

		SelNGBase.selfAccountName = attendees;
		page.zLoginpage.zLoginToZimbraAjax(attendees);

		MailApp.ClickCheckMailUntilMailShowsUp(subject);

		obj.zMessageItem.zClick(subject);

		String[] itemsToVerify = { subject, organizer.toLowerCase(), location,
				attendees, body, localize(locator.recurrence) };

		page.zCalApp.zVerifyInviteContent(localize(locator.apptNew),
				itemsToVerify);
		needReset = false;

	}

	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptModifyInviteCheckContent(String oldSubject,
			String oldLocation, String attendees, String body,
			String startTime, String endTime, String newSubject,
			String newLocation, String newStartTime, String newEndTime)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject;
		String verifySubject;
		String verifyLocation;
		String verifyTime;
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateAppt(oldSubject, oldLocation, "", "", "", "",
				"", "", startTime, endTime, "", "", attendees, body);
		page.zCalCompose.zEditAppointmentWithDetails(oldSubject, newSubject,
				newLocation, "", "", "", "", "", "", newStartTime, newEndTime,
				"", "", "", "");
		if (!newSubject.equals("")) {
			verifySubject = newSubject + " "
					+ localize(locator.apptModifiedStamp);
			subject = newSubject;
		} else {
			verifySubject = oldSubject;
			subject = oldSubject;
		}
		if (!newLocation.equals("")) {
			verifyLocation = newLocation + " "
					+ localize(locator.apptModifiedStamp);
		} else {
			verifyLocation = "";
		}
		if (!newStartTime.equals("") || !newEndTime.equals("")) {
			verifyTime = localize(locator.apptModifiedStamp);
		} else {
			verifyTime = "";
		}

		zKillBrowsers();
		Thread.sleep(1000);
		SelNGBase.selfAccountName = attendees;
		String[] itemsToVerify = { verifySubject, verifyLocation, verifyTime };
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		page.zCalApp.zVerifyInviteContent(localize(locator.apptModified),
				itemsToVerify);

		needReset = false;
	}

	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptModifyRecurringCheckContent(String seriesOrInstance,
			String oldSubject, String oldLocation, String attendees,
			String body, String recurring, String startTime, String endTime,
			String newSubject, String newLocation, String newStartTime,
			String newEndTime) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject;
		String verifySubject;
		String verifyLocation;
		String verifyTime;
		String firstLineSummary;
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateAppt(oldSubject, oldLocation, "", "", "", "",
				"", "", startTime, endTime, recurring, "", attendees, body);
		if (seriesOrInstance.equals("instance")) {
			page.zCalApp.zOpenInstanceOfRecurringAppt(oldSubject);
			firstLineSummary = localize(locator.apptInstanceModified);
		} else if (seriesOrInstance.equals("series")) {
			page.zCalApp.zOpenSeriesRecurringAppt(oldSubject);
			firstLineSummary = localize(locator.apptModified);
		} else {
			page.zCalApp.zOpenInstanceOfRecurringAppt(oldSubject);
			firstLineSummary = localize(locator.apptInstanceModified);
		}
		page.zCalCompose.zCalendarEnterDetails(newSubject, newLocation, "", "",
				"", "", "", "", newStartTime, newEndTime, "", "", "", "");
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		Thread.sleep(1000);
		zWaitTillObjectExist("button", page.zCalApp.zCalNewApptBtn);
		if (!newSubject.equals("")) {
			verifySubject = newSubject + " "
					+ localize(locator.apptModifiedStamp);
			subject = newSubject;
		} else {
			verifySubject = oldSubject;
			subject = oldSubject;
		}
		if (!newLocation.equals("")) {
			verifyLocation = newLocation + " "
					+ localize(locator.apptModifiedStamp);
		} else {
			verifyLocation = "";
		}
		if (!newStartTime.equals("") || !newEndTime.equals("")) {
			verifyTime = localize(locator.apptModifiedStamp);
		} else {
			verifyTime = "";
		}

		String[] itemsToVerify = { verifySubject, verifyLocation, verifyTime };
		zKillBrowsers();
		Thread.sleep(1000);
		SelNGBase.selfAccountName = attendees;
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		Thread.sleep(2500);
		page.zCalApp.zVerifyInviteContent(firstLineSummary, itemsToVerify);

		needReset = false;
	}

	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptDeleteInviteCheckContent(String singleOrInstanceOrSeries,
			String subject, String location, String attendees, String body,
			String recurring) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String firstLineSummary;
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateAppt(subject, location, "", "", "", "", "", "",
				"", "", recurring, "", attendees, body);
		obj.zAppointment.zExists(subject);
		if (singleOrInstanceOrSeries.equals("instance")) {
			page.zCalApp.zDeleteInstanceOfRecurringAppt(subject);
			obj.zButton.zClickInDlgByName(localize(locator.yes),
					localize(locator.confirmTitle));
			firstLineSummary = localize(locator.apptInstanceCanceled);
		} else if (singleOrInstanceOrSeries.equals("series")) {
			page.zCalApp.zDeleteSeriesRecurringAppt(subject);
			obj.zButton.zClickInDlgByName(localize(locator.yes),
					localize(locator.confirmTitle));
			obj.zButton.zClickInDlgByName(localize(locator.yes),
					localize(locator.confirmTitle));
			firstLineSummary = localize(locator.apptCanceled);
		} else {
			page.zCalApp.zDeleteAppointmentWithAttendees(subject);
			firstLineSummary = localize(locator.apptCanceled);
		}
		zWaitTillObjectExist("button", localize(locator.send));
		obj.zButton.zClick(localize(locator.send));

		String[] itemsToVerify = { subject };
		zKillBrowsers();
		Thread.sleep(1000);
		SelNGBase.selfAccountName = attendees;
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(localize(locator.cancelled));
		obj.zMessageItem.zClick(localize(locator.cancelled));
		waitForSF();
		page.zCalApp.zVerifyInviteContent(firstLineSummary, itemsToVerify);

		needReset = false;
	}

	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptAddRemoveAttendeeCheckContent(
			String singleOrInstanceOrSeries, String subject, String location,
			String attendees, String body, String recurring, String newAttendees)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String itemsToVerify[] = { subject };
		String firstLineSummary;
		String firstLineSummaryNewAttendee;
		String browser = config.getString("browser");

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateAppt(subject, location, "", "", "", "", "", "",
				"", "", recurring, "", attendees, body);
		obj.zAppointment.zExists(subject);
		Thread.sleep(500);
		if (singleOrInstanceOrSeries.equals("instance")) {
			page.zCalApp.zOpenInstanceOfRecurringAppt(subject);
			firstLineSummary = localize(locator.calendarCancelRemovedFromAttendeeList);
			firstLineSummaryNewAttendee = localize(locator.apptInstanceModified);
		} else if (singleOrInstanceOrSeries.equals("series")) {
			page.zCalApp.zOpenSeriesRecurringAppt(subject);
			firstLineSummary = localize(locator.calendarCancelRemovedFromAttendeeList);
			firstLineSummaryNewAttendee = localize(locator.apptModified);
		} else {
			obj.zAppointment.zDblClick(subject);
			firstLineSummary = localize(locator.calendarCancelRemovedFromAttendeeList);
			firstLineSummaryNewAttendee = localize(locator.apptModified);
		}
		Thread.sleep(1000);
		obj.zTextAreaField.zActivate(localize(locator.attendeesLabel));
		page.zCalCompose.zCalendarEnterDetails("", "", "", "", "", "", "", "",
				"", "", "", "", newAttendees, "");
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		Thread.sleep(1000);
		obj.zRadioBtn.zClickInDlg(localize(locator.sendUpdatesAll));
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);

		zKillBrowsers();
		Thread.sleep(1000);
		SelNGBase.selfAccountName = attendees;
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		// obj.zMessageItem.zClick(localize(locator.calendarSubjectCancelled));
		// Thread.sleep(2000);
		// page.zCalApp.zVerifyInviteContent(firstLineSummary, itemsToVerify);

		zKillBrowsers();
		Thread.sleep(1000);
		SelNGBase.selfAccountName = newAttendees;
		page.zLoginpage.zLoginToZimbraAjax(newAttendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		Thread.sleep(2000);
		page.zCalApp.zVerifyInviteContent(firstLineSummaryNewAttendee,
				itemsToVerify);

		needReset = false;
	}

	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyNumberOfRecurringItems(String subject, String location,
			String attendees, String body, String recurring) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		// ////////////////////////////////////////////
		// Gets the current system date and adds 10 days to it.
		// ///////////////////////////////////////////
		String DATE_FORMAT = "yyyyMMdd";

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

		Calendar testcal = Calendar.getInstance();

		testcal.add(Calendar.DATE, 9);

		String dateToVerify = sdf.format(testcal.getTime());

		testcal.add(Calendar.DATE, 1);

		String nextDateToVerify = sdf.format(testcal.getTime());

		String urlToNavigate = config.getString("mode") + "://"
				+ config.getString("server")
				+ "/zimbra/?app=calendar&view=day&date=" + dateToVerify;
		String urlToNavigate2 = config.getString("mode") + "://"
				+ config.getString("server")
				+ "/zimbra/?app=calendar&view=day&date=" + nextDateToVerify;

		// Creating the appointment
		page.zCalApp.zNavigateToCalendar();

		// obj.zButton.zClick(page.zCalApp.zCalWeekBtn);

		page.zCalApp.zNavigateToApptCompose();

		page.zCalCompose.zCalendarEnterSimpleDetails(subject, location,
				attendees, body);

		obj.zFeatureMenu.zClick(localize(locator.repeatLabel));

		obj.zMenuItem.zClick(localize(locator.custom));

		Thread.sleep(1000);

		obj.zDialog.zExists(localize(locator.customRepeat));

		//

		String locale = config.getString("locale");
		String endField = localize(locator.recurEndNumber);

		int a = endField.indexOf("{");

		endField = endField.substring(0, a);

		if (locale.equals("ru") || locale.equals("es")
				|| locale.equals("pt_BR") || locale.equals("it")
				|| locale.equals("de") || locale.equals("ar"))
			obj.zEditField.zTypeInDlg(endField, "10");
		else
			obj.zEditField.zTypeInDlg(localize(locator.end), "10");

		obj.zButton.zClickInDlg(localize(locator.ok));

		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);

		Thread.sleep(1000);

		obj.zAppointment.zExists(subject);

		selenium.open(urlToNavigate);

		obj.zAppointment.zExists(subject);

		selenium.open(urlToNavigate2);

		obj.zAppointment.zNotExists(subject);

		needReset = false;

	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zAppointmentsRespondWithEditReply() throws Exception {

		String acceptSubject = getLocalizedData_NoSpecialChar();
		String declineSubject = getLocalizedData_NoSpecialChar();
		String tentativeSubject = getLocalizedData_NoSpecialChar();

		String body = getLocalizedData_NoSpecialChar();

		String acceptLocation = getLocalizedData_NoSpecialChar();
		String declineLocation = getLocalizedData_NoSpecialChar();
		String tentativeLocation = getLocalizedData_NoSpecialChar();

		String acceptEditedReplyContent = getLocalizedData_NoSpecialChar();
		String declineEditedReplyContent = getLocalizedData_NoSpecialChar();
		String tentativeEditedReplyContent = getLocalizedData_NoSpecialChar();

		String attendees = ProvZCS.getRandomAccount();

		String organizer = SelNGBase.selfAccountName;

		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		page.zCalCompose.zCreateSimpleAppt(acceptSubject, acceptLocation,
				attendees, body);
		Thread.sleep(1000);
		waitForIE();

		page.zCalCompose.zCreateSimpleAppt(declineSubject, declineLocation,
				attendees, body);
		Thread.sleep(1000);
		waitForIE();

		page.zCalCompose.zCreateSimpleAppt(tentativeSubject, tentativeLocation,
				attendees, body);

		Thread.sleep(1000);
		waitForIE();

		zKillBrowsers();

		Thread.sleep(500);

		SelNGBase.selfAccountName = attendees;
		page.zLoginpage.zLoginToZimbraAjax(attendees);

		// obj.zButton.zClick(page.zMailApp.zMailViewIconBtn);

		// obj.zMenuItem.zClick(localize(locator.byMessage));

		MailApp.ClickCheckMailUntilMailShowsUp(acceptSubject);

		page.zCalApp.zRespondApptEditReply(acceptSubject, "accept",
				acceptEditedReplyContent);

		MailApp.ClickCheckMailUntilMailShowsUp(declineSubject);

		page.zCalApp.zRespondApptEditReply(declineSubject, "decline",
				declineEditedReplyContent);

		MailApp.ClickCheckMailUntilMailShowsUp(tentativeSubject);

		page.zCalApp.zRespondApptEditReply(tentativeSubject, "tentative",
				tentativeEditedReplyContent);

		Thread.sleep(500);

		zKillBrowsers();

		Thread.sleep(500);

		SelNGBase.selfAccountName = organizer;
		page.zLoginpage.zLoginToZimbraAjax(organizer);

		obj.zButton.zClick(page.zMailApp.zMailViewIconBtn);

		obj.zMenuItem.zClick(localize(locator.byMessage));

		String itemsToVerify[] = { acceptEditedReplyContent };
		MailApp.ClickCheckMailUntilMailShowsUp(acceptSubject);

		obj.zMessageItem.zClick(acceptSubject);

		page.zCalApp.zVerifyInviteContent("", itemsToVerify);

		String itemsToVerify1[] = { declineEditedReplyContent };
		MailApp.ClickCheckMailUntilMailShowsUp(declineSubject);

		obj.zMessageItem.zClick(declineSubject);

		page.zCalApp.zVerifyInviteContent("", itemsToVerify1);

		String itemsToVerify2[] = { tentativeEditedReplyContent };
		MailApp.ClickCheckMailUntilMailShowsUp(tentativeSubject);

		obj.zMessageItem.zClick(tentativeSubject);

		page.zCalApp.zVerifyInviteContent("", itemsToVerify2);

		needReset = false;

	}

	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteAppt_bug38150(String singleOrInstanceOrSeries,
			String subject, String location, String attendees, String body,
			String recurring)

	throws Exception {

		String firstLineSummary;

		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateAppt(subject, location, "", "", "", "", "", "",
				"", "", recurring, "", attendees, body);
		obj.zAppointment.zExists(subject);

		if (singleOrInstanceOrSeries.equals("instance")) {
			page.zCalApp.zDeleteInstanceOfRecurringAppt(subject);
			firstLineSummary = localize(locator.apptInstanceCanceled);
		} else if (singleOrInstanceOrSeries.equals("series")) {
			page.zCalApp.zDeleteSeriesRecurringAppt(subject);
			firstLineSummary = localize(locator.apptCanceled);
		} else {
			page.zCalApp.zDeleteAppointmentWithAttendees(subject);
			firstLineSummary = localize(locator.apptCanceled);
		}

		subject = "Edited Subject";
		body = "Edited body";

		obj.zEditField.zType(localize(locator.subject), subject);
		obj.zEditor.zType(body);
		obj.zButton.zClick(localize(locator.send));
		Thread.sleep(10000);

		String[] itemsToVerify = { body };
		zKillBrowsers();
		Thread.sleep(1000);
		SelNGBase.selfAccountName = attendees;
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		waitForSF();

		page.zCalApp.zVerifyInviteContent(firstLineSummary, itemsToVerify);
		needReset = false;
	}

	private void waitForIE() throws Exception {
		String browser = config.getString("browser");

		if (browser.equals("IE"))
			Thread.sleep(2000);

	}

	private void waitForSF() throws Exception {
		String browser = config.getString("browser");

		if (browser.equals("SF"))
			Thread.sleep(2000);

	}

	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}

}
