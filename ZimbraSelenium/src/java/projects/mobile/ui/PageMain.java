/**
 * 
 */
package projects.mobile.ui;

import framework.ui.AbsApplication;
import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class PageMain extends AbsMobilePage {

	private static final String _logout = "xpath=//*[@id='_logout']";
	
	public PageMain(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageMain.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean isActive() throws HarnessException {

		// Make sure the Mobile Client is loaded in the browser
		if ( !MyApplication.isLoaded() )
			throw new HarnessException("Admin Console application is not active!");
		

		// Look for the Logout button
		boolean present = isElementPresent(_logout);
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

		if ( !isElementPresent(_logout) ) {
			throw new HarnessException("The logoff button is not present " + _logout);
		}
				
		// Click on logout
		click(_logout);
				
		MyApplication.zPageLogin.waitForActive();
		
		MyApplication.setActiveAcount(null);

	}
	

}
