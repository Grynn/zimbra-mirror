package projects.mobile.ui;

import framework.ui.AbsApplication;
import framework.util.HarnessException;
import framework.util.ZimbraAccount;

public class PageLogin extends AbsMobilePage {

	public static class Locators {
		
		// TODO: Should this just extend a single class for all (Ajax, HTML, Mobile) login pages?
		
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
	public boolean isActive() throws HarnessException {
		
		// Make sure the application is loaded first
		if ( !MyApplication.isLoaded() )
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
	public void navigateTo() throws HarnessException {

		if ( isActive() ) {
			// This page is already active.
			return;
		}
		
		
		// Logout
		if ( MyApplication.zPageMain.isActive() ) {
			MyApplication.zPageMain.logout();
		}
		
		waitForActive();
		
	}

	/**
	 * Login as DefaultLoginAccount
	 * @throws HarnessException
	 */
	public void login() throws HarnessException {
		logger.debug("login()");

		login(DefaultLoginAccount);
	}

	
	/**
	 * Login as the specified account
	 * @param account
	 * @throws HarnessException
	 */
	public void login(ZimbraAccount account) throws HarnessException {
		logger.debug("login(ZimbraAccount account)" + account.EmailAddress);

		navigateTo();
		
		// Fill out the form
		fillLoginFormFields(account);
		
		// Click the Login button
		sClick(Locators.zBtnLogin);

		// Wait for the app to load
		MyApplication.zPageMain.waitForActive();
		
		MyApplication.setActiveAcount(account);
		
	}
	
	/**
	 * Fill the form with the specified user
	 * @throws HarnessException
	 */
	public void fillLoginFormFields(ZimbraAccount account) throws HarnessException {
		logger.debug("fillFields(ZimbraAccount account)" + account.EmailAddress);
		
		if ( !isActive() )
			throw new HarnessException("LoginPage is not active");
		
		sType(Locators.zInputUsername, account.EmailAddress);
		sType(Locators.zInputPassword, account.Password);
	}


}
