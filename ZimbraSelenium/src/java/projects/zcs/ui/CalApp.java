package projects.zcs.ui;

//import java.io.File;

//import java.util.Map;

//import org.testng.Assert;

//import projects.zcs.Locators;

import org.testng.Assert;

import framework.util.ZimbraSeleniumProperties;

import projects.zcs.tests.CommonTest;

/**
 * This Class have UI-level methods related calendar application. If you are
 * dealing with the toolbar buttons, use these icons since in vmware resolutions
 * and in some languages button-labels are not displayed(but just their icons)
 * 
 * @author Krishna Kumar Sure
 * 
 */
@SuppressWarnings("static-access")
public class CalApp extends CommonTest {
	public static String zNewCalOverviewPaneIcon = "id=ztih__main_Calendar__CALENDAR_textCell";
	public static String zCalendarFolder = "id=zti__main_Calendar__10_checkbox";
	public static final String zCalendarTab = "id=zb__App__Calendar_left_icon";
	public static final String zViewBtn = "id=zb__CLD__VIEW_MENU_left_icon";
	public static final String zCalendarOverviewFolder = "id=ztih__main_Calendar__CALENDAR_table";
	public static final String zCalNewApptBtn = "id=zb__CLD__NEW_MENU_left_icon";
	public static final String zCalRefreshBtn = "id=zb__CLD__CAL_REFRESH_left_icon";
	public static final String zCalDeleteBtn = "id=zb__CLD__DELETE_left_icon";
	public static final String zCalDelete = "id=zb__CLD__DELETE";
	public static final String zCalPrintBtn = "id=zb__CLD__PRINT_left_icon";
	public static final String zCalTagBtn = "id=zb__CLD__TAG_MENU_left_icon";
	public static final String zCalDayBtn = "id=zb__CLD__DAY_VIEW_left_icon";
	public static final String zCalWorkWeekBtn = "id=zb__CLD__WORK_WEEK_VIEW_left_icon";
	public static final String zCalWeekBtn = "id=zb__CLD__WEEK_VIEW_left_icon";
	public static final String zCalMonthBtn = "id=zb__CLD__MONTH_VIEW_left_icon";
	public static final String zCalScheduleBtn = "id=zb__CLD__SCHEDULE_VIEW_left_icon";
	public static final String zCalTodayBtn = "id=zb__CLD__TODAY_left_icon";
	public static final String zCalPreviousPageBtn = "id=zb__CAL__Nav__PAGE_BACK_left_icon";
	public static final String zCalForwardPageBtn = "id=zb__CAL__Nav__PAGE_FORWARD_left_icon";
	public static final String zPreferencesTabIconBtn = "id=zb__App__Options_left_icon";
	public static final String zPreferencesCalendarIconBtn = "id=ztab__PREF__"
			+ localize(locator.calendar) + "_title";
	public static final String zPreferencesSaveIconBtn = "id=zb__PREF__SAVE_left_icon";

	public static final String zAppointmentAcceptDropdown = "id=zb__CLV__Inv__REPLY_ACCEPT_right_icon";
	public static final String zAppointmentTentativeDropdown = "id=zb__CLV__Inv__REPLY_TENTATIVE_right_icon";
	public static final String zAppointmentDeclineDropdown = "id=zb__CLV__Inv__REPLY_DECLINE_right_icon";

	public static final String zApptSaveBtn = "id=zb__APPT__SAVE_left_icon";
	public static final String zCalViewBtn = "id=zb__CLD__VIEW_MENU_left_icon";

	/**
	 * Navigates to Calendar from MailApp
	 */
	public static void zNavigateToCalendar() throws Exception {
		zGoToApplication("Calendar");
	}

	/**
	 * Navigates directly to appointment compose
	 */
	public static void zNavigateToApptComposeDirect() throws Exception {
		zGoToApplication("Calendar");
		obj.zButton.zClick(zCalNewApptBtn);
	}

	/**
	 * Navigates to appointment compose pane when in calendar application
	 */
	public static void zNavigateToApptCompose() throws Exception {
		obj.zButton.zClick(zCalNewApptBtn);
		zWaitTillObjectExist("button", page.zCalApp.zApptSaveBtn);
	}

	/**
	 * Navigates to the Calendar preferences
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToCalendarPreferences() throws Exception {
		zGoToApplication("Preferences");
		zGoToPreferences("Calendar");
	}

	/**
	 * Navigates to the particular given view
	 * 
	 * @param view
	 *            Day, Week, WorkWeek, Month, List, Schedule
	 * @throws Exception
	 */
	public static void zCalViewSwitch(String view) throws Exception {
		obj.zButton.zClick(zCalViewBtn);
		view = view.toLowerCase();
		if (view.equals("day"))
			obj.zMenuItem.zClick(localize(locator.day));
		else if (view.equals("week"))
			if (ZimbraSeleniumProperties.getStringProperty("locale").equals("zh_HK")) {
				obj.zMenuItem.zClick(localize(locator.viewWeek));
			} else {
				obj.zMenuItem.zClick(localize(locator.week));
			}
		else if (view.equals("workweek"))
			obj.zMenuItem.zClick(localize(locator.workWeek));
		else if (view.equals("month"))
			obj.zMenuItem.zClick(localize(locator.month));
		else if (view.equals("list"))
			obj.zMenuItem.zClick(localize(locator.list));
		else if (view.equals("schedule"))
			obj.zMenuItem.zClick(localize(locator.schedule));
	}

	/**
	 * Opens one instance of a recurring appointment
	 * 
	 * @param subject
	 *            : subject of the appointment to be opened
	 * @throws Exception
	 */
	public static void zOpenInstanceOfRecurringAppt(String subject)
			throws Exception {
		obj.zAppointment.zDblClick(subject);
		obj.zDialog.zExists(localize(locator.openRecurringItem));
		obj.zButton.zClickInDlg(localize(locator.ok));
	}

	/**
	 * Opens the entire series of the recurring appointment
	 * 
	 * @param subject
	 *            : subject of the appointment to be opened
	 * @throws Exception
	 */
	public static void zOpenSeriesRecurringAppt(String subject)
			throws Exception {
		obj.zAppointment.zDblClick(subject);
		obj.zDialog.zExists(localize(locator.openRecurringItem));
		obj.zRadioBtn.zClickInDlg(localize(locator.openSeries));
		obj.zButton.zClickInDlg(localize(locator.ok));
	}

	/**
	 * Deletes appointment which has attendees
	 * 
	 * @param subject
	 *            : subject of the appointment to be deleted
	 * @throws Exception
	 */
	public static void zDeleteAppointmentWithAttendees(String subject)
			throws Exception {
		zDeleteAppointment(subject, "1");
	}

	/**
	 * Deletes appointment which has no attendees
	 * 
	 * @param subject
	 *            : subject of the appointment to be deleted
	 * @throws Exception
	 */
	public static void zDeleteAppointmentWithoutAttendees(String subject)
			throws Exception {
		zDeleteAppointment(subject, "");
	}

	/**
	 * Delete appointment core function
	 * 
	 * @param subject
	 *            : subject of the appointment to be deleted
	 * @param attendees
	 *            : string for attendees present or not. if not pass ""
	 * @throws Exception
	 */
	private static void zDeleteAppointment(String subject, String attendees)
			throws Exception {
		obj.zAppointment.zClick(subject);
		obj.zButton.zClick(zCalDeleteBtn);
		obj.zDialog.zExists(localize(locator.confirmTitle));
		if (attendees.equals("1")) {
			obj.zButton.zClickInDlg(localize(locator.yes));
		} else {
			obj.zButton.zClickInDlg(localize(locator.no));
		}
		Thread.sleep(2000);
	}

	/**
	 * Deletes first instance of a recurring appointment
	 * 
	 * @param subject
	 *            : subject of the appointment to be deleted
	 * @throws Exception
	 */
	public static void zDeleteInstanceOfRecurringAppt(String subject)
			throws Exception {
		obj.zAppointment.zClick(subject);
		obj.zButton.zClick(zCalDeleteBtn);
		obj.zDialog.zExists(localize(locator.deleteRecurringItem));
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(2000);
	}

	/**
	 * Deletes entire series of a recurring appointment
	 * 
	 * @param subject
	 *            : subject of the appointment to be deleted
	 * @throws Exception
	 */
	public static void zDeleteSeriesRecurringAppt(String subject)
			throws Exception {
		obj.zAppointment.zClick(subject);
		obj.zButton.zClick(zCalDeleteBtn);
		obj.zDialog.zExists(localize(locator.deleteRecurringItem));
		obj.zRadioBtn.zClickInDlg(localize(locator.deleteSeries));
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(2000);
	}

	/**
	 * 
	 * @param subject
	 *            : subject of the appointment to be moved
	 * @param sourceCalendar
	 *            : source calendar for the appointment
	 * @param newCalendar
	 *            : new calendar for the appointment
	 * @throws Exception
	 */
	public static void zMoveAppointment(String subject, String sourceCalendar,
			String newCalendar) throws Exception {
		obj.zAppointment.zDblClick(subject);
		obj.zFeatureMenu.zClick(localize(locator.calendarLabel));
		obj.zMenuItem.zClick(newCalendar);
		obj.zButton.zClick(zApptSaveBtn);
		Thread.sleep(1000);
		obj.zButton.zClick(zCalRefreshBtn);
	}

	/**
	 * Creates a new calendar using the overview button
	 * 
	 * @param calendarName
	 *            : name of the calendar to be created
	 * @throws Exception
	 */
	public static void zCreateNewCalendarFolder(String calendarName)
			throws Exception {
		zWaitTillObjectExist("button",
				replaceUserNameInStaticId(zNewCalOverviewPaneIcon));
		obj.zButton
				.zRtClick(replaceUserNameInStaticId(zNewCalOverviewPaneIcon));
		obj.zMenuItem.zClick(localize(locator.newCalendar));
		obj.zEditField.zTypeInDlgByName(localize(locator.nameLabel),
				calendarName, localize(locator.createNewCalendar));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewCalendar));
		Thread.sleep(1000);
	}

	/**
	 * Deletes the given calendar
	 * 
	 * @param calendarName
	 *            : name of the calendar to be deleted
	 * @throws Exception
	 */
	public static void zDeleteCalendarFolder(String calendarName)
			throws Exception {
		obj.zCalendarFolder.zRtClick(calendarName);
		obj.zMenuItem.zClick(localize(locator.del));
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.confirmTitle));
	}

	/**
	 * Rename the calendar
	 * 
	 * @param calendarName
	 *            : name of the calendar to be renamed
	 * @param newCalendarName
	 *            : new name for the calendar
	 * @throws Exception
	 */
	public static void zRenameCalendarFolder(String calendarName,
			String newCalendarName) throws Exception {
		obj.zCalendarFolder.zRtClick(calendarName);
		obj.zMenuItem.zClick(localize(locator.editProperties));
		obj.zEditField.zTypeInDlgByName(localize(locator.nameLabel),
				newCalendarName, localize(locator.folderProperties));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.folderProperties));
	}

	/**
	 * Checks the calendar check-box
	 * 
	 * @param calendarName
	 *            : name of the calendar to be checked
	 * @throws Exception
	 */
	public static void zCalendarCheck(String calendarName) throws Exception {
		obj.zFolderCheckbox.zActivate(calendarName);
	}

	/**
	 * Unchecks the calendar checkbox
	 * 
	 * @param calendarName
	 *            : name of the calendar to be unchecked
	 * @throws Exception
	 */
	public static void zCalendarUncheck(String calendarName) throws Exception {
		obj.zFolderCheckbox.zActivate(calendarName);
	}

	/**
	 * @param subject
	 *            : appointment invite to be accepted
	 * @throws Exception
	 */
	public static void zAcceptInvite(String subject) throws Exception {
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.replyAccept));
		Thread.sleep(1000);
		Assert
				.assertEquals(
						obj.zMessageItem.zExistsDontWait(subject),
						"false",
						"Appointment invitation mail doesn't move to Trash folder after accept/decline/tentative invite");
		obj.zButton.zNotExists(localize(locator.replyAccept));
		obj.zButton.zNotExists(localize(locator.replyTentative));
		obj.zButton.zNotExists(localize(locator.replyDecline));
	}

	/**
	 * @param subject
	 *            : appointment invite to be declined
	 * @throws Exception
	 */
	public static void zDeclineInvite(String subject) throws Exception {
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.replyDecline));
		Thread.sleep(1000);
		Assert
				.assertEquals(
						obj.zMessageItem.zExistsDontWait(subject),
						"false",
						"Appointment invitation mail doesn't move to Trash folder after accept/decline/tentative invite");
		obj.zButton.zNotExists(localize(locator.replyAccept));
		obj.zButton.zNotExists(localize(locator.replyTentative));
		obj.zButton.zNotExists(localize(locator.replyDecline));
	}

	/**
	 * @param subject
	 *            : appointment invite to be marked tentative
	 * @throws Exception
	 */
	public static void zTentativeInvite(String subject) throws Exception {
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.replyTentative));
		Thread.sleep(1000);
		Assert
				.assertEquals(
						obj.zMessageItem.zExistsDontWait(subject),
						"false",
						"Appointment invitation mail doesn't move to Trash folder after accept/decline/tentative invite");
		obj.zButton.zNotExists(localize(locator.replyAccept));
		obj.zButton.zNotExists(localize(locator.replyTentative));
		obj.zButton.zNotExists(localize(locator.replyDecline));
	}

	/**
	 * @param subject
	 *            : subject of the appointment to be replied to
	 * @param action
	 *            : action as whether to Accept/Decline/Tentative appointments
	 * @param replyContent
	 *            : content to be replied with
	 * @throws Exception
	 */
	public static void zRespondApptEditReply(String subject, String action,
			String replyContent) throws Exception {
		obj.zMessageItem.zClick(subject);
		Thread.sleep(1000);
		if (action.equals("accept"))
			obj.zButtonMenu.zClick(localize(locator.replyAccept));
		if (action.equals("decline"))
			obj.zButtonMenu.zClick(localize(locator.replyDecline));
		if (action.equals("tentative"))
			obj.zButtonMenu.zClick(localize(locator.replyTentative));
		obj.zMenuItem.zClick(localize(locator.editReply));
		zWaitTillObjectExist("button", page.zComposeView.zSendIconBtn);
		obj.zEditor.zType(replyContent);
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		Thread.sleep(1500);
	}

	/**
	 * This function verifies that the given summary and items are present in
	 * the invite message body
	 * 
	 * @param firstLineSummary
	 *            : first summary line of the appointment invite
	 * @param itemsToVerify
	 *            : array of items to be verified in the invite message body
	 * @throws Exception
	 */
	public static void zVerifyInviteContent(String firstLineSummary,
			String itemsToVerify[]) throws Exception {
		Thread.sleep(3000);
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
}