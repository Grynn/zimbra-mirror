package framework.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.util.HarnessException;
import framework.util.ZimbraAccount;

/**
 * This class defines an abstract Zimbra "Application"
 * 
 * Example, Admin Console, Ajax Client, HTML Client
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsApplication {
	protected static Logger logger = LogManager.getLogger(AbsApplication.class);
	
	// A map of the pages in this app
	protected Map<String, AbsPage>			pages = null;
	
	// A pointer to the currently logged in user
	private ZimbraAccount authenticatedAccount = null;

	protected AbsApplication() {
		logger.info("new " + AbsApplication.class.getCanonicalName());
		
		pages = new HashMap<String, AbsPage>();

	}
	
	public abstract String myApplicationName();

	public abstract boolean isLoaded() throws HarnessException;

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


	protected ZimbraAccount setActiveAcount(ZimbraAccount account) {
		authenticatedAccount = account;
		return (authenticatedAccount);
	}
	
	public ZimbraAccount getActiveAccount() {
		return (authenticatedAccount);
	}
}
