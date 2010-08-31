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
public class CreateCalendar extends CommonTest {
	@DataProvider(name = "dataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("createCalendarFolder")
				|| test.equals("tryToCreateDuplicateCalendarFolder")) {
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
	 * Creates a calendar folder using the overview button and right click menu
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createCalendarFolder() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String calendarNameBtn = getLocalizedData_NoSpecialChar();
		String calendarNameRtClick = getLocalizedData_NoSpecialChar();
		page.zCalApp.zCreateNewCalendarFolder(calendarNameBtn);
		page.zCalApp.zCreateNewCalendarFolder(calendarNameRtClick);
		obj.zCalendarFolder.zExists(calendarNameBtn);
		obj.zCalendarFolder.zExists(calendarNameRtClick);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateCalendarFolder() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String calendarName = getLocalizedData_NoSpecialChar();
		page.zCalApp.zCreateNewCalendarFolder(calendarName);

		obj.zButton
				.zRtClick(replaceUserNameInStaticId(page.zCalApp.zNewCalOverviewPaneIcon));
		SleepUtil.sleep(1000);
		obj.zMenuItem.zClick(localize(locator.newCalendar));
		obj.zEditField.zTypeInDlgByName(localize(locator.nameLabel),
				calendarName, localize(locator.createNewCalendar));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewCalendar));
		assertReport(localize(locator.errorAlreadyExists, calendarName, ""),
				obj.zDialog.zGetMessage(localize(locator.criticalMsg)),
				"Verifying dialog message");
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.criticalMsg));
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewCalendar));

		SelNGBase.needReset.set(false);
	}
}
