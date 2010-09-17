/**
 * 
 */
package projects.mobile.ui;

import framework.ui.AbsApplication;
import framework.util.HarnessException;
import framework.util.ZimbraAccount;

/**
 * @author Matt Rhoades
 *
 */
public class AppMobileClient extends AbsApplication {
	
	public PageLogin					zPageLogin = null;
	public PageMain						zPageMain = null;
	
	public AppMobileClient() {
		super();
		
		logger.info("new " + AppMobileClient.class.getCanonicalName());
		
		// Login page
		
		zPageLogin = new PageLogin(this);
		pages.put(zPageLogin.myPageName(), zPageLogin);
		
		// Main page
		zPageMain = new PageMain(this);
		pages.put(zPageMain.myPageName(), zPageMain);
		
	}
	
	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsApplication#isLoaded()
	 */
	@Override
	public boolean isLoaded() throws HarnessException {
		// TODO: Need to define this method
		return (true);
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsApplication#myApplicationName()
	 */
	@Override
	public String myApplicationName() {
		return ("Mobile Client");
	}

	protected ZimbraAccount setActiveAcount(ZimbraAccount account) throws HarnessException {
		return (super.setActiveAcount(account));
	}

}
