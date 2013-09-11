/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.touch.ui.calendar;

import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.zimbra.qa.selenium.framework.core.SeleniumService;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;

/**
 * The <code>FormMailNew<code> object defines a compose new message view
 * in the Zimbra Ajax client.
 * <p>
 * This class can be used to compose a new message.
 * <p>
 * 
 * @author Matt Rhoades
 * @see http 
 *      ://wiki.zimbra.com/wiki/Testing:_Selenium:_ZimbraSelenium_Overview#Mail_Page
 */
public class FormApptNew extends AbsForm {
	public static String locatorValue;
	PageCalendar pageCal = new PageCalendar(MyApplication);

	/**
	 * Defines Selenium locators for various objects in {@link FormApptNew}
	 */
	public static class Locators {
						
		public static final String SubjectDisabled = "css=div[id^='APPT_COMPOSE_'] td[id$='_subject'] div[class='DwtInputField-disabled']";
		public static final String ToDisabled = "css=div[id^='APPT_COMPOSE_'] td tr[id$='_forward_options'] div[class='ZButton ZPicker ZWidget ZHasText ZDisabled']";
		public static final String AttendeesDisabled = "css=div[id^='APPT_COMPOSE_'] td tr[id$='_attendeesContainer'] div[class='ZButton ZPicker ZWidget ZHasText ZDisabled']";
		public static final String OptionalDisabled = "css=div[id^='APPT_COMPOSE_'] td tr[id$='_optionalContainer'] div[class='ZButton ZPicker ZWidget ZHasText ZDisabled']";
		public static final String LocationDisabled = "css=div[id^='APPT_COMPOSE_'] td tr[id$='_forward_options'] div[class='ZButton ZPicker ZWidget ZHasText ZDisabled']";
		public static final String EquipmentDisabled = "css=div[id^='APPT_COMPOSE_'] td tr[id$='_resourcesContainer'] div[class='ZButton ZPicker ZWidget ZHasText ZDisabled']";
		public static final String DisplayDisabled = "css=div[id^='APPT_COMPOSE_'] td[id$='_showAsSelect'] div[class$='ZHasDropDown ZDisabled ZHasLeftIcon']";
		public static final String FolderDisabled = "css=div[id^='APPT_COMPOSE_'] td[id$='_folderSelect'] div[class$='ZHasDropDown ZDisabled ZHasLeftIcon']";
		public static final String PrivateDisabled = "css=div[id^='APPT_COMPOSE_'] td input[id$='_privateCheckbox'][type='checkbox'][disabled]";
		
		public static final String RepeatOptionsDisabled = "css=td[id$='_repeat_options'] div[id^='DWT'][class$='ZHasDropDown ZDisabled']";
		public static final String RepeatDescriptionDisabled = "css=div[id$='_repeatDesc'][class='DisabledText']";

		public static final String ShowOptionalLink = "css=td[id$='_show_optional']";
		public static final String ShowEquipmentLink = "css=td[id$='_show_resources']";
		public static final String CustomizeLink = "css=div[id$='repeatDesc']:contains('Customize')";
		public static final String ConfigureLink = "css=div[class='FakeAnchor']:contains('Configure')";
		public static final String SuggestAtimeLink = "css=div[id$='_suggest_time']:contains('Suggest a time')";
		public static final String SuggestATime10AM = "css=div[id$='_suggest_view'] td:contains(10:00 AM)";
		public static final String SuggestALocationLink = "css=div[id$='_suggest_location']:contains('Suggest a location')";
		public static String SuggestedLocations = "css=div[id='zv__CSLP'] div[class$='ZmLocationSuggestion']:contains('"+ locatorValue + "')";
		public static final String ShowSchedulerLink = "css=div[id$='_scheduleButton']:contains('Show')";
		public static final String HideSchedulerLink = "css=div[id$='_scheduleButton']:contains('Hide')";
		public static final String SelectFirstFreeTimeFromSuggestTimePane = "css=div[id$='_suggest_view'] table:nth-child(2) tbody tr td:nth-child(2)";
		public static final String ShowTimesAnywayLink = "css=div[id$='_suggest_view'] div[class='NoSuggestions'] span[id$='showall']";

		public static final String Button_Send = "css=div[id^='ztb__APPT-'] td[id$='_SEND_INVITE_title']";
		public static final String Button_Save = "css=div[id^='ztb__APPT-'] td[id$='_SAVE_title']";
		public static final String Button_SaveAndClose = "css=div[id^='ztb__APPT-'] td[id$='_SAVE_title']";
		public static final String Button_Close = "css=div[id^='ztb__APPT-'] td[id$='_CANCEL_title']";
		
		public static final String OptionsDropdown = "css=div[id$='__COMPOSE_OPTIONS'] td[id$='COMPOSE_OPTIONS_title']";
		public static final String SuggestionPreferencesMenu = "css=div[class='DwtMenu ZHasCheck ZHasIcon'] td[id$='_title']:contains('Suggestion Preferences')";
		public static final String OnlyIncludeMyWorkingHoursCheckBox = "css=input[id$='_my_working_hrs_pref']";
		public static final String OnlyIncludeOtherAttendeeCheckBox = "css=input[id$='_others_working_hrs_pref']";
		public static final String NameLocationPreferencesField = "css=div[class='ZmTimeSuggestionPrefDialog'] table[id$='_locationpref'] input[id$='_name']";
		public static final String OKButtonSuggestionPreferencesDialog = "css=div[class='ZmTimeSuggestionPrefDialog'] td[id$='_button2_title']";
		public static final String CancelButtonSuggestionPreferencesDialog = "css=div[class='ZmTimeSuggestionPrefDialog'] td[id$='_button1_title']";

		public static final String NoneMenuItem = "css=div[id*='_Menu'] div[id^='NON'] td[id$='title']:contains('None')";
		public static final String NoneButton = "css=div[id$='_repeatSelect'] td[id$='_select_container'] td[id$='_title']";
		public static final String EveryDayMenuItem = "css=div[id*='_Menu'] div[id^='DAI'] td[id$='title']:contains('Every Day')";
		public static final String EveryDayButton = "css=td[id$='_title']:contains('Every Day')";
		public static final String EveryWeekMenuItem = "css=div[id*='_Menu'] div[id^='WEE'] td[id$='title']:contains('Every Week')";
		public static final String EveryWeekButton = "css=td[id$='_title']:contains('Every Week')";
		public static final String EveryMonthMenuItem = "css=div[id*='_Menu'] div[id^='MON'] td[id$='title']:contains('Every Month')";
		public static final String EveryMonthButton = "css=td[id$='_title']:contains('Every Month')";
		public static final String EveryYearMenuItem = "css=div[id*='_Menu'] div[id^='YEA'] td[id$='title']:contains('Every Year')";
		public static final String EveryYearButton = "css=td[id$='_title']:contains('Every Year')";
		public static final String CustomMenuItem = "css=div[id*='_Menu'] div[id^='CUS'] td[id$='title']:contains('Custom')";
		public static final String CustomButton = "css=td[id$='_title']:contains('Custom')";
		public static final String CustomizedLink = "css=div[id$='_repeatDesc']div[class='FakeAnchor']";
		public static final String CustomizedLinkDisabled = "css=div[id$='_repeatDesc']div[class='DisabledText']";
		
		public static final String EveryDayRadioButton = "css=div[id='REPEAT_DAILY_DIV'] input[id='DAILY_DEFAULT']";
		public static final String EveryWeekdayRadioButton = "css=div[id='REPEAT_DAILY_DIV'] input[id='DAILY_WEEKDAY']";
		public static final String EveryXdaysRadioButton = "css=div[id='REPEAT_DAILY_DIV'] input[id='DAILY_FIELD_RADIO']";
		public static final String EveryXdaysEditField = "css=input[id='RECUR_DAILY_FIELD']";
		
		public static final String EveryXRadioButton = "css=div[id='REPEAT_WEEKLY_DIV'] input[id='WEEKLY_DEFAULT']";
		public static final String EveryXDropdown = "css=div[id='REPEAT_WEEKLY_DIV'] td[id='WEEKLY_SELECT'] td[id$='_title']";
		public static final String EveryXweeksOnRadioButton = "css=div[id='REPEAT_WEEKLY_DIV'] input[id='WEEKLY_FIELD_RADIO']";
		public static final String EveryXweeksOnEditField = "css=div[id='REPEAT_WEEKLY_DIV'] input[id='RECUR_WEEKLY_FIELD']";
		public static final String SundayCheckBox = "css=div[id='REPEAT_WEEKLY_DIV'] input[id='WEEKLY_CHECKBOX_NAME_1']";
		public static final String MondayCheckBox = "css=div[id='REPEAT_WEEKLY_DIV'] input[id='WEEKLY_CHECKBOX_NAME_2']";
		public static final String TuesdayCheckBox = "css=div[id='REPEAT_WEEKLY_DIV'] input[id='WEEKLY_CHECKBOX_NAME_3']";
		public static final String WednesdayCheckBox = "css=div[id='REPEAT_WEEKLY_DIV'] input[id='WEEKLY_CHECKBOX_NAME_4']";
		public static final String ThursdayCheckBox = "css=div[id='REPEAT_WEEKLY_DIV'] input[id='WEEKLY_CHECKBOX_NAME_5']";
		public static final String FridayCheckBox = "css=div[id='REPEAT_WEEKLY_DIV'] input[id='WEEKLY_CHECKBOX_NAME_6']";
		public static final String SaturdayCheckBox = "css=div[id='REPEAT_WEEKLY_DIV'] input[id='WEEKLY_CHECKBOX_NAME_7']";
		
		public static final String DayXofEveryYmonthsRadioButton = "css=div[id='REPEAT_MONTHLY_DIV'] input[id='MONTHLY_DEFAULT']";
		public static final String TheXYofEveryZmonthsRadioButton = "css=div[id='REPEAT_MONTHLY_DIV'] input[id='MONTHLY_FIELD_RADIO']";
		
		public static final String EveryYearOnXYRadioButton = "css=div[id='REPEAT_YEARLY_DIV'] input[id='YEALY_DEFAULT']";
		public static final String TheXYofEveryZRadioButton = "css=div[id='REPEAT_YEARLY_DIV'] input[id='YEARLY_FIELD_RADIO']";
		
		public static final String NoEndDateRadioButton = "css=div[id$='_content'] td input[id='NO_END_DATE_RADIO']";
		public static final String EndAfterXoccurrencesRadioButton = "css=div[id$='_content'] td input[id='END_AFTER_RADIO']";
		public static final String EndAfterXoccurrencesEditField = "css=div[id$='_content'] td input[id='RECUR_END_INTERVAL_FIELD']";
		public static final String EndByXDateRadioButton = "css=div[id$='_content'] td input[id='END_BY_RADIO']";
		public static final String EndByXDateEditField = "css=div[id$='_content'] td input[id='RECUR_END_BY_FIELD']";
		
		public static final String DeleteZimletContextMenu = "css=div[id^='POPUP_'] td[id='DELETE_title']";
		public static final String EditZimletContextMenu = "css=div[id^='POPUP_'] td[id='EDIT_title']";
		public static final String ExpandZimletContextMenu = "css=div[id^='POPUP_'] td[id='EXPAND_title']";
		public static final String AddToContactsZimletContextMenu = "css=div[id^='POPUP_'] td[id='CONTACT_title']";

		public static final String SendUpdatesToAddedRemovedRadioButton = "css=div[id='SEND_NOTIFY_DIALOG'] div[id='SEND_NOTIFY_DIALOG_content']>table tbody tr:nth-child(1) input";
		public static final String SendUpdatesToAllRadioButton = "css=div[id='SEND_NOTIFY_DIALOG'] div[id='SEND_NOTIFY_DIALOG_content']>table tbody tr:nth-child(2) input";

		public static final String Ok_changes = "css=td[id='CHNG_DLG_ORG_1_button2_title']";
		public static final String Cancel_changes = "css=td[id='CHNG_DLG_ORG_1_button1_title']";
		public static final String AddLocation = "css=td[id$='_title']:contains('Location:')";
		public static final String addEquipment = "css=td[id$='_title']:contains('Equipment:')";

		public static final String AddAttendees = "css=td[id$='_title']:contains('Attendees:')";
		public static final String AddOptiponalAttendees = "css=td[id$='_title']:contains('Optional:')";
		public static final String EquipmentName= "css=div[class='DwtDialog'] div[id$='_content'] table tr td:nth-child(2) input";
		public static final String ToolbarOptions= "css=td[id='zb__APPT-1__COMPOSE_OPTIONS_title']";
		public static final String RequestResponse= "css=td[id$='_title']:contains('Request Responses')";
		public static final String ConflictResourceNote = "css= div[id$='_location_status']:contains('One or more locations are not available at the selected time')";

	}

	public static class Field {

		public static final Field Subject = new Field("Subject");
		public static final Field Attendees = new Field("Attendees");
		public static final Field Optional = new Field("Optional");
		public static final Field Location = new Field("Location");
		public static final Field Equipment = new Field("Equipment");
		public static final Field StartDate = new Field("StartDate");
		public static final Field StartTime = new Field("StartTime");
		public static final Field EndDate = new Field("EndDate");
		public static final Field EndTime = new Field("EndTime");
		public static final Field AllDay = new Field("AllDay");
		public static final Field Repeat = new Field("Repeat");
		public static final Field Display = new Field("Display");
		public static final Field CalendarFolder = new Field("CalendarFolder");
		public static final Field Private = new Field("Private");
		public static final Field Reminder = new Field("Reminder");
		public static final Field Body = new Field("Body");

		private String field;

		private Field(String name) {
			field = name;
		}

		@Override
		public String toString() {
			return (field);
		}

	}

	/**
	 * Protected constuctor for this object. Only classes within this package
	 * should create DisplayMail objects.
	 * 
	 * @param application
	 */
	public FormApptNew(AbsApplication application) {
		super(application);

		logger.info("new " + FormApptNew.class.getCanonicalName());

	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zSubmit() throws HarnessException {
		logger.info("FormMailNew.submit()");

		// Send: if there are attendees
		// Save: If there are no attendees

		// If send is visible, click it
		// Otherwise, click Save
		String locator = "css=div[id$=_SEND_INVITE]";
		if (this.sIsElementPresent(locator) && this.sIsVisible(locator)) {
			zToolbarPressButton(Button.B_SEND);
		} else {
			zToolbarPressButton(Button.B_SAVE);
		}

		this.zWaitForBusyOverlay();
		SleepUtil.sleepMedium();
		
		// Wait for the message to be delivered
		Stafpostqueue sp = new Stafpostqueue();
		sp.waitForPostqueue();
	}

	public String zGetSuggestedLocation(String apptLocation)
			throws HarnessException {
		return "css=div[id='zv__CSLP'] div[id^='zli__CSLP__']:contains('"
				+ apptLocation + "')";
	}

	public String zGetLocationVaueFromPopUp(String apptLocation)
			throws HarnessException {
		return "css=div[id^='POPUP_DWT'] td[id^='" + apptLocation + "']";

	}
	
	public Boolean zIsLocationExistsInSuggestPane(String apptLocation)
			throws HarnessException {
		return sIsElementPresent("css=div[id='zv__CSLP'] div[id^='zli__CSLP__']:contains('" + apptLocation + "')");
	}
	
	public void zAddRequiredAttendeeFromScheduler(String attendee, int keyEvent)
			throws HarnessException {
		zToolbarPressButton(Button.B_SHOW);
		SleepUtil.sleepSmall();
		this.zType("css=td[id$='_scheduler'] td[id$='_NAME_'] input", attendee);
		this.sClickAt("css=td[id$='_scheduler'] td[id$='_NAME_'] input", "");
		SleepUtil.sleepSmall();
		pageCal.zKeyboard.zTypeKeyEvent(keyEvent);
	}

	public void zAddOptionalAttendeeFromScheduler(String attendee, int keyEvent)
			throws HarnessException {
		zToolbarPressButton(Button.B_SHOW);
		SleepUtil.sleepSmall();
		this
				.zClickAt(
						"css=td[id$='_scheduler'] td[id$='_SELECT_'] td[id$='_dropdown']",
						"");
		this
				.zClickAt(
						"css=div[class='DwtMenu ZHasIcon'] td[id$='_title']:contains('Optional Attendee')",
						"");
		this.zType("css=td[id$='_scheduler'] td[id$='_NAME_'] input", attendee);
		this.sClickAt("css=td[id$='_scheduler'] td[id$='_NAME_'] input", "");
		SleepUtil.sleepSmall();
		pageCal.zKeyboard.zTypeKeyEvent(keyEvent);
	}

	public void zAddLocationFromScheduler(String location, int keyEvent)
			throws HarnessException {
		SleepUtil.sleepMedium();
		zToolbarPressButton(Button.B_SHOW);
		SleepUtil.sleepSmall();
		this
				.zClickAt(
						"css=td[id$='_scheduler'] td[id$='_SELECT_'] td[id$='_dropdown']",
						"");
		this
				.zClickAt(
						"css=div[class='DwtMenu ZHasIcon'] td[id$='_title']:contains('Location')",
						"");
		this.zType("css=td[id$='_scheduler'] td[id$='_NAME_'] input", location);
		this.sClickAt("css=td[id$='_scheduler'] td[id$='_NAME_'] input", "");
		SleepUtil.sleepSmall();
		pageCal.zKeyboard.zTypeKeyEvent(keyEvent);
	}

	public void zAddEquipmentFromScheduler(String equipment, int keyEvent)
			throws HarnessException {
		zToolbarPressButton(Button.B_SHOW);
		SleepUtil.sleepSmall();
		this
				.zClickAt(
						"css=td[id$='_scheduler'] td[id$='_SELECT_'] td[id$='_dropdown']",
						"");
		this
				.zClickAt(
						"css=div[class='DwtMenu ZHasIcon'] td[id$='_title']:contains('Equipment')",
						"");
		this
				.zType("css=td[id$='_scheduler'] td[id$='_NAME_'] input",
						equipment);
		this.sClickAt("css=td[id$='_scheduler'] td[id$='_NAME_'] input", "");
		SleepUtil.sleepSmall();
		pageCal.zKeyboard.zTypeKeyEvent(keyEvent);
	}

	public String zGetApptSubject() throws HarnessException {
		return sGetValue("css=td[id$='_subject'] input[id$='_subject_input']");
	}

	public Boolean zVerifyRequiredAttendee(String attendee)
			throws HarnessException {
		return sIsElementPresent("css=td[id$='_person'] span:contains('"
				+ attendee + "')");
	}

	public Boolean zVerifyOptionalAttendee(String attendee)
			throws HarnessException {
		return sIsElementPresent("css=td[id$='_optional'] span:contains('"
				+ attendee + "')");
	}

	public Boolean zVerifyLocation(String location) throws HarnessException {
		return sIsElementPresent("css=td[id$='_location'] span:contains('"
				+ location + "')");
	}

	public Boolean zVerifyEquipment(String equipment) throws HarnessException {
		return sIsElementPresent("css=td[id$='_resourcesData'] span:contains('"
				+ equipment + "')");
	}

	public String zGetApptBodyValue() throws HarnessException {
		return sGetValue("css=div[class='ZmHtmlEditor'] textarea[class='DwtHtmlEditorTextArea']");
	}
	
	public String zGetApptBodyText() throws HarnessException {
		String bodyText;
		this.sSelectFrame("css=iframe[id$='_content_ifr']");
		bodyText = sGetText("css=body[id='tinymce']"); 
		this.sSelectFrame("relative=top");
		return bodyText;
		
	}
	

	public void zRemoveAttendee(String attendee) throws HarnessException {
		SleepUtil.sleepSmall(); // let free/busy UI draw and then we take UI
								// actions
		this.zRightClickAt("css=td[id$='_person'] span:contains('" + attendee
				+ "')", "");
		SleepUtil.sleepSmall();
		this.zClickAt(Locators.DeleteZimletContextMenu, "");
	}

	public void zRemoveLocation(String location) throws HarnessException {
		SleepUtil.sleepSmall(); // let free/busy UI draw and then we take UI
								// actions
		this.zRightClickAt("css=td[id$='_location'] span:contains('" + location
				+ "')", "");
		this.zClickAt(Locators.DeleteZimletContextMenu, "");
		this.zClickAt("css=input[id$='_location_input']", "");
		SleepUtil.sleepMedium();
	}
	
	public void zVerifyDisabledControlInProposeNewTimeUI() throws HarnessException {
		SleepUtil.sleepMedium(); // opening appt takes some time so assert fails
		ZAssert.assertTrue(this.sIsElementPresent(Locators.ToDisabled), "Verify to is disabled while attendee propose new time");
		ZAssert.assertTrue(this.sIsElementPresent(Locators.SubjectDisabled), "Verify subject is disabled while attendee propose new time");		
		ZAssert.assertTrue(this.sIsElementPresent(Locators.AttendeesDisabled), "Verify attendees is disabled while attendee propose new time");
		ZAssert.assertTrue(this.sIsElementPresent(Locators.OptionalDisabled), "Verify optional is disabled while attendee propose new time");
		ZAssert.assertTrue(this.sIsElementPresent(Locators.LocationDisabled), "Verify location is disabled while attendee propose new time");
		ZAssert.assertTrue(this.sIsElementPresent(Locators.EquipmentDisabled), "Verify equipment is disabled while attendee propose new time");
		ZAssert.assertTrue(this.sIsElementPresent(Locators.DisplayDisabled), "Verify display is disabled while attendee propose new time");
		ZAssert.assertTrue(this.sIsElementPresent(Locators.FolderDisabled), "Verify folder is disabled while attendee propose new time");
		ZAssert.assertTrue(this.sIsElementPresent(Locators.PrivateDisabled), "Verify private is disabled while attendee propose new time");
	}
	
	public void zVerifyDisabledControlInOpenInstance() throws HarnessException {
		ZAssert.assertTrue(this.sIsElementPresent(Locators.RepeatOptionsDisabled), "Verify repeat dropdown remains disabled");
		ZAssert.assertTrue(this.sIsElementPresent(Locators.RepeatDescriptionDisabled), "Verify repeat description remains disabled");
	}
	
	/**
	 * Press the toolbar button
	 * 
	 * @param button
	 * @return
	 * @throws HarnessException
	 */

	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		SleepUtil.sleepSmall();

		logger.info(myPageName() + " zToolbarPressButton(" + button + ")");

		tracer.trace("Click button " + button);

		if (button == null)
			throw new HarnessException("Button cannot be null!");

		// Fallthrough objects
		AbsPage page = null;
		String locator = null;

		if (button == Button.B_SEND) {

			locator = Locators.Button_Send;

			// Click on send
			this.zClick(locator);

			this.zWaitForBusyOverlay();

			// Wait for the message to be delivered
			Stafpostqueue sp = new Stafpostqueue();
			sp.waitForPostqueue();
			
			SleepUtil.sleepMedium();

			return (page);

		} else if (button == Button.B_SAVE) {

			locator = Locators.Button_Save;
			page = null;

			// FALL THROUGH
		} else if (button == Button.B_SAVEANDCLOSE) {

			locator = Locators.Button_SaveAndClose;
			page = null;

			// FALL THROUGH

		} else if (button == Button.B_CLOSE) {

			locator = Locators.Button_Close;
			page = null;

			// FALL THROUGH

		} else if (button == Button.B_SUGGESTATIME) {

			locator = Locators.SuggestAtimeLink;
			SleepUtil.sleepMedium();
			page = null;

			// FALL THROUGH

		} else if (button == Button.B_10AM) {

			SleepUtil.sleepMedium();
			locator = Locators.SuggestATime10AM;
			
			if (this.sIsElementPresent(locator) == false) {
				this.sClickAt(Locators.ShowTimesAnywayLink, "");
				return null;
			}

			// FALL THROUGH

		} else if (button == Button.B_SUGGESTALOCATION) {

			locator = Locators.SuggestALocationLink;
			SleepUtil.sleepMedium();
			page = null;

			// FALL THROUGH

		} else if (button == Button.B_SELECT_FIRST_FREE_TIME_FROM_SUGGEST_PANE) {
			
			SleepUtil.sleepMedium();
			locator = Locators.SelectFirstFreeTimeFromSuggestTimePane;
			
			if (this.sIsElementPresent(locator) == false) {
				this.sClickAt(Locators.ShowTimesAnywayLink, "");
				return null;
			
			}
			
			page = null;

		} else if (button == Button.B_SHOW) {

			locator = Locators.ShowSchedulerLink;
			page = null;

			this.sClick(locator);

			this.zWaitForBusyOverlay();

			return (page);
		
		} else if (button == Button.B_SHOW_TIMES_ANYWAY) {

			locator = Locators.ShowTimesAnywayLink;
			page = null;

			if (this.sIsElementPresent(locator) == true) {
				this.sClickAt(Locators.ShowTimesAnywayLink, "");
				return null;
			}

			// FALL THROUGH

		} else if (button == Button.B_LOCATION) {

			locator = Locators.AddLocation;
			this.sClickAt(locator, "");

			this.zWaitForBusyOverlay();
			page = new DialogFindLocation(this.MyApplication, pageCal);
			return (page);

			// FALL THROUGH

		} else if (button == Button.B_EQUIPMENT) {

			locator = Locators.addEquipment;
			this.sClickAt(locator, "");

			this.zWaitForBusyOverlay();
			page = new DialogFindLocation(this.MyApplication, pageCal);
			return (page);

			// FALL THROUGH

		} 
		else if (button == Button.B_TO) {

			locator = Locators.AddAttendees;
			this.sClickAt(locator, "");

			this.zWaitForBusyOverlay();
			page = new DialogFindLocation(this.MyApplication, pageCal);
			return (page);

			// FALL THROUGH

		}else if (button == Button.B_OPTIONAL) {

			locator = Locators.AddOptiponalAttendees;
			this.sClickAt(locator, "");

			this.zWaitForBusyOverlay();
			page = new DialogFindLocation(this.MyApplication, pageCal);
			return (page);

			// FALL THROUGH

		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		// Make sure a locator was set
		if (locator == null)
			throw new HarnessException("locator was null for button " + button);

		// Default behavior, process the locator by clicking on it
		//

		// Click it
		this.sClickAt(locator, "");

		// if the app is busy, wait for it to become active again
		this.zWaitForBusyOverlay();

		if (page != null) {

			// Make sure the page becomes active
			page.zWaitForActive();

		}

		// Return the page, if specified
		SleepUtil.sleepSmall();
		return (page);

	}

	public AbsPage zPressButton(Button button, String value)
			throws HarnessException {
		logger.info(myPageName() + " zPressButton(" + button + ")");
		SleepUtil.sleepMedium();

		tracer.trace("Click button " + button);

		if (button == null)
			throw new HarnessException("Button cannot be null!");

		// Fallthrough objects
		AbsPage page = null;
		String locator = null;

		if (button == Button.B_SUGGESTEDLOCATION) {

			locator = zGetSuggestedLocation(value);
			page = null;

		} else if (button == Button.B_LOCATIONMENU) {

			locator = zGetLocationVaueFromPopUp(value);
			page = null;

			// FALL THROUGH

		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (locator == null)
			throw new HarnessException("locator was null for button " + button);

		this.sClickAt(locator, "");
		SleepUtil.sleepMedium(); // Let location bubble gets ready and adds
									// value in field

		this.zWaitForBusyOverlay();

		if (page != null) {
			page.zWaitForActive();

		}

		return (page);

	}

	public void zVerifySpecificTimeNotExists(String time)
			throws HarnessException {
		PageCalendar pageCal = new PageCalendar(MyApplication);
		String[] timeArray = time.split(",");
		for (int i = 0; i <= timeArray.length - 1; i++) {
			ZAssert
					.assertEquals(
							false,
							pageCal
									.sIsElementPresent("css=div[id$='_suggest_view'] td:contains('"+ timeArray[i] + "')"),
							"Verify busy timeslots are not showing while suggesting a time");
		}
	}

	public void zVerifySpecificTimeExists(String time) throws HarnessException {
		PageCalendar pageCal = new PageCalendar(MyApplication);
		String[] timeArray = time.split(",");
		for (int i = 0; i <= timeArray.length - 1; i++) {
			ZAssert
					.assertEquals(
							true,
							pageCal
									.sIsElementPresent("css=div[id$='_suggest_view'] td:contains('" + timeArray[i] + "')"),
							"Verify free timeslots are showing while suggesting a time");
		}
	}

	public void zVerifySpecificLocationNotExists(String location)
			throws HarnessException {
		PageCalendar pageCal = new PageCalendar(MyApplication);
		String[] locationArray = location.split(",");
		for (int i = 0; i <= locationArray.length - 1; i++) {
			ZAssert
					.assertEquals(
							false,
							pageCal
									.sIsElementPresent("css=div[id$='_suggest_view'] td:contains('"+ locationArray[i] + "')"),
							"Verify busy timeslot are not showing while suggesting a time");
		}
	}
	
	public void zSetTomorrowDate() throws HarnessException{
	    Calendar c = Calendar.getInstance();
	    c.add(Calendar.DATE, 2);
	    this.zTypeKeys("css=input[id$='_startDateField']", new SimpleDateFormat("MM/dd/yyyy").format(c.getTime()));
	}
	
	public String zGetTomorrowDate() throws HarnessException{
		Calendar c = Calendar.getInstance();
	    c.add(Calendar.DATE, 2);	    
	    return new SimpleDateFormat("yyyyMMdd").format(c.getTime());
	}

	/**
	 * Press the toolbar pulldown and the menu option
	 * 
	 * @param pulldown
	 * @param option
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		
		logger.info(myPageName() + " zToolbarPressPulldown(" + pulldown + ", "
				+ option + ")");

		tracer.trace("Click pulldown " + pulldown + " then " + option);

		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (option == null)
			throw new HarnessException("Option cannot be null!");

		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if (pulldown == Button.B_OPTIONS) {
			pulldownLocator = Locators.OptionsDropdown;

		} else {
			throw new HarnessException("no logic defined for pulldown "
					+ pulldown);
		}
		
		if (option == Button.O_SUGGESTION_PREFERENCES) {
			optionLocator = Locators.SuggestionPreferencesMenu;

		} else {
			throw new HarnessException("no logic defined for option "
					+ pulldown);
		}

		// Default behavior
		if (pulldownLocator != null) {

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown + " option "
						+ option + " pulldownLocator " + pulldownLocator
						+ " not present!");
			}

			this.sClickAt(pulldownLocator, "");

			this.zWaitForBusyOverlay();

			if (optionLocator != null) {
				
				if (option == Button.O_SUGGESTION_PREFERENCES) {
					optionLocator = Locators.SuggestionPreferencesMenu;

				} else {
					throw new HarnessException("no logic defined for pulldown "
							+ pulldown);
				}

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("Button " + pulldown
							+ " option " + option + " optionLocator "
							+ optionLocator + " not present!");
				}

				this.sClickAt(optionLocator, "");

				this.zWaitForBusyOverlay();

			}

			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if (page != null) {
				page.zWaitForActive();
			}

		}

		// Return the specified page, or null if not set
		return (page);
	}

	public void zFillField(Field field, ZDate value) throws HarnessException {
		String stringFormat;

		if (field == Field.StartDate || field == Field.EndDate) {
			// TODO: need INTL
			stringFormat = value.toMM_DD_YYYY();
		} else if (field == Field.StartTime || field == Field.EndTime) {
			// TODO: need INTL
			stringFormat = value.tohh_mm_aa();
		} else {
			throw new HarnessException(
					"zFillField() not implemented for field: " + field);
		}

		zFillField(field, stringFormat);
	}

	public void zFillField(Field field) throws HarnessException {

		tracer.trace("Set " + field);

		String locator = null;

		// all day
		if (field == Field.AllDay) {

			locator = "css=input[id$='_allDayCheckbox']";

			// repeat
		} else if (field == Field.Private) {

			locator = "css=input[id$='_privateCheckbox']";

		} else {
			throw new HarnessException("not implemented for field " + field);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for field " + field);
		}

		// Make sure the element exists
		if (!this.sIsElementPresent(locator))
			throw new HarnessException("Field is not present field=" + field
					+ " locator=" + locator);

		this.sClick(locator);

		this.zWaitForBusyOverlay();

	}

	/**
	 * Fill in the form field with the specified text
	 * 
	 * @param field
	 * @param value
	 * @throws HarnessException
	 */
	public void zFillField(Field field, String value) throws HarnessException {

		tracer.trace("Set " + field + " to " + value);

		String locator = null;
		String isRepeat = null;

		// subject
		if (field == Field.Subject) {

			locator = "css=div[id^='APPT_COMPOSE_'] td[id$='_subject'] input";

			// attendees
		} else if (field == Field.Attendees) {

			locator = "css=input[id$='_person_input']";

			// optional
		} else if (field == Field.Optional) {

			locator = "css=input[id$='_optional_input']";

			// location
		} else if (field == Field.Location) {

			locator = "css=input[id$='_location_input']";

			// equipment
		} else if (field == Field.Equipment) {

			locator = "css=input[id$='_resourcesData_input']";

			// start date
		} else if (field == Field.StartDate) {

			locator = "css=input[id$='_startDateField']";

			// start time
		} else if (field == Field.StartTime) {

			locator = "css=td[id$='_startTimeSelect'] td[id$='_timeSelectInput'] input";

			// end date
		} else if (field == Field.EndDate) {

			locator = "css=input[id$='_endDateField']";

			// end time
		} else if (field == Field.EndTime) {

			locator = "css=td[id$='_endTimeSelect'] td[id$='_timeSelectInput'] input";

			// display
		} else if (field == Field.Display) {

			locator = "css=td[id$='_showAsSelect'] input";

			// calendar folder
		} else if (field == Field.CalendarFolder) {

			locator = "css=td[id$='_folderSelect'] td[id$='_select_container']";
			this.sClickAt(locator, "");

			value = "css=div[id*='_Menu_'] td[id$='_title']:contains('" + value
					+ "')";
			this.sClickAt(value, "");

			return;

			// repeat
		} else if (field == Field.Repeat) {

			isRepeat = value;
			locator = "css=div[id$='_repeatSelect'] td[id$='_dropdown']";

			// body
		} else if (field == Field.Body) {

			int frames = this.sGetCssCount("css=iframe");
			logger.info("Body: # of frames: " + frames);
			String browser = SeleniumService.getInstance().getSeleniumBrowser();

			if (browser.contains("iexplore")) {
				if (frames == 1) {
					// //
					// Text compose
					// //

					locator = "css=textarea[id*=_content]";

					if (!this.sIsElementPresent(locator))
						throw new HarnessException(
								"Unable to locate compose body");
					if (ZimbraSeleniumProperties.isWebDriver()) {
						this.sClickAt(locator, "");
						this.clearField(locator);
						this.sClickAt(locator, "");
					}
					this.sFocus(locator);
					this.zClick(locator);
					this.zWaitForBusyOverlay();
					this.sType(locator, value);

					return;

				} else if (frames == 2) {

					locator = "css=iframe[id$='_content_ifr']";
					if (!this.sIsElementPresent(locator))
						throw new HarnessException(
								"Unable to locate compose body");

					zTypeFormattedText(locator, value);

					// Is this requried?
					this.zWaitForBusyOverlay();

					return;

				}

			} else {
				// If plain text editor present then there is no need to count
				// iframes. Also there is a a bug in iframe counting if single test
				// logouts multiple time for e.g. run 2 Accept propose new time tests
				if (this
						.sIsElementPresent("css=textarea[class='DwtHtmlEditorTextArea']") && frames == 1) {
					locator = "css=textarea[class='DwtHtmlEditorTextArea']";
					
					this.sFocus(locator);
					this.zClick(locator);
					this.zWaitForBusyOverlay();
					this.sType(locator, value);

					return;
				}

				if (frames == 0) {
					// Text compose

					locator = "css=textarea[class='DwtHtmlEditorTextArea']";

					if (!this.sIsElementPresent(locator))
						throw new HarnessException(
								"Unable to locate compose body");

					this.sFocus(locator);
					this.zClick(locator);
					this.zWaitForBusyOverlay();
					this.sType(locator, value);

					return;

				} else if (frames == 1) {
					// HTML compose

					try {

						//this.sSelectFrame("css=iframe[id$='_content_ifr']");

						//locator = "css=body[id='tinymce']";

						locator = "css=iframe[id$='_content_ifr']";

						if (!this.sIsElementPresent(locator))
							throw new HarnessException(
									"Unable to locate compose body");

						this.sFocus(locator);
						this.zClick(locator);

						/*
						 * Oct 25, 2011: The new TinyMCE editor broke sType().
						 * Use zKeyboard instead, however, it is preferred to
						 * use sType() if possible, but I can't find a solution
						 * right now.
						 */
						// this.sType(locator, value);
						//this.zKeyboard.zTypeCharacters(value);
						
						zTypeFormattedText(locator, value);

					} finally {
						// Make sure to go back to the original iframe
						this.sSelectFrame("relative=top");

					}

					// Is this requried?
					this.zWaitForBusyOverlay();

					return;

				} else if (frames == 2) {
					// HTML compose

					try {

						if (this.sIsElementPresent("css=iframe[id$='_content_ifr']")) {
							locator = "css=html body";
							this.sSelectFrame("css=iframe[id$='_content_ifr']"); // iframe index is 0 based
							this.sFocus(locator);
							this.zClick(locator);
							//this.sType(locator, value);
							this.zKeyboard.zTypeCharacters(value);

						} else if (this.sIsElementPresent("css=textarea[class='DwtHtmlEditorTextArea']")) {	
							locator = "css=textarea[class='DwtHtmlEditorTextArea']";
							this.sFocus(locator);
							this.zClick(locator);
							this.sType(locator, value);
						
						} else {
							throw new HarnessException("Unable to locate compose body");
						}

					} finally {
						
						// Make sure to go back to the original iframe
						this.sSelectFrame("relative=top");

					}

					// Is this requried?
					this.zWaitForBusyOverlay();

					return;

				} else {
					throw new HarnessException("Compose //iframe count was "
							+ frames);
				}
			}

		} else {
			throw new HarnessException("not implemented for field " + field);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for field " + field);
		}

		// Default behavior, enter value into locator field
		//

		// Make sure the button exists
		if (!this.sIsElementPresent(locator))
			throw new HarnessException("Field is not present field=" + field
					+ " locator=" + locator);

		if (isRepeat != null) {
			this.sClickAt(locator, "");
			zRecurringOptions(locator, value, isRepeat);
		} else {
			if (ZimbraSeleniumProperties.isWebDriver()) {
				this.sClickAt(locator, "");
				this.clearField(locator);
				this.sClickAt(locator, "");
			}
			this.sType(locator, value);
			SleepUtil.sleepSmall();

			if (field == Field.Attendees || field == Field.Optional
					|| field == Field.Location || field == Field.Equipment) {
				this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_TAB);
			}
		}
		this.zWaitForBusyOverlay();
	}

	@Override
	public void zFill(IItem item) throws HarnessException {
		logger.info(myPageName() + ".zFill(ZimbraItem)");
		logger.info(item.prettyPrint());

		// Make sure the item is a MailItem
		if (!(item instanceof AppointmentItem)) {
			throw new HarnessException(
					"Invalid item type - must be AppointmentItem");
		}

		AppointmentItem appt = (AppointmentItem) item;

		// Subject
		if (appt.getSubject() != null) {
			zFillField(Field.Subject, appt.getSubject());
		}

		// Attendees
		if (appt.getAttendees() != null) {
			zFillField(Field.Attendees, appt.getAttendees());
			SleepUtil.sleepSmall();
			this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_ENTER);
			this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_TAB);
		}

		// Optional
		if (appt.getOptional() != null) {
			this.sClickAt(Locators.ShowOptionalLink, "");
			zFillField(Field.Optional, appt.getOptional());
			SleepUtil.sleepSmall();
			this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_ENTER);
		}

		// Location
		if (appt.getLocation() != null) {
			zFillField(Field.Location, appt.getLocation());
			SleepUtil.sleepSmall();
			this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_ENTER);
			this.sClickAt("css= input[id$='_location_input']", "");
			this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_TAB);
			
		}

		// Equipment
		if (appt.getEquipment() != null) {
			this.sClickAt(Locators.ShowEquipmentLink, "");
			zFillField(Field.Equipment, appt.getEquipment());
			SleepUtil.sleepSmall();
			this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_ENTER);
		}

		// Start date-time
		if (appt.getStartTime() != null) {
			zFillField(Field.StartDate, appt.getStartTime());
			zFillField(Field.StartTime, appt.getStartTime());
		}

		// End date-time
		if (appt.getEndTime() != null) {
			zFillField(Field.EndDate, appt.getEndTime());
			zFillField(Field.EndTime, appt.getEndTime());
		}

		// Calendar folder
		if (appt.getFolder() != null) {
			zFillField(Field.CalendarFolder, appt.getFolder());
		}

		// Is recurring
		if (appt.getRecurring() != null) {
			zFillField(Field.Repeat, appt.getRecurring());
		}

		// Is all day
		if (appt.getIsAllDay() == true) {
			zFillField(Field.AllDay);
		}

		// Is private
		if (appt.getIsPrivate() == true) {
			String locator = "css=input[id$='_privateCheckbox']";
			 this.sCheck(locator);
			 this.zWaitForBusyOverlay();
		}
		
		if (appt.getIsPrivate() == false) {
		 String locator = "css=input[id$='_privateCheckbox']";
		 this.sUncheck(locator);
		 this.zWaitForBusyOverlay();
		}
		// Body
		if (appt.getContent() != null) {
			zFillField(Field.Body, appt.getContent());
		}

	}
	
	private void zRepeatCore(Button recurringType, Button repeat, String repeatOption1, Button end, String endOption1) throws HarnessException {
		
		logger.info(myPageName() + " zRepeat(recurringType, " + repeat + ", " + repeatOption1 + ", " + end + ", " + endOption1 + ")");
		
		if (recurringType.equals(null) || repeat.equals(null) || end.equals(null)) {
			throw new HarnessException("Repeat options can't be null!");
		}
		
		SleepUtil.sleepSmall();
		this.zClickAt(Locators.NoneButton, "");
		
		if (!recurringType.equals(Button.O_CUSTOM_MENU)) {
			
			if (recurringType.equals(Button.O_EVERY_DAY_MENU)) {
				this.sClickAt(Locators.EveryDayMenuItem, "");
				this.sClickAt(Locators.CustomizedLink,"");
				
				if (repeat.equals(Button.B_EVERY_DAY_RADIO_BUTTON)) {
					this.sClickAt(Locators.EveryDayRadioButton, "");
					
				} else if (repeat.equals(Button.B_EVERY_WEEKDAY_RADIO_BUTTON)) {
					this.sClickAt(Locators.EveryWeekdayRadioButton, "");
					
				} else if (repeat.equals(Button.B_EVERY_X_DAYS_RADIO_BUTTON)) {
					this.sClickAt(Locators.EveryXdaysRadioButton, "");
					this.sType(Locators.EveryXdaysEditField, repeatOption1);
				} 
				
			} else if (recurringType.equals(Button.O_EVERY_WEEK_MENU)) {
				this.sClickAt(Locators.EveryWeekMenuItem, "");
				this.sClickAt(Locators.CustomizedLink,"");
				
				if (repeat.equals(Button.B_EVERY_X_RADIO_BUTTON)) {
					this.sClickAt(Locators.EveryXRadioButton, "");
					if (!repeatOption1.equals("")) {
						this.sClickAt("css=td[id='WEEKLY_SELECT'] td[id$='_title']", "");
						this.sClickAt("css=div[id^='POPUP_DWT'] td[id$='_title']:contains('" + repeatOption1 + "')", "");
					}
					
				} else if (repeat.equals(Button.B_EVERY_X_WEEKS_ON_RADIO_BUTTON)) {
					this.sClickAt(Locators.EveryXweeksOnRadioButton, "");
					
				} else if (repeat.equals(Button.E_EVERY_X_WEEKS_ON_EDIT_FIELD)) {
					this.sType(Locators.EveryXweeksOnEditField, repeatOption1);
					
				} else if (repeat.equals(Button.B_SUNDAY_CHECK_BOX)) {
					this.sClickAt(Locators.SundayCheckBox, repeatOption1);
				
				} else if (repeat.equals(Button.B_MONDAY_CHECK_BOX)) {
					this.sClickAt(Locators.MondayCheckBox, repeatOption1);

				} else if (repeat.equals(Button.B_TUESDAY_CHECK_BOX)) {
					this.sClickAt(Locators.TuesdayCheckBox, repeatOption1);

				} else if (repeat.equals(Button.B_WEDNESDAY_CHECK_BOX)) {
					this.sClickAt(Locators.WednesdayCheckBox, repeatOption1);

				} else if (repeat.equals(Button.B_THURSDAY_CHECK_BOX)) {
					this.sClickAt(Locators.ThursdayCheckBox, repeatOption1);

				} else if (repeat.equals(Button.B_FRIDAY_CHECK_BOX)) {
					this.sClickAt(Locators.FridayCheckBox, repeatOption1);

				} else if (repeat.equals(Button.B_SATURDAY_CHECK_BOX)) {
					this.sClickAt(Locators.SaturdayCheckBox, repeatOption1);
					
				}
				
			} else if (recurringType.equals(Button.O_EVERY_MONTH_MENU)) {
				this.sClickAt(Locators.EveryMonthMenuItem, "");
				this.sClickAt(Locators.CustomizedLink,"");
				
				if (repeat.equals(Button.B_DAY_X_OF_EVERY_Y_MONTHS_RADIO_BUTTON)) {
					this.sClickAt(Locators.DayXofEveryYmonthsRadioButton, "");
				
				} else if (repeat.equals(Button.B_THE_X_Y_OF_EVERY_Z_MONTHS_RADIO_BUTTON)) {
					this.sClickAt(Locators.TheXYofEveryZmonthsRadioButton, "");
					this.sClickAt("css=td[id='MONTHLY_WEEKDAY_SELECT'] td[id$='_title']", "");
					this.sClickAt("css=div[id*='Menu_'] td[id$='_title']:contains('Monday')", "");        
				}
				
			} else if (recurringType.equals(Button.O_EVERY_YEAR_MENU)) {
				this.sClickAt(Locators.EveryYearMenuItem, "");
				this.sClickAt(Locators.CustomizedLink,"");
				
				if (repeat.equals(Button.B_EVERY_YEAR_ON_X_Y_RADIO_BUTTON)) {
					this.sClickAt(Locators.EveryYearOnXYRadioButton, "");
					
				} else if (repeat.equals(Button.B_THE_X_Y_OF_EVERY_Z_RADIO_BUTTON)) {
					this.sClickAt(Locators.TheXYofEveryZRadioButton, "");
				}
				
			}
		
			if (end.equals(Button.B_NO_END_DATE_RADIO_BUTTON)) {
				this.sClickAt(Locators.NoEndDateRadioButton, "");
				
			} else if (end.equals(Button.B_END_AFTER_X_OCCURRENCES_RADIO_BUTTON)) {
				this.sClickAt(Locators.EndAfterXoccurrencesRadioButton, "");
				this.sType(Locators.EndAfterXoccurrencesEditField, endOption1);
				
			} else if (end.equals(Button.B_END_BY_DATE_RADIO_BUTTON)) {
				this.sClickAt(Locators.EndByXDateRadioButton, "");
				this.sType(Locators.EndByXDateEditField, endOption1);
			}	
	
		}
		
		DialogCustomRepeat dlgCustomRepeat = (DialogCustomRepeat) new DialogCustomRepeat(DialogCustomRepeat.DialogWarningID.DialogCustomRepeat, MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
		dlgCustomRepeat.zClickButton(Button.B_OK);
		
		SleepUtil.sleepSmall();
	}
	
	public void zRepeat(Button recurringType, Button repeat, Button end) throws HarnessException {
		zRepeatCore(recurringType, repeat, "", end, "");
		
	}
	
	public void zRepeat(Button recurringType, Button repeat, String repeatOption1, Button end, String endOption1) throws HarnessException {
		zRepeatCore(recurringType, repeat, repeatOption1, end, endOption1);
		
	}
	
	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		// Look for the div
		// See https://bugzilla.zimbra.com/show_bug.cgi?id=58477
		// String locator = "css=div[id^='ztb__APPT']"; // 8.0 D2
		String locator = "css=div[id^='zb__App__tab_APPT-']"; // 8.0 D3

		if (!this.sIsElementPresent(locator)) {
			return (false);
		}

		if (!this.zIsVisiblePerPosition(locator, 150, 75)) {
			return (false);
		}

		// TODO: temporary workaround for
		// main.projects.ajax.tests.calendar.appointments.views.day.allday.
		// CreateAppointment.CreateAllDayAppointment_01
		// REF: http://server/testlogs/UBUNTU10_64/IRONMAIDEN-800/20120807000101_FOSS/SelNG-projects-ajax-tests/134432857672933/server/AJAX/firefox/en_US/debug/projects/ajax/tests/calendar/appointments/views/day/allday/CreateAppointment/CreateAllDayAppointment_01ss191.png
		// which is failing to click the save/close button.
		// It seems that the appointment compose is taking a long time to open
		// instead of waiting for an element to appear (which is the preferred
		// solution), just
		// wait 5 seconds here
		SleepUtil.sleep(5000);

		logger.info(myPageName() + " zIsActive() = true");
		return (true);
	}

	public String zGetApptSubject(String subject) throws HarnessException {
		return this.sGetText("css=td[id*='_subject']:contains('" + subject
				+ "')");
	}

	public String zGetApptAttendees(String attendee) throws HarnessException {
		return this.sGetText("css=td[id*='_person']:contains('" + attendee
				+ "')");
	}

	public String zGetApptOptional(String optional) throws HarnessException {
		return this.sGetText("css=td[id*='_optional']:contains('" + optional
				+ "')");
	}

	public String zGetApptLocation(String location) throws HarnessException {
		return this.sGetText("css=td[id*='_location']:contains('" + location
				+ "')");
	}

	public String zGetApptLocationFloating(String location)
			throws HarnessException {
		return this.sGetValue("css=input[id$='_location_input']");
	}

	public String zGetApptEquipment(String equipment) throws HarnessException {
		return this.sGetText("css=td[id*='_resourcesData']:contains('"
				+ equipment + "')");
	}

	public void zRecurringOptions(String locator, String recurringType,
			String endBy) throws HarnessException {

		if (recurringType.split(",")[0].toUpperCase().equals("NONE")) {
			this.sClickAt(Locators.NoneMenuItem, "");

		} else if (recurringType.split(",")[0].toUpperCase().equals("EVERYDAY")) {
			this.sClickAt(Locators.EveryDayMenuItem, "");

		} else if (recurringType.split(",")[0].toUpperCase()
				.equals("EVERYWEEK")) {
			this.sClickAt(Locators.EveryWeekMenuItem, "");

		} else if (recurringType.split(",")[0].toUpperCase().equals(
				"EVERYMONTH")) {
			this.sClickAt(Locators.EveryMonthMenuItem, "");

		} else if (recurringType.split(",")[0].toUpperCase()
				.equals("EVERYYEAR")) {
			this.sClickAt(Locators.EveryYearMenuItem, "");

		} else if (recurringType.split(",")[0].toUpperCase().equals("CUSTOM")) {
			this.sClickAt(Locators.CustomMenuItem, "");
		} else {
			this.sType(locator, recurringType);
		}
	}
	
	public boolean zVerifyNewApptTabClosed()throws HarnessException {
		String disappeared = sGetEval("this.browserbot.getUserWindow().document.getElementById('ztb__APPT-1').style.left");
		
		// if locator is not visible the style.left returns 10000px else returns 0 , so checked the same
		if (disappeared.equalsIgnoreCase("-10000px")){
			return true;
		}else{
		   return false;
		
		}
		
	}
	
	public void zRequestResponseOFF()throws HarnessException {
		// click at toolbar >> request response once to disable it	
		this.zClickAt(Locators.ToolbarOptions, "");
		this.zClickAt(Locators.RequestResponse, ""); //Request Response Set to OFF
		
	}
	
	public void zRequestResponseON()throws HarnessException {
		// click at toolbar >> click at request response twice to enable it		
		this.zClickAt(Locators.ToolbarOptions, "");
		this.zClickAt(Locators.RequestResponse, ""); //Request Response Set to OFF
		SleepUtil.sleepSmall();
		this.zClickAt(Locators.ToolbarOptions, "");
		this.zClickAt(Locators.RequestResponse, ""); //Request Response Set to ON
		
	}
	
	public void zCloseModifiedApptTab()throws HarnessException {
		// Close the modified appointment without saving changes
		this.zToolbarPressButton(Button.B_CLOSE);
		DialogConfirmModification confirmClose = (DialogConfirmModification) new DialogConfirmModification(this.MyApplication, pageCal);
	    confirmClose.zClickButton(Button.B_CANCEL);	
	}
	
}
