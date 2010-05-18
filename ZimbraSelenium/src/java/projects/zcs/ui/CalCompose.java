package projects.zcs.ui;

//import java.io.File;

//import java.util.Map;

//import org.testng.Assert;

//import projects.zcs.Locators;

import junit.framework.Assert;
import projects.zcs.tests.CommonTest;

/**
 * This Class have UI-level methods related calendar compose. If you are dealing
 * with the toolbar buttons, use these icons since in vmware resolutions and in
 * some languages button-labels are not displayed(but just their icons)
 * 
 * @author Krishna Kumar Sure
 * 
 */
@SuppressWarnings("static-access")
public class CalCompose extends CommonTest {
	public static final String zApptSaveBtn = "id=zb__APPT__SAVE_left_icon";
	public static final String zApptCancelBtn = "id=zb__APPT__CANCEL_left_icon";
	public static final String zApptCloseBtn = "id=zb__APPT__CANCEL_left_icon";
	public static final String zApptAddAttachmentBtn = "id=zb__APPT__ATTACHMENT_left_icon";
	public static final String zApptSpellCheckBtn = "id=zb__APPT__SPELL_CHECK_left_icon";
	public static final String zApptFormatBtn = "id=zb__APPT__COMPOSE_FORMAT_left_icon";
	public static final String zApptDetailsTab = "id=ztab__APPT__details_left_icon";
	public static final String zApptScheduleTab = "id=ztab__APPT__schedule_left_icon";
	public static final String zApptFindAttendeesTab = "id=ztab__APPT__attendees_left_icon";
	public static final String zApptFindLocationsTab = "id=ztab__APPT__locations_left_icon";
	public static final String zApptFindResourcesTab = "id=ztab__APPT__equipment_left_icon";
	public static final String zDayViewIconBtn = "id=zb__CLD__DAY_VIEW_left_icon";
	public static final String zDayViewBtn = "id=zb__CLD__DAY_VIEW";
	public static final String zWorkWeekViewIconBtn = "id=zb__CLD__WORK_WEEK_VIEW_left_icon";
	public static final String zWorkWeekViewBtn = "id=zb__CLD__WORK_WEEK_VIEW";
	public static final String zWeekViewIconBtn = "id=zb__CLD__WEEK_VIEW_left_icon";
	public static final String zWeekViewBtn = "id=zb__CLD__WEEK_VIEW";
	public static final String zMonthViewIconBtn = "id=zb__CLD__MONTH_VIEW_left_icon";
	public static final String zMonthViewBtn = "id=zb__CLD__MONTH_VIEW";

	/**
	 * Enters simple calendar appointment details
	 * 
	 * @param subject
	 *            : subject for the appointment
	 * @param location
	 *            : location for the appointment
	 * @param attendees
	 *            : attendees for the appointment
	 * @param body
	 *            : body of the appointment
	 * @throws Exception
	 */
	public static void zCalendarEnterSimpleDetails(String subject,
			String location, String attendees, String body) throws Exception {
		if (!subject.equals(""))
			obj.zEditField.zType(
					getNameWithoutSpace(localize(locator.subjectLabel)),
					subject);
		if (!location.equals(""))
			obj.zEditField.zType(localize(locator.locationLabel), location);
		if (!attendees.equals(""))
			obj.zTextAreaField.zType(
					getNameWithoutSpace(localize(locator.attendeesLabel)),
					attendees);
		if (!body.equals(""))
			obj.zEditor.zType(body);
	}

	/**
	 * Enters all the appointment details
	 * 
	 * @param subject
	 *            : subject for the appointment
	 * @param location
	 *            : location for the appointment
	 * @param showAs
	 *            : 'Show As' menu value (Busy, Tentative etc.)
	 * @param markAs
	 *            : 'Mark As' menu value (Public, Private etc.)
	 * @param calendar
	 *            : calendar the appointment should be created in
	 * @param allDayEvent
	 *            : If the event is an all day event or not. Provide "" or 0 if
	 *            not.
	 * @param startDate
	 *            : start date of the appointment
	 * @param endDate
	 *            : end date of the appointment
	 * @param repeat
	 *            : Repeat value for the appointment (e.g. Every Week, Every
	 *            Year etc.)
	 * @param reminder
	 *            : Reminder value for the appointment (e.g. Never, 1 minute
	 *            before etc.)
	 * @param attendees
	 *            : attendees for the appointment
	 * @param body
	 *            : body of the appointment
	 * @throws Exception
	 */
	public static void zCalendarEnterDetails(String subject, String location,
			String showAs, String markAs, String calendar, String allDayEvent,
			String startDate, String endDate, String startTime, String endTime,
			String repeat, String reminder, String attendees, String body)
			throws Exception {
		zCalendarEnterSimpleDetails(subject, location, attendees, body);
		if (!showAs.equals("")) {
			obj.zButton.zClick(localize(locator.busy));
			obj.zMenuItem.zClick(showAs);
		}
		if (!markAs.equals("")) {
			obj.zButton.zClick(localize(locator._public));
			obj.zMenuItem.zClick(markAs);
		}
		if (!calendar.equals("")) {
			obj.zFeatureMenu.zClick(localize(locator.calendarLabel));
			obj.zMenuItem.zClick(calendar);
		}
		if ((allDayEvent.equals(1))
				|| ((!allDayEvent.equals(0)) && (!allDayEvent.equals("")))) {
			obj.zCheckbox.zClick(localize(locator.allDayEvent));
		}
		if (!startDate.equals(""))
			obj.zEditField.zType(localize(locator.start), startDate);
		if (!endDate.equals(""))
			obj.zEditField.zType(localize(locator.end), endDate);
		if (!startTime.equals(""))
			zSetStartTime(startTime);
		if (!endTime.equals(""))
			zSetEndTime(endTime);
		if (!repeat.equals("")) {
			obj.zButton.zClick(localize(locator.none));
			obj.zMenuItem.zClick(repeat);
		}
		if (!reminder.equals("")) {
			obj.zFeatureMenu.zClick(localize(locator.reminderLabel));
			obj.zMenuItem.zClick(reminder);
		}
	}

	/**
	 * Creates a simple appointment
	 * 
	 * @param subject
	 *            : subject of the appointment
	 * @param location
	 *            : location for the appointment
	 * @param attendees
	 *            : attendees for the appointment
	 * @param body
	 *            : body of the appointment
	 * @throws Exception
	 */
	public static void zCreateSimpleAppt(String subject, String location,
			String attendees, String body) throws Exception {
		page.zCalApp.zNavigateToApptCompose();
		zCalendarEnterSimpleDetails(subject, location, attendees, body);
		obj.zButton.zClick(zApptSaveBtn);
		zWaitTillObjectExist("button", page.zCalApp.zCalNewApptBtn);
	}

	public static void zCreateSimpleApptWithRSRPNotification(String subject,
			String location, String attendees, String body, String RSRP,
			String Notifcation) throws Exception {
		page.zCalApp.zNavigateToApptCompose();
		zCalendarEnterDetails(subject, location, "", "", "", "", "", "", "",
				"", "", "", attendees, body);
		if (RSRP.equals("0")) {
			obj.zCheckbox.zClick("id=*_requestResponses");
		}
		if (Notifcation.equals("0")) {
			obj.zCheckbox.zClick("id=*_sendNotificationMail");
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.warningMsg));
		}
		obj.zButton.zClick(zApptSaveBtn);
		zWaitTillObjectExist("button", page.zCalApp.zCalNewApptBtn);
	}

	/**
	 * Creates an appointment with full details
	 * 
	 * @param subject
	 *            : subject for the appointment
	 * @param location
	 *            : location for the appointment
	 * @param showAs
	 *            : 'Show As' menu value (Busy, Tentative etc.)
	 * @param markAs
	 *            : 'Mark As' menu value (Public, Private etc.)
	 * @param calendar
	 *            : calendar the appointment should be created in
	 * @param allDayEvent
	 *            : If the event is an all day event or not. Provide "" or 0 if
	 *            not.
	 * @param startDate
	 *            : start date of the appointment
	 * @param endDate
	 *            : end date of the appointment
	 * @param repeat
	 *            : Repeat value for the appointment (e.g. Every Week, Every
	 *            Year etc.)
	 * @param reminder
	 *            : Reminder value for the appointment (e.g. Never, 1 minute
	 *            before etc.)
	 * @param attendees
	 *            : attendees for the appointment
	 * @param body
	 *            : body of the appointment
	 * @throws Exception
	 */

	public static void zCreateAppt(String subject, String location,
			String showAs, String markAs, String calendar, String allDayEvent,
			String startDate, String endDate, String startTime, String endTime,
			String repeat, String reminder, String attendees, String body)
			throws Exception {
		page.zCalApp.zNavigateToApptCompose();
		zCalendarEnterDetails(subject, location, showAs, markAs, calendar,
				allDayEvent, startDate, endDate, startTime, endTime, repeat,
				reminder, attendees, body);
		obj.zButton.zClick(zApptSaveBtn);
		zWaitTillObjectExist("button", page.zCalApp.zCalNewApptBtn);
	}

	/**
	 * Creates a appointment in the given calendar
	 * 
	 * @param subject
	 *            : subject for the appointment
	 * @param location
	 *            : location for the appointment
	 * @param attendees
	 *            : attendees for the appointment
	 * @param body
	 *            : body of the appointment
	 * @param calendar
	 *            : calendar in which the appointment is to be created
	 * @throws Exception
	 */
	public static void zCreateSimpleApptInCalendar(String subject,
			String location, String attendees, String body, String calendar)
			throws Exception {
		page.zCalApp.zNavigateToApptCompose();
		zCalendarEnterDetails(subject, location, "", "", calendar, "", "", "",
				"", "", "", "", attendees, body);
		obj.zButton.zClick(zApptSaveBtn);
		zWaitTillObjectExist("button", page.zCalApp.zCalNewApptBtn);
	}

	/**
	 * Edits subject of an appointment
	 * 
	 * @param oldSubject
	 *            : original subject of the appointment
	 * @param newSubject
	 *            : new subject of the appointment
	 * @throws Exception
	 */
	public static void zEditAppointment(String oldSubject, String newSubject)
			throws Exception {
		obj.zAppointment.zDblClick(oldSubject);
		obj.zEditField
				.zActivate(getNameWithoutSpace(localize(locator.subjectLabel)));
		zCalendarEnterSimpleDetails(newSubject, "", "", "");
		obj.zButton.zClick(zApptSaveBtn);
	}

	/**
	 * Edits an appointment will all the details given
	 * 
	 * @param oldSubject
	 *            : subject of the appointment to be edited All below items can
	 *            be edited.
	 * @param newSubject
	 *            , location, showAs, markAs, calendar, allDayEvent, startDate,
	 *            endDate, startTime, endTime, repeat, reminder, attendees, body
	 ** @throws Exception
	 */
	public static void zEditAppointmentWithDetails(String oldSubject,
			String newSubject, String location, String showAs, String markAs,
			String calendar, String allDayEvent, String startDate,
			String endDate, String startTime, String endTime, String repeat,
			String reminder, String attendees, String body) throws Exception {
		obj.zAppointment.zDblClick(oldSubject);
		obj.zEditField
				.zActivate(getNameWithoutSpace(localize(locator.subjectLabel)));
		zCalendarEnterDetails(newSubject, location, showAs, markAs, calendar,
				allDayEvent, startDate, endDate, startTime, endTime, repeat,
				reminder, attendees, body);
		obj.zButton.zClick(zApptSaveBtn);
	}

	/**
	 * Adds attendee to the appointment opened
	 * 
	 * @param attendees
	 *            : attendees to be added to the appointment
	 * @throws Exception
	 */
	public static void zAddAttendeeAndSave(String attendees) throws Exception {
		String currentAttendees;
		String allAttendees;
		currentAttendees = obj.zTextAreaField
				.zGetInnerText(localize(locator.attendeesLabel));
		allAttendees = currentAttendees + ";" + attendees;
		obj.zTextAreaField
				.zType(localize(locator.attendeesLabel), allAttendees);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		obj.zRadioBtn.zClickInDlg(localize(locator.sendUpdatesAll));
		obj.zButton.zClickInDlg(localize(locator.ok));
	}

	/**
	 * Sets the start time for the calendar invite
	 * 
	 * @param hrColonMinColonAmPm
	 *            : Start time to be set
	 * @throws Exception
	 */
	public static void zSetStartTime(String hrColonMinColonAmPm)
			throws Exception {
		// zSetTimeCore(getNameWithoutSpace(localize(locator.startLabel)),
		// hrColonMinColonAmPm);
		String[] tmp = hrColonMinColonAmPm.split(":");
		String ampmLoc = getTimeMenuLocation("a");
		selenium
				.clickAt(
						"//td[contains(@id, 'startTimeSelect')]//td[contains(@id, 'timeSelectBtn')]//div[contains(@class, 'ImgSelectPullDownArrow')]",
						"");
		if (!ampmLoc.equals("-1")) {
			obj.zMenuItem.zClickInDlg(tmp[0] + ":" + tmp[1] + " " + tmp[2]);
		} else {
			obj.zMenuItem.zClickInDlg(tmp[0] + ":" + tmp[1]);
		}
	}

	public static void zSetStartTimeInQuickAddApptDlg(String hrColonMinColonAmPm)
			throws Exception {
		String[] tmp = hrColonMinColonAmPm.split(":");
		String ampmLoc = getTimeMenuLocation("a");
		selenium
				.clickAt(
						"//td[contains(@id, 'startTime')]//td[contains(@id, 'timeSelectBtn')]//div[contains(@class, 'ImgSelectPullDownArrow')]",
						"");
		if (!ampmLoc.equals("-1")) {
			obj.zMenuItem.zClickInDlg(tmp[0] + ":" + tmp[1] + " " + tmp[2]);
		} else {
			obj.zMenuItem.zClickInDlg(tmp[0] + ":" + tmp[1]);
		}
	}

	/**
	 * Sets the end time for the calendar invite
	 * 
	 * @param hrColonMinColonAmPm
	 *            : End time to be set
	 * @throws Exception
	 */
	public static void zSetEndTime(String hrColonMinColonAmPm) throws Exception {
		// zSetTimeCore(localize(locator.endLabel), hrColonMinColonAmPm);
		String[] tmp = hrColonMinColonAmPm.split(":");
		String ampmLoc = getTimeMenuLocation("a");
		selenium
				.clickAt(
						"//td[contains(@id, 'endTimeSelect')]//td[contains(@id, 'timeSelectBtn')]//div[contains(@class, 'ImgSelectPullDownArrow')]",
						"");
		if (!ampmLoc.equals("-1")) {
			obj.zMenuItem.zClickInDlg(tmp[0] + ":" + tmp[1] + " " + tmp[2]);
		} else {
			obj.zMenuItem.zClickInDlg(tmp[0] + ":" + tmp[1]);
		}
	}

	public static void zSetEndTimeInQuickAddApptDlg(String hrColonMinColonAmPm)
			throws Exception {
		String[] tmp = hrColonMinColonAmPm.split(":");
		String ampmLoc = getTimeMenuLocation("a");
		selenium
				.clickAt(
						"//td[contains(@id, 'endTime')]//td[contains(@id, 'timeSelectBtn')]//div[contains(@class, 'ImgSelectPullDownArrow')]",
						"");
		if (!ampmLoc.equals("-1")) {
			obj.zMenuItem.zClickInDlg(tmp[0] + ":" + tmp[1] + " " + tmp[2]);
		} else {
			obj.zMenuItem.zClickInDlg(tmp[0] + ":" + tmp[1]);
		}
	}

	/**
	 * Core function for setting the time
	 * 
	 * @param startOrEnd
	 *            : flag for start or end time
	 * @param hrColonMinColonAmPm
	 *            : Time to be set
	 */
	@SuppressWarnings("unused")
	private static void zSetTimeCore(String startOrEnd,
			String hrColonMinColonAmPm) {
		String[] tmp = hrColonMinColonAmPm.split(":");
		String hourLoc = getTimeMenuLocation("h");
		String minuteLoc = getTimeMenuLocation("mm");
		String ampmLoc = getTimeMenuLocation("a");

		obj.zFeatureMenu.zClick(startOrEnd, hourLoc);
		obj.zMenuItem.zClick(tmp[0]);
		obj.zFeatureMenu.zClick(startOrEnd, minuteLoc);
		obj.zMenuItem.zClick(tmp[1]);
		if (!ampmLoc.equals("-1")) {
			obj.zFeatureMenu.zClick(startOrEnd, ampmLoc);
			obj.zMenuItem.zClick(tmp[2]);
		}
	}

	/**
	 * Different locale has time-menus in different location. This function
	 * returns their location
	 * 
	 * @param val
	 *            pass "h" for hour menu, "mm" for minutes menu, //and "a" for
	 *            ampm menu
	 * @return
	 */
	public static String getTimeMenuLocation(String val) {
		// pass "h" for hour menu, "mm" for minutes menu,
		// and "a" for ampm menu
		String str = localize(locator.formatTimeShort);
		if (str.indexOf("H") >= 0)
			return getLocation24(val);
		else
			return getLocation12(val);
	}

	private static String getLocation12(String val) {
		String str = localize(locator.formatTimeShort);
		String[] str1 = str.split(" ");
		String one = "";
		String two = "";
		String three = "";
		if (str1[0].indexOf(":") > 0) {
			String[] str2 = str1[0].split(":");
			one = str2[0];
			two = str2[1];
			three = str1[1];
		} else if (str1[1].indexOf(":") > 0) {
			String[] str2 = str1[1].split(":");
			two = str2[0];
			three = str2[1];
			one = str1[0];
		}
		if (one.toLowerCase().indexOf(val) >= 0)
			return "1";
		else if (two.toLowerCase().indexOf(val) >= 0)
			return "2";
		else if (three.toLowerCase().indexOf(val) >= 0)
			return "3";

		// something has gone wrong
		return "-1";
	}

	private static String getLocation24(String val) {
		String str = localize(locator.formatTimeShort);
		String[] str1 = str.split(":");
		String one = str1[0];
		String two = str1[1];
		if (one.toLowerCase().indexOf(val) >= 0)
			return "1";
		else if (two.toLowerCase().indexOf(val) >= 0)
			return "2";

		// something has gone wrong
		return "-1";
	}

	public static void zVerifyStartEndTime(String hhmmss, String time)
			throws Exception {
		String hourLoc = page.zCalCompose.getTimeMenuLocation("h");
		String minuteLoc = page.zCalCompose.getTimeMenuLocation("mm");
		String ampmLoc = page.zCalCompose.getTimeMenuLocation("a");
		if (time.equals("startTime")) {
			Thread.sleep(2000);
			Assert
					.assertTrue(selenium
							.isElementPresent("//div[contains(@class, 'DwtDialog LightWindowOuterContainer')]//td[contains(@id, 'DWT') and contains(@id, 'startTime')]//td[contains(text(), '"
									+ hhmmss.split(":")[0] + "')]"));

			selenium
					.clickAt(
							"//td[contains(@id, 'startTimeSelect')]//td[contains(@id, 'timeSelectInput') and contains(text(), '10:00')]",
							"");

			obj.zFeatureMenu.zClick(
					getNameWithoutSpace(localize(locator.startLabel)), hourLoc);
			obj.zFeatureMenu.zClick(
					getNameWithoutSpace(localize(locator.startLabel)),
					minuteLoc);
			if (!ampmLoc.equals("-1")) {
				obj.zFeatureMenu.zClick(
						getNameWithoutSpace(localize(locator.startLabel)),
						ampmLoc);
			}
		} else if (time.equals("endTime")) {
			obj.zFeatureMenu.zClick(
					getNameWithoutSpace(localize(locator.endLabel)), hourLoc);
			obj.zFeatureMenu.zClick(
					getNameWithoutSpace(localize(locator.endLabel)), minuteLoc);
			if (!ampmLoc.equals("-1")) {
				obj.zFeatureMenu.zClick(
						getNameWithoutSpace(localize(locator.endLabel)),
						ampmLoc);
			}
		}
	}

	public static void zVerifyStartEndTimeInQuickAddApptDlg(String hhmmss,
			String time) throws Exception {
		if (time.equals("startTime")) {
			Thread.sleep(1000);
			if (config.getString("locale").equals("en_US")) {
				Assert
						.assertTrue(selenium
								.isElementPresent("//td[contains(@id, 'startTime')]//td[contains(@id, 'timeSelectInput') and contains(text(), '"
										+ hhmmss.split(":")[0] + "')]"));

			} else {
				Assert
						.assertTrue(selenium
								.isElementPresent("//td[contains(@id, 'startTime')]//td[contains(@id, 'timeSelectInput') and contains(text(), '"
										+ hhmmss.split(":")[0] + "')]"));
			}
		} else if (time.equals("endTime")) {
			if (config.getString("locale").equals("en_US")) {
				Assert
						.assertTrue(selenium
								.isElementPresent("//td[contains(@id, 'endTime')]//td[contains(@id, 'timeSelectInput') and contains(text(), '"
										+ hhmmss.split(":")[0] + "')]"));
			} else {
				Assert
						.assertTrue(selenium
								.isElementPresent("//td[contains(@id, 'endTime')]//td[contains(@id, 'timeSelectInput') and contains(text(), '"
										+ hhmmss.split(":")[0] + "')]"));
			}
		}
	}

	public static void zVerifyStartEndTimeInQuickAddApptDlgNotExists()
			throws Exception {
		Thread.sleep(2000);
		Assert
				.assertFalse(selenium
						.isElementPresent("//div[contains(@class, 'DwtDialog LightWindowOuterContainer')]//td[contains(@id, 'DWT') and contains(@id, 'startTime') and contains(@style, 'visibility: hidden')]"));

		Assert
				.assertFalse(selenium
						.isElementPresent("//div[contains(@class, 'DwtDialog LightWindowOuterContainer')]//td[contains(@id, 'DWT') and contains(@id, 'endTime') and contains(@style, 'visibility: hidden')]"));
	}

	public static void zVerifyStartEndTimeNotExists() throws Exception {
		Thread.sleep(2000);
		obj.zFeatureMenu.zNotExists(localize(locator.startLabel));
		obj.zFeatureMenu.zNotExists(localize(locator.endLabel));
	}
}