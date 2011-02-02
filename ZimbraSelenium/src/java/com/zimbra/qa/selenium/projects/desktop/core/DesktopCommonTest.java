package com.zimbra.qa.selenium.projects.desktop.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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

import com.zimbra.qa.selenium.projects.desktop.ui.AppDesktopClient;
import com.zimbra.qa.selenium.projects.desktop.ui.PageAccounts;
import com.zimbra.qa.selenium.projects.desktop.ui.PageMain;

import com.thoughtworks.selenium.SeleniumException;

import com.zimbra.qa.selenium.framework.core.ClientSession;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.core.ZimbraSelenium;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.util.BuildUtility;
import com.zimbra.qa.selenium.framework.util.CommandLine;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.OperatingSystem;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraDesktopProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.BuildUtility.ARCH;
import com.zimbra.qa.selenium.framework.util.BuildUtility.BRANCH;
import com.zimbra.qa.selenium.framework.util.BuildUtility.PRODUCT_NAME;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.framework.util.OperatingSystem.OsType;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;

/**
 * Common definitions for all Desktop test cases
 * @author Jeffry Hidayat
 *
 */
public class DesktopCommonTest {
   protected static OsType osType = null;
	protected static Logger logger = LogManager.getLogger(DesktopCommonTest.class);
	protected Map<String, String> startingAccountPreferences = null;

	private String _downloadFilePath = null;
	private String _executableFilePath = null;
	private ARCH _arch = null;
	private final static String _accountFlavor = "Zimbra";
	protected final static String defaultAccountName = ZimbraSeleniumProperties.getUniqueString();
	public static ZimbraSelenium _selenium = null;
	private static boolean _firstTime = true;

	// Configurable from config file or input parameters
	private PRODUCT_NAME _productName = PRODUCT_NAME.ZDESKTOP;
	private BRANCH _branchName = BRANCH.HELIX;
	private boolean _uninstallAppAfterTest = false;
	private boolean _forceInstall = false;

	/**
	 * The AdminConsole application object
	 */
	protected AppDesktopClient app = null;

	/**
	 * BeforeMethod variables
	 * startingPage = the starting page before the test method starts
	 * startingAccount = the account to log in as
	 */
	protected AbsTab startingPage = null;
	protected ZimbraAccount startingAccount = ZimbraAccount.AccountZDC();

	protected DesktopCommonTest() throws HarnessException {
		logger.info("New "+ DesktopCommonTest.class.getCanonicalName());

		app = new AppDesktopClient(startingAccount);

		startingPage = app.zPageMain;
		
		logger.debug("Email Address: " + startingAccount.EmailAddress);
		logger.debug("Email Password: " + startingAccount.Password);
	}

	/**
	 * Global BeforeSuite
	 * 1. Make sure that Desktop Application is installed
	 * 2. Make sure that Desktop Application is initialized
	 * 3. Make sure the selenium server is available
	 * 
	 * @throws HarnessException
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws InterruptedException 
	 */
	@BeforeSuite( groups = { "always" } )
	public void commonTestBeforeSuite() throws HarnessException, SAXException, IOException, InterruptedException {
		logger.info("commonTestBeforeSuite");

		_forceInstall = ZimbraSeleniumProperties.getStringProperty("desktop.forceInstall", "true").toLowerCase().equals("true") ? true : false;
		_uninstallAppAfterTest = ZimbraSeleniumProperties.getStringProperty("desktop.uninstallAfterTest", "false").toLowerCase().equals("true") ? true : false;
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
		      String downloadPath = BuildUtility.downloadLatestBuild(_downloadFilePath, _productName, _branchName, _arch);
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
		   SleepUtil.sleep(30000);
		} else {
		   logger.info("App is already running...");
		}

		try
		{
			ClientSession session = ClientSessionFactory.session();
			_selenium = session.selenium();
			_selenium.start();
			_selenium.windowMaximize();
			_selenium.windowFocus();
			_selenium.allowNativeXpath("true");
			ZimbraSeleniumProperties.setAppType(
			      ZimbraSeleniumProperties.AppType.DESKTOP);
			_selenium.open(ZimbraSeleniumProperties.getBaseURL());
		} catch (SeleniumException e) {
			logger.error("Unable to open admin app." +
					"  Is a valid cert installed?", e);
			throw e;
		}
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
                                            .append(startingAccount.EmailAddress).append("&password=")
                                            .append(startingAccount.Password).append("&host=") 
                                            .append(emailServerName).append("&port=")
                                            .append(emailServerPort).append("&syncFreqSecs=900&debugTraceEnabled=on")
                                            .append(securityType).toString();
      logger.info("accountSetupUrl: " + accountSetupUrl);

      try {
         URL url = new URL(accountSetupUrl);
         URLConnection conn = url.openConnection();
         
         //Get the response
         BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         StringBuffer sb = new StringBuffer();
         String line;
         while ((line = rd.readLine()) != null)
         {
            sb.append(line);
         }
         rd.close();
         logger.info("HTTP POST information ==> " + sb.toString());
         

      } catch (IOException e) {
         throw new HarnessException("HTTP Post method for creating new account failed, please check the parameters");
      }

      String accountUrl = new StringBuilder(serverScheme).append("://")
                                       .append(serverName). append(":")
                                       .append(connectionPort).append("/")
                                       .append("?at=")
                                       .append(zdp.getSerialNumber()).toString();
      logger.debug("Selenium is opening: " + accountUrl);
      _selenium.open(accountUrl);
      ZimbraSeleniumProperties.waitForElementPresent(app.zPageAccounts,
            PageAccounts.Locators.zLoginButton);
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
		startingAccount.authenticateToMailClientHost();
		logger.info("Wait dynamically until the application is loaded");
		boolean isLoaded = (Boolean) GeneralUtility.waitFor("com.zimbra.qa.selenium.framework.ui.AbsApplication.AppDesktopClient",
		      app, false, "zIsLoaded", null, WAIT_FOR_OPERAND.EQ, true, 30000, 1000);
		boolean isPageAccountActive = app.zPageAccounts.zIsActive();

		if (isLoaded) {
   		if (isPageAccountActive) {
   		   logger.info("Account Page is active, so no accounts have been created");
   		} else {
   		   logger.info("Main page is active");
   		   ZimbraSeleniumProperties.waitForElementPresent(app.zPageMain,
   		         PageMain.Locators.zSetupButton);
   		   app.zPageMain.sClick(PageMain.Locators.zSetupButton);
   		   ZimbraSeleniumProperties.waitForElementPresent(app.zPageAccounts,
   		         PageAccounts.Locators.zLoginButton);

   		   boolean bFoundOtherUser = true;
   		   logger.debug("Cleaning up all existing users");

   		   String deleteButtonLocator = null;

   		   // If this is the first time checking, then cleaning up all the pre-existing user
   		   // Otherwise, only cleans the non-default users, which is second user and so on...
   		   // Second user is located in row 3.
   		   if (_firstTime) {
   	         deleteButtonLocator = PageAccounts.Locators.zDeleteButton;
   		   } else {
   		      String[] temp = PageAccounts.Locators.zDeleteButton.trim().split(" ");
   		      deleteButtonLocator = new StringBuffer(temp[0]).append(" tr:nth-child(3)>td div[class^='ZAccount'] ").
   		                              append(temp[1]).toString();
   		   }

   		   while (bFoundOtherUser) {
   		      if (app.zPageAccounts.sIsElementPresent(deleteButtonLocator)) {
   		         app.zPageAccounts.sClick(deleteButtonLocator);
   		         logger.debug("Selenium Confirmation: " + _selenium.getConfirmation());
   		         SleepUtil.sleep(3000);
   		         String nthChildString = "nth-child(3)";
   		         if (deleteButtonLocator.contains(nthChildString)) {
   		            // It is switched from 3 to 4 because after clicking the delete button the first time
   		            // , there will be confirmation message which appears to be on the 3rd row.
   		            deleteButtonLocator = deleteButtonLocator.replace(nthChildString, "nth-child(4)");
   		         }
   		      }

   		      if (!(Boolean)ZimbraSeleniumProperties.waitForElementPresent(app.zPageAccounts,
   		            deleteButtonLocator, 5000)) {
   		         bFoundOtherUser = false;
   		      }
   		   }
   		}
   		addDefaultAccount();

		} else {
		   throw new HarnessException("Nothing is loaded, please check the connection");
		}
		_firstTime = false;
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
      if ( (startingAccountPreferences != null) && (!startingAccountPreferences.isEmpty()) ) {
         logger.debug("commonTestBeforeMethod: startingAccountPreferences are defined");
         ZimbraAccount.AccountZDC().modifyPreferences(startingAccountPreferences,
               SOAP_DESTINATION_HOST_TYPE.CLIENT);
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
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	@AfterSuite( groups = { "always" } )
	public void commonTestAfterSuite() throws HarnessException, IOException, InterruptedException {	
		logger.info("commonTestAfterSuite: start");

		ClientSessionFactory.session().selenium().stop();
		if (_uninstallAppAfterTest) {
		   DesktopInstallUtil.uninstallDesktopApp();
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
	public void commonTestAfterMethod() throws HarnessException {
		logger.info("commonTestAfterMethod: start");

		logger.info("commonTestAfterMethod: finish");
	}

}
