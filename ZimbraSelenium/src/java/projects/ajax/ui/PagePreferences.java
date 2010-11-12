/**
 * 
 */
package projects.ajax.ui;

import projects.ajax.ui.Actions.Action;
import projects.ajax.ui.Buttons.Button;
import framework.ui.AbsApplication;
import framework.ui.AbsSeleniumObject;
import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class PagePreferences extends AbsAjaxPage {

	
	public static class Locators {
		

	}
	
	



	public PagePreferences(AbsApplication application) {
		super(application);
		
		logger.info("new " + PagePreferences.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean isActive() throws HarnessException {

		// Make sure the main page is active
		if ( !this.MyApplication.zPageMain.isActive() ) {
			this.MyApplication.zPageMain.navigateTo();
		}
		
		/*
		 * Active:
		 * <div id="zov__main_Options" style="position: absolute; overflow: auto; z-index: 300; left: 0px; top: 76px; width: 169px; height: 537px;" class="ZmOverview" parentid="z_shell">
		 * 
		 * Not active:
		 * <div id="zov__main_Options" style="position: absolute; overflow: auto; z-index: 300; left: -10000px; top: -10000px; width: 169px; height: 537px;" class="ZmOverview" parentid="z_shell">
		 */
		
		
		// If the "folders" tree is visible, then mail is active
		String locator = "xpath=//div[@id='zov__main_Options']";
		
		boolean loaded = this.sIsElementPresent(locator);
		if ( !loaded )
			return (loaded);
		
		boolean active = this.zIsVisiblePerPosition(locator, -1, 74);
		return (active);

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
	public void navigateTo() throws HarnessException {

		// Check if this page is already active.
		if ( isActive() ) {
			return;
		}
		
		// Make sure we are logged into the Mobile app
		if ( !MyApplication.zPageMain.isActive() ) {
			MyApplication.zPageMain.navigateTo();
		}
		
		// Click on Preferences icon
		zClick(PageMain.Locators.zAppbarPreferences);
		
		waitForActive();

	}

	/**
	 * Determine if a checkbox is checked or not
	 * @param preference the Account preference to check
	 * @return true if checked, false if not checked
	 * @throws HarnessException
	 */
	public boolean zGetCheckboxStatus(String preference) throws HarnessException {
		logger.info("zGetCheckboxStatus(" + preference +")");

		String locator = null;
		
		if ( preference.equals("zimbraPrefIncludeSpamInSearch")) {
			
			locator = "//input[contains(@id,'_SEARCH_INCLUDES_SPAM')]";

		} else if (preference.equals("zimbraPrefIncludeTrashInSearch")) {
			
			locator = "//input[contains(@id,'_SEARCH_INCLUDES_TRASH')]";

		} else if (preference.equals("zimbraPrefShowSearchString")) {

			locator = "//input[contains(@id,'_SHOW_SEARCH_STRING')]";

		} else {
			throw new HarnessException("zGetCheckboxStatus() not defined for preference "+ preference);
		}
		
		if ( locator == null ) {
			throw new HarnessException("locator not defined for preference "+ preference);
		}
		
		if ( !sIsElementPresent(locator) ) {
			throw new HarnessException("locator not present "+ locator);
		}
		
		boolean checked = sIsChecked(locator);
		logger.info("zGetCheckboxStatus(" + preference +") = "+ checked);
		
		return (checked);

	}
	
	@Override
	public AbsSeleniumObject zListItem(Action action, String item) throws HarnessException {
		throw new HarnessException(myPageName() + " does not have a Toolbar");
	}

	@Override
	public AbsSeleniumObject zListItem(Action action, Action option, String item) throws HarnessException {
		throw new HarnessException(myPageName() + " does not have a Toolbar");
	}

	@Override
	public AbsSeleniumObject zToolbarPressButton(Button button) throws HarnessException {
		throw new HarnessException(myPageName() + " does not have a Toolbar");
	}

	@Override
	public AbsSeleniumObject zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		throw new HarnessException(myPageName() + " does not have a Toolbar");
	}
	
	




}
