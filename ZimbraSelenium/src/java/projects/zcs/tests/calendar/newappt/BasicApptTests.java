package projects.zcs.tests.calendar.newappt;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.CalCompose;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class BasicApptTests extends CommonTest {
	@DataProvider(name = "apptCreateDataProvider")
	private Object[][] createData(Method method) {
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
		} else if (test.equals("deleteAppt_Bug38359")) {
			return new Object[][] { { getLocalizedData(1), getLocalizedData(1),
					localize(locator.busy), localize(locator._public), "", "1",
					"", "", "", "", localize(locator.everyWeek), "",
					"ccuser@testdomain.com", getLocalizedData(3) } };
		} else if (test.equals("editAppt") || test.equals("moveAppt")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), "ccuser@testdomain.com",
					getLocalizedData(3), getLocalizedData_NoSpecialChar() } };
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
	 * Enters different invalid email address types and checks if alert/warning
	 * dialog is thrown
	 */
	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createSimpleAppt(String subject, String location,
			String attendees, String body) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);

		needReset = false;
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createAppt(String subject, String location, String showAs,
			String markAs, String calendar, String allDayEvent,
			String startDate, String endDate, String startTime, String endTime,
			String repeat, String reminder, String attendees, String body)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateAppt(subject, location, showAs, markAs,
				calendar, allDayEvent, startDate, endDate, startTime, endTime,
				repeat, reminder, attendees, body);
		obj.zAppointment.zExists(subject);

		needReset = false;
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteAppt(String subject, String location, String attendees,
			String body) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);
		page.zCalApp.zDeleteAppointmentWithAttendees(subject);
		obj.zAppointment.zNotExists(subject);

		needReset = false;
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteAppt_Bug38359(String subject, String location,
			String showAs, String markAs, String calendar, String allDayEvent,
			String startDate, String endDate, String startTime, String endTime,
			String repeat, String reminder, String attendees, String body)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		for (int i = 0; i <= 2; i++) {
			page.zCalApp.zNavigateToCalendar();
			page.zCalCompose.zCreateAppt(subject, location, showAs, markAs,
					calendar, allDayEvent, startDate, endDate, startTime,
					endTime, repeat, reminder, attendees, body);
			obj.zAppointment.zExists(subject);
		}
		page.zCalApp.zCalViewSwitch("list");
		selenium.clickAt(("//*[@id=\"zlhi__CLL__se\"]"), "");
		obj.zButton.zClick(page.zCalApp.zCalDeleteBtn);
		obj.zRadioBtn.zClickInDlgByName(localize(locator.deleteSeries),
				localize(locator.deleteRecurringItem));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.deleteRecurringItem));
		obj.zAppointment.zNotExists(subject);

		needReset = false;
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editAppt(String subject, String location, String attendees,
			String body, String newSubject) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		page.zCalCompose.zEditAppointment(subject, newSubject);
		obj.zAppointment.zNotExists(subject);
		obj.zAppointment.zExists(newSubject);

		needReset = false;
	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveAppt(String subject, String location, String attendees,
			String body, String newCalendar) throws Exception {
		if (isExecutionARetry)
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

		needReset = false;
	}

	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}

}
