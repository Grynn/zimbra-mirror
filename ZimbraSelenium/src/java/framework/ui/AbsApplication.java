package framework.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
	
	// Localization bundles for the currently logged in user
	protected I18N L10N = null;

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


	protected ZimbraAccount setActiveAcount(ZimbraAccount account) throws HarnessException {
		
		if ( !account.equals(authenticatedAccount) ) {
			
			logger.info("New authenticated account = "+ account.EmailAddress);
			
			// Remember who is logged in
			authenticatedAccount = account;
			
			// Based on account settings, the localization strings will change
			L10N = new I18N(authenticatedAccount.getPreference("zimbraPrefLocale"));
			
		}
		
		return (authenticatedAccount);
	}
	
	public ZimbraAccount getActiveAccount() {
		return (authenticatedAccount);
	}

	/**
	 * Get the Localized string for the specified key
	 * @param key
	 * @return
	 * @throws HarnessException
	 */
	public String getLocaleString(String key) throws HarnessException {
		
		if ( L10N == null ) {
			// If we are on a non-authenticated page, return the default L10N values
			return (defaultL10N.getString(key));
		}
		return (L10N.getString(key));
	}
	// Use the default java locale for non-authenticated screens
	// Most of the time, this will be English
	private static I18N defaultL10N = new I18N(Locale.getDefault()); 

}
