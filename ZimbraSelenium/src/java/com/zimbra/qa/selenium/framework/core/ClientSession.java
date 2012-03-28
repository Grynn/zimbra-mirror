package com.zimbra.qa.selenium.framework.core;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

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
	private WebDriver webDriver = null;
	private WebDriverBackedSelenium webDriverBackedSelenium = null;
	
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
	 * Get the current WebDriverBackedSelenium object
	 * <p>
	 * 
	 * @return
	 */
	public WebDriverBackedSelenium webDriverBackedSelenium() {
		if (webDriverBackedSelenium == null) {
			if(ZimbraSeleniumProperties.getStringProperty("browser").contains("googlechrome")){
				webDriverBackedSelenium = new WebDriverBackedSelenium(new ChromeDriver(), applicationURL);
			}else{
				FirefoxProfile profile = new FirefoxProfile();
				profile.setEnableNativeEvents(false);
				webDriverBackedSelenium = new WebDriverBackedSelenium(new FirefoxDriver(profile), applicationURL);
			}
		}
		return webDriverBackedSelenium;
	}
	
	/**
	 * Get the current WebDriver object
	 * <p>
	 * 
	 * @return
	 */
	public WebDriver webDriver() {
		if (webDriver == null) {
			/*
			try {
				DesiredCapabilities capability = DesiredCapabilities.firefox();
				capability.setJavascriptEnabled(true);
				webDriver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capability);
				} catch (Exception ex) {
					ex.printStackTrace();
				}									
			*/
			if(ZimbraSeleniumProperties.getStringProperty("browser").contains("googlechrome")){
				//DesiredCapabilities caps = DesiredCapabilities.chrome();
				//caps.setJavascriptEnabled(true);
				//caps.setCapability("chrome.binary", "path/to/chrome.exe");
				//System.setProperty("webdriver.chrome.driver","/path/to/chromedriver.exe");
				//ChromeDriver driver = new ChromeDriver(caps);
				//ChromeOptions options = new ChromeOptions();
				//webDriver = new ChromeDriver(options);
				//System.setProperty("webdriver.chrome.driver","C:/p4/zimbra/main/ZimbraSelenium/chromedriver.exe");
				webDriver = new ChromeDriver();
			} else {
				FirefoxProfile profile = new FirefoxProfile();
				//Proxy proxy = new Proxy();
				//proxy.setHttpProxy("proxy.vmware.com:3128");
				//profile.setProxyPreferences(proxy);
				//profile.addExtension(....);
				profile.setEnableNativeEvents(false);
				webDriver = new FirefoxDriver(profile);
				//webDriver = new FirefoxDriver();					
			}			
		}
		return webDriver;
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
