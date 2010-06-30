package projects.zcs.tests.calendar.newappt;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.tools.ant.taskdefs.WaitFor;
import org.testng.Assert;
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
		if (test.equals("acceptDeclineTentativeAppt")) {
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
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), ProvZCS.getRandomAccount(),
					getLocalizedData(3) } };
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
		} else if (test.equals("editReplyAppt_Bug37186")
				|| test.equals("editReplyApptAsAnAlias_Bug12301")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), ProvZCS.getRandomAccount(),
					getLocalizedData(3) } };
		} else {
			return new Object[][] { { "" } };
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
	public void acceptDeclineTentativeAppt(String subject, String location,
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

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zAppointmentsRespondWithEditReply() throws Exception {
		if (isExecutionARetry)
			handleRetry();

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

	/**
	 * Test Case:- editReplyAccept Appointment
	 * 
	 * @steps 1.user1 creates an appointment and send to user2 2. user2 logs in
	 *        and goes to calendar 3. rt. clicks the appointment in the calendar
	 *        grid 4. selects 'Edit Reply', Accept. 5. Compose page opens 6.
	 *        Verify 'To' and 'Subject' fields they should not remains empty.
	 *        7.'user1' should be filled in To, and 'appointment subject' should
	 *        be filled in the 'Subject' field
	 * @author Girish
	 */
	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void editReplyAppt_Bug37186(String subject, String location,
			String attendees, String body) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String loggeduser = SelNGBase.selfAccountName;
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);

		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		SelNGBase.selfAccountName = attendees;
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zRtClick(subject);
		obj.zMenuItem.zMouseOver(localize(locator.editReply));
		selenium.clickAt("xpath=//td[contains(@id,'DW') and contains(text(),'"
				+ localize(locator.accept) + "')][1]", "");
		zWaitTillObjectExist("editfield", localize(locator.subjectLabel));
		Assert.assertTrue(obj.zTextAreaField.zGetInnerText(
				page.zComposeView.zToField).equalsIgnoreCase(loggeduser),
				"Replied and logged user does not Match");
		Assert.assertTrue(obj.zTextAreaField.zGetInnerText(
				page.zComposeView.zSubjectField).contains(subject),
				"Subject does not matched");

		needReset = false;

	}

	/**
	 * Test case users can't reply to appt invitations as an alias
	 * 
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void editReplyApptAsAnAlias_Bug12301(String subject,
			String location, String attendees, String body) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String acc1 = ProvZCS.getRandomAccount();
		GregorianCalendar thisday = new GregorianCalendar();
		Date d = thisday.getTime();
		DateFormat df = new SimpleDateFormat("yyMMddHHmmss");
		String s = df.format(d);
		String alias = s + "@testdomain.com";

		ProvZCS.addAlias(acc1, alias);
		ProvZCS.modifyAccount(acc1, "zimbraPrefFromAddress", alias);
		String loggeduser = SelNGBase.selfAccountName;
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, alias, body);
		obj.zAppointment.zExists(subject);

		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraAjax(acc1);
		SelNGBase.selfAccountName = acc1;
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zRtClick(subject);
		obj.zMenuItem.zMouseOver(localize(locator.editReply));
		selenium.clickAt("xpath=//td[contains(@id,'DW') and contains(text(),'"
				+ localize(locator.accept) + "')][1]", "");
		zWaitTillObjectExist("editfield", localize(locator.subjectLabel));
		Assert.assertTrue(obj.zTextAreaField.zGetInnerText(
				page.zComposeView.zToField).equalsIgnoreCase(loggeduser),
				"Replied and logged user does not Match");
		Assert.assertTrue(obj.zTextAreaField.zGetInnerText(
				page.zComposeView.zSubjectField).contains(subject),
				"Subject does not matched");

		obj.zButton.zClick(localize(locator.send));

		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraAjax(loggeduser);
		SelNGBase.selfAccountName = loggeduser;
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byMessage));
		Thread.sleep(1000);
		obj.zMessageItem.zRtClick(subject);
		obj.zMenuItem.zClick(localize(locator.showOrig));
		Thread.sleep(4000);
		selenium.selectWindow("_blank");
		String showOrigText = selenium.getBodyText();
		Thread.sleep(1000);
		Assert.assertTrue(showOrigText.contains("From: " + alias));
		Assert.assertFalse(showOrigText.contains("From: " + acc1));
		selenium.selectWindow(null);

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
