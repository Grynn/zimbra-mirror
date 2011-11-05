package com.zimbra.qa.selenium.projects.html.core;

import java.io.IOException;
import java.util.*;

import org.apache.log4j.*;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.html.ui.AppHtmlClient;

/**
 * The <code>HtmlCommonTest</code> class is the base test case class
 * for normal HTML client test case classes.
 * <p>
 * The HtmlCommonTest provides two basic functionalities:
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
 * public class TestCaseClass extends HtmlCommonTest {
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
public class HtmlCommonTest {
	protected static Logger logger = LogManager.getLogger(HtmlCommonTest.class);



	/**
	 * The Html application object
	 */
	protected AppHtmlClient app = null;



	// Configurable from config file or input parameters

	/**
	 * BeforeMethod variables
	 * startingPage = the starting page before the test method starts
	 * startingAccount = the account to log in as
	 */
	protected AbsTab startingPage = null;
	protected Map<String, String> startingAccountPreferences = null;
	protected Map<String, String> startingAccountZimletPreferences = null;

	protected HtmlCommonTest() {
		logger.info("New "+ HtmlCommonTest.class.getCanonicalName());

		app = new AppHtmlClient();

		startingPage = app.zPageMain;
		startingAccountPreferences = new HashMap<String, String>();
		startingAccountZimletPreferences = new HashMap<String, String>();
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
	public void commonTestBeforeSuite() throws HarnessException {
		logger.info("commonTestBeforeSuite: start");


		ZimbraSeleniumProperties.setAppType(ZimbraSeleniumProperties.AppType.HTML);

		ClientSessionFactory.session().selenium().start();
		ClientSessionFactory.session().selenium().windowMaximize();
		ClientSessionFactory.session().selenium().windowFocus();
		ClientSessionFactory.session().selenium().allowNativeXpath("true");
		ClientSessionFactory.session().selenium().setTimeout("30000");// Use 30 second timeout for opening the browser

		ClientSessionFactory.session().selenium().open(ZimbraSeleniumProperties.getBaseURL());

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
	public void commonTestBeforeMethod() throws HarnessException {
		logger.info("commonTestBeforeMethod: start");


		// If test account preferences are defined, then make sure the test account
		// uses those preferences
		//
		if ( (startingAccountPreferences != null) && (!startingAccountPreferences.isEmpty()) ) {
			logger.debug("commonTestBeforeMethod: startingAccountPreferences are defined");

			StringBuilder settings = new StringBuilder();
			for (Map.Entry<String, String> entry : startingAccountPreferences.entrySet()) {
				settings.append(String.format("<a n='%s'>%s</a>", entry.getKey(), entry.getValue()));
			}
			ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<ModifyAccountRequest xmlns='urn:zimbraAdmin'>"
					+		"<id>"+ ZimbraAccount.AccountHTML().ZimbraId +"</id>"
					+		settings.toString()
					+	"</ModifyAccountRequest>");


			// Set the flag so the account is reset for the next test
			ZimbraAccount.AccountHTML().accountIsDirty = true;
		}

		// If test account zimlet preferences are defined, then make sure the test account
		// uses those zimlet preferences
		//
		if ( (startingAccountZimletPreferences != null) && (!startingAccountZimletPreferences.isEmpty()) ) {
			logger.debug("commonTestBeforeMethod: startingAccountPreferences are defined");
			ZimbraAccount.AccountHTML().modifyZimletPreferences(startingAccountZimletPreferences);
		}

		// If AccountHTML is not currently logged in, then login now
		if ( !ZimbraAccount.AccountHTML().equals(app.zGetActiveAccount()) ) {
			logger.debug("commonTestBeforeMethod: AccountHTML is not currently logged in");

			if ( app.zPageMain.zIsActive() )
				app.zPageMain.zLogout();

			app.zPageLogin.zLogin(ZimbraAccount.AccountHTML());

			// Confirm
			if ( !ZimbraAccount.AccountHTML().equals(app.zGetActiveAccount())) {
				throw new HarnessException("Unable to authenticate as "+ ZimbraAccount.AccountHTML().EmailAddress);
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

		// For Ajax and Html, if account is considered dirty (modified),
		// then recreate a new account
		ZimbraAccount currentAccount = app.zGetActiveAccount();
		if (currentAccount != null 
				&& currentAccount.accountIsDirty 
				&& currentAccount == ZimbraAccount.AccountHTML()) {

			ZimbraAccount.ResetAccountHTML();

		}

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
