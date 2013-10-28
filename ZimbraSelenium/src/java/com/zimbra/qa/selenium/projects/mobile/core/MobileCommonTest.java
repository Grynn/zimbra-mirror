/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.projects.mobile.core;

import java.awt.Toolkit;
import java.lang.reflect.Method;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;


import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.mobile.ui.AppMobileClient;


/**
 * Common definitions for all Mobile Client test cases
 * @author Matt Rhoades
 *
 */
public class MobileCommonTest {
	protected static Logger logger = LogManager.getLogger(MobileCommonTest.class);
		
	
	// Web Driver integration
	private WebDriver _webDriver = null;
	private DefaultSelenium _selenium = null;

	/**
	 * The AdminConsole application object
	 */
	protected AppMobileClient app = null;

	/**
	 * BeforeMethod variables
	 * startingPage = the starting page before the test method starts
	 * startingAccount = the account to log in as
	 */
	protected AbsTab startingPage = null;
	protected ZimbraAccount startingAccount = null;

	protected MobileCommonTest() {
		logger.info("New "+ MobileCommonTest.class.getCanonicalName());
		
		app = new AppMobileClient();
		
		startingPage = app.zPageMain;
		startingAccount = ZimbraAccount.AccountZMC();
		
		app.zPageLogin.DefaultLoginAccount = startingAccount;
		
	}
	
	/**
	 * Global BeforeSuite
	 * 
	 * 1. Make sure the selenium server is available
	 * 
	 * @throws HarnessException
	 */
	@BeforeSuite( groups = { "always" } )
	public void commonTestBeforeSuite() throws HarnessException {
		logger.info("commonTestBeforeSuite: start");



      	// Make sure there is a new default account
		ZimbraAccount.ResetAccountZMC();
				
		// Set the app type
		ZimbraSeleniumProperties.setAppType(ZimbraSeleniumProperties.AppType.MOBILE);

		try
		{

			if (ZimbraSeleniumProperties.isWebDriver()) {

				_webDriver = ClientSessionFactory.session().webDriver();

				Capabilities cp =  ((RemoteWebDriver)_webDriver).getCapabilities();
				if (cp.getBrowserName().equals(DesiredCapabilities.firefox().getBrowserName())||cp.getBrowserName().equals(DesiredCapabilities.chrome().getBrowserName())||cp.getBrowserName().equals(DesiredCapabilities.internetExplorer().getBrowserName())){				
					_webDriver.manage().window().setPosition(new Point(0, 0));
					_webDriver.manage().window().setSize(new Dimension((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(),(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
				}								

			} else {


				_selenium = ClientSessionFactory.session().selenium();
				_selenium.start();
				_selenium.windowMaximize();
				_selenium.windowFocus();
				_selenium.allowNativeXpath("true");
				_selenium.setTimeout("30000");	// Use 30 second timeout for opening the browser

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
					else {
						_selenium.open(ZimbraSeleniumProperties.getBaseURL());
					}

					appIsReady = true;
				} catch (SeleniumException e) {
					if (retry >= maxRetry) {
						logger.error("Unable to open admin app.  Is a valid cert installed?", e);
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
			logger.error("Unable to mobile app.", e);
			throw e;
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
	 * 
	 * 1. For all tests, make sure the CommonTest.startingPage is active
	 * 2. For all tests, make sure the logged in user is 
	 * 
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
		      synchronized (MobileCommonTest.class) {
		         logger.info("---------BeforeMethod-----------------------");
		         logger.info("Test       : " + methodClass
		               + "." + ngMethod.getMethodName());
		         logger.info("Description: " + ngMethod.getDescription());
		         logger.info("----------------------------------------");
		      }
		      break;
		   }
		}


	      // If a startinAccount is defined, then make sure we are authenticated as that user
		if ( startingAccount != null ) {
			logger.debug("commonTestBeforeMethod: startingAccount is defined");

			if ( !startingAccount.equals(app.zGetActiveAccount())) {

				if ( app.zPageMain.zIsActive() )
					app.zPageMain.zLogout();
				app.zPageLogin.zLogin(startingAccount);

			}

			// Confirm
			if ( !startingAccount.equals(app.zGetActiveAccount())) {
				throw new HarnessException("Unable to authenticate as "+ startingAccount.EmailAddress);
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
		

		logger.info("commonTestBeforeMethod: finish");

	}

	/**
	 * Global AfterSuite
	 * 
	 * @throws HarnessException
	 */
	@AfterSuite( groups = { "always" } )
	public void commonTestAfterSuite() throws HarnessException {	
		logger.info("commonTestAfterSuite: start");
		
		if (ZimbraSeleniumProperties.isWebDriver()) {
			_webDriver.quit();
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


      logger.info("commonTestAfterMethod: finish");
	}

}
