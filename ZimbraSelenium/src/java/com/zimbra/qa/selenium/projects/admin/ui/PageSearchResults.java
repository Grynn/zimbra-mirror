package com.zimbra.qa.selenium.projects.admin.ui;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;



public class PageSearchResults extends AbsTab {
	public static class Locators {
		public static final String SEARCH_INPUT_TEXT_BOX="_XForm_query_display";
		public static final String SEARCH_BUTTON="css=td.xform_container div.ImgSearch";
		public static final String DELETE_BUTTON="zmi__zb_currentApp__DELETE";
		public static final String CONFIGURE_ICON="css=div.ImgConfigure";
	}

	public PageSearchResults(AbsApplication application) {
		super(application);
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		throw new HarnessException("implement me");
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zNavigateTo() throws HarnessException {
		throw new HarnessException("implement me");
	}

	/**
	 * Enter text into the query string field
	 * @param query
	 * @throws HarnessException 
	 */
	public void zAddSearchQuery(String query) throws HarnessException {
		logger.info(myPageName() + " zAddSearchQuery("+ query +")");

		tracer.trace("Search for the query "+ query);

		this.sType(Locators.SEARCH_INPUT_TEXT_BOX, query);

	}

	@Override
	public AbsPage zListItem(Action action, String entity) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ entity +")");

		tracer.trace(action +" on subject = "+ entity);

		AbsPage page = null;

		// How many items are in the table?
		String rowsLocator = "css=div#zl__SEARCH_MANAGE div[id$='__rows'] div[id^='zli__']";
		int count = this.sGetCssCount(rowsLocator);
		logger.debug(myPageName() + " zListGetAccounts: number of accounts: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {
			final String accountLocator = rowsLocator + ":nth-child("+i+")";
			String locator;

			// Email Address
			locator = accountLocator + " td[id^='SEARCH_MANAGE_data_emailaddress']";


			if(this.sIsElementPresent(locator)) 
			{
				if(this.sGetText(locator).trim().equalsIgnoreCase(entity)) 
				{
					if(action == Action.A_LEFTCLICK) {
						zClick(locator);
						break;
					}

				}
			}
		}
		return page;
	}


	@Override
	public AbsPage zListItem(Action action, Button option, String item)
	throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption ,String item)
	throws HarnessException {
		// TODO Auto-generated method stub
		return null;	
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

			locator = Locators.SEARCH_BUTTON;
			page = new PageSearchResults(MyApplication);

			// Make sure the button exists
			if ( !this.sIsElementPresent(locator) )
				throw new HarnessException("Button is not present locator="+ locator +" button="+ button);

			// FALL THROUGH

		} else if(button == Button.B_DELETE) {
			locator = Locators.DELETE_BUTTON;
			page = new DialogForDeleteOperation(this.MyApplication, null);

			// Make sure the button exists
			if ( !this.sIsElementPresent(locator) )
				throw new HarnessException("Button is not present locator="+ locator +" button="+ button);

			// FALL THROUGH
		} else{
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}

		// Default behavior, process the locator by clicking on it
		//

		// Click it
		this.zClick(locator);


		// If page was specified, make sure it is active
		if ( page != null ) {

			// This function (default) throws an exception if never active
			//page.zWaitForActive();
			SleepUtil.sleepMedium();

		}

		sMouseOut(locator);
		return (page);


	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
	throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ option +")");

		tracer.trace("Click pulldown "+ pulldown +" then "+ option);

		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (option == null)
			throw new HarnessException("Option cannot be null!");


		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if (pulldown == Button.B_GEAR_BOX) {

			if (option == Button.O_DELETE) {

				pulldownLocator = Locators.CONFIGURE_ICON;
				optionLocator = Locators.DELETE_BUTTON;

				page = new WizardCreateAccount(this);

				// FALL THROUGH

			} else {
				throw new HarnessException("no logic defined for pulldown/option " + pulldown + "/" + option);
			}

		} else {
			throw new HarnessException("no logic defined for pulldown/option "
					+ pulldown + "/" + option);
		}

		// Default behavior
		if (pulldownLocator != null) {

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown + " option " + option + " pulldownLocator " + pulldownLocator + " not present!");
			}

			this.zClickAt(pulldownLocator,"");

			// If the app is busy, wait for it to become active
			//zWaitForBusyOverlay();

			if (optionLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("Button " + pulldown + " option " + option + " optionLocator " + optionLocator + " not present!");
				}

				this.zClickAt(optionLocator,"");

				// If the app is busy, wait for it to become active
				//zWaitForBusyOverlay();
			}

		}

		// Return the specified page, or null if not set
		return (page);
	}

	/**
	 * Return a list of all accounts in the current view
	 * @return
	 * @throws HarnessException 
	 * @throws HarnessException 
	 */
	public List<AccountItem> zListGetAccounts() throws HarnessException {

		List<AccountItem> items = new ArrayList<AccountItem>();

		// Make sure the button exists
		if ( !this.sIsElementPresent("css=div[id='zl__SEARCH_MANAGE'] div[id$='__rows']") )
			throw new HarnessException("Account Rows is not present");

		// How many items are in the table?
		String rowsLocator = "css=div#zl__SEARCH_MANAGE div[id$='__rows'] div[id^='zli__']";
		int count = this.sGetCssCount(rowsLocator);
		logger.debug(myPageName() + " zListGetAccounts: number of accounts: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {
			final String accountLocator = rowsLocator + ":nth-child("+i+")";
			String locator;

			AccountItem item = new AccountItem();

			// Type (image)
			// ImgAdminUser ImgAccount ImgSystemResource (others?)
			locator = accountLocator + " td[id^='SEARCH_MANAGE_data_type'] div";
			if ( this.sIsElementPresent(locator) ) {
				item.setGAccountType(this.sGetAttribute(locator + "@class"));
			}


			// Email Address
			locator = accountLocator + " td[id^='SEARCH_MANAGE_data_emailaddress']";
			if ( this.sIsElementPresent(locator) ) {
				item.setGEmailAddress(this.sGetText(locator).trim());
			}

			// Display Name
			// Status
			// Lost Login Time
			// Description


			// Add the new item to the list
			items.add(item);
			logger.info(item.prettyPrint());
		}

		// Return the list of items
		return (items);
	}


}
