package com.zimbra.qa.selenium.projects.ajax.ui.calendar;

import java.awt.event.KeyEvent;

import com.zimbra.qa.selenium.framework.core.SeleniumService;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;

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

	/**
	 * Defines Selenium locators for various objects in {@link FormApptNew}
	 */
	public static class Locators {
		public static final String ShowOptionalLink = "css=td[id$='_show_optional']";
		public static final String ShowEquipmentLink = "css=td[id$='_show_resources']";
		public static final String CustomizeLink = "css=div[id$='repeatDesc']:contains('Customize')";
		public static final String ConfigureLink = "css=div[class='FakeAnchor']:contains('Configure')";
		public static final String SuggestAtimeLink = "css=div[id$='_suggest_time']:contains('Suggest a time')";
		public static final String SuggestALocationLink = "css=css=div[id$='_suggest_location']:contains('Suggest a location')";
		public static final String ShowSchedulerLink = "css=div[id$='_scheduleButton']:contains('Show')";
		public static final String HideSchedulerLink = "css=div[id$='_scheduleButton']:contains('Hide')";
		
		public static final String Button_Send = "css=div[id^='ztb__APPT-'] td[id$='_SEND_INVITE_title']";
		public static final String Button_Save = "css=div[id^='ztb__APPT-'] td[id$='_SAVE_title']";
		public static final String Button_SaveAndClose = "css=div[id^='ztb__APPT-'] td[id$='_SAVE_title']";
		public static final String Button_Close = "css=div[id^='ztb__APPT-'] td[id$='_CANCEL_title']";
		
		public static final String NoneMenuItem = "css=div[id*='_Menu'] div[id^='NON'] td[id$='title']:contains('None')";
		public static final String NoneButton = "css=td[id$='_title']:contains('None')";
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
		public static final String RepeatEnabled = "css=div[id$='_repeatDesc']div[class='FakeAnchor']";
		public static final String RepeatDisabled = "css=div[id$='_repeatDesc']div[class='DisabledText']";
		
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
		public static final Field Remdinder = new Field("Remdinder");
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

	}

	/**
	 * Press the toolbar button
	 * 
	 * @param button
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton(" + button + ")");

		SleepUtil.sleepMedium();
		
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

			return (page);

		} else if (button == Button.B_SAVE) {

			locator = Locators.Button_Save;
			page = null;

			// FALL THROUGH
		} else if (button == Button.B_SAVEANDCLOSE) {

			locator = Locators.Button_SaveAndClose;
			page = null;

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
		this.zClick(locator);

		// if the app is busy, wait for it to become active again
		this.zWaitForBusyOverlay();

		if (page != null) {

			// Make sure the page becomes active
			page.zWaitForActive();

		}

		// Return the page, if specified
		SleepUtil.sleepMedium();
		return (page);

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
		//
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if (pulldown == Button.B_PRIORITY) {

		} else {
			throw new HarnessException("no logic defined for pulldown "
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

			this.zClick(pulldownLocator);

			this.zWaitForBusyOverlay();

			if (optionLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("Button " + pulldown
							+ " option " + option + " optionLocator "
							+ optionLocator + " not present!");
				}

				this.zClick(optionLocator);

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
			stringFormat = value.toMM_DD_YYYY();
		} else if (field == Field.StartTime || field == Field.EndTime) {
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

			locator = "css=td[id$='_folderSelect'] input";

			
		// repeat
		} else if (field == Field.Repeat) {

			isRepeat = value;
			locator = "css=div[id$='_repeatSelect'] td[id$='_dropdown']";
			
		
		// body 
		} else if (field == Field.Body) {

			int frames = this.sGetCssCount("css=iframe");
			logger.info("Body: # of frames: " + frames);
			String browser = SeleniumService.getInstance().getSeleniumBrowser();

			if (browser.equalsIgnoreCase("iexplore")) {
				if (frames == 1) {
					// //
					// Text compose
					// //

					locator = "css=textarea[id*='textarea_']";

					if (!this.sIsElementPresent(locator))
						throw new HarnessException(
								"Unable to locate compose body");

					this.sFocus(locator);
					this.zClick(locator);
					this.zWaitForBusyOverlay();
					this.sType(locator, value);

					return;

				} else if (frames == 2) {

					locator ="css=iframe[id$='_content_ifr']";
					if (!this.sIsElementPresent(locator))
						throw new HarnessException(
								"Unable to locate compose body");

					zTypeFormattedText(locator, value);

					// Is this requried?
					this.zWaitForBusyOverlay();

					return;

				}

			} else {
				if (frames == 0) {
					// Text compose

					locator = "css=textarea[class='DwtHtmlEditorTextArea']";

					if (!this.sIsElementPresent(locator))
						throw new HarnessException("Unable to locate compose body");

					this.sFocus(locator);
					this.zClick(locator);
					this.zWaitForBusyOverlay();
					this.sType(locator, value);

					return;

				} else if (frames == 1) {
					// HTML compose

					try {

						this.sSelectFrame("css=iframe[id$='_content_ifr']");

						locator = "css=body[id='tinymce']";

						if (!this.sIsElementPresent(locator))
							throw new HarnessException("Unable to locate compose body");

						this.sFocus(locator);
						this.zClick(locator);
						
						/*
						 * Oct 25, 2011: The new TinyMCE editor broke sType().  Use zKeyboard instead,
						 * however, it is preferred to use sType() if possible, but I can't find a
						 * solution right now. 
						 */
						// this.sType(locator, value);
						this.zKeyboard.zTypeCharacters(value);

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

						this.sSelectFrame("css=iframe[id$='_content_ifr']"); // iframe index is 0 based

						locator = "css=html body";

						if (!this.sIsElementPresent(locator))
							throw new HarnessException(
									"Unable to locate compose body");

						this.sFocus(locator);
						this.zClick(locator);
						this.sType(locator, value);

					} finally {
						// Make sure to go back to the original iframe
						this.sSelectFrame("relative=top");

					}

					// Is this requried?
					this.zWaitForBusyOverlay();

					return;
					
				} else {
					throw new HarnessException("Compose //iframe count was " + frames);
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
			this.sType(locator, value);
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
			this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_ENTER);
		}
		
		// Optional
		if (appt.getOptional() != null) {
			this.sClickAt(Locators.ShowOptionalLink, "");
			zFillField(Field.Optional, appt.getOptional());
			this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_ENTER);
		}
		
		// Location
		if (appt.getLocation() != null) {
			zFillField(Field.Location, appt.getLocation());
			this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_ENTER);
		}
		
		// Equipment
		if (appt.getEquipment() != null) {
			this.sClickAt(Locators.ShowEquipmentLink, "");
			zFillField(Field.Equipment, appt.getEquipment());
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
			zFillField(Field.Private);
		}
		
		// Body
		if (appt.getContent() != null) {
			zFillField(Field.Body, appt.getContent());
		}

	}

	
	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		// Look for the div
		// See https://bugzilla.zimbra.com/show_bug.cgi?id=58477
		// String locator = "css=div[id^='ztb__APPT']";			// 8.0 D2
		String locator = "css=div[id^='zb__App__tab_APPT-']";	// 8.0 D3

		if (!this.sIsElementPresent(locator)) {
			return (false);
		}

		if (!this.zIsVisiblePerPosition(locator, 150, 75)) {
			return (false);
		}

		logger.info(myPageName() + " zIsActive() = true");
		return (true);
	}

	public String zGetApptSubject(String subject) throws HarnessException {
		return this.sGetText("css=td[id*='_subject']:contains('" + subject + "')");		
	}
	
	public String zGetApptAttendees(String attendee) throws HarnessException {
		return this.sGetText("css=td[id*='_person']:contains('" + attendee + "')");		
	}
	
	public String zGetApptOptional(String optional) throws HarnessException {
		return this.sGetText("css=td[id*='_optional']:contains('" + optional + "')");		
	}
	
	public String zGetApptLocation(String location) throws HarnessException {
		return this.sGetText("css=td[id*='_location']:contains('" + location + "')");		
	}
	
	public String zGetApptLocationFloating(String location) throws HarnessException {
		return this.sGetValue("css=input[id$='_location_input']");
	}	
	
	public String zGetApptEquipment(String equipment) throws HarnessException {
		return this.sGetText("css=td[id*='_resourcesData']:contains('" + equipment + "')");		
	}
	
	public void zRecurringOptions(String locator, String recurringType, String endBy) throws HarnessException {
		
		if (recurringType.split(",")[0].toUpperCase().equals("NONE")) {
			this.sClickAt(Locators.NoneMenuItem, "");
			
		} else if (recurringType.split(",")[0].toUpperCase().equals("EVERYDAY")) {
			this.sClickAt(Locators.EveryDayMenuItem, "");
			
		} else if (recurringType.split(",")[0].toUpperCase().equals("EVERYWEEK")) {
			this.sClickAt(Locators.EveryWeekMenuItem, "");
			
		} else if (recurringType.split(",")[0].toUpperCase().equals("EVERYMONTH")) {
			this.sClickAt(Locators.EveryMonthMenuItem, "");
			
		} else if (recurringType.split(",")[0].toUpperCase().equals("EVERYYEAR")) {
			this.sClickAt(Locators.EveryYearMenuItem, "");
			
		} else if (recurringType.split(",")[0].toUpperCase().equals("CUSTOM")) {
			this.sClickAt(Locators.CustomMenuItem, "");
		} else {
			this.sType(locator, recurringType);
		}
	}
	
}
