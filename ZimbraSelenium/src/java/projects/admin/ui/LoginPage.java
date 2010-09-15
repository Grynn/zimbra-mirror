package projects.admin.ui;

import framework.util.HarnessException;
import framework.util.ZimbraAccount;
import framework.util.ZimbraAdminAccount;

/**
 * This class defines the login page
 * @author Matt Rhoades
 *
 */
public class LoginPage extends AbsPage {
	
	public static final String ZLoginDialog = "xpath=//div[@class='ZaLoginDialog']";
	public static final String ZLoginUserName = "xpath=//*[@id='ZLoginUserName']";
	public static final String ZLoginPassword = "xpath=//*[@id='ZLoginPassword']";
	public static final String ZLoginButtonContainer = "xpath=//*[@id='ZLoginButton']";
	public static final String ZLoginLicenseContainer = "xpath=//*[@id='ZLoginLicenseContainer']";

	/**
	 * An object that controls the Admin Console Login Page
	 */
	public LoginPage(AbsApplication application) {
		super(application);
		
		logger.info("new " + myPageName());
	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/**
	 * If the "Login" button is visible, assume the LoginPage is active
	 */
	public boolean isActive() throws HarnessException {

		// Make sure the application is loaded first
		if ( !MyApplication.isLoaded() )
			throw new HarnessException("Admin Console application is not active!");


		// Look for the login button. 
		boolean present = isElementPresent(ZLoginButtonContainer);
		if ( !present ) {
			logger.debug("isActive() present = "+ present);
			return (false);
		}
		
		boolean visible = isVisiblePerPosition(ZLoginButtonContainer, 0 , 0);
		if ( !visible ) {
			logger.debug("isActive() visible = "+ visible);
			return (false);
		}
		
		logger.debug("isActive() = "+ true);
		return (true);
	}
		
	@Override
	public void navigateTo() throws HarnessException {
		
		if ( isActive() ) {
			// This page is already active.
			return;
		}
		
		
		// Logout
		if ( MyApplication.zMainPage.isActive() ) {
			MyApplication.zMainPage.logout();
		}
		
		waitForActive();
	}


	/**
	 * Login as the GlobalAdmin
	 * @throws HarnessException
	 */
	public void login() throws HarnessException {
		logger.debug("login()");

		login(ZimbraAdminAccount.AdminConsoleAdmin());
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
		click(ZLoginButtonContainer);

		// Wait for the app to load
		MyApplication.zMainPage.waitForActive();
		
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
		
		type(ZLoginUserName, account.EmailAddress);
		type(ZLoginPassword, account.Password);
	}



}
