//Test cases related to calendar appointments
//Krishna Kumar Sure

package projects.html.tests.calendar;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.html.tests.CommonTest;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;

@SuppressWarnings( { "static-access", "unused" })
public class CalendarFolderTests extends CommonTest {

	@DataProvider(name = "CreateApptsDataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();

		// final String lastName = getLocalizedData_NoSpecialChar();
		if (test.equals("createCalendarFolder")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					localize(locator.green) } };
		} else if (test.equals("createApptInCalendarFolder")
				|| test.equals("uncheckCalendar")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					localize(locator.blue), getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					Stafzmprov.getRandomAccount(), getLocalizedData(3) } };
		} else if (test.equals("subscribeCalendar")) {
			return new Object[][] { {
					"webcal://ical.mac.com/ical/US32Holidays.ics",
					getLocalizedData_NoSpecialChar(),
					"http://" + ZimbraSeleniumProperties.getStringProperty("server")
							+ "/zimbra/h/calendar?date=20091225" } };
		} else if (test.equals("verifyDeleteConfirmationAsked")
				|| test.equals("verifyDeleteAllAppts")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					Stafzmprov.getRandomAccount(), getLocalizedData(3) } };
		} else
			return new Object[][] { {} };
	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	/**
	 * Creates a calendar folder
	 * 
	 * @param calName
	 *            : folder to be created
	 * @param color
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createCalendarFolder(String calName, String color)
			throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String expectedMsg = localize(locator.actionCalendarCreated).replace(
				"{0}", calName);

		page.zCalFolderApp.zCreateNewCalendar(calName, color);

		SleepUtil.sleepSmall();

		if (!ZimbraSeleniumProperties.getStringProperty("locale").equals("fr"))
			obj.zToastAlertMessage.zAlertMsgExists(expectedMsg,
					"Toast message incorrect");

		page.zCalendarApp.zNavigateToCalendar();

		obj.zCalendarFolder.zExists(calName);

		SelNGBase.needReset.set(false);

	}

	/**
	 * Creates a appointment in calendar folder
	 * 
	 * @param calName
	 * @param color
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createApptInCalendarFolder(String calName, String color,
			String subject, String location, String attendees, String body)
			throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalFolderApp.zCreateNewCalendar(calName, color);

		page.zCalendarApp.zCreateApptInCalendar(subject, location, attendees,
				body, calName);

		obj.zAppointment.zExists(subject);

		SelNGBase.needReset.set(false);

	}

	/**
	 * Verifies unchecking a calendar folder doesn't show appointment
	 * 
	 * @param calName
	 * @param color
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void uncheckCalendar(String calName, String color, String subject,
			String location, String attendees, String body) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalFolderApp.zCreateNewCalendar(calName, color);

		page.zCalendarApp.zCreateApptInCalendar(subject, location, attendees,
				body, calName);

		obj.zCalendarFolder.zClick(calName);

		SleepUtil.sleepMedium(); //unchecking checking takes some time

		obj.zAppointment.zNotExists(subject);

		obj.zCalendarFolder.zClick(calName);

		SleepUtil.sleepMedium(); //unchecking checking takes some time

		obj.zAppointment.zExists(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Subscribe calendar to an external url
	 * 
	 * @param url
	 * @param calName
	 * @param urlToNavigate
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void subscribeCalendar(String url, String calName,
			String urlToNavigate) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalFolderApp.zSubscribeToCalendar(calName, url);

		SelNGBase.selenium.get().open(urlToNavigate);

		obj.zButton.zClick(page.zCalendarApp.calRefresh);

		obj.zAppointment.zExists("Christmas");

		obj.zButton.zClick(page.zCalendarApp.calTodayBtn);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Verify toast message on deleting all appointments of a calendar without
	 * checking the confirmation checkbox
	 * 
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyDeleteConfirmationAsked(String subject, String location,
			String attendees, String body) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalendarApp.zCreateSimpleAppt(subject, location, attendees, body);

		page.zCalFolderApp.zNavigateToCalendarFoldersPage();

		obj.zButton.zClick(page.zCalFolderApp.calDeleteAllApptsBtn);

		String actualMsg = obj.zToastAlertMessage.zGetMsg();

		Assert.assertTrue(actualMsg
				.contains(localize(locator.actionDeleteCheckConfirm)),
				"Toast message to check the checkbox is incorrect");

		SelNGBase.needReset.set(false);

	}

	/**
	 * Verify all appointments of a calendar can be deleted
	 * 
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyDeleteAllAppts(String subject, String location,
			String attendees, String body) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String calName = getLocalizedData_NoSpecialChar();

		page.zCalFolderApp.zCreateNewCalendar(calName, "");

		page.zCalendarApp.zCreateApptInCalendar(subject, location, attendees,
				body, calName);

		page.zCalFolderApp.zDeleteAllApptsOfCalendar(calName);

		obj.zButton.zClick(page.zCalFolderApp.calDeleteAllApptsBtn);

		page.zCalendarApp.zNavigateToCalendar();

		obj.zAppointment.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Verify deletion of a calendar
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyDeleteCalendar() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String calName = getLocalizedData_NoSpecialChar();

		page.zCalFolderApp.zCreateNewCalendar(calName, "");

		obj.zCalendarFolder.zExists(calName);

		page.zCalFolderApp.zDeleteCalendar(calName);

		obj.zCalendarFolder.zNotExists(calName);

		SelNGBase.needReset.set(false);
	}

	/**
	 *Renames a calendar and verifies if calendar is renamed
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void calendarRename() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String calName = getLocalizedData_NoSpecialChar();
		String renameCalName = getLocalizedData_NoSpecialChar();

		page.zCalFolderApp.zCreateNewCalendar(calName, "");

		page.zCalFolderApp.zRenameCalendar(calName, renameCalName);

		obj.zCalendarFolder.zExists(renameCalName);

		obj.zCalendarFolder.zNotExists(calName);

		SelNGBase.needReset.set(false);

	}

	/**
	 *Renames a calendar and verifies menu contains the renamed field
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void calendarRenameVerifyInMenu() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String calName = getLocalizedData_NoSpecialChar();
		String renameCalName = getLocalizedData_NoSpecialChar();

		page.zCalFolderApp.zCreateNewCalendar(calName, "");

		page.zCalFolderApp.zRenameCalendar(calName, renameCalName);

		page.zCalendarApp.zNavigateToCalendar();

		page.zCalendarApp.zNavigateToCalendarCompose();

		obj.zHtmlMenu.zClick(page.zCalendarApp.apptComposeCalendarDropdown,
				renameCalName);

		SelNGBase.needReset.set(false);

	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}

}
