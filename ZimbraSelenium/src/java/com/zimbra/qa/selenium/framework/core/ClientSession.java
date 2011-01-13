package com.zimbra.qa.selenium.framework.core;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.ZimbraAccount;


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
	private static Logger logger = LogManager.getLogger(ClientSession.class);
	
	private String name;	// A unique string identifying this session
	
	private ZimbraSelenium selenium = null;
	private String applicationURL = "http://qa60.lab.zimbra.com"; // TODO: Get this from properties?
	private String currentBrowserName = null;
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
	public String currentBrowserName() {
		if ( currentBrowserName == null ) {
			BrowserUtil util = new BrowserUtil();
			currentBrowserName = util.getBrowserName();
		}
		return (currentBrowserName);
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

	
	private class BrowserUtil extends SelNGBase {
		private String userAgent = null;

		private void setBrowserAgent() {
			if (userAgent == null) {
				userAgent = ClientSessionFactory.session().selenium().getEval("navigator.userAgent;");
				if ( userAgent.equals("") ) {
					userAgent = null;
				}
			}
		}

		/**
		 * Get the browser name
		 * @return the browser name
		 */
		public String getBrowserName() {
			setBrowserAgent();
			String browserName = "";
			if (userAgent.indexOf("Firefox/") >= 0){
				browserName = "FF " + userAgent.split("Firefox/")[1];
				String[] temp = browserName.split(" ");
				browserName = temp[0]+ " "+ temp[1];
			} else if (userAgent.indexOf("MSIE") >= 0) {
				String[] arry = userAgent.split(";");
				for (int t = 0; t < arry.length; t++) {
					if (arry[t].indexOf("MSIE") >= 0) {
						browserName = arry[t];
						break;
					}
				}
			} else if (userAgent.indexOf("Chrome") >= 0) {
				String[] arry = userAgent.split("/");
				for (int t = 0; t < arry.length; t++) {
					if (arry[t].indexOf("Chrome") >= 0) {
						String [] tmp = arry[t].split(" ");
						browserName = tmp[1] + " " +tmp[0];
						break;
					}
				}
			}else if (userAgent.indexOf("Safari") >= 0) {
				String[] arry = userAgent.split("/");
				for (int t = 0; t < arry.length; t++) {
					if (arry[t].indexOf("Safari") >= 0) {
						String [] tmp = arry[t].split(" ");
						browserName = tmp[1] + " " +tmp[0];
						break;
					}
				}
			}
			return browserName;
		}
		
	}

}
