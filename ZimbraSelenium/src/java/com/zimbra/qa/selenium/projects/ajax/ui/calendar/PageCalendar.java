package com.zimbra.qa.selenium.projects.ajax.ui.calendar;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.ContextMenuItem.CONTEXT_MENU_ITEM_NAME;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogRedirect;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.TreeMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.PageMail.PageMailView;

@SuppressWarnings("unused")
public class PageCalendar extends AbsTab {

	public static class Locators {
		// Buttons
		public static final String NewButton = "css=td#zb__CLWW__NEW_MENU_title";
		public static final String RefreshButton = "css=td#CHECK_MAIL_left_icon>div[class='ImgRefresh']";
		public static final String ViewButton = "id=zb__CLD__VIEW_MENU_dropdown";
		
		// Menus
		public static final String ViewDayMenu = "id=POPUP_DAY_VIEW";
        public static final String ViewWorkWeekMenu = "id=POPUP_WORK_WEEK_VIEW";
        public static final String ViewWeekMenu = "id=POPUP_WEEK_VIEW";
        public static final String ViewMonthMenu = "id=POPUP_MONTH_VIEW";
        public static final String ViewListMenu = "id=POPUP_CAL_LIST_VIEW";
        public static final String ViewScheduleMenu = "id=POPUP_SCHEDULE_VIEW";
        public static final String OpenMenu = "id=zmi__Calendar__VIEW_APPOINTMENT_title";
        public static final String PrintMenu = "id=zmi__Calendar__PRINT_title";
        public static final String AcceptMenu = "id=zmi__Calendar__REPLY_ACCEPT_title";
        public static final String TentativeMenu = "id=zmi__Calendar__REPLY_TENTATIVE_title";
        public static final String DeclineMenu = "id=zmi__Calendar__REPLY_DECLINE_title";
        public static final String EditReplyMenu = "id=zmi__Calendar__INVITE_REPLY_MENU_title";
        public static final String EditReplyAcceptSubMenu = "id=POPUP_EDIT_REPLY_ACCEPT";
        public static final String EditReplyTentativeSubMenu = "id=POPUP_EDIT_REPLY_TENTATIVE";
        public static final String EditReplyDeclineSubMenu = "id=POPUP_EDIT_REPLY_DECLINE";
        public static final String ProposeNewTimeMenu = "id=zmi__Calendar__PROPOSE_NEW_TIME_title";
        public static final String CreateACopyMenu = "id=zmi__Calendar__DUPLICATE_APPT_title";
        public static final String ReplyMenu = "id=zmi__Calendar__REPLY_title";
        public static final String ReplyToAllMenu = "id=zmi__Calendar__REPLY_ALL_title";
        public static final String ForwardMenu = "id=zmi__Calendar__FORWARD_APPT_title";
        public static final String DeleteMenu = "id=zmi__Calendar__DELETE_title";
        public static final String CancelMenu = "id=zmi__Calendar__CANCEL_title";
        public static final String MoveMenu = "id=zmi__Calendar__MOVE_title";
        public static final String TagAppointmentMenu = "id=zmi__Calendar__TAG_MENU_title";
        public static final String TagAppointmentNewTagSubMenu = "id=zmi__Calendar__TAG_MENU|MENU|NEWTAG_title";
        public static final String TagAppointmentRemoveTagSubMenu = "id=zmi__Calendar__TAG_MENU|MENU|REMOVETAG_title";
        public static final String ShowOriginalMenu = "id=zmi__Calendar__SHOW_ORIG_title";
        public static final String QuickCommandsMenu = "id=zmi__Calendar__QUICK_COMMANDS_title";
        public static final String InstanceMenu = "id=POPUP_VIEW_APPT_INSTANCE";
        public static final String SeriesMenu = "id=POPUP_VIEW_APPT_SERIES";
        public static final String OpenInstanceMenu = "id=zmi__Calendar__OPEN_APPT_INSTANCE_title";
        public static final String ForwardInstanceMenu = "id=zmi__Calendar__FORWARD_APPT_INSTANCE_title";
        public static final String DeleteInstanceMenu = "id=zmi__Calendar__DELETE_INSTANCE_title";
        public static final String OpenSeriesMenu = "id=zmi__Calendar__OPEN_APPT_SERIES_title";
        public static final String ForwardSeriesMenu = "id=zmi__Calendar__FORWARD_APPT_SERIES_title";
        public static final String DeleteSeriesMenu = "id=zmi__Calendar__DELETE_SERIES_title";
        public static final String NewAppointmentMenu = "id=POPUP_NEW_APPT";
        public static final String NewAllDayAppointmentMenu = "id=POPUP_NEW_ALLDAY_APPT";
        public static final String GoToTodayMenu = "id=POPUP_TODAY";
        public static final String ViewMenu = "id=POPUP_CAL_VIEW_MENU";
        public static final String ViewDaySubMenu = "id=POPUP_DAY_VIEW";
        public static final String ViewWorkWeekSubMenu = "id=POPUP_WORK_WEEK_VIEW";
        public static final String ViewWeekSubMenu = "id=POPUP_WEEK_VIEW";
        public static final String ViewMonthSubMenu = "id=POPUP_MONTH_VIEW";
        public static final String ViewListSubMenu = "id=POPUP_CAL_LIST_VIEW";
        public static final String ViewScheduleSubMenu = "id=POPUP_SCHEDULE_VIEW";
        
        // Radio button
        public static final String OpenThisInstanceRadioButton = "id=";
        public static final String OpenThisSeriesRadioButton = "id=";
	}

	public PageCalendar(AbsApplication application) {
		super(application);

		logger.info("new " + PageCalendar.class.getCanonicalName());
	}

	@Override
	public AbsPage zListItem(Action action, String subject) throws HarnessException {
		
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
			
		} else if ( action == Action.A_RIGHTCLICK ) {
			this.zRightClickAt(locator, "");
			
		} else if ( action == Action.A_DOUBLECLICK) {
			this.sDoubleClick(locator);
			
		} else if ( action == Action.A_DOUBLECLICK) { //need to add action for iselement present
			this.sIsElementPresent(locator);
			
		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}
		
		this.zWaitForBusyOverlay();
		
		if ( page != null ) {
			page.zWaitForActive();
		}

		return (new ContextMenu(MyApplication));
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
		
		return (new ContextMenu(MyApplication));
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

		if (button == Button.B_NEW) {

			// New button
			// 7.X version: locator =
			// "css=div[id^='ztb__CLD'] td[id$='zb__CLD__NEW_MENU_title']";
			locator = Locators.NewButton;

			// Create the page
			page = new FormApptNew(this.MyApplication);
			// FALL THROUGH

		} else if (button == Button.B_REFRESH) {
			locator = Locators.RefreshButton;
			page = null;
			// FALL THROUGH

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

		if (pulldown == Button.B_LISTVIEW) {
			pulldownLocator = "id=zb__CLD__VIEW_MENU_left_icon";

			if (option == Button.O_LISTVIEW_DAY) {
				page = new ApptDayView(this.MyApplication);
			} else if (option == Button.O_LISTVIEW_WEEK) {
				page = new ApptWeekView(this.MyApplication);
			} else if (option == Button.O_LISTVIEW_WORKWEEK) {
				page = new ApptWorkWeekView(this.MyApplication);
			} else if (option == Button.O_LISTVIEW_SCHEDULE) {
				page = new ApptScheduleView(this.MyApplication);
			} else if (option == Button.O_LISTVIEW_LIST) {
				page = new ApptListView(this.MyApplication);
			} else if (option == Button.O_LISTVIEW_MONTH) {
				page = new ApptMonthView(this.MyApplication);
			}
		}

		if (pulldownLocator != null) {

			// Make sure the locator exists
			if (!sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown + " option "
						+ option + " pulldownLocator " + pulldownLocator
						+ " not present!");
			}

			if (ClientSessionFactory.session().currentBrowserName().contains(
					"IE")) {
				// IE
				sClickAt(pulldownLocator, "0,0");
			} else {
				// others
				zClickAt(pulldownLocator, "0,0");
			}

			zWaitForBusyOverlay();

			if (option != null) {

				// Make sure the locator exists
				if (option == Button.O_LISTVIEW_DAY) {
					optionLocator = "id=POPUP_DAY_VIEW";
				} else if (option == Button.O_LISTVIEW_WEEK) {
					optionLocator = "id=POPUP_WEEK_VIEW";
				} else if (option == Button.O_LISTVIEW_WORKWEEK) {
					optionLocator = "id=POPUP_WORK_WEEK_VIEW";
				} else if (option == Button.O_LISTVIEW_MONTH) {
					optionLocator = "id=POPUP_MONTH_VIEW";
				} else if (option == Button.O_LISTVIEW_LIST) {
					optionLocator = "id=POPUP_CAL_LIST_VIEW";
				} else if (option == Button.O_LISTVIEW_SCHEDULE) {
					optionLocator = "id=POPUP_SCHEDULE_VIEW";
				}	
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

}
