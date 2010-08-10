package projects.zcs.tests.calendar;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.ZimbraSeleniumProperties;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings( { "static-access", "unused" })
public class CalendarBugTests extends CommonTest {
	@DataProvider(name = "apptCreateDataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("subCalendarBreaks_Bug28846")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), "", getLocalizedData(3) } };
		} else if (test.equals("setPermissionToReceiveAppt_Bug43046")
				|| test.equals("dontReceiveAnyInvitePermission_Bug43046")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), "", getLocalizedData(3) } };
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

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void subCalendarBreaks_Bug28846(String subject, String location,
			String attendees, String body) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String currentLoggedInUser = SelNGBase.selfAccountName;
		String user1 = ProvZCS.getRandomAccount();
		String user2 = ProvZCS.getRandomAccount();

		resetSession();
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
		resetSession();
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
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
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
		resetSession();
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
		resetSession();
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
	public void dontReceiveAnyInvitePermission_Bug43046(String subject,
			String location, String attendees, String body) throws Exception {
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

		resetSession();
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

		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
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

		resetSession();
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

	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}
