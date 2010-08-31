package projects.zcs.tests.calendar.folders;

//import java.lang.reflect.Method;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

//import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

@SuppressWarnings({ "static-access", "unused" })
public class DeleteCalendar extends CommonTest {
	@DataProvider(name = "dataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("deleteCalendarFolder")) {
			return new Object[][] { {} };
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
	 * Deletes a calendar folder and verifies that the folder is deleted
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteCalendarFolder() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String deleteCalendarName = getLocalizedData_NoSpecialChar();
		page.zCalApp.zCreateNewCalendarFolder(deleteCalendarName);
		obj.zCalendarFolder.zExists(deleteCalendarName);
		page.zCalApp.zDeleteCalendarFolder(deleteCalendarName);
		obj.zCalendarFolder.zNotExists(deleteCalendarName);

		SelNGBase.needReset.set(false);
	}

}
