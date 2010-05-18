package projects.zcs.tests.calendar;

//import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

//import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.util.RetryFailedTests;

import projects.zcs.tests.CommonTest;

@SuppressWarnings( { "static-access" })
public class CalendarFolderTests extends CommonTest {

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		Thread.sleep (2000);
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
	 * Creates a calendar folder using the overview button and right click menu
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createCalendar() throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		String calendarNameBtn = "calendarNewBtn";
		String calendarNameRtClick = "calendarRtClick";

		page.zCalApp.zCreateNewCalendarFolder(calendarNameBtn);
		page.zCalApp.zCreateNewCalendarFolder(calendarNameRtClick);

		obj.zCalendarFolder.zExists(calendarNameBtn);
		obj.zCalendarFolder.zExists(calendarNameRtClick);
		needReset = false;
	}

	/**
	 * Deletes a calendar folder and verifies that the folder is deleted
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteCalendar() throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		String deleteCalendarName = "deleteCalendar";

		page.zCalApp.zCreateNewCalendarFolder(deleteCalendarName);

		obj.zCalendarFolder.zExists(deleteCalendarName);

		page.zCalApp.zDeleteCalendarFolder(deleteCalendarName);

		obj.zCalendarFolder.zNotExists(deleteCalendarName);

		needReset = false;

	}

	/**
	 * renames a calendar and verifies that the calendar is renamed
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void renameCalendar() throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		page.zCalApp.zNavigateToCalendar();

		String calendarName = "renameCalendar";

		String newCalendarName = "newRenamedCalendar";

		page.zCalApp.zCreateNewCalendarFolder(calendarName);

		obj.zCalendarFolder.zExists(calendarName);

		page.zCalApp.zRenameCalendarFolder(calendarName, newCalendarName);

		obj.zCalendarFolder.zNotExists(calendarName);

		obj.zCalendarFolder.zExists(newCalendarName);

		needReset = false;

	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		page.zComposeView.zGoToMailAppFromCompose();
		zLogin();
	}

}
