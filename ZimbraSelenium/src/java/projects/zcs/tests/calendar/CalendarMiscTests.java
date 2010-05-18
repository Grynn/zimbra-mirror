package projects.zcs.tests.calendar;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings( { "static-access", "unused" })
public class CalendarMiscTests extends CommonTest {
	@DataProvider(name = "apptCreateDataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("forwardAppt")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), ProvZCS.getRandomAccount(),
					getLocalizedData(3) } };
		} else if (test.equals("editReplyAppt_37186")
				|| test.equals("deleteApptByKeyBoard_35866")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), ProvZCS.getRandomAccount(),
					getLocalizedData(3) } };
		} else if (test.equals("setPermissionToReceiveAppt_Bug41072")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), "", getLocalizedData(3) } };
		} else {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1),
					"ccuser@testdomain.com, bccuser@testdomain.com",
					getLocalizedData(3) } };
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

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void subCalendarBreaks_Bug28846(String subject, String location,
			String attendees, String body) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentLoggedInUser = SelNGBase.selfAccountName;
		String user1 = ProvZCS.getRandomAccount();
		String user2 = ProvZCS.getRandomAccount();

		zKillBrowsers();
		String subject2 = getLocalizedData_NoSpecialChar();
		page.zLoginpage.zLoginToZimbraAjax(user1);
		SelNGBase.selfAccountName = user1;
		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		page.zCalCompose.zCalendarEnterSimpleDetails(subject2, location,
				currentLoggedInUser, body);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		Thread.sleep(2000);
		obj.zDialog.zNotExists(localize(locator.infoMsg));

		needReset = false;
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void setPermissionToReceiveAppt_Bug43046(String subject,
			String location, String attendees, String body) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentLoggedInUser;
		currentLoggedInUser = SelNGBase.selfAccountName;

		String user1_allowed = ProvZCS.getRandomAccount();
		String user2_allowed = ProvZCS.getRandomAccount();
		String user3_Notallowed = ProvZCS.getRandomAccount();
		zGoToApplication("Preferences");
		zGoToPreferences("Calendar");
		obj.zRadioBtn.zClick(localize(locator.invitesAllowSome));
		obj.zTextAreaField.zType("id=CAL_INVITE_ACL_USERS*", user1_allowed
				+ "," + user2_allowed);
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		Thread.sleep(2000);

		// -ve test case
		zKillBrowsers();
		String subject1 = getLocalizedData_NoSpecialChar();
		page.zLoginpage.zLoginToZimbraAjax(user3_Notallowed);
		SelNGBase.selfAccountName = user3_Notallowed;
		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		page.zCalCompose.zCalendarEnterSimpleDetails(subject1, location,
				currentLoggedInUser, body);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		Thread.sleep(2000);

		String expectedValue = localize(locator.invitePermissionDenied,
				currentLoggedInUser, "").replace(
				System.getProperty("line.separator"), " ");
		String actualValue = obj.zDialog.zGetMessage(localize(locator.infoMsg)
				.replace(System.getProperty("line.separator"), ""));
		if (config.getString("browser").equals("IE")) {
			expectedValue = expectedValue.replace("  ", " ");
			actualValue = actualValue.replace(System
					.getProperty("line.separator"), "");
		}
		Assert.assertTrue(expectedValue.contains(actualValue),
				"Verifying dialog for permission issue for sending invitation");
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.infoMsg));
		Thread.sleep(2000);

		// positive test case
		zKillBrowsers();
		String subject2 = getLocalizedData_NoSpecialChar();
		page.zLoginpage.zLoginToZimbraAjax(user1_allowed);
		SelNGBase.selfAccountName = user1_allowed;
		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		page.zCalCompose.zCalendarEnterSimpleDetails(subject2, location,
				currentLoggedInUser, body);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		Thread.sleep(2000);
		obj.zDialog.zNotExists(localize(locator.infoMsg));

		// Verification for both case
		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraAjax(currentLoggedInUser);
		Thread.sleep(2000);
		SelNGBase.selfAccountName = currentLoggedInUser;
		String msgExists = obj.zMessageItem.zExistsDontWait(subject1);
		// already enhancement 43340 for this bug otherwise it would be false
		assertReport(
				"true",
				msgExists,
				"Invite message not exists though user set preference not to receive invitation from this user");
		msgExists = obj.zMessageItem.zExistsDontWait(subject2);
		assertReport(
				"true",
				msgExists,
				"Invite message not exists though user set preference to receive invitation from this user");
		zGoToApplication("Calendar");
		obj.zAppointment.zNotExists(subject1);
		obj.zAppointment.zExists(subject2);

		needReset = false;
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void dontReceiveAnyInvitePermission(String subject, String location,
			String attendees, String body) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentLoggedInUser;
		currentLoggedInUser = SelNGBase.selfAccountName;

		String user1 = ProvZCS.getRandomAccount();
		zGoToApplication("Preferences");
		zGoToPreferences("Calendar");
		obj.zRadioBtn.zClick(localize(locator.invitesAllowNone));
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		Thread.sleep(2000);

		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraAjax(user1);
		SelNGBase.selfAccountName = user1;
		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		page.zCalCompose.zCalendarEnterSimpleDetails(subject, location,
				currentLoggedInUser, body);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		Thread.sleep(2000);
		String expectedValue = localize(locator.invitePermissionDenied,
				currentLoggedInUser, "").replace(
				System.getProperty("line.separator"), " ");
		String actualValue = obj.zDialog.zGetMessage(localize(locator.infoMsg)
				.replace(System.getProperty("line.separator"), ""));

		if (config.getString("browser").equals("IE")) {
			expectedValue = expectedValue.replace("  ", " ");
			actualValue = actualValue.replace(System
					.getProperty("line.separator"), "");
		}
		Assert.assertTrue(expectedValue.contains(actualValue),
				"Verifying dialog for permission issue for sending invitation");
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.infoMsg));
		Thread.sleep(2000); // this wait is necessary, because zcs crashes
		// browser without sending email

		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraAjax(currentLoggedInUser);
		SelNGBase.selfAccountName = currentLoggedInUser;
		Thread.sleep(2000);
		String msgExists = obj.zMessageItem.zExistsDontWait(subject);
		// already enhancement 43340 for this bug otherwise it would be false
		assertReport(
				"true",
				msgExists,
				"Invite message not exists though user set preference not to receive invitation from this user");
		zGoToApplication("Calendar");
		obj.zAppointment.zNotExists(subject);

		needReset = false;
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void forwardAppt(String subject, String location, String attendees,
			String body) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		String newBody = "ForwardAppt_BodyUpdated";
		String thirdUser = ProvZCS.getRandomAccount();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);

		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		SelNGBase.selfAccountName = attendees;
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

		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraAjax(thirdUser);
		SelNGBase.selfAccountName = thirdUser;
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.accept));
		page.zCalApp.zNavigateToCalendar();
		obj.zAppointment.zExists(subject);

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
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editReplyAppt_37186(String subject, String location,
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
	 * TestCase:-Deleting appointment using keyboard.
	 * 
	 * @steps 1.login to mail client 2.go to calendar 3.select an appointment
	 *        and hit Delete key on the keyboard 4.Verify Appointment should get
	 *        deleted.
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @throws Exception
	 * @author Girish
	 */

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteApptByKeyBoard_35866(String subject, String location,
			String attendees, String body) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		Thread.sleep(1000);
		obj.zAppointment.zClick(subject);
		Robot zRobot = new Robot();
		zRobot.keyPress(KeyEvent.VK_DELETE);
		zRobot.keyRelease(KeyEvent.VK_DELETE);
		Thread.sleep(1000);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.confirmTitle));
		Thread.sleep(1000);
		obj.zAppointment.zNotExists(subject);

		needReset = false;
	}

	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}
