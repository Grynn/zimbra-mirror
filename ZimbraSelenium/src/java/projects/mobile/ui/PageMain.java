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

	public static final String _logout = "xpath=//*[@id='_logout']";
	
	public static final String mainCopyright = "xpath=//div[@id='copyright_notice']";
	public static final String mainCopyrightText = "xpath=//div[@id='copyright_notice']//a";
	
	public static final String appbarMail = "xpath=//div[@id='appbar']//a[@id='mail']";
	public static final String appbarContact = "xpath=//div[@id='appbar']//a[@id='contact']";
	public static final String appbarCal = "xpath=//div[@id='appbar']//a[@id='cal']";
	public static final String appbarDocs = "xpath=//div[@id='appbar']//a[@id='docs']";
	public static final String appbarSearch = "xpath=//div[@id='appbar']//a[@id='search']";
	
	public static final String compose = "xpath=//a[@href='zmain?st=newmail']";
	
	public static final String preferences = "xpath=//a[@href='?st=prefs']";
	
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
		boolean present = sIsElementPresent(_logout);
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

		if ( !sIsElementPresent(_logout) ) {
			throw new HarnessException("The logoff button is not present " + _logout);
		}
				
		// Click on logout
		sClick(_logout);
				
		MyApplication.zPageLogin.waitForActive();
		
		MyApplication.setActiveAcount(null);

	}
	

}
