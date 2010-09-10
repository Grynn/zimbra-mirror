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
	
	public static final String PageName = "LoginPage";

	public static final String ZLoginUserName = "xpath=//*[@id='ZLoginUserName']";
	public static final String ZLoginPassword = "xpath=//*[@id='ZLoginPassword']";
	public static final String ZLoginButtonContainer = "xpath=//*[@id='ZLoginButton']";

	/**
	 * An object that controls the Admin Console Login Page
	 */
	public LoginPage(AbsApplication application) {
		super(application);
		
		logger.info("new " + LoginPage.class.getCanonicalName());
	}
	
	/**
	 * If the "Login" button is visible, assume the LoginPage is active
	 */
	public boolean isActive() throws HarnessException {
		
		// Look for the login button. 
		boolean active = isVisible(ZLoginButtonContainer);
		
		logger.debug("isActive() = "+ active);
		return (active);
	}
		
	/**
	 * Login as the GlobalAdmin
	 * @throws HarnessException
	 */
	public void login() throws HarnessException {
		logger.debug("login()");

		login(ZimbraAdminAccount.GlobalAdmin());
	}
	
	/**
	 * Login as the specified account
	 * @param account
	 * @throws HarnessException
	 */
	public void login(ZimbraAccount account) throws HarnessException {
		logger.debug("login(ZimbraAccount account)" + account.EmailAddress);

		if ( !isActive() )
			throw new HarnessException("LoginPage is not active");
		
		// Fill out the form
		fillFields(account);
		
		// Click the Login button
		super.click(ZLoginButtonContainer);

		// Wait for the app to load
		MyApplication.zMainPage.waitForActive(30000);
		
	}
	
	/**
	 * Fill the form with the specified user
	 * @throws HarnessException
	 */
	public void fillFields(ZimbraAccount account) throws HarnessException {
		logger.debug("fillFields(ZimbraAccount account)" + account.EmailAddress);
		
		if ( !isActive() )
			throw new HarnessException("LoginPage is not active");
		
		super.type(ZLoginUserName, account.EmailAddress);
		super.type(ZLoginPassword, account.Password);
	}


}
