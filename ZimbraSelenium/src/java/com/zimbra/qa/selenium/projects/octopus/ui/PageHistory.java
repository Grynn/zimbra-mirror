/**
 * 
 */
package com.zimbra.qa.selenium.projects.octopus.ui;

import java.util.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogError;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogError.DialogErrorID;


public class PageHistory extends AbsTab {
    interface HISTORY_CONSTANTS {
      String HISTORY_VIEW_LOCATOR = "css=div.octopus-updates-view";
      String HISTORY_HEADER_VIEW_LOCATOR = HISTORY_VIEW_LOCATOR + " div#my-updates-header-view";
      String HISTORY_FILTER_VIEW_LOCATOR = HISTORY_VIEW_LOCATOR + " div.my-updates-filter-view";
    	 
   	  String[] HISTORY_FILTER_VIEW_TEXT = {"Refine", 
                                           "Activity Type",
                                           "all types",
                                           "favorites",
                                           "comment",
                                           "sharing",
                                           "new version"};
	
    }
	public static class Locators {
		public static final Locators zTabHistory = new Locators(
				"css=div.octopus-tab-label:contains(History)");
		public static final Locators zTabHistorySelected = new Locators(
				"css=div[class^=octopus-tab sc-collection-item sel]>div.octopus-tab-label:contains(History)");
		public static final Locators zHistoryView = new Locators(
				"css=div[id=octopus-updates-view]");
		public static final Locators zHistoryHeader = new Locators(
				"css=div[id=my-updates-header-view]");
		public static final Locators zHistoryItemsView = new Locators(
				"css=div[id=my-updates-view]");
		public static final Locators zHistoryItemRow = new Locators(
				"css=div[class=activity-stream-pane-list-item]");
		public static final Locators zHistoryFilterView = new Locators(
				"css=div[class=my-updates-filter-view]");
		public static final Locators zHistoryFilterAllTypes = new Locators(
				"css=input[id=filter_alltypes]");
		public static final Locators zHistoryFilterFavorites = new Locators(
				"css=input[id=filter_watched]");
		public static final Locators zHistoryFilterComment = new Locators(
				"css=input[id=filter_comment]");
		public static final Locators zHistoryFilterSharing = new Locators(
				"css=input[id=filter_sharing]");
		public static final Locators zHistoryFilterNewVersion = new Locators(
				"css=input[id=filter_new]");
		public static final Locators zHistoryFilterRename = new Locators(
				"css=input[id=filter_rename]");

			public final String locator;

		private Locators(String locator) {
			this.locator = locator;
		}
	}

	public PageHistory(AbsApplication application) {
		super(application);

		logger.info("new " + PageSharing.class.getCanonicalName());

	}

	public Toaster zGetToaster() throws HarnessException {
		return (new Toaster(this.MyApplication));
	}

	public DialogError zGetErrorDialog(DialogErrorID zimbra) {
		return (new DialogError(zimbra, this.MyApplication, this));
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// Look for the Sharing tab
		boolean selected = sIsElementPresent(Locators.zTabHistorySelected.locator);

		if (!selected) {
			logger.debug("zIsActive(): " + selected);
			return (false);
		}

		logger.debug("isActive() = " + true);
		return (true);
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zNavigateTo() throws HarnessException {

		if (zIsActive()) {
			// This page is already active
			return;
		}

		// Make sure PageOctopus page is active
		if (!((AppOctopusClient) MyApplication).zPageOctopus.zIsActive()) {
			((AppOctopusClient) MyApplication).zPageOctopus.zNavigateTo();
		}

		String locator = Locators.zTabHistory.locator;

		if (!zWaitForElementPresent(locator, "5000")) {
			throw new HarnessException(locator + " Not Present!");
		}

		// Click on Sharing tab
		zClickAt(locator, "0,0");

		zWaitForBusyOverlay();

		zWaitForActive();
	}

	public AbsPage zToolbarPressButton(Button button, IItem item)
			throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton(" + button + ")");

		tracer.trace("Press the " + button + " button");

		throw new HarnessException("No logic defined for: " + button + " :"
				+ item);

	}

	public AbsPage zToolbarCheckMark(Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarCheckOption(" + option + ")");

		tracer.trace("Check the " + option + " option");

		if (option == null)
			throw new HarnessException("Check box cannot be null!");

		// Default behavior variables
		//
		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if (option == Button.O_ALL_TYPES) {
			locator = Locators.zHistoryFilterAllTypes.locator;
		} else if (option == Button.O_FAVORITES) {
			locator = Locators.zHistoryFilterFavorites.locator;
		} else if (option == Button.O_COMMENT) {
			locator = Locators.zHistoryFilterComment.locator;
		} else if (option == Button.O_SHARING) {
			locator = Locators.zHistoryFilterSharing.locator;
		} else if (option == Button.O_NEW_VERSION) {
			locator = Locators.zHistoryFilterNewVersion.locator;
		} else if (option == Button.O_RENAME) {
			locator = Locators.zHistoryFilterRename.locator;
		} else {
			throw new HarnessException("no logic defined for check box " + option);
		}

		if (!this.sIsElementPresent(locator))
			throw new HarnessException("Check box is not present: " + locator);

		// Check box
		this.sCheck(locator);

		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

		return (page);
	}

	
	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton(" + button + ")");

		tracer.trace("Press the " + button + " button");

		if (button == null)
			throw new HarnessException("Check box cannot be null!");

		// Default behavior variables
		//
		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if (button == Button.O_ALL_TYPES) {
			locator = Locators.zHistoryFilterAllTypes.locator;
		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (!this.sIsElementPresent(locator))
			throw new HarnessException("button is not present: " + locator);

		// Check box
		this.zClick(locator);

		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

		return (page);
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		throw new HarnessException("Implement me");
	}
	
    // return a list of history items 
	public ArrayList<HistoryItem> zListItem()
	    throws HarnessException  
	{
        ArrayList<HistoryItem> historyItems = new ArrayList<HistoryItem>();
        
       // Is this necessary?
		this.zWaitForBusyOverlayOctopus();
		
		String listLocator = "css=div[id='my-updates-view'] div[class='activity-stream-pane-list-item']";
		int count = sGetCssCount(listLocator);
		logger.debug("total items= " + count);
		for (int i = 1; i <= count; i++) {
			
			String locator;
			String itemLocator = listLocator + ":nth-of-type("+ i +")";
			
			HistoryItem item = new HistoryItem();

			// Set the locator to the item
			item.setLocator(itemLocator);
						
			// Get the user
			locator = itemLocator + " span[class='activity-item-body'] span[class='user']";
			item.setHistoryUser(sGetText(locator));
			
			// Get the Time
			locator = itemLocator + " span[class='activity-item-time']";
			item.setHistoryTime(sGetText(locator));
			
			// Get the Comment Text
			locator = itemLocator + " span[class='activity-item-body']";
			item.setHistoryText(sGetText(locator));
			
			logger.info(item.prettyPrint());
			
			historyItems.add(item);
		}
		
		
		return historyItems;
    }

	@Override
	public AbsPage zListItem(Action action, String item)
			throws HarnessException {
		throw new HarnessException("Implement me");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item)
			throws HarnessException {
		throw new HarnessException("Implement me");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption,
			String item) throws HarnessException {
		throw new HarnessException("Implement me");
	}
}
