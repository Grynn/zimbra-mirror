package projects.admin.ui;

import framework.ui.AbsApplication;
import framework.util.HarnessException;

/**
 * This class defines the top menu bar of the admin console application
 * @author Matt Rhoades
 *
 */
public class PageMain extends AbsAdminPage {

	public static class Locators {
		public static final String zSkinContainerLogo		= "xpath=//*[@id='skin_container_logo']";
		public static final String zSkinContainerUsername	= "xpath=//*[@id='skin_container_username']";
		public static final String zSkinContainerLogoff		= "css=table[class=skin_table] span[onclick=ZaZimbraAdmin.logOff();]"; 
		public static final String zSkinContainerHelp		= "xpath=//*[@id='skin_container_help']";
		public static final String zSkinContainerDW			= "xpath=//*[@id='skin_container_dw']";
	}
	
	public PageMain(AbsApplication application) {
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
		boolean present = sIsElementPresent(Locators.zSkinContainerLogoff);
		if ( !present ) {
			logger.debug("isActive() present = "+ present);
			return (false);
		}
		

		// Look for the Logout button. 
		boolean visible = zIsVisiblePerPosition(Locators.zSkinContainerLogoff, 0, 0);
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
		if ( !MyApplication.zPageLogin.isActive() ) {
			MyApplication.zPageLogin.navigateTo();
		}
		MyApplication.zPageLogin.login();

		waitForActive();
		
	}

	/**
	 * Click the logout button
	 * @throws HarnessException
	 */
	public void logout() throws HarnessException {
		logger.debug("logout()");
		
		navigateTo();

		if ( !sIsElementPresent(Locators.zSkinContainerLogoff) ) {
			throw new HarnessException("The logoff button is not present " + Locators.zSkinContainerLogoff);
		}
		
		if ( !zIsVisiblePerPosition(Locators.zSkinContainerLogoff, 10, 10) ) {
			throw new HarnessException("The logoff button is not visible " + Locators.zSkinContainerLogoff);
		}
		
		// Click on logout
		sClick(Locators.zSkinContainerLogoff);
		
		// Sometimes there is a "confirm" popup.
		// Disable it using zimbraPrefAdminConsoleWarnOnExit=FALSE
		// This is the default configureation for the AdminConsoleAdmin() account
		
		
		MyApplication.zPageLogin.waitForActive();
		
		MyApplication.setActiveAcount(null);

	}
	
	public String getContainerUsername() throws HarnessException {
		logger.debug("getLoggedInAccount()");
		
		if ( !isActive() )
			throw new HarnessException("MainPage is not active");

		String username = sGetText(Locators.zSkinContainerUsername);	
		return (username);
		
	}



}
