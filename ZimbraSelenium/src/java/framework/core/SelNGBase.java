package framework.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;

import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.Assert;

import framework.util.ZimbraSeleniumLogger;
import framework.util.ZimbraSeleniumProperties;

public class SelNGBase {

	protected SeleniumServer ss;
	public static ZimbraSelenium selenium;
	public static  String WAIT_FOR_PAGE_LOAD = "30000";

	public static String currentBrowserName = "";
	public static HashMap<String, String> expectedValue = new HashMap<String, String>();
	protected RemoteControlConfiguration rcConfig;
	public static int maxRetryCount = 0;
	public static int currentRetryCount = 0;
	public static boolean isExecutionARetry = false;
	public static boolean needReset = false;
	public static String selfAccountName = "";
	public static String someting = " ";
	public static String appType = "AJAX";
	public static String suiteName = "";
	public static long SMALL_WAIT = 1000;
	public static long MEDIUM_WAIT = 2000;
	public static long LONG_WAIT = 4000;
	public static long VERY_LONG_WAIT = 10000;
	
    public static Map<String, ArrayList<Integer>> FILENAME_TO_COVERAGE = new HashMap<String, ArrayList<Integer>>();
    public static Map<String, JSONArray> FILENAME_TO_SOURCE = new HashMap<String, JSONArray>();


	/**
	 * indicates that the actual object name must start with required obj name
	 */
	public static boolean labelStartsWith = false;

	/**
	 * @fieldLabelIsAnObject if true, searches for edit/textArea's associated
	 *                       label on menu/button ex: [menuLabel][editfield]
	 *                       where menuLabel is the label for editField
	 */
	public static boolean fieldLabelIsAnObject = false;

	/**
	 * actOnLabel: //set this to true if you want to click on the exact
	 * label-element currently used by listItemCore function
	 */
	public static boolean actOnLabel = false;

	/**
	 * dontMatchHeader: //set this to true if you want to ignore Folder header.
	 * Comes in handy when you have a header('Calendars') and a
	 * folder('Calendar') and you want to act on 'Calendar'-folder.
	 */
	public static boolean ignoreFolderHdr = false;

	// can be used @beforeTest
	public void startSeleniumServer() throws Exception {
		if (ZimbraSeleniumProperties.getStringProperty("serverMachineName").toLowerCase().equals("localhost")){
			CmdExec("taskkill /f /t /im iexplore.exe");
			CmdExec("taskkill /f /t /im firefox.exe");
			CmdExec("taskkill /f /t /im Safari.exe");
			CmdExec("taskkill /f /t /im chrome.exe");
			rcConfig = new RemoteControlConfiguration();
			rcConfig.setPort(Integer.parseInt(ZimbraSeleniumProperties.getStringProperty("serverPort", "4444")));
			rcConfig.setUserExtensions(new File("src/java/framework/lib/user-extensions.js"));
			ss = new SeleniumServer(false, rcConfig);
			if(ZimbraSeleniumProperties.getStringProperty("runCodeCoverage", "no").equalsIgnoreCase("yes")) {
				WAIT_FOR_PAGE_LOAD="90000";
			}

			
			try{
				URL stopUrl;
				stopUrl = new URL("http://localhost:" +
						ZimbraSeleniumProperties.getStringProperty("serverPort", "4444") +
						"/selenium-server/driver/?cmd=shutDownSeleniumServer");
				BufferedReader in = new BufferedReader(new InputStreamReader(stopUrl.openStream()));
	
				while (in.readLine() != null)
					ZimbraSeleniumLogger.mLog.info("A Selenium Server was running already." +
							" Attempting to kill and start then");
				in.close();
				Thread.sleep(10000);
			} catch (Exception e) {
				// Server was not running, ignore
			}
			try{
				ss.boot();
			} catch (Exception e){
				// TODO: Couldn't kill running RC, we will try to reuse it to 
				// avoid skips for now but that is not the best approach
			}
		}
	}
	
	// can be used as @aftermethod
	public static void stopSeleniumSession() {
		if (selenium != null){
			selenium.stop();
		}
	}

	// Can be used @aftertest
	public void stopSeleniumServer() {
		if (ZimbraSeleniumProperties.getStringProperty("serverMachineName").toLowerCase().equals("localhost")){
			ss.stop();
			try {
				URL stopUrl;
				stopUrl = new URL("http://localhost:" +
						ZimbraSeleniumProperties.getStringProperty("serverPort", "4444") +
						"/selenium-server/driver/?cmd=shutDownSeleniumServer");
				BufferedReader in = new BufferedReader(new InputStreamReader(stopUrl.openStream()));
		
				while (in.readLine() != null)
					ZimbraSeleniumLogger.mLog.info("A Selenium Server was not stopped. Attempting to kill");
				in.close();
			} catch (IOException e) {
				// Selenium server must be down already, ignore
			}
		}
	}


	public void openApplication() {
		openApplication("AJAX");
	}

	public void openZimbraHTML() {
		openApplication("HTML");
	}

	public void openZimbraMobile() {
		openApplication("MOBILE");
	}

	public void openZimbraDesktop() {
		openApplication("DESKTOP");
	}

	public void openApplication(String app_type) {
		appType = app_type;


		SMALL_WAIT = ZimbraSeleniumProperties.getIntProperty("small_wait", 1000);
		MEDIUM_WAIT = ZimbraSeleniumProperties.getIntProperty("medium_wait", 2000);
		LONG_WAIT = ZimbraSeleniumProperties.getIntProperty("long_wait", 4000);
		VERY_LONG_WAIT = ZimbraSeleniumProperties.getIntProperty("very_long_wait", 10000);

		String serverMachineName = ZimbraSeleniumProperties.getStringProperty("serverMachineName");
		Integer serverPort = Integer.parseInt(ZimbraSeleniumProperties.getStringProperty("serverPort", "4444"));
		String browser = ZimbraSeleniumProperties.getStringProperty("browser");
		String browserVersion = ZimbraSeleniumProperties.getStringProperty("browserVersion");
		
		if (serverMachineName.toLowerCase().equals("sauceondemand")){
			serverMachineName = "ondemand.saucelabs.com";
			serverPort = 80;
			String browserFinal = "{\"username\": \"" + ZimbraSeleniumProperties.getStringProperty("sauceUsername") + "\"," +
						          "\"access-key\": \"" + ZimbraSeleniumProperties.getStringProperty("sauceAccessKey") + "\"," +
						          "\"os\": \"" + ZimbraSeleniumProperties.getStringProperty("OS", "Windows 2003") + "\"," +
						          "\"browser\": \"" + browser + "\"," +
						          "\"browser-version\": \"" + browserVersion + "\"," +
			/* TODO: Adding the job name would be useful for finding the test videos in OnDemand
						          "\"job-name\": \"" + 	Current method or class name + "\"," +  */
						          "\"user-extensions-url\": \"http://" + ZimbraSeleniumProperties.getStringProperty("server") + ":8080/user-extensions.js\"}";
			browser = browserFinal;
		};

		selenium = new ZimbraSelenium(serverMachineName, 
									  serverPort,
									  browser,
									  getBaseURL());
		selenium.start();
		selenium.windowMaximize();
		selenium.windowFocus();
		selenium.setupZVariables();
		selenium.allowNativeXpath("true");
		selenium.open(getBaseURL());

	}

	public static void customLogin(String parameter) {

		String serverMachineName = ZimbraSeleniumProperties.getStringProperty("serverMachineName");
		Integer serverPort = Integer.parseInt(ZimbraSeleniumProperties.getStringProperty("serverPort", "4444"));
		String browser = ZimbraSeleniumProperties.getStringProperty("browser");
		String browserVersion = ZimbraSeleniumProperties.getStringProperty("browserVersion");
		
		if (serverMachineName.toLowerCase().equals("sauceondemand")){
			serverMachineName = "ondemand.saucelabs.com";
			serverPort = 80;
			browser = "{\"username\": \"" + ZimbraSeleniumProperties.getStringProperty("sauceUsername") + "\"," +
					  "\"access-key\": \"" + ZimbraSeleniumProperties.getStringProperty("sauceAccessKey") + "\"," +
					  "\"os\": \"" + ZimbraSeleniumProperties.getStringProperty("OS", "Windows 2003") + "\"," +
			          "\"browser\": \"" + browser + "\"," +
				      "\"browser-version\": \"" + browserVersion + "\"}";
			/* TODO: Adding the job name would be useful for finding the test videos in OnDemand
						          "\"job-name\": \"" + 	Current method or class name + "\"}";  */
		} else {
			if (!browser.startsWith("*")){
				browser = "*" + browser;
			}
		}


		selenium = new ZimbraSelenium(serverMachineName, 
									  serverPort,
									  browser,
									  ZimbraSeleniumProperties.getStringProperty("mode") + "://" + ZimbraSeleniumProperties.getStringProperty("server")	+ "/" + parameter);
		selenium.start();
		selenium.windowMaximize();
		selenium.windowFocus();
		selenium.allowNativeXpath("true");
		selenium.open(ZimbraSeleniumProperties.getStringProperty("mode") + "://"	+ ZimbraSeleniumProperties.getStringProperty("server") + "/" + parameter);
	}

	public static String getBaseURL() {
		if (appType.equals("DESKTOP"))
			return "http://localhost:7633/zimbra/desktop/zmail.jsp";
		else if (appType.equals("HTML"))
			return ZimbraSeleniumProperties.getStringProperty("mode") + "://"
					+ ZimbraSeleniumProperties.getStringProperty("server") + "/h/";
		else if (appType.equals("MOBILE"))
			return ZimbraSeleniumProperties.getStringProperty("mode") + "://"
					+ ZimbraSeleniumProperties.getStringProperty("server") + "/m/";
		else if(ZimbraSeleniumProperties.getStringProperty("runCodeCoverage", "no").equalsIgnoreCase("yes")) 
			return ZimbraSeleniumProperties.getStringProperty("mode") + "://"
					+ ZimbraSeleniumProperties.getStringProperty("server") + "?dev=1";
			else
				return ZimbraSeleniumProperties.getStringProperty("mode") + "://"
				+ ZimbraSeleniumProperties.getStringProperty("server") + "";
	}

	// can be used as @aftermethod
	public void deleteCookie(String name, String path) {
		selenium.deleteCookie(name, path);
	}

	public static void stopClient() {
		selenium.close();
	}


	protected void fail(String name) {
		Assert.fail(name);
	}

	protected void fail(String name, Throwable t) {
		if (t instanceof RuntimeException) {
			throw (RuntimeException) t;
		} else {
			throw new RuntimeException(t);
		}

	}

	public static void CmdExec(String str) {
		try {
			Process p = Runtime.getRuntime().exec(str);
			p.waitFor();
			System.out.println(p.exitValue());
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}
