package projects.zcs.tests.calendar.bugs;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


import projects.zcs.tests.CommonTest;
import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;

/**
 * @author
 */
@SuppressWarnings( { "static-access", "unused" })
public class CalendarBugs extends CommonTest {
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

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="calendar";
		super.zLogin();
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void subCalendarBreaks_Bug28846(String subject, String location,
			String attendees, String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String currentLoggedInUser = ClientSessionFactory.session().currentUserName();
		String user1 = Stafzmprov.getRandomAccount();
		String user2 = Stafzmprov.getRandomAccount();

		resetSession();
		String subject2 = getLocalizedData_NoSpecialChar();
		page.zLoginpage.zLoginToZimbraAjax(user1);
		
		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		page.zCalCompose.zCalendarEnterSimpleDetails(subject2, location,
				currentLoggedInUser, body);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		SleepUtil.sleep(2000);
		obj.zDialog.zNotExists(localize(locator.infoMsg));

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void setPermissionToReceiveAppt_Bug43046(String subject,
			String location, String attendees, String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String currentLoggedInUser;
		currentLoggedInUser = ClientSessionFactory.session().currentUserName();

		String user1_allowed = Stafzmprov.getRandomAccount();
		String user2_allowed = Stafzmprov.getRandomAccount();
		String user3_Notallowed = Stafzmprov.getRandomAccount();
		zGoToApplication("Preferences");
		zGoToPreferences("Calendar");
		obj.zRadioBtn.zClick(localize(locator.invitesAllowSome));
		obj.zTextAreaField.zType("id=CAL_INVITE_ACL_USERS*", user1_allowed
				+ "," + user2_allowed);
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		SleepUtil.sleep(2000);

		// -ve test case
		resetSession();
		String subject1 = getLocalizedData_NoSpecialChar();
		page.zLoginpage.zLoginToZimbraAjax(user3_Notallowed);
		
		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		page.zCalCompose.zCalendarEnterSimpleDetails(subject1, location,
				currentLoggedInUser, body);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		SleepUtil.sleep(2000);

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
		SleepUtil.sleep(2000);

		// positive test case
		resetSession();
		String subject2 = getLocalizedData_NoSpecialChar();
		page.zLoginpage.zLoginToZimbraAjax(user1_allowed);
		
		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		page.zCalCompose.zCalendarEnterSimpleDetails(subject2, location,
				currentLoggedInUser, body);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		SleepUtil.sleep(2000);
		obj.zDialog.zNotExists(localize(locator.infoMsg));

		// Verification for both case
		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(currentLoggedInUser);
		SleepUtil.sleep(2000);
		
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

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void dontReceiveAnyInvitePermission_Bug43046(String subject,
			String location, String attendees, String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String currentLoggedInUser;
		currentLoggedInUser = ClientSessionFactory.session().currentUserName();

		String user1 = Stafzmprov.getRandomAccount();
		zGoToApplication("Preferences");
		zGoToPreferences("Calendar");
		obj.zRadioBtn.zClick(localize(locator.invitesAllowNone));
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		SleepUtil.sleep(2000);

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(user1);
		
		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		page.zCalCompose.zCalendarEnterSimpleDetails(subject, location,
				currentLoggedInUser, body);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		SleepUtil.sleep(2000);
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
		SleepUtil.sleep(2000); // this wait is necessary, because zcs crashes
		// browser without sending email

		resetSession();
		page.zLoginpage.zLoginToZimbraAjax(currentLoggedInUser);
		
		SleepUtil.sleep(2000);
		String msgExists = obj.zMessageItem.zExistsDontWait(subject);
		// already enhancement 43340 for this bug otherwise it would be false
		assertReport(
				"true",
				msgExists,
				"Invite message not exists though user set preference not to receive invitation from this user");
		zGoToApplication("Calendar");
		obj.zAppointment.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}
}
