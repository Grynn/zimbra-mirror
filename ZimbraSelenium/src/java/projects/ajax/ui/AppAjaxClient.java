/**
 * 
 */
package projects.ajax.ui;

import framework.ui.AbsApplication;
import framework.util.HarnessException;
import framework.util.ZimbraAccount;

/**
 * @author Matt Rhoades
 *
 */
public class AppAjaxClient extends AbsApplication {
	
	public PageLogin					zPageLogin = null;
	public PageMain						zPageMain = null;
	public PageMail						zPageMail = null;
	public PagePreferences				zPagePreferences = null;
	
	public TreePreferences				zTreePreferences = null;
	
	public AppAjaxClient() {
		super();
		
		logger.info("new " + AppAjaxClient.class.getCanonicalName());
		
		// Login page
		
		zPageLogin = new PageLogin(this);
		pages.put(zPageLogin.myPageName(), zPageLogin);
		
		// Main page
		zPageMain = new PageMain(this);
		pages.put(zPageMain.myPageName(), zPageMain);
		
		// Mail page
		zPageMail = new PageMail(this);
		pages.put(zPageMail.myPageName(), zPageMail);
		
		// Preferences page
		zPagePreferences = new PagePreferences(this);
		pages.put(zPagePreferences.myPageName(), zPagePreferences);

		zTreePreferences = new TreePreferences(this);
		trees.put(zTreePreferences.myPageName(), zTreePreferences);
		
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
		return ("Ajax Client");
	}

	protected ZimbraAccount setActiveAcount(ZimbraAccount account) throws HarnessException {
		return (super.setActiveAcount(account));
	}

}
