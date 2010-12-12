/**
 * 
 */
package projects.ajax.ui.preferences;

import projects.ajax.ui.AbsAjaxPage;
import projects.ajax.ui.PageMain;
import projects.ajax.ui.PageMain.Locators;
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
public class PagePreferences extends AbsAjaxPage {

	
	public static class Locators {
		
		// Preferences Toolbar: Save, Cancel
		public static final String zToolbarSaveID = "zb__PREF__SAVE_title";
		public static final String zToolbarCancelID = "zb__PREF__CANCEL_title";

		public static final String zSaveChangesYes = "id=DWT241_title";
		public static final String zSaveChangesNo = "id=DWT242_title";
		public static final String zSaveChangesCancel = "id=DWT243_title";
		

	}
	
	



	public PagePreferences(AbsApplication application) {
		super(application);
		
		logger.info("new " + PagePreferences.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the main page is active
		if ( !this.MyApplication.zPageMain.zIsActive() ) {
			this.MyApplication.zPageMain.zNavigateTo();
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
	public void zNavigateTo() throws HarnessException {

		// Check if this page is already active.
		if ( zIsActive() ) {
			return;
		}
		
		// Make sure we are logged into the Mobile app
		if ( !MyApplication.zPageMain.zIsActive() ) {
			MyApplication.zPageMain.zNavigateTo();
		}
		
		// Click on Preferences icon
		if ( !sIsElementPresent(PageMain.Locators.zAppbarPreferences) ) {
			throw new HarnessException("Can't locate preferences icon");
		}

		zClick(PageMain.Locators.zAppbarPreferences);
		
		zWaitForActive();

	}

	/**
	 * Click "Cancel" to navigate away from preferences
	 * @throws HarnessException 
	 */
	public void zNavigateAway(Button savechanges) throws HarnessException {
		logger.info("zNavigateAway(" + savechanges +")");

		// See also bug 53203

		// Click Cancel
		zToolbarPressButton(Button.B_CANCEL);

		// Check if the "Would you like to save your changes?" appears
		//
		
		// Wait for the dialog to appear
		SleepUtil.sleep(5000);
		
		// Check for the dialog
		if ( zIsVisiblePerPosition("id=DWT240", 420, 200) ) {
			logger.debug("zNavigateAway(" + savechanges +") - dialog is showing");

			String locator = null;
			
			// "Would you like to save your changes?" is displayed.  
			if ( savechanges == Button.B_YES ) {
				locator = Locators.zSaveChangesYes;
			} else if ( savechanges == Button.B_NO ) {
				locator = Locators.zSaveChangesNo;
			} else if ( savechanges == Button.B_CANCEL ) {
				locator = Locators.zSaveChangesCancel;
			} else {
				throw new HarnessException("zNavigateAway() not defined for button "+ savechanges);
			}
			
			if ( locator == null ) {
				throw new HarnessException("zNavigateAway() no locator for button "+ savechanges);
			}
			
			if ( !sIsElementPresent(locator) ) {
				throw new HarnessException("zNavigateAway() locator is not present "+ locator);
			}
			
			zClick(locator);
			
		} else {
			logger.debug("zNavigateAway(" + savechanges +") - dialog did not show");
		}
		
		
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
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");
		
		if ( button == null )
			throw new HarnessException("Button cannot be null!");
		
				
		// Default behavior variables
		//
		String locator = null;			// If set, this will be clicked
		AbsSeleniumObject page = null;	// If set, this page will be returned
		
		// Based on the button specified, take the appropriate action(s)
		//
		
		if ( button == Button.B_SAVE ) {
			
			locator = "id="+ Locators.zToolbarSaveID;
			page = null;
			
		} else if ( button == Button.B_CANCEL ) {
			
			locator = "id="+ Locators.zToolbarCancelID;
			page = null;
						
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
		throw new HarnessException(myPageName() + " does not have a Toolbar");
	}

	




}
