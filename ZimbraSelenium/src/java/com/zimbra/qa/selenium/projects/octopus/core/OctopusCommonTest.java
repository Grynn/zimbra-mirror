package com.zimbra.qa.selenium.projects.octopus.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.xml.sax.SAXException;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.OperatingSystem.OsType;
import com.zimbra.qa.selenium.projects.octopus.ui.AppOctopusClient;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogError;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogError.DialogErrorID;
import com.zimbra.qa.selenium.framework.util.*;

public class OctopusCommonTest {
	protected static Logger logger = LogManager
			.getLogger(OctopusCommonTest.class);
	private static DefaultSelenium _selenium = null;
	protected static OsType osType = null;
	protected AbsTab startingPage = null;
	protected Map<String, String> startingAccountPreferences = null;
	protected Map<String, String> startingAccountZimletPreferences = null;
	public final static String defaultAccountName = ZimbraSeleniumProperties
			.getUniqueString();
	protected AppOctopusClient app;

	protected OctopusCommonTest() {
		logger.info("New " + OctopusCommonTest.class.getCanonicalName());

		app = new AppOctopusClient();

		startingPage = app.zPageOctopus;
		startingAccountPreferences = new HashMap<String, String>();
		startingAccountZimletPreferences = new HashMap<String, String>();
	}

	@BeforeSuite(groups = { "always" })
	public void commonTestBeforeSuite() throws HarnessException, IOException,
			InterruptedException, SAXException {
		logger.info("commonTestBeforeSuite: start");

		// Make sure there is a new default account
		ZimbraAccount.ResetAccountZWC();
		osType = OperatingSystem.getOSType();
		try {
			ZimbraSeleniumProperties
					.setAppType(ZimbraSeleniumProperties.AppType.OCTOPUS);
			_selenium = ClientSessionFactory.session().selenium();
			//BrowserConfigurationOptions bco = new BrowserConfigurationOptions();
			//bco.setCommandLineFlags("--disable-web-security");
			//bco.setCommandLineFlags("--trustAllSSLCertificates");
			//_selenium.start(bco);
			_selenium.start();
			_selenium.windowMaximize();
			_selenium.windowFocus();
			_selenium.allowNativeXpath("true");
			_selenium.setTimeout("30000");// Use 30 second timeout for opening
			int maxRetry = 10;
			int retry = 0;
			boolean appIsReady = false;
			while (retry < maxRetry && !appIsReady) {
				try {
					logger.info("Retry #" + retry);
					retry++;
					_selenium.open(ZimbraSeleniumProperties.getBaseURL());
					appIsReady = true;
				} catch (SeleniumException e) {
					if (retry == maxRetry) {
						logger.error("Unable to open admin app."
								+ "  Is a valid cert installed?", e);
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
			logger.info(e.getMessage());
			e.printStackTrace();
		}
		logger.info("commonTestBeforeSuite: finish");
	}

	/**
	 * Global BeforeClass
	 * 
	 * @throws HarnessException
	 */
	@BeforeClass(groups = { "always" })
	public void commonTestBeforeClass() throws HarnessException {
		logger.info("commonTestBeforeClass: start");
		logger.info("commonTestBeforeClass: finish");
	}
	
	/**
	 * Global BeforeMethod
	 * 
	 * @throws HarnessException
	 */
	@BeforeMethod(groups = { "always" })
	public void commonTestBeforeMethod() throws HarnessException {
		logger.info("commonTestBeforeMethod: start");

		// If test account preferences are defined, then make sure the test
		// account
		// uses those preferences
		//
		if ((startingAccountPreferences != null)
				&& (!startingAccountPreferences.isEmpty())) {
			logger
					.debug("commonTestBeforeMethod: startingAccountPreferences are defined");

			StringBuilder settings = new StringBuilder();
			for (Map.Entry<String, String> entry : startingAccountPreferences
					.entrySet()) {
				settings.append(String.format("<a n='%s'>%s</a>", entry
						.getKey(), entry.getValue()));
			}
			ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<ModifyAccountRequest xmlns='urn:zimbraAdmin'>" + "<id>"
							+ ZimbraAccount.AccountZWC().ZimbraId + "</id>"
							+ settings.toString() + "</ModifyAccountRequest>");

			// Set the flag so the account is reset for the next test
			ZimbraAccount.AccountZWC().accountIsDirty = true;
		}

		// If AccountZWC is not currently logged in, then login now
		if (!ZimbraAccount.AccountZWC().equals(app.zGetActiveAccount())) {
			logger
					.debug("commonTestBeforeMethod: AccountZWC is not currently logged in");

			if (app.zPageOctopus.zIsActive())
				app.zPageOctopus.zLogout();
			app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());

			// Confirm
			if (!ZimbraAccount.AccountZWC().equals(app.zGetActiveAccount())) {
				throw new HarnessException("Unable to authenticate as "
						+ ZimbraAccount.AccountZWC().EmailAddress);
			}

			DialogError dialog = app.zPageOctopus
					.zGetErrorDialog(DialogErrorID.ErrorDialog);
			if (dialog.zIsActive()) {
				dialog.zClickButton(Button.B_OK);
			}
			//
			// END REF: https://bugzilla.zimbra.com/show_bug.cgi?id=63711

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
	 * <p>
	 * <ol>
	 * <li>Stop the DefaultSelenium client</li>
	 * </ol>
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

		// if account is considered dirty (modified),
		ZimbraAccount currentAccount = app.zGetActiveAccount();
		if (currentAccount != null && currentAccount.accountIsDirty
				&& currentAccount == ZimbraAccount.AccountZWC()) {
			// Reset the account
			ZimbraAccount.ResetAccountZWC();
		}
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
