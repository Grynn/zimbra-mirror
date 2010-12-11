/**
 * 
 */
package projects.ajax.ui;

import framework.ui.AbsApplication;
import framework.ui.AbsSeleniumObject;
import framework.ui.Action;
import framework.ui.Button;
import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class PageMain extends AbsAjaxPage {

	public static class Locators {
		
		public static final String zLogoffButton = "xpath=//*[@id='skin_container_logoff']";
		
		public static final String zAppbarMail = "xpath=//div[@id='zb__App__Mail']";
		public static final String zAppbarContact = "xpath=//div[@id='zb__App__Contacts']";
		public static final String zAppbarCal = "xpath=//div[@id='appbar']//a[@id='zb__App__Calendar']";
		public static final String zAppbarTasks = "xpath=//div[@id='appbar']//a[@id='zb__App__Tasks']";
		public static final String zAppbarBriefcase = "css=td[id='zb__App__Briefcase_left_icon'] [class='ImgBriefcase']";
		public static final String zAppbarPreferences = "xpath=//div[@id='appbar']//a[@id='zb__App__Options']";
		
		// For Social tab, see Zimlet classes
		
	}
	
	
	public PageMain(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageMain.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the Mobile Client is loaded in the browser
		if ( !MyApplication.zIsLoaded() )
			throw new HarnessException("Admin Console application is not active!");
		

		// Look for the Logout button
		boolean present = sIsElementPresent(Locators.zLogoffButton);
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
		
		
		// 1. Logout
		// 2. Login as the default account
		if ( !MyApplication.zPageLogin.zIsActive() ) {
			MyApplication.zPageLogin.zNavigateTo();
		}
		MyApplication.zPageLogin.zLogin();

		zWaitForActive();
		
	}

	/**
	 * Click the logout button
	 * @throws HarnessException
	 */
	public void zLogout() throws HarnessException {
		logger.debug("logout()");
		
		zNavigateTo();

		if ( !sIsElementPresent(Locators.zLogoffButton) ) {
			throw new HarnessException("The logoff button is not present " + Locators.zLogoffButton);
		}
				
		// Click on logout
		sClick(Locators.zLogoffButton);
				
		MyApplication.zPageLogin.zWaitForActive();
		
		MyApplication.zSetActiveAcount(null);

	}

	@Override
	public AbsSeleniumObject zToolbarPressButton(Button button) throws HarnessException {

		// Q. Should the tabs or help or logout be processed here?
		// A. I don't think those are considered "toolbars", so don't handle here for now (Matt)
		throw new HarnessException("Main page does not have a Toolbar");
		
	}

	@Override
	public AbsSeleniumObject zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		throw new HarnessException("Main page does not have a Toolbar");
	}

	@Override
	public AbsSeleniumObject zListItem(Action action, String item) throws HarnessException {
		throw new HarnessException("Main page does not have lists");
	}

	@Override
	public AbsSeleniumObject zListItem(Action action, Action option, String item) throws HarnessException {
		throw new HarnessException("Main page does not have lists");
	}

	

}
