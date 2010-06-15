package framework.core;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;

public class ZimbraSelenium extends DefaultSelenium {

	public String doubleQuote = "\"";
	// general timeout
	private int timeOut = 30000;

	// if true, selenium-call would perform action and immediately return the
	// control
	public static boolean Z_DONT_WAIT = false;
	// if true, selenium-call would just return false upon timeouts
	public static boolean Z_RETURN_FALSE_FOR_TIMEOUTEXPN = false;

	public ZimbraSelenium(String serverHost, int serverPort,
			String browserStartCommand, String browserURL) {
		super(serverHost, serverPort, browserStartCommand, browserURL);
	}

	public String call(String coreFunction, String locator, String action,
			String panel, String param1) {
		return this.call(coreFunction, locator, action, panel, param1, "", "");
	}

	public String call(String coreFunction, String locator, String action,
			String panel, String param1, String param2) {
		return this.call(coreFunction, locator, action, panel, param1, param2, "");
	}	
	public String call(String coreFunction, String locator, String action,
			String panel, String param1, String param2, String param3)
			throws SeleniumException {
		String origAction = action;

		// indicates that the actual object name must start with required obj
		// name(js function should take care of resolving
		if (SelNGBase.labelStartsWith)
			locator = locator + "::labelStartsWith";
		// if we have a field whose label is an object(say a button/menu), then
		// set this to true
		// ex: [menuLabel][editfield] where menuLabel is the label for editField
		if (SelNGBase.fieldLabelIsAnObject)
			locator = locator + "::fieldLabelIsAnObject";
			
		if (SelNGBase.ignoreFolderHdr)
			locator = locator + "::ignoreFolderHdr";
		

		

		if (SelNGBase.actOnLabel)// set this to true if you want to click on the
			// exact label-element
			locator = locator + "::actOnLabel";

		int _timeOut = timeOut;
		if (action.toLowerCase().indexOf("exist") >= 0) {
			if (action.toLowerCase().indexOf("_dontwait") >= 0) {
				action = action.replaceAll("_dontwait", "");
			}

		}

		// try {
		if (SelNGBase.appType.equals("HTML") && (origAction.toLowerCase().indexOf("dontwait") < 0)
				|| ((origAction.toLowerCase().indexOf("dontwait") < 0)
						&& (action.toLowerCase().indexOf("get") < 0)
						&& (action.toLowerCase().indexOf("enabled") < 0)
						&& (action.toLowerCase().indexOf("disabled") < 0)
						&& (action.toLowerCase().indexOf("checked") < 0)
						&& (action.toLowerCase().indexOf("is") != 0) && (action
						.toLowerCase().indexOf("has") != 0))) {

			String jsFunc = "Selenium." + "decorateFunctionWithTimeout" + "("
					+ "fnBind(this.browserbot." + coreFunction
					+ ", this.browserbot," + doubleQuote(locator) + ","
					+ doubleQuote(action) + "," + doubleQuote(panel) + ","
					+ doubleQuote(param1) + "," + doubleQuote(param2) + ","
					+ doubleQuote(param3) + "), " + _timeOut + ")";
			String retval = commandProcessor.doCommand("callFunctions",
					new String[]{jsFunc});

			if (action.indexOf("exist") >= 0) {// convert OK to true and
				retval = "true";
			} else {
				retval = retval.replace("OK,", "");
			}
			return retval;
		} else {
			String jsFunc = "this.browserbot." + coreFunction + "("
					+ doubleQuote(locator) + "," + doubleQuote(action) + ","
					+ doubleQuote(panel) + "," + doubleQuote(param1) + ","
					+ doubleQuote(param2) + "," + doubleQuote(param3) + ")";
			return commandProcessor.getString("getStrFunctions",
					new String[]{jsFunc});
		}

	}

	private String doubleQuote(String str) {
		return doubleQuote + str + doubleQuote;
	}

}
