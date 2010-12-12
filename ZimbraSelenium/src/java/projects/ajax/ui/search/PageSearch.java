/**
 * 
 */
package projects.ajax.ui.search;

import projects.ajax.ui.AbsAjaxPage;
import framework.ui.AbsApplication;
import framework.ui.AbsSeleniumObject;
import framework.ui.Button;
import framework.ui.Action;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.SleepUtil;

/**
 * @author Matt Rhoades
 *
 */
public class PageSearch extends AbsAjaxPage {

	public static class Locators {
		
		public static final String zActiveLocator = "id=zb__Search__SEARCH";
		
		public static final String zSearchInput = "//input[@class='search_input']";
		public static final String zSearchButton = "id=zb__Search__SEARCH_title";
		
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
		

		// Look for the Logout button
		boolean present = sIsElementPresent(Locators.zActiveLocator);
		if ( !present ) {
			logger.debug("isActive() present = "+ present);
			return (false);
		}
		
		logger.debug("isActive() = "+ true);
		return (true);

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
		if ( !MyApplication.zPageMain.zIsActive() ) {
			MyApplication.zPageMain.zNavigateTo();
		}

		// Nothing more to do to make search appear, since it is always active if the app is active
		
		zWaitForActive();
		
	}

	@Override
	public AbsSeleniumObject zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");
		
		if ( button == null )
			throw new HarnessException("Button cannot be null!");
		
				
		// Default behavior variables
		//
		String locator = null;			// If set, this will be clicked
		AbsSeleniumObject page = null;	// If set, this page will be returned
		
		// Based on the button specified, take the appropriate action(s)
		//
		
		if ( button == Button.B_SEARCHTYPE ) {
					
			throw new HarnessException("implement me!");
			
		} else if ( button == Button.B_SEARCH ) {
			
			locator = Locators.zSearchButton;
			page = null;
			
		} else if ( button == Button.B_SEARCHSAVE ) {
			
			throw new HarnessException("implement me!");
			
		} else if ( button == Button.B_SEARCHADVANCED ) {
			
			throw new HarnessException("implement me!");
			
		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}
		
		// Default behavior, process the locator by clicking on it
		//
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Button is not present locator="+ locator +" button="+ button);
		
		// Click it
		this.zClick(locator);

		return (page);
	}

	@Override
	public AbsSeleniumObject zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		throw new HarnessException("implement me");
	}

	@Override
	public AbsSeleniumObject zListItem(Action action, String item)
			throws HarnessException {
		throw new HarnessException(myPageName() + " does not have a list view");
	}

	@Override
	public AbsSeleniumObject zListItem(Action action, Action option, String item)
			throws HarnessException {
		throw new HarnessException(myPageName() + " does not have a list view");
	}

	/**
	 * Enter text into the query string field
	 * @param query
	 * @throws HarnessException 
	 */
	public void zAddSearchQuery(String query) throws HarnessException {
		logger.info(myPageName() + " zAddSearchQuery("+ query +")");
		
		this.sType(Locators.zSearchInput, query);

	}
	
	/**
	 * Execute the specified query
	 * @param query
	 * @throws HarnessException 
	 */
	public void zRunSearchQuery(String query) throws HarnessException {
		logger.info(myPageName() + " zRunSearchQuery("+ query +")");

		zAddSearchQuery(query);
		zToolbarPressButton(Button.B_SEARCH);
		
		SleepUtil.sleep(10000);
		
	}



	

}
