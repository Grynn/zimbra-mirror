package projects.admin.tests;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import projects.admin.ui.AppAdminConsole;

import com.thoughtworks.selenium.SeleniumException;

import framework.core.ClientSession;
import framework.core.ClientSessionFactory;
import framework.core.SeleniumService;
import framework.core.ZimbraSelenium;
import framework.util.HarnessException;
import framework.util.ZimbraAdminAccount;
import framework.util.ZimbraSeleniumProperties;

/**
 * Common definitions for all Admin Console test cases
 * @author Matt Rhoades
 *
 */
public class CommonTest {
	protected static Logger logger = LogManager.getLogger(CommonTest.class);
	
	/**
	 * Helper field.  admin = ZimbraAdminAccount.GlobalAdmin()
	 */
	protected ZimbraAdminAccount admin = null;
	protected AppAdminConsole app = null;
	
	protected CommonTest() {
		logger.info("New "+ CommonTest.class.getCanonicalName());
		
		admin = ZimbraAdminAccount.GlobalAdmin();
		app = new AppAdminConsole();
		
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
		
		SeleniumService.getInstance().startSeleniumServer();
		
		try
		{
			ClientSession session = ClientSessionFactory.session();
			ZimbraSelenium selenium = session.selenium();
			selenium.start();
			selenium.windowMaximize();
			selenium.windowFocus();
			selenium.setupZVariables();
			selenium.allowNativeXpath("true");
			selenium.open(ZimbraSeleniumProperties.getBaseURL());
		} catch (SeleniumException e) {
			logger.error("Unable to open admin app.  Is a valid cert installed?", e);
			throw e;
		}

		
	}
	
	/**
	 * Global BeforeClass
	 * 
	 * @throws HarnessException
	 */
	@BeforeClass( groups = { "always" } )
	public void commonTestBeforeClass() throws HarnessException {
		logger.info("commonTestBeforeClass");

	}

	/**
	 * Global BeforeMethod
	 * 
	 * 1. For all tests, make sure the global admin is logged in
	 * If the global admin should not be logged in for the test, then
	 * the individual test case should log out
	 * 
	 * @throws HarnessException
	 */
	@BeforeMethod( groups = { "always" } )
	public void commonTestBeforeMethod() throws HarnessException {
		logger.info("commonTestBeforeMethod: start");
		
		if ( !app.zMainPage.isActive() ) {
			
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
