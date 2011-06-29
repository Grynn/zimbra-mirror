package com.zimbra.qa.selenium.projects.ajax.ui.calendar;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

public class PageCalendar extends AbsTab {

	public static class Locators {
	}

	public PageCalendar(AbsApplication application) {
		super(application);

		logger.info("new " + PageCalendar.class.getCanonicalName());
	}

	@Override
	public AbsPage zListItem(Action action, String item) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption, String item) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");

		tracer.trace("Press the "+ button +" button");

		if ( button == null )
			throw new HarnessException("Button cannot be null!");


		// Default behavior variables
		//
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if ( button == Button.B_NEW ) {

			// New button
			// 7.X version: locator = "css=div[id^='ztb__CLD'] td[id$='zb__CLD__NEW_MENU_title']";
			locator = "css=td#zb__CLWW__NEW_MENU_title";
			
			// Create the page
			page = new FormApptNew(this.MyApplication);

			// FALL THROUGH

		} else if ( button == Button.B_REFRESH ) {

			locator = "css=td#CHECK_MAIL_left_icon>div[class='ImgRefresh']";
			
			page = null;
			
			// FALL THROUGH
			
		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}

		// Default behavior, process the locator by clicking on it
		//
		this.zClick(locator);

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP &&
				button == Button.B_GETMAIL) {


			// Wait for the spinner image
			zWaitForDesktopLoadingSpinner(5000);
		}

		// If page was specified, make sure it is active
		if ( page != null ) {

			// This function (default) throws an exception if never active
			page.zWaitForActive();

		}


		return (page);
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressPulldown("+ pulldown +", "+ option +")");

		tracer.trace("Click pulldown "+ pulldown +" then "+ option);

		if ( pulldown == null )
			throw new HarnessException("Button cannot be null!");

	    String pulldownLocator = null;	// If set, this will be expanded
		String optionLocator = null;	// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned
	  
		if ( pulldown == Button.B_LISTVIEW ) {
			pulldownLocator = "css=div#zb__CLD__VIEW_MENU";
			optionLocator = "css=tr#" + option.toString();
		    
			if (option == Button.O_LISTVIEW_DAY) {				
			    page = new ApptDayView(this.MyApplication);
			} 
			else if (option == Button.O_LISTVIEW_WEEK) {				
			    page = new ApptWeekView(this.MyApplication);
			}  
			else if (option == Button.O_LISTVIEW_WORKWEEK) {				
			    page = new ApptWorkWeekView(this.MyApplication);
			}  
			else if (option == Button.O_LISTVIEW_SCHEDULE) {				
			    page = new ApptScheduleView(this.MyApplication);
			}  
			else if (option == Button.O_LISTVIEW_LIST) {				
			    page = new ApptListView(this.MyApplication);
			}  
			else if (option == Button.O_LISTVIEW_MONTH) {				
			    page = new ApptMonthView(this.MyApplication);
			}  

		}
	
		if ( pulldownLocator != null ) {
						
			// Make sure the locator exists
			if ( !sIsElementPresent(pulldownLocator) ) {
				throw new HarnessException("Button "+ pulldown +" option "+ option +" pulldownLocator "+ pulldownLocator +" not present!");
			}

			if (ClientSessionFactory.session().currentBrowserName().contains("IE")) {
				//IE			
				sClickAt(pulldownLocator,"0,0");
			}
			else {
			    //others
			    zClickAt(pulldownLocator,"0,0");
			}
			
			zWaitForBusyOverlay();
			
			if ( optionLocator != null ) {
                
				// Make sure the locator exists
				zWaitForElementPresent(optionLocator);
				
				zClick(optionLocator);
				zWaitForBusyOverlay();

			}
			
			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if ( page != null ) {
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
		if ( zIsActive() ) {
			return;
		}

		// Make sure we are logged in
		if ( !((AppAjaxClient)MyApplication).zPageMain.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageMain.zNavigateTo();
		}

		tracer.trace("Navigate to "+ this.myPageName());

		this.zClick(PageMain.Locators.zAppbarCal);

		this.zWaitForBusyOverlay();

		zWaitForActive();
		
	}


	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the main page is active
		if ( !((AppAjaxClient)MyApplication).zPageMain.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageMain.zNavigateTo();
		}

		/**
		 * 8.0:
		 * <div 
		 * 		id="ztb__CLD" 
		 * 		style="position: absolute; overflow: visible; z-index: 300; left: 179px; top: 78px; width: 1280px; height: 26px;" 
		 * 		class="ZToolbar ZWidget" 
		 * 		parentid="z_shell">
		 */
		// If the "folders" tree is visible, then mail is active
		String locator = "css=div#ztb__CLD";

		boolean loaded = this.sIsElementPresent(locator);
		if ( !loaded )
			return (false);

		boolean active = this.zIsVisiblePerPosition(locator, 178, 74);
		if ( !active )
			return (false);
		
		// html body div#z_shell.DwtShell div#ztb__CLD.ZToolbar
		// Made it here.  The page is active.
		return (true);

	}
	
}
