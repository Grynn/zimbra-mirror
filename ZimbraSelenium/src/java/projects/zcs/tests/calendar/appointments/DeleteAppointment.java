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
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;


import projects.zcs.tests.CommonTest;
import projects.zcs.ui.CalCompose;
import projects.zcs.ui.MailApp;

@SuppressWarnings({ "static-access", "unused" })
public class DeleteAppointment extends CommonTest {
	@DataProvider(name = "dataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("deleteAppt")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1),
					"ccuser@testdomain.com, bccuser@testdomain.com",
					getLocalizedData(3) } };
		} else if (test.equals("deleteApptByKeyBoard_Bug35866")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), Stafzmprov.getRandomAccount(),
					getLocalizedData(3) } };
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
	public void deleteAppt(String subject, String location, String attendees,
			String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		obj.zAppointment.zExists(subject);
		page.zCalApp.zDeleteAppointmentWithAttendees(subject);
		obj.zAppointment.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * TestCase:-Deleting appointment using keyboard.
	 * 
	 * @steps 1.login to mail client 2.go to calendar 3.select an appointment
	 *        and hit Delete key on the keyboard 4.Verify Appointment should get
	 *        deleted.
	 * @author Girish
	 */

	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteApptByKeyBoard_Bug35866(String subject, String location,
			String attendees, String body) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateSimpleAppt(subject, location, attendees, body);
		SleepUtil.sleep(1000);
		obj.zAppointment.zClick(subject);
		Robot zRobot = new Robot();
		zRobot.keyPress(KeyEvent.VK_DELETE);
		zRobot.keyRelease(KeyEvent.VK_DELETE);
		SleepUtil.sleep(1000);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.confirmTitle));
		SleepUtil.sleep(1000);
		obj.zAppointment.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}
}
