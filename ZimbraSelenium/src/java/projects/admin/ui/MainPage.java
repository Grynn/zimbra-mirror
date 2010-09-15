package projects.admin.ui;

import framework.util.HarnessException;

/**
 * This class defines the top menu bar of the admin console application
 * @author Matt Rhoades
 *
 */
public class MainPage extends AbsPage {

	public static final String skin_container_logo		= "xpath=//*[@id='skin_container_logo']";
	public static final String Zskin_container_username	= "xpath=//*[@id='skin_container_username']";
	public static final String Zskin_container_logoff	= "css=table[class=skin_table] span[onclick=ZaZimbraAdmin.logOff();]"; 
	public static final String Zskin_container_help		= "xpath=//*[@id='skin_container_help']";
	public static final String Zskin_container_dw		= "xpath=//*[@id='skin_container_dw']";

	public MainPage(AbsApplication application) {
		super(application);
		
		logger.info("new " + myPageName());
	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/**
	 * If the "Logout" button is visible, assume the MainPage is active
	 */
	public boolean isActive() throws HarnessException {
		
		// Make sure the Admin Console is loaded in the browser
		if ( !MyApplication.isLoaded() )
			throw new HarnessException("Admin Console application is not active!");
		

		// Look for the Logo 
		boolean present = isElementPresent(Zskin_container_logoff);
		if ( !present ) {
			logger.debug("isActive() present = "+ present);
			return (false);
		}
		

		// Look for the Logout button. 
		boolean visible = isVisiblePerPosition(Zskin_container_logoff, 0, 0);
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
			// This page is already active
			return;
		}
		
		
		// 1. Logout
		// 2. Login as the default account
		if ( !MyApplication.zLoginPage.isActive() ) {
			MyApplication.zLoginPage.navigateTo();
		}
		MyApplication.zLoginPage.login();

		waitForActive();
		
	}

	/**
	 * Click the logout button
	 * @throws HarnessException
	 */
	public void logout() throws HarnessException {
		logger.debug("logout()");
		
		navigateTo();

		if ( !isElementPresent(Zskin_container_logoff) ) {
			throw new HarnessException("The logoff button is not present " + Zskin_container_logoff);
		}
		
		if ( !isVisiblePerPosition(Zskin_container_logoff, 10, 10) ) {
			throw new HarnessException("The logoff button is not visible " + Zskin_container_logoff);
		}
		
		// Click on logout
		click(Zskin_container_logoff);
		
		// TODO: Sometimes there is a popup.  Can it be disabled?
		
		MyApplication.zLoginPage.waitForActive();
		
		MyApplication.setActiveAcount(null);

	}
	
	public String getContainerUsername() throws HarnessException {
		logger.debug("getLoggedInAccount()");
		
		if ( !isActive() )
			throw new HarnessException("MainPage is not active");

		String username = getText(Zskin_container_username);	
		return (username);
		
	}



}
