package projects.zcs.tests.calendar.newappt;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.CalCompose;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class BasicApptTests extends CommonTest {
	@DataProvider(name = "apptCreateDataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("createSimpleAppt") || test.equals("deleteAppt")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1),
					"ccuser@testdomain.com, bccuser@testdomain.com",
					getLocalizedData(3) } };
		} else if (test.equals("createAppt")) {
			return new Object[][] {
					{ getLocalizedData(1), getLocalizedData(1),
							localize(locator.free), localize(locator._public),
							"", "", "", "",
							"10:00:" + localize(locator.periodAm),
							"11:00:" + localize(locator.periodAm),
							localize(locator.everyWeek), "",
							"ccuser@testdomain.com", getLocalizedData(3) },
					{ getLocalizedData(1), getLocalizedData(1),
							localize(locator.busy), localize(locator._public),
							"", "1", "", "", "", "",
							localize(locator.everyMonth), "",
							"ccuser@testdomain.com", getLocalizedData(3) } };
		} else if (test.equals("deleteAppt_Bug38150")) {
			return new Object[][] { { "single",
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					ProvZCS.getRandomAccount(), getLocalizedData(3), "" } };
		} else if (test.equals("deleteApptByKeyBoard_Bug35866")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), ProvZCS.getRandomAccount(),
					getLocalizedData(3) } };
		} else if (test.equals("deleteAppt_Bug38359")) {
			return new Object[][] { { getLocalizedData(1), getLocalizedData(1),
					localize(locator.busy), localize(locator._public), "", "1",
					"", "", "", "", localize(locator.everyWeek), "",
					"ccuser@testdomain.com", getLocalizedData(3) } };
		} else if (test.equals("editAppt") || test.equals("moveAppt")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), "ccuser@testdomain.com",
					getLocalizedData(3), getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("forwardAppt")
				|| test.equals("forwardApptInviteForMultivalue")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), ProvZCS.getRandomAccount(),
					getLocalizedData(3) } };
		} else if (test.equals("verifyNumberOfRecurringItems")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					ProvZCS.getRandomAccount(), getLocalizedData(3),
					localize(locator.everyDay) } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		SleepUtil.sleep(2000);
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	/**
	 * Enters different invalid email address types and checks if alert/warning
	 * dialog is thrown
	 */
	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createSimpleAppt(String subject, String location,
			String attendees, String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createAppt(String subject, String location, String showAs,
			String markAs, String calendar, String allDayEvent,
			String startDate, String endDate, String startTime, String endTime,
			String repeat, String reminder, String attendees, String body)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateAppt(subject, location, showAs, markAs,
				calendar, allDayEvent, startDate, endDate, startTime, endTime,
				repeat, reminder, attendees, body);
		obj.zAppointment.zExists(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteAppt(String subject, String location, String attendees,
			String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);
		page.zCalApp.zDeleteAppointmentWithAttendees(subject);
		obj.zAppointment.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteAppt_Bug38150(String singleOrInstanceOrSeries,
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
		SleepUtil.sleep(10000);

		String[] itemsToVerify = { body };
		resetSession();
		SleepUtil.sleep(1000);
		SelNGBase.selfAccountName.set(attendees);
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(2000);
		page.zCalApp.zVerifyInviteContent(firstLineSummary, itemsToVerify);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteAppt_Bug38359(String subject, String location,
			String showAs, String markAs, String calendar, String allDayEvent,
			String startDate, String endDate, String startTime, String endTime,
			String repeat, String reminder, String attendees, String body)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		for (int i = 0; i <= 2; i++) {
			page.zCalApp.zNavigateToCalendar();
			page.zCalCompose.zCreateAppt(subject, location, showAs, markAs,
					calendar, allDayEvent, startDate, endDate, startTime,
					endTime, repeat, reminder, attendees, body);
			obj.zAppointment.zExists(subject);
		}
		page.zCalApp.zCalViewSwitch("list");
		SelNGBase.selenium.get().clickAt(("//*[@id=\"zlhi__CLL__se\"]"), "");
		obj.zButton.zClick(page.zCalApp.zCalDeleteBtn);
		obj.zRadioBtn.zClickInDlgByName(localize(locator.deleteSeries),
				localize(locator.deleteRecurringItem));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.deleteRecurringItem));
		obj.zAppointment.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * TestCase:-Deleting appointment using keyboard.
	 * 
	 * @steps 1.login to mail client 2.go to calendar 3.select an appointment
	 *        and hit Delete key on the keyboard 4.Verify Appointment should get
	 *        deleted.
	 * @author Girish
	 */

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteApptByKeyBoard_Bug35866(String subject, String location,
			String attendees, String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		SleepUtil.sleep(1000);
		obj.zAppointment.zClick(subject);
		Robot zRobot = new Robot();
		zRobot.keyPress(KeyEvent.VK_DELETE);
		zRobot.keyRelease(KeyEvent.VK_DELETE);
		SleepUtil.sleep(1000);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.confirmTitle));
		SleepUtil.sleep(1000);
		obj.zAppointment.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editAppt(String subject, String location, String attendees,
			String body, String newSubject) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		page.zCalCompose.zEditAppointment(subject, newSubject);
		obj.zAppointment.zNotExists(subject);
		obj.zAppointment.zExists(newSubject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveAppt(String subject, String location, String attendees,
			String body, String newCalendar) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zCreateNewCalendarFolder(newCalendar);
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		page.zCalApp.zMoveAppointment(subject, localize(locator.calendar),
				newCalendar);
		page.zCalApp.zCalendarUncheck(newCalendar);
		obj.zButton.zClick(page.zCalApp.zCalRefreshBtn);
		obj.zAppointment.zNotExists(subject);
		page.zCalApp.zCalendarCheck(newCalendar);
		obj.zButton.zClick(page.zCalApp.zCalRefreshBtn);
		obj.zAppointment.zExists(subject);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void forwardAppt(String subject, String location, String attendees,
			String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String newBody = "ForwardAppt_BodyUpdated";
		String thirdUser = ProvZCS.getRandomAccount();
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		SelNGBase.selfAccountName.set(attendees);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.accept));
		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zRtClick(subject);
		obj.zMenuItem.zClick(localize(locator.forward));
		zWaitTillObjectExist("editfield", localize(locator.subjectLabel));
		// obj.zEditField.zIsDisabled(localize(locator.subjectLabel));
		// obj.zEditField.zIsDisabled(localize(locator.location));
		// obj.zButton.zIsDisabled(localize(locator.busy));
		// obj.zButton.zIsDisabled(localize(locator._public));
		// obj.zButton.zIsDisabled(localize(locator.calendarLabel));
		// obj.zCheckbox.zIsDisabled(localize(locator.allDayEvent));
		// obj.zEditField.zIsDisabled(localize(locator.start));
		// obj.zEditField.zIsDisabled(localize(locator.end));
		// obj.zButton.zIsDisabled(localize(locator.none));
		// obj.zButton.zIsDisabled(localize(locator.reminderLabel));
		// obj.zEditField.zIsDisabled(localize(locator.attendeesLabel));
		obj.zEditor.zType(newBody);
		obj.zTextAreaField
				.zType(
						"xpath=//td[contains(@id,'_to_control')]//textarea[contains(@id,'DWT')]",
						thirdUser);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(thirdUser);
		SelNGBase.selfAccountName.set(thirdUser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.accept));
		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zExists(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test case:zimbraPrefCalendarForwardInvitesTo doesn't work for multivalue
	 * Steps: 1. Go to Preferences -> Calendar -> Forward my invites to: and add
	 * 2 mail id here. 2. Invite above user to a meeting. and see all the
	 * invitation mails went to those 2 users also.
	 * 
	 * @author Girish
	 */
	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void forwardApptInviteForMultivalue(String subject, String location,
			String attendees, String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String currentLoggedInUser = SelNGBase.selfAccountName.get();
		String user1 = ProvZCS.getRandomAccount();
		String user2 = ProvZCS.getRandomAccount();
		String user3 = ProvZCS.getRandomAccount();
		String currentloggedInUser = currentLoggedInUser.toLowerCase();

		zGoToApplication("Preferences");
		zGoToPreferences("Calendar");
		obj.zTextAreaField.zType(
				"xpath=//input[contains(@id,'_CAL_INV_FORWARDING_ADDRESS')]",
				user2 + "," + user3);
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		SleepUtil.sleep(2000);
		resetSession();
		// login to user1 and send invitaion currentLoggeduser
		String subject1 = getLocalizedData_NoSpecialChar();
		page.zLoginpage.zLoginToZimbraAjax(user1);
		SelNGBase.selfAccountName.set(user1);
		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		page.zCalCompose.zCalendarEnterSimpleDetails(subject1, location,
				currentLoggedInUser, body);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		SleepUtil.sleep(2000);
		// login to currentLoggeduser and accept invitation
		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(currentLoggedInUser);
		SelNGBase.selfAccountName.set(currentLoggedInUser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject1);
		obj.zMessageItem.zClick(subject1);
		obj.zButton.zClick(localize(locator.accept));
		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zExists(subject1);
		resetSession();
		// login to user2 and check fwd'ed invitation
		page.zLoginpage.zLoginToZimbraAjax(user2);
		SelNGBase.selfAccountName.set(user2);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject1);
		obj.zMessageItem.zClick(subject1);
		obj.zButton.zExists(localize(locator.replyAccept));
		obj.zButton.zExists(localize(locator.replyDecline));
		obj.zButton.zExists(localize(locator.replyTentative));
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US")) {

			String onbehalfof = localize(locator.onBehalfOf).toLowerCase();
			Assert
					.assertTrue(SelNGBase.selenium.get()
							.isElementPresent("xpath=//td[contains(@id,'ztb__CLV__Inv_item') and contains(text(),'"
									+ onbehalfof
									+ "') ]/b[contains(text(),'"
									+ currentloggedInUser + "')]"));
			obj.zButton.zClick(localize(locator.accept));
			SleepUtil.sleep(1000);
			obj.zDialog.zExists(localize(locator.zimbraTitle));

			obj.zDialog.zVerifyAlertMessage(localize(locator.zimbraTitle),
					localize(locator.errorPermission));
			obj.zButton.zClickInDlg(localize(locator.ok));
		}
		// Permission denied error dialog box and press ok

		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zNotExists(subject1);
		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(user3);
		SelNGBase.selfAccountName.set(user3);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject1);
		obj.zMessageItem.zClick(subject1);

		obj.zButton.zExists(localize(locator.replyAccept));
		obj.zButton.zExists(localize(locator.replyDecline));
		obj.zButton.zExists(localize(locator.replyTentative));

		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US")) {
			String onbehalfof = localize(locator.onBehalfOf).toLowerCase();
			Assert
					.assertTrue(SelNGBase.selenium.get()
							.isElementPresent("xpath=//td[contains(@id,'ztb__CLV__Inv_item') and contains(text(),'"
									+ onbehalfof
									+ "') ]/b[contains(text(),'"
									+ currentloggedInUser + "')]"));
			obj.zButton.zClick(localize(locator.accept));
			SleepUtil.sleep(1000);
			obj.zDialog.zExists(localize(locator.zimbraTitle));

			obj.zDialog.zVerifyAlertMessage(localize(locator.zimbraTitle),
					localize(locator.errorPermission));
			obj.zButton.zClickInDlg(localize(locator.ok));
		}

		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zNotExists(subject1);

		SelNGBase.needReset.set(false);

	}

	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyNumberOfRecurringItems(String subject, String location,
			String attendees, String body, String recurring) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
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
		String urlToNavigate = ZimbraSeleniumProperties.getStringProperty("mode") + "://"
				+ ZimbraSeleniumProperties.getStringProperty("server")
				+ "/zimbra/?app=calendar&view=day&date=" + dateToVerify;
		String urlToNavigate2 = ZimbraSeleniumProperties.getStringProperty("mode") + "://"
				+ ZimbraSeleniumProperties.getStringProperty("server")
				+ "/zimbra/?app=calendar&view=day&date=" + nextDateToVerify;
		// Creating the appointment
		page.zCalApp.zNavigateToCalendar();
		// obj.zButton.zClick(page.zCalApp.zCalWeekBtn);
		page.zCalApp.zNavigateToApptCompose();
		page.zCalCompose.zCalendarEnterSimpleDetails(subject, location,
				attendees, body);
		obj.zFeatureMenu.zClick(localize(locator.repeatLabel));
		obj.zMenuItem.zClick(localize(locator.custom));
		SleepUtil.sleep(1000);
		obj.zDialog.zExists(localize(locator.customRepeat));

		String locale = ZimbraSeleniumProperties.getStringProperty("locale");
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
		SleepUtil.sleep(1000);
		obj.zAppointment.zExists(subject);
		SelNGBase.selenium.get().open(urlToNavigate);
		obj.zAppointment.zExists(subject);
		SelNGBase.selenium.get().open(urlToNavigate2);
		obj.zAppointment.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}

	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}
