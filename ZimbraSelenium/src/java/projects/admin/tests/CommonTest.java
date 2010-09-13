package projects.admin.tests;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import projects.admin.ui.AbsPage;
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
	protected final ZimbraAdminAccount gAdmin = ZimbraAdminAccount.GlobalAdmin();
	

	/**
	 * The AdminConsole application object
	 */
	protected AppAdminConsole app = null;

	/**
	 * BeforeMethod variables
	 * startingPage = the starting page before the test method starts
	 * startingAccount = the account to log in as
	 */
	protected AbsPage startingPage = null;
	protected ZimbraAdminAccount startingAccount = null;
	
	protected CommonTest() {
		logger.info("New "+ CommonTest.class.getCanonicalName());
		
		app = new AppAdminConsole();
		
		startingPage = app.zMainPage;
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
	 * 1. For all tests, make sure the CommonTest.startingPage is active
	 * 2. For all tests, make sure the logged in user is 
	 * 
	 * @throws HarnessException
	 */
	@BeforeMethod( groups = { "always" } )
	public void commonTestBeforeMethod() throws HarnessException {
		logger.info("commonTestBeforeMethod: start");
		
		// If a startinAccount is defined, then make sure we are authenticated as that user
		if ( startingAccount != null ) {
			logger.debug("commonTestBeforeMethod: startingAccount is defined");
			
			if ( !startingAccount.equals(app.getActiveAccount())) {
				
				if ( app.zMainPage.isActive() )
					app.zMainPage.logout();
				app.zLoginPage.login(startingAccount);
				
			}
			
			// Confirm
			if ( !startingAccount.equals(app.getActiveAccount())) {
				throw new HarnessException("Unable to authenticate as "+ startingAccount.EmailAddress);
			}
		}

		// If a startingPage is defined, then make sure we are on that page
		if ( startingPage != null ) {
			logger.debug("commonTestBeforeMethod: startingPage is defined");
			
			// If the starting page is not active, navigate to it
			if ( !startingPage.isActive() ) {
				startingPage.navigateTo();
			}
			
			// Confirm that the page is active
			if ( !startingPage.isActive() ) {
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
