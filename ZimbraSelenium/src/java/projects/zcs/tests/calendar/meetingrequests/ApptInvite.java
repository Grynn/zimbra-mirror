package projects.zcs.tests.calendar.meetingrequests;

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
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings({ "static-access", "unused" })
public class ApptInvite extends CommonTest {
	@DataProvider(name = "apptInviteTestDataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		 if (test.equals("apptInviteContentVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), ProvZCS.getRandomAccount(),
					getLocalizedData(3) } };
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
		}  else if (test.equals("apptDeleteInviteCheckContent")) {
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
		} else {
			return new Object[][] { { "" } };
		}
	}
	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="calendar";
		super.zLogin();
	}

	@Test(dataProvider = "apptInviteTestDataProvider", groups = {
			"amitwilltake", "amitwilltake" }, retryAnalyzer = RetryFailedTests.class)
	public void openApptViaReminder(String subject, String location,
			String attendees, String body, String action) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);

		resetSession();
		SleepUtil.sleep(1000);
		SelNGBase.selfAccountName.set(attendees);
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		/*
		 * MailApp.ClickCheckMailUntilMailShowsUp(subject); if
		 * (action.equals("accept")) page.zCalApp.zAcceptInvite(subject);
		 * SleepUtil.sleep(1500); obj.zMessageItem.zNotExists(subject);
		 * obj.zButton.zNotExists(localize(locator.replyAccept)); if
		 * (action.equals("decline")) page.zCalApp.zDeclineInvite(subject);
		 * SleepUtil.sleep(1500); obj.zMessageItem.zNotExists(subject);
		 * obj.zButton.zNotExists(localize(locator.replyDecline)); if
		 * (action.equals("tentative")) page.zCalApp.zTentativeInvite(subject);
		 * SleepUtil.sleep(1500); obj.zMessageItem.zNotExists(subject);
		 * obj.zButton.zNotExists(localize(locator.replyTentative));
		 */
		SelNGBase.needReset.set(false);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String organizer;
		organizer = SelNGBase.selfAccountName.get();
		page.zCalApp.zNavigateToCalendar();
		// obj.zButton.zClick(page.zCalApp.zCalWeekBtn);
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);

		resetSession();
		SleepUtil.sleep(500);
		SelNGBase.selfAccountName.set(attendees);
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		String[] itemsToVerify = { subject, organizer.toLowerCase(), location,
				attendees, body };
		page.zCalApp.zVerifyInviteContent(localize(locator.apptNew),
				itemsToVerify);

		SelNGBase.needReset.set(false);
	}

	
	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptModifyInviteCheckContent(String oldSubject,
			String oldLocation, String attendees, String body,
			String startTime, String endTime, String newSubject,
			String newLocation, String newStartTime, String newEndTime)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
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

		resetSession();
		SleepUtil.sleep(1000);
		SelNGBase.selfAccountName.set(attendees);
		String[] itemsToVerify = { verifySubject, verifyLocation, verifyTime };
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		page.zCalApp.zVerifyInviteContent(localize(locator.apptModified),
				itemsToVerify);

		SelNGBase.needReset.set(false);
	}

	

	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptDeleteInviteCheckContent(String singleOrInstanceOrSeries,
			String subject, String location, String attendees, String body,
			String recurring) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
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
		resetSession();
		SleepUtil.sleep(1000);
		SelNGBase.selfAccountName.set(attendees);
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(localize(locator.cancelled));
		obj.zMessageItem.zClick(localize(locator.cancelled));
		waitForSF();
		page.zCalApp.zVerifyInviteContent(firstLineSummary, itemsToVerify);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptAddRemoveAttendeeCheckContent(
			String singleOrInstanceOrSeries, String subject, String location,
			String attendees, String body, String recurring, String newAttendees)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String itemsToVerify[] = { subject };
		String firstLineSummary;
		String firstLineSummaryNewAttendee;
		String browser = ZimbraSeleniumProperties.getStringProperty("browser");

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateAppt(subject, location, "", "", "", "", "", "",
				"", "", recurring, "", attendees, body);
		obj.zAppointment.zExists(subject);
		SleepUtil.sleep(500);
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
		SleepUtil.sleep(1000);
		obj.zTextAreaField.zActivate(localize(locator.attendeesLabel));
		page.zCalCompose.zCalendarEnterDetails("", "", "", "", "", "", "", "",
				"", "", "", "", newAttendees, "");
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		SleepUtil.sleep(1000);
		obj.zRadioBtn.zClickInDlg(localize(locator.sendUpdatesAll));
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(1000);

		resetSession();
		SleepUtil.sleep(1000);
		SelNGBase.selfAccountName.set(attendees);
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		// obj.zMessageItem.zClick(localize(locator.calendarSubjectCancelled));
		// SleepUtil.sleep(2000);
		// page.zCalApp.zVerifyInviteContent(firstLineSummary, itemsToVerify);

		resetSession();
		SleepUtil.sleep(1000);
		SelNGBase.selfAccountName.set(newAttendees);
		page.zLoginpage.zLoginToZimbraAjax(newAttendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(2000);
		page.zCalApp.zVerifyInviteContent(firstLineSummaryNewAttendee,
				itemsToVerify);

		SelNGBase.needReset.set(false);
	}

	private void waitForIE() throws Exception {
		String browser = ZimbraSeleniumProperties.getStringProperty("browser");
		if (browser.equals("IE"))
			SleepUtil.sleep(2000);

	}

	private void waitForSF() throws Exception {
		String browser = ZimbraSeleniumProperties.getStringProperty("browser");
		if (browser.equals("SF"))
			SleepUtil.sleep(2000);
	}
}
