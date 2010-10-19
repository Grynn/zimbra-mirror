package framework.core;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;

import framework.util.SleepUtil;
import framework.util.HarnessException;

public class ZimbraSelenium extends DefaultSelenium {
	private static Logger logger = LogManager.getLogger(ExecuteHarnessMain.class);

	public String doubleQuote = "\"";

	public ZimbraSelenium(String serverHost, int serverPort,
			String browserStartCommand, String browserURL) {
		super(serverHost, serverPort, browserStartCommand, browserURL);
	}
	
	public boolean isElementPresent(String locator){
		boolean result=this.isElementPresent(locator);
	    logger.info("selenium.isElementPresent(" + locator + ")");
	    logger.info(result + "\n");	   
	    
	    return result;
	}
	
	public void click(String locator){
		logger.info("selenium.click(" + locator + ")" + "\n");
		this.click(locator);
	}
	
	public void clickAt(String locator, String coord){
		logger.info("selenium.clickAt(" + locator + "," + coord + ")" + "\n");
		this.clickAt(locator, coord);
	}
	
	public String call(String coreFunction, String locator, String action, Boolean retryOnFalse)throws HarnessException  {
		return this.call(coreFunction, locator, action, retryOnFalse, "", "");
	}
	public String call(String coreFunction, String locator, String action, Boolean retryOnFalse,
			String panel, String param1) throws HarnessException  {
		return this.call(coreFunction, locator, action, retryOnFalse, panel, param1, "", "");
	}
	public String call(String coreFunction, String locator, String action, Boolean retryOnFalse,
			String panel, String param1, String param2)throws HarnessException  {
		return this.call(coreFunction, locator, action, retryOnFalse, panel, param1, param2, "");
	}	
	public String call(String coreFunction, String locator, String action, Boolean retryOnFalse,
			String panel, String param1, String param2, String param3)
			throws HarnessException {

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
		logger.info("js >> " + jsFunc);
		logger.info("js >> " + retval + "\n");
		
		
		if (retval.equals("false")) {

			throw new HarnessException("js function: " + jsFunc + " returns false");

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
