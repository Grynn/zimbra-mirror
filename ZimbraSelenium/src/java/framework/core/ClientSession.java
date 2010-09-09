package framework.core;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.util.ZimbraAccount;

/**
 * This class defines all stateful information for test methods
 *  
 * Save information here to ensure thread-safe execution
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

	public ClientSession() {
		logger.info("New ClientSession");
		
		name = "ClientSession-" + Thread.currentThread().getName();
		
	}
	
	/**
	 * Get the current ZimbraSelenium (DefaultSelenium) object
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
	 * 
	 * This method should only be used by the AppPage LoginPage object.
	 * 
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
