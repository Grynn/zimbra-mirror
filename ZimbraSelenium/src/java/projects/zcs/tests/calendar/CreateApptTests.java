package projects.zcs.tests.calendar;

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
public class CreateApptTests extends CommonTest {

	@DataProvider(name = "apptCreateDataProvider")
	private Object[][] createData(Method method) {

		String test = method.getName();

		if (test.equals("createSimpleAppt") || test.equals("deleteAppt")
				|| test.equals("calVerifyListView")) {

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

		} else if (test.equals("modifyMonthlyEvent_Bug_40771")) {

			return new Object[][] {

			{ getLocalizedData(1), getLocalizedData(1), localize(locator.busy),
					localize(locator._public), "", "1", "", "", "", "",
					localize(locator.everyMonth), "", "ccuser@testdomain.com",
					getLocalizedData(3) } };

		} else if (test.equals("deleteAppt_Bug38359")) {

			return new Object[][] {

			{ getLocalizedData(1), getLocalizedData(1), localize(locator.busy),
					localize(locator._public), "", "1", "", "", "", "",
					localize(locator.everyWeek), "", "ccuser@testdomain.com",
					getLocalizedData(3) } };

		} else if (test.equals("openAnd_modifyRecurringInstance_Bug_40124")) {

			return new Object[][] {

					{ getLocalizedData(1), getLocalizedData(1),
							localize(locator.busy), localize(locator._public),
							"", "1", "", "", "", "",
							localize(locator.everyDay), "",
							"ccuser@testdomain.com", getLocalizedData(3) },

					{ getLocalizedData(1), getLocalizedData(1),
							localize(locator.busy), localize(locator._public),
							"", "1", "", "", "", "",
							localize(locator.everyWeek), "",
							"ccuser@testdomain.com", getLocalizedData(3) },

					{ getLocalizedData(1), getLocalizedData(1),
							localize(locator.busy), localize(locator._public),
							"", "1", "", "", "", "",
							localize(locator.everyMonth), "",
							"ccuser@testdomain.com", getLocalizedData(3) },
					{ getLocalizedData(1), getLocalizedData(1),
							localize(locator.busy), localize(locator._public),
							"", "1", "", "", "", "",
							localize(locator.everyYear), "",
							"ccuser@testdomain.com", getLocalizedData(3) } };

		} else if (test.equals("editAppt") || test.equals("moveAppt")
				|| test.equals("createApptInCalendar")
				|| test.equals("editAnd_searchAppt_Bug39463")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), "ccuser@testdomain.com",
					getLocalizedData(3), getLocalizedData_NoSpecialChar() } };
		}if (test.equals("updateResourceLocationAndVerify_Bug43698") ) {

			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					"",
					"ccuser@testdomain.com, bccuser@testdomain.com",
					getLocalizedData(3) } };

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
	 * Enters different invalid email address types and checks if alert/warning
	 * dialog is thrown
	 */
	@Test(dataProvider = "apptCreateDataProvider", groups = { "full", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createSimpleAppt(String subject, String location,
			String attendees, String body) throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);

		obj.zAppointment.zExists(subject);

		needReset = false;
	}

	/**
	 * Verifies appointments are displayed in list view
	 * 
	 */
	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void calVerifyListView(String subject, String location,
			String attendees, String body) throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		page.zCalApp.zCalViewSwitch("week");

		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);

		page.zCalApp.zCalViewSwitch("list");

		obj.zMessageItem.zExists(subject);

		page.zCalApp.zCalViewSwitch("week");

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
	public void modifyMonthlyEvent_Bug_40771(String subject, String location,
			String showAs, String markAs, String calendar, String allDayEvent,
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
		obj.zAppointment.zDblClick(subject);
		obj.zDialog.zExists(localize(locator.openRecurringItem));
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.openRecurringItem));

		needReset = false;

	}

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void openAnd_modifyRecurringInstance_Bug_40124(String subject,
			String location, String showAs, String markAs, String calendar,
			String allDayEvent, String startDate, String endDate,
			String startTime, String endTime, String repeat, String reminder,
			String attendees, String body) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateAppt(subject, location, showAs, markAs,
				calendar, allDayEvent, startDate, endDate, startTime, endTime,
				repeat, reminder, attendees, body);
		obj.zAppointment.zDblClick(subject);
		Thread.sleep(1000);
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.openRecurringItem));
		zWaitTillObjectExist("button", page.zCalApp.zApptSaveBtn);
		obj.zButton.zClick(page.zCalCompose.zApptCloseBtn);
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
	public void editAnd_searchAppt_Bug39463(String subject, String location,
			String attendees, String body, String newSubject) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		page.zCalCompose.zCreateSimpleAppt(subject + "Original", location,
				attendees, body);

		page.zCalApp.zCalViewSwitch("list");
		selenium.type("//input[@type='text']", subject + "*");
		obj.zListItem.zExists(subject);
		obj.zListItem.zExists(subject + "Original");
		page.zCalApp.zCalViewSwitch("week");

		newSubject = "123456";
		page.zCalCompose.zEditAppointment(subject, newSubject);
		page.zCalApp.zCalViewSwitch("list");
		selenium.type("//input[@type='text']", newSubject);
		obj.zButton.zClick(localize(locator.search));
		obj.zListItem.zNotExists(subject + "Original");
		obj.zListItem.zExists(newSubject);

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

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createApptInCalendar(String subject, String location,
			String attendees, String body, String calendar) throws Exception {

		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		page.zCalApp.zCreateNewCalendarFolder(calendar);

		page.zCalCompose.zCreateSimpleApptInCalendar(subject, location,
				attendees, body, calendar);

		obj.zAppointment.zExists(subject);

		needReset = false;

	}
	/**
	 * Test case: When a user changes resource location it is should  reflected properly in calendar view
	 * 1.Create 2 test resources with the admin console or directly through command line
	 * 2.Create an appointment.  Find location.  Select resource1 from dropdown list
	 * 3.Verify Resources:= testresouce1 and save it
	 * 4.Edit the same appointment
	 * 5.Again verify that "Location "field and  "Resources:" field should contains testresource1.
	 * 6.Update Location Field with testresouce2
	 * 7.Verify same resource reflected in "Resources:" field as testresource2.
	 * 8.Save edited appointment
	 * 9.Again open same appointment and  verify that "Location "field and  "Resources:" field should contains testresource2.
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @throws Exception
	 * @author Girish
	 */

	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void updateResourceLocationAndVerify_Bug43698(String subject, String location,
			String attendees, String body) throws Exception {


		if (isExecutionARetry)
			handleRetry();

		ProvZCS.createResource("testResource1@testdomain.com");
		ProvZCS.createResource("testResource2@testdomain.com");
		String selfattendees = SelNGBase.selfAccountName;

		page.zCalApp.zNavigateToCalendar();

		String testResource1 = "testResource";

		page.zCalApp.zNavigateToApptCompose();
		CalCompose
				.zCalendarEnterSimpleDetails(subject, "", selfattendees, body);
		
		obj.zEditField.zType(localize(locator.locationLabel), testResource1);
		selenium.keyDown("xpath=//td[contains(@id,'_location')]/div/input",
				"\\13");
		selenium.keyUp("xpath=//td[contains(@id,'_location')]/div/input",
				"\\13");
		Thread.sleep(2000);
		selenium
				.mouseOver("xpath=//div[contains(@id,'AutoCompleteListViewDiv_0') and contains(text(),'testResource1@testdomain.com')]");
		selenium
				.mouseDown("xpath=//div[contains(@id,'AutoCompleteListViewDiv_0') and contains(text(),'testResource1@testdomain.com')]");
		selenium
				.mouseUp("xpath=//div[contains(@id,'AutoCompleteListViewDiv_0') and contains(text(),'testResource1@testdomain.com')]");

		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//td[contains(@id,'_resourcesData')]/div/a[contains(text(),'testResource1@testdomain.com')]"),
						"testResource1 does not displayed");

		obj.zButton.zClick(CalCompose.zApptSaveBtn);
		Thread.sleep(2000);

		if (selenium
				.isElementPresent("xpath=//td[contains(@id,'_title') and contains(text(),'"
						+ localize(locator.resourceConflictLabel) + "')]")) {

			obj.zButton.zClickInDlgByName("Save",
					localize(locator.resourceConflictLabel));
		}
		zWaitTillObjectExist("button", page.zCalApp.zCalNewApptBtn);
		obj.zAppointment.zExists(subject);

		// Edit
		obj.zAppointment.zDblClick(subject);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				localize(locator.location)).contains(
				"testResource1@testdomain.com"),
				"After edit location field does not shows resource1");
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//td[contains(@id,'_resourcesData')]/div/a[contains(text(),'testResource1@testdomain.com')]"),
						"After edit Resource does not shows resource1");
		obj.zEditField
				.zActivate(getNameWithoutSpace(localize(locator.location)));
		Robot zRobot = new Robot();
		zRobot.keyPress(KeyEvent.VK_END);
		zRobot.keyRelease(KeyEvent.VK_END);
		Thread.sleep(100);
		zRobot.keyPress(KeyEvent.VK_SHIFT);
		zRobot.keyPress(KeyEvent.VK_HOME);
		zRobot.keyRelease(KeyEvent.VK_SHIFT);// need to release shiftkyes
		zRobot.keyRelease(KeyEvent.VK_HOME);
	//	selenium.typeKeys("xpath=//td[contains(@id,'_location')]/div/input",testResource1);
		obj.zEditField.zType(localize(locator.locationLabel), testResource1);
		selenium.keyDown("xpath=//td[contains(@id,'_location')]/div/input",
				"\\13");
		selenium.keyUp("xpath=//td[contains(@id,'_location')]/div/input",
				"\\13");
		Thread.sleep(1000);
		selenium
				.mouseOver("xpath=//div[contains(@id,'AutoCompleteListViewDiv_1') and contains(text(),'testResource2@testdomain.com')]");
		selenium
				.mouseDown("xpath=//div[contains(@id,'AutoCompleteListViewDiv_1') and contains(text(),'testResource2@testdomain.com')]");
		selenium
				.mouseUp("xpath=//div[contains(@id,'AutoCompleteListViewDiv_1') and contains(text(),'testResource2@testdomain.com')]");
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//td[contains(@id,'_resourcesData')]/div/a[contains(text(),'testResource2@testdomain.com')]"),
						"After edit  with testresource2 then resource does not reflected with testresouce2");
		obj.zButton.zClick(CalCompose.zApptSaveBtn);
		Thread.sleep(2000);
		if (selenium
				.isElementPresent("xpath=//td[contains(@id,'_title') and contains(text(),'"
						+ localize(locator.resourceConflictLabel) + "')]")) {

			obj.zButton.zClickInDlgByName("Save",
					localize(locator.resourceConflictLabel));
		}
		zWaitTillObjectExist("button", page.zCalApp.zCalNewApptBtn);
		obj.zAppointment.zExists(subject);
		obj.zAppointment.zDblClick(subject);
		Assert.assertTrue(obj.zEditField.zGetInnerText(
				localize(locator.location)).contains(
				"testResource2@testdomain.com"));
		Assert
				.assertTrue(selenium
						.isElementPresent("xpath=//td[contains(@id,'_resourcesData')]/div/a[contains(text(),'testResource2@testdomain.com')]"));


		needReset = false;
	}

	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}

}
