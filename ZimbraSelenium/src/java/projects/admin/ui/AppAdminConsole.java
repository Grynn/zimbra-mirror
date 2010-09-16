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

	public Map<String, AbsPage>		pages = null;
	public PageLogin				zPageLogin = null;
	public PageMain					zPageMain = null;
	public PageSearchResults		zPageSearchResults = null;

	public PageManageAccounts		zPageManageAccounts = null;
	public PageEditAccount			zPageEditAccount = null;

	
	
	public AppAdminConsole() {
		super();
		
		logger.info("new " + PageMain.class.getCanonicalName());

		pages = new HashMap<String, AbsPage>();
		
		zPageLogin = new PageLogin(this);
		pages.put(zPageLogin.myPageName(), zPageLogin);
		
		zPageMain = new PageMain(this);
		pages.put(zPageMain.myPageName(), zPageMain);

		zPageSearchResults = new PageSearchResults(this);
		pages.put(zPageSearchResults.myPageName(), zPageSearchResults);

		zPageManageAccounts = new PageManageAccounts(this);
		pages.put(zPageManageAccounts.myPageName(), zPageManageAccounts);

	}


	@Override
	public boolean isLoaded() throws HarnessException {
		// TODO: how to determine if the current browser app is the AdminConsole
		// Maybe check the current URL?
		return (true);
	}

	@Override
	public String myApplicationName() {
		return ("Admin Console");
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
