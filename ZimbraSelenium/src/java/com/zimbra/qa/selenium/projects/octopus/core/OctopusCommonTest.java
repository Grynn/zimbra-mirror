package com.zimbra.qa.selenium.projects.octopus.core;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import com.thoughtworks.selenium.SeleniumException;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.ui.AppOctopusClient;

public class OctopusCommonTest {
	protected static Logger logger = LogManager
			.getLogger(OctopusCommonTest.class);
	protected final ZimbraAdminAccount gAdmin = ZimbraAdminAccount
			.GlobalAdmin();
	protected AppOctopusClient app = null;
	protected AbsTab startingPage = null;
	protected ZimbraAdminAccount startingAccount = null;

	protected OctopusCommonTest() {
		logger.info("New " + OctopusCommonTest.class.getCanonicalName());
		app = new AppOctopusClient();
		startingPage = app.zPageMain;
		startingAccount = gAdmin;
	}

	@BeforeSuite(groups = { "always" })
	public void commonTestBeforeSuite() throws HarnessException {

		logger.info("commonTestBeforeSuite");

		try {
			ZimbraSeleniumProperties
					.setAppType(ZimbraSeleniumProperties.AppType.OCTOPUS);

			// Use 30 second timeout for opening the browser
			String timeout = ZimbraSeleniumProperties.getStringProperty(
					"selenium.maxpageload.msec", "30000");

			ClientSessionFactory.session().selenium().start();
			ClientSessionFactory.session().selenium().windowMaximize();
			ClientSessionFactory.session().selenium().windowFocus();
			ClientSessionFactory.session().selenium().allowNativeXpath("true");
			ClientSessionFactory.session().selenium().setTimeout(timeout);
			ClientSessionFactory.session().selenium().open(
					ZimbraSeleniumProperties.getBaseURL());
		} catch (SeleniumException e) {
			logger.error(
					"Unable to open octopus app.  Is a valid cert installed?",
					e);
			throw e;
		}

	}

	/**
	 * Global BeforeClass
	 * 
	 * @throws HarnessException
	 */
	@BeforeClass(groups = { "always" })
	public void commonTestBeforeClass() throws HarnessException {
		logger.info("commonTestBeforeClass");

	}

	/**
	 * Global BeforeMethod
	 * 
	 * 1. For all tests, make sure the CommonTest.startingPage is active 2. For
	 * all tests, make sure the logged in user is
	 * 
	 * @throws HarnessException
	 */
	@BeforeMethod(groups = { "always" })
	public void commonTestBeforeMethod() throws HarnessException {
		logger.info("commonTestBeforeMethod: start");

		// If a startinAccount is defined, then make sure we are authenticated
		// as that user
		if (startingAccount != null) {
			logger.debug("commonTestBeforeMethod: startingAccount is defined");

			if (!startingAccount.equals(app.zGetActiveAccount())) {

				if (app.zPageMain.zIsActive())
					app.zPageMain.zLogout();
				app.zPageLogin.zLogin(startingAccount);

			}

			// Confirm
			if (!startingAccount.equals(app.zGetActiveAccount())) {
				throw new HarnessException("Unable to authenticate as "
						+ startingAccount.EmailAddress);
			}
		}

		// If a startingPage is defined, then make sure we are on that page
		if (startingPage != null) {
			logger.debug("commonTestBeforeMethod: startingPage is defined");

			// If the starting page is not active, navigate to it
			if (!startingPage.zIsActive()) {
				startingPage.zNavigateTo();
			}

			// Confirm that the page is active
			if (!startingPage.zIsActive()) {
				throw new HarnessException("Unable to navigate to "
						+ startingPage.myPageName());
			}

		}

		logger.info("commonTestBeforeMethod: finish");

	}

	/**
	 * Global AfterSuite
	 * 
	 * @throws HarnessException
	 */
	@AfterSuite(groups = { "always" })
	public void commonTestAfterSuite() throws HarnessException {
		logger.info("commonTestAfterSuite: start");

		ClientSessionFactory.session().selenium().stop();

		logger.info("commonTestAfterSuite: finish");

	}

	/**
	 * Global AfterClass
	 * 
	 * @throws HarnessException
	 */
	@AfterClass(groups = { "always" })
	public void commonTestAfterClass() throws HarnessException {
		logger.info("commonTestAfterClass: start");

		logger.info("commonTestAfterClass: finish");
	}

	/**
	 * Global AfterMethod
	 * 
	 * @throws HarnessException
	 */
	@AfterMethod(groups = { "always" })
	public void commonTestAfterMethod() throws HarnessException {
		logger.info("commonTestAfterMethod: start");

		logger.info("commonTestAfterMethod: finish");
	}

}
