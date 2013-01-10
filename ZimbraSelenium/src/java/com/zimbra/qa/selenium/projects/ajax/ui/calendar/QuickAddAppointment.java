package com.zimbra.qa.selenium.projects.ajax.ui.calendar;

import java.awt.event.KeyEvent;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.seleniumhq.jetty7.util.log.Log;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.core.SeleniumService;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

@SuppressWarnings("unused")
public class QuickAddAppointment extends AbsTab {

	public static class Locators {
		
		// Quick add appointment
		public static final String SubjectFieldQuickAdd = "css=div[class='DwtDialog'] td[id$='_subject'] input";
		public static final String LocationFieldQuickAdd = "css=div[class='DwtDialog'] td[id$='_location'] input";
		public static final String DisplayDropdpwnQuickAdd = "css=div[class='DwtDialog'] td[id$='_showAs']";
		public static final String MarkAsDropdownQuickAdd = "css=div[class='DwtDialog'] td[id$='_privacy']";
		public static final String CalendarDropdownQuickAdd = "css=div[class='DwtDialog'] td[id$='_calendar']";
		public static final String StartDateFieldQuickAdd = "css=div[class='DwtDialog'] input[id$='_startDate']";
		public static final String EndDateFieldQuickAdd = "css=div[class='DwtDialog'] input[id$='_endDate']";
		public static final String StartTimeFieldQuickAdd = "css=div[class='DwtDialog'] td[id$='_startTime'] td[id$='_timeSelectInput'] input";
		public static final String EndTimeFieldQuickAdd = "css=div[class='DwtDialog'] td[id$='_endTime'] td[id$='_timeSelectInput'] input";
		public static final String RepeatDropdownQuickAdd = "css=div[class='DwtDialog'] td[id$='_repeat'] td[id$='_title']";
		public static final String ReminderDropdownQuickAdd = "css=div[class='DwtDialog'] td[id$='_reminderSelect']";
		public static final String OKButtonQuickAdd = "css=div[class='DwtDialog'] td[id$='_button2_title']:contains(" + "'OK'" + ")";
		public static final String CancelButtonQuickAdd = "css=div[class='DwtDialog'] td[id$='_button1_title']:contains(" + "'Cancel'" + ")";
		public static final String MoreDetailsButtonQuickAdd = "css=div[class='DwtDialog'] div[id$='_buttons'] td[id^='More Details..._DWT'] td[id$='_title']";
		
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
		
		public static final String QuickAddDialog = "css=div[class='DwtDialog'] td[class='DwtDialogTitle']:contains('QuickAdd Appointment')";
		
	}
	
	public static class Field {

		public static final Field Subject = new Field("Subject");
		public static final Field Location = new Field("Location");
		public static final Field Display = new Field("Display");
		public static final Field MarkAs = new Field("MarkAs");
		public static final Field Calendar = new Field("Calendar");
		public static final Field StartDate = new Field("StartDate");
		public static final Field StartTime = new Field("StartTime");
		public static final Field EndDate = new Field("EndDate");
		public static final Field EndTime = new Field("EndTime");
		public static final Field Repeat = new Field("Repeat");		
		public static final Field Reminder = new Field("Reminder");

		private String field;

		private Field(String name) {
			field = name;
		}

		@Override
		public String toString() {
			return (field);
		}

	}
	
	public QuickAddAppointment(AbsApplication application) {
		super(application);

		logger.info("new " + QuickAddAppointment.class.getCanonicalName());
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

			locator = Locators.SubjectFieldQuickAdd;

		
		// location
		} else if (field == Field.Location) {

			locator = Locators.LocationFieldQuickAdd;
			
		
		// start date
		} else if (field == Field.StartDate) {

			locator = Locators.StartDateFieldQuickAdd;


		// start time
		} else if (field == Field.StartTime) {

			locator = Locators.StartTimeFieldQuickAdd;

			
		// end date
		} else if (field == Field.EndDate) {

			locator = Locators.EndDateFieldQuickAdd;


		// end time
		} else if (field == Field.EndTime) {

			locator = Locators.EndTimeFieldQuickAdd;

			
		// display
		} else if (field == Field.Display) {

			locator = Locators.DisplayDropdpwnQuickAdd;
			
			
		// calendar folder 
		} else if (field == Field.Calendar) {

			locator = Locators.CalendarDropdownQuickAdd;
			this.sClickAt(locator, "");
			
			value = "css=div[id*='_Menu_'] td[id$='_title']:contains('" + value + "')";
			this.sClickAt(value, "");			
			
			return;

			
		// repeat
		} else if (field == Field.Repeat) {

			isRepeat = value;
			locator = Locators.RepeatDropdownQuickAdd;

		} else {
			throw new HarnessException("not implemented for field " + field);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for field " + field);
		}

		// Make sure the button exists
		if (!this.sIsElementPresent(locator))
			throw new HarnessException("Field is not present field=" + field
					+ " locator=" + locator);
	
		if (isRepeat != null) {
			this.sClickAt(locator, "");
			zRecurringOptions(locator, value, isRepeat);
		} else {
		    if(ZimbraSeleniumProperties.isWebDriver()){
			this.clearField(locator);
		    }
		    this.sType(locator, value);
		}
		this.zWaitForBusyOverlay();

	}
	
	public void zNewAppointment() throws HarnessException {
		this.zRightClickAt("css=div[class='calendar_hour_scroll'] td[class='calendar_grid_body_time_td'] div[id$='_10']", "");
		SleepUtil.sleepSmall();
		this.zClickAt("css=div[id^='POPUP_'] td[id='NEW_APPT_title']", "");
	}
	
	public void zNewAppointmentMonthView(Action action) throws HarnessException {
		if (action.equals(Action.A_DOUBLECLICK)) {
			this.sDoubleClick("css=td[class='calendar_month_cells_td-Selected']");
		} else if (action.equals(Action.A_RIGHTCLICK)) {
			this.zRightClickAt("css=td[class='calendar_month_cells_td-Selected']", "");
			SleepUtil.sleepSmall();
			this.zClickAt("css=div[id^='POPUP_'] td[id='NEW_APPT_title']", "");
		}	
		SleepUtil.sleepSmall();
	}
	
	public void zNewAppointmentUsingMiniCal() throws HarnessException {
		zWaitForMiniCalToLoad();
		this.zRightClickAt("css=td[class^='DwtCalendarDay']:contains('15')", "");
		SleepUtil.sleepSmall();
		this.zClickAt("css=div[id^='POPUP_'] td[id='NEW_APPT_title']", "");
	}
	
	public void zNewAllDayAppointment() throws HarnessException {
		this.zRightClickAt("css=div[class='calendar_hour_scroll'] td[class='calendar_grid_body_time_td'] div[id$='_10']", "");
		SleepUtil.sleepSmall();
		this.zClickAt("css=div[id^='POPUP_'] td[id='NEW_ALLDAY_APPT_title']", "");
	}
	
	public void zNewAllDayAppointmentUsingMiniCal() throws HarnessException {
		zWaitForMiniCalToLoad();
		this.zRightClickAt("css=td[class^='DwtCalendarDay']:contains('15')", "");
		SleepUtil.sleepSmall();
		this.zClickAt("css=div[id^='POPUP_'] td[id='NEW_ALLDAY_APPT_title']", "");
	}
	
	public void zVerifyQuickAddDialog(Boolean status) throws HarnessException {
		ZAssert.assertEquals(this.sIsElementPresent(Locators.QuickAddDialog), status, "Verify quick add appt dialog status");
	}
	
	public void zWaitForMiniCalToLoad() throws HarnessException {
		Boolean isElementPresent = this.sIsElementPresent("css=td[class='DwtCalendarTitlebar']");
		while (isElementPresent == false) {
			SleepUtil.sleepSmall();
		}
	}
	
	public void zMoreDetails() throws HarnessException {
		this.zClickAt(Locators.MoreDetailsButtonQuickAdd, "");
		SleepUtil.sleepSmall();
	}
	
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
		
		// Location
		if (appt.getLocation() != null) {
			zFillField(Field.Location, appt.getLocation());
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

		// Calendar
		if (appt.getFolder() != null) {
			zFillField(Field.Calendar, appt.getFolder());
		}
		
		// Is recurring
		if (appt.getRecurring() != null) {
			zFillField(Field.Repeat, appt.getRecurring());
		}

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
	
	public void zSubmit() throws HarnessException {
		logger.info("PageCalendar.submit()");	
		this.zClickAt(Locators.OKButtonQuickAdd, "");
		SleepUtil.sleepMedium();
	}

	@Override
	public AbsPage zListItem(Action action, String item)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption,
			String item) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void zNavigateTo() throws HarnessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String myPageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// TODO Auto-generated method stub
		return false;
	}
	
}
