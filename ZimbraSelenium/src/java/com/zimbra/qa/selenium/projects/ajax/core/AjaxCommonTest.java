package com.zimbra.qa.selenium.projects.ajax.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import org.apache.log4j.*;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.xml.sax.SAXException;
import com.thoughtworks.selenium.*;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.core.Repository;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.OperatingSystem.OsType;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogError.DialogErrorID;

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
 * public class TestCaseClass extends AjaxCommonTest {
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
public class AjaxCommonTest {
	public String DoubleQuoteChar = new Character((char) 34).toString();
	
	protected static Logger logger = LogManager.getLogger(AjaxCommonTest.class);
	public final boolean isRunningDesktopTest = ZimbraSeleniumProperties.getStringProperty(
			ZimbraSeleniumProperties.getLocalHost() + ".desktop.test", "false").toLowerCase().equals("true") ? true : false;

	private static DefaultSelenium _selenium = null;

	/**
	 * The AdminConsole application object
	 */
	protected AppAjaxClient app = null;

	protected static OsType osType = null;
	private final static String _accountFlavor = "Zimbra";
	public final static String defaultAccountName = ZimbraSeleniumProperties.getUniqueString();
	private Repository _repository = new Repository();

	// Configurable from config file or input parameters
	protected String[] desktopZimlets = null;

	/**
	 * BeforeMethod variables
	 * startingPage = the starting page before the test method starts
	 * startingAccountSettings = the account's settings (ModifyAccountRequest)
	 * startingAccountPreferences = the account's preferences (ModifyPrefsRequest)
	 * startingAccountZimletPreferences = the account's zimlet preferences (ModifyZimletPrefsRequest)
	 */
	protected AbsTab startingPage = null;
	protected Map<String, String> startingAccountPreferences = null;
	protected Map<String, String> startingAccountZimletPreferences = null;

	protected AjaxCommonTest() {
		logger.info("New "+ AjaxCommonTest.class.getCanonicalName());

		app = new AppAjaxClient();

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
	public void commonTestBeforeSuite()
	throws HarnessException, IOException, InterruptedException, SAXException {
		logger.info("commonTestBeforeSuite: start");

      //Racetrack
      String DbHostURL = ZimbraSeleniumProperties.getStringProperty("racetrack.dbUrl",
            "racetrack.eng.vmware.com");
      String buildNumber = ZimbraSeleniumProperties.getStringProperty("racetrack.buildNumber",
            "000000");
      String userName = ZimbraSeleniumProperties.getStringProperty("racetrack.username",
            "anonymous");
      String product = ZimbraSeleniumProperties.getStringProperty("racetrack.product",
            "ZCS");
      String description = ZimbraSeleniumProperties.getStringProperty("racetrack.description",
            "zdesktop description");
      String branch = ZimbraSeleniumProperties.getStringProperty("racetrack.branch",
            "Please specify version");
      String buildType = ZimbraSeleniumProperties.getStringProperty("racetrack.buildType",
            "beta");
      String testType = ZimbraSeleniumProperties.getStringProperty("racetrack.testType",
            "functional");
      String recordToRacetrack = ZimbraSeleniumProperties.getStringProperty("racetrack.recordToRacetrack",
            "false");
      String appendToExisting = ZimbraSeleniumProperties.getStringProperty("racetrack.appendToExisting",
            "false");
      String resultId = ZimbraSeleniumProperties.getStringProperty("racetrack.resultId",
            "");

      _repository.connectingToRacetrack(DbHostURL);
      _repository.beginTestSet(
            buildNumber,
            userName,
            product,
            description,
            branch,
            buildType,
            testType,
            Boolean.parseBoolean(recordToRacetrack),
            Boolean.parseBoolean(appendToExisting),
            resultId);

      // Make sure there is a new default account
		ZimbraAccount.ResetAccountZWC();

		osType = OperatingSystem.getOSType();

		try
		{
			
			ZimbraSeleniumProperties.setAppType(ZimbraSeleniumProperties.AppType.AJAX);


			_selenium = ClientSessionFactory.session().selenium();
			_selenium.start();
			_selenium.windowMaximize();
			_selenium.windowFocus();
			_selenium.allowNativeXpath("true");
			_selenium.setTimeout("30000");// Use 30 second timeout for opening the browser

			// Dynamic wait for App to be ready
			int maxRetry = 10;
			int retry = 0;
			boolean appIsReady = false;
			while (retry < maxRetry && !appIsReady) {       
				try
				{
					logger.info("Retry #" + retry);
					retry ++;
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
	@BeforeClass( groups = { "always" } )
	public void commonTestBeforeClass() throws HarnessException {
		logger.info("commonTestBeforeClass: start");

		logger.info("commonTestBeforeClass: finish");

	}

	/**
	 * Add default account using HTTP post
	 * @throws HarnessException
	 */
	public void addDefaultAccount() throws HarnessException {
		logger.info("Creating new account...");
		String serverScheme = ZimbraSeleniumProperties.getStringProperty("server.scheme", "http");
		String serverName = ZimbraSeleniumProperties.getStringProperty("desktop.server.host", "localhost");
		ZimbraDesktopProperties zdp = ZimbraDesktopProperties.getInstance();
		String connectionPort = zdp.getConnectionPort();

		String emailServerName = ZimbraSeleniumProperties.getStringProperty("adminName", "admin@localhost").split("@")[1];
		String emailServerPort = ZimbraSeleniumProperties.getStringProperty("server.port", "80");
		String securityType = serverScheme.equals("http") ? "&security=cleartext" : "";
		String accountSetupUrl = new StringBuilder(serverScheme).append("://")
		.append(serverName). append(":")
		.append(connectionPort).append("/")
		.append("zimbra/desktop/accsetup.jsp?at=")
		.append(zdp.getSerialNumber()).append("&accountId=&verb=add&accountFlavor=")
		.append(_accountFlavor).append("&accountName=")
		.append(defaultAccountName).append("&email=")
		.append(ZimbraAccount.AccountZWC().EmailAddress).append("&password=")
		.append(ZimbraAccount.AccountZWC().Password).append("&host=") 
		.append(emailServerName).append("&port=")
		.append(emailServerPort).append("&syncFreqSecs=900&debugTraceEnabled=on")
		.append(securityType).toString();
		logger.info("accountSetupUrl: " + accountSetupUrl);
		GeneralUtility.doHttpPost(accountSetupUrl);

		String accountUrl = new StringBuilder(serverScheme).append("://")
		.append(serverName). append(":")
		.append(connectionPort).append("/")
		.append("?at=")
		.append(zdp.getSerialNumber()).toString();
		logger.debug("Selenium is opening: " + accountUrl);
		logger.debug("Selenium is: " + _selenium);
		_selenium.open(accountUrl);
		GeneralUtility.waitForElementPresent(app.zPageLogin,
				PageLogin.Locators.zBtnLoginDesktop);
	}

	/**
	 * Delete Desktop account through HTTP Post
	 * @param accountName Account Name to be deleted
	 * @param accountId Account ID to be deleted
	 * @param accountType Account Type (usually: zimbra)
	 * @param accountFlavor Account Flavor (usually: Zimbra) 
	 * @throws HarnessException
	 */
	public void deleteDesktopAccount(String accountName, String accountId,
			String accountType, String accountFlavor) throws HarnessException {
		String serverScheme = ZimbraSeleniumProperties.getStringProperty("server.scheme", "http");
		String serverName = ZimbraSeleniumProperties.getStringProperty("desktop.server.host", "localhost");
		ZimbraDesktopProperties zdp = ZimbraDesktopProperties.getInstance();
		String connectionPort = zdp.getConnectionPort();
		String accountDeleteUrl = new StringBuilder(serverScheme).append("://")
		.append(serverName). append(":")
		.append(connectionPort).append("/")
		.append("zimbra/desktop/accsetup.jsp?at=")
		.append(zdp.getSerialNumber()).append("&accountId=")
		.append(accountId).append("&verb=del&accountFlavor=")
		.append(accountFlavor).append("&accountName=")
		.append(accountName).append("&accountType=")
		.append(accountType).toString();

		logger.info("accountDeleteUrl: " + accountDeleteUrl);
		GeneralUtility.doHttpPost(accountDeleteUrl);

		_selenium.refresh();
		GeneralUtility.waitForElementPresent(app.zPageLogin,
				PageLogin.Locators.zAddNewAccountButton);
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

		String packageName = method.getDeclaringClass().getPackage().getName();
		String methodName = method.getName();

		// Get the test description
		// By default, the test description is set to method's name
		// if it is set, then change it to the specified one
		String testDescription = methodName;
      for (ITestNGMethod ngMethod : testContext.getAllTestMethods()) {
         String methodClass = ngMethod.getRealClass().getSimpleName();
         if (methodClass.equals(method.getDeclaringClass().getSimpleName())
               && ngMethod.getMethodName().equals(method.getName())) {
            synchronized (AjaxCommonTest.class) {
               logger.info("---------BeforeMethod-----------------------");
               logger.info("Test       : " + methodClass
                     + "." + ngMethod.getMethodName());
               logger.info("Description: " + ngMethod.getDescription());
               logger.info("----------------------------------------");
               testDescription = ngMethod.getDescription();
            }
            break;
         }
      }

      Repository.testCaseBegin(methodName, packageName, testDescription);

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
					+		"<id>"+ ZimbraAccount.AccountZWC().ZimbraId +"</id>"
					+		settings.toString()
					+	"</ModifyAccountRequest>");


			// Set the flag so the account is reset for the next test
			ZimbraAccount.AccountZWC().accountIsDirty = true;
		}

		// If test account zimlet preferences are defined, then make sure the test account
		// uses those zimlet preferences
		//
		if ( (startingAccountZimletPreferences != null) && (!startingAccountZimletPreferences.isEmpty()) ) {
			logger.debug("commonTestBeforeMethod: startingAccountPreferences are defined");
			ZimbraAccount.AccountZWC().modifyZimletPreferences(startingAccountZimletPreferences, SOAP_DESTINATION_HOST_TYPE.SERVER);
		}

		// If AccountZWC is not currently logged in, then login now
		if ( !ZimbraAccount.AccountZWC().equals(app.zGetActiveAccount()) ) {
			logger.debug("commonTestBeforeMethod: AccountZWC is not currently logged in");

			if ( app.zPageMain.zIsActive() )
				app.zPageMain.zLogout();
			app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());

			// Confirm
			if ( !ZimbraAccount.AccountZWC().equals(app.zGetActiveAccount())) {
				throw new HarnessException("Unable to authenticate as "+ ZimbraAccount.AccountZWC().EmailAddress);
			}

			// Handle http://wiki.zimbra.com/wiki/File:ZimbraSeleniumScreenshotPopups1.jpeg
			// START REF: https://bugzilla.zimbra.com/show_bug.cgi?id=63711
			// Depending on how bug 63711 is implemented, need to add/update/remove the 
			// code below.  Checking for a dialog after login is too generic - it
			// could miss other un-wanted dialog boxes that are bugs.
			//
			DialogError dialog = app.zPageMain.zGetErrorDialog(DialogErrorID.ZmMsgDialog);
			if ( dialog.zIsActive() ) {
				dialog.zClickButton(Button.B_OK);
			}
			//
			// END REF: https://bugzilla.zimbra.com/show_bug.cgi?id=63711

			
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


		ClientSessionFactory.session().selenium().stop();

		_repository.endRepository();

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

		// For Ajax, if account is considered dirty (modified),
		// then recreate a new account, but for desktop, the zimlet
		// preferences has to be reset to default, all core zimlets are enabled
		ZimbraAccount currentAccount = app.zGetActiveAccount();
		if (currentAccount != null && currentAccount.accountIsDirty &&
				currentAccount == ZimbraAccount.AccountZWC()) {
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
	@AfterMethod( groups = { "always" } )
	public void commonTestAfterMethod(Method method, ITestResult testResult)
	throws HarnessException {
		logger.info("commonTestAfterMethod: start");

		String testCaseResult = String.valueOf(testResult.getStatus());
      Repository.testCaseEnd(testCaseResult);

		logger.info("commonTestAfterMethod: finish");
	}


    /**
     * Performance test after method
     */
    @AfterMethod(groups={"performance"})
    public void performanceTestAfterMethod() {

       // Resetting the account to flush after each performance test method,
       // so that the next test is running with new account
       ZimbraAccount.ResetAccountZWC();

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
