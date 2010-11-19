package framework.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import projects.ajax.ui.PageMail;
import projects.ajax.ui.PageMain;
import framework.util.HarnessException;
import framework.util.ZimbraAccount;

/**
 * A <code>AbsApplication</code> object defines a Zimbra "mail client", such as
 * the Ajax client, HTML client, Mobile client, Desktop client, and Admin console.
 * <p>
 * The <code>Application</code> object is the main access point
 * for the test methods to access the GUI.  The application 
 * contains a list of  {@link AbsPage} objects, {@link AbsTree}
 * objects, and other GUI objects.  Test case methods contain an
 * instance of the application and can access the GUI pages using
 * the application object.  The application is initialized during
 * test class construction.
 * <p>
 * Additionally, the <code>Application</code> object has data
 * specific to the current running client, such as currently logged in 
 * {@link ZimbraAccount}, the current localization {@link I18N}, etc.
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsApplication {
	protected static Logger logger = LogManager.getLogger(AbsApplication.class);
	
	/**
	 * A map of {@link AbsPage} objects being managed by this object
	 **/
	protected Map<String, AbsPage>			pages = null;

	/**
	 * A map of {@link AbsTree} objects being managed by this object
	 **/
	protected Map<String, AbsTree>			trees = null;
	
	/**
	 * The currently logged in user
	 **/
	private ZimbraAccount authenticatedAccount = null;
	
	/**
	 * The Localization bundles for the currently logged in user
	 **/
	protected I18N L10N = null;

	protected AbsApplication() {
		logger.info("new " + AbsApplication.class.getCanonicalName());
		
		pages = new HashMap<String, AbsPage>();
		trees = new HashMap<String, AbsTree>();

	}
	
	/**
	 * A unique string per class used to identify the implementing application class
	 * @return
	 */
	public abstract String myApplicationName();

	/**
	 * Determines whether this application is currently loaded in the browser
	 * @return true if loaded, false otherwise
	 * @throws HarnessException
	 */
	public abstract boolean isLoaded() throws HarnessException;

	/**
	 * Return a list of active pages
	 * <p>
	 * For instance, when the {@link AppAjaxClient} is displaying the
	 * {@link PageMail} mail view, this method will return {@link PageMail},
	 * {@link PageMain}, {@link TreeMail} objects.
	 * @see <a href="http://wiki.zimbra.com/wiki/Testing:_Selenium:_ZimbraSelenium_Overview#Mail_Page">Screen reference</a>
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


	/**
	 * Set the currently authenticated account
	 * <p>
	 * This method is used by the implementing classes to set
	 * the authenticated account.
	 * <p>
	 * <b>This method should not be used by the test methods.</b>
	 * <p>
	 * @return the authenticated account
	 */
	protected ZimbraAccount setActiveAcount(ZimbraAccount account) throws HarnessException {
		
		// Check if we are setting the active account to nobody
		if ( account == null ) {
			logger.info("Set authenticated account to null");
			authenticatedAccount = null;
			L10N = null;
			return (null);
		}
		
		// Check if we are setting the active account to the already set account
		if ( account.equals(authenticatedAccount)) {
			logger.info("Same authenticated account = "+ account.EmailAddress);
			return (authenticatedAccount);
		}
							
		logger.info("New authenticated account = "+ account.EmailAddress);
		
		// Remember who is logged in
		authenticatedAccount = account;
		
		// Based on account settings, the localization strings will change
		L10N = new I18N(authenticatedAccount.getPreference("zimbraPrefLocale"));
					
		return (authenticatedAccount);
	}
	
	/**
	 * Return the currently logged in account
	 * @return null if no account is authenticated
	 */
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
