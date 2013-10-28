/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.touch.core;

import java.awt.Toolkit;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import org.apache.log4j.*;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;
import org.testng.*;
import org.testng.annotations.*;
import org.xml.sax.SAXException;
import com.thoughtworks.selenium.*;
import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.touch.ui.AppTouchClient;

/**
 * The <code>AjaxCommonTest</code> class is the base test case class
 * for normal Ajax client test case classes.
 * <p>
 * The AjaxCommonTest provides two basic functionalities:
 * <ol>
 * <li>{@link AbsTab} {@link #startingPage} - navigate to this
 * page before each test case method</li>
 * <li>{@link ZimbraAccount} {@link #startingAccountPreferences} - ensure this
 * account is authenticated before each test case method</li>
 * </ol>
 * <p>
 * It is important to note that no re-authentication (i.e. logout
 * followed by login) will occur if {@link #startingAccountPreferences} is 
 * already the currently authenticated account.
 * <p>
 * The same rule applies to the {@link #startingPage}, as well.  If
 * the "Contact App" is the specified starting page, and the contact
 * app is already opened, albiet in a "new contact" view, then the
 * "new contact" view will not be closed.
 * <p>
 * Typical usage:<p>
 * <pre>
 * {@code
 * public class TestCaseClass extends TouchCommonTest {
 * 
 *     public TestCaseClass() {
 *     
 *         // All tests start at the Mail page
 *         super.startingPage = app.zPageMail;
 *         
 *         // Create a new account to log into
 *         ZimbraAccount account = new ZimbraAccount();
 *         super.startingAccount = account;
 *         
 *         // ...
 *         
 *     }
 *     
 *     // ...
 * 
 * }
 * }
 * </pre>
 * 
 * @author Matt Rhoades
 *
 */
public class TouchCommonTest {
	
	protected static Logger logger = LogManager.getLogger(TouchCommonTest.class);


	private WebDriverBackedSelenium _webDriverBackedSelenium = null;
	private WebDriver _webDriver = null;
	
	/**
	 * The AdminConsole application object
	 */
	protected AppTouchClient app = null;



	/**
	 * BeforeMethod variables
	 * startingPage = the starting page before the test method starts
	 * startingAccountSettings = the account's settings (ModifyAccountRequest)
	 * startingAccountPreferences = the account's preferences (ModifyPrefsRequest)
	 * startingAccountZimletPreferences = the account's zimlet preferences (ModifyZimletPrefsRequest)
	 */
	protected AbsTab startingPage = null;
	protected Map<String, String> startingAccountPreferences = null;
	protected Map<String, String> startingUserPreferences = null;		// TODO:
	protected Map<String, String> startingUserZimletPreferences = null;

	
	
	
	protected TouchCommonTest() {
		logger.info("New "+ TouchCommonTest.class.getCanonicalName());

		app = new AppTouchClient();

		startingPage = app.zPageMain;
		startingAccountPreferences = new HashMap<String, String>();
		startingUserZimletPreferences = new HashMap<String, String>();
	}

	/**
	 * Global BeforeSuite
	 * <p>
	 * <ol>
	 * <li>Start the DefaultSelenium client</li>
	 * </ol>
	 * <p>
	 * @throws HarnessException
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws SAXException  
	 */
	@BeforeSuite( groups = { "always" } )
	public void commonTestBeforeSuite()
	throws HarnessException, IOException, InterruptedException, SAXException {
		logger.info("commonTestBeforeSuite: start");



      // Make sure there is a new default account
		ZimbraAccount.ResetAccountZTC();


		try
		{
			
			ZimbraSeleniumProperties.setAppType(ZimbraSeleniumProperties.AppType.TOUCH);
			DefaultSelenium _selenium = null;
			
			if (ZimbraSeleniumProperties.isWebDriver()) {
				_webDriver = ClientSessionFactory.session().webDriver();
				
				/*
				Set<String> handles = _webDriver.getWindowHandles(); 
				String script = "if (window.screen){var win = window.open(window.location); win.moveTo(0,0);win.resizeTo(window.screen.availWidth, window.screen.availHeight);};"; 
				((JavascriptExecutor) _webDriver).executeScript(script); 
				Set<String> newHandles = _webDriver.getWindowHandles(); 
				newHandles.removeAll(handles); 
				_webDriver.switchTo().window(newHandles.iterator().next());
							 							 
				_webDriver.manage().window().setSize(new Dimension(800,600));
				 
				Selenium selenium = new WebDriverBackedSelenium(_webDriver, _webDriver.getCurrentUrl());
				selenium.windowMaximize();
						
				int width = Integer.parseInt(selenium.getEval("screen.width;"));
				int height = Integer.parseInt(selenium.getEval("screen.height;"));
				_webDriver.manage().window().setPosition(new Point(0, 0));
				_webDriver.manage().window().setSize(new Dimension(width,height));
				*/
				
				Capabilities cp =  ((RemoteWebDriver)_webDriver).getCapabilities();
				 if (cp.getBrowserName().equals(DesiredCapabilities.firefox().getBrowserName())||cp.getBrowserName().equals(DesiredCapabilities.chrome().getBrowserName())||cp.getBrowserName().equals(DesiredCapabilities.internetExplorer().getBrowserName())){				
					_webDriver.manage().window().setPosition(new Point(0, 0));
					_webDriver.manage().window().setSize(new Dimension((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(),(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
				}								
			} else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()) {
				_webDriverBackedSelenium = ClientSessionFactory.session()
						.webDriverBackedSelenium();
				_webDriverBackedSelenium.windowMaximize();
				_webDriverBackedSelenium.windowFocus();
				_webDriverBackedSelenium.setTimeout("30000");// Use 30 second timeout for
			} else {
				_selenium = ClientSessionFactory.session().selenium();
				_selenium.start();
				_selenium.windowMaximize();
				_selenium.windowFocus();
				_selenium.allowNativeXpath("true");
				_selenium.setTimeout("30000");// Use 30 second timeout for opening the browser
			}
			// Dynamic wait for App to be ready
			int maxRetry = 10;
			int retry = 0;
			boolean appIsReady = false;
			while (retry < maxRetry && !appIsReady) {       
				try
				{
					logger.info("Retry #" + retry);
					retry ++;
					
					if (ZimbraSeleniumProperties.isWebDriver()) {
						//_webDriver.get(ZimbraSeleniumProperties.getBaseURL());
						_webDriver.navigate().to(ZimbraSeleniumProperties.getBaseURL());
					} 
					else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()) 
						_webDriverBackedSelenium.open(ZimbraSeleniumProperties.getBaseURL());
					else
						_selenium.open(ZimbraSeleniumProperties.getBaseURL());

					appIsReady = true;
				} catch (SeleniumException e) {
					if (retry == maxRetry) {
						logger.error("Unable to open admin app." +
								"  Is a valid cert installed?", e);
						throw e;
					} else {
						logger.info("App is still not ready...", e);
						SleepUtil.sleep(10000);
						continue;
					}
				}
			}
			logger.info("App is ready!");

		} catch (SeleniumException e) {
			throw new HarnessException("Unable to open app", e);
		} catch (Exception e) {
			logger.warn(e);
		}

		logger.info("commonTestBeforeSuite: finish");		
	}

	/**
	 * Global BeforeClass
	 *
	 * @throws HarnessException
	 */
	@BeforeClass( groups = { "always" } )
	public void commonTestBeforeClass() throws HarnessException {
		logger.info("commonTestBeforeClass: start");

		logger.info("commonTestBeforeClass: finish");

	}



	/**
	 * Global BeforeMethod
	 * <p>
	 * <ol>
	 * <li>For all tests, make sure {@link #startingPage} is active</li>
	 * <li>For all tests, make sure {@link #startingAccountPreferences} is logged in</li>
	 * <li>For all tests, make any compose tabs are closed</li>
	 * </ol>
	 * <p>
	 * @throws HarnessException
	 */
	@BeforeMethod( groups = { "always" } )
	public void commonTestBeforeMethod(Method method, ITestContext testContext) throws HarnessException {
		logger.info("commonTestBeforeMethod: start");


		// Get the test description
		// By default, the test description is set to method's name
		// if it is set, then change it to the specified one
		for (ITestNGMethod ngMethod : testContext.getAllTestMethods()) {
			String methodClass = ngMethod.getRealClass().getSimpleName();
			if (methodClass.equals(method.getDeclaringClass().getSimpleName())
					&& ngMethod.getMethodName().equals(method.getName())) {
				synchronized (TouchCommonTest.class) {
					logger.info("---------BeforeMethod-----------------------");
					logger.info("Test       : " + methodClass
							+ "." + ngMethod.getMethodName());
					logger.info("Description: " + ngMethod.getDescription());
					logger.info("----------------------------------------");
				}
				break;
			}
		}


		// If test account preferences are defined, then make sure the test account
		// uses those preferences
		//
		if ( (startingAccountPreferences != null) && (!startingAccountPreferences.isEmpty()) ) {
			logger.debug("commonTestBeforeMethod: startingAccountPreferences are defined");

			// If the current test accounts preferences match, then the account can be used
			if ( !ZimbraAccount.AccountZTC().compareAccountPreferences(startingAccountPreferences) ) {
				
				logger.debug("commonTestBeforeMethod: startingAccountPreferences do not match active account");

				// Reset the account
				ZimbraAccount.ResetAccountZTC();
				
				// Create a new account
				// Set the preferences accordingly
				ZimbraAccount.AccountZTC().modifyAccountPreferences(startingAccountPreferences);
				ZimbraAccount.AccountZTC().modifyUserZimletPreferences(startingUserZimletPreferences);

			}
			
		}
		
		// If AccountZTC is not currently logged in, then login now
		if ( !ZimbraAccount.AccountZTC().equals(app.zGetActiveAccount()) ) {
			logger.debug("commonTestBeforeMethod: AccountZTC is not currently logged in");

			if ( app.zPageMain.zIsActive() )
				try{
					app.zPageMain.zLogout();

				}catch(Exception ex){
					if ( !app.zPageLogin.zIsActive()) {
						logger.error("Login page is not active ", ex);

						app.zPageLogin.sOpen(ZimbraSeleniumProperties.getLogoutURL());            
						app.zPageLogin.sOpen(ZimbraSeleniumProperties.getBaseURL());
					}
				}							
		}

		// If a startingPage is defined, then make sure we are on that page
		if ( startingPage != null ) {
			logger.debug("commonTestBeforeMethod: startingPage is defined");

			// If the starting page is not active, navigate to it
			if ( !startingPage.zIsActive() ) {
				startingPage.zNavigateTo();
			}

			// Confirm that the page is active
			if ( !startingPage.zIsActive() ) {
				throw new HarnessException("Unable to navigate to "+ startingPage.myPageName());
			}

		}

		// Make sure any extra compose tabs are closed
		app.zPageMain.zCloseComposeTabs();

		logger.info("commonTestBeforeMethod: finish");

	}

	/**
	 * Global AfterSuite
	 * <p>
	 * <ol>
	 * <li>Stop the DefaultSelenium client</li>
	 * </ol>
	 * 
	 * @throws HarnessException
	 */
	@AfterSuite( groups = { "always" } )
	public void commonTestAfterSuite() throws HarnessException {	
		logger.info("commonTestAfterSuite: start");

		if (ZimbraSeleniumProperties.isWebDriver()) {
			_webDriver.quit();
		} else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()) {
			_webDriverBackedSelenium.stop();
		} else {
			ClientSessionFactory.session().selenium().stop();
		}
		

		logger.info("commonTestAfterSuite: finish");

	}

	/**
	 * Global AfterClass
	 * 
	 * @throws HarnessException
	 */
	@AfterClass( groups = { "always" } )
	public void commonTestAfterClass() throws HarnessException {
		logger.info("commonTestAfterClass: start");

		// For Touch, if account is considered dirty (modified),
		// then recreate a new account, but for desktop, the zimlet
		// preferences has to be reset to default, all core zimlets are enabled
		ZimbraAccount currentAccount = app.zGetActiveAccount();
		if (currentAccount != null && currentAccount.accountIsDirty &&
				currentAccount == ZimbraAccount.AccountZTC()) {
			// Reset the account
			ZimbraAccount.ResetAccountZTC();

		}

		logger.info("commonTestAfterClass: finish");
	}

	/**
	 * Global AfterMethod
	 * 
	 * @throws HarnessException
	 */
	@AfterMethod( groups = { "always" } )
	public void commonTestAfterMethod(Method method, ITestResult testResult)
	throws HarnessException {
		logger.info("commonTestAfterMethod: start");


		// If the active URL does not match the base URL, then
		// the test case may have manually navigated somewhere.
		//
		// Clear the cookies and reload
		//
		if ( ZimbraURI.needsReload() ) {
            logger.error("The URL does not match the base URL.  Reload app.");
            // app.zPageLogin.sDeleteAllVisibleCookies();
            app.zPageLogin.sOpen(ZimbraSeleniumProperties.getLogoutURL());            
            app.zPageLogin.sOpen(ZimbraSeleniumProperties.getBaseURL());
		}

		// If neither the main page or login page are active, then
		// The app may be in a confused state.
		//
		// Clear the cookies and reload
		//
		if ( (!app.zPageMain.zIsActive()) && (!app.zPageLogin.zIsActive()) ) {
            logger.error("Neither login page nor main page were active.  Reload app.", new Exception());
            // app.zPageLogin.sDeleteAllVisibleCookies();
            app.zPageLogin.sOpen(ZimbraSeleniumProperties.getLogoutURL());            
            app.zPageLogin.sOpen(ZimbraSeleniumProperties.getBaseURL());
        }
		
		logger.info("commonTestAfterMethod: finish");
	}


    /**
     * Performance test after method
     */
    @AfterMethod(groups={"performance"})
    public void performanceTestAfterMethod() {

       // Resetting the account to flush after each performance test method,
       // so that the next test is running with new account
       ZimbraAccount.ResetAccountZTC();

    }
	
	/**
	 * A TestNG data provider for all supported character sets
	 * @return
	 * @throws HarnessException 
	 */
	@DataProvider(name = "DataProviderSupportedCharsets")
	public Object[][] DataProviderSupportedCharsets() throws HarnessException {
		return (ZimbraCharsets.getInstance().getSampleTable());
	}


	public void ModifyAccountPreferences(String string) throws HarnessException {
		StringBuilder settings = new StringBuilder();
		for (Map.Entry<String, String> entry : startingAccountPreferences.entrySet()) {
			settings.append(String.format("<a n='%s'>%s</a>", entry.getKey(), entry.getValue()));
		}		
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<ModifyAccountRequest xmlns='urn:zimbraAdmin'>"
				+		"<id>"+ string +"</id>"
				+		settings.toString()
				+	"</ModifyAccountRequest>");
	}
}
