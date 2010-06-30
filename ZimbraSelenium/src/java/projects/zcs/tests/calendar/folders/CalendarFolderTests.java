package projects.zcs.tests.calendar.folders;

//import java.lang.reflect.Method;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

//import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.util.RetryFailedTests;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

@SuppressWarnings( { "static-access", "unused" })
public class CalendarFolderTests extends CommonTest {
	@DataProvider(name = "apptCreateDataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("createCalendarFolder")
				|| test.equals("deleteCalendarFolder")
				|| test.equals("renameCalendarFolder")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1),
					"ccuser@testdomain.com, bccuser@testdomain.com",
					getLocalizedData(3) } };
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
	 * Creates a calendar folder using the overview button and right click menu
	 */
	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createCalendarFolder() throws Exception {
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
	 */
	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteCalendarFolder() throws Exception {
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
	 */
	@Test(dataProvider = "apptCreateDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void renameCalendarFolder() throws Exception {
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
