package com.zimbra.qa.selenium.projects.desktop.core;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import com.thoughtworks.selenium.*;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.BuildUtility.*;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.framework.util.OperatingSystem.OsType;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.desktop.ui.*;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.TreeMail;

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
	protected static Logger logger = LogManager.getLogger(AjaxCommonTest.class);
	public final boolean isRunningDesktopTest = ZimbraSeleniumProperties.getStringProperty(
			ZimbraSeleniumProperties.getLocalHost() + ".desktop.test", "false").toLowerCase().equals("true") ? true : false;

	private static DefaultSelenium _selenium = null;

	/**
	 * The AdminConsole application object
	 */
	protected AppAjaxClient app = null;
	protected static OsType osType = null;
	private String _downloadFilePath = null;
	private String[] _executableFilePath = null;
	private String [] _params = null;
	public final static String accountFlavor = "Zimbra";
	public final static String defaultAccountName = ZimbraSeleniumProperties.getUniqueString();
	public final static String yahooUserName = ZimbraSeleniumProperties.getStringProperty("desktop.yahoo.login");
	public final static String yahooPassword = ZimbraSeleniumProperties.getStringProperty("desktop.yahoo.password");
	public final static String gmailUserName = ZimbraSeleniumProperties.getStringProperty("desktop.gmail.login");
	public final static String gmailPassword = ZimbraSeleniumProperties.getStringProperty("desktop.gmail.password");
	public final static String hotmailUserName = ZimbraSeleniumProperties.getStringProperty("desktop.hotmail.login");
	public final static String hotmailPassword = ZimbraSeleniumProperties.getStringProperty("desktop.hotmail.password");
	public final static String hotmailUserName2 = ZimbraSeleniumProperties.getStringProperty("desktop.hotmail2.login");
	public final static String hotmailPassword2 = ZimbraSeleniumProperties.getStringProperty("desktop.hotmail2.password");
	public final static String gmailImapReceivingServer = "imap.gmail.com";
	public final static String gmailImapSmtpServer = "smtp.gmail.com";
	public final static String hotmailPopReceivingServer = "pop3.live.com";
	public final static String hotmailPopSmtpServer = "smtp.live.com";

	// This variable is to track desktop current account, if new account is created
	// then, desktop has to add that newly created account, while removing the
	// existing ones. For desktop purpose, this cannot use app.zGetActiveAccount
	// because the implementation is different where in Ajax client, active account
	// is set in login and logout, while in desktop, it is only set in addDefaultAccount
	private static ZimbraAccount _currentAccount = null;

	// Configurable from config file or input parameters
	private PRODUCT_NAME _productName = PRODUCT_NAME.ZDESKTOP;
	private BRANCH _branchName = BRANCH.HELIX;
	private ARCH _arch = null;
	private boolean _uninstallAppAfterTest = false;
	private boolean _forceInstall = false;
	protected String[] desktopZimlets = null;
	private static StartDesktopClient _startDesktopClient = null;

	/**
	 * BeforeMethod variables
	 * startingPage = the starting page before the test method starts
	 * startingAccount = the account to log in as
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
	@BeforeSuite(alwaysRun=true)
	public void commonTestBeforeSuite() throws HarnessException, IOException, InterruptedException, SAXException {
		logger.info("commonTestBeforeSuite: start");

		// Make sure there is a new default account
		ZimbraAccount.ResetAccountZWC();

		osType = OperatingSystem.getOSType();

		try
		{
			_selenium = ClientSessionFactory.session().selenium();
			logger.debug("Starting selenium");

			// This is needed only in Mac OS because when selenium invokes the test browser window,
			// the window is not active (in background), thus any methods involving robot will not work
			// properly
			// Also for Mac OS, selenium start has to be at the very beginning in order for the robot to
			// activate the browser
			if (osType == OsType.MAC) {
				_selenium.start();
				app.zPageMain.zMouseClick(100, 100);		   
			}

			if (isRunningDesktopTest) {
				ZimbraSeleniumProperties.setAppType(ZimbraSeleniumProperties.AppType.DESKTOP);


				_forceInstall = ZimbraSeleniumProperties.getStringProperty("desktop.forceInstall", "true").toLowerCase().equals("true") ? true : false;
				_uninstallAppAfterTest = ZimbraSeleniumProperties.getStringProperty("desktop.uninstallAfterTest", "false").toLowerCase().equals("true") ? true : false;

				String productName = ZimbraSeleniumProperties.getStringProperty("desktop.productName", "ZDESKTOP").toUpperCase();
				try {
					logger.info("productName: " + productName);
					_productName = PRODUCT_NAME.valueOf(productName);
				} catch (IllegalArgumentException e) {
					_productName = PRODUCT_NAME.ZDESKTOP;
				}

				String productBranch = ZimbraSeleniumProperties.getStringProperty("desktop.productBranch", "HELIX").toUpperCase();
				try {
					logger.info("productBranch: " + productBranch);
					_branchName = BRANCH.valueOf(productBranch);
				} catch (IllegalArgumentException e) {
					_branchName = BRANCH.HELIX;
				}

				logger.info("_forceInstall: " + _forceInstall);
				logger.info("_uninstallAppAfterTest: " + _uninstallAppAfterTest);
				logger.info("_productName: " + _productName);
				logger.info("_branchName: " + _branchName);
				logger.info("osType: " + osType);

				switch (osType){
				case WINDOWS: case WINDOWS_XP:
					_downloadFilePath = "C:\\download-zimbra-qa-test\\";
					_arch = ARCH.WINDOWS;

					String filePath = "C:\\Program Files (x86)";

					File root = new File(filePath); 
					if (root.exists()) {
						// 64 bit
						_executableFilePath = new String[] {"C:\\WINDOWS\\SysWOW64\\cscript.exe", "C:\\Program Files (x86)\\Zimbra\\Zimbra Desktop\\win32\\zdrun.vbs"};
					} else {
						// 32 bit
						_executableFilePath = new String[] {"C:\\WINDOWS\\system32\\cscript.exe", "C:\\Program Files\\Zimbra\\Zimbra Desktop\\win32\\zdrun.vbs"};
					}

					break;

				case LINUX:

					_downloadFilePath = "/download-zimbra-qa-test/";
					_arch = ARCH.RHEL4;
					String username = ZimbraDesktopProperties.getInstance().getUserName();
					String command = "/opt/zimbra/zdesktop/linux/prism/zdclient -webapp /home/<USER_NAME>/zdesktop/zdesktop.webapp -override /home/<USER_NAME>/zdesktop/zdesktop.webapp/override.ini -profile /home/<USER_NAME>/zdesktop/profile";
					command = command.replaceAll("<USER_NAME>", username);

					_executableFilePath = new String[] {"su", "-", username, "-c", command}; 
					_params = null;
					break;

				case MAC:
					_downloadFilePath = "/download-zimbra-qa-test/";
					_arch = ARCH.MACOSX_X86_10_6;
					username = ZimbraDesktopProperties.getInstance().getUserName();
					command = "/Applications/Zimbra\\ Desktop/Zimbra\\ Desktop.app/Contents/MacOS/zdrun";

					_executableFilePath = new String[] {"su", "-", username, "-c", command};
					_params = null;

				}

				logger.info("_forceInstall: " + _forceInstall);
				if (_forceInstall) {
					DesktopInstallUtil.forceInstallLatestBuild(_productName, _branchName, _arch, _downloadFilePath);
				} else {
					if (!DesktopInstallUtil.isDesktopAppInstalled()) {
						String buildUrl = ZimbraSeleniumProperties.getStringProperty("desktop.buildUrl", ""); 
						String downloadPath = null;

						if (buildUrl.equals("")) {
							downloadPath = BuildUtility.downloadLatestBuild(_downloadFilePath, _productName, _branchName, _arch);             
						} else {
							downloadPath = BuildUtility.downloadBuild(_downloadFilePath, buildUrl);
						}

						logger.info("Now installing: " + downloadPath);
						DesktopInstallUtil.installDesktopApp(downloadPath);
					} else {
						// Running test with already installed Desktop App.
						logger.info("Running with already installed app");
					}
				}

				if (!DesktopInstallUtil.isDesktopAppRunning()) {
					logger.info("Executable file path: " + Arrays.toString(_executableFilePath));
					_startDesktopClient = new StartDesktopClient(_executableFilePath, _params);
					_startDesktopClient.start();

				} else {
					logger.info("App is already running...");
				}


				GeneralUtility.waitFor(null, ZimbraAccount.AccountZWC(), false,
						"authenticateToMailClientHost", null, WAIT_FOR_OPERAND.NEQ, null, 60000, 3000);

			} else {
				// AJAX test
				ZimbraSeleniumProperties.setAppType(ZimbraSeleniumProperties.AppType.AJAX);
			}

			// For non Mac OS, selenium start is done after the installation and app initialization.
			if (osType != OsType.MAC) {
				_selenium.start();
			}

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
			throw new HarnessException("Error in Before Suite", e);
		}

		logger.info("commonTestBeforeSuite: finish");		
	}

	/**
	 * Global BeforeClass
	 *
	 * @throws HarnessException
	 */
	@BeforeClass(alwaysRun=true)
	public void commonTestBeforeClass() throws HarnessException {
		logger.info("commonTestBeforeClass: start");

		if (isRunningDesktopTest) {
			logger.info("Wait dynamically until the application is loaded");
			boolean isLoaded = (Boolean) GeneralUtility.waitFor(null,
					app, false, "zIsLoaded", null, WAIT_FOR_OPERAND.EQ, true, 30000, 1000);

			// Navigating to login page is important because for new App is created
			// in each different class, and tests are using zGetActiveAcount.
			// The only way zSetActiveAccount is called is whenever logging in
			// and logging out, so unlike Ajax, Desktop is comparing the AccountZWC
			// with the current added account, not necessarily ActiveAccount in Ajax
			app.zPageLogin.zNavigateTo();

			if (!isLoaded) {
				throw new HarnessException("Nothing is loaded, please check the connection");
			}
		}
		logger.info("commonTestBeforeClass: finish");

	}

  /**
    * Going to login page, then going back to the starting page
    * @throws HarnessException
    */
   public void relogin() throws HarnessException {
      app.zPageLogin.zNavigateTo();
      startingPage.zNavigateTo();
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
		.append(accountFlavor).append("&accountName=")
		.append(defaultAccountName).append("&email=")
		.append(ZimbraAccount.AccountZWC().EmailAddress).append("&password=")
		.append(ZimbraAccount.AccountZWC().Password).append("&host=") 
		.append(emailServerName).append("&port=")
		.append(emailServerPort).append("&syncFreqSecs=900&debugTraceEnabled=on")
		.append(securityType).toString();
		//.append("&dev=1&scripterrors=1").toString();
		logger.info("accountSetupUrl: " + accountSetupUrl);
		GeneralUtility.doHttpPost(accountSetupUrl);

		String accountUrl = new StringBuilder(serverScheme).append("://")
		.append(serverName). append(":")
		.append(connectionPort).append("/")
		.append("?at=")
		.append(zdp.getSerialNumber()).toString();
		//append("&dev=1&scripterrors=1").toString();
		logger.debug("Selenium is opening: " + accountUrl);
		logger.debug("Selenium is: " + _selenium);
		_selenium.open(accountUrl);
		GeneralUtility.waitForElementPresent(app.zPageLogin,
				PageLogin.Locators.zBtnLoginDesktop);
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
	@BeforeMethod(alwaysRun=true)
	public void commonTestBeforeMethod() throws HarnessException {
		logger.info("commonTestBeforeMethod: start");

		SOAP_DESTINATION_HOST_TYPE destType = null;
		AppType appType = ZimbraSeleniumProperties.getAppType(); 
		switch (appType) {
		case AJAX:
			destType = SOAP_DESTINATION_HOST_TYPE.SERVER;
			break;

		case DESKTOP:
			destType = SOAP_DESTINATION_HOST_TYPE.CLIENT;

			if (_currentAccount != ZimbraAccount.AccountZWC()) {
				app.zPageLogin.zNavigateTo();

				if  (app.zPageLogin.sIsElementPresent(PageLogin.Locators.zBtnLoginDesktop)) {
					boolean bFoundOtherUser = true;
					logger.debug("Cleaning up all existing users");

					String deleteButtonLocator = null;

					// If this is the first time checking, then cleaning up all the pre-existing user
					// Otherwise, only cleans the non-default users, which is second user and so on...
					// Second user is located in row 3.
					if (_currentAccount != ZimbraAccount.AccountZWC()) {
						deleteButtonLocator = PageLogin.Locators.zDeleteButton;
					} else {
						String[] temp = PageLogin.Locators.zDeleteButton.trim().split(" ");
						deleteButtonLocator = new StringBuffer(temp[0]).append(" tr:nth-child(3)>td div[class^='ZAccount'] ").
						append(temp[1]).toString();
					}

					int maxRetry = 30;
					int retry = 0;
					while (bFoundOtherUser && retry < maxRetry) {
						SleepUtil.sleepSmall();
						if (app.zPageLogin.sIsElementPresent(PageLogin.Locators.zMyAccountsTab)) {
							app.zPageLogin.sClick(PageLogin.Locators.zMyAccountsTab);
							GeneralUtility.waitForElementPresent(app.zPageLogin,
									PageLogin.Locators.zBtnLoginDesktop);
						}

						if (app.zPageLogin.sIsElementPresent(deleteButtonLocator)) {
							String attribute = app.zPageLogin.sGetAttribute(deleteButtonLocator + "@href");
							String accountId = attribute.split("'")[1];
							String accountName = attribute.split("'")[3];
							String accountFlavor = attribute.split("'")[5];
							String accountType = attribute.split("'")[7];
							app.zDeleteDesktopAccount(accountName, accountId, accountType, accountType);

							String nthChildString = "nth-child(3)";
							if (deleteButtonLocator.contains(nthChildString)) {
								// It is switched from 3 to 4 because after clicking the delete button the first time
								// , there will be confirmation message which appears to be on the 3rd row.
								deleteButtonLocator = deleteButtonLocator.replace(nthChildString, "nth-child(4)");
							}
						}

						if (!(Boolean)GeneralUtility.waitForElementPresent(app.zPageLogin,
								deleteButtonLocator, 5000)) {
							bFoundOtherUser = false;
						}
						retry++;
					}

					if (retry == maxRetry) {
						throw new HarnessException("Retry deleting the user timed out");
					}
				}
				if (startingPage != app.zPageAddNewAccount) {
					addDefaultAccount();
					_currentAccount = ZimbraAccount.AccountZWC();               
				}
			}

			if (startingPage != app.zPageAddNewAccount) {
			   ZimbraAdminAccount.GlobalAdmin().authenticateToMailClientHost();
				ZimbraAccount.AccountZWC().authenticateToMailClientHost();
			}

			break;

		default:
			throw new HarnessException("Please add a support for appType: " + appType);
		}

		// If test account preferences are defined, then make sure the test account
		// uses those preferences
		//
		if ( (startingAccountPreferences != null) && (!startingAccountPreferences.isEmpty()) ) {
			logger.debug("commonTestBeforeMethod: startingAccountPreferences are defined");
         ZimbraAccount.AccountZWC().modifyPreferences(startingAccountPreferences, destType);

         /**StringBuilder settings = new StringBuilder();
			for (Map.Entry<String, String> entry : startingAccountPreferences.entrySet()) {
				settings.append(String.format("<a n='%s'>%s</a>", entry.getKey(), entry.getValue()));
			}
			ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<ModifyAccountRequest xmlns='urn:zimbraAdmin'>"
					+		"<id>"+ ZimbraAccount.AccountZWC().ZimbraId +"</id>"
					+		settings.toString()
					+	"</ModifyAccountRequest>", destType);

*/
			// Set the flag so the account is reset for the next test
			ZimbraAccount.AccountZWC().accountIsDirty = true;
		}

		// If test account zimlet preferences are defined, then make sure the test account
		// uses those zimlet preferences
		//
		if ( (startingAccountZimletPreferences != null) && (!startingAccountZimletPreferences.isEmpty()) ) {
			logger.debug("commonTestBeforeMethod: startingAccountPreferences are defined");
			ZimbraAccount.AccountZWC().modifyZimletPreferences(startingAccountZimletPreferences,
					destType);
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

			if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP &&
					startingPage != app.zPageLogin &&
					startingPage != app.zPageAddNewAccount) {
				if (desktopZimlets == null) {
					desktopZimlets = app.zGetActiveAccount().getAvailableZimlets(
							SOAP_DESTINATION_HOST_TYPE.CLIENT);
				}
				logger.debug("Desktop Zimlets are: ");
				for (int i = 0; i < desktopZimlets.length; i++) {
					logger.debug("==> Zimlet " + i + " is: " + desktopZimlets[i]);
				}

				app.zTreeMail.zExpandAll();

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
	@AfterSuite(alwaysRun=true)
	public void commonTestAfterSuite() throws HarnessException {	
		logger.info("commonTestAfterSuite: start");

		// Only for linux, kill the desktop process because
		// in linux, the app is holding up the thread
		if (OperatingSystem.getOSType() == OsType.LINUX ||
				OperatingSystem.getOSType() == OsType.MAC) {
			DesktopInstallUtil.killDesktopProcess();
		}

		_startDesktopClient = null;

		ClientSessionFactory.session().selenium().stop();

		logger.info("commonTestAfterSuite: finish");



	}

	/**
	 * Global AfterClass
	 * 
	 * @throws HarnessException
	 */
	@AfterClass(alwaysRun=true)
	public void commonTestAfterClass() throws HarnessException {
		logger.info("commonTestAfterClass: start");

		logger.info("commonTestAfterClass: finish");
	}

	/**
	 * Global AfterMethod
	 * 
	 * @throws HarnessException
	 */
	@AfterMethod(alwaysRun=true)
	public void commonTestAfterMethod() throws HarnessException {
		logger.info("commonTestAfterMethod: start");

		// For Ajax, if account is considered dirty (modified),
		// then recreate a new account, but for desktop, the zimlet
		// preferences has to be reset to default, all core zimlets are enabled
		ZimbraAccount currentAccount = app.zGetActiveAccount();

		if (currentAccount != null) {
			if (startingPage != app.zPageLogin &&
					startingPage != app.zPageAddNewAccount &&
					desktopZimlets == null) {
				throw new HarnessException("Desktop zimlets are null for unknown reason");
			}

			// Reset the zimlets preferences to default
			Map<String, String> defaultZimlets = new HashMap<String, String>();

			for (int i = 0; i < desktopZimlets.length; i++) {
				defaultZimlets.put(desktopZimlets[i], "enabled");
			}

			currentAccount.authenticateToMailClientHost();
			currentAccount.modifyZimletPreferences(defaultZimlets,
					SOAP_DESTINATION_HOST_TYPE.CLIENT);

		}

		logger.info("commonTestAfterMethod: finish");
	}

}
