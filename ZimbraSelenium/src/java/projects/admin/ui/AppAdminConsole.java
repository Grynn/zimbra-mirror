package projects.admin.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import framework.util.HarnessException;

/**
 * This class defines the Admin Console application
 * @author Matt Rhoades
 *
 */
public class AppAdminConsole extends AbsApplication {

	public Map<String, AbsPage> pages = null;
	public LoginPage	zLoginPage = null;
	public MainPage		zMainPage = null;
	
	public AppAdminConsole() {
		super();
		
		logger.info("new " + MainPage.class.getCanonicalName());

		pages = new HashMap<String, AbsPage>();
		
		zLoginPage = new LoginPage(this);
		pages.put(LoginPage.PageName, zLoginPage);
		
		zMainPage = new MainPage(this);
		pages.put(MainPage.PageName, zMainPage);

	}

	/**
	 * Return a list of active pages
	 * @return
	 * @throws HarnessException
	 */
	public List<AbsPage> getActivePages() throws HarnessException {
		List<AbsPage> actives = new ArrayList<AbsPage>();
		for (AbsPage p : pages.values()) {
			if ( p.isActive() ) {
				actives.add(p);
			}
		}
		return (actives);
	}
}
