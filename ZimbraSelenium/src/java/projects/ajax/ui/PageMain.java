/**
 * 
 */
package projects.ajax.ui;

import framework.ui.AbsApplication;
import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class PageMain extends AbsAjaxPage {

	public static final String LLogout = "xpath=//*[@id='skin_container_logoff']";
	
	public static final String appbarMail = "xpath=//div[@id='zb__App__Mail']";
	public static final String appbarContact = "xpath=//div[@id='appbar']//a[@id='zb__App__Contacts']";
	public static final String appbarCal = "xpath=//div[@id='appbar']//a[@id='zb__App__Calendar']";
	public static final String appbarTasks = "xpath=//div[@id='appbar']//a[@id='zb__App__Tasks']";
	public static final String appbarBriefcase = "xpath=//div[@id='appbar']//a[@id='zb__App__Briefcase']";
	public static final String appbarPreferences = "xpath=//div[@id='appbar']//a[@id='zb__App__Options']";
	// For Social tab, see Zimlet classes
	
	
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
		boolean present = sIsElementPresent(LLogout);
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

		if ( !sIsElementPresent(LLogout) ) {
			throw new HarnessException("The logoff button is not present " + LLogout);
		}
				
		// Click on logout
		sClick(LLogout);
				
		MyApplication.zPageLogin.waitForActive();
		
		MyApplication.setActiveAcount(null);

	}
	

}
