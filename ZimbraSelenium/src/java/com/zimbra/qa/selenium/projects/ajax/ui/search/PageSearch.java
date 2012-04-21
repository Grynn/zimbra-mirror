/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.search;

import java.util.*;

import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;


/**
 * @author Matt Rhoades
 *
 */
public class PageSearch extends AbsTab {

	public static class Locators {
		
		public static final String zActiveLocator = "css=div#ztb_search";
		
		public static final String zSearchInput = "css=input#zi_search_inputfield";
		public static final String zSearchButton = "css=td#zb__Search__SAVE_left_icon";
		
	}

	private boolean zIsIncludeSharedItems=false;
	private static HashMap<Button,String> imagesMap             = new HashMap<Button,String>();
	private static HashMap<Button,String> imagesIncludeShareMap = new HashMap<Button,String>();
	{	
		imagesMap.put(Button.O_SEARCHTYPE_ALL,"ImgGlobe");
		imagesIncludeShareMap.put(Button.O_SEARCHTYPE_ALL,"ImgGlobe");
		
		imagesMap.put(Button.O_SEARCHTYPE_EMAIL,"ImgMessage");
		imagesIncludeShareMap.put(Button.O_SEARCHTYPE_EMAIL,"ImgSharedMailFolder");
		
		imagesMap.put(Button.O_SEARCHTYPE_CONTACTS,"ImgContact");
		imagesIncludeShareMap.put(Button.O_SEARCHTYPE_CONTACTS,"ImgSharedContactsFolder");

		imagesMap.put(Button.O_SEARCHTYPE_GAL,"ImgGAL");
		imagesIncludeShareMap.put(Button.O_SEARCHTYPE_GAL,"ImgGAL");
		
		imagesMap.put(Button.O_SEARCHTYPE_APPOINTMENTS,"ImgAppointment");
		imagesIncludeShareMap.put(Button.O_SEARCHTYPE_APPOINTMENTS,"ImgAppointment");
		
		imagesMap.put(Button.O_SEARCHTYPE_TASKS,"ImgTasksApp");
		imagesIncludeShareMap.put(Button.O_SEARCHTYPE_TASKS,"ImgSharedTaskList");
		
		imagesMap.put(Button.O_SEARCHTYPE_FILES,"ImgDoc");
		imagesIncludeShareMap.put(Button.O_SEARCHTYPE_FILES,"ImgDoc");		
	}
	
	public PageSearch(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageSearch.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the Mobile Client is loaded in the browser
		if ( !MyApplication.zIsLoaded() )
			throw new HarnessException("Application is not active!");
		

		// Look for the search toolbar button
		boolean present = sIsElementPresent(Locators.zActiveLocator);
		if ( !present ) {
			logger.debug("isActive() present = "+ present);
			return (false);
		}
		
		logger.debug("isActive() = "+ true);
		return (true);

	}
	
	public void zClose() throws HarnessException {
		
		if ( !zIsActive() ) {
			return; // Already closed
		}
		
		String locator = "css=div[id^='zb__App__tab_SR'] td[id$='_right_icon'] div.ImgCloseGray";
		
		if ( !this.sIsElementPresent(locator) ) {
			return; // Already closed
		}
		
		this.zClickAt(locator, "");
		this.zWaitForBusyOverlay();
		
		return;
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#navigateTo()
	 */
	@Override
	public void zNavigateTo() throws HarnessException {


		if ( zIsActive() ) {
			// This page is already active
			return;
		}
		

		// If search is not active, then we must not be logged in
		if ( !((AppAjaxClient)MyApplication).zPageMain.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageMain.zNavigateTo();
		}

		// Nothing more to do to make search appear, since it is always active if the app is active
		tracer.trace("Navigate to "+ this.myPageName());

		zWaitForActive();
		
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");
		
		tracer.trace("Click button "+ button);

		if ( button == null )
			throw new HarnessException("Button cannot be null!");
		
				
		// Default behavior variables
		//
		String locator = null;	// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned
		
		// Based on the button specified, take the appropriate action(s)
		//
		
		if ( button == Button.B_SEARCH ) {
			locator = "css=div#zb__Search__SEARCH_left_icon div.ImgSearch2";
			
			// for all item types
			if (zIsSearchType(Button.O_SEARCHTYPE_ALL)) {
			    page = new PageAllItemTypes(((AppAjaxClient)MyApplication));
			}
			
		} else if ( (button == Button.B_SEARCHSAVE) || (button == Button.B_SAVE) ) {
			
			locator = "css=div[id^='ztb_searchresults__'] td[id$='_saveButton'] td[id$='_title']";
			page = new DialogSaveSearch(MyApplication, this);
			
		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}
		
		// Make sure the button exists
		if ( !sIsElementPresent(locator) )
			throw new HarnessException("Button is not present locator="+ locator +" button="+ button);
		
		// Default behavior, process the locator by clicking on it
		//
	
		// Click it
		zClick(locator);
		
		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();
		
		
		// If page was specified, make sure it is active
		if ( page != null ) {
            
			// This function (default) throws an exception if never active
			page.zWaitForActive();
			
		}
		
		return (page);
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ option +")");
		
		tracer.trace("Click pulldown "+ pulldown +" then "+ option);

		if ( pulldown == null )
			throw new HarnessException("Pulldown cannot be null!");
		
		if ( option == null )
			throw new HarnessException("Option cannot be null!");

		// Default behavior variables
		//
		String pulldownLocator = null;	// If set, this will be expanded
		String optionLocator = null;	// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned
		
		// Based on the button specified, take the appropriate action(s)
		//
		
		if ( pulldown == Button.B_SEARCHTYPE ) {
			pulldownLocator = "css=td#ztb_search_searchMenuButton";
			
			if ( option == Button.O_SEARCHTYPE_ALL ) {        
				optionLocator = "css=div#zmi__Search__ANY";						
			} else if ( option == Button.O_SEARCHTYPE_EMAIL ) {
				optionLocator = "css=div#zmi__Search__MAIL";		
			} else if ( option == Button.O_SEARCHTYPE_CONTACTS ) {
				optionLocator = "css=div#zmi__Search__CONTACT";		
			} else if ( option == Button.O_SEARCHTYPE_GAL ) {
				optionLocator = "css=div#zmi__Search__GAL";		
			} else if ( option == Button.O_SEARCHTYPE_APPOINTMENTS ) {
				optionLocator = "css=div#zmi__Search__APPT";		
			} else if ( option == Button.O_SEARCHTYPE_TASKS ) {
				optionLocator = "css=div#zmi__Search__TASK";		
			} else if ( option == Button.O_SEARCHTYPE_FILES ) {
				optionLocator = "css=div#zmi__Search__BRIEFCASE_ITEM";		
			} else if ( option == Button.O_SEARCHTYPE_INCLUDESHARED ) {
				optionLocator = "css=div#zmi__Search__SHARED";		
				
			} else {
				throw new HarnessException("no logic defined for pulldown/option "+ pulldown +"/"+ option);
			}
			
		} else {
			throw new HarnessException("no logic defined for pulldown "+ pulldown);
		}

		// Default behavior
		if ( pulldownLocator != null ) {
						
			// Make sure the locator exists
			if ( !this.sIsElementPresent(pulldownLocator) ) {
				throw new HarnessException("Button "+ pulldown +" option "+ option +" pulldownLocator "+ pulldownLocator +" not present!");
			}
			
			this.zClick(pulldownLocator);

			// If the app is busy, wait for it to become active
			this.zWaitForBusyOverlay();
			
			
			if ( optionLocator != null ) {

				// Make sure the locator exists
				if ( !this.sIsElementPresent(optionLocator) ) {
					throw new HarnessException("Button "+ pulldown +" option "+ option +" optionLocator "+ optionLocator +" not present!");
				}
				
				this.zClick(optionLocator);

				// If the app is busy, wait for it to become active
				this.zWaitForBusyOverlay();
				
				if ( option == Button.O_SEARCHTYPE_INCLUDESHARED ) {
				   zIsIncludeSharedItems = !zIsIncludeSharedItems; 
				}
				
			}
			
			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if ( page != null ) {
				page.zWaitForActive();
			}
			
			if (!zIsSearchType(option)) {
				throw new HarnessException("Not able to change search type "+ option ); 
			}
			
		}
		
		// Return the specified page, or null if not set
		return (page);
	}

	@Override
	public AbsPage zListItem(Action action, String item) throws HarnessException {
		throw new HarnessException(myPageName() + " does not have a list view");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item) throws HarnessException {
		throw new HarnessException(myPageName() + " does not have a list view");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption ,String item)  throws HarnessException {
	   throw new HarnessException(myPageName() + " does not have a list view");
    }
		
	/**
	 * Enter text into the query string field
	 * @param query
	 * @throws HarnessException 
	 */
	public void zAddSearchQuery(String query) throws HarnessException {
		logger.info(myPageName() + " zAddSearchQuery("+ query +")");
		
		tracer.trace("Search for the query "+ query);

		
		this.zTypeKeys(Locators.zSearchInput, query);

	}
	

    public boolean zIsSearchType(Button button) throws HarnessException
    {
    	String imageClass=null;
        if (zIsIncludeSharedItems) {
        	imageClass = imagesIncludeShareMap.get(button);
        }
        else {
        	imageClass = imagesMap.get(button);
        }
            
    	return sIsElementPresent("css=td#zb__Search__MENU_left_icon>div." + imageClass);
    }

	public List<MailItem> zListGetMessages() throws HarnessException {

		List<MailItem> items = new ArrayList<MailItem>();

		String listLocator = null;
		String rowLocator = null;
		if (zGetPropMailView() == SearchView.BY_MESSAGE) {
			listLocator = "css=div[id^='zv__TV-SR-Mail-']";
			rowLocator = "div[id$='__rows']";
		} else {
			listLocator = "css=div[id^='zv__CLV-SR-Mail-']";
			rowLocator = "div[id$='__rows']";
		}

		// Make sure the button exists
		if ( !this.sIsElementPresent(listLocator) )
			throw new HarnessException("Message List View Rows is not present: " + listLocator);

		String tableLocator = listLocator + " " + rowLocator;
		
		// How many items are in the table?
		int count = this.sGetCssCount(tableLocator);
		logger.debug(myPageName() + " zListGetMessages: number of messages: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {

			// Add the new item to the list
			MailItem item = ((AppAjaxClient)this.MyApplication).zPageMail.parseMessageRow(listLocator + " div:nth-of-type("+ i +") ");
			items.add(item);
			logger.info(item.prettyPrint());
		}

		// Return the list of items
		return (items);
	}
	
	public enum SearchView {
		BY_MESSAGE, BY_CONVERSATION
	}


	public SearchView zGetPropMailView() throws HarnessException {
		if ( this.zIsVisiblePerPosition("css=div[id^='zv__CLV-SR-Mail-']", 0, 0) ) {
			return (SearchView.BY_CONVERSATION);
		} else if ( this.zIsVisiblePerPosition("css=div[id^='zv__TV-SR-Mail-']", 0, 0) ) {
			return (SearchView.BY_MESSAGE);
		}

		throw new HarnessException("Unable to determine the Page Mail View");
	}


}
