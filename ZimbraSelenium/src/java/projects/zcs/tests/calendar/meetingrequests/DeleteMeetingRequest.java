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

import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;


import projects.zcs.tests.CommonTest;
import projects.zcs.ui.CalCompose;
import projects.zcs.ui.MailApp;

@SuppressWarnings({ "static-access", "unused" })
public class DeleteMeetingRequest extends CommonTest {
	@DataProvider(name = "dataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("deleteAppt_Bug38150")) {
			return new Object[][] { { "single",
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					Stafzmprov.getRandomAccount(), getLocalizedData(3), "" } };
		} else if (test.equals("deleteAppt_Bug38359")) {
			return new Object[][] { { getLocalizedData(1), getLocalizedData(1),
					localize(locator.busy), localize(locator._public), "", "1",
					"", "", "", "", localize(locator.everyWeek), "",
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

	@Test(dataProvider = "dataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteAppt_Bug38150(String singleOrInstanceOrSeries,
			String subject, String location, String attendees, String body,
			String recurring) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String firstLineSummary;
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateAppt(subject, location, "", "", "", "", "", "",
				"", "", recurring, "", attendees, body);
		obj.zAppointment.zExists(subject);
		if (singleOrInstanceOrSeries.equals("instance")) {
			page.zCalApp.zDeleteInstanceOfRecurringAppt(subject);
			firstLineSummary = localize(locator.apptInstanceCanceled);
		} else if (singleOrInstanceOrSeries.equals("series")) {
			page.zCalApp.zDeleteSeriesRecurringAppt(subject);
			firstLineSummary = localize(locator.apptCanceled);
		} else {
			page.zCalApp.zDeleteAppointmentWithAttendees(subject);
			firstLineSummary = localize(locator.apptCanceled);
		}

		subject = "Edited Subject";
		body = "Edited body";
		obj.zEditField.zType(localize(locator.subject), subject);
		obj.zEditor.zType(body);
		obj.zButton.zClick(localize(locator.send));
		SleepUtil.sleep(10000);

		String[] itemsToVerify = { body };
		resetSession();
		SleepUtil.sleep(1000);
		
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(2000);
		page.zCalApp.zVerifyInviteContent(firstLineSummary, itemsToVerify);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteAppt_Bug38359(String subject, String location,
			String showAs, String markAs, String calendar, String allDayEvent,
			String startDate, String endDate, String startTime, String endTime,
			String repeat, String reminder, String attendees, String body)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		for (int i = 0; i <= 2; i++) {
			page.zCalApp.zNavigateToCalendar();
			page.zCalCompose.zCreateAppt(subject, location, showAs, markAs,
					calendar, allDayEvent, startDate, endDate, startTime,
					endTime, repeat, reminder, attendees, body);
			obj.zAppointment.zExists(subject);
		}
		page.zCalApp.zCalViewSwitch("list");
		ClientSessionFactory.session().selenium().clickAt(("//div[@id=\"zlhi__CLL__se\"]"), "");
		obj.zButton.zClick(page.zCalApp.zCalDeleteBtn);
		obj.zRadioBtn.zClickInDlgByName(localize(locator.deleteSeries),
				localize(locator.deleteRecurringItem));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.deleteRecurringItem));
		obj.zAppointment.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}
}
