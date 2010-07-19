package framework.core;

import framework.core.ZimbraSelenium;

import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;

import org.testng.Assert;

import org.apache.commons.configuration.*;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;

import java.io.File;
import java.util.HashMap;

public class SelNGBase {

	protected SeleniumServer ss;
	public static ZimbraSelenium selenium;
	public static final String WAIT_FOR_PAGE_LOAD = "30000";

	public static Configuration config = null;
	public static String currentBrowserName = "";
	public static HashMap<String, String> expectedValue = new HashMap<String, String>();
	protected RemoteControlConfiguration rcConfig;
	public static int maxRetryCount = 1;
	public static int currentRetryCount = 0;
	public static boolean isExecutionARetry = false;
	public static boolean needReset = false;
	public static String selfAccountName = "";
	public static String someting = " ";
	public static String appType = "AJAX";
	public static String ZimbraVersion = "";
	public static String suiteName = "";
	public static long SMALL_WAIT = 1000;
	public static long MEDIUM_WAIT = 2000;
	public static long LONG_WAIT = 4000;
	public static long VERY_LONG_WAIT = 10000;

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
		rcConfig = new RemoteControlConfiguration();
		rcConfig.setPort(Integer.parseInt(config.getString("serverport")));
		File a = new File("src/java/framework/lib/user-extensions.js");
		rcConfig.setUserExtensions(a.getCanonicalFile());
		ss = new SeleniumServer(false, rcConfig);
		ss.boot();
		expectedValue.clear();
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
		String browser = config.getString("browser");

		if (config.containsKey("small_wait")) {
			SMALL_WAIT = Integer.parseInt(config.getString("small_wait"));
		}
		if (config.containsKey("medium_wait")) {
			MEDIUM_WAIT = Integer.parseInt(config.getString("medium_wait"));
		}
		if (config.containsKey("long_wait")) {
			LONG_WAIT = Integer.parseInt(config.getString("long_wait"));
		}
		if (config.containsKey("very_long_wait")) {
			VERY_LONG_WAIT = Integer.parseInt(config
					.getString("very_long_wait"));
		}

		String serverMachineName = config.getString("serverMachineName");

		String browserLauncher = "";
		if (browser.equals("IE")) {
			browserLauncher = "*iexplore";
		} else if (browser.equals("IEHTA")) {
			browserLauncher = "*iehta";
		} else if (browser.equals("FF")) {
			browserLauncher = "*firefox";
		} else if (browser.equals("IE8")) {
			browserLauncher = "*custom C:\\Program Files\\Internet Explorer\\iexplore.exe";
		} else if (browser.equals("FF3")) {
			browserLauncher = "*custom C:\\Program Files\\Mozilla Firefox\\firefox.exe";
		} else if (browser.equals("SF4")) {
			browserLauncher = "*custom C:\\Program Files\\Safari\\Safari.exe";
		} else if (browser.equals("GC") || browser.equals("googleChrome")) {
			browserLauncher = "*custom 	C:\\Documents and Settings\\"
					+ System.getProperty("user.name")
					+ "\\Local Settings\\Application Data\\Google\\Chrome\\Application\\chrome.exe";
		} else if (browser.equals("SF")) {
			browserLauncher = "*safari C:\\Program Files\\Safari\\Safari.exe";
		} else if (browser.equals("SF-NIGHTLY")) {
			browserLauncher = "*custom C:\\SAFARI-NIGHTLY\\run-nightly-webkit.cmd";
		}

		selenium = new ZimbraSelenium(serverMachineName, 
									  Integer.parseInt(config.getString("serverport")),
									  browserLauncher,
									  getBaseURL());
		selenium.start();
		selenium.windowMaximize();
		selenium.windowFocus();
		selenium.setupZVariables();
		selenium.allowNativeXpath("true");
		selenium.open(getBaseURL());

		/* google's chrome */
		if (browser.equals("GC") || browser.equals("googleChrome")) {
			clickOnGoogleChromePopup();
		}
	}

	public static void customLogin(String parameter) {
		String browser = config.getString("browser");
		String serverMachineName = config.getString("serverMachineName");

		String browserLauncher = "";
		if (browser.equals("IE")) {
			browserLauncher = "*iexplore";
		} else if (browser.equals("IEHTA")) {
			browserLauncher = "*iehta";
		} else if (browser.equals("FF")) {
			browserLauncher = "*firefox";
		} else if (browser.equals("FF3")) {
			browserLauncher = "*firefox";
		} else if (browser.equals("GC") || browser.equals("googleChrome")) {// google's chrome
			browserLauncher = "*custom 	C:\\Documents and Settings\\"
					+ System.getProperty("user.name")
					+ "\\Local Settings\\Application Data\\Google\\Chrome\\Application\\chrome.exe";
		} else if (browser.equals("SF")) {
			browserLauncher = "*safari C:\\Program Files\\Safari\\Safari.exe";
		} else if (browser.equals("SF-NIGHTLY")) {
			browserLauncher = "*custom C:\\SAFARI-NIGHTLY\\run-nightly-webkit.cmd";
		}

		selenium = new ZimbraSelenium(serverMachineName, Integer
				.parseInt(config.getString("serverport")), browserLauncher,
				config.getString("mode") + "://" + config.getString("server")
						+ "/" + parameter);
		selenium.start();
		selenium.windowMaximize();
		selenium.windowFocus();
		selenium.allowNativeXpath("true");
		selenium.open(config.getString("mode") + "://"
				+ config.getString("server") + "/" + parameter);

		if (browser.equals("GC") || browser.equals("googleChrome")) {
			clickOnGoogleChromePopup();
		}
	}

	public static void clickOnGoogleChromePopup() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Robot robot = new Robot();
			robot.mouseMove(500, d.height - 50);// just a hack for now
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		selenium.windowFocus();
		selenium.windowMaximize();
	}

	public static String getBaseURL() {
		if (appType.equals("DESKTOP"))
			return "http://localhost:7633/zimbra/desktop/zmail.jsp";
		else if (appType.equals("HTML"))
			return config.getString("mode") + "://"
					+ config.getString("server") + "/h/";
		else if (appType.equals("MOBILE"))
			return config.getString("mode") + "://"
					+ config.getString("server") + "/m/";
		else
			return config.getString("mode") + "://"
					+ config.getString("server");
	}

	// can be used as @aftermethod
	public void deleteCookie(String name, String path) {
		selenium.deleteCookie(name, path);
	}

	// can be used as @aftermethod
	public void stopSeleniumSession() {
		selenium.stop();
	}

	// Can be used @aftertest
	public void stopSeleniumServer() {
		ss.stop();
	}

	public static void stopClient() {
		selenium.close();
	}

	public static void initFramework(Configuration configfile) {
		config = configfile;

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
