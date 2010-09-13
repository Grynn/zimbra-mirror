package projects.admin.ui;

import com.zimbra.common.soap.Element;

import framework.util.HarnessException;
import framework.util.ZimbraAccount;
import framework.util.ZimbraAdminAccount;

/**
 * This class defines the login page
 * @author Matt Rhoades
 *
 */
public class LoginPage extends AbsPage {
	
	private static final String MyPageName = "LoginPage";

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
		
		logger.info("new " + LoginPage.class.getCanonicalName());
	}
	
	@Override
	public String myPageName() {
		return (MyPageName);
	}

	/**
	 * If the "Login" button is visible, assume the LoginPage is active
	 */
	public boolean isActive() throws HarnessException {
		String id = super.getSelectedId(ZLoginDialog);
		logger.info("id = "+ id);
		
		// Look for the login button. 
		boolean present = super.isElementPresent(ZLoginButtonContainer);
		if ( !present ) {
			logger.debug("isActive() present = "+ present);
			return (false);
		}
		
		// is it visible? 
		super.focus(ZLoginButtonContainer);
		boolean visible = isVisible(ZLoginButtonContainer);
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
		
		waitForActive(30000);
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
		
		MyApplication.setActiveAcount(account);
		
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
