package projects.ajax.ui;

import framework.ui.AbsApplication;
import framework.ui.AbsPage;
import framework.ui.AbsTab;
import framework.ui.Action;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.ZimbraAccount;

public class PageLogin extends AbsTab {

	public static class Locators {

		// Buttons
		public static final String zBtnLogin = "xpath=//input[@class='zLoginButton']";
		
		// Text Input
		public static final String zInputUsername = "xpath=//*[@id='username']";
		public static final String zInputPassword = "xpath=//*[@id='password']";
		public static final String zInputRemember = "xpath=//*[@id='remember']";
		
		// Displayed text
		public static final String zDisplayedZLoginAppName = "xpath=//*[@id='ZLoginAppName']";
		public static final String zDisplayedusername = "xpath=//form[@name='loginForm']//label[@for='username']";
		public static final String zDisplayedpassword = "xpath=//td[@class='zLoginLabelContainer']//label[@for='password']";
		public static final String zDisplayedremember = "xpath=//td[@class='zLoginCheckboxLabelContainer']//label[@for='remember']";
		public static final String zDisplayedwhatsthis = "xpath=//*[@id='ZLoginWhatsThisAnchor']";
		public static final String zDisplayedcopyright = "xpath=//div[@class='copyright']";

	}
	
	
	public ZimbraAccount DefaultLoginAccount = null;
	
	public PageLogin(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageLogin.class.getCanonicalName());

	}

	@Override
	public boolean zIsActive() throws HarnessException {
		
		// Make sure the application is loaded first
		if ( !MyApplication.zIsLoaded() )
			throw new HarnessException("Admin Console application is not active!");


		// Look for the login button. 
		boolean present = sIsElementPresent(Locators.zBtnLogin);
		if ( !present ) {
			logger.debug("isActive() present = "+ present);
			return (false);
		}
		
		boolean visible = zIsVisiblePerPosition(Locators.zBtnLogin, 0 , 0);
		if ( !visible ) {
			logger.debug("isActive() visible = "+ visible);
			return (false);
		}
		
		logger.debug("isActive() = "+ true);
		return (true);
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zNavigateTo() throws HarnessException {

		if ( zIsActive() ) {
			// This page is already active.
			return;
		}
		
		
		// Logout
		if ( ((AppAjaxClient)MyApplication).zPageMain.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageMain.zLogout();
		}
		
		zWaitForActive();
		
	}

	/**
	 * Login as DefaultLoginAccount
	 * @throws HarnessException
	 */
	public void zLogin() throws HarnessException {
		logger.debug("login()");

		zLogin(DefaultLoginAccount);
	}

	
	/**
	 * Login as the specified account
	 * @param account
	 * @throws HarnessException
	 */
	public void zLogin(ZimbraAccount account) throws HarnessException {
		logger.debug("login(ZimbraAccount account)" + account.EmailAddress);

		zNavigateTo();
		
		// Fill out the form
		zSetLoginName(account.EmailAddress);
		zSetLoginPassword(account.Password);
		
		// Click the Login button
		sClick(Locators.zBtnLogin);

		// Wait for the app to load
		sWaitForPageToLoad();
		((AppAjaxClient)MyApplication).zPageMain.zWaitForActive();
		
		((AppAjaxClient)MyApplication).zSetActiveAcount(account);
		
	}
	
	/**
	 * Add the specified name to the login name field
	 * @param name
	 * @throws HarnessException
	 */
	public void zSetLoginName(String name) throws HarnessException {
		String locator = Locators.zInputUsername;
		if ( name == null ) {
			throw new HarnessException("Name is null");
		}
			
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Login field does not exist "+ locator);
		}
		sType(locator, name);
	}
	
	/**
	 * Add the specified password to the login password field
	 * @param name
	 * @throws HarnessException
	 */
	public void zSetLoginPassword(String password) throws HarnessException {
		String locator = Locators.zInputPassword;
		if ( password == null ) {
			throw new HarnessException("Password is null");
		}
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Password field does not exist "+ locator);
		}
		sType(locator, password);
	}
	

	

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		throw new HarnessException("Login page does not have a Toolbar");
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		throw new HarnessException("Login page does not have a Toolbar");
	}

	@Override
	public AbsPage zListItem(Action action, String item) throws HarnessException {
		throw new HarnessException("Login page does not have lists");
	}

	@Override
	public AbsPage zListItem(Action action, Action option, String item) throws HarnessException {
		throw new HarnessException("Login page does not have lists");
	}



}
