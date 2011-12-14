package com.zimbra.qa.selenium.projects.ajax.ui.calendar;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.ContextMenuItem.CONTEXT_MENU_ITEM_NAME;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogRedirect;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.TreeMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.PageMail.PageMailView;

@SuppressWarnings("unused")
public class PageCalendar extends AbsTab {

	public static class Locators {
		
		// Buttons
		public static final String NewButton = "css=td#zb__CLWW__NEW_MENU_title";
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
		public static final String CancelMenu = "css=div[id='zm__Calendar'] tr[id='POPUP_DELETE']";
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

		// Radio buttons
		public static final String OpenThisInstanceRadioButton = "id=";
		public static final String OpenThisSeriesRadioButton = "id=";

		public static final String CalendarViewListCSS		= "css=div[id='zv__CLL']";
		public static final String CalendarViewDayCSS		= "css=div[id='zv__CLD']";
		public static final String CalendarViewWorkWeekCSS	= "css=div[id='zv__CLWW']";
		public static final String CalendarViewWeekCSS		= "css=div[id='zv__CLW']";
		public static final String CalendarViewMonthCSS		= "css=div[id='zv__CLM']";
		public static final String CalendarViewScheduleCSS	= "css=div[id='zv__CLS']";

	}

	public PageCalendar(AbsApplication application) {
		super(application);

		logger.info("new " + PageCalendar.class.getCanonicalName());
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

		if ( this.zIsVisiblePerPosition(Locators.CalendarViewListCSS, 0, 0) ) {
			return (zListItemListView(action, subject));
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewDayCSS, 0, 0) ) {
			return (zListItemWorkWeekView(action, subject));
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewWorkWeekCSS, 0, 0) ) {
			return (zListItemWorkWeekView(action, subject));
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewWeekCSS, 0, 0) ) {
			return (zListItemWorkWeekView(action, subject));
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewMonthCSS, 0, 0) ) {
			return (zListItemMonthView(action, subject));
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewScheduleCSS, 0, 0) ) {
			return (zListItemWorkWeekView(action, subject));
		} else {
			throw new HarnessException("Unknown calendar view");
		}

	}
	
	public AbsPage zListItemAllDay(Action action, String subject) throws HarnessException {

		if ( this.zIsVisiblePerPosition(Locators.CalendarViewListCSS, 0, 0) ) {
			return (zListItemListView(action, subject));
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewDayCSS, 0, 0) ) {
			return (zListItemWorkWeekViewAllDay(action, subject));
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewWorkWeekCSS, 0, 0) ) {
			return (zListItemWorkWeekViewAllDay(action, subject));
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewWeekCSS, 0, 0) ) {
			return (zListItemWorkWeekViewAllDay(action, subject));
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewMonthCSS, 0, 0) ) {
			return (zListItemMonthView(action, subject));
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewScheduleCSS, 0, 0) ) {
			return (zListItemWorkWeekViewAllDay(action, subject));
		} else {
			throw new HarnessException("Unknown calendar view");
		}

	}
	
	public AbsPage zListItemWorkWeekView(Action action, String subject) throws HarnessException {

		logger.info(myPageName() + " zListItem("+ action +", "+ subject +")");
		tracer.trace(action +" on subject = "+ subject);

		if ( action == null )
			throw new HarnessException("action cannot be null");

		if ( subject == null )
			throw new HarnessException("subject cannot be null");

		// Default behavior variables
		String locator = null;
		AbsPage page = null;

		locator = "css=td.appt_name:contains('" + subject + "')";
		SleepUtil.sleepMedium();

		if ( action == Action.A_LEFTCLICK ) {
			this.zClickAt(locator, "");
			page=PageCalendar.this;
			
		} else if ( action == Action.A_RIGHTCLICK ) {
			this.zRightClickAt(locator, "");

		} else if ( action == Action.A_DOUBLECLICK) {
			this.sDoubleClick(locator);
			
		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}

		this.zWaitForBusyOverlay();

		if ( page != null ) {
			page.zWaitForActive();
		}

		return (page);
	}

	public AbsPage zListItemWorkWeekViewAllDay(Action action, String subject) throws HarnessException {

		logger.info(myPageName() + " zListItem("+ action +", "+ subject +")");
		tracer.trace(action +" on subject = "+ subject);

		if ( action == null )
			throw new HarnessException("action cannot be null");

		if ( subject == null )
			throw new HarnessException("subject cannot be null");

		// Default behavior variables
		String locator = null;
		AbsPage page = null;

		locator = "css=td.appt_allday_name:contains('" + subject + "')";
		SleepUtil.sleepMedium();

		if ( action == Action.A_LEFTCLICK ) {
			this.zClickAt(locator, "");
			page=PageCalendar.this;
			
		} else if ( action == Action.A_RIGHTCLICK ) {
			this.zRightClickAt(locator, "");

		} else if ( action == Action.A_DOUBLECLICK) {
			this.sDoubleClick(locator);
			
		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}

		this.zWaitForBusyOverlay();

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

		locator = "css=td.appt_name:contains('" + subject + "')";

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
				page = new DialogConfirmDelete(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);

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
	public AbsPage zListItem(Action action, Button option, String subject)
	throws HarnessException {

		logger.info(myPageName() + " zListItem("+ action +", "+ option +", "+ subject +")");
		tracer.trace(action +" then "+ option +" on subject = "+ subject);

		if ( action == null )
			throw new HarnessException("action cannot be null");
		if ( option == null )
			throw new HarnessException("button cannot be null");
		if ( subject == null || subject.trim().length() == 0)
			throw new HarnessException("subject cannot be null or blank");

		// If we are in list view, route to that method
		if ( this.zIsVisiblePerPosition(Locators.CalendarViewListCSS, 0, 0) ) {
			return (zListItemListView(action, option, subject));
		}

		// Default behavior variables
		String locator = null;
		AbsPage page = null;
		String optionLocator;

		locator = "css=td.appt_name:contains('" + subject + "')";
		SleepUtil.sleepMedium();

		if (action == Action.A_RIGHTCLICK) {
			if (option == Button.O_VIEW_DAY_MENU) {
				optionLocator = Locators.ViewDayMenu;

			} else if (option == Button.O_VIEW_WORK_WEEK_MENU) {
				optionLocator = Locators.ViewWorkWeekMenu;

			}else if (option == Button.O_VIEW_WEEK_MENU) {
				optionLocator = Locators.ViewWeekMenu;

			} else if (option == Button.O_VIEW_MONTH_MENU) {
				optionLocator = Locators.ViewMonthMenu;

			} else if (option == Button.O_VIEW_LIST_MENU) {
				optionLocator = Locators.ViewListMenu;

			} else if (option == Button.O_VIEW_SCHEDULE_MENU) {
				optionLocator = Locators.ViewScheduleMenu;

			} else if (option == Button.O_OPEN_MENU) {
				optionLocator = Locators.OpenMenu;

			} else if (option == Button.O_PRINT_MENU) {
				optionLocator = Locators.PrintMenu;

			} else if (option == Button.O_ACCEPT_MENU) {
				optionLocator = Locators.AcceptMenu;

			} else if (option == Button.O_TENTATIVE_MENU) {
				optionLocator = Locators.TentativeMenu;

			} else if (option == Button.O_DECLINE_MENU) {
				optionLocator = Locators.DeclineMenu;

			} else if (option == Button.O_EDIT_REPLY_MENU) {
				optionLocator = Locators.EditReplyMenu;

			} else if (option == Button.O_EDIT_REPLY_ACCEPT_SUB_MENU) {
				optionLocator = Locators.EditReplyAcceptSubMenu;

			} else if (option == Button.O_EDIT_REPLY_TENTATIVE_SUB_MENU) {
				optionLocator = Locators.EditReplyTentativeSubMenu;

			} else if (option == Button.O_EDIT_REPLY_DECLINE_SUB_MENU) {
				optionLocator = Locators.EditReplyDeclineSubMenu;

			} else if (option == Button.O_PROPOSE_NEW_TIME_MENU) {
				optionLocator = Locators.ProposeNewTimeMenu;

			} else if (option == Button.O_CREATE_A_COPY_MENU) {
				optionLocator = Locators.CreateACopyMenu;

			} else if (option == Button.O_REPLY_MENU) {
				optionLocator = Locators.ReplyMenu;

			} else if (option == Button.O_REPLY_TO_ALL_MENU) {
				optionLocator = Locators.ReplyToAllMenu;

			} else if (option == Button.O_FORWARD_MENU) {
				optionLocator = Locators.ForwardMenu;

			} else if (option == Button.O_DELETE_MENU) {
				optionLocator = Locators.DeleteMenu;

			} else if (option == Button.O_CANCEL_MENU) {
				optionLocator = Locators.CancelMenu;

			} else if (option == Button.O_MOVE_MENU) {
				optionLocator = Locators.MoveMenu;

			} else if (option == Button.O_TAG_APPOINTMENT_MENU) {
				optionLocator = Locators.TagAppointmentMenu;

			} else if (option == Button.O_TAG_APPOINTMENT_NEW_TAG_SUB_MENU) {
				optionLocator = Locators.TagAppointmentNewTagSubMenu;

			} else if (option == Button.O_TAG_APPOINTMENT_REMOVE_TAG_SUB_MENU) {
				optionLocator = Locators.TagAppointmentRemoveTagSubMenu;

			} else if (option == Button.O_SHOW_ORIGINAL_MENU) {
				optionLocator = Locators.ShowOriginalMenu;

			} else if (option == Button.O_QUICK_COMMANDS_MENU) {
				optionLocator = Locators.QuickCommandsMenu;

			} else if (option == Button.O_INSTANCE_MENU) {
				optionLocator = Locators.InstanceMenu;

			} else if (option == Button.O_SERIES_MENU) {
				optionLocator = Locators.SeriesMenu;

			} else if (option == Button.O_OPEN_INSTANCE_MENU) {
				optionLocator = Locators.OpenInstanceMenu;

			} else if (option == Button.O_FORWARD_INSTANCE_MENU) {
				optionLocator = Locators.ForwardInstanceMenu;

			} else if (option == Button.O_DELETE_INSTANCE_MENU) {
				optionLocator = Locators.DeleteInstanceMenu;

			} else if (option == Button.O_OPEN_SERIES_MENU) {
				optionLocator = Locators.OpenSeriesMenu;

			} else if (option == Button.O_FORWARD_SERIES_MENU) {
				optionLocator = Locators.ForwardSeriesMenu;

			} else if (option == Button.O_NEW_APPOINTMENT_MENU) {
				optionLocator = Locators.NewAppointmentMenu;

			} else if (option == Button.O_NEW_ALL_DAY_APPOINTMENT_MENU) {
				optionLocator = Locators.NewAllDayAppointmentMenu;

			} else if (option == Button.O_GO_TO_TODAY_MENU) {
				optionLocator = Locators.GoToTodayMenu;

			} else if (option == Button.O_VIEW_MENU) {
				optionLocator = Locators.ViewMenu;

			} else if (option == Button.O_VIEW_DAY_SUB_MENU) {
				optionLocator = Locators.ViewDaySubMenu;

			} else if (option == Button.O_VIEW_WORK_WEEK_SUB_MENU) {
				optionLocator = Locators.ViewWorkWeekSubMenu;

			} else if (option == Button.O_VIEW_WEEK_SUB_MENU) {
				optionLocator = Locators.ViewWeekSubMenu;

			} else if (option == Button.O_VIEW_MONTH_SUB_MENU) {
				optionLocator = Locators.ViewMonthSubMenu;

			} else if (option == Button.O_VIEW_LIST_SUB_MENU) {
				optionLocator = Locators.ViewListSubMenu;

			} else if (option == Button.O_VIEW_SCHEDULE_SUB_MENU) {
				optionLocator = Locators.ViewScheduleSubMenu;

			}
			else {
				throw new HarnessException("implement action:"+ action +" option:"+ option);
			}

		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}

		this.zRightClickAt(locator, "");
		SleepUtil.sleepSmall();
		this.zClickAt(optionLocator, "");
		SleepUtil.sleepSmall();
		this.zWaitForBusyOverlay();

		if ( page != null ) {
			page.zWaitForActive();
		}

		return (page);
	}

	public AbsPage zListItemAllDay(Action action, Button option, String subject)
	throws HarnessException {

		logger.info(myPageName() + " zListItem("+ action +", "+ option +", "+ subject +")");
		tracer.trace(action +" then "+ option +" on subject = "+ subject);

		if ( action == null )
			throw new HarnessException("action cannot be null");
		if ( option == null )
			throw new HarnessException("button cannot be null");
		if ( subject == null || subject.trim().length() == 0)
			throw new HarnessException("subject cannot be null or blank");

		// If we are in list view, route to that method
		if ( this.zIsVisiblePerPosition(Locators.CalendarViewListCSS, 0, 0) ) {
			return (zListItemListView(action, option, subject));
		}

		// Default behavior variables
		String locator = null;
		AbsPage page = null;
		String optionLocator;

		locator = "css=td.appt_allday_name:contains('" + subject + "')";
		SleepUtil.sleepMedium();

		if (action == Action.A_RIGHTCLICK) {
			if (option == Button.O_VIEW_DAY_MENU) {
				optionLocator = Locators.ViewDayMenu;

			} else if (option == Button.O_VIEW_WORK_WEEK_MENU) {
				optionLocator = Locators.ViewWorkWeekMenu;

			}else if (option == Button.O_VIEW_WEEK_MENU) {
				optionLocator = Locators.ViewWeekMenu;

			} else if (option == Button.O_VIEW_MONTH_MENU) {
				optionLocator = Locators.ViewMonthMenu;

			} else if (option == Button.O_VIEW_LIST_MENU) {
				optionLocator = Locators.ViewListMenu;

			} else if (option == Button.O_VIEW_SCHEDULE_MENU) {
				optionLocator = Locators.ViewScheduleMenu;

			} else if (option == Button.O_OPEN_MENU) {
				optionLocator = Locators.OpenMenu;

			} else if (option == Button.O_PRINT_MENU) {
				optionLocator = Locators.PrintMenu;

			} else if (option == Button.O_ACCEPT_MENU) {
				optionLocator = Locators.AcceptMenu;

			} else if (option == Button.O_TENTATIVE_MENU) {
				optionLocator = Locators.TentativeMenu;

			} else if (option == Button.O_DECLINE_MENU) {
				optionLocator = Locators.DeclineMenu;

			} else if (option == Button.O_EDIT_REPLY_MENU) {
				optionLocator = Locators.EditReplyMenu;

			} else if (option == Button.O_EDIT_REPLY_ACCEPT_SUB_MENU) {
				optionLocator = Locators.EditReplyAcceptSubMenu;

			} else if (option == Button.O_EDIT_REPLY_TENTATIVE_SUB_MENU) {
				optionLocator = Locators.EditReplyTentativeSubMenu;

			} else if (option == Button.O_EDIT_REPLY_DECLINE_SUB_MENU) {
				optionLocator = Locators.EditReplyDeclineSubMenu;

			} else if (option == Button.O_PROPOSE_NEW_TIME_MENU) {
				optionLocator = Locators.ProposeNewTimeMenu;

			} else if (option == Button.O_CREATE_A_COPY_MENU) {
				optionLocator = Locators.CreateACopyMenu;

			} else if (option == Button.O_REPLY_MENU) {
				optionLocator = Locators.ReplyMenu;

			} else if (option == Button.O_REPLY_TO_ALL_MENU) {
				optionLocator = Locators.ReplyToAllMenu;

			} else if (option == Button.O_FORWARD_MENU) {
				optionLocator = Locators.ForwardMenu;

			} else if (option == Button.O_DELETE_MENU) {
				optionLocator = Locators.DeleteMenu;

			} else if (option == Button.O_CANCEL_MENU) {
				optionLocator = Locators.CancelMenu;

			} else if (option == Button.O_MOVE_MENU) {
				optionLocator = Locators.MoveMenu;

			} else if (option == Button.O_TAG_APPOINTMENT_MENU) {
				optionLocator = Locators.TagAppointmentMenu;

			} else if (option == Button.O_TAG_APPOINTMENT_NEW_TAG_SUB_MENU) {
				optionLocator = Locators.TagAppointmentNewTagSubMenu;

			} else if (option == Button.O_TAG_APPOINTMENT_REMOVE_TAG_SUB_MENU) {
				optionLocator = Locators.TagAppointmentRemoveTagSubMenu;

			} else if (option == Button.O_SHOW_ORIGINAL_MENU) {
				optionLocator = Locators.ShowOriginalMenu;

			} else if (option == Button.O_QUICK_COMMANDS_MENU) {
				optionLocator = Locators.QuickCommandsMenu;

			} else if (option == Button.O_INSTANCE_MENU) {
				optionLocator = Locators.InstanceMenu;

			} else if (option == Button.O_SERIES_MENU) {
				optionLocator = Locators.SeriesMenu;

			} else if (option == Button.O_OPEN_INSTANCE_MENU) {
				optionLocator = Locators.OpenInstanceMenu;

			} else if (option == Button.O_FORWARD_INSTANCE_MENU) {
				optionLocator = Locators.ForwardInstanceMenu;

			} else if (option == Button.O_DELETE_INSTANCE_MENU) {
				optionLocator = Locators.DeleteInstanceMenu;

			} else if (option == Button.O_OPEN_SERIES_MENU) {
				optionLocator = Locators.OpenSeriesMenu;

			} else if (option == Button.O_FORWARD_SERIES_MENU) {
				optionLocator = Locators.ForwardSeriesMenu;

			} else if (option == Button.O_NEW_APPOINTMENT_MENU) {
				optionLocator = Locators.NewAppointmentMenu;

			} else if (option == Button.O_NEW_ALL_DAY_APPOINTMENT_MENU) {
				optionLocator = Locators.NewAllDayAppointmentMenu;

			} else if (option == Button.O_GO_TO_TODAY_MENU) {
				optionLocator = Locators.GoToTodayMenu;

			} else if (option == Button.O_VIEW_MENU) {
				optionLocator = Locators.ViewMenu;

			} else if (option == Button.O_VIEW_DAY_SUB_MENU) {
				optionLocator = Locators.ViewDaySubMenu;

			} else if (option == Button.O_VIEW_WORK_WEEK_SUB_MENU) {
				optionLocator = Locators.ViewWorkWeekSubMenu;

			} else if (option == Button.O_VIEW_WEEK_SUB_MENU) {
				optionLocator = Locators.ViewWeekSubMenu;

			} else if (option == Button.O_VIEW_MONTH_SUB_MENU) {
				optionLocator = Locators.ViewMonthSubMenu;

			} else if (option == Button.O_VIEW_LIST_SUB_MENU) {
				optionLocator = Locators.ViewListSubMenu;

			} else if (option == Button.O_VIEW_SCHEDULE_SUB_MENU) {
				optionLocator = Locators.ViewScheduleSubMenu;

			}
			else {
				throw new HarnessException("implement action:"+ action +" option:"+ option);
			}

		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}

		this.zRightClickAt(locator, "");
		SleepUtil.sleepSmall();
		this.zClickAt(optionLocator, "");
		SleepUtil.sleepSmall();
		this.zWaitForBusyOverlay();

		if ( page != null ) {
			page.zWaitForActive();
		}

		return (page);
	}
	
	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption,
			String subject) throws HarnessException {

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
		SleepUtil.sleepMedium();

		if (action == Action.A_RIGHTCLICK) {
			if (option == Button.O_VIEW_MENU) {
				optionLocator = Locators.ViewMenu;

				if (subOption == Button.O_VIEW_DAY_SUB_MENU) {
					subOptionLocator = Locators.ViewDaySubMenu;

				} else if (subOption == Button.O_VIEW_WORK_WEEK_SUB_MENU) {
					subOptionLocator = Locators.ViewWorkWeekSubMenu;

				} else if (subOption == Button.O_VIEW_WEEK_SUB_MENU) {
					subOptionLocator = Locators.ViewWeekSubMenu;

				} else if (subOption == Button.O_VIEW_MONTH_SUB_MENU) {
					subOptionLocator = Locators.ViewMonthSubMenu;

				} else if (subOption == Button.O_VIEW_LIST_SUB_MENU) {
					subOptionLocator = Locators.ViewListSubMenu;

				} else if (option == Button.O_VIEW_SCHEDULE_SUB_MENU) {
					subOptionLocator = Locators.ViewScheduleSubMenu;
				}

			} else if (option == Button.O_EDIT_REPLY_MENU) {
				optionLocator = Locators.EditReplyMenu;

				if (subOption == Button.O_EDIT_REPLY_ACCEPT_SUB_MENU) {
					subOptionLocator = Locators.EditReplyAcceptSubMenu;

				} else if (subOption == Button.O_EDIT_REPLY_TENTATIVE_SUB_MENU) {
					subOptionLocator = Locators.EditReplyTentativeSubMenu;

				} else if (subOption == Button.O_EDIT_REPLY_DECLINE_SUB_MENU) {
					subOptionLocator = Locators.EditReplyDeclineSubMenu;
				}

			} else if (option == Button.O_TAG_APPOINTMENT_MENU) {
				optionLocator = Locators.TagAppointmentMenu;

				if (subOption == Button.O_TAG_APPOINTMENT_NEW_TAG_SUB_MENU) {
					subOptionLocator = Locators.TagAppointmentNewTagSubMenu;

				} else if (subOption == Button.O_TAG_APPOINTMENT_REMOVE_TAG_SUB_MENU) {
					subOptionLocator = Locators.TagAppointmentRemoveTagSubMenu;
				}
			}
			else {
				throw new HarnessException("implement action:"+ action +" option:"+ option);
			}

		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}

		this.zRightClickAt(locator, "");
		this.sMouseMoveAt(optionLocator, "");
		SleepUtil.sleepMedium();
		this.zClickAt(subOptionLocator, "");

		this.zWaitForBusyOverlay();

		if ( page != null ) {
			page.zWaitForActive();
		}

		return (new ContextMenu(MyApplication));

	}

	public AbsPage zListItemAllDay(Action action, Button option, Button subOption,
			String subject) throws HarnessException {

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

		locator = "css=td.appt_allday_name:contains('" + subject + "')";
		SleepUtil.sleepMedium();

		if (action == Action.A_RIGHTCLICK) {
			if (option == Button.O_VIEW_MENU) {
				optionLocator = Locators.ViewMenu;

				if (subOption == Button.O_VIEW_DAY_SUB_MENU) {
					subOptionLocator = Locators.ViewDaySubMenu;

				} else if (subOption == Button.O_VIEW_WORK_WEEK_SUB_MENU) {
					subOptionLocator = Locators.ViewWorkWeekSubMenu;

				} else if (subOption == Button.O_VIEW_WEEK_SUB_MENU) {
					subOptionLocator = Locators.ViewWeekSubMenu;

				} else if (subOption == Button.O_VIEW_MONTH_SUB_MENU) {
					subOptionLocator = Locators.ViewMonthSubMenu;

				} else if (subOption == Button.O_VIEW_LIST_SUB_MENU) {
					subOptionLocator = Locators.ViewListSubMenu;

				} else if (option == Button.O_VIEW_SCHEDULE_SUB_MENU) {
					subOptionLocator = Locators.ViewScheduleSubMenu;
				}

			} else if (option == Button.O_EDIT_REPLY_MENU) {
				optionLocator = Locators.EditReplyMenu;

				if (subOption == Button.O_EDIT_REPLY_ACCEPT_SUB_MENU) {
					subOptionLocator = Locators.EditReplyAcceptSubMenu;

				} else if (subOption == Button.O_EDIT_REPLY_TENTATIVE_SUB_MENU) {
					subOptionLocator = Locators.EditReplyTentativeSubMenu;

				} else if (subOption == Button.O_EDIT_REPLY_DECLINE_SUB_MENU) {
					subOptionLocator = Locators.EditReplyDeclineSubMenu;
				}

			} else if (option == Button.O_TAG_APPOINTMENT_MENU) {
				optionLocator = Locators.TagAppointmentMenu;

				if (subOption == Button.O_TAG_APPOINTMENT_NEW_TAG_SUB_MENU) {
					subOptionLocator = Locators.TagAppointmentNewTagSubMenu;

				} else if (subOption == Button.O_TAG_APPOINTMENT_REMOVE_TAG_SUB_MENU) {
					subOptionLocator = Locators.TagAppointmentRemoveTagSubMenu;
				}
			}
			else {
				throw new HarnessException("implement action:"+ action +" option:"+ option);
			}

		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}

		this.zRightClickAt(locator, "");
		this.sMouseMoveAt(optionLocator, "");
		SleepUtil.sleepMedium();
		this.zClickAt(subOptionLocator, "");

		this.zWaitForBusyOverlay();

		if ( page != null ) {
			page.zWaitForActive();
		}

		return (new ContextMenu(MyApplication));

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
			// 7.X version: locator =
			// "css=div[id^='ztb__CLD'] td[id$='zb__CLD__NEW_MENU_title']";
			locator = Locators.NewButton;

			// Create the page
			page = new FormApptNew(this.MyApplication);
			// FALL THROUGH

		} else if (button == Button.B_DELETE) {

			locator = "css=td[id='zb__CLD__DELETE_title']";
			page = new DialogConfirmDelete(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);


		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for button " + button);
		}

		// Default behavior, process the locator by clicking on it
		//
		this.zClick(locator);

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

			page = new DialogConfirmDelete(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);

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

			page = new DialogConfirmDelete(MyApplication, ((AppAjaxClient) MyApplication).zPageCalendar);

		} else if ( 
				shortcut == Shortcut.S_MAIL_MOVETOTRASH ||
				shortcut == Shortcut.S_MAIL_HARDELETE ) {

			page = new DialogConfirmDelete(MyApplication,  ((AppAjaxClient) MyApplication).zPageCalendar);

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

				pulldownLocator = "css=div[id='zb__CLWW__NEW_MENU'] td[id$='_dropdown'] div[class='ImgSelectPullDownArrow']";
				optionLocator = "css=div[id='zb__CLWW__NEW_MENU_NEW_CALENDAR'] td[id$='_title']";
				page = new DialogCreateFolder(MyApplication, ((AppAjaxClient)MyApplication).zPageCalendar);

			} else {

				throw new HarnessException("No logic defined for pulldown " + pulldown + " and option " + option);

			}

		} else if (pulldown == Button.B_LISTVIEW) {

			pulldownLocator = "id=zb__CLD__VIEW_MENU_left_icon";

			if (option == Button.O_LISTVIEW_DAY) {

				optionLocator = "id=POPUP_DAY_VIEW";
				page = new ApptDayView(this.MyApplication);

			} else if (option == Button.O_LISTVIEW_WEEK) {

				optionLocator = "id=POPUP_WEEK_VIEW";
				page = new ApptWeekView(this.MyApplication);

			} else if (option == Button.O_LISTVIEW_WORKWEEK) {

				optionLocator = "id=POPUP_WORK_WEEK_VIEW";
				page = new ApptWorkWeekView(this.MyApplication);

			} else if (option == Button.O_LISTVIEW_SCHEDULE) {

				optionLocator = "id=POPUP_SCHEDULE_VIEW";
				page = new ApptScheduleView(this.MyApplication);

			} else if (option == Button.O_LISTVIEW_LIST) {

				optionLocator = "id=POPUP_CAL_LIST_VIEW";
				page = new ApptListView(this.MyApplication);

			} else if (option == Button.O_LISTVIEW_MONTH) {

				optionLocator = "id=POPUP_MONTH_VIEW";
				page = new ApptMonthView(this.MyApplication);

			} else if (option == Button.O_LISTVIEW_FREEBUSY) {

				optionLocator = "id=POPUP_FB_VIEW";
				page = null;

			}

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

		boolean active = this.zIsVisiblePerPosition(locator, 178, 74);
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

	private List<AppointmentItem> zListGetAppointmentsDayView() throws HarnessException {
		throw new HarnessException("implement me");
	}

	private List<AppointmentItem> zListGetAppointmentsWorkWeekView() throws HarnessException {
		throw new HarnessException("implement me");
	}

	private List<AppointmentItem> zListGetAppointmentsWeekView() throws HarnessException {
		throw new HarnessException("implement me");
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

    <tr class="calendar_month_day_item_row" id="zli__CLM__260_DWT558">
      <td class="calendar_month_day_item">
        <div style="position:relative;" id="zli__CLM__260_DWT558_body" class="">
          <table width="100%" cellspacing="0" cellpadding="0" border="0" style=
          "table-layout:fixed; background:-moz-linear-gradient(top,#FFFFFF, #ecd49c);"
          id="zli__CLM__260_DWT558_tableBody">
            <tbody>
              <tr>
                <td width="4px" style=
                "background:-moz-linear-gradient(top,#FFFFFF, #4AA6F1);"></td>

                <td width="100%">
                  <div style="overflow:hidden;white-space:nowrap;">
                    &nbsp;4:30 PM asdfasdf
                  </div>
                </td>

                <td width="20px" style="padding-right:3px;" id=
                "zli__CLM__260_DWT558_tag">
                  <div style="width:16" class="ImgBlank_16"></div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </td>
    </tr>  
		 */
		
		String locator;
		
		AppointmentItem appt = new AppointmentItem();
		appt.setLocator(cssLocator + " tr td + td"); // Point at the appt name


		appt.setGIsAllDay(false);
		
		// Get the subject
		locator = cssLocator + " tr td + td";
		appt.setSubject(this.sGetText(locator));
		
		// TODO: get the tags		
		
		return (appt);
	}
	
	private List<AppointmentItem> zListGetAppointmentsMonthView() throws HarnessException {
		logger.info(myPageName() + " zListGetAppointmentsMonthView()");
		
		String itemsLocator;
		
		int count;
		List<AppointmentItem> items = new ArrayList<AppointmentItem>();
		
		// Process the all-day items first
		itemsLocator = Locators.CalendarViewMonthCSS + " div.appt";
		count = this.sGetCssCount(itemsLocator);
		logger.info(itemsLocator +" count: "+ count);
		
		for (int i = 0; i < count; i++) {
			
			String itemLocator = itemsLocator + StringUtils.repeat(" + div.appt", i);
			
			String alldayLocator = itemLocator + " div.appt_allday_body";
			if ( this.sIsElementPresent(alldayLocator) ) {
				items.add(parseMonthViewAllDay(alldayLocator));
			}			
		}
		
		// Process the non-all-day items next
		itemsLocator = Locators.CalendarViewMonthCSS + " tr.calendar_month_day_item_row";
		count = this.sGetCssCount(itemsLocator);
		logger.info(itemsLocator +" count: "+ count);
		
		for (int i = 0; i < count; i++) {
			
			String itemLocator = itemsLocator + StringUtils.repeat(" + tr.calendar_month_day_item_row", i);
			if ( this.sIsElementPresent(itemLocator + ">td.calendar_month_day_item") ) {
				items.add(parseMonthViewNonAllDay(itemLocator));
			}
		}

		return (items);
	}

	private List<AppointmentItem> zListGetAppointmentsScheduleView() throws HarnessException {
		throw new HarnessException("implement me");
	}

	public List<AppointmentItem> zListGetAppointments() throws HarnessException {

		if ( this.zIsVisiblePerPosition(Locators.CalendarViewListCSS, 0, 0) ) {
			return (zListGetAppointmentsListView());
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewDayCSS, 0, 0) ) {
			return (zListGetAppointmentsDayView());
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewWorkWeekCSS, 0, 0) ) {
			return (zListGetAppointmentsDayView());
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewWeekCSS, 0, 0) ) {
			return (zListGetAppointmentsDayView());
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewMonthCSS, 0, 0) ) {
			return (zListGetAppointmentsMonthView());
		} else if ( this.zIsVisiblePerPosition(Locators.CalendarViewScheduleCSS, 0, 0) ) {
			return (zListGetAppointmentsDayView());
		} else {
			throw new HarnessException("Unknown calendar view");
		}
	}

}
