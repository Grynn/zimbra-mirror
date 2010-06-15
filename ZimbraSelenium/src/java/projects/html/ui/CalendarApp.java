package projects.html.ui;

import java.io.File;

import org.testng.Assert;

import framework.core.SelNGBase;

import projects.html.tests.CommonTest;

/**
 *This class has UI level static id's and methods for html contact related
 * tests Prashant Jaiswal
 */
/**
 * @author KK Sure
 * 
 */
@SuppressWarnings( { "static-access", "unused" })
public class CalendarApp extends CommonTest {

	public static final String calendarTab = "id=TAB_CALENDAR";

	// IDs in main calendar view
	public static final String calRefresh = "id=CAL_REFRESH";
	public static final String calNewApptBtn = "id=CAL_NEWAPPT";
	public static final String calDayViewBtn = "id=CAL_DAY";
	public static final String calWorkWeekViewBtn = "id=CAL_WORK";
	public static final String calWeekViewBtn = "id=CAL_WEEK";
	public static final String calMonthViewBtn = "id=CAL_MONTH";
	public static final String calScheduleViewBtn = "id=CAL_SCHED";
	public static final String calTodayBtn = "id=CAL_TODAY";
	public static final String calPrevPageBtn = "id=PREV_PAGE";
	public static final String calNextPageBtn = "id=NEXT_PAGE";

	// IDs in Appt compose view
	public static final String apptComposeSaveBtn = "id=SOPSAVE";
	public static final String apptComposeCancelBtn = "name=actionCancel";
	public static final String apptComposeRepeatBtn = "name=actionRepeatEdit";
	public static final String apptCancelButton = "name=actionApptCancel";
	public static final String apptComposeAddAttendeesBtn = "name=actionContactAdd";
	public static final String apptComposeSubjectField = "id=subject";
	public static final String apptComposeLocationField = "id=location";
	public static final String apptComposeAttendeesField = "id=attField";
	public static final String apptComposeBodyArea = "name=body";
	public static final String apptComposeShowAsDropdown = "id=showAs";
	public static final String apptComposeMarkAsDropdown = "id=markAs";
	public static final String apptComposeCalendarDropdown = "id=apptFolderId";
	public static final String apptComposeTimezoneDropdown = "id=timeZone";

	public static final String apptComposeAllDayCheckbox = "id=allday";
	public static final String apptComposeStartDateField = "id=start";
	public static final String apptComposeEndDateField = "id=end";
	public static final String apptComposeStartHourDropDown = "name=startHour";
	public static final String apptComposeStartMinuteDropDown = "name=startMinute";
	public static final String apptComposeEndHourDropDown = "name=endHour";
	public static final String apptComposeEndMinuteDropDown = "name=endMinute";

	public static final String apptComposeAddAttendeesFindField = "id=findField";
	public static final String apptComposeAddAttendeesFindDropdown = "name=contactLocation";
	public static final String apptComposeAddAttendeesSearchBtn = "id=doSearch";
	public static final String apptComposeAddAttendeesDoneBtn = "name=actionContactDone";

	// IDs in Repeat (Recurrence page)

	public static final String apptRepeatRadioBtn = "name=repeatType";
	public static final String apptRepeatEndTypeRadioBtn = "name=repeatEndType";

	public static final String apptRepeatBasicDropdown = "name=repeatBasicType";

	public static final String apptRepeatEveryDayIntervalField = "name=repeatDailyInterval";

	public static final String apptRepeatWeeklyDayDropdown = "name=repeatWeeklyByDay";
	public static final String apptRepeatWeeklyIntervalField = "name=repeatWeeklyInterval";
	public static final String apptRepeatSundayCheckbox = "name=repeatWeeklySun";
	public static final String apptRepeatMondayCheckbox = "name=repeatWeeklyMon";
	public static final String apptRepeatTuesdayCheckbox = "name=repeatWeeklyTue";
	public static final String apptRepeatWednesdayCheckbox = "name=repeatWeeklyWed";
	public static final String apptRepeatThursdayCheckbox = "name=repeatWeeklyThu";
	public static final String apptRepeatFridayCheckbox = "name=repeatWeeklyFri";
	public static final String apptRepeatSaturdayCheckbox = "name=repeatWeeklySat";

	public static final String apptRepeatMonthlyDayEditField = "name=repeatMonthlyMonthDay";
	public static final String apptRepeatMonthlyOfEveryMonthEditField = "name=repeatMonthlyInterval";

	public static final String apptRepeatMonthlyNthDayDropdown = "name=repeatMonthlyRelativeOrd";
	public static final String apptRepeatMonthlyDayDropdown = "name=repeatMonthlyRelativeDay";
	public static final String apptRepeatMonthlyRelativeMonthEditField = "name=repeatMonthlyRelativeInterval";

	public static final String apptRepeatYearlyMonthDropdown = "name=repeatYearlyMonth";
	public static final String apptRepeatYearlyDateEditField = "name=repeatYearlyMonthDay";

	public static final String apptRepeatYearlyNthDayDropdown = "name=repeatYearlyRelativeOrd";
	public static final String apptRepeatYearlyDayDropdown = "name=repeatYearlyRelativeDay";
	public static final String apptRepeatYearlyRelativeMonthDropdown = "name=repeatYearlyRelativeMonth";

	public static final String apptRepeatEndAfterOccurrencesEditField = "name=repeatEndCount";

	public static final String apptRepeatEndByEditField = "name=repeatEndDate";

	public static final String apptRepeatDoneBtn = "name=actionRepeatDone";
	public static final String apptRepeatCancelBtn = "name=actionRepeatCancel";

	// ID's in calendar preference page

	public static final String preferencesTab = "id=tab_ikon_options";
	public static final String calPrefInitialViewDropdown = "id=initView";
	public static final String calPrefFirstDayDropdown = "id=fdow";
	public static final String calPrefDayStartsDropdown = "id=dayStart";
	public static final String calPrefDayEndsDropdown = "id=dayEnd";
	public static final String calPrefShowTimezoneCheckbox = "id=zimbraPrefUseTimeZoneListInCalendar";

	/**
	 * Navigate to calendar app
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToCalendar() throws Exception {
		obj.zButton.zClick(calendarTab);
		Thread.sleep(500);
	}

	/**
	 * Navigates to calendar compose page
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToCalendarCompose() throws Exception {
		obj.zButton.zClick(calNewApptBtn);
	}

	/**
	 * Enters simple appointment details
	 * 
	 * @param subject
	 *            : Subject of the appointment
	 * @param location
	 *            : Location of the appointment
	 * @param attendees
	 *            : Attendees of the appointment
	 * @param body
	 *            : Message body
	 * @throws Exception
	 */
	public static void zEnterSimpleApptDetails(String subject, String location,
			String attendees, String body) throws Exception {

		obj.zEditField.zType(apptComposeSubjectField, subject);

		if (!location.equals(""))
			obj.zEditField.zType(apptComposeLocationField, location);

		if (!attendees.equals(""))
			obj.zTextAreaField.zType(apptComposeAttendeesField, attendees);

		if (!body.equals(""))
			obj.zTextAreaField.zType(apptComposeBodyArea, body);
	}

	/**
	 * Enters appointment details in compose page
	 * 
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @param calendar
	 * @param showAs
	 * @param markAs
	 * @param allDayEvent
	 * @param startDate
	 * @param endDate
	 * @param startHour
	 * @param endHour
	 * @param startMinute
	 * @param endMinute
	 * @throws Exception
	 */
	public static void zEnterApptDetails(String subject, String location,
			String attendees, String body, String calendar, String showAs,
			String markAs, String allDayEvent, String startDate,
			String endDate, String startTime, String endTime) throws Exception {

		String startHour, startMinute, endHour, endMinute;
		String[] start;
		String[] end;

		zEnterSimpleApptDetails(subject, location, attendees, body);

		if (!calendar.equals(""))
			obj.zHtmlMenu.zClick(apptComposeCalendarDropdown, calendar);

		if (!showAs.equals(""))
			obj.zHtmlMenu.zClick(apptComposeShowAsDropdown, showAs);

		if (!markAs.equals(""))
			obj.zHtmlMenu.zClick(apptComposeMarkAsDropdown, markAs);

		if (!allDayEvent.equals(""))
			obj.zCheckbox.zClick(apptComposeAllDayCheckbox);

		if (!startDate.equals(""))
			zSetStartDate(startDate);
		else {
			String startDateTemp = obj.zEditField
					.zGetInnerText(apptComposeStartDateField);
			obj.zEditField.zType(apptComposeEndDateField, startDateTemp);
		}

		if (!endDate.equals(""))
			zSetEndDate(endDate);

		if (!startTime.equals(""))
			zSetStartTime(startTime);

		if (!endTime.equals(""))
			zSetEndTime(endTime);

	}

	/**
	 * Verifies the details of appointment after opening it
	 * 
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @throws Exception
	 */
	public static void zVerifySimpleApptDetails(String subject,
			String location, String attendees, String body) throws Exception {

		zOpenAppt(subject);

		Assert.assertTrue(obj.zEditField.zGetInnerText(apptComposeSubjectField)
				.equals(subject), "Subject field is incorrect");

		Assert.assertTrue(obj.zEditField
				.zGetInnerText(apptComposeLocationField).contains(location),
				"Location field is incorrect");

		Assert.assertTrue(obj.zTextAreaField.zGetInnerText(
				apptComposeAttendeesField).contains(attendees),
				"Attendees field is incorrect");

		Assert.assertTrue(obj.zTextAreaField.zGetInnerText(apptComposeBodyArea)
				.contains(body), "Appointment body is incorrect");
	}

	/**
	 * Creates a simple appointment
	 * 
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @throws Exception
	 */
	public static void zCreateSimpleAppt(String subject, String location,
			String attendees, String body) throws Exception {

		zNavigateToCalendar();

		zNavigateToCalendarCompose();

		zEnterSimpleApptDetails(subject, location, attendees, body);

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);

	}

	/**
	 * Creates an appointment with the given details
	 * 
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @param calendar
	 * @param showAs
	 * @param markAs
	 * @param allDayEvent
	 * @param startDate
	 * @param endDate
	 * @param startTime
	 * @param endTime
	 * @throws Exception
	 */
	public static void zCreateAppt(String subject, String location,
			String attendees, String body, String calendar, String showAs,
			String markAs, String allDayEvent, String startDate,
			String endDate, String startTime, String endTime) throws Exception {

		zNavigateToCalendar();

		zNavigateToCalendarCompose();

		zEnterApptDetails(subject, location, attendees, body, calendar, showAs,
				markAs, allDayEvent, startDate, endDate, startTime, endTime);

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);

	}

	/**
	 * Creates appointment in the specified calendar
	 * 
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @param calendar
	 * @throws Exception
	 */
	public static void zCreateApptInCalendar(String subject, String location,
			String attendees, String body, String calendar) throws Exception {

		zNavigateToCalendar();

		zNavigateToCalendarCompose();

		zEnterApptDetails(subject, location, attendees, body, calendar, "", "",
				"", "", "", "", "");

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);

	}

	public static void zCreateAllDayAppt(String subject, String location,
			String attendees, String body, String startDate, String endDate)
			throws Exception {

		zNavigateToCalendar();

		zNavigateToCalendarCompose();

		zEnterApptDetails(subject, location, attendees, body, "", "", "", "1",
				startDate, endDate, "", "");

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);
	}

	/**
	 * Opens a appointment by clicking
	 * 
	 * @param subject
	 * @throws Exception
	 */
	public static void zOpenAppt(String subject) throws Exception {

		obj.zAppointment.zClick(subject);
	}

	/**
	 * Edits simple appointment
	 * 
	 * @param oldSubject
	 * @param newSubject
	 * @param location
	 * @param attendees
	 * @param body
	 * @throws Exception
	 */
	public static void zEditSimpleAppt(String oldSubject, String subject,
			String location, String attendees, String body) throws Exception {

		zOpenAppt(oldSubject);

		zEnterSimpleApptDetails(subject, location, attendees, body);

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);

	}

	/**
	 * Edits and saves an appointment
	 * 
	 * @param oldSubject
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @param calendar
	 * @param showAs
	 * @param markAs
	 * @param allDayEvent
	 * @param startDate
	 * @param endDate
	 * @param startTime
	 * @param endTime
	 * @throws Exception
	 */
	public static void zEditAppointment(String oldSubject, String subject,
			String location, String attendees, String body, String calendar,
			String showAs, String markAs, String allDayEvent, String startDate,
			String endDate, String startTime, String endTime) throws Exception {

		zOpenAppt(oldSubject);

		zEnterApptDetails(subject, location, attendees, body, calendar, showAs,
				markAs, allDayEvent, startDate, endDate, startTime, endTime);

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);

	}

	//--------------------------------------------------------------------------
	// FUNCTIONS FOR CREATING DIFFERENT KINDS OF REPEAT

	/**
	 * Creates a appointment with the give repeat time
	 * 
	 * @param subject
	 * @param location
	 * @param attendees
	 * @param body
	 * @param repeatType
	 * @throws Exception
	 */
	public static void zCreateRepeatApptBasicNoEndDate(String subject,
			String location, String attendees, String body,
			String apptStartDate, String apptEndDate, String startTime,
			String endTime, String repeatType) throws Exception {

		zNavigateToCalendar();

		zNavigateToCalendarCompose();

		zEnterApptDetails(subject, location, attendees, body, "", "", "", "",
				apptStartDate, apptEndDate, startTime, endTime);

		zRepeatBasicNoEndDate(repeatType);

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);
	}

	public static void zCreateApptRepeatEveryDay(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate, String noEndDate,
			String endAfterNOccur, String endByDate) throws Exception {

		zNavigateToCalendar();

		zNavigateToCalendarCompose();

		zEnterApptDetails(subject, location, attendees, body, "", "", "", "",
				startDate, endDate, startTime, endTime);
		
		zRepeatSetEveryDay();

		zRepeatSetEndDate(noEndDate, endAfterNOccur, endByDate);

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);
	}

	public static void zCreateApptRepeatEveryWeekDay(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate, String noEndDate,
			String endAfterNOccur, String endByDate) throws Exception {

		zNavigateToCalendar();

		zNavigateToCalendarCompose();

		zEnterApptDetails(subject, location, attendees, body, "", "", "", "",
				startDate, endDate, startTime, endTime);

		zRepeatSetEveryWeekDay();

		zRepeatSetEndDate(noEndDate, endAfterNOccur, endByDate);

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);
	}

	public static void zCreateApptRepeatEveryDaysInterval(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate,
			String daysInterval, String noEndDate, String endAfterNOccur,
			String endByDate) throws Exception {

		zNavigateToCalendar();

		zNavigateToCalendarCompose();

		zEnterApptDetails(subject, location, attendees, body, "", "", "", "",
				startDate, endDate, startTime, endTime);

		zRepeatSetEveryDaysInterval(daysInterval);

		zRepeatSetEndDate(noEndDate, endAfterNOccur, endByDate);

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);

	}

	public static void zCreateApptRepeatDayOfWeek(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate, String day,
			String noEndDate, String endAfterNOccur, String endByDate)
			throws Exception {

		zNavigateToCalendar();

		zNavigateToCalendarCompose();

		zEnterApptDetails(subject, location, attendees, body, "", "", "", "",
				startDate, endDate, startTime, endTime);

		zRepeatSetDayOfWeek(day);

		zRepeatSetEndDate(noEndDate, endAfterNOccur, endByDate);

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);

	}

	public static void zCreateApptRepeatWeeklyInterval(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate,
			String weekInterval, String repeatDayInEnglishShort,
			String noEndDate, String endAfterNOccur, String endByDate)
			throws Exception {

		zNavigateToCalendar();

		zNavigateToCalendarCompose();

		zEnterApptDetails(subject, location, attendees, body, "", "", "", "",
				startDate, endDate, startTime, endTime);

		zRepeatSetWeeklyInterval(weekInterval, repeatDayInEnglishShort);

		zRepeatSetEndDate(noEndDate, endAfterNOccur, endByDate);

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);

	}

	public static void zCreateApptRepeatDayOfMonthInterval(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate, String dayNumber,
			String monthInterval, String noEndDate, String endAfterNOccur,
			String endByDate) throws Exception {

		zNavigateToCalendar();

		zNavigateToCalendarCompose();

		zEnterApptDetails(subject, location, attendees, body, "", "", "", "",
				startDate, endDate, startTime, endTime);

		zRepeatSetDayOfMonthInterval(dayNumber, monthInterval);

		zRepeatSetEndDate(noEndDate, endAfterNOccur, endByDate);

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);

	}

	public static void zCreateApptRepeatEveryNthDayOfMonthInterval(
			String subject, String location, String attendees, String body,
			String startTime, String endTime, String startDate, String endDate,
			String NthValue, String day, String monthInterval,
			String noEndDate, String endAfterNOccur, String endByDate)
			throws Exception {

		zNavigateToCalendar();

		zNavigateToCalendarCompose();

		zEnterApptDetails(subject, location, attendees, body, "", "", "", "",
				startDate, endDate, startTime, endTime);

		zRepeatSetEveryNthDayOfMonthInterval(NthValue, day, monthInterval);

		zRepeatSetEndDate(noEndDate, endAfterNOccur, endByDate);

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);

	}

	public static void zCreateApptRepeatYearlyDay(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate,
			String repeatMonth, String dayValue, String noEndDate,
			String endAfterNOccur, String endByDate) throws Exception {

		zNavigateToCalendar();

		zNavigateToCalendarCompose();

		zEnterApptDetails(subject, location, attendees, body, "", "", "", "",
				startDate, endDate, startTime, endTime);

		zRepeatSetYearlyDay(repeatMonth, dayValue);

		zRepeatSetEndDate(noEndDate, endAfterNOccur, endByDate);

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);

	}

	public static void zCreateApptRepeatYearlyRelative(String subject,
			String location, String attendees, String body, String startTime,
			String endTime, String startDate, String endDate,
			String nthDayOfMonth, String day, String repeatMonth,
			String noEndDate, String endAfterNOccur, String endByDate)
			throws Exception {

		zNavigateToCalendar();

		zNavigateToCalendarCompose();

		zEnterApptDetails(subject, location, attendees, body, "", "", "", "",
				startDate, endDate, startTime, endTime);

		zRepeatSetYearlyRelative(nthDayOfMonth, day, repeatMonth);

		zRepeatSetEndDate(noEndDate, endAfterNOccur, endByDate);

		obj.zButton.zClick(apptComposeSaveBtn);
		Thread.sleep(3000);

	}

	//--------------------------------------------------------------------------

	// PRIVATE FUNCTIONS FOR REPEAT TYPE APPOINTMENTS
	//--------------------------------------------------------------------------
	// Functions to set different kind of repeats

	/**
	 * Sets basic repeat of the given type, with no end date
	 * 
	 * @param recurringType
	 * @throws Exception
	 */
	private static void zRepeatBasicNoEndDate(String recurringType)
			throws Exception {

		Thread.sleep(3000);

		obj.zButton.zClick(apptComposeRepeatBtn);
		Thread.sleep(6000);
		obj.zRadioBtn.zClick(apptRepeatRadioBtn, "2");

		obj.zHtmlMenu.zClick(apptRepeatBasicDropdown, recurringType);

		obj.zButton.zClick(apptRepeatDoneBtn);

		Thread.sleep(6000);
	}

	private static void zRepeatSetEveryDay() throws Exception {
		// thread required
		Thread.sleep(2000);
		obj.zButton.zClick(apptComposeRepeatBtn);
		Thread.sleep(6000);
		obj.zRadioBtn.zClick(apptRepeatRadioBtn, "3");
	}

	private static void zRepeatSetEveryWeekDay() throws Exception {

		waitForSF();

		obj.zButton.zClick(apptComposeRepeatBtn);
		Thread.sleep(6000);
		obj.zRadioBtn.zClick(apptRepeatRadioBtn, "4");
	}

	private static void zRepeatSetEveryDaysInterval(String daysInterval)
			throws Exception {

		waitForSF();

		obj.zButton.zClick(apptComposeRepeatBtn);
		Thread.sleep(6000);
		obj.zRadioBtn.zClick(apptRepeatRadioBtn, "5");

		obj.zEditField.zType(apptRepeatEveryDayIntervalField, daysInterval);
	}

	public static void zRepeatSetDayOfWeek(String day) throws Exception {

		waitForSF();

		obj.zButton.zClick(apptComposeRepeatBtn);
		Thread.sleep(6000);
		obj.zRadioBtn.zClick(apptRepeatRadioBtn, "6");

		obj.zHtmlMenu.zClick(apptRepeatWeeklyDayDropdown, day);
	}

	private static void zRepeatSetWeeklyInterval(String weekInterval,
			String repeatDayInEnglishShort) throws Exception {

		waitForSF();

		String checkboxToClick = "repeatWeekly" + repeatDayInEnglishShort;

		obj.zButton.zClick(apptComposeRepeatBtn);
		Thread.sleep(6000);
		obj.zRadioBtn.zClick(apptRepeatRadioBtn, "7");

		obj.zEditField.zType(apptRepeatWeeklyIntervalField, weekInterval);

		obj.zCheckbox.zClick("name=" + checkboxToClick);

	}

	private static void zRepeatSetDayOfMonthInterval(String dayNumber,
			String monthInterval) throws Exception {

		waitForSF();

		obj.zButton.zClick(apptComposeRepeatBtn);
		Thread.sleep(6000);
		obj.zRadioBtn.zClick(apptRepeatRadioBtn, "8");

		obj.zEditField.zType(apptRepeatMonthlyDayEditField, dayNumber);

		obj.zEditField.zType(apptRepeatMonthlyOfEveryMonthEditField,
				monthInterval);

	}

	private static void zRepeatSetEveryNthDayOfMonthInterval(String NthValue,
			String day, String monthInterval) throws Exception {

		waitForSF();

		obj.zButton.zClick(apptComposeRepeatBtn);
		Thread.sleep(6000);
		obj.zRadioBtn.zClick(apptRepeatRadioBtn, "9");

		obj.zHtmlMenu.zClick(apptRepeatMonthlyNthDayDropdown, NthValue);
		obj.zHtmlMenu.zClick(apptRepeatMonthlyDayDropdown, day);
		obj.zEditField.zClick(apptRepeatMonthlyRelativeMonthEditField,
				monthInterval);

	}

	private static void zRepeatSetYearlyDay(String repeatMonth, String dayValue)
			throws Exception {

		waitForSF();

		obj.zButton.zClick(apptComposeRepeatBtn);
		Thread.sleep(6000);
		obj.zRadioBtn.zClick(apptRepeatRadioBtn, "10");

		obj.zHtmlMenu.zClick(apptRepeatYearlyMonthDropdown, repeatMonth);
		obj.zEditField.zType(apptRepeatYearlyDateEditField, dayValue);

	}

	private static void zRepeatSetYearlyRelative(String nthDayOfMonth,
			String day, String repeatMonth) throws Exception {

		waitForSF();

		obj.zButton.zClick(apptComposeRepeatBtn);
		Thread.sleep(6000);
		obj.zRadioBtn.zClick(apptRepeatRadioBtn, "11");

		obj.zHtmlMenu.zClick(apptRepeatYearlyNthDayDropdown, nthDayOfMonth);

		obj.zHtmlMenu.zClick(apptRepeatYearlyDayDropdown, day);

		obj.zHtmlMenu
				.zClick(apptRepeatYearlyRelativeMonthDropdown, repeatMonth);
	}

	public static void zRepeatSetEndDate(String noEndDate,
			String endAfterNOccur, String endByDate) throws Exception {
		Thread.sleep(6000);
		
		if (!noEndDate.equals("")) {
			obj.zRadioBtn.zClick(apptRepeatEndTypeRadioBtn, "1");
		} else if (!endAfterNOccur.equals("")) {
			obj.zRadioBtn.zClick(apptRepeatEndTypeRadioBtn, "2");
			obj.zEditField.zType(apptRepeatEndAfterOccurrencesEditField,
					endAfterNOccur);
		} else if (!endByDate.equals("")) {
			obj.zRadioBtn.zClick(apptRepeatEndTypeRadioBtn, "3");
			obj.zEditField.zType(apptRepeatEndByEditField,
					zDateFormat(endByDate));
		}

		obj.zButton.zClick(apptRepeatDoneBtn);
		Thread.sleep(6000);
	}

	//--------------------------------------------------------------------------

	public static void zNavigateToViewAndDate(String viewAndDateSeparatedByColon)
			throws Exception {

		String[] temp = viewAndDateSeparatedByColon.split(":");
		Thread.sleep(3000);
		String url = "http://" + config.getString("server")
				+ "/zimbra/h/calendar?view=" + temp[0] + "&date=" + temp[1];

		selenium.open(url);

		Thread.sleep(5000);
	}

	public static void zVerifyInviteContent(String firstLineSummary,
			String itemsToVerify[]) throws Exception {

		Thread.sleep(500);
		String msgBody;

		int numberOfItemsToVerify;

		msgBody = obj.zMessageItem.zGetCurrentMsgBodyText();

		if (!firstLineSummary.equals(""))
			Assert
					.assertTrue(
							msgBody.indexOf(firstLineSummary) >= 0,
							"The first line summary of the appointment invite is incorrect. Invite body is: "
									+ msgBody
									+ " Expected first line of summary is: "
									+ firstLineSummary);

		numberOfItemsToVerify = itemsToVerify.length;

		for (int i = 0; i <= (numberOfItemsToVerify - 1); i++) {

			Assert
					.assertTrue(
							msgBody.indexOf(itemsToVerify[i]) >= 0,
							itemsToVerify[i]
									+ " is not present in the appointment invite. Invite body is "
									+ msgBody);
		}

	}

	// CALENDAR PREFERENCES RELATED FUNCTIONS
	//--------------------------------------------------------------------------

	/**
	 * Navigates to calendar preferences
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToCalendarPreferences() throws Exception {
		obj.zButton.zClick(preferencesTab);
		Thread.sleep(1000);
		obj.zTab.zClick(localize(locator.calendar), "2");
		Thread.sleep(500);
	}

	/**
	 * Sets the calendar initial view
	 * 
	 * @param initialView
	 * @throws Exception
	 */
	public static void zSetCalPrefInitialView(String initialView)
			throws Exception {
		zNavigateToCalendarPreferences();
		Thread.sleep(3000);
		obj.zHtmlMenu.zClick(calPrefInitialViewDropdown, initialView);
		obj.zButton.zClick(page.zABComposeHTML.zPrefSaveButton);
		Thread.sleep(1000);
	}

	/**
	 * Sets the first day of week
	 * 
	 * @param firstDay
	 * @throws Exception
	 */
	public static void zSetCalPrefFirstDay(String firstDay) throws Exception {
		zNavigateToCalendarPreferences();

		obj.zHtmlMenu.zClick(calPrefFirstDayDropdown, firstDay);

		obj.zButton.zClick(page.zABComposeHTML.zPrefSaveButton);

	}

	/**
	 * Clicks on the manage calendars link
	 * 
	 * @throws Exception
	 */
	public static void zCalPrefManageCalendars() throws Exception {
		zNavigateToCalendarPreferences();

		Thread.sleep(500);

		if (selenium.isElementPresent("link="
				+ localize(locator.optionsManageCalendarsLink)))
			selenium.click("link="
					+ localize(locator.optionsManageCalendarsLink));

	}

	/**
	 * 
	 * @throws Exception
	 */
	public static void zSetCalPrefShowTimezone() throws Exception {

		zNavigateToCalendarPreferences();

		obj.zCheckbox.zClick(calPrefShowTimezoneCheckbox);

		obj.zButton.zClick(page.zABComposeHTML.zPrefSaveButton);
	}

	/**
	 * Sets the start time and end time of the calendar view
	 * 
	 * @param startTime
	 * @param endTime
	 * @throws Exception
	 */
	public static void zSetCalPrefSetStartEndTime(String startTime,
			String endTime) throws Exception {

		zNavigateToCalendarPreferences();

		obj.zHtmlMenu.zClick(calPrefDayStartsDropdown, startTime);
		obj.zHtmlMenu.zClick(calPrefDayEndsDropdown, endTime);

		obj.zButton.zClick(page.zABComposeHTML.zPrefSaveButton);
	}

	private static void waitForSF() throws Exception {
		if (config.getString("browser").equals("SF"))
			Thread.sleep(2000);
	}

	// ----------------------------------------------------------------

	private static void zSetStartDate(String date) throws Exception {
		String dateToSet;
		dateToSet = zDateFormat(date);
		obj.zEditField.zType(apptComposeStartDateField, dateToSet);
	}

	private static void zSetEndDate(String date) throws Exception {
		String dateToSet;
		dateToSet = zDateFormat(date);
		obj.zEditField.zType(apptComposeEndDateField, dateToSet);
	}

	private static String zDateFormat(String date) throws Exception {

		String[] dateArr = date.split("/");

		String newDate = localize(locator.CAL_APPT_EDIT_DATE_FORMAT);

		String dateReturn;
		if(config.getProperty("locale").equals("zh_HK") || config.getProperty("locale").equals("ko")
				|| config.getProperty("locale").equals("zh_CN")) {
			dateReturn = newDate.replace("M", dateArr[0]).replace("d",
					dateArr[1]).replace("yyyy", dateArr[2]);
			
		} else {
			dateReturn = newDate.replace("MM", dateArr[0]).replace("dd",
				dateArr[1]).replace("yyyy", dateArr[2]);
		}

		return dateReturn;
	}

	private static void zSetStartTime(String startTime) throws Exception {

		String[] startTimeArr = startTime.split(":");
		String hourToSet = zHourFormat(startTime);
		String minuteToSet = ":" + startTimeArr[1];

		obj.zHtmlMenu.zClick(apptComposeStartHourDropDown, hourToSet);
		obj.zHtmlMenu.zClick(apptComposeStartMinuteDropDown, minuteToSet);

	}

	private static void zSetEndTime(String endTime) throws Exception {

		String[] endTimeArr = endTime.split(":");
		String hourToSet = zHourFormat(endTime);
		String minuteToSet = ":" + endTimeArr[1];

		obj.zHtmlMenu.zClick(apptComposeEndHourDropDown, hourToSet);
		obj.zHtmlMenu.zClick(apptComposeEndMinuteDropDown, minuteToSet);

	}

	public static String zHourFormat(String timeString) throws Exception {

		String[] hourArr = timeString.split(":");
		String hourFormat = localize(locator.CAL_APPT_EDIT_HOUR_FORMAT);

		if (hourFormat.equals("h"))
			return hourArr[0];
		else if (hourFormat.equals("H") && (hourArr[2].equals("AM"))) {
			return hourArr[0];
		} else if ((hourFormat.equals("H") || hourFormat.equals("HH"))
				&& (hourArr[2].equals("PM")) && !(hourArr[0].equals("12"))) {
			int hr = Integer.parseInt(hourArr[0]);
			hr = hr + 12;
			return Integer.toString(hr);
		} else if (hourFormat.equals("HH") && hourArr[2].equals("AM")) {
			if (hourArr[0].length() == 1)
				return "0" + hourArr[0];
			else
				return hourArr[0];
		} else if (hourFormat.equals("h a")) {
			if (hourArr[2].equals("AM"))
				return hourArr[0] + " " + localize(locator.periodAm);
			else
				return hourArr[0] + " " + localize(locator.periodPm);
		} else if (hourFormat.equals("a h")) {
			if (hourArr[2].equals("AM"))
				return localize(locator.periodAm) + " " + hourArr[0];
			else
				return localize(locator.periodPm) + " " + hourArr[0];
		} else {
			return hourArr[0];
		}
	}

	// Contains lots of workarounds for different locales.
	public static String zTimeShortFormat(String time) throws Exception {

		String[] timeArr = time.split(":");
		String timeFormat = localize(locator.formatTimeShort);

		if (config.getString("locale").equals("ja")
				|| config.getString("locale").equals("ru"))
			timeFormat = "HH:mm";

		if (config.getString("locale").equals("en_AU")
				|| config.getString("locale").equals("da")
				|| config.getString("locale").equals("en_GB")
				|| config.getString("locale").equals("ar"))
			timeFormat = "H:mm";

		if (timeFormat.equals("'kl 'H:mm"))
			timeFormat = "H:mm";

		if (timeFormat.contains("a")) {
			if (timeArr[2].equals("AM"))
				return timeFormat.replace("h", timeArr[0]).replace("mm",
						timeArr[1]).replace("a", localize(locator.periodAm));
			else
				return timeFormat.replace("h", timeArr[0]).replace("mm",
						timeArr[1]).replace("a", localize(locator.periodPm));
		} else if (timeFormat.equals("H:mm") || timeFormat.contains("kl")) {

			if (timeArr[2].equals("PM") && (!timeArr[0].equals("12"))) {
				int hr = Integer.parseInt(timeArr[0]);
				hr = hr + 12;
				if (config.getString("locale").equals("it")) {
					return timeFormat.replace("H", Integer.toString(hr))
							.replace("mm", timeArr[1]).replace(":", ".");
				} else {
					return timeFormat.replace("H", Integer.toString(hr))
							.replace("mm", timeArr[1]);
				}
			} else {

				if (config.getString("locale").equals("it")) {
					return timeFormat.replace("H", timeArr[0]).replace("mm",
							timeArr[1]).replace(":", ".");
				}
				if (config.getString("locale").equals("nl")) {
					return timeFormat.replace("H", timeArr[0]).replace("mm",
							timeArr[1]);
				} else if (timeArr[0].length() == 1) {
					return timeFormat.replace("H", "0" + timeArr[0]).replace(
							"mm", timeArr[1]);
				} else {
					return timeFormat.replace("H", timeArr[0]).replace("mm",
							timeArr[1]);
				}

			}
		} else if (timeFormat.equals("HH:mm")) {
			if (timeArr[2].equals("PM") && (!timeArr[0].equals("12"))) {
				int hr = Integer.parseInt(timeArr[0]);
				hr = hr + 12;
				return timeFormat.replace("HH", Integer.toString(hr)).replace(
						"mm", timeArr[1]);
			} else {
				// if (timeArr[0].length() == 1)
				// return timeFormat.replace("HH", "0" + timeArr[0]).replace(
				// "mm", timeArr[1]);
				// else
				return timeFormat.replace("HH", timeArr[0]).replace("mm",
						timeArr[1]);
			}
		} else {
			return "invalid format";
		}
	}
}
