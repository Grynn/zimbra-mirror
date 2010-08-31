package projects.zcs.tests.calendar.meetingrequests;

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

@SuppressWarnings({ "static-access", "unused" })
public class CreateMeetingRequest extends CommonTest {
	@DataProvider(name = "dataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("createAppt")) {
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

	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
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
}
