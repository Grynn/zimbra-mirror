package projects.zcs.tests.calendar.meetingrequests.rsvp;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;

@SuppressWarnings({ "static-access", "unused" })
public class AcceptMeetingRequest extends CommonTest {
	@DataProvider(name = "dataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("acceptAppt")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), Stafzmprov.getRandomAccount(),
					getLocalizedData(3), "accept" } };
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

	/**
	 * Sends meeting invite to attendees and verifies that attendee can
	 * Accept/Decline/Tentative the appointment
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void acceptAppt(String subject, String location, String attendees,
			String body, String action) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);

		resetSession();
		SleepUtil.sleep(1000);
		SelNGBase.selfAccountName.set(attendees);
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		if (action.equals("accept"))
			page.zCalApp.zAcceptInvite(subject);
		SleepUtil.sleep(1500);
		obj.zMessageItem.zNotExists(subject);
		obj.zButton.zNotExists(localize(locator.replyAccept));

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
