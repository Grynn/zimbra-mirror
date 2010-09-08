package framework.core;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.Assert;

import framework.util.ZimbraSeleniumProperties;

public class SelNGBase {
	private static Logger logger = LogManager.getLogger(SelNGBase.class);
	

	public static String currentBrowserName = "";
	
	public static int maxRetryCount = 0;
	public static int currentRetryCount = 0;
	public static String someting = " ";
	public static String appType = "AJAX";
	public static String suiteName = "";
	
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

	
	public SelNGBase() {
		logger.debug("New SelNGBase");
	}
	
	// can be used as @aftermethod
	public static void stopSeleniumSession() {
		if (ClientSessionFactory.session().selenium() != null){
			ClientSessionFactory.session().selenium().stop();
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
		
		ClientSession session = ClientSessionFactory.session();
		ZimbraSelenium selenium = session.selenium();
		selenium.start();
		selenium.windowMaximize();
		selenium.windowFocus();
		selenium.setupZVariables();
		selenium.allowNativeXpath("true");
		selenium.open(getBaseURL());
		
	}

	public static void customLogin(String parameter) {


		// TODO: is this needed?  It is not specified in openApplication()
		String browser = SeleniumService.getInstance().getSeleniumBrowser();
		if (!browser.startsWith("*")){
			browser = "*" + browser;
		}

		ClientSession session = ClientSessionFactory.session();
		ZimbraSelenium selenium = session.selenium();
		
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
					+ ZimbraSeleniumProperties.getStringProperty("server") + "?dev=1&debug=0";
			else
				return ZimbraSeleniumProperties.getStringProperty("mode") + "://"
				+ ZimbraSeleniumProperties.getStringProperty("server") + "";
	}

	// can be used as @aftermethod
	public void deleteCookie(String name, String path) {
		ClientSessionFactory.session().selenium().deleteCookie(name, path);
	}

	public static void stopClient() {
		ClientSessionFactory.session().selenium().close();
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


	
	public static ThreadLocal<Boolean> isExecutionARetry = new ThreadLocal<Boolean>() {
		protected synchronized Boolean initialValue() {
			// return Boolean value per thread
			boolean retry = false;
			return retry;
		}
	};

	public static ThreadLocal<Boolean> needReset = new ThreadLocal<Boolean>() {
		protected synchronized Boolean initialValue() {
			// return Boolean value per thread
			boolean reset = false;
			return reset;
		}
	};
	
	public static ThreadLocal<String> selfAccountName = new ThreadLocal<String>() {
		protected synchronized String initialValue() {
			String username = "";
			return new String(username);
		}
	};
}
