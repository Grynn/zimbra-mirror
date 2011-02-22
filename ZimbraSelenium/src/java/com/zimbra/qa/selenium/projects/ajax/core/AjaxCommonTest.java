package com.zimbra.qa.selenium.projects.ajax.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
import com.zimbra.qa.selenium.framework.core.ZimbraSelenium;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.util.CodeCoverage;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.BuildUtility;
import com.zimbra.qa.selenium.framework.util.CommandLine;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.OperatingSystem;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraDesktopProperties;
import com.zimbra.qa.selenium.framework.util.BuildUtility.ARCH;
import com.zimbra.qa.selenium.framework.util.BuildUtility.BRANCH;
import com.zimbra.qa.selenium.framework.util.BuildUtility.PRODUCT_NAME;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.framework.util.OperatingSystem.OsType;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.PageLogin;
import com.zimbra.qa.selenium.projects.desktop.core.DesktopInstallUtil;

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
   private String _executableFilePath = null;
   private final static String _accountFlavor = "Zimbra";
   public final static String defaultAccountName = ZimbraSeleniumProperties.getUniqueString();

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

	/**
	 * BeforeMethod variables
	 * startingPage = the starting page before the test method starts
	 * startingAccount = the account to log in as
	 */
	protected AbsTab startingPage = null;
	protected Map<String, String> startingAccountPreferences = null;
	
	protected AjaxCommonTest() {
		logger.info("New "+ AjaxCommonTest.class.getCanonicalName());
		
		app = new AppAjaxClient();
		
		startingPage = app.zPageMain;
		startingAccountPreferences = new HashMap<String, String>();
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
	public void commonTestBeforeSuite() throws HarnessException, IOException, InterruptedException, SAXException {
		logger.info("commonTestBeforeSuite: start");

		// Make sure there is a new default account
		ZimbraAccount.ResetAccountZWC();

		try
		{
		   if (isRunningDesktopTest) {
		      ZimbraSeleniumProperties.setAppType(ZimbraSeleniumProperties.AppType.DESKTOP);
		      logger.info("commonTestBeforeSuite");

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

		      osType = OperatingSystem.getOSType();

		      boolean isAppRunning = false;
		      switch (osType){
		      case WINDOWS: case WINDOWS_XP:
		         _downloadFilePath = "C:\\download-zimbra-qa-test\\";
		         _arch = ARCH.WINDOWS;

		         String filePath = "C:\\Program Files (x86)";

		         File root = new File(filePath); 
		         if (root.exists()) {
		            // 64 bit
		            _executableFilePath = "C:\\WINDOWS\\SysWOW64\\cscript.exe \"C:\\Program Files (x86)\\Zimbra\\Zimbra Desktop\\win32\\zdrun.vbs\"";
		         } else {
		            // 32 bit
		            _executableFilePath = "C:\\WINDOWS\\system32\\cscript.exe \"C:\\Program Files\\Zimbra\\Zimbra Desktop\\win32\\zdrun.vbs\"";
		         }

		         if (GeneralUtility.findWindowsRunningTask("zdesktop.exe")) {
		            isAppRunning = true;
		         }

		         break;

		      case LINUX:
		         _arch = ARCH.RHEL4;
		         //TODO: _executableFilePath
		         break;

		      case MAC:
		         _arch = ARCH.MACOSX_X86_10_6;
		         //TODO: _executableFilePath
		      }

		      logger.info("_forceInstall: " + _forceInstall);
		      if (_forceInstall) {
		         DesktopInstallUtil.forceInstallLatestBuild(_productName, _branchName, _arch, _downloadFilePath);
		         isAppRunning = false;
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

		      if (!isAppRunning) {
		         logger.info("Executable file path: " + _executableFilePath);
		         CommandLine.CmdExec(_executableFilePath);
		      } else {
		         logger.info("App is already running...");
		      }

		      GeneralUtility.waitFor(null, ZimbraAccount.AccountZWC(), false,
		            "authenticateToMailClientHost", null, WAIT_FOR_OPERAND.NEQ, null, 30000, 3000);
		   } else {
		      // AJAX test
		      ZimbraSeleniumProperties.setAppType(ZimbraSeleniumProperties.AppType.AJAX);
		   }

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

      String accountUrl = new StringBuilder(serverScheme).append("://")
            .append(serverName). append(":")
            .append(connectionPort).append("/")
            .append("?at=")
            .append(zdp.getSerialNumber()).toString();

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
         ZimbraAccount.AccountZWC().authenticateToMailClientHost();
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
                     deleteDesktopAccount(accountName, accountId, "Zimbra", _accountFlavor);

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
            addDefaultAccount();
            _currentAccount = ZimbraAccount.AccountZWC();
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
      }
		
      // If AccountZWC is not currently logged in, then login now
      if ( !ZimbraAccount.AccountZWC().equals(app.zGetActiveAccount()) ) {
         logger.debug("commonTestBeforeMethod: AccountZWC is not currently logged in");

         switch (appType) {
         case AJAX:
            if ( app.zPageMain.zIsActive() )
               app.zPageMain.zLogout();
            app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());
			   
            // Confirm
            if ( !ZimbraAccount.AccountZWC().equals(app.zGetActiveAccount())) {
               throw new HarnessException("Unable to authenticate as "+ ZimbraAccount.AccountZWC().EmailAddress);
            }
            break;
         case DESKTOP:
            // Fall Through
            break;
         default:
            throw new HarnessException("Please add a support for appType: " + appType);
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
		
		CodeCoverage.getInstance().writeCoverage();
		
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
		
		CodeCoverage.getInstance().calculateCoverage();

		logger.info("commonTestAfterMethod: finish");
	}

}
