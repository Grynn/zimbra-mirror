package projects.admin.ui;

import framework.util.HarnessException;

/**
 * This class defines the top menu bar of the admin console application
 * @author Matt Rhoades
 *
 */
public class MainPage extends AbsPage {

	public static final String PageName = "MainPage";

	public static final String Zskin_td_logo			= "xpath=//*[@id='skin_td_logo']";
	public static final String Zskin_container_username	= "xpath=//*[@id='skin_container_username']";
	public static final String Zskin_container_logoff	= "xpath=//*[@id='skin_container_logoff']";
	public static final String Zskin_container_help		= "xpath=//*[@id='skin_container_help']";
	public static final String Zskin_container_dw		= "xpath=//*[@id='skin_container_dw']";

	public MainPage(AbsApplication application) {
		super(application);
		
		logger.info("new " + MainPage.class.getCanonicalName());
	}
	
	/**
	 * If the "Logout" button is visible, assume the MainPage is active
	 */
	public boolean isActive() throws HarnessException {
		
		// Look for the Logout button. 
		boolean active = super.isVisible(Zskin_td_logo);
		logger.debug("isActive() = "+ active);
		return (active);
	}

	/**
	 * Click the logout button
	 * @throws HarnessException
	 */
	public void logout() throws HarnessException {
		logger.debug("logout()");
		
		if ( !isActive() )
			throw new HarnessException("MainPage is not active");

		super.click(Zskin_container_logoff);

	}
	
	public String getContainerUsername() throws HarnessException {
		logger.debug("getLoggedInAccount()");
		
		if ( !isActive() )
			throw new HarnessException("MainPage is not active");

		String username = getText(Zskin_container_username);	
		return (username);
		
	}

}
