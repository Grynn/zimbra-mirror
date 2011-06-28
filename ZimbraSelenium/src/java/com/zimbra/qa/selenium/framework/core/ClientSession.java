package com.zimbra.qa.selenium.framework.core;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;


/**
 * A <code>ClientSession</code> object contains all session information for the test methods.
 * <p>
 * The Zimbra Selenium harness is designed to  
 * execute test cases concurrently at the class level.
 * 
 * The {@link ClientSession} objects maintain all session information on 
 * a per thread basis, such as the current DefaultSelenium object.  Each 
 * TestNG thread uses a single {@link ClientSession} Object.
 * <p>
 * Use the {@link ClientSessionFactory} to retrieve the current {@link ClientSession}.
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public class ClientSession {
	public static final String IE9="MSIE 9";
	public static final String IE8="MSIE 8";
	
	private static Logger logger = LogManager.getLogger(ClientSession.class);
	
	private String name;	// A unique string identifying this session
	
	private ZimbraSelenium selenium = null;
	private String applicationURL = ZimbraSeleniumProperties.getStringProperty("server.scheme", "http") 
	+ "://" + ZimbraSeleniumProperties.getStringProperty("server.host", "localhost"); 
	private ZimbraAccount currentAccount = null;

	protected ClientSession() {
		logger.info("New ClientSession");
		
		name = "ClientSession-" + Thread.currentThread().getName();
		
	}
	
	/**
	 * Get the current ZimbraSelenium (DefaultSelenium) object
	 * <p>
	 * @return
	 */
	public ZimbraSelenium selenium() {
		if ( selenium == null ) {
			selenium = new ZimbraSelenium(
							SeleniumService.getInstance().getSeleniumServer(), 
							SeleniumService.getInstance().getSeleniumPort(),
							SeleniumService.getInstance().getSeleniumBrowser(), 
							applicationURL);
		}
		return (selenium);
	}
	
	/**
	 * Get the current Browser Name
	 * <p>
	 * @return
	 */
	@Deprecated()
	public String currentBrowserName() {
		return (ClientSessionFactory.session().selenium().getEval("navigator.userAgent;"));
	}

	/**
	 * Get the currently logged in user name
	 * <p>
	 * @return
	 */
	public String currentUserName() {
		if ( currentAccount == null ) {
			return ("");
		}
		return (currentAccount.EmailAddress);
	}
	
	/**
	 * NOT FOR TEST CASE USE.  Set the currently logged in user name.
	 * <p>
	 * This method should only be used by the AppPage LoginPage object.
	 * <p>
	 * TODO: once projects.zcs.* and projects.html.* are converted to this
	 * mechanism, need to make this method "protected" rather than "public"
	 * <p>
	 * @param account
	 * @return
	 */
	public String setCurrentUser(ZimbraAccount account) {
		currentAccount = account;
		return (currentUserName());
	}
	
	/**
	 * A unique string ID for this ClientSession object
	 */
	public String toString() {
		logger.debug("ClientSession.toString()="+ name);
		return (name);
	}

	
		

}
