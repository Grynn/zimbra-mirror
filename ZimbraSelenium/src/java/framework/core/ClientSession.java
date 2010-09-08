package framework.core;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.util.BrowserUtil;

public class ClientSession {
	private static Logger logger = LogManager.getLogger(ClientSession.class);
	
	private String name;	// A unique string identifying this session
	
	private ZimbraSelenium selenium = null;
	private String applicationURL = "http://qa60.lab.zimbra.com"; // TODO: Get this from properties;
	private String currentBrowserName = "";

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
	
	public String setApplicationUrl(String url) {
		applicationURL = url;
		return (applicationURL);
	}
	
	public String getCurrentBrowserName() {
		currentBrowserName = BrowserUtil.getBrowserName();
		return (currentBrowserName);
	}
	
	public String toString() {
		logger.debug("ClientSession.toString()="+ name);
		return (name);
	}

}
