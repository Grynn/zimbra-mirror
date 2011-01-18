package com.zimbra.qa.selenium.projects.desktop.core;

import java.io.File;
import java.io.IOException;

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

import com.thoughtworks.selenium.SeleniumException;

import com.zimbra.qa.selenium.framework.core.ClientSession;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.core.ZimbraSelenium;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.util.BuildUtility;
import com.zimbra.qa.selenium.framework.util.CommandLine;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.OperatingSystem;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.BuildUtility.ARCH;
import com.zimbra.qa.selenium.framework.util.BuildUtility.BRANCH;
import com.zimbra.qa.selenium.framework.util.BuildUtility.PRODUCT_NAME;
import com.zimbra.qa.selenium.framework.util.OperatingSystem.OsType;

/**
 * Common definitions for all Desktop test cases
 * @author Jeffry Hidayat
 *
 */
public class DesktopCommonTest {
   protected static OsType osType = null;
	protected static Logger logger = LogManager.getLogger(DesktopCommonTest.class);
	private String _downloadFilePath = null;
	private String _executableFilePath = null;
	private PRODUCT_NAME _productName = null;
	private BRANCH _branchName = null;
	private ARCH _arch = null;
	private boolean _uninstallAppAfterTest = true;

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
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws InterruptedException 
	 */
	@BeforeSuite( groups = { "always" } )
	public void commonTestBeforeSuite() throws HarnessException, SAXException, IOException, InterruptedException {
		logger.info("commonTestBeforeSuite");
		osType = OperatingSystem.getOSType();
		_productName = PRODUCT_NAME.ZDESKTOP;
		_branchName = BRANCH.HELIX;
		boolean isAppRunning = false;
		switch (osType){
		case WINDOWS: case WINDOWS_XP:
		   _downloadFilePath = "C:\\download-zimbra-qa-test\\";
		   _arch = ARCH.WINDOWS;

		   File root = new File("C:\\Program Files (x86)"); 
		   if (root.exists()) {
	         _executableFilePath = "C:\\WINDOWS\\SysWOW64\\cscript.exe \"C:\\Program Files (x86)\\Zimbra\\Zimbra Desktop\\win32\\zdrun.vbs\"";
	      } else {
	         // TODO: Find out for 32 bit system
	         _executableFilePath = "C:\\WINDOWS\\SysWOW64\\cscript.exe \"C:\\Program Files (x86)\\Zimbra\\Zimbra Desktop\\win32\\zdrun.vbs\"";
	      }

		   if (GeneralUtility.findWindowsRunningTask("zdesktop.exe")) {
		      isAppRunning = true;
		   }

		   File file = new File(_downloadFilePath);
		   if (!file.exists()) {
		      logger.info("Creating directory " + _downloadFilePath + "...");
		      file.mkdir();
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

		if (!DesktopInstallUtil.isDesktopAppInstalled()) {
		   String downloadPath = BuildUtility.downloadLatestBuild(_downloadFilePath, _productName, _branchName, _arch);
		   logger.info("Now installing: " + downloadPath);
		   DesktopInstallUtil.installDesktopApp(downloadPath);
		} else {
		   // Running test with already installed Desktop App.
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
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	@AfterSuite( groups = { "always" } )
	public void commonTestAfterSuite() throws HarnessException, IOException, InterruptedException {	
		logger.info("commonTestAfterSuite: start");

		ClientSessionFactory.session().selenium().stop();
		if (_uninstallAppAfterTest) {
		   switch (osType) {
		   case WINDOWS: case WINDOWS_XP:
		      CommandLine.CmdExec("TASKKILL /F /IM zdclient.exe");
		      CommandLine.CmdExec("TASKKILL /F /IM zdesktop.exe");
		      break;
		   case LINUX: case MAC:
		      // TODO: Terminate the running services of Zimbra Desktop
		   }
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
