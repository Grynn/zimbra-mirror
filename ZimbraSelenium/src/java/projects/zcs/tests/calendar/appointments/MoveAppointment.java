package projects.zcs.tests.calendar.appointments;

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


import projects.zcs.tests.CommonTest;
import projects.zcs.ui.CalCompose;
import projects.zcs.ui.MailApp;

@SuppressWarnings({ "static-access", "unused" })
public class MoveAppointment extends CommonTest {
	@DataProvider(name = "dataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("moveAppt")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), "ccuser@testdomain.com",
					getLocalizedData(3), getLocalizedData_NoSpecialChar() } };
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
	public void moveAppt(String subject, String location, String attendees,
			String body, String newCalendar) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
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

		SelNGBase.needReset.set(false);
	}
}
