package projects.desktop.core;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import projects.desktop.ui.AppDesktopClient;

import com.thoughtworks.selenium.SeleniumException;

import framework.core.ClientSession;
import framework.core.ClientSessionFactory;
import framework.core.ZimbraSelenium;
import framework.ui.AbsPage;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZimbraAccount;
import framework.util.ZimbraSeleniumProperties;

/**
 * Common definitions for all Desktop test cases
 * @author Jeffry Hidayat
 *
 */
public class DesktopCommonTest {
	protected static Logger logger = LogManager.getLogger(DesktopCommonTest.class);
	
	/**
	 * Helper field.  admin = ZimbraAdminAccount.GlobalAdmin()
	 */
	protected final ZimbraAccount gAdmin = ZimbraAccount.AccountZWC();
	

	/**
	 * The AdminConsole application object
	 */
	protected AppDesktopClient app = null;

	/**
	 * BeforeMethod variables
	 * startingPage = the starting page before the test method starts
	 * startingAccount = the account to log in as
	 */
	protected AbsPage startingPage = null;
	protected ZimbraAccount startingAccount = null;

	protected DesktopCommonTest() {
		logger.info("New "+ DesktopCommonTest.class.getCanonicalName());

		app = new AppDesktopClient();

		startingPage = app.zPageMain;
		startingAccount = gAdmin;

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
		logger.info("commonTestBeforeSuite");

		try
		{
			ClientSession session = ClientSessionFactory.session();
			ZimbraSelenium selenium = session.selenium();
			selenium.start();
			selenium.windowMaximize();
			selenium.windowFocus();
			// selenium.setupZVariables();
			// admin doesn't use any of the JS code
			selenium.allowNativeXpath("true");
			ZimbraSeleniumProperties.setAppType(
			      ZimbraSeleniumProperties.AppType.DESKTOP);
			selenium.open(ZimbraSeleniumProperties.getBaseURL());
			//selenium.open("http://127.0.0.1:1884/desktop/login.jsp?at=42077838-fc72-4756-a68b-959537b5ecc8");
		} catch (SeleniumException e) {
			logger.error("Unable to open admin app." +
					"  Is a valid cert installed?", e);
			throw e;
		}
	}
	
	/**
	 * Global BeforeClass
	 *
	 * 1. Dynamically wait for the application to be completely loaded
	 * @throws HarnessException
	 */
	@BeforeClass( groups = { "always" } )
	public void commonTestBeforeClass() throws HarnessException {
		logger.info("commonTestBeforeClass");

		logger.info("Wait dynamically until the application is loaded");
		int retry = 0;
		int maxRetry = 30;
		while (retry < maxRetry && !app.zIsLoaded()) {
		   retry ++;
		   SleepUtil.sleep(1000);
		}
		logger.info("retry is " + retry);
		logger.info("App is loaded: " + app.zIsLoaded());

		if (retry == maxRetry) {
		   throw new HarnessException(
		         "Time out: Desktop Application is never loaded");
		}
	}

	//TODO:
	/**
	 * Global BeforeMethod
	 *
	 * 1. For all tests, make sure the CommonTest.startingPage is active
	 * 2. For all tests, make sure the logged in user is 
	 *
	 * @throws HarnessException
	 */
	@BeforeMethod( groups = { "always" } )
	public void commonTestBeforeMethod() throws HarnessException {
		logger.info("commonTestBeforeMethod: start");

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

		ClientSessionFactory.session().selenium().stop();

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
	public void commonTestAfterMethod() throws HarnessException {
		logger.info("commonTestAfterMethod: start");

		logger.info("commonTestAfterMethod: finish");
	}

}
