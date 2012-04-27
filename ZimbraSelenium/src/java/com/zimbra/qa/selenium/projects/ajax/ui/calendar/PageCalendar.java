package com.zimbra.qa.selenium.projects.ajax.ui.calendar;

import java.awt.event.KeyEvent;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.seleniumhq.jetty7.util.log.Log;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;


@SuppressWarnings("unused")
public class PageCalendar extends AbsTab {

	public static class Locators {
		
		// Buttons
		public static final String NewButton = "css=td#zb__CLWW__NEW_MENU_title";
		public static final String CloseButton = "css=td[id$='__CANCEL_title']:contains('Close')";
		public static final String ViewButton = "id=zb__CLD__VIEW_MENU_dropdown";

		// Menus
		public static final String ViewDayMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_DAY_VIEW']";
		public static final String ViewWorkWeekMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_WORK_WEEK_VIEW']";
		public static final String ViewWeekMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_WEEK_VIEW']";
		public static final String ViewMonthMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_MONTH_VIEW']";
		public static final String ViewListMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_CAL_LIST_VIEW']";
		public static final String ViewScheduleMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_SCHEDULE_VIEW']";
		
		public static final String OpenMenu = "id=VIEW_APPOINTMENT_title";
		public static final String PrintMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_PRINT']";
		public static final String AcceptMenu = "id=REPLY_ACCEPT_title";
		public static final String TentativeMenu = "id=REPLY_TENTATIVE_title";
		public static final String DeclineMenu = "id=REPLY_DECLINE_title";
		public static final String EditReplyMenu = "id=INVITE_REPLY_MENU_title";
		public static final String EditReplyAcceptSubMenu = "id=EDIT_REPLY_ACCEPT_title";
		public static final String EditReplyTentativeSubMenu = "id=EDIT_REPLY_TENTATIVE_title";
		public static final String EditReplyDeclineSubMenu = "id=EDIT_REPLY_DECLINE_title";
		public static final String ProposeNewTimeMenu = "id=PROPOSE_NEW_TIME_title";
		public static final String CreateACopyMenu = "id=DUPLICATE_APPT_title";
		public static final String ReplyMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_REPLY']";
		public static final String ReplyToAllMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_REPLY_ALL']";
		public static final String ForwardMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_FORWARD_APPT']";
		public static final String DeleteMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_DELETE']";
		public static final String CancelMenu = "css=div#zm__Calendar div#DELETE td[id$='_title']";
		public static final String MoveMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_MOVE']";
		public static final String TagAppointmentMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_TAG_MENU']";
		public static final String TagAppointmentNewTagSubMenu = "id=TAG_MENU|MENU|NEWTAG_title";
		public static final String TagAppointmentRemoveTagSubMenu = "id=TAG_MENU|MENU|REMOVETAG_title";
		public static final String ShowOriginalMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_SHOW_ORIG']";
		public static final String QuickCommandsMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_QUICK_COMMANDS']";
		
		public static final String InstanceMenu = "id=VIEW_APPT_INSTANCE_title";
		public static final String SeriesMenu = "id=VIEW_APPT_SERIES_title";
		public static final String OpenInstanceMenu = "id=OPEN_APPT_INSTANCE_title";
		public static final String ForwardInstanceMenu = "id=FORWARD_APPT_INSTANCE_title";
		public static final String DeleteInstanceMenu = "id=DELETE_INSTANCE_title";
		public static final String OpenSeriesMenu = "id=OPEN_APPT_SERIES_title";
		public static final String ForwardSeriesMenu = "id=FORWARD_APPT_SERIES_title";
		public static final String DeleteSeriesMenu = "id=DELETE_SERIES_title";
		
		public static final String NewAppointmentMenu = "id=NEW_APPT_title";
		public static final String NewAllDayAppointmentMenu = "id=NEW_ALLDAY_APPT_title";
		public static final String GoToTodayMenu = "id=TODAY_title";
		public static final String ViewMenu = "id=CAL_VIEW_MENU_title";
		public static final String ViewDaySubMenu = "id=DAY_VIEW_title";
		public static final String ViewWorkWeekSubMenu = "id=WORK_WEEK_VIEW_title";
		public static final String ViewWeekSubMenu = "id=WEEK_VIEW_title";
		public static final String ViewMonthSubMenu = "id=MONTH_VIEW_title";
		public static final String ViewListSubMenu = "id=CAL_LIST_VIEW_title";
		public static final String ViewScheduleSubMenu = "id=SCHEDULE_VIEW_title";

		public static final String SendCancellationButton = "id=CNF_DEL_SENDEDIT_button4_title";
		public static final String EditMessageButton = "id=CNF_DEL_SENDEDIT_button5_title";
		public static final String CancelButton_ConfirmDelete = "id=CNF_DEL_SENDEDIT_button1_title";
		
		// Radio buttons
		public static final String OpenThisInstanceRadioButton = "css=td input[id*='_defaultRadio']";
		public static final String OpenTheSeriesRadioButton = "css=td input[id$='_openSeries']";
		public static final String DeleteThisInstanceRadioButton = "css=td input[id*='_defaultRadio']";
		public static final String DeleteTheSeriesRadioButton = "css=td input[id$='_openSeries']";
		
		public static final String CalendarViewListDivID		= "zv__CLL";
		public static final String CalendarViewDayDivID			= "zv__CLD";
		public static final String CalendarViewWeekDivID		= "zv__CLW";
		public static final String CalendarViewWorkWeekDivID	= "zv__CLWW";
		public static final String CalendarViewMonthDivID		= "zv__CLM";
		public static final String CalendarViewScheduleDivID	= "zv__CLS";
		public static final String CalendarViewFreeBusyDivID	= "zv__CLFB";

		public static final String CalendarViewListCSS			= "css=div#"+ CalendarViewListDivID;
		public static final String CalendarViewDayCSS			= "css=div#"+ CalendarViewDayDivID;
		public static final String CalendarViewWeekCSS			= "css=div#"+ CalendarViewWeekDivID;
		public static final String CalendarViewWorkWeekCSS		= "css=div#"+ CalendarViewWorkWeekDivID;
		public static final String CalendarViewMonthCSS			= "css=div#"+ CalendarViewMonthDivID;
		public static final String CalendarViewScheduleCSS		= "css=div#"+ CalendarViewScheduleDivID;
		public static final String CalendarViewFreeBusyCSS		= "css=div#"+ CalendarViewFreeBusyDivID;
		
		public static final String CalendarViewDayItemCSS		= CalendarViewDayCSS + " div[id^='zli__CLD__']>table[id^='zli__CLD__']";
		public static final String CalendarViewWeekItemCSS		= CalendarViewWeekCSS + " div[id^='zli__CLW__']>table[id^='zli__CLW__']";
		public static final String CalendarViewWorkWeekItemCSS	= CalendarViewWorkWeekCSS + " div[id^='zli__CLWW__']>table[id^='zli__CLWW__']";

		// Dialog locators
		public static final String DialogDivID = "CNF_DEL_YESNO";
		public static final String DialogDivCss = "css=div[id='CNF_DEL_YESNO']";
		
	}

	public PageCalendar(AbsApplication application) {
		super(application);

		logger.info("new " + PageCalendar.class.getCanonicalName());
	}

	private String getLocatorBySubject(String subject) throws HarnessException {
		int count;
		String locator;

		// Organizer's view
		locator = "css=td.appt_name:contains('"+ subject +"')";
		count = this.sGetCssCount(locator);
		if ( count == 1 ) {
			return (locator);
		} else if ( count > 1 ) {
			for ( int i = 1; i <= count; i++ ) {
				if ( this.zIsVisiblePerPosition(locator + ":nth-child("+ i +")", 0, 0) ) {
					return (locator + ":nth-child("+ i +")");
				}
			}
		}
		
		// Attendee's view
		locator = "css=td.appt_new_name:contains('"+ subject +"')";
		count = this.sGetCssCount(locator);
		if ( count == 1 ) {
			return (locator);
		} else if ( count > 1 ) {
			for ( int i = 1; i <= count; i++ ) {
				if ( this.zIsVisiblePerPosition(locator + ":nth-of-type("+ i +")", 0, 0) ) {
					return (locator + ":nth-of-type("+ i +")");
				}
			}
		}
		
		// All day, Organizer's view
		locator = "css=td.appt_allday_name:contains('"+ subject +"')";
		count = this.sGetCssCount(locator);
		if ( count == 1 ) {
			return (locator);
		} else if ( count > 1 ) {
			for ( int i = 1; i <= count; i++ ) {
				if ( this.zIsVisiblePerPosition(locator + ":nth-of-type("+ i +")", 0, 0) ) {
					return (locator + ":nth-of-type("+ i +")");
				}
			}
		}
		
		// All day, Attendee's view
		locator = "css=td.appt_allday_new_name:contains('"+ subject +"')";
		count = this.sGetCssCount(locator);
		if ( count == 1 ) {
			return (locator);
		} else if ( count > 1 ) {
			for ( int i = 1; i <= count; i++ ) {
				if ( this.zIsVisiblePerPosition(locator + ":nth-of-type("+ i +")", 0, 0) ) {
					return (locator + ":nth-of-type("+ i +")");
				}
			}
		}
		
		throw new HarnessException("Unable to locate appointment!");
		
	}
	
	public String zGetApptLocator(String apptSubject) throws HarnessException {
		return "css=td.appt_name:contains('" + apptSubject + "')";
	}
	
	public String zGetReadOnlyApptLocator(String apptSubject) throws HarnessException {
		return "css=td.appt_new_name:contains('" + apptSubject + "')";
	}
	
	public String zGetAllDayApptLocator(String apptSubject) throws HarnessException {
		return "css=td.appt_allday_name:contains('" + apptSubject + "')";
	}
	
	public String zGetReadOnlyAllDayApptLocator(String apptSubject) throws HarnessException {
		return "css=td.appt_allday_new_name:contains('" + apptSubject + "')";
	}
	
	private AbsPage zListItemListView(Action action, String subject) throws HarnessException {
		logger.info(myPageName() + " zListItemListView("+ action +", "+ subject +")");

		// The default locator points at the subject
		String locator = "css=div[id='zl__CLL__rows'] td[id$='__su']:contains('" + subject + "')";
		AbsPage page = null;


		if ( action == Action.A_LEFTCLICK ) {

			// Left-Click on the item
			this.zClickAt(locator,"");
			this.zWaitForBusyOverlay();

			page = null;

			// FALL THROUGH

		} else if ( action == Action.A_CHECKBOX || action == Action.A_UNCHECKBOX ) {

			// Find the locator to the row
			locator = null;
			int count = this.sGetCssCount("css=div[id='zl__CLL__rows']>div");
			for (int i = 1; i <= count; i++) {

				String itemLocator = "css=div[id='zl__CLL__rows']>div:nth-of-type("+ i +")";
				String s = this.sGetText(itemLocator + " td[id$='__su']").trim();

				if ( s.contains(subject) ) {
					locator = itemLocator;
					break; // found it
				}

			}

			if ( locator == null )
				throw new HarnessException("Unable to locate row with subject: "+ subject);

			String selectLocator = locator + " div[id$='__se']";
			if ( !this.sIsElementPresent(selectLocator) )
				throw new HarnessException("Checkbox locator is not present "+ selectLocator);

			if ( action == Action.A_CHECKBOX ) {
				if ( this.sIsElementPresent(selectLocator +"[class*='ImgCheckboxChecked']"))
					throw new HarnessException("Trying to check box, but it was already checked");
			} else if ( action == Action.A_UNCHECKBOX ) {
				if ( this.sIsElementPresent(selectLocator +"[class*='ImgCheckboxUnchecked']"))
					throw new HarnessException("Trying to uncheck box, but it was already unchecked");
			}


			// Left-Click on the flag field
			this.zClick(selectLocator);

			this.zWaitForBusyOverlay();

			// No page to return
			page = null;

			// FALL THROUGH

		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}

		// Action should take place in the if/else block.
		// No need to take action on a locator at this point.

		// If a page was specified, make sure it is active
		if ( page != null ) {
			page.zWaitForActive();
		}

		return (page);
	}

	@Override
	public AbsPage zListItem(Action action, String subject) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ subject +")");
		tracer.trace(action +" on subject = "+ subject);

		if ( this.zIsVisiblePerPosition(Locators.CalendarViewListCSS, 0, 0) ) {
			return (zListItemListView(action, subject));											// LIST
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewDayCSS, 0, 0) ) {
			return (zListItemGeneral(Locators.CalendarViewDayItemCSS, action, subject));		// DAY
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewWorkWeekCSS, 0, 0) ) {
			return (zListItemGeneral(Locators.CalendarViewWorkWeekItemCSS, action, subject));	// WORKWEEK
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewWeekCSS, 0, 0) ) {
			return (zListItemGeneral(Locators.CalendarViewWeekItemCSS, action, subject));		// WEEK
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewMonthCSS, 0, 0) ) {
			return (zListItemMonthView(action, subject));											// MONTH
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewScheduleCSS, 0, 0) ) {
			return (zListItemGeneral("TODO", action, subject));								// SCHEDULE
		} else {
			throw new HarnessException("Unknown calendar view");
		}

	}
	
	private AbsPage zListItemGeneral(String itemsLocator, Action action, String subject) throws HarnessException {

		/**

		The DAY, WEEK, WORKWEEK, all use the same logic, just
		different DIV objects in the DOM.
		
		Based on the itemsLocator, which locates each individual
		appointment in the view, parse available appointments
		and return them.

		LIST, MONTH, SCHEDULE, (FREE/BUSY) use different logic.
		That processing must happen in a different method.
		
		 */

		if ( itemsLocator == null )
			throw new HarnessException("itemsLocator cannot be null");

		if ( action == null )
			throw new HarnessException("action cannot be null");

		if ( subject == null )
			throw new HarnessException("subject cannot be null");


		logger.info(myPageName() + " zListItemGeneral("+ itemsLocator +", "+ action +", "+ subject +")");
		tracer.trace(action +" on subject = "+ subject);

		
		
		// Default behavior variables
		String locator = null;
		AbsPage page = null;

		if ( this.sIsElementPresent(itemsLocator +" td.appt_name:contains('"+ subject +"')")) {
			
			// Single occurrence locator
			locator = itemsLocator +" td.appt_name:contains('"+ subject +"')";

		} else if ( this.sIsElementPresent(itemsLocator +" td[id$='appt_new_name']:contains('"+ subject +"')")) {
			
			// Recurring appointment locator (might point to any visible instance)
			locator = itemsLocator +" td[id$='appt_new_name']:contains('"+ subject +"')";
			
		} else if ( this.sIsElementPresent(itemsLocator +" td.appt_allday_name>div:contains('"+ subject +"')")) {
			
			// All day single occurrence locator
			locator = itemsLocator +" td.appt_allday_name>div:contains('"+ subject +"')";
			
		}
		
		// Make sure one of the locators found the appt
		if ( locator == null ) {
			throw new HarnessException("Unable to determine locator for appointment: "+ subject);
		}
		
		

		if ( action == Action.A_LEFTCLICK ) {
			
			this.zClickAt(locator, "");
			this.zWaitForBusyOverlay();

			page = null;
			
			// FALL THROUGH
			
		} else if ( action == Action.A_DOUBLECLICK) {
			
			this.sDoubleClick(locator);
			this.zWaitForBusyOverlay();
			
			page = new FormApptNew(this.MyApplication);
			SleepUtil.sleepMedium();

			// FALL THROUGH
			
		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}


		if ( page != null ) {
			page.zWaitForActive();
		}

		return (page);
	}

	private AbsPage zListItemMonthView(Action action, String subject) throws HarnessException {
		logger.info(myPageName() + " zListItemMonthView("+ action +", "+ subject +")");

		tracer.trace(action +" on subject = "+ subject);

		if ( action == null )
			throw new HarnessException("action cannot be null");

		if ( subject == null )
			throw new HarnessException("subject cannot be null");

		// Default behavior variables
		String locator = null;
		AbsPage page = null;

		// TODO: need some way to get a locator to all-day and non-all-day appts
		// For now, give pref to non-all-day.  If not present, try all-day
		
// 		locator = "css=td.appt_name:contains('" + subject + "')"; // non-all-day
		locator = "css=table.calendar_month_day_table td.calendar_month_day_item span[id$='_subject']:contains('"+ subject +"')"; // non-all-day
		
		if ( !this.sIsElementPresent(locator) ) {
			// locator = "css=td.appt_allday_name:contains('" + subject + "')"; // all-day
			locator = "css=td.appt_allday_name:contains('" + subject + "')"; // all-day

		}

		if ( action == Action.A_LEFTCLICK ) {
			
			this.zClickAt(locator, "");
			this.zWaitForBusyOverlay();

			page = null;
			
			return (page);
			
		} else if ( action == Action.A_RIGHTCLICK ) {
			
			this.zRightClickAt(locator, "");
			this.zWaitForBusyOverlay();

			page = null;
			
			return (page);
			
		} else if ( action == Action.A_DOUBLECLICK) {
			
			this.sDoubleClick(locator);
			this.zWaitForBusyOverlay();

			page = null; // Should probably return the read-only or organizer view of the appointment
			
			return (page);
			
		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}


	}
	
	private AbsPage zListItemListView(Action action, Button option, String subject) throws HarnessException {
		
		logger.info(myPageName() + " zListItemListView("+ action +", "+ option +", "+ subject +")");

		// The default locator points at the subject
		String itemlocator = "css=div[id='zl__CLL__rows'] td[id$='__su']:contains('" + subject + "')";
		String optionLocator = null;
		AbsPage page = null;


		if ( action == Action.A_RIGHTCLICK ) {

			// Right-Click on the item
			this.zRightClickAt(itemlocator,"");

			// Now the ContextMenu is opened
			// Click on the specified option

			if (option == Button.O_OPEN_MENU) {
				
				optionLocator = Locators.OpenMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if (option == Button.O_PRINT_MENU) {

				optionLocator = Locators.PrintMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if ( option == Button.O_ACCEPT_MENU ) {

				optionLocator = Locators.AcceptMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if ( option == Button.O_TENTATIVE_MENU ) {

				optionLocator = Locators.TentativeMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if ( option == Button.O_DECLINE_MENU ) {

				optionLocator = Locators.DeclineMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if ( option == Button.O_PROPOSE_NEW_TIME_MENU ) {

				optionLocator = Locators.ProposeNewTimeMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if ( option == Button.O_CREATE_A_COPY_MENU ) {

				optionLocator = Locators.CreateACopyMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if ( option == Button.O_REPLY ) {

				optionLocator = Locators.ReplyMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if ( option == Button.O_REPLY_TO_ALL ) {

				optionLocator = Locators.ReplyToAllMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if ( option == Button.O_FORWARD ) {

				optionLocator = Locators.ForwardMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if ( option == Button.O_DELETE ) {

				optionLocator = Locators.DeleteMenu;
				page = new DialogConfirmDeleteAppointment(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);

				// Depending on the type of appointment being deleted,
				// We may need to use a different type of page here
				// page = new DialogConfirmDeleteAttendee(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
				// page = new DialogConfirmDeleteOrganizer(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);

			} else if ( option == Button.O_MOVE ) {

				optionLocator = Locators.MoveMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if ( option == Button.O_TAG_APPOINTMENT_MENU ) {

				optionLocator = Locators.TagAppointmentMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);
				
			} else if (option == Button.O_TAG_APPOINTMENT_NEW_TAG_SUB_MENU) {
				
				optionLocator = Locators.TagAppointmentNewTagSubMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if (option == Button.O_TAG_APPOINTMENT_REMOVE_TAG_SUB_MENU) {
				
				optionLocator = Locators.TagAppointmentRemoveTagSubMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if ( option == Button.O_SHOW_ORIGINAL_MENU ) {
				
				optionLocator = Locators.ShowOriginalMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if ( option == Button.O_QUICK_COMMANDS_MENU ) {
				
				optionLocator = Locators.QuickCommandsMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);
				
			} else if (option == Button.O_INSTANCE_MENU) {
				
				optionLocator = Locators.InstanceMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if (option == Button.O_SERIES_MENU) {
				
				optionLocator = Locators.SeriesMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if (option == Button.O_OPEN_INSTANCE_MENU) {
				
				optionLocator = Locators.OpenInstanceMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if (option == Button.O_FORWARD_INSTANCE_MENU) {
				
				optionLocator = Locators.ForwardInstanceMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if (option == Button.O_DELETE_INSTANCE_MENU) {
				
				optionLocator = Locators.DeleteInstanceMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if (option == Button.O_OPEN_SERIES_MENU) {
				
				optionLocator = Locators.OpenSeriesMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if (option == Button.O_FORWARD_SERIES_MENU) {
				
				optionLocator = Locators.ForwardSeriesMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if (option == Button.O_NEW_APPOINTMENT_MENU) {
				
				optionLocator = Locators.NewAppointmentMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if (option == Button.O_NEW_ALL_DAY_APPOINTMENT_MENU) {
				
				optionLocator = Locators.NewAllDayAppointmentMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);

			} else if (option == Button.O_GO_TO_TODAY_MENU) {
				
				optionLocator = Locators.GoToTodayMenu;
				throw new HarnessException("implement action:"+ action +" option:"+ option);
				
			}
			else {
				throw new HarnessException("implement action:"+ action +" option:"+ option);
			}

			// click on the option
			this.zClickAt(optionLocator,"");

			this.zWaitForBusyOverlay();

			// FALL THROUGH


		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}

		// Action should take place in the if/else block.
		// No need to take action on a locator at this point.

		if ( page != null ) {
			page.zWaitForActive();
		}


		// Default behavior
		return (page);

	}

	@Override
	public AbsPage zListItem(Action action, Button option, String subject) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ option +", "+ subject +")");

		if ( this.zIsVisiblePerPosition(Locators.CalendarViewListCSS, 0, 0) ) {
			return (zListItemListView(action, option, subject));									// LIST
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewDayCSS, 0, 0) ) {
			return (zListItemGeneral(Locators.CalendarViewDayItemCSS, action, option, subject));	// DAY
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewWorkWeekCSS, 0, 0) ) {
			return (zListItemGeneral(Locators.CalendarViewWorkWeekItemCSS, action, option, subject));	// WORKWEEK
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewWeekCSS, 0, 0) ) {
			return (zListItemGeneral(Locators.CalendarViewWeekItemCSS, action, option, subject));	// WEEK
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewMonthCSS, 0, 0) ) {
			return (zListItemMonthView(action, option, subject));									// MONTH
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewScheduleCSS, 0, 0) ) {
			return (zListItemGeneral("TODO", action, option, subject));								// SCHEDULE
		} else {
			throw new HarnessException("Unknown calendar view");
		}

	}
	

	private AbsPage zListItemGeneral(String itemsLocator, Action action, Button option, String subject) throws HarnessException {


		if ( itemsLocator == null )
			throw new HarnessException("itemsLocator cannot be null");
		if ( action == null )
			throw new HarnessException("action cannot be null");
		if ( option == null )
			throw new HarnessException("button cannot be null");
		if ( subject == null || subject.trim().length() == 0)
			throw new HarnessException("subject cannot be null or blank");

		logger.info(myPageName() + " zListItemGeneral("+ itemsLocator +", "+ action +", "+ option +", "+ subject +")");
		tracer.trace(action +" then "+ option +" on subject = "+ subject);

		// Default behavior variables
		String locator = null;
		AbsPage page = null;
		String optionLocator = null;

		
		if ( this.sIsElementPresent(itemsLocator +" td.appt_name:contains('"+ subject +"')")) {
			
			// Single occurrence locator
			locator = itemsLocator +" td.appt_name:contains('"+ subject +"')";

		} else if ( this.sIsElementPresent(itemsLocator +" td[id$='appt_new_name']:contains('"+ subject +"')")) {
			
			// Recurring appointment locator (might point to any visible instance)
			locator = itemsLocator +" td[id$='appt_new_name']:contains('"+ subject +"')";
			
		} else if ( this.sIsElementPresent(itemsLocator +" td.appt_allday_name>div:contains('"+ subject +"')")) {
			
			// All day single occurrence locator
			locator = itemsLocator +" td.appt_allday_name>div:contains('"+ subject +"')";
			
		}
		
		// Make sure one of the locators found the appt
		if ( locator == null ) {
			throw new HarnessException("Unable to determine locator for appointment: "+ subject);
		}

		if (action == Action.A_RIGHTCLICK) {
			
			if ( (option == Button.O_DELETE) || (option == Button.O_CANCEL_MENU) ) {
				
				optionLocator = Locators.CancelMenu;

				this.zRightClickAt(locator, "");
				this.zWaitForBusyOverlay();

				this.zClickAt(optionLocator, "");
				this.zWaitForBusyOverlay();


				// Since we are not going to "wait for active", insert
				// a small delay to make sure the dialog shows up
				// before the zIsActive() method is called
				SleepUtil.sleepMedium();

				// If the organizer deletes an appointment, you get "Send Cancellation" dialog
				page = new DialogConfirmDeleteOrganizer(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
				if ( page.zIsActive() ) {
					return (page);
				}
				
				// If an attendee deletes an appointment, you get a "Confirm Delete" dialog with "Notify Organizer?"
				page = new DialogConfirmDeleteAttendee(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
				if ( page.zIsActive() ) {
					return (page);
				}

				// If an attendee deletes an appointment, you get a "Confirm Delete" dialog
				page = new DialogConfirmDeleteAppointment(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
				if ( page.zIsActive() ) {
					return (page);
				}

				page = new DialogConfirmDeleteRecurringAppointment(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
				if ( page.zIsActive() ) {
					return (page);
				}

				return (page);
				
			} else if ( option == Button.O_PRINT ) {
				
				// TODO: implement me
				locator = "TODO:implement me";
				page = null;
				
				// FALL THROUGH
				
			} else {

				throw new HarnessException("implement action:"+ action +" option:"+ option);
			}

		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}

		if ( locator == null ) {
			throw new HarnessException("Unable to determine the appointment locator");
		}
		
		this.zRightClickAt(locator, "");
		this.zWaitForBusyOverlay();
		SleepUtil.sleepSmall();
		
		if ( optionLocator != null ) {

			this.zClickAt(optionLocator, "");
			SleepUtil.sleepSmall();
			this.zWaitForBusyOverlay();

		}
		
		if ( page != null ) {
			page.zWaitForActive();
		}
		
		return (page);
	}

	private AbsPage zListItemMonthView(Action action, Button option, String subject) throws HarnessException {
		throw new HarnessException("implement me!");
	}


	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption, String subject) throws HarnessException {

		logger.info(myPageName() + " zListItem("+ action +", "+ option +", "+ subOption +", "+ subject +")");
		tracer.trace(action +" then "+ option + "," + subOption + " on item = "+ subject);

		if ( action == null )
			throw new HarnessException("action cannot be null");
		if ( option == null || subOption == null )
			throw new HarnessException("button cannot be null");
		if ( subject == null || subject.trim().length() == 0)
			throw new HarnessException("subject cannot be null or blank");

		// Default behavior variables
		String locator = null;
		AbsPage page = null;
		String optionLocator = null;
		String subOptionLocator = null;

		locator = "css=td.appt_name:contains('" + subject + "')";
		locator = this.getLocatorBySubject(subject);
		
		if (action == Action.A_RIGHTCLICK) {
			
			if (option == Button.O_SERIES_MENU) {
				
				optionLocator = "css=div#VIEW_APPT_SERIES td[id$='_title']";
				
			} else if (option == Button.O_INSTANCE_MENU) {
				
				optionLocator = "css=div#VIEW_APPT_INSTANCE td[id$='_title']";
				
			}

			this.zRightClickAt(locator, "");
			this.zWaitForBusyOverlay();
			
			this.sFocus(optionLocator);
			this.sMouseOver(optionLocator);
			this.zWaitForBusyOverlay();
			
			// Very complicated popups at this point
			// Instance and Series will have id='zm__Calendar__DWTXYZ' format.
			// Determine which is visible
//			String popupCSS = null;
//			int count = this.sGetCssCount("css=div[id^='zm__Calendar']");
//			if ( count < 1 ) {
//				throw new HarnessException("No popup ever opened!");
//			}
//			if (count == 1) {
//				popupCSS = "css=div[id^='zm__Calendar']";
//			} else {
//				for (int i = 1; i <= count; i ++) {
//					String l = "css=div[id^='zm__Calendar']:nth-child("+ i +")";
//					if ( this.sIsElementPresent(l) && this.zIsVisiblePerPosition(l, 0, 0) ) {
//						popupCSS = l;
//						break;
//					}
//				}
//			}
//			if ( popupCSS == null ) {
//				throw new HarnessException("No popup ever opened!");
//			}
			
			if ( subOption == Button.O_DELETE ) {
				subOptionLocator = "css=td#DELETE_SERIES_title";
			}

			if ( subOptionLocator == null ) {
				throw new HarnessException("implement action:"+ action +" option:"+ option +" suboption:" + subOption);
			}
			
			this.zClickAt(subOptionLocator, "");
			this.zWaitForBusyOverlay();

			// Since we are not going to "wait for active", insert
			// a small delay to make sure the dialog shows up
			// before the zIsActive() method is called
			SleepUtil.sleepMedium();


			// If the organizer deletes an appointment, you get "Send Cancellation" dialog
			page = new DialogConfirmDeleteOrganizer(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if ( page.zIsActive() ) {
				return (page);
			}
			
			// If an attendee deletes an appointment, you get a "Confirm Delete / Notify Organizer" dialog
			page = new DialogConfirmDeleteAttendee(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if ( page.zIsActive() ) {
				return (page);
			}

			// If an organizer deletes an appointment (no attendees), you get a "Confirm Delete" dialog
			page = new DialogConfirmDeleteAppointment(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if ( page.zIsActive() ) {
				return (page);
			}
			
			page = new DialogConfirmDeleteRecurringAppointment(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if ( page.zIsActive() ) {
				return (page);
			}

			// No dialog
			return (null);

		}
		
		if ( locator == null || optionLocator == null || subOptionLocator == null ) {
			throw new HarnessException("implement action:"+ action +" option:"+ option +" suboption:" + subOption);
		}


		
		// TODO: implement me
		this.zWaitForBusyOverlay();

		if ( page != null ) {
			page.zWaitForActive();
		}

		return (page);

	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton(" + button + ")");

		tracer.trace("Press the " + button + " button");

		if (button == null)
			throw new HarnessException("Button cannot be null!");

		// Default behavior variables
		//
		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if (button == Button.B_REFRESH) {
			
			return (((AppAjaxClient)this.MyApplication).zPageMain.zToolbarPressButton(Button.B_REFRESH));

		} else if (button == Button.B_NEW) {

			// New button
			// 7.X version: locator = "css=div[id^='ztb__CLD'] td[id$='zb__CLD__NEW_MENU_title']";
			// 8.X version: locator = "css=td#zb__NEW_MENU_title"
			locator = "css=td#zb__NEW_MENU_title";

			// Create the page
			page = new FormApptNew(this.MyApplication);
			// FALL THROUGH
		
		} else if (button == Button.B_CLOSE) {
			locator = Locators.CloseButton;
			page = null;

		} else if (button == Button.B_DELETE) {

			locator = "css=td[id='zb__CLD__DELETE_title']";
			this.zClickAt(locator, "");
			this.zWaitForBusyOverlay();


			// Since we are not going to "wait for active", insert
			// a small delay to make sure the dialog shows up
			// before the zIsActive() method is called
			SleepUtil.sleepMedium();


			// If the organizer deletes an appointment, you get "Send Cancellation" dialog
			page = new DialogConfirmDeleteOrganizer(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if ( page.zIsActive() ) {
				return (page);
			}
			
			// If an attendee deletes an appointment, you get a "Confirm Delete / Notify Organizer" dialog
			page = new DialogConfirmDeleteAttendee(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if ( page.zIsActive() ) {
				return (page);
			}

			// If an organizer deletes an appointment (no attendees), you get a "Confirm Delete" dialog
			page = new DialogConfirmDeleteAppointment(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if ( page.zIsActive() ) {
				return (page);
			}
			
			page = new DialogConfirmDeleteRecurringAppointment(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if ( page.zIsActive() ) {
				return (page);
			}

			// No dialog
			return (null);

		} else if (button == Button.O_LISTVIEW_DAY) {

			locator = "css=div[id='ztb__CLD'] div[id='zb__CLD__DAY_VIEW'] td[id$='_title']";
			page = null;

		} else if (button == Button.O_LISTVIEW_WEEK) {

			locator = "css=div[id='ztb__CLD'] div[id='zb__CLD__WEEK_VIEW'] td[id$='_title']";
			page = null;

		} else if (button == Button.O_LISTVIEW_WORKWEEK) {

			locator = "css=div[id='ztb__CLD'] div[id='zb__CLD__WORK_WEEK_VIEW'] td[id$='_title']";
			page = null;

		} else if (button == Button.O_LISTVIEW_LIST) {

			locator = "css=div[id='ztb__CLD'] div[id='zb__CLD__CAL_LIST_VIEW'] td[id$='_title']";
			page = null;

		} else if (button == Button.O_LISTVIEW_MONTH) {

			locator = "css=div[id='ztb__CLD'] div[id='zb__CLD__MONTH_VIEW'] td[id$='_title']";
			page = null;

		} else if (button == Button.O_LISTVIEW_FREEBUSY) {

			locator = "css=div[id='ztb__CLD'] div[id='zb__CLD__FB_VIEW'] td[id$='_title']";
			page = null;

		} else if (button == Button.B_OPEN_THE_SERIES) {
			
			locator = Locators.OpenTheSeriesRadioButton;
			page = null;
			
		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for button " + button);
		}

		// Default behavior, process the locator by clicking on it
		//
		this.zClickAt(locator, "");

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		// If page was specified, make sure it is active
		if (page != null) {

			// This function (default) throws an exception if never active
			page.zWaitForActive();

		}

		return (page);
	}

	public AbsPage zKeyboardKeyEvent(int keyEvent) throws HarnessException {
		AbsPage page = null;

		if ( keyEvent == KeyEvent.VK_DELETE || keyEvent == KeyEvent.VK_BACK_SPACE ) {


			this.zKeyboard.zTypeKeyEvent(keyEvent);
			this.zWaitForBusyOverlay();
			
			// Since we are not going to "wait for active", insert
			// a small delay to make sure the dialog shows up
			// before the zIsActive() method is called
			SleepUtil.sleepMedium();

			// If the organizer deletes an appointment, you get "Send Cancellation" dialog
			page = new DialogConfirmDeleteOrganizer(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if ( page.zIsActive() ) {
				return (page);
			}
			
			// If an attendee deletes an appointment, you get a "Confirm Delete" dialog with "Notify Organizer?"
			page = new DialogConfirmDeleteAttendee(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if ( page.zIsActive() ) {
				return (page);
			}

			// If an attendee deletes an appointment, you get a "Confirm Delete" dialog
			page = new DialogConfirmDeleteAppointment(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if ( page.zIsActive() ) {
				return (page);
			}

			page = new DialogConfirmDeleteRecurringAppointment(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			if ( page.zIsActive() ) {
				return (page);
			}

			return (page);


		}

		this.zKeyboard.zTypeKeyEvent(keyEvent);

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		// If a page is specified, wait for it to become active
		if ( page != null ) {
			page.zWaitForActive();	// This method throws a HarnessException if never active
		}

		return (page);
	}

	@Override
	public AbsPage zKeyboardShortcut(Shortcut shortcut) throws HarnessException {
		AbsPage page = null;

		if ( shortcut == Shortcut.S_ASSISTANT ) {

			page = new DialogAssistant(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);

		} else if ( shortcut == Shortcut.S_DELETE ) {

			page = new DialogConfirmDeleteAppointment(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);

			// Depending on the type of appointment being deleted,
			// We may need to use a different type of page here
			// page = new DialogConfirmDeleteAttendee(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			// page = new DialogConfirmDeleteOrganizer(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);

		} else if ( 
				shortcut == Shortcut.S_MAIL_MOVETOTRASH ||
				shortcut == Shortcut.S_MAIL_HARDELETE ) {

			page = new DialogConfirmDeleteAppointment(MyApplication,  ((AppAjaxClient) MyApplication).zPageCalendar);

			// Depending on the type of appointment being deleted,
			// We may need to use a different type of page here
			// page = new DialogConfirmDeleteAttendee(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);
			// page = new DialogConfirmDeleteOrganizer(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);

		} else if ( shortcut == Shortcut.S_NEWCALENDAR ) {

			page = new DialogCreateFolder(MyApplication, ((AppAjaxClient)MyApplication).zPageCalendar);

		}

		// Type the characters
		zKeyboard.zTypeCharacters(shortcut.getKeys());

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		// If a page is specified, wait for it to become active
		if ( page != null ) {
			page.zWaitForActive();	// This method throws a HarnessException if never active
		}
		return (page);

	}

	@SuppressWarnings("deprecation")
	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
	throws HarnessException {
		logger.info(myPageName() + " zToolbarPressPulldown(" + pulldown + ", "
				+ option + ")");

		tracer.trace("Click pulldown " + pulldown + " then " + option);

		if (pulldown == null)
			throw new HarnessException("Button cannot be null!");

		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if ( pulldown == Button.B_NEW ) {

			if ( option == Button.O_NEW_CALENDAR || option == Button.O_NEW_FOLDER) {

				pulldownLocator = "css=div[id='zb__NEW_MENU'] td[id$='_dropdown'] div[class='ImgSelectPullDownArrow']";
				optionLocator = "css=div[id='zb__NEW_MENU_NEW_CALENDAR'] td[id$='_title']";
				page = new DialogCreateFolder(MyApplication, ((AppAjaxClient)MyApplication).zPageCalendar);

			} else {

				throw new HarnessException("No logic defined for pulldown " + pulldown + " and option " + option);

			}

		} else if (pulldown == Button.B_LISTVIEW) {

			// In 8.0 D3, there is no pulldown for the view anymore.  There are just buttons.
			//
			// Redirect to the press button method
			//
			return (this.zToolbarPressButton(option));
			
		} else {

			throw new HarnessException("No logic defined for pulldown " + pulldown + " and option " + option);

		}

		if (pulldownLocator != null) {

			// Make sure the locator exists
			if (!sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown + " option " + option + " pulldownLocator " + pulldownLocator + " not present!");
			}

			if (ClientSessionFactory.session().currentBrowserName().contains("IE")) {
				// IE
				sClickAt(pulldownLocator, "0,0");
			} else {
				// others
				zClickAt(pulldownLocator, "0,0");
			}

			zWaitForBusyOverlay();

			if (optionLocator != null) {

				zClick(optionLocator);
				zWaitForBusyOverlay();

			}

			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if (page != null) {
				page.zWaitForActive();
			}

		}
		return page;

	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zNavigateTo() throws HarnessException {

		// Check if this page is already active.
		if (zIsActive()) {
			return;
		}

		// Make sure we are logged in
		if (!((AppAjaxClient) MyApplication).zPageMain.zIsActive()) {
			((AppAjaxClient) MyApplication).zPageMain.zNavigateTo();
		}

		tracer.trace("Navigate to " + this.myPageName());

		this.zClick(PageMain.Locators.zAppbarCal);

		this.zWaitForBusyOverlay();

		zWaitForActive();

	}

	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the main page is active
		if (!((AppAjaxClient) MyApplication).zPageMain.zIsActive()) {
			((AppAjaxClient) MyApplication).zPageMain.zNavigateTo();
		}

		/**
		 * 8.0: <div id="ztb__CLD" style="position: absolute; overflow: visible; z-index: 300; left: 179px; top: 78px; width: 1280px; height: 26px;"
		 * class="ZToolbar ZWidget" parentid="z_shell">
		 */
		// If the "folders" tree is visible, then mail is active
		String locator = "css=div#ztb__CLD";

		boolean loaded = this.sIsElementPresent(locator);
		if (!loaded)
			return (false);

		boolean active = this.zIsVisiblePerPosition(locator, 0, 0);
		if (!active)
			return (false);

		// html body div#z_shell.DwtShell div#ztb__CLD.ZToolbar
		// Made it here. The page is active.
		return (true);

	}

	private AppointmentItem parseListViewRow(String rowLocator) throws HarnessException {
		String locator;

		AppointmentItem item = new AppointmentItem();

		// Is the item checked/unchecked?
		locator = rowLocator + " div[id=$='__se'][class='ImgCheckboxChecked']";
		item.setGIsChecked(this.sIsElementPresent(locator));

		// Is the item tagged/untagged
		locator = rowLocator + " div[id=$='__tg'][class='ImgBlank_16']";
		if ( this.sIsElementPresent(locator) ) {
			// Not tagged
		} else {
			// Tagged : TODO
		}

		// Is there an attachment?
		locator = rowLocator + " div[id=$='__at'][class='ImgAttachment']";
		item.setGHasAttachment(this.sIsElementPresent(locator));

		// Get the fragment and the subject
		locator = rowLocator + " span[id$='__fm']";
		if ( this.sIsElementPresent(locator) ) {

			String fragment = this.sGetText(locator).trim();

			// Get the subject
			locator = rowLocator + " td[id$='__su']";
			String subject = this.sGetText(locator).trim();

			// The subject contains the fragment, e.g. "subject - fragment", so
			// strip it off
			item.setGFragment(fragment);
			item.setGSubject(subject.replace(fragment, "").trim());


		} else {

			// Only the subject is present
			locator = rowLocator + " td[id$='__su']";
			item.setGSubject(this.sGetText(locator).trim());

		}


		// What is the location
		locator = rowLocator + " td[id$='__lo']";
		if ( this.sIsElementPresent(locator) ) {
			String location = this.sGetText(locator).trim();
			item.setGLocation(location);
		}
		

		// What is the status
		locator = rowLocator + " span[id$='__st']";
		if ( this.sIsElementPresent(locator) ) {
			String status = this.sGetText(locator).trim();
			item.setGStatus(status);
		}

		// What calendar is it in
		// TODO

		// Is it recurring
		locator = rowLocator + " div[id=$='__re'][class='ImgApptRecur']";
		item.setGIsRecurring(this.sIsElementPresent(locator));

		// What is the start date
		locator = rowLocator + " td[id$='__dt']";
		item.setGStartDate(this.sGetText(locator));


		return (item);
	}

	private List<AppointmentItem> zListGetAppointmentsListView() throws HarnessException {
		List<AppointmentItem> items = new ArrayList<AppointmentItem>();

		String divLocator = "css=div[id='zl__CLL__rows']";
		String listLocator = divLocator +">div[id^='zli__CLL__']";
		String rowLocator = null;

		// Make sure the div exists
		if ( !this.sIsElementPresent(divLocator) ) {
			throw new HarnessException("List View Rows is not present: " + divLocator);
		}

		// If the list doesn't exist, then no items are present
		if ( !this.sIsElementPresent(listLocator) ) {
			// return an empty list
			return (items);
		}

		// How many items are in the table?
		int count = this.sGetCssCount(listLocator);
		logger.debug(myPageName() + " zListGetAppointmentsListView: number of appointments: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {

			// Add the new item to the list
			AppointmentItem item = parseListViewRow(listLocator + ":nth-of-type("+ i +")");
			items.add(item);
			logger.info(item.prettyPrint());

		}

		// Return the list of items
		return (items);

	}

	private AppointmentItem parseAppointmentRow(String rowLocator) throws HarnessException {

		/**

		The DAY, WEEK, WORKWEEK, all use the same logic, just
		different DIV objects in the DOM.
		
		Based on the itemsLocator, which locates each individual
		appointment in the view, parse available appointments
		and return them.

		LIST, MONTH, SCHEDULE, (FREE/BUSY) use different logic.
		That processing must happen in a different method.
		
		*/

		AppointmentItem item = new AppointmentItem();
		
		// Initialize the locator (but narrow to the subject field, if found later)
		item.setLocator(rowLocator);

		// Get the location
		String locator = rowLocator + " div.appt_location";
		if ( this.sIsElementPresent(locator) ) {
			item.setLocation(this.sGetText(locator).trim());
		}


		// Get the name of the appointment (organizer view)
		locator = rowLocator + " td.appt_name";
		if ( this.sIsElementPresent(locator) ) {
			
			// The name field contains both the subject and location, if there is a location
			String subject = this.sGetText(locator);
			if ( item.getLocation() == null ) {
				item.setSubject(subject.trim());
			} else {
				item.setSubject(subject.replace(item.getLocation(), "").trim());
			}
			
			item.setLocator(locator); // Update the appointment locator to point to the subject field
			
		}
		
		// Get the name of the appointment (Attendee view)
		locator = rowLocator + " td.appt_new_name";
		if ( this.sIsElementPresent(locator) ) {
			
			// The name field contains both the subject and location, if there is a location
			String subject = this.sGetText(locator);
			if ( item.getLocation() == null ) {
				item.setSubject(subject.trim());
			} else {
				item.setSubject(subject.replace(item.getLocation(), "").trim());
			}
			
			item.setLocator(locator); // Update the appointment locator to point to the subject field
			
		}
		
		// Get the name of the appointment (Attendee view)
		locator = rowLocator + " td.appt_allday_name";
		if ( this.sIsElementPresent(locator) ) {
			
			// The name field contains both the subject and location, if there is a location
			String subject = this.sGetText(locator);
			if ( item.getLocation() == null ) {
				item.setSubject(subject.trim());
			} else {
				item.setSubject(subject.replace(item.getLocation(), "").trim());
			}
			
			item.setLocator(locator); // Update the appointment locator to point to the subject field
			
		}
		
		// TODO: parse other elements

		return (item);
	}
	
	private List<AppointmentItem> zListGetAppointmentsGeneral(String itemsLocator) throws HarnessException {
		logger.info(myPageName() + " zListGetAppointmentsGeneral("+ itemsLocator +")");

		/**

			The DAY, WEEK, WORKWEEK, all use the same logic, just
			different DIV objects in the DOM.
			
			Based on the itemsLocator, which locates each individual
			appointment in the view, parse available appointments
			and return them.

			LIST, MONTH, SCHEDULE, (FREE/BUSY) use different logic.
			That processing must happen in a different method.
			
		 */
		List<AppointmentItem> items = new ArrayList<AppointmentItem>();

		// If the list doesn't exist, then no items are present
		if ( !this.sIsElementPresent(itemsLocator) ) {
			// return an empty list
			return (items);
		}

		
		String locator = null;

		// How many items are in the table?
		int count = this.sGetCssCount(itemsLocator);
		logger.debug(myPageName() + " zListGetAppointments: number of appointments: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {

			locator = itemsLocator + ":nth-of-type("+ i +")";
			
			// Add the new item to the list
			AppointmentItem item = parseAppointmentRow(locator);
			items.add(item);
			logger.info(item.prettyPrint());

		}

		// Return the list of items
		return (items);

	}

	
	

	/**
	 * @param cssLocator
	 * @return
	 * @throws HarnessException
	 */
	private AppointmentItem parseMonthViewAllDay(String cssLocator) throws HarnessException {
		logger.info(myPageName() + " parseMonthViewAllDay("+ cssLocator +")");

		/*

  <div style=
  "position: absolute; width: 119px; height: 20px; overflow: hidden; padding-bottom: 4px; left: 133.208px; top: 85px;"
  class="appt" id="zli__CLM__258_DWT557">
    <div class="appt_allday_body ZmSchedulerApptBorder-free" id=
    "zli__CLM__258_DWT557_body" style="width: 119px; height: 16px;">
      <table cellspacing="0" cellpadding="0" style=
      "table-layout: fixed; height: 100%; background: -moz-linear-gradient(center top , rgb(255, 255, 255), rgb(235, 175, 96)) repeat scroll 0% 0% transparent; opacity: 0.4;"
      id="zli__CLM__258_DWT557_tableBody">
        <tbody>
          <tr style="background:-moz-linear-gradient(top,#FFFFFF, #ebaf60);">
            <td width="4px" style=
            "background:-moz-linear-gradient(top,#FFFFFF, #FFFFFF);" class=""></td>

            <td width="100%" class="appt_allday_name">
              <div style="overflow: hidden; white-space: nowrap;">
                appointment13213151729848
              </div>
            </td>

            <td width="20px" style="padding-right:3px;" id="zli__CLM__258_DWT557_tag">
              <div style="width:16" class="ImgBlank_16"></div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
		 */
		
		
		
		String locator;
		
		AppointmentItem appt = new AppointmentItem();
		appt.setLocator(cssLocator + " table tr td");
		
		appt.setGIsAllDay(true);
		
		// Get the subject
		locator = cssLocator + " table tr td + td";
		appt.setSubject(this.sGetText(locator)); // Subject contains start time + subject
		
		// TODO: get the tags
		
		
		return (appt);
	}
	
	private AppointmentItem parseMonthViewNonAllDay(String cssLocator) throws HarnessException {
		logger.info(myPageName() + " parseMonthViewNonAllDay("+ cssLocator +")");

		/*

      <td class="calendar_month_day_item">
        <div style="position:relative;" id="zli__CLM__258_DWT304_body" class="">
          <table width="100%" style=
          "table-layout:fixed; background:-moz-linear-gradient(top,#FFFFFF, #98b6e9);"
          id="zli__CLM__258_DWT304_tableBody">
            <tbody>
              <tr>
                <td width="4px" style=
                "background:-moz-linear-gradient(top,#FFFFFF, #FFFFFF);"></td>

                <td width="100%">
                  <div style="overflow:hidden;white-space:nowrap;" id=
                  "zli__CLM__258_DWT304_st_su">
                    <span id="zli__CLM__258_DWT304_start_time">&nbsp;9:00
                    PM</span><span id=
                    "zli__CLM__258_DWT304_subject">appointment13335134710154</span>
                  </div>
                </td>

                <td width="20px" style="padding-right:3px;" id=
                "zli__CLM__258_DWT304_tag">
                  <div style="width:16" class="ImgBlank_16"></div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </td>		 */
		
		String locator;
		
		AppointmentItem appt = new AppointmentItem();
		appt.setLocator(cssLocator + " tr td + td"); // Point at the appt name


		appt.setGIsAllDay(false);
		
		// Get the subject
		locator = cssLocator + " span[id$='_subject']";
		appt.setSubject(this.sGetText(locator));
		
		// TODO: get the tags		
		
		return (appt);
	}
	
	private List<AppointmentItem> zListGetAppointmentsMonthView() throws HarnessException {
		logger.info(myPageName() + " zListGetAppointmentsMonthView()");
		
		List<AppointmentItem> items = new ArrayList<AppointmentItem>();

		String divLocator = "css=div#zv__CLM"; 
		String itemsLocator = null;
		String itemLocator = null;
		String locator = null;

		// Make sure the div exists
		if ( !this.sIsElementPresent(divLocator) ) {
			throw new HarnessException("Day View is not present: " + divLocator);
		}

		// Process the non-all-day items first

		itemsLocator = divLocator +" tr[id^='zli__CLM__']>td.calendar_month_day_item";
		if ( this.sIsElementPresent(itemsLocator) ) {

			int count = this.sGetCssCount(itemsLocator);
			logger.info(itemsLocator +" count: "+ count);

			for (int i = 1; i <= count; i++) {

				AppointmentItem item = parseMonthViewNonAllDay(itemsLocator + ":nth-of-type("+ i +")");
				items.add(item);
				logger.info(item.prettyPrint());

			}

		}
		
		
		// Process the all-day items next
		
		itemsLocator = divLocator +" div[id^='zli__CLM__']>div[id$='_body']";
		if ( this.sIsElementPresent(itemsLocator) ) {

			int count = this.sGetCssCount(itemsLocator);
			logger.info(itemsLocator +" count: "+ count);

			for (int i = 1; i <= count; i++) {

				AppointmentItem item = parseMonthViewAllDay(itemsLocator + ":nth-of-type("+ i +")");
				items.add(item);
				logger.info(item.prettyPrint());

			}

		}

		return (items);
	}

	private List<AppointmentItem> zListGetAppointmentsScheduleView() throws HarnessException {
		throw new HarnessException("implement me");
	}

	private List<AppointmentItem> zListGetAppointmentsFreeBusyView() throws HarnessException {
		logger.info(myPageName() + " zListGetAppointmentsFreeBusyView()");
		
		
		List<AppointmentItem> items = new ArrayList<AppointmentItem>();
		
		String listLocator = Locators.CalendarViewFreeBusyCSS;
		String rowLocator = listLocator + " div[id^='zli__CLFB__']";
		String itemLocator = null;

		// Process the non-all-day items first
		if ( !this.sIsElementPresent(rowLocator) )
			throw new HarnessException("List View Rows is not present "+ rowLocator);

		// How many items are in the table?
		int count = this.sGetCssCount(rowLocator);
		logger.debug(myPageName() + " zListSelectItem: number of list items: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {
			
			itemLocator = rowLocator + ":nth-child("+ i +")";

			String clazz = this.sGetAttribute(itemLocator +"@class");
			if ( clazz.contains("appt") ) {
				
				// Look for the subject
				String s = this.sGetText(itemLocator).trim();
				
				if ( (s == null) || (s.length() == 0) ) {
					continue; // No subject
				}
				
				AppointmentItem item = new AppointmentItem();
				
				// Parse the subject (which is the only data available from F/B
				item.setSubject(s);
				
				// Add the item to the returned list
				items.add(item);
				logger.info(item.prettyPrint());

			}

		}
		
		
		// Process the all-day items next (?)
		// TODO
		
		// Process the recurring items next (?)
		// TODO
		
		
		return (items);
	}

	public List<AppointmentItem> zListGetAppointments() throws HarnessException {

		if ( this.zIsVisiblePerPosition(Locators.CalendarViewListCSS, 0, 0) ) {
			return (zListGetAppointmentsListView());								// LIST
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewDayCSS, 0, 0) ) {
			return (zListGetAppointmentsGeneral(Locators.CalendarViewDayItemCSS));			// DAY
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewWeekCSS, 0, 0) ) {
			return (zListGetAppointmentsGeneral(Locators.CalendarViewWeekItemCSS));		// WEEK
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewWorkWeekCSS, 0, 0) ) {
			return (zListGetAppointmentsGeneral(Locators.CalendarViewWorkWeekItemCSS));	// WORK WEEK
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewMonthCSS, 0, 0) ) {
			return (zListGetAppointmentsMonthView());								// MONTH
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewScheduleCSS, 0, 0) ) {
			return (zListGetAppointmentsScheduleView());							// SCHEDULE
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewFreeBusyCSS, 0, 0) ) {
			return (zListGetAppointmentsFreeBusyView());							// FREE/BUSY
		} else {
			throw new HarnessException("Unknown calendar view");
		}
	}

}
