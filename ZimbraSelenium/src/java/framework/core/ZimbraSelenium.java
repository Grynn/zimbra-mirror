package framework.core;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;

import framework.util.SleepUtil;

public class ZimbraSelenium extends DefaultSelenium {

	public String doubleQuote = "\"";

	public ZimbraSelenium(String serverHost, int serverPort,
			String browserStartCommand, String browserURL) {
		super(serverHost, serverPort, browserStartCommand, browserURL);
	}
	
	public String call(String coreFunction, String locator, String action, Boolean retryOnFalse) {
		return this.call(coreFunction, locator, action, retryOnFalse, "", "");
	}
	public String call(String coreFunction, String locator, String action, Boolean retryOnFalse,
			String panel, String param1) {
		return this.call(coreFunction, locator, action, retryOnFalse, panel, param1, "", "");
	}
	public String call(String coreFunction, String locator, String action, Boolean retryOnFalse,
			String panel, String param1, String param2) {
		return this.call(coreFunction, locator, action, retryOnFalse, panel, param1, param2, "");
	}	
	public String call(String coreFunction, String locator, String action, Boolean retryOnFalse,
			String panel, String param1, String param2, String param3)
			throws SeleniumException {

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
		
		String jsFunc = "this." 
			+ coreFunction + "("  + doubleQuote(locator) + ","
			+ doubleQuote(action) + "," + doubleQuote(panel) + ","
			+ doubleQuote(param1) + "," + doubleQuote(param2) + ","
			+ doubleQuote(param3) + ")";
	
		String retval = this.getEval(jsFunc);
		
		if (retryOnFalse){
			Integer second = 0;
			while(second < 10 && retval.equals("false")){
				SleepUtil.sleep(1000);
				retval = this.getEval(jsFunc);
				second++;
			}
		}
		return retval;
	}

	public void setupZVariables()
			throws SeleniumException {
		commandProcessor.doCommand("setupZVariables", null);
		
	}
	
	private String doubleQuote(String str) {
		return doubleQuote + str + doubleQuote;
	}

}
