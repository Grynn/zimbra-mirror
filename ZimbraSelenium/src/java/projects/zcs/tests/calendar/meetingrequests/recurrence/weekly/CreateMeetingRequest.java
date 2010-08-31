package projects.zcs.tests.calendar.meetingrequests.recurrence.weekly;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.tools.ant.taskdefs.WaitFor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.service.admin.GetConfig;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings({ "static-access", "unused" })
public class CreateMeetingRequest extends CommonTest {
	@DataProvider(name = "apptInviteTestDataProvider")
	private Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("apptRecurringInviteContentVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData(1), localize(locator.everyWeek),
					ProvZCS.getRandomAccount(), getLocalizedData(3) } };
		} else if (test.equals("apptModifyRecurringCheckContent")) {
			return new Object[][] {
					{ "instance", getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							ProvZCS.getRandomAccount(), getLocalizedData(3),
							localize(locator.everyWeek),
							"10:00:" + localize(locator.periodAm),
							"11:00:" + localize(locator.periodAm),
							getLocalizedData_NoSpecialChar(), "",
							"11:00:" + localize(locator.periodAm),
							"12:00:" + localize(locator.periodPm) },
					{ "series", getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							ProvZCS.getRandomAccount(), getLocalizedData(3),
							localize(locator.everyWeek),
							"10:00:" + localize(locator.periodAm),
							"11:00:" + localize(locator.periodAm),
							getLocalizedData_NoSpecialChar(), "",
							"11:00:" + localize(locator.periodAm),
							"12:00:" + localize(locator.periodPm) }, };
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
	 * Sends a meeting invite to some user Verifies that the recurring
	 * appointment invite content has all the information
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptRecurringInviteContentVerify(String subject,
			String location, String recurring, String attendees, String body)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String organizer;
		organizer = SelNGBase.selfAccountName.get();
		page.zCalApp.zNavigateToCalendar();
		// obj.zButton.zClick(page.zCalApp.zCalWeekBtn);
		page.zCalCompose.zCreateAppt(subject, location, "", "", "", "", "", "",
				"", "", recurring, "", attendees, body);
		obj.zAppointment.zExists(subject);

		resetSession();
		SleepUtil.sleep(1000);
		SelNGBase.selfAccountName.set(attendees);
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		String[] itemsToVerify = { subject, organizer.toLowerCase(), location,
				attendees, body, localize(locator.recurrence) };
		page.zCalApp.zVerifyInviteContent(localize(locator.apptNew),
				itemsToVerify);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "apptInviteTestDataProvider", groups = { "smoke",
			"full" }, retryAnalyzer = RetryFailedTests.class)
	public void apptModifyRecurringCheckContent(String seriesOrInstance,
			String oldSubject, String oldLocation, String attendees,
			String body, String recurring, String startTime, String endTime,
			String newSubject, String newLocation, String newStartTime,
			String newEndTime) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject;
		String verifySubject;
		String verifyLocation;
		String verifyTime;
		String firstLineSummary;
		page.zCalApp.zNavigateToCalendar();
		page.zCalCompose.zCreateAppt(oldSubject, oldLocation, "", "", "", "",
				"", "", startTime, endTime, recurring, "", attendees, body);
		if (seriesOrInstance.equals("instance")) {
			page.zCalApp.zOpenInstanceOfRecurringAppt(oldSubject);
			firstLineSummary = localize(locator.apptInstanceModified);
		} else if (seriesOrInstance.equals("series")) {
			page.zCalApp.zOpenSeriesRecurringAppt(oldSubject);
			firstLineSummary = localize(locator.apptModified);
		} else {
			page.zCalApp.zOpenInstanceOfRecurringAppt(oldSubject);
			firstLineSummary = localize(locator.apptInstanceModified);
		}
		page.zCalCompose.zCalendarEnterDetails(newSubject, newLocation, "", "",
				"", "", "", "", newStartTime, newEndTime, "", "", "", "");
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		SleepUtil.sleep(1000);
		zWaitTillObjectExist("button", page.zCalApp.zCalNewApptBtn);
		if (!newSubject.equals("")) {
			verifySubject = newSubject + " "
					+ localize(locator.apptModifiedStamp);
			subject = newSubject;
		} else {
			verifySubject = oldSubject;
			subject = oldSubject;
		}
		if (!newLocation.equals("")) {
			verifyLocation = newLocation + " "
					+ localize(locator.apptModifiedStamp);
		} else {
			verifyLocation = "";
		}
		if (!newStartTime.equals("") || !newEndTime.equals("")) {
			verifyTime = localize(locator.apptModifiedStamp);
		} else {
			verifyTime = "";
		}

		String[] itemsToVerify = { verifySubject, verifyLocation, verifyTime };
		resetSession();
		SleepUtil.sleep(1000);
		SelNGBase.selfAccountName.set(attendees);
		page.zLoginpage.zLoginToZimbraAjax(attendees);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(2500);
		page.zCalApp.zVerifyInviteContent(firstLineSummary, itemsToVerify);

		SelNGBase.needReset.set(false);
	}

	private void waitForIE() throws Exception {
		String browser = ZimbraSeleniumProperties.getStringProperty("browser");
		if (browser.equals("IE"))
			SleepUtil.sleep(2000);

	}

	private void waitForSF() throws Exception {
		String browser = ZimbraSeleniumProperties.getStringProperty("browser");
		if (browser.equals("SF"))
			SleepUtil.sleep(2000);
	}
}
