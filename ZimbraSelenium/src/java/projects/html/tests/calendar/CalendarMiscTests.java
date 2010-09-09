//Test cases related to calendar appointments
//Krishna Kumar Sure

package projects.html.tests.calendar;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.html.tests.CommonTest;
import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

@SuppressWarnings( { "static-access", "unused" })
public class CalendarMiscTests extends CommonTest {

	@DataProvider(name = "CreateApptsDataProvider")
	public Object[][] createData(Method method) throws Exception {
		String test = method.getName();

		// final String lastName = getLocalizedData_NoSpecialChar();
		if (test.equals("zNavigateToToday")) {
			return new Object[][] {
					{ "day", getLocalizedData_NoSpecialChar() },
					{ "week", getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("zVerifyPaginationButtons")) {
			return new Object[][] {
					{
							"day",
							"http://"
									+ ZimbraSeleniumProperties.getStringProperty("server")
									+ "/zimbra/h/calendar?view=day&date=20081015",
							"http://"
									+ ZimbraSeleniumProperties.getStringProperty("server")
									+ "/zimbra/h/calendar?view=day&date=20081016",
							"http://"
									+ ZimbraSeleniumProperties.getStringProperty("server")
									+ "/zimbra/h/calendar?view=day&date=20081014" },
					{
							"workWeek",
							"http://"
									+ ZimbraSeleniumProperties.getStringProperty("server")
									+ "/zimbra/h/calendar?view=workWeek&date=20081015",
							"http://"
									+ ZimbraSeleniumProperties.getStringProperty("server")
									+ "/zimbra/h/calendar?view=workWeek&date=20081022",
							"http://"
									+ ZimbraSeleniumProperties.getStringProperty("server")
									+ "/zimbra/h/calendar?view=workWeek&date=20081008" },
					{
							"month",
							"http://"
									+ ZimbraSeleniumProperties.getStringProperty("server")
									+ "/zimbra/h/calendar?view=month&date=20081015",
							"http://"
									+ ZimbraSeleniumProperties.getStringProperty("server")
									+ "/zimbra/h/calendar?view=month&date=20081115",
							"http://"
									+ ZimbraSeleniumProperties.getStringProperty("server")
									+ "/zimbra/h/calendar?view=month&date=20080915" },
					{
							"week",
							"http://"
									+ ZimbraSeleniumProperties.getStringProperty("server")
									+ "/zimbra/h/calendar?view=week&date=20081015",
							"http://"
									+ ZimbraSeleniumProperties.getStringProperty("server")
									+ "/zimbra/h/calendar?view=week&date=20081022",
							"http://"
									+ ZimbraSeleniumProperties.getStringProperty("server")
									+ "/zimbra/h/calendar?view=week&date=20081008" } };
		} else if (test.equals("zCalendarScheduleView")) {
			return new Object[][] { { "check" }, { "uncheck" } };
		} else if (test.equals("zCalPrefInitialView")) {
			return new Object[][] { { localize(locator.calViewDay) },
					{ localize(locator.calViewWorkWeek) },
					{ localize(locator.calViewMonth) },
					{ localize(locator.calViewWeek) } };
		} else if (test.equals("verifyFreeBusyView")) {
			return new Object[][] { { localize(locator.calViewDay) },
					{ localize(locator.calViewWorkWeek) },
					{ localize(locator.calViewWeek) } };
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
	 * Verifies free busy view
	 * 
	 * @param view
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyFreeBusyView(String view) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
				
		String urlInitial =  "http://" + ZimbraSeleniumProperties.getStringProperty("server") + "/zimbra/h/calendar";
		
		String urlToNavigate = "http://" + ZimbraSeleniumProperties.getStringProperty("server") + ":80/home/"
		+ ClientSessionFactory.session().currentUserName() + "?fmt=freebusy";
		
		if(null == someting || someting.trim().length() == 0)
			someting = getLocalizedData_NoSpecialChar();
		
		//open calendar tab
		ClientSessionFactory.session().selenium().open(urlInitial);
		
		SleepUtil.sleepMedium();
		
		//check if appointment exists and create if doesn't exist
		if(obj.zAppointment.zExistsDontWait(someting).equalsIgnoreCase("false"))
			page.zCalendarApp.zCreateSimpleAppt(someting, "", "", "");
				
		//open rest url
		ClientSessionFactory.session().selenium().open(urlToNavigate);

		SleepUtil.sleepMedium();

		//open different rest views
		if (view.equals(localize(locator.calViewDay)))
			obj.zButton.zClick(page.zCalendarApp.calDayViewBtn);
		else if (view.equals(localize(locator.calViewWorkWeek))){
			if(Calendar.getInstance().DAY_OF_WEEK!=6 &&
			   Calendar.getInstance().DAY_OF_WEEK!=7)
				obj.zButton.zClick(page.zCalendarApp.calWorkWeekViewBtn);
			else
				obj.zButton.zClick(page.zCalendarApp.calWeekViewBtn);
		}
		else if (view.equals(localize(locator.calViewWeek)))
			obj.zButton.zClick(page.zCalendarApp.calWeekViewBtn);
		
		SleepUtil.sleepMedium();

		//verify appointment exists in rest view
		obj.zAppointment.zExists("Busy");
		
		//return to initial page
		ClientSessionFactory.session().selenium().open(urlInitial);
		
		SelNGBase.needReset.set(false);
	}

	/**
	 * Verifies recurring appointments/exceptions/change of time zone
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyRepeatApptExceptionChangeTimezone() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject = getLocalizedData_NoSpecialChar();
		String location = getLocalizedData_NoSpecialChar();
		String url =  "http://" + ZimbraSeleniumProperties.getStringProperty("server") + "/zimbra/h/calendar";
		String timezone;
		
		//open calendar view
		ClientSessionFactory.session().selenium().open(url);
		SleepUtil.sleepMedium();
				
		// Navigate To Preferences view 
		obj.zButton.zClick(page.zCalendarApp.preferencesTab);
		SleepUtil.sleep(2000);	
		//get initial time zone value
		timezone = obj.zHtmlMenu.zGetSelectedItemName(page.zCalendarApp.apptComposeTimezoneDropdown);
		//set time zone to some value on general tab under preferences
		obj.zHtmlMenu.zClick(page.zCalendarApp.apptComposeTimezoneDropdown, obj.zHtmlMenu.zGetAllItemNames(page.zCalendarApp.apptComposeTimezoneDropdown).split("::")[0]);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABComposeHTML.zPrefSaveButton);
		SleepUtil.sleep(1000);
		//select calendar tab under preferences
		obj.zTab.zClick("/zimbra/h/options?selected=calendar");
		SleepUtil.sleep(1000);
		//set option to show time zone list 
		if(!obj.zCheckbox.zGetStatus(page.zCalendarApp.calPrefShowTimezoneCheckbox))
		obj.zCheckbox.zClick(page.zCalendarApp.calPrefShowTimezoneCheckbox);
		SleepUtil.sleep(1000);
		//set initial view
		obj.zHtmlMenu.zClick(page.zCalendarApp.calPrefInitialViewDropdown, localize(locator.calViewWeek));
		obj.zButton.zClick(page.zABComposeHTML.zPrefSaveButton);
		SleepUtil.sleep(1000);

		//navigate to calendar compose view
		page.zCalendarApp.zNavigateToCalendar();
		page.zCalendarApp.zNavigateToCalendarCompose();		
		page.zCalendarApp.zEnterSimpleApptDetails(subject, location, "", "");
		
		//set repeat options
		SleepUtil.sleep(2000);
		obj.zButton.zClick(page.zCalendarApp.apptComposeRepeatBtn);
		SleepUtil.sleep(3000);
		obj.zRadioBtn.zClick(page.zCalendarApp.apptRepeatRadioBtn, "3");
		SleepUtil.sleep(1000);
		obj.zRadioBtn.zClick(page.zCalendarApp.apptRepeatEndTypeRadioBtn, "2");
		obj.zEditField.zType(page.zCalendarApp.apptRepeatEndAfterOccurrencesEditField, "7");
		obj.zButton.zClick(page.zCalendarApp.apptRepeatDoneBtn);
		SleepUtil.sleep(3000);
		obj.zButton.zClick(page.zCalendarApp.apptComposeSaveBtn);
		SleepUtil.sleep(3000);
		
		//change time zone for single appointment
		obj.zAppointment.zClick(subject);
		SleepUtil.sleep(3000);
		obj.zHtmlMenu.zClick(page.zCalendarApp.apptComposeTimezoneDropdown, obj.zHtmlMenu.zGetAllItemNames(page.zCalendarApp.apptComposeTimezoneDropdown).split("::")[4]);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zCalendarApp.apptComposeSaveBtn);	
		SleepUtil.sleep(3000);
		
		//verify exception in a single appointment
		obj.zAppointment.zExistsDontWait("exception");
		
		// Navigate To Calendar Preferences and set time zone back to initial value 
		obj.zButton.zClick(page.zCalendarApp.preferencesTab);
		SleepUtil.sleep(1000);	
		obj.zHtmlMenu.zClick(page.zCalendarApp.apptComposeTimezoneDropdown, timezone);

		SelNGBase.needReset.set(false);
	}

	
	/**
	 * Verifies the free busy link formed for a calendar
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendFreeBusyLink() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String expectedMsg;
		expectedMsg = "http://" + ZimbraSeleniumProperties.getStringProperty("server") + ":80/home/"
				+ ClientSessionFactory.session().currentUserName() + "?fmt=freebusy";

		page.zCalFolderApp.zNavigateToCalendarFoldersPage();

		obj.zButton.zClick(page.zCalFolderApp.calFoldersSendFreeBusyLinkBtn);

		String actualMsg = obj.zTextAreaField.zGetInnerText("id=body");

		Assert.assertTrue(actualMsg.toLowerCase().contains(
				expectedMsg.toLowerCase()),
				"Incorrect free busy link is formed");

		SelNGBase.needReset.set(false);
	}

	/**
	 * Verifies that today button works properly
	 * 
	 * @param view
	 * @param subject
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zNavigateToToday(String view, String subject) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		// ////////////////////////////////////////////
		// Gets the current system date and adds 10 days to it.
		// ///////////////////////////////////////////
		String DATE_FORMAT = "yyyyMMdd";

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

		Calendar testcal = Calendar.getInstance();

		testcal.add(Calendar.DATE, -59);

		String dateToNavigate = sdf.format(testcal.getTime());

		String urlToNavigate = ZimbraSeleniumProperties.getStringProperty("mode") + "://"
				+ ZimbraSeleniumProperties.getStringProperty("server") + "/zimbra/h/calendar?view="
				+ view + "&date=" + dateToNavigate;

		page.zCalendarApp.zCreateSimpleAppt(subject, "", "", "");

		ClientSessionFactory.session().selenium().open(urlToNavigate);

		SleepUtil.sleepMedium();

		obj.zButton.zClick(page.zCalendarApp.calTodayBtn);

		SleepUtil.sleepMedium();

		obj.zAppointment.zExists(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Verifies the next/prev pagination buttons in calendar app
	 * 
	 * @param view
	 * @param url
	 * @param nextUrl
	 * @param prevUrl
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zVerifyPaginationButtons(String view, String url,
			String nextUrl, String prevUrl) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String browserTitle;
		String browserTitle2;

		ClientSessionFactory.session().selenium().open(url);
		SleepUtil.sleepMedium();

		obj.zButton.zClick(page.zCalendarApp.calNextPageBtn);

		SleepUtil.sleepMedium();

		browserTitle = ClientSessionFactory.session().selenium().getTitle();

		ClientSessionFactory.session().selenium().open(nextUrl);

		SleepUtil.sleepMedium();

		browserTitle2 = ClientSessionFactory.session().selenium().getTitle();

		Assert.assertTrue(browserTitle.equals(browserTitle2),
				"Next pagination button works fine");

		ClientSessionFactory.session().selenium().open(url);
		SleepUtil.sleepMedium();
		obj.zButton.zClick(page.zCalendarApp.calPrevPageBtn);

		SleepUtil.sleepMedium();

		browserTitle = ClientSessionFactory.session().selenium().getTitle();

		ClientSessionFactory.session().selenium().open(prevUrl);

		SleepUtil.sleepMedium();

		browserTitle2 = ClientSessionFactory.session().selenium().getTitle();

		Assert.assertTrue(browserTitle.equals(browserTitle2),
				"Prev pagination button works fine");

		obj.zButton.zClick(page.zCalendarApp.calTodayBtn);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Verifies that calendar editfield is disabled for "Calendar" and enabled
	 * for other calendar folders
	 * 
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zVerifyCalendarEditField() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String calName = getLocalizedData_NoSpecialChar();

		page.zCalFolderApp.zCreateNewCalendar(calName, "");

		// page.zCalFolderApp.zNavigateToCalendarProperties(localize(locator.
		// calendar));
		//		
		// obj.zEditField.zIsDisabled(page.zCalFolderApp.calNameField);

		// String kk =
		// obj.zEditField.zGetInnerHTML(page.zCalFolderApp.calNameField);

		page.zCalFolderApp.zNavigateToCalendarProperties(calName);

		obj.zEditField.zIsEnabled(page.zCalFolderApp.calNameField);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Verifies the schedule view in calendar app
	 * 
	 * @param calCheck
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalendarScheduleView(String calCheck) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String calName = getLocalizedData_NoSpecialChar();
		String subject1 = getLocalizedData_NoSpecialChar();
		String subject2 = getLocalizedData_NoSpecialChar();

		page.zCalFolderApp.zCreateNewCalendar(calName, "");

		page.zCalendarApp.zCreateSimpleAppt(subject1, "", "", "");

		page.zCalendarApp.zCreateApptInCalendar(subject2, "", "", "", calName);

		SleepUtil.sleepSmall();

		obj.zButton.zClick(page.zCalendarApp.calScheduleViewBtn);
		SleepUtil.sleepLong(); //timing issue
		obj.zButton.zClick(page.zCalendarApp.calTodayBtn);
		SleepUtil.sleepSmall();
		
		if (calCheck.equals("uncheck")) {
			obj.zCalendarFolder.zClick(calName);
			SleepUtil.sleepMedium();

			obj.zAppointment.zExists(subject1);
			obj.zAppointment.zNotExists(subject2);

		} else {
			obj.zAppointment.zExists(subject1);
			obj.zAppointment.zExists(subject2);
		}
		SelNGBase.needReset.set(false);
	}

	/**
	 * Verifies the calendar preference of initial view
	 * 
	 * @param initialView
	 * @throws Exception
	 */
	@Test(dataProvider = "CreateApptsDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalPrefInitialView(String initialView) throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String browserTitle1, browserTitle2;

		page.zCalendarApp.zSetCalPrefInitialView(initialView);

		page.zCalendarApp.zNavigateToCalendar();

		SleepUtil.sleepSmall();

		browserTitle1 = ClientSessionFactory.session().selenium().getTitle();

		if (initialView.equals(localize(locator.calViewDay)))
			obj.zButton.zClick(page.zCalendarApp.calDayViewBtn);
		else if (initialView.equals(localize(locator.calViewWorkWeek)))
			obj.zButton.zClick(page.zCalendarApp.calWorkWeekViewBtn);
		else if (initialView.equals(localize(locator.calViewWeek)))
			obj.zButton.zClick(page.zCalendarApp.calWeekViewBtn);
		else if (initialView.equals(localize(locator.calViewMonth)))
			obj.zButton.zClick(page.zCalendarApp.calMonthViewBtn);

		SleepUtil.sleepMedium();

		browserTitle2 = ClientSessionFactory.session().selenium().getTitle();

		Assert.assertTrue(browserTitle1.equals(browserTitle2),
				"Initial view calendar preference doesn't work");

		SelNGBase.needReset.set(false);
	}

	/**
	 * Verifies manager calendars link goes to calendar edit page
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalPrefManageCalendars() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String calName = getLocalizedData_NoSpecialChar();

		page.zCalFolderApp.zCreateNewCalendar(calName, "");
		page.zCalendarApp.zCalPrefManageCalendars();

		obj.zCalendarFolder.zExists(calName);

		obj.zButton.zExists(page.zCalFolderApp.calSaveChangesBtn);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Verifies show timeZone in appointment compose calendar preference
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCalPrefShowTimezone() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalendarApp.zSetCalPrefShowTimezone();

		page.zCalendarApp.zNavigateToCalendar();

		page.zCalendarApp.zNavigateToCalendarCompose();

		obj.zHtmlMenu.zExists(page.zCalendarApp.apptComposeTimezoneDropdown);

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptSubjectNegativeTest() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalendarApp.zCreateSimpleAppt("", "", "", "");

		obj.zToastAlertMessage
				.zAlertMsgExists(localize(locator.errorMissingSubject),
						"Correct toast message is not thrown on entering blank subject");

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptAttendeeNegativeTest() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalendarApp.zCreateSimpleAppt(getLocalizedData_NoSpecialChar(),
				"", "invalid-address", "");

		SleepUtil.sleepSmall();

		String ToastMsg = obj.zToastAlertMessage.zGetMsg();

		SleepUtil.sleepSmall();

		// there is no localize key for 'invalid attendees' string so running
		// only for english as of now
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US")) {
			Assert
					.assertTrue(ToastMsg.trim().contains("Invalid attendees"),
							"Correct toast message is not thrown on entering invalid attendees");
		}

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptDateNegativeTest() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalendarApp.zCreateAppt("test", "", "", "", "", "", "", "",
				"10/09/2008", "10/08/2008", "", "");

		obj.zToastAlertMessage
				.zAlertMsgExists(
						localize(locator.errorInvalidApptEndBeforeStart),
						"Correct toast message is not thrown on entering invalid dates");

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptTimeNegativeTest() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalendarApp.zCreateAppt("test", "", "", "", "", "", "", "",
				"10/09/2008", "10/09/2008", "11:00:AM", "10:00:AM");

		obj.zToastAlertMessage
				.zAlertMsgExists(
						localize(locator.errorInvalidApptEndBeforeStart),
						"Correct toast message is not thrown on entering invalid dates");

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "parallel", "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zApptAddAttendeesTest() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zCalendarApp.zNavigateToCalendar();
		page.zCalendarApp.zNavigateToCalendarCompose();

		obj.zButton.zClick(page.zCalendarApp.apptComposeAddAttendeesBtn);

		obj.zEditField.zType(
				page.zCalendarApp.apptComposeAddAttendeesFindField, "ccuser");
		obj.zHtmlMenu.zClick(
				page.zCalendarApp.apptComposeAddAttendeesFindDropdown,
				localize(locator.GAL));
		obj.zButton.zClick(page.zCalendarApp.apptComposeAddAttendeesSearchBtn);

		obj.zCheckbox.zClick("name=addAttendees", "1");
		obj.zCheckbox.zClick("name=addAttendees", "2");
		obj.zButton.zClick(page.zCalendarApp.apptComposeAddAttendeesDoneBtn);

		String addAttendees = obj.zTextAreaField
				.zGetInnerText(page.zCalendarApp.apptComposeAttendeesField);

		Assert.assertTrue(addAttendees.contains("ccuser"),
				"Add attendees doesn't add attendees properly");
		Assert.assertTrue(addAttendees.contains("bccuser"),
				"Add attendees doesn't add attendees properly");

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zImportICSTest() throws Exception {

		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String calName = getLocalizedData_NoSpecialChar();

		String ICSFile = "CalendarToImport.ics";

		page.zCalFolderApp.zCreateNewCalendar(calName, "");

		page.zCalFolderApp.zImportFromICSToCalendar(calName, ICSFile);

		page.zCalendarApp.zNavigateToViewAndDate("day:20090101");
		obj.zAppointment.zExists("jan appt - imported from ICS");

		page.zCalendarApp.zNavigateToViewAndDate("day:20090601");
		obj.zAppointment.zExists("june appt - imported from ICS");

		page.zCalendarApp.zNavigateToViewAndDate("day:20091201");
		obj.zAppointment.zExists("dec appt - imported from ICS");

		SelNGBase.needReset.set(false);
	}

	//--------------------------------------------------------------------------
	// Private functions
	private static void waitForIE() throws Exception {

		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE"))
			SleepUtil.sleepMedium();
	}

	private static void waitForSF() throws Exception {

		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("SF"))
			SleepUtil.sleepSmall();
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}

}
