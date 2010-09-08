package framework.core;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ClientSession {
	private static Logger logger = LogManager.getLogger(ClientSession.class);
	
	private String name;	// A unique string identifying this session
	
	private ZimbraSelenium selenium = null;
	private String applicationURL = "http://qa60.lab.zimbra.com"; // TODO: Get this from properties?
	private String currentBrowserName = null;

	public ClientSession() {
		logger.info("New ClientSession");
		
		name = "ClientSession-" + Thread.currentThread().getName();
		
	}
	
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
		
	public String currentBrowserName() {
		if ( currentBrowserName == null ) {
			BrowserUtil util = new BrowserUtil();
			currentBrowserName = util.getBrowserName();
		}
		return (currentBrowserName);
	}
	
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
