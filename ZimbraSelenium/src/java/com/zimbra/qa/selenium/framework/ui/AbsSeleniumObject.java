package com.zimbra.qa.selenium.framework.ui;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Mouse;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.seleniumemulation.JavascriptLibrary;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;
import com.zimbra.qa.selenium.framework.core.ClientSession;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.core.ExecuteHarnessMain;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import org.openqa.selenium.HasInputDevices;
import org.openqa.selenium.internal.Locatable;

/**
 * The <code>AbsSeleniumObject</code> class is a base class that all "GUI"
 * objects can derive from, allowing access to the DefaultSelenium methods.
 * <p>
 * The <code>AbsSeleniumObject</code> is implemented as a thread safe (on the
 * test class level) way to access DefaultSelenium methods.
 * <p>
 * It is intended that Pages, Forms, Trees, etc. will derive from
 * AbsSeleniumObject and call DefaultSelenium methods using AbsSeleniumObject
 * methods. The class implementations should not use the {@link ClientSession}
 * objects directly.
 * <p>
 * Selenium methods start with a lower case "s", so that
 * {@link DefaultSelenium#click(String)} can be accessed using
 * {@link #sClick(String)}.
 * <p>
 * Zimbra specific methods start with a lower case "z", such as the
 * Zimbra-specific implementation of click {@link #zClick(String)}, which
 * performs the more stable action of MOUSE_DOWN followed by MOUSE_UP.
 * <p>
 * 
 * @author Matt Rhoades
 * 
 */
public abstract class AbsSeleniumObject {
	protected static final int LoadDelay = 30000; // wait 30 seconds for objects
	// to load
	protected static Logger logger = LogManager
			.getLogger(AbsSeleniumObject.class);

	protected static final Logger tracer = LogManager
			.getLogger(ExecuteHarnessMain.TraceLoggerName);

	public AbsSeleniumObject() {
		logger.info("new " + AbsSeleniumObject.class.getCanonicalName());
	}
	
	protected static class Coordinate {
		final int X;
		final int Y;
		
		public Coordinate(int x, int y) {
			this.X = x;
			this.Y = y;
		}
		
		/** 
		 * Print this coordinate in "x,y" format
		 */
		public String toString() {
			return (this.X + "," + this.Y);
		}
		
	}

	protected class BrowserMasks {

		public static final int BrowserMaskIE = 1 << 0; // 1
		public static final int BrowserMaskIE6 = 1 << 1; // 2
		public static final int BrowserMaskIE7 = 1 << 2; // 4
		public static final int BrowserMaskIE8 = 1 << 3; // 8
		public static final int BrowserMaskIE9 = 1 << 4; // 16
		public static final int BrowserMaskFF = 1 << 5; // ...
		public static final int BrowserMaskFF30 = 1 << 6; // ...
		public static final int BrowserMaskFF35 = 1 << 7; // ...
		public static final int BrowserMaskFF36 = 1 << 8; // ...
		public static final int BrowserMaskFF40 = 1 << 9; // ...
		public static final int BrowserMaskFF50 = 1 << 10; // ...
		public static final int BrowserMaskChrome = 1 << 11; // ...
		public static final int BrowserMaskChrome11 = 1 << 12; // ...
		public static final int BrowserMaskChrome12 = 1 << 13; // ...
		public static final int BrowserMaskChrome13 = 1 << 14; // ...
		public static final int BrowserMaskSafari = 1 << 15; // ...
		public static final int BrowserMaskSafari4 = 1 << 16; // ...
		public static final int BrowserMaskSafari5 = 1 << 17; // ...
		public static final int BrowserMaskSafari6 = 1 << 18; // ...
		// ...
		@SuppressWarnings("unused")
		private static final int BrowserMaskLast = 1 << 31; // Can't go higher
															// than this

	}

	// Since the browser user agent doesn't change, just set it once
	private static String BrowserUserAgent = null;
	private static int BrowserMask = 0;

	/**
	 * Determine which browser is open
	 * 
	 * @param mask
	 *            a mask composed of AbsSelenium.BrowserMask* values
	 * @return true if the browser matches all mask values
	 * @throws HarnessException
	 */
	protected boolean zIsBrowserMatch(int mask) throws HarnessException {

		if (BrowserUserAgent == null) {
			if (ZimbraSeleniumProperties.isWebDriver()){
				BrowserUserAgent = sGetEval("return navigator.userAgent;");
			}else{
				BrowserUserAgent = sGetEval("navigator.userAgent;");
			}
			logger.info("UserAgent: (navigator.userAgent;) >>>>>> "
					+ BrowserUserAgent);			
		}

		if (BrowserMask == 0) {

			if (BrowserUserAgent.contains("Firefox/")) {

				// Set the "general" browser type
				BrowserMask |= BrowserMasks.BrowserMaskFF;

				// Set the browser version

				if (BrowserUserAgent.contains("Firefox/3.0")) {

					// TBD - I don't see any FF 3.0 clients in WDC
					BrowserMask |= BrowserMasks.BrowserMaskFF30;

				} else if (BrowserUserAgent.contains("Firefox/3.5")) {

					// Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US;
					// rv:1.9.1.16) Gecko/20101130 Firefox/3.5.16
					BrowserMask |= BrowserMasks.BrowserMaskFF35;

				} else if (BrowserUserAgent.contains("Firefox/3.6")) {

					// Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US;
					// rv:1.9.2.18) Gecko/20110614 Firefox/3.6.18
					BrowserMask |= BrowserMasks.BrowserMaskFF36;

				} else if (BrowserUserAgent.contains("Firefox/4.0")) {

					// FF 4.0: Mozilla/5.0 (Windows NT 6.0; rv:2.0)
					// Gecko/20100101 Firefox/4.0
					BrowserMask |= BrowserMasks.BrowserMaskFF40;

				} else if (BrowserUserAgent.contains("Firefox/5.0")) {

					// TBD - I don't see any FF 5.0 clients in WDC
					BrowserMask |= BrowserMasks.BrowserMaskFF50;

				}

			} else if (BrowserUserAgent.contains("MSIE")) {

				// Set the "general" browser type
				BrowserMask |= BrowserMasks.BrowserMaskIE;

				// Set the browser version

				if (BrowserUserAgent.contains("IE6")) {

					// TBD - I don't see any IE6 clients in WDC
					BrowserMask |= BrowserMasks.BrowserMaskIE6;

				} else if (BrowserUserAgent.contains("IE7")) {

					// TBD - I don't see any IE7 clients in WDC
					BrowserMask |= BrowserMasks.BrowserMaskIE7;

				} else if (BrowserUserAgent.contains("IE8")) {

					// Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET
					// CLR 3.0.04506.648; .NET CLR 3.5.21022)
					BrowserMask |= BrowserMasks.BrowserMaskIE8;

				} else if (BrowserUserAgent.contains("IE9")) {

					// Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1;
					// Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR
					// 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0)
					BrowserMask |= BrowserMasks.BrowserMaskIE9;

				}

			} else if (BrowserUserAgent.contains("Chrome/")) {

				// Set the "general" browser type
				BrowserMask |= BrowserMasks.BrowserMaskChrome;

				// Set the browser version

				if (BrowserUserAgent.contains("Chrome/12")) {

					// Mozilla/5.0 (Windows NT 6.1) AppleWebKit/534.30 (KHTML,
					// like Gecko) Chrome/12.0.742.100 Safari/534.30
					BrowserMask |= BrowserMasks.BrowserMaskChrome12;

				} else if (BrowserUserAgent.contains("Chrome/13")) {

					// Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.1 (KHTML,
					// like Gecko) Chrome/13.0.782.32 Safari/535.1
					BrowserMask |= BrowserMasks.BrowserMaskChrome13;

				}

			} else if (BrowserUserAgent.contains("Safari/")) {

				// Set the "general" browser type
				BrowserMask |= BrowserMasks.BrowserMaskSafari;

				// Set the browser version

				if (BrowserUserAgent.contains("Safari/5")) {

					// Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)
					// AppleWebKit/533.19.4 (KHTML, like Gecko) Version/5.0.3
					// Safari/533.19.4
					BrowserMask |= BrowserMasks.BrowserMaskSafari5;

				}

			}

		}

		return ((BrowserMask & mask) == mask);

	}

	protected WebDriverBackedSelenium webDriverBackedSelenium() {
		return ClientSessionFactory.session().webDriverBackedSelenium();
	}

	protected WebDriver webDriver() {
		return ClientSessionFactory.session().webDriver();
	}
	
	/**
	 * Zimbra: return if the specified element is visible per style coordinates
	 * 
	 * @param locator
	 * @param leftLimit
	 * @param topLimit
	 * @return
	 * @throws HarnessException
	 */
	public boolean zIsVisiblePerPosition(String locator, int leftLimit,
			int topLimit) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...findElement:getLocation().x:y");
			return elementVisible(locator);
		}else{
			// Check if the locator is present
			if (!sIsElementPresent(locator)) {
				logger.info("isVisiblePerPosition(" + locator
					+ ") element is not present");
				return (false);
			}

			// Find the current position
			int left = sGetElementPositionLeft(locator);
			int top = sGetElementPositionTop(locator);

			// If the position is less than the limits, then it is hidden
			boolean hidden = ((left < leftLimit) && (top < topLimit));
			logger.info("isVisiblePerPosition(" + locator + ") - "
				+ "(left, top) = (" + left + ", " + top + ")"
				+ "(limit, limit) = (" + leftLimit + ", " + topLimit + ")  = "
				+ (!hidden));

			return (!hidden);
		}
	}

	/**
	 * Execute mouseDownAt followed by mouseUpAt on the coordination "(x,y)"
	 * position of a locator
	 * 
	 * @param locator
	 *            , coord
	 * @throws HarnessException
	 */
	public void zClickAt(String locator, String coord) throws HarnessException {

		// Check if the locator is present
		if (!sIsElementPresent(locator)) {
			logger.info("zClick(" + locator + ") element is not present");
			throw new HarnessException("zClick(" + locator
					+ ") element is not present");

		}
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...moveToElement:click()");
			final WebElement we = getElement(locator);
			final Actions builder = new Actions(webDriver());
			Action action = builder.moveToElement(we)
				    .click(we)
				    .build();
			action.perform();
			//Mouse mouse = ((HasInputDevices) webDriver()).getMouse();			
		    //mouse.click(((Locatable)we).getCoordinates());
		}
		else if(ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
			webDriverBackedSelenium().clickAt(locator, coord);
		}else{
			this.sMouseDownAt(locator, coord);
			this.sMouseUpAt(locator, coord);
		}
		
		logger.info("zClick(" + locator + "," + coord + ")");
	}

	/**
	 * Execute mouseDown followed by mouseUp
	 * 
	 * @param locator
	 * @throws HarnessException
	 */
	public void zClick(String locator) throws HarnessException {

		// Check if the locator is present
		if (!sIsElementPresent(locator)) {
			logger.info("zClick(" + locator + ") element is not present");
			throw new HarnessException("zClick(" + locator
					+ ") element is not present");

		}
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...click()");
			WebElement we = getElement(locator);
			Actions builder = new Actions(webDriver());
			Action action = builder
					.click(we)
				    .build();
			action.perform();
		} else {
			this.sMouseDown(locator);
			this.sMouseUp(locator);
		}
		logger.info("zClick(" + locator + ")");
	}

	/**
	 * Execute mouseDownRight followed by mouseUpRight on a locator at a
	 * coordinator
	 * 
	 * @param locator
	 *            , coord
	 * @throws HarnessException
	 */
	public void zRightClickAt(String locator, String coord)
			throws HarnessException {

		// Check if the locator is present
		if (!sIsElementPresent(locator)) {
			logger.info("zRightClick(" + locator + ") element is not present");
			throw new HarnessException("zRightClick(" + locator
					+ ") element is not present");
		}
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...moveToElement:contextClick()");
			WebElement element = getElement(locator);
			Actions builder = new Actions(webDriver());
			Action rClick = builder
					.moveToElement(element)
				    .contextClick(element)
				    .build();
			rClick.perform();	
			
			//Mouse mouse = ((HasInputDevices) webDriver()).getMouse();			
		    //mouse.contextClick(((Locatable)element).getCoordinates());
		} else {				
			this.sMouseDownRightAt(locator, coord);
			this.sMouseUpRightAt(locator, coord);
		}
		logger.info("zRightClick(" + locator + "," + coord + ")");
	}

	/**
	 * Execute mouseDownRight followed by mouseUpRight on a locator
	 * 
	 * @param locator
	 * @throws HarnessException
	 */
	public void zRightClick(String locator) throws HarnessException {

		// Check if the locator is present
		if (!sIsElementPresent(locator)) {
			logger.info("zRightClick(" + locator + ") element is not present");
			throw new HarnessException("zRightClick(" + locator
					+ ") element is not present");
		}
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...contextClick()");
			WebElement element = getElement(locator);
			Actions builder = new Actions(webDriver());
			Action rClick = builder.contextClick(element).build();
			rClick.perform();
		} else {
			this.sMouseDownRight(locator);
			this.sMouseUpRight(locator);
		}
		logger.info("zRightClick(" + locator + ")");
	}

	/**
	 * Execute select on a windowID
	 * 
	 * @param windowID
	 * @throws HarnessException
	 */
	public void zSelectWindow(String windowID) throws HarnessException {
		logger.info("zSelectWindow(" + windowID + ")");
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...switchTo:executeScript.focus:window.setPosition");
		}
		
		this.sSelectWindow(windowID);

		this.sWindowFocus();

		this.sWindowMaximize();
	}

	public String zGetHtml(String locator) throws HarnessException {
		try {
			String html = "";
			String script = "";
			if(ZimbraSeleniumProperties.isWebDriver()){
				logger.info("...WebDriver...executeScript.getInnerHTML");
				WebElement element = getElement(locator);
				script = "return arguments[0].innerHTML;";
				html = executeScript(script, element);
			}else{	
				script = "this.page().findElement('" + locator
						+ "').innerHTML";
				html = this.sGetEval(script);
			}
			logger.info("zGetHtml(" + locator + ") = " + html);
			return (html);
		} catch (SeleniumException e) {
			throw new HarnessException("Unable to grab HTML from locator "
					+ locator, e);
		}

	}

	/**
	 * The method writes in dialog box's input fields.
	 * 
	 * @param locator
	 * @param value
	 * @throws HarnessException
	 */
	public void zType(String locator, String value) throws HarnessException {
		if(ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...executeScript.focus:click:sendKeys()");
		}
		// Check if the locator is present
		if (!sIsElementPresent(locator)) {
			logger.info("zType(" + locator + ") element is not present");
			throw new HarnessException("zType(" + locator
					+ ") element is not present");
		}

		this.sFocus(locator);
		this.sClickAt(locator, "0,0");
		this.sType(locator, value);

		logger.info("zType(" + locator + "," + value + ")");
	}

	/**
	 * This method uses sTypeKeys to simulate the activation of textfield,
	 * then change the property internally through sType method
	 * The weakness of sTypeKeys is some characters such as '.' don't get
	 * printed to the textfield
	 * @param locator
	 * @param value
	 * @throws HarnessException 
	 */
	public void zTypeKeys(String locator, String value) throws HarnessException {
		if(ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...sendKeys()");
			sType(locator, value);
		}else{
			sTypeKeys(locator, value);
			sType(locator, value);
			logger.info("zTypeKeys(" + locator + "," + value + ")");
		}
	}
	
	public void zKeyDown(String keyCode) throws HarnessException {

		if (keyCode == null || keyCode.isEmpty()){
			throw new HarnessException("keyCode needs to be provided");
		}
		tracer.trace("keyboard shortcut " + keyCode);
		
		if(ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...executeScript.KeyDownEvent");			
			String locator = "//html//body";					
			//WebElement we = getElement(locator); 			
			//Actions builder = new Actions(webDriver());			
			for (String kc : keyCode.split(",")) {
				zKeyEvent(locator, kc, "keydown");
				/*
				try{
					int code = Integer.parseInt(kc);
					String key = KeyEvent.getKeyText(code);
					if (key == null || key.isEmpty()){
						throw new HarnessException("cannot convert " + code + " to String");
					}else{
						
						//we.sendKeys(key.toLowerCase());
						
						//builder.sendKeys(we,key.toLowerCase()).build().perform();
						
						//sType(locator,key.toLowerCase());						
					}
				}catch(Exception ex){
					logger.error(ex);
				}
				*/
			}			
		}else{
			for (String kc : keyCode.split(",")) {
				sGetEval("if(document.createEventObject){var body_locator=\"css=html>body\"; "
					+ "var body=selenium.browserbot.findElement(body_locator);"
					+ "var evObj = body.document.createEventObject();"
					+ "evObj.keyCode="
					+ kc
					+ ";evObj.repeat = false;"
					+ "body.focus(); body.fireEvent(\"onkeydown\",evObj);}"
					+ "else{if(window.KeyEvent){var evObj = document.createEvent('KeyEvents');"
					+ "evObj.initKeyEvent( 'keydown', true, true, window, false, false, false, false,"
					+ kc
					+ ", 0 );}else {var evObj = document.createEvent('HTMLEvents');"
					+ "evObj.initEvent( 'keydown', true, true, window, 1 );"
					+ "evObj.keyCode = "
					+ kc
					+ ";}var x = selenium.browserbot.findElementOrNull('"
					+ "css=html>body"
					+ "');x.focus(); x.dispatchEvent(evObj);}");
			}
		}
		
	}

	public void zKeyEvent(String locator, String keyCode, String event)
			throws HarnessException {
		if(ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...executeScript.KeyEvent");		
			if (this.zIsBrowserMatch(BrowserMasks.BrowserMaskIE)) {
				executeScript(
					"try {var el = arguments[0]; " 
					+ "var evObj = document.createEventObject(); " 
					+ "evObj.keyCode=" 
					+ keyCode 
					+ "; evObj.repeat = false; " 
					+ "el.focus(); el.fireEvent(\"on" 
					+ event 
					+ "\", evObj);}catch(err){return(err.message)}",
					getElement(locator));
			}else if (this.zIsBrowserMatch(BrowserMasks.BrowserMaskFF)){
				executeScript(
					"try {var el = arguments[0]; " 
				    + "var evo = document.createEvent('HTMLEvents'); " 
					+ "evo.initEvent('"
					+ event
					+ "', true, true, window, 1 ); evo.keyCode="
					+ keyCode
					+ "; el.blur(); el.focus(); el.dispatchEvent(evo);}catch(err){return(err.message)}",
					getElement("css=html body"));
			}else {
				executeScript(
						"try {var el = arguments[0]; " 
					    + "var evo = document.createEvent('HTMLEvents'); " 
						+ "evo.initEvent('"
						+ event
						+ "', true, true, window, 1 ); evo.keyCode="
						+ keyCode
						+ "; el.blur(); el.focus(); el.dispatchEvent(evo);}catch(err){return(err.message)}",
						getElement("css=html body"));
			}
		}else{			
			sGetEval("if(document.createEventObject){var x=selenium.browserbot.findElementOrNull('"
				+ locator
				+ "');var evObj = x.document.createEventObject();"
				+ "evObj.keyCode="
				+ keyCode
				+ "; evObj.repeat = false; x.focus(); x.fireEvent(\"on"
				+ event
				+ "\",evObj);}"
				+ "else{if(window.KeyEvent){var evObj = document.createEvent('KeyEvents');"
				+ "evObj.initKeyEvent( '"
				+ event
				+ "', true, true, window, false, false, false, false,"
				+ keyCode
				+ ", 0 );} "
				+ "else {var evObj = document.createEvent('HTMLEvents');"
				+ "evObj.initEvent( '"
				+ event
				+ "', true, true, window, 1 ); evObj.keyCode="
				+ keyCode
				+ ";} var x = selenium.browserbot.findElementOrNull('"
				+ locator + "'); x.blur(); x.focus(); x.dispatchEvent(evObj);}");
		}
	}

	/**
     * Enter HTML formatted text into a iframe specified by locator.
     * @param locator selenium locator, e.g. css=iframe[id^=’iframe_DWT’]
     * @param html HTML string, e.g. <strong><i>foo</i></strong>
     * @throws HarnessException
     */
	public void zTypeFormattedText(String locator, String html)
			throws HarnessException {
		
			logger.info("zTypeFormattedText(" + locator + ", " + html + ")");

			if(ZimbraSeleniumProperties.isWebDriver()){
				logger.info("...WebDriver...executeScript.setInnerHTML");
				executeScript("try{var bodytext=\""
					+ html
					+ "\";"
					+ "var iframe_element=arguments[0];"
					+ "var iframe_body=iframe_element.contentWindow.document.body;"
					+ "iframe_body.innerHTML = bodytext;}catch(err){return(err);}",getElement(locator));	
			}else{
				sGetEval("var bodytext=\""
					+ html
					+ "\";"
					+ "var iframe_locator=\""
					+ locator
					+ "\";"
					+ "var iframe_body=selenium.browserbot.findElement(iframe_locator).contentWindow.document.body;"
					+ "iframe_body.innerHTML = bodytext;");	
			}
	}

	/**
	 * DefaultSelenium.fireEvent(locator, eventName)
	 * 
	 * @param locator
	 * @param eventName
	 */
	public void sFireEvent(String locator, String eventName) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver... JavascriptLibrary callEmbeddedSelenium()");
			//not used in webdriver			
			WebElement we = getElement(locator);
			JavascriptLibrary jsLib = new JavascriptLibrary();
			//jsLib.callEmbeddedSelenium(webDriver(), "triggerMouseEvent", we, eventName);
			//jsLib.callEmbeddedSelenium(webDriver(), "triggerMouseEventAt", we, "focus","0,0");
			//jsLib.callEmbeddedSelenium(webDriver(), "triggerEvent", we, eventName);
			jsLib.callEmbeddedSelenium(webDriver(), "doFireEvent", we, eventName);
		}else{
			ClientSessionFactory.session().selenium().fireEvent(locator, eventName);
		}
		logger.info("fireEvent(" + locator + ", " + eventName + ")");
	}

	// // ***
	// Start: Selenium methods
	// // ***

	/**
	 * DefaultSelenium.getEval()
	 * 
	 * @param script
	 */
	public String sGetEval(String script) throws HarnessException {
		String value = null;
		try {			
			if (ZimbraSeleniumProperties.isWebDriver()){
				logger.info("...WebDriver... executeScript()");
				value = executeScript(script);				
			}else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
				value = webDriverBackedSelenium().getEval(script);
			}
			else{
				value = ClientSessionFactory.session().selenium().getEval(script);
			}
		logger.info("getEval(" + script + ") = " + value);
		return (value);
		} catch (Exception e) {
			logger.info(e + " executing " + script);			
			return value;			
		}
	}

	/**
	 * getHtmlSource()
	 * 
	 */
	public String sGetHtmlSource() throws HarnessException {
		String htmlString = null;
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver... getPageSource()");
			htmlString = webDriver().getPageSource();
		}else{
			htmlString = ClientSessionFactory.session().selenium()
				.getHtmlSource();
		}
		logger.info("sGetHtmlSource()");
		return (htmlString);
	}

	/**
	 * DefaultSelenium.getElementHeight()
	 * 
	 * @param locator
	 */
	public int sGetElementHeight(String locator) throws HarnessException {
		try {
			int n = -1;
			if (ZimbraSeleniumProperties.isWebDriver()){
				logger.info("...WebDriver... getSize().height");
				n = getElement(locator).getSize().height;				
			}else{ 
				n= ClientSessionFactory.session().selenium().getElementHeight(
				locator).intValue();
			}
			logger.info("getElementHeight(" + locator + ") = " + n);
			return (n);
		} catch (SeleniumException e) {
			throw new HarnessException(e);
		}
	}

	/**
	 * DefaultSelenium.getElementWidth()
	 * 
	 * @param locator
	 */
	public int sGetElementWidth(String locator) throws HarnessException {
		try {
			int n = -1;
			if (ZimbraSeleniumProperties.isWebDriver()){
				logger.info("...WebDriver... getSize().width");
				n = getElement(locator).getSize().width;				
			}else{
				n = ClientSessionFactory.session().selenium().getElementWidth(
				locator).intValue();
			}
			logger.info("getElementWidth(" + locator + ") = " + n);
			return (n);
		} catch (SeleniumException e) {
			throw new HarnessException(e);
		}
	}

	/**
	 * DefaultSelenium.getElementPositionLeft()
	 * 
	 * @param locator
	 */
	public int sGetElementPositionLeft(String locator) throws HarnessException {
		try {
			int n = -1;
			if (ZimbraSeleniumProperties.isWebDriver()){
				logger.info("...WebDriver... getLocation().x");
				n = getElement(locator).getLocation().x;				
			} 
			else if(ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
				n = webDriverBackedSelenium().getElementPositionLeft(locator).intValue();
			}
			else{
				n = ClientSessionFactory.session().selenium().getElementPositionLeft(locator).intValue();
			}
			logger.info("getElementPositionLeft(" + locator + ") = " + n);
			return (n);
		} catch (SeleniumException e) {
			throw new HarnessException(e);
		}
	}

	/**
	 * DefaultSelenium.getElementPositionTop()
	 * 
	 * @param locator
	 */
	public int sGetElementPositionTop(String locator) throws HarnessException {
		try {
			int n = -1;
			if (ZimbraSeleniumProperties.isWebDriver()){
				logger.info("...WebDriver... getLocation().y");
				n = getElement(locator).getLocation().y;				
			}
			else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
				n = webDriverBackedSelenium().getElementPositionTop(locator).intValue();
			}
			else{
				n = ClientSessionFactory.session().selenium().getElementPositionTop(locator).intValue();
			}
			logger.info("getElementPositionTop(" + locator + ") = " + n);
			return (n);
		} catch (SeleniumException e) {
			throw new HarnessException(e);
		}
	}

	/**
	 * getNextSiblingId()
	 * 
	 * @param
	 */
	public String sGetNextSiblingId(String id) throws HarnessException {
		String sibLingid = null;
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...executeScript:getElementById().nextSibling.id");
			sibLingid = executeScript("return document.getElementById('"
			+ id + "')" + ".nextSibling.id");				
		}else{
			sibLingid = ClientSessionFactory.session().selenium().getEval(
			"this.browserbot.getUserWindow().document.getElementById('"
			+ id + "')" + ".nextSibling.id");		
		}
		logger.info("sGetNextSiblingId( " + id + ") = " + sibLingid);
		return (sibLingid);
	}

	/**
	 * getPreviousSiblingId()
	 * 
	 * @param
	 */
	public String sGetPreviousSiblingId(String id) throws HarnessException {
		String sibLingid = null;
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...executeScript:getElementById().previousSibling.id");
			sibLingid = executeScript("return document.getElementById('"
			+ id + "')" + ".previousSibling.id");				
		}else{
			sibLingid = ClientSessionFactory.session().selenium().getEval(
			"this.browserbot.getUserWindow().document.getElementById('"
			+ id + "')" + ".previousSibling.id");
		}
		logger.info("sGetPreviousSiblingId( " + id + ") = " + sibLingid);
		return (sibLingid);
	}

	/**
	 * DefaultSelenium.getSelectedId()
	 * 
	 * @param locator
	 */
	public String sGetSelectedId(String locator) throws HarnessException {
		String id = null;
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...getFirstSelectedOption()");
			Select select =  new Select (getElement(locator));
			WebElement we = select.getFirstSelectedOption();
			id = we.getAttribute("id");
		}else{
			id = ClientSessionFactory.session().selenium().getSelectedId(
				locator);
		}
		logger.info("getSelectedId(" + locator + ") = " + id);
		return (id);
	}

	/**
	 * DefaultSelenium.sClickAt(String locator, String coord)
	 */
	public void sClickAt(String locator, String coord) throws HarnessException {
		// Cast to DefaultSelenium ... Workaround until ZimbraSelnium is removed
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...moveToElement:click()");
			WebElement we = getElement(locator);
			Actions builder = new Actions(webDriver());
			Action action = builder
					.moveToElement(we)
				    .click(we)
				    .build();
			action.perform();					
		}	
		else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
			webDriverBackedSelenium().clickAt(locator, coord);
		}
		else{
			((DefaultSelenium) ClientSessionFactory.session().selenium()).clickAt(
					locator, coord);
		}
		logger.info("clickAt(" + locator + "," + coord + ")");
	}

	/**
	 * DefaultSelenium.sClick()
	 * @throws HarnessException 
	 */
	public void sClick(String locator) throws HarnessException {
		try {

			// Cast to DefaultSelenium ... Workaround until ZimbraSelnium is removed
			if (ZimbraSeleniumProperties.isWebDriver()){
				logger.info("...WebDriver...click()");
				WebElement we = getElement(locator);
				we.click();
			}
			else if(ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
				webDriverBackedSelenium().click(locator);
			}
			else{
				((DefaultSelenium) ClientSessionFactory.session().selenium()).click(locator);
			}
			logger.info("click(" + locator + ")");

		} catch (SeleniumException e){
			throw new HarnessException(e);
		}
	}

	/**
	 * DefaultSelenium.close()
	 */
	public void sClose() throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...close()");
			webDriver().close();
		}
		else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
			webDriverBackedSelenium().close();
		}
		else{
			ClientSessionFactory.session().selenium().close();
		}
		logger.info("close()");
	}

	/**
	 * DefaultSelenium.doubleClick()
	 */
	public void sDoubleClick(String locator) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...doubleClick()");
			Actions actions = new Actions(webDriver()); 
			WebElement we = getElement(locator);			
			Action doubleClick = actions.doubleClick(we).build();
			doubleClick.perform();		    
			//Mouse mouse = ((HasInputDevices) webDriver()).getMouse();			
		   	//mouse.doubleClick(((Locatable)we).getCoordinates());
			/*
			executeScript("try{var evt = document.createEvent('MouseEvents');" +
			        "evt.initMouseEvent('dblclick',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" +
			        "arguments[0].dispatchEvent(evt)}catch(err){return(err.message)};", we);
			executeScript("arguments[0].fireEvent('ondblclick');", we);
			*/
		}else{		
			((DefaultSelenium) ClientSessionFactory.session().selenium())
				.doubleClick(locator);
			logger.info("doubleClick(" + locator + ")");
		}
	}

	/**
	 * Ger Center point of item in "(x,y)" format based on given locator
	 * @param locator
	 * @return
	 */
	public String zGetCenterPoint(String locator) throws HarnessException {
		int height = -1;
		int width  = -1;
		
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...getSize()");
		}
		height = sGetElementHeight(locator) / 2;
		width =  sGetElementWidth(locator) / 2;
					
		String centerHeight = Integer.toString(height);
		String centerWidth = Integer.toString(width);
   	   	return new StringBuilder("(").append(centerWidth).append(",").append(centerHeight).append(")").toString();
	}

	/**
	 * DefaultSelenium.waitForPageToLoad()
	 */
	public void sWaitForPageToLoad() throws HarnessException {
		String timeout = ZimbraSeleniumProperties.getStringProperty(
				"selenium.maxpageload.msec", "20000");

		try {

			// Cast to DefaultSelenium ... Workaround until ZimbraSelnium is
			// removed
			logger.info("waitForPageToLoad(" + timeout + ")");
			
			if(ZimbraSeleniumProperties.isWebDriver()){
				logger.info("...WebDriver...executeScript:readyState");
				/*
				waitForElementPresent("zov__main_Mail",20);					
				
				Wait<WebDriver> wait = new FluentWait<WebDriver>(webDriver()).withTimeout(30, TimeUnit.SECONDS).pollingEvery(500, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class);
			    WebElement we = wait.until(new ExpectedCondition<WebElement>() {
			    	 public WebElement apply(WebDriver driver) {
			    		 return driver.findElement(By.id("zov__main_Mail"));
			    	 }
				});
				*/
				
				//WebDriverWait wait = new WebDriverWait(webDriver(), 20);
				Wait<WebDriver> wait = new FluentWait<WebDriver>(webDriver()).withTimeout(10, TimeUnit.SECONDS).pollingEvery(500, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class);

				try{
					wait.until(new ExpectedCondition<Boolean>(){					
						public Boolean apply(WebDriver driver) {
							Boolean result = false;
							String str;						
							str = executeScript("return document['readyState'] ? 'complete' == document.readyState : true");
							if(str!=null && str.contentEquals("true")){
								result = true;							
							}						
							return result;
						}
					});
				} catch (Exception e) {
					logger.warn(e);
				}
			} else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
				webDriverBackedSelenium().waitForPageToLoad(timeout);
			} else {
				((DefaultSelenium) ClientSessionFactory.session().selenium())
					.waitForPageToLoad(timeout);
			}
		} catch (Exception ex) {
			logger.warn("sWaitForPageToLoad() error", ex);
		}
	}

	/**
	 * DefaultSelenium.mouseDown()
	 * @throws HarnessException 
	 */
	public void sMouseDown(String locator) throws HarnessException {
		if(ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...mouseDown()");
			WebElement we =  getElement(locator);
			Mouse mouse = ((HasInputDevices)webDriver()).getMouse();
			mouse.mouseDown(((RemoteWebElement)we).getCoordinates());
		}else{
			ClientSessionFactory.session().selenium().mouseDown(locator);
			logger.info("mouseDown(" + locator + ")");
		}
	}

	public void sMouseDownAt(String locator, String coordString) throws HarnessException {
		try {
			if(ZimbraSeleniumProperties.isWebDriver()){
				logger.info("...WebDriver...mouseMove.MouseDown()");
				//WebElement we = getElement(locator);
				//Point p = ((Locatable)we).getLocationOnScreenOnceScrolledIntoView();
				//p = p.moveBy((we.getSize().getWidth()/2), (we.getSize().getHeight()/2));
				Coordinates co =  ((RemoteWebElement)getElement(locator)).getCoordinates();
				Mouse mouse = ((HasInputDevices)webDriver()).getMouse();
				mouse.mouseMove(co,0,0);
				mouse.mouseDown(co);
			}else{
				ClientSessionFactory.session().selenium().mouseDownAt(locator, coordString);
			}
			logger.info("mouseDownAt(" + locator + ",'" + coordString + "')");
			
		} catch (SeleniumException e) {
			throw new HarnessException(e);
		}
	}

	public void sMouseDownRightAt(String locator, String coordString) throws HarnessException {
		if(ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...action.moveToElement.contextClick()");
			WebElement we = getElement(locator);
			Actions action = new Actions(webDriver());    
		    action.moveToElement(we,1,1).contextClick(we).build().perform();		   	
		}else{
			ClientSessionFactory.session().selenium().mouseDownRightAt(locator,
				coordString);
		}
		logger.info("mouseDownRightAt(" + locator + ",'" + coordString + "')");
	}

	public void sMouseUpRightAt(String locator, String coordString) throws HarnessException {
		if(ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...action.moveToElement.release()");
			WebElement we = getElement(locator);
			Actions action = new Actions(webDriver());    
			action.moveToElement(we,0,0).release(we).build().perform();		   	
		}else{
			ClientSessionFactory.session().selenium().mouseUpRightAt(locator,
				coordString);
		}
		logger.info("mouseUpRightAt(" + locator + ",'" + coordString + "')");
	}

	/**
	 * DefaultSelenium.mouseOver()
	 */
	public void sMouseOver(String locator) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...action.moveToElement()");
			WebElement we = getElement(locator);
			//Mouse mouse = ((HasInputDevices) webDriver()).getMouse();
		   	//mouse.mouseMove(((Locatable)we).getCoordinates());
		   	Actions action = new Actions(webDriver());    
		    action.moveToElement(we).build().perform();		   	
		}
		else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
			webDriverBackedSelenium().mouseOver(locator);
		}
		else{
			ClientSessionFactory.session().selenium().mouseOver(locator);
		}
		logger.info("mouseOver(" + locator + ")");
	}

	/**
	 * DefaultSelenium.mouseOut()
	 */
	public void sMouseOut(String locator) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...action.clickAndHold.moveByOffset()");
			WebElement we = getElement(locator);
			Actions action = new Actions(webDriver());    
		    action.clickAndHold(we).moveByOffset(1,1).build().perform();		 
		}else{
			ClientSessionFactory.session().selenium().mouseOut(locator);
		}
		logger.info("mouseOut(" + locator + ")");
	}

	/**
	 * DefaultSelenium.refresh()
	 */
	public void sRefresh() throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...navigate.refresh()");
			webDriver().navigate().refresh();
		}else{
			ClientSessionFactory.session().selenium().refresh();
		}
		logger.info("refresh()");
	}

	/**
	 * DefaultSelenium.mouseUp()
	 * @throws HarnessException 
	 */
	public void sMouseUp(String locator) throws HarnessException {
		if(ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...MouseUp()");
			Coordinates co =  ((Locatable)getElement(locator)).getCoordinates();
			Mouse mouse = ((HasInputDevices)webDriver()).getMouse();
			mouse.mouseUp(co);
		}else{
			ClientSessionFactory.session().selenium().mouseUp(locator);
		}
		logger.info("mouseUp(" + locator + ")");
	}

	/**
	 * DefaultSelenium.mouseMoveAt()
	 */
	public void sMouseMoveAt(String locator, String coordString) throws HarnessException {
		if(ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...action.moveToElement()");
			WebElement we = getElement(locator);
			Actions action = new Actions(webDriver());    
			action.moveToElement(we,0,0).build().perform();		   	
		}else{
			ClientSessionFactory.session().selenium().mouseMoveAt(locator,
				coordString);
		}
		logger.info("mouseMoveAt(" + locator + ",'" + coordString + "')");
	}

	public void sMouseMove(String locator) throws HarnessException {
		if(ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...action.moveToElement()");
			WebElement we = getElement(locator);
			Actions action = new Actions(webDriver());    
			action.moveToElement(we).build().perform();		   	
		}else{
			ClientSessionFactory.session().selenium().mouseMove(locator);
		}
		logger.info("mouseMove(" + locator + ")");
	}

	/**
	 * DefaultSelenium.mouseUpAt()
	 */
	public void sMouseUpAt(String locator, String coordString) throws HarnessException {
		try {
			logger.info("mouseUpAt(" + locator + ",'" + coordString + ")'");
			if(ZimbraSeleniumProperties.isWebDriver()){
				logger.info("...WebDriver...mouseMove.MouseUp()");
				Coordinates co =  ((RemoteWebElement)getElement(locator)).getCoordinates();
				Mouse mouse = ((HasInputDevices)webDriver()).getMouse();
				mouse.mouseMove(co,0,0);
				mouse.mouseUp(co);
			}else{
				ClientSessionFactory.session().selenium().mouseUpAt(locator, coordString);
			}
		} catch (SeleniumException e) {
			throw new HarnessException(e);
		}
	}

	/**
	 * mouseDownRight()
	 */
	public void sMouseDownRight(String locator) throws HarnessException {
		if(ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...action.contextClick()");
			WebElement we = getElement(locator);
			Actions action = new Actions(webDriver());    
		    action.contextClick(we).build().perform();		   	
		}else{
			ClientSessionFactory.session().selenium().mouseDownRight(locator);
		}
		logger.info("mouseDownRight(" + locator + ")");
	}

	/**
	 * mouseUpRight()
	 */
	public void sMouseUpRight(String locator) throws HarnessException {
		if(ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...action.release()");
			WebElement we = getElement(locator);
			Actions action = new Actions(webDriver());    
			action.release(we).build().perform();		   	
		}else{
			ClientSessionFactory.session().selenium().mouseUpRight(locator);
		}
		logger.info("mouseUpRight(" + locator + ")");
	}

	/**
	 * DefaultSelenium.focus()
	 * @throws HarnessException 
	 */
	public void sFocus(String locator) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){ 
			logger.info("...WebDriver...executeScript:focus()");
			WebElement we = getElement(locator);
			Capabilities cp =  ((RemoteWebDriver)webDriver()).getCapabilities();
			if (cp.getBrowserName().equals(DesiredCapabilities.firefox().getBrowserName())||cp.getBrowserName().equals(DesiredCapabilities.chrome().getBrowserName())||cp.getBrowserName().equals(DesiredCapabilities.internetExplorer().getBrowserName())){
				executeScript("arguments[0].focus();", we);  
			}
		}
		else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
			webDriverBackedSelenium().focus(locator);
		}
		else{
			ClientSessionFactory.session().selenium().focus(locator);
		}
		logger.info("focus(" + locator + ")");
	}

	/**
	 * DefaultSelenium.isElementPresent()
	 * @throws HarnessException 
	 */
	public boolean sIsElementPresent(String locator) throws HarnessException {
		boolean present;
		if (locator.startsWith("//") || locator.startsWith("xpath")) {
			logger.warn("FIXME: the locator " + locator
					+ " is a xpath - should change to css");
		}
		if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()
				|| ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...findElement()");
			present = elementPresent(locator);
		}
		else {
			long startTime = System.currentTimeMillis();
			present = ((DefaultSelenium) ClientSessionFactory.session()
				.selenium()).isElementPresent(locator);
			long runTime = System.currentTimeMillis() - startTime;
			// if run time > 2 sec, the locator is probably xpath; should change to
			// css
			if (runTime > 2000) {
				logger.warn("FIXME: Run time = " + runTime
					+ " sec for sIsElementPresent(" + locator + ")");
			}
			logger.info("sIsElementPresent(" + locator + ") = " + present);
		}
		return (present);
	}

	/**
	 * getXpathCount()
	 */
	public int sGetXpathCount(String xpath) throws HarnessException {
		int count = 0;
		if(ZimbraSeleniumProperties.isWebDriver()){		
			count = getElements(By.xpath(xpath)).size();
		}else{
			count = ClientSessionFactory.session().selenium().getXpathCount(
				xpath).intValue();
		}
		logger.info("getXpathCount(" + xpath + ") = " + count);
		return (count);
	}

	/**
	 * getCssCount()
	 * @throws HarnessException 
	 */
	public int sGetCssCount(String css) throws HarnessException {
		int count = 0;
		
		if(ZimbraSeleniumProperties.isWebDriver()){		
			count = getElements(By.cssSelector(getCssLocator(css).getLocator())).size();
		}else{		
			count = ClientSessionFactory.session().selenium().getCssCount(css)
				.intValue();
		}
		logger.info("getCssCount(" + css + ") = " + count);
		return (count);
	}

	/**
	 * DefaultSelenium.getAllWindowTitles()
	 */
	public List<String> sGetAllWindowTitles() throws HarnessException {
		logger.info("getAllWindowTitles()");
		List<String> list = null;
				
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver... switchTo.window.getTitles()");
			list =  getAllWindowNames(); 		   		
		}else{
			String[] windows = ClientSessionFactory.session().selenium()
				.getAllWindowTitles();
			list = (Arrays.asList(windows));
		}
		return list;
	}

	/**
	 * DefaultSelenium.getAllWindowIds()
	 */
	public List<String> sGetAllWindowIds() throws HarnessException {
		logger.info("getAllWindowIds()");
		List<String> list = null;
		
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver... getWindowHandles");
			list = new ArrayList<String>(webDriver().getWindowHandles()); 		   		
		}else{
			String[] ids = ClientSessionFactory.session().selenium().getAllWindowIds();
			list = (Arrays.asList(ids));
		}
		return list;
	}

	
	/**
	 * DefaultSelenium.getAllWindowNames()
	 */
	public List<String> sGetAllWindowNames() throws HarnessException {
		logger.info("getAllWindowNames()");
		List<String> list = null;
		Set<String> availableWindows = null;
		
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver... switchTo.window.getTitle()");
			list =  getAllWindowNames(); 		   		
		}
		else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
			availableWindows =  webDriverBackedSelenium().getWrappedDriver().getWindowHandles(); 
	    	list = new ArrayList<String>(availableWindows);	    	
		}
		else{
			String[] windows = ClientSessionFactory.session().selenium()
				.getAllWindowNames();
			list = (Arrays.asList(windows));
		}
		return list;
	}

	/**
	 * DefaultSelenium.getAttribute() Use this method if you need the value of
	 * the attribute. If you are checking whether an attribute contains a value,
	 * use this instead:
	 * <p>
	 * sIsElementPresent("css=div[id='divid'][class*=ZSelected]");
	 * 
	 * @throws SeleniumException
	 */
	public String sGetAttribute(String locator) throws SeleniumException {

		// How can we determine whether the attribute exists or not?
		// Default selenium doesn't seem to have a way.
		// Tasks requires the SeleniumException to be thrown, then caught ...
		// so, can't convert to HarnessException
		//

		try {

			logger.info("getAttribute(" + locator + ")");
			
			String attrs = "";
			if (ZimbraSeleniumProperties.isWebDriver()) {
				logger.info("...WebDriver...findElement.getAttribute()");
				String [] elements = locator.split("@");
				if(elements != null && elements.length > 1){
					try {
						WebElement we = getElement(elements[0]);
						attrs = we.getAttribute(elements[1]);
					} catch (Exception ex) {
						logger.error(ex);
					}								
				}
			} else{
				attrs = ClientSessionFactory.session().selenium()
					.getAttribute(locator);
			}
			
			logger.info("getAttribute(" + locator + ") = " + attrs);
			return (attrs);

		} catch (SeleniumException e) {
			logger.error(e.getMessage(), e); // SeleniumExceptions don't use
			// logger, so log it here
			throw e;
		}
	}

	/**
	 * DefaultSelenium.isVisible()
	 */
	public boolean sIsVisible(String locator) throws HarnessException {
		boolean visible = false;
		if (ZimbraSeleniumProperties.isWebDriver()) {
			logger.info("...WebDriver...findElement.getLocation()");
			visible = elementVisible(locator);					
		} 
		else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
			visible = webDriverBackedSelenium().isVisible(locator);
		}
		else{
			visible = ClientSessionFactory.session().selenium().isVisible(locator);
		}
		logger.info("isVisible(" + locator + ") = " + visible);
		return (visible);
	}

	/**
	 * zIsBusyOverlay()
	 * 
	 * @throws HarnessException
	 */
	public boolean zIsBusyOverlay() throws HarnessException {
		boolean isBusyOverlay = true;
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...executeScript:getBusy()");
			isBusyOverlay = Boolean.parseBoolean(executeScript("return top.appCtxt.getShell().getBusy()==true"));
		}
		else{
			isBusyOverlay = (this
				.sGetEval("this.browserbot.getUserWindow().top.appCtxt.getShell().getBusy()"))
				.equals("true");
		}
		logger.info("isBusyOverlay(" + ") = " + isBusyOverlay);
		return (isBusyOverlay);
	}

	/**
	 * zWaitForBusyOverlay()
	 */

	public void zWaitForBusyOverlay() throws HarnessException {
		logger.info("zWaitForBusyOverlay()");

		try {
			if (ZimbraSeleniumProperties.isWebDriver()){
				logger.info("...WebDriver...executeScript:wait.until.getBusy()");
				sWaitForCondition("return top.appCtxt.getShell().getBusy()==false");
			}
			else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
				sWaitForCondition("selenium.browserbot.getCurrentWindow().top.appCtxt.getShell().getBusy()==false");
			}
			else{
				sWaitForCondition("selenium.browserbot.getUserWindow().top.appCtxt.getShell().getBusy()==false");
			}
		} catch (Exception ex) {
			throw new HarnessException("Busy Overlay never disappeared!", ex);
		}
	}

	/**
	 * zWaitForBusyOverlayHTML
	 */
	public void zWaitForBusyOverlayHTML() throws HarnessException {
		logger.info("zWaitForBusyOverlayHTML()");
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...");
		}else{
			SleepUtil.sleepLong();
		}
	}
	
	/**
	 * zWaitForBusyOverlayOctopus
	 */
	public void zWaitForBusyOverlayOctopus() throws HarnessException {
		logger.info("zWaitForBusyOverlayOctopus()");
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...");
		}else{
			SleepUtil.sleepLong();
		}
	}

	/**
	 * DefaultSelenium.waitForCondition() Runs the specified JavaScript snippet
	 * repeatedly until it evaluates to true
	 * 
	 * @param condition
	 * @throws HarnessException
	 */
	protected boolean sWaitForCondition(String condition) throws HarnessException {
		logger.info("sWaitForCondition(" + condition + "), timeout="
				+ LoadDelay); 
		try {
			boolean result = false;
			if (ZimbraSeleniumProperties.isWebDriver()) {
				logger.info("...WebDriver...executeScript.wait.until()");
				final String script = condition;
				result = (new WebDriverWait(webDriver(), LoadDelay/SleepUtil.SleepGranularity))
						.until(new ExpectedCondition<Boolean>() {
							public Boolean apply(WebDriver d) {
								if(d==null){
									return false;
								}else{
									return (Boolean) ((JavascriptExecutor) d)
											.executeScript(script);
								}
							}
						});
			}
			else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
				webDriverBackedSelenium().waitForCondition( condition, String.valueOf(LoadDelay));
				result = true;
			}
			else{				
				ClientSessionFactory.session().selenium().waitForCondition(
					condition, "" + LoadDelay);
				result = true;
			}
			return result;
		} catch (Exception ex) {
			logger.info(condition + " never become true: " + ex);
			return false;
		}
	}
	
	/**
	 * DefaultSelenium.waitForCondition() Runs the specified JavaScript snippet
	 * repeatedly during timout period until it evaluates to true
	 * 
	 * @param condition
	 * @param timeout
	 * @throws HarnessException
	 */
	public boolean sWaitForCondition(String condition, String timeout) throws HarnessException {
		logger.info("sWaitForCondition(" + condition + "), timeout="
				+ timeout);
		try {
			boolean result = false;
			if (ZimbraSeleniumProperties.isWebDriver()) {
				logger.info("...WebDriver...executeScript:wait.until()");
				final String script = condition;
				result = (new WebDriverWait(webDriver(), Long.valueOf(timeout)/SleepUtil.SleepGranularity))
						.until(new ExpectedCondition<Boolean>() {
							public Boolean apply(WebDriver d) {
								if(d==null){
									return false;									
								}else{
									return (Boolean) ((JavascriptExecutor) d)
										.executeScript(script);
								}
							}
						});
			}
			else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
				webDriverBackedSelenium().waitForCondition( condition, timeout);
				result = true;
			}
			else{
				ClientSessionFactory.session().selenium().waitForCondition(
					condition, timeout);
				result = true;
			}
			return result;
		} catch (Exception ex) {
			logger.info(condition + " never become true: " + ex);
			return false;
		}
	}

	/**
	 * zWaitForElementPresent() Waits for condition when
	 * selenium.isElementPresent() returns true
	 * 
	 * @param locator
	 * @throws HarnessException
	 */
	public boolean zWaitForElementPresent(String locator)
			throws HarnessException {
		logger.info("zWaitForElementPresent(" + locator + ")");

		if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()
				|| ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...executeScript:wait.until()");
			return waitForElementPresent(locator, true, 10);
		}else{
			return sWaitForCondition("selenium.isElementPresent(\"" + locator
				+ "\")");
		}
	}

	/**
	 * zWaitForElementPresent() Waits for condition during a given timout period until
	 * selenium.isElementPresent() returns true
	 * 
	 * @param locator
	 * @param timeout
	 * @throws HarnessException
	 */
	public boolean zWaitForElementPresent(String locator, String timeout)
			throws HarnessException {
		logger.info("zWaitForElementPresent(" + locator + ", " + timeout +")");

		if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()
				|| ZimbraSeleniumProperties.isWebDriver())	{
			logger.info("...WebDriver...executeScript:wait.until()");
			return waitForElementPresent(locator, true, Long.valueOf(timeout)/SleepUtil.SleepGranularity);
		}else{
			return sWaitForCondition("selenium.isElementPresent(\"" + locator
				+ "\")", timeout);
		}
	}

	
	/**
	 * zWaitForElementDeleted() Waits for condition when
	 * selenium.isElementPresent() returns false
	 * 
	 * @param locator
	 * @throws HarnessException
	 */
	public void zWaitForElementDeleted(String locator) throws HarnessException {
		logger.info("zWaitForElementDeleted(" + locator + ")");
		try {
			if (ZimbraSeleniumProperties.isWebDriver())	{
				logger.info("...WebDriver...executeScript:wait.until()");
				waitForElementPresent(locator, false, 10);
			}else{
				sWaitForCondition("!selenium.isElementPresent(\"" + locator + "\")");
			}
		} catch (Exception ex) {
			throw new HarnessException(locator + " never disappeared : ", ex);
		}
	}
	
	/**
	 * zWaitForElementDeleted() Waits for condition during a given timout period until
	 * selenium.isElementPresent() returns false
	 * 
	 * @param locator
	 * @param timeout
	 * @throws HarnessException
	 */
	public boolean zWaitForElementDeleted(String locator, String timeout) throws HarnessException {
		logger.info("zWaitForElementDeleted(" + locator + ", " + timeout +")");
		if (ZimbraSeleniumProperties.isWebDriver())	{
			logger.info("...WebDriver...executeScript:wait.until()");
			return waitForElementPresent(locator, false, Long.valueOf(timeout)/SleepUtil.SleepGranularity);
		}else{
			return sWaitForCondition("!selenium.isElementPresent(\"" + locator + "\")", timeout);
		}		   		
	}

	/**
	 * zIsElementDisabled(String cssLocator) check if the element (id) enabled
	 * 
	 * @param cssLocator
	 * @throws HarnessException
	 */
	public boolean zIsElementDisabled(String cssLocator) throws HarnessException {
		logger.info("zIsElementDisabled(" + cssLocator + ")");
		String locator = (cssLocator.startsWith("css=") ? "" : "css=")
				+ cssLocator + "[class*=ZDisabled]";
		if (ZimbraSeleniumProperties.isWebDriver())	{
			logger.info("...WebDriver...findElement()");
		}
		return sIsElementPresent(locator);
	}

	/**
	 * zWaitForElementEnabled(String id) Wait until the element (id) becomes
	 * enabled
	 * 
	 * @param id
	 * @throws HarnessException
	 */
	public void zWaitForElementEnabled(String cssLocator) throws HarnessException {
		logger.info("zWaitForElementEnabled(" + cssLocator + ")");
		if (ZimbraSeleniumProperties.isWebDriver())	{
			logger.info("...WebDriver...findElement()");
		}
		for (int i = 0; i < 15; i++) {			
			if (!zIsElementDisabled(cssLocator)) {
				return;
			}
			SleepUtil.sleepSmall();
		}
		throw new HarnessException("Element " + cssLocator + " never become enabled: ");

	}

	/**
	 * zWaitForElementVisible(String id) Wait until the element (id) becomes
	 * visible
	 * 
	 * @param id
	 * @throws HarnessException
	 */
	public void zWaitForElementVisible(String locator) throws HarnessException {
		logger.info("zWaitForElementVisible(" + locator + ")");
		if (ZimbraSeleniumProperties.isWebDriver())	{
			logger.info("...WebDriver...findElement.getLocation()");
			if(waitForElementVisible(locator, true, 5)){
				return;
			}
		}else{
			for (int i = 0; i < 15; i++) {
				if (zIsVisiblePerPosition(locator, 0, 0)) {
					return;
				}
				SleepUtil.sleepSmall();
			}
		}
		throw new HarnessException(locator + " - never visibled!");		
	}

	/**
	 * zWaitForElementInvisible(String id) Wait until the element (id) becomes
	 * invisible
	 * 
	 * @param id
	 * @throws HarnessException
	 */
	public void zWaitForElementInvisible(String locator)
			throws HarnessException {
		logger.info("zWaitForElementInvisible(" + locator + ")");
		if (ZimbraSeleniumProperties.isWebDriver())	{
			logger.info("...WebDriver...findElement.getLocation()");
			if(waitForElementVisible(locator, false, 5)){
				return;
			}
		}else{
			for (int i = 0; i < 15; i++) {
				if (!zIsVisiblePerPosition(locator, 0, 0)) {
					return;
				}
				SleepUtil.sleepSmall();
			}
		}
		throw new HarnessException(locator + "never invisible!");
	}

	/**
	 * zWaitForWindow() Waits for condition when window with a given name is
	 * opened
	 * 
	 * @param name
	 * @throws HarnessException
	 */
	public void zWaitForWindow(String name) throws HarnessException {
		logger.info("zWaitForWindow(" + name + ")");
		if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()
				|| ZimbraSeleniumProperties.isWebDriver()) {
			waitForWindowOpen(name,10L);
		} else {
			try {
				sWaitForCondition("var x; for(var windowName in selenium.browserbot.openedWindows)"
					+ "{var targetWindow = selenium.browserbot.openedWindows[windowName];"
					+ "if(!selenium.browserbot._windowClosed(targetWindow)&&"
					+ "(targetWindow.name.indexOf('"
					+ name.split("\\.")[0]
					+ "')!=-1||targetWindow.document.title.indexOf('"
					+ name.split("\\.")[0]
					+ "')!=-1)){x=windowName;}};x!=null;");
			} catch (Exception ex) {
				throw new HarnessException(name + " never opened : ", ex);
			}
		}
	}

	/**
	 * zWaitForIframeText() Waits for condition when text appears in the iframe
	 * body
	 * 
	 * @param iframe
	 * @param text
	 * @throws HarnessException
	 */
	public boolean zWaitForIframeText(String iframe, final String text)
			throws HarnessException {
		logger.info("zWaitForIframeText(" + iframe + ", " + text + ")");
		Boolean result = false;
		try {
			if(ZimbraSeleniumProperties.isWebDriver()){
				final WebElement we = getElement(iframe);
				logger.info("...WebDriver...executeScript.textContent");				
				ExpectedCondition<Boolean> ec = new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver driver) {
						String result =  executeScript("var iframe = arguments[0];"				
								+ "var iframe_body = " 
								+ "iframe.contentWindow.document.body;" 
								+ "if(navigator.userAgent.indexOf('MSIE')!=-1){" 
								+ "var result = iframe_body.innerHTML.indexOf('"
								+ text
								+ "') >= 0}else{"
								+ "var result = iframe_body.textContent.indexOf('"
								+ text
								+ "') >= 0} return result",we);
						if(result == null){
							return false;							
						}else{
							return Boolean.valueOf(result);
						}
					}
				};
				result =  waitForCondition(ec,10);						
			}else{
				result = sWaitForCondition("var x = selenium.browserbot.findElementOrNull(\""
					+ iframe
					+ "\");if(x!=null){x=x.contentWindow.document.body;if(browserVersion.isChrome){x.textContent.indexOf('"
					+ text
					+ "') >= 0;}else if(browserVersion.isIE){x.innerText.indexOf('"
					+ text
					+ "') >= 0;}else{x.textContent.indexOf('"
					+ text
					+ "') >= 0;}}else{false}","10000");
			}
			return result;
		} catch (Exception ex) {
			throw new HarnessException(iframe + " never opened : ", ex);
		}
	}

	/**
	 * zIsWindowOpen() Checks if window with a given name is open
	 * 
	 * @param name
	 * @throws HarnessException
	 */
	public boolean zIsWindowOpen(String name) throws HarnessException {
		logger.info("zIsWindowOpen(" + name + ")");
		if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()
				|| ZimbraSeleniumProperties.isWebDriver()) {
			return isWindowOpen(name);
		} else {
			String result = sGetEval("{var x; for(var windowName in selenium.browserbot.openedWindows ){"
				+ "var targetWindow = selenium.browserbot.openedWindows[windowName];"
				+ "if((!selenium.browserbot._windowClosed(targetWindow))&&"
				+ "(targetWindow.name == '"
				+ name
				+ "' || targetWindow.document.title == '"
				+ name
				+ "')){x=windowName;" + "}}}; x!=null;");
		logger.info("zIsWindowOpen(" + name + ") = " + result);
		return (result.contains("true"));
		}
	}

	/**
	 * zWaitForWindowClosed() Waits for condition when window with a given name
	 * is closed
	 * 
	 * @param name
	 * @throws HarnessException
	 */
	public boolean zWaitForWindowClosed(String name) throws HarnessException {
		logger.info("zWaitForWindowClosed(" + name + ")");
		boolean result = false;
		try {
			if(ZimbraSeleniumProperties.isWebDriver()){
				result = waitForWindowClosed(name, 5L, webDriver().getWindowHandles().size());
			}else{
				String condition = "{var x; for(var windowName in selenium.browserbot.openedWindows ){"
					+ "var targetWindow = selenium.browserbot.openedWindows[windowName];"
					+ "if((!selenium.browserbot._windowClosed(targetWindow))&&"
					+ "(targetWindow.name == '"
					+ name
					+ "' || targetWindow.document.title == '"
					+ name
					+ "')){x=windowName;" + "}}}; x==null;";
			
				result = sWaitForCondition(condition);
			}
			return result;
		} catch (Exception ex) {
			logger.info("window not found " + name, ex);
			return result;
		}
	}

	/**
	 * DefaultSelenium.check()
	 */
	public void sCheck(String locator) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()) {
			logger.info("...WebDriver...findElement.click()");
			WebElement we = getElement(locator);
			if(!we.isSelected()){				  
				we.click();		 
		    } 	
		}else{
			ClientSessionFactory.session().selenium().check(locator);
		}
		logger.info("check(" + locator + ")");
	}

	/**
    * DefaultSelenium.uncheck()
    */
	public void sUncheck(String locator) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...findElement.isSelected.click()");
			WebElement we = getElement(locator);
			if(we.isSelected()){				  
				we.click();		 
		    } 			
		}else{
			ClientSessionFactory.session().selenium().uncheck(locator);
		}
      logger.info("uncheck(" + locator + ")");
	}

	/**
	 * DefaultSelenium.isChecked()
	 */
	public boolean sIsChecked(String locator) throws HarnessException {
		boolean checked = false;
		if (ZimbraSeleniumProperties.isWebDriver()) {
			logger.info("...WebDriver...findElement.isSelected()");
			WebElement element = getElement(locator);
			checked = element.isSelected();
		} else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()) {
			checked = webDriverBackedSelenium().isChecked(locator);
		} else {
			// Cast to DefaultSelenium ... Workaround until ZimbraSelnium is removed
			checked = ((DefaultSelenium) ClientSessionFactory.session()
				.selenium()).isChecked(locator);
		}
		
		logger.info("isChecked(" + locator + ") = " + checked);
		return (checked);
	}

	/**
	 * DefaultSelenium.getText()
	 * 
	 * @throws HarnessException
	 */
	public String sGetText(String locator) throws HarnessException {
		try {
			String text = null;
			if (ZimbraSeleniumProperties.isWebDriver()) {
				logger.info("...WebDriver...findElement.getText()");
				WebElement we = getElement(locator);
				text = we.getText();
			}
			else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
				text = webDriverBackedSelenium().getText(locator);
			}
			else{
				text = ClientSessionFactory.session().selenium().getText(locator);
			}			
			logger.info("DefaultSelenium.getText(" + locator + ") = " + text);
			return (text);
		} catch (SeleniumException e) {
			throw new HarnessException(e);
		}
	}

	/**
	 * DefaultSelenium.getValue()
	 */
	public String sGetValue(String locator) throws HarnessException {
		String text = null;
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...findElement.getAttribute()");
			WebElement el = getElement(locator);
			text = el.getAttribute("value");
		}else{
			text = ClientSessionFactory.session().selenium().getValue(
				locator);
		}
		logger.info("getValue(" + locator + ") = " + text);
		return (text);
	}

	/**
	 * DefaultSelenium.getBodyText()
	 * 
	 * @return
	 */
	public String sGetBodyText() throws HarnessException {
		String text = "";
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...findElement.getText()");
			WebElement el = getElement("css=body");
			text = el.getText();
		}else{
			text = ClientSessionFactory.session().selenium().getBodyText();
		}
		logger.info("sGetBodyText() = " + text);
		return text;
	}

	/**
	 * DefaultSelenium.getTitle()
	 * 
	 * @return
	 */
	public String sGetTitle() throws HarnessException {
		String text = null;
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...getTitle()");
			text = webDriver().getTitle();
		}else{
			text = ClientSessionFactory.session().selenium().getTitle();
		}
		logger.info("DefaultSelenium.getTitle() = " + text);
		return text;

	}

	/**
	 * DefaultSelenium.type()
	 * @throws HarnessException 
	 */
	public void sType(String locator, String text) throws HarnessException {
		try {
			if (ZimbraSeleniumProperties.isWebDriver()){
				logger.info("...WebDriver...action.sendKeys()");
				sendKeys(locator,text);
			} 
			else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
				webDriverBackedSelenium().type(locator, text);
			}
			else{
				ClientSessionFactory.session().selenium().type(locator, text);
			}
			logger.info("type(" + locator + ", " + text + ")");
		} catch (SeleniumException e) {
			throw new HarnessException(e);
		}
	}

	/**
	 * DefaultSelenium.typeKeys()
	 */
	public void sTypeKeys(String locator, String text) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...action.sendKeys()");
			WebElement we = getElement(locator);
			Actions builder = new Actions(webDriver());
			Action action = builder.sendKeys(we,text).build();
			action.perform();
		}else{
			ClientSessionFactory.session().selenium().typeKeys(locator, text);
		}
		logger.info("typeKeys(" + locator + ", " + text + ")");
	}

	/**
	 * DefaultSelenium.getConfirmation()
	 */
	public String sGetConfirmation() throws HarnessException {
		String confirm = null;
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...switchTo.alert.getText()");
			confirm = webDriver().switchTo().alert().getText();
		}else{
			confirm =  ClientSessionFactory.session().selenium().getConfirmation();
		}
		logger.info("getConfirmation()");
		
		return confirm;
	}

	/**
	 * DefaultSelenium.keyPressNative()
	 */
	public void sKeyPressNative(String code) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...action.sendKeys()");
			Actions builder = new Actions(webDriver());
			builder.sendKeys(code).build().perform();				
		}else{
			ClientSessionFactory.session().selenium().keyPressNative(code);
		}
		logger.info("keyPressNative(" + code + ")");
	}

	/**
	 * DefaultSelenium.keyPress()
	 */
	public void sKeyPress(String locator, String code) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...action.sendKeys()");
			Actions builder = new Actions(webDriver());
			builder.sendKeys(getElement(locator), code).build().perform();			
		}else{
			ClientSessionFactory.session().selenium().keyPress(locator, code);			
		}
		logger.info("keypress(" + code + ")");
	}
	
	/**
	 * keyDown()
	 */
	public void sKeyDown(String locator, String code) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...Actions.keyDown()");
			Actions builder = new Actions(webDriver());
			builder.keyDown(getElement(locator),Keys.valueOf(code)).build().perform();				
		}else{
			ClientSessionFactory.session().selenium().keyDown(locator, code);
		}
		logger.info("keyDown(" + code + ")");
	}

	/**
	 * keyUp()
	 */
	public void sKeyUp(String locator, String code) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...Actions.keyUp()");
			Actions builder = new Actions(webDriver());
			builder.keyUp(getElement(locator),Keys.valueOf(code)).build().perform();				
		}else{
			ClientSessionFactory.session().selenium().keyUp(locator, code);
		}
		logger.info("keyUp(" + code + ")");
	}

	/**
	 * DefaultSelenium.keyDownNative()
	 */
	public void sKeyDownNative(String code) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...action.sendKeys()");
			Actions builder = new Actions(webDriver());
			builder.sendKeys(code).build().perform();				
		}else{
			ClientSessionFactory.session().selenium().keyDownNative(code);
		}
		logger.info("keyDownNative(" + code + ")");
	}

	/**
	 * DefaultSelenium.keyUpNative()
	 */
	public void sKeyUpNative(String code) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...action.release()");
			Actions builder = new Actions(webDriver());
			builder.release().build().perform();				
		}else{
			ClientSessionFactory.session().selenium().keyUpNative(code);
		}
		logger.info("keyUpNative(" + code + ")");
	}

	/**
	 * DefaultSelenium.select
	 * 
	 * @param selectLocator
	 *            Locator of the dropdown-list
	 * @param optionLocator
	 *            Option locators provide different ways of specifying options
	 *            of an HTML Select element (e.g. for selecting a specific
	 *            option, or for asserting that the selected option satisfies a
	 *            specification). There are several forms of Select Option
	 *            Locator. label=labelPattern matches options based on their
	 *            labels, i.e. the visible text. (This is the default.)
	 *            label=regexp:^[Oo]ther value=valuePattern matches options
	 *            based on their values. value=other id=id matches options based
	 *            on their ids. id=option1index=index matches an option based on
	 *            its index (offset from zero). index=2 If no option locator
	 *            prefix is provided, the default behaviour is to match on
	 *            label.
	 */
	public void sSelectDropDown(String selectLocator, String optionLocator) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...getFirstSelectedOption()");
			Select select =  new Select (getElement(selectLocator));
			String option = optionLocator;
			if(option.contains("value=")){
				option = option.split("value=")[1];
			}
			select.selectByValue(option);
		}else{
			ClientSessionFactory.session().selenium().select(selectLocator,
				optionLocator);
		}
		logger.info("sSelectDropDown(" + selectLocator + ", " + optionLocator
				+ ")");
	}

	/**
	 * DefaultSeleniu.selectFrame()
	 * @throws HarnessException 
	 */
	public void sSelectFrame(String locator) throws HarnessException {	
		
		try {
			if (ZimbraSeleniumProperties.isWebDriver()) {
				logger.info("...WebDriver...switchTo.frame()");
				if(locator.contains("relative=top")){
					webDriver().switchTo().defaultContent();
				}else{
					webDriver().switchTo().frame(getElement(locator));
				}
			} else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()) {
				webDriverBackedSelenium().getWrappedDriver().switchTo()
						.frame(getElement(locator));
				// WebElement el = getElement("body");
			} else {
				ClientSessionFactory.session().selenium().selectFrame(locator);
			}
			logger.info("sSelectFrame(" + locator + ")");		
		} catch (SeleniumException e) {			
			throw new HarnessException(e); // In case the frame doesn't exist			
		}		
	}

	/**
	 * DefaultSelenium.selectWindow()
	 */
	public void sSelectWindow(String windowID) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...switchTo()");
			switchTo(windowID);
			//switchToWindowUsingTitle(windowID);
		}else{
			ClientSessionFactory.session().selenium().selectWindow(windowID);			
		}
		logger.info("sSelectWindow(" + windowID + ")");
	}

	/**
	 * DefaultSelenium.deleteAllVisibleCookies()
	 */
	public void sDeleteAllVisibleCookies() {
		logger.info("sDeleteAllVisibleCookies()");
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...manage.deleteAllCookies()");
			webDriver().manage().deleteAllCookies();
		}
		else if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
			webDriverBackedSelenium().getWrappedDriver().manage().deleteAllCookies();
		}
		else{		
			ClientSessionFactory.session().selenium().deleteAllVisibleCookies();
		}
	}
	
	/**
	 * DefaultSelenium.deleteCookie()
	 */
	public void sDeleteCookie(String name, String optionString) {
		logger.info("sDeleteCookie("+ name +", "+ optionString +")");
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...manage.deleteCookieNamed()");
			webDriver().manage().deleteCookieNamed(name);
		}else{
			ClientSessionFactory.session().selenium().deleteCookie(name , optionString);
		}			
	}
	
	/**
	 * DefaultSelenium.openWindow()
	 * 
	 * @param url
	 * @param windowID
	 */
	public void sOpen(String url) throws HarnessException {
		logger.info("open(" + url + ")");
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...navigate().to()");
			webDriver().navigate().to(url);
		}else{			
			ClientSessionFactory.session().selenium().open(url);
		}
	}

	/**
	 * DefaultSelenium.openWindow()
	 * 
	 * @param url
	 * @param windowID
	 */
	public void sOpenWindow(String url, String windowID) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...navigate().to()");
			webDriver().navigate().to(url);
		}else{
			ClientSessionFactory.session().selenium().openWindow(url, windowID);
		}
		logger.info("openWindow(" + url + ", " + windowID + ")");
	}

	public void sWaitForPopUp(String windowID, String timeout) throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver...wait().switchTo()");			
			waitForWindowOpen(windowID,Long.valueOf(timeout)/SleepUtil.SleepGranularity);		
		}else{
			ClientSessionFactory.session().selenium().waitForPopUp(windowID,
				timeout);
		}
		logger.info("sWaitForPopUp(" + windowID + ")");
	}

	/**
	 * DefaultSelenium.windowFocus()
	 */
	public void sWindowFocus() throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			logger.info("...WebDriver... focus()");
			executeScript("window.focus()");
		}else {
			ClientSessionFactory.session().selenium().windowFocus();
		}
		logger.info("sWindowFocus()");
	}

	/**
	 * DefaultSelenium.wwindowMaximize()
	 */
	public void sWindowMaximize() throws HarnessException {
		if (ZimbraSeleniumProperties.isWebDriver()){
			webDriver().manage().window().setPosition(new Point(0, 0));
			webDriver().manage().window().setSize(new Dimension((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(),(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
	
		}else{
			ClientSessionFactory.session().selenium().windowMaximize();
		}
		logger.info("sWindowMaximize()");
	}

	/**
	 * sGetLocation()
	 */
	public String sGetLocation() throws HarnessException {
		String url = null;
		if (ZimbraSeleniumProperties.isWebDriver()){
			url = webDriver().getCurrentUrl();
		}else{
			url = ClientSessionFactory.session().selenium().getLocation();
		}
		logger.info("sGetLocation(): " + url);
		return url;
	}
	
	/**
	 * Drag and Drop a locator onto another locator
	 * @param locatorSource The locator item to drag
	 * @param locatorDestination The locator item to drop onto
	 * @param xOffset The offset of x coordinate
	 * @param yOffset The offset of y coordinate
	 * @throws HarnessException	 
	 */
	public void zDragAndDropBy(String locatorSource, String locatorDestination, int xOffset, int yOffset) throws HarnessException {

		if ( !this.sIsElementPresent(locatorSource) ) {
			throw new HarnessException("locator (source) cannot be found: "+ locatorSource);
		}
		
		if ( !this.sIsElementPresent(locatorDestination) ) {
			throw new HarnessException("locator (destination) cannot be found: "+ locatorDestination);
		}
		
		SleepUtil.sleepSmall();
		
		// Get the coordinates for the locators
		Coordinate source = new Coordinate(
				this.sGetElementPositionLeft(locatorSource), 
				this.sGetElementPositionTop(locatorSource));
		
		Coordinate destination = new Coordinate(
			this.sGetElementPositionLeft(locatorDestination), 
			this.sGetElementPositionTop(locatorDestination));
		
		Coordinate relative = new Coordinate(
				(destination.X - source.X) + xOffset,
				(destination.Y - source.Y) + yOffset);
		
		logger.info("x,y coordinate of the objectToBeDragged=" + source);
		logger.info("x,y coordinate of the objectToBeDroppedInto=" + destination);
		logger.info("xOffset,yOffset =" + xOffset + "," + yOffset);
		logger.info("x,y coordinate of the objectToBeDroppedInto relative to objectToBeDragged + offset = " + relative);

		if (ZimbraSeleniumProperties.isWebDriver()){
		    WebElement sourceElement = getElement(locatorSource);
		    WebElement destinationElement = getElement(locatorDestination);
		    
		    //(new Actions(webDriver())).dragAndDropBy(sourceElement,relative.X,relative.Y).build().perform();
		    (new Actions(webDriver())).clickAndHold(sourceElement)
		    .moveToElement(destinationElement,xOffset,yOffset)
		    .build().perform();
		    (new Actions(webDriver())).release(sourceElement).build().perform();
		}else{
		    ClientSessionFactory.session().selenium().dragAndDrop(locatorSource,relative.toString());
		}
		
		// Wait for the client to come back
		SleepUtil.sleepSmall();
	
		this.zWaitForBusyOverlay();
	}

	// // ***
	// End: Selenium methods
	// // ***

	// // ***
	// Start: WebDriver methods
	// // ***
		
	protected boolean zWaitForElementVisible(String locator, Boolean flag, String timeout) throws HarnessException {
		logger.info("zWaitForElementVisible(" + locator + ", " + timeout +")");
		Long wait = Long.valueOf(timeout)/SleepUtil.SleepGranularity;
		if (ZimbraSeleniumProperties.isWebDriver())	{
			logger.info("...WebDriver...findElement.getLocation()");
			if(waitForElementVisible(locator, flag, wait)){
				return true;
			}
		}else{
			for (int i = 0; i < wait; i++) {
				if (zIsVisiblePerPosition(locator, 0, 0)||!flag) {
					return true;
				}
				SleepUtil.sleepSmall();
			}
		}
		throw new HarnessException(locator + " - wait for visisble timed out after " + wait + "s");		
	}
	
	private void sendKeys(String locator, CharSequence ... keyValues) throws HarnessException {
		logger.info("...WebDriver...sendKeys()");
		WebElement we = getElement(locator);
		we.sendKeys(keyValues);
	}
	
	protected void clearField(String locator) throws HarnessException{
		logger.info("...WebDriver...clear()");
		WebElement we = getElement(locator);
		we.clear();		
	}
	
	protected String executeScript(String script, Object... arg){
		logger.info("...WebDriver...executeScript()");
		String value = null;
		try {			
			Object ob = ((JavascriptExecutor) webDriver()).executeScript(script,arg);
					
			logger.info(ob + " ...executing... " + script);
			if(ob != null){
				value = ob.toString();
			}
			return (value);
		} catch (Exception e) {
				logger.info(e + " Exception...executing " + script);			
				return value;			
		}
	}
	
	private static final class CssLocator {
		private String locator;
		private String text;
		private String preText;
		private String postText;

		private String getLocator() {
			return locator;
		}

		private void setLocator(String str) {
			locator = str;
		}

		private String getPreText() {
			return preText;
		}

		private void setPreText(String str) {
			preText = str;
		}
		
		private String getPostText() {
			return postText;
		}

		private void setPostText(String str) {
			postText = str;
		}
		private String getText() {
			return text;
		}

		private void setText(String txt) {
			text = txt;
		}

	}

	private WebElement findBy(By ... bys){
		logger.info("...WebDriver...findBy()");
		WebElement we = null;
		if(bys != null){
			for(By by:bys){
				if(we == null){
					we = webDriver().findElement(by);
				}else{
					we = we.findElement(by);
				}
			}
		}
		return we;
	}
	
	protected void clickBy(By ... bys) {
		logger.info("...WebDriver...clickBy()");
		findBy(bys).click();		
	}	
	
	private CssLocator configureCssLocator(String locator, String startSuffix,
			String containSuffix) {
		logger.info("...WebDriver...configureCssLocator()");
		String modLocator = locator;
		String preText = "";
		String text = "";
		String postText = "";
		CssLocator cssl = new CssLocator();
		
		if(modLocator!= null){		
			if (modLocator.startsWith(startSuffix)) {
				modLocator = modLocator.substring(startSuffix.length());				
			}

			if (modLocator.contains(containSuffix)){
				String[] tokens = modLocator.split(containSuffix);
				preText = tokens[0];
				if(tokens.length > 1){
					if(tokens[1].startsWith("(")&& tokens[1].contains(")")){
						text = tokens[1].substring(tokens[1].indexOf('(') + 1,
							tokens[1].lastIndexOf(')'));
						if(text.startsWith("'")&& text.endsWith("'")){
							text = text.substring(text.indexOf('\'') + 1,
								text.lastIndexOf('\''));
						}
					}
					if(tokens[1].length()> tokens[1].lastIndexOf(')')){
						postText = tokens[1].substring(tokens[1].lastIndexOf(')') + 1);
					}
				}
				modLocator = preText + postText;
			}
		}

		cssl.setPreText(preText);
		cssl.setText(text);
		cssl.setPostText(postText);
		cssl.setLocator(modLocator);
		return cssl;
	}
	
	private CssLocator getCssLocator(String locator) {
		logger.info("...WebDriver...getCssLocator()");
		return configureCssLocator(locator, "css=", ":contains");
	}
	
	private List<WebElement> getElements(By by) {
		logger.info("...WebDriver...getElements()");
		return webDriver().findElements(by);
	}
	
	protected WebElement getElement(String locator) throws HarnessException{
		logger.info("...WebDriver...getElement(" + locator + ")");
		WebElement we = getElementOrNull(locator);
		
		if(we==null){
			throw new HarnessException("WebElement is null: " + locator );			
		}else{
			return we;
		}
	}

	private WebElement getElementByXPath(String locator) {
		logger.info("...WebDriver...getElementByXPath()");
		WebElement element = null;
		WebDriver driver = webDriver();
		if (locator != null){
			try {
				element = driver.findElement(By.xpath(locator));
			}catch(Exception ex){
				logger.info("...getElementByXPath()..." + ex);
			}
		}
		return element;
	}
	
	private WebElement getElementById(String locator) {
		logger.info("...WebDriver...getElementById()");
		String startSuffix = "id=";
		WebElement element = null;
		WebDriver driver = webDriver();
		String modifiedLocator = locator;
		if (modifiedLocator != null){
			if( modifiedLocator.startsWith(startSuffix)) {
				modifiedLocator = modifiedLocator.substring(startSuffix.length());				
			}
			try {
				element = driver.findElement(By.id(modifiedLocator));
			}catch(Exception ex){
				logger.info("...getElementById()..." + ex);
			}
		}
		return element;
	}
	
	private WebElement getElementByClassName(String locator) {
		logger.info("...WebDriver...getElementByClassName()");
		String startSuffix = "class=";
		WebElement element = null;
		WebDriver driver = webDriver();
		String modifiedLocator = locator;
		if (modifiedLocator != null){
			if( modifiedLocator.startsWith(startSuffix)) {
				modifiedLocator = modifiedLocator.substring(startSuffix.length());				
			}
			try {
				element = driver.findElement(By.className(modifiedLocator));
			}catch(Exception ex){
				logger.info("...getElementById()..." + ex);
			}
		}
		return element;
	}
	
	private WebElement getElementByCss(String locator) {
		logger.info("...WebDriver...getElementByCss()");
		String startSuffix = "css=";
		String containSuffix = ":contains";
		WebElement we = null;
		WebDriver driver = null;
		CssLocator cssl = configureCssLocator(locator, startSuffix, containSuffix);
		if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
			driver = webDriverBackedSelenium().getWrappedDriver();
		}else{
			driver = webDriver();
		}	
		String modLocator = cssl.getLocator();
		if (null != modLocator) {
			try {
				String txt = cssl.getText();				
				if (txt != null && !txt.isEmpty()) {
					String preText = cssl.getPreText();
					String postText = cssl.getPostText();
					logger.info("...WebDriver.findElements(By.cssSelector(" 
					+ preText + "))");
					List<WebElement> elements = driver.findElements(By
						.cssSelector(preText));
					Iterator<WebElement> it = elements.iterator();
					while (it.hasNext()) {
						WebElement el = it.next();
						String returnedText = el.getText();
						if (returnedText!=null && returnedText.contains(txt)){
							logger.info("...WebDriver...found element containing: "	+ txt);
							if(postText !=null && !postText.isEmpty()){
								logger.info("...WebDriver...applying filter: findElement(By.cssSelector(" 
										+ postText + "))");
								el = el.findElement(By.cssSelector(postText));
							}
							we = el;
							break;
						}
					}					
				} else {
					logger.info("...WebDriver.findElement(By.cssSelector(" 
							+ modLocator + "))");
					we = driver.findElement(By.cssSelector(modLocator));
				}

			} catch (Exception ex) {
				logger.info("...getElementByCss()..." + ex);
			}
		}
		return we;
	}

	private WebElement getElementOrNull(String locator) {
		logger.info("...WebDriver...getElementOrNull()");
		WebElement we = null;
		if(locator.startsWith("id=")){
			we = getElementById(locator);
		}else if(locator.startsWith("class=")){
			we = getElementByClassName(locator);
		}else if(locator.startsWith("//")){
			we = getElementByXPath(locator);
		}else if(locator.startsWith("css=")){
			we = getElementByCss(locator);
		}else{
			if(locator.contains("=")){
				we = getElementByCss(locator);
			}else{
				we = getElementById(locator);
			}
		}
		return we;
	}
	
	private boolean elementPresent(String locator) {
		logger.info("...WebDriver...elementPresent()");
		WebElement el = getElementOrNull(locator);
		return el != null;
	}

	private boolean elementVisible(String locator) {
		logger.info("...WebDriver...elementVisible()");
		Boolean visible = false;
		WebElement we = getElementOrNull(locator);
		if( we != null){
			int left = we.getLocation().x;
			int top = we.getLocation().y;

			// If the position is less than zero, then it is hidden
			visible = (!((left < 0) && (top < 0)));
			logger.info("locator: " + locator
				+  "\n (left, top) = (" + left + ", " + top + ")"
				+  " - visible : " + (visible));						
		}else{
			logger.info("WebElement is null - " + locator );
		}
		return visible;
	}
	
	private boolean waitForElementPresent(final String locator, final boolean flag, long timeout) {
		logger.info("...WebDriver...waitForElementPresent()");
		Boolean present = false;
		if(locator !=null && !locator.isEmpty()){
			try{
				present = (new FluentWait<WebDriver>(webDriver()).withTimeout(timeout, TimeUnit.SECONDS).
					pollingEvery(500, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class))
						.until(new ExpectedCondition<Boolean>(){
							public Boolean apply(WebDriver d) {
								if(flag){
									return elementPresent(locator);
								}else{
									return !elementPresent(locator);
								}								
				}});
			}catch(TimeoutException  e){
				logger.info("...waitForElementPresent()... " + locator + " timed out after " + timeout + "s");
			}
		}
		return present;
	}
	
	private boolean waitForElementVisible(final String locator, final boolean flag , long timeout) {
		logger.info("...WebDriver...waitForElementVisible()");
		Boolean visible = false;
		if(locator !=null && !locator.isEmpty()){
			try{
				visible = (new FluentWait<WebDriver>(webDriver()).withTimeout(timeout, TimeUnit.SECONDS).
					pollingEvery(500, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class).ignoring(MoveTargetOutOfBoundsException.class).ignoring(ElementNotVisibleException.class))
						.until(new ExpectedCondition<Boolean>(){
							public Boolean apply(WebDriver d) {
								if(flag){
									return elementVisible(locator);
								}else{
									return !elementVisible(locator);
								}
				}});
			}catch(TimeoutException  e){
				logger.info("...waitForElementVisible()... " + locator + " timed out after " + timeout + "s");
			}
		}
		return visible;
	}
	
	private Boolean waitForCondition(ExpectedCondition<Boolean> condition, long timeout) {
		logger.info("...WebDriver...waitForCondition()");
		WebDriverWait wait = new WebDriverWait(webDriver(), timeout);
		Boolean result = false;		
		try{
			result = wait.until(condition);
		}catch(TimeoutException  e){
				logger.info("...WebDriver...waitForCondition()... timed out after " + timeout + "s");
		}				
		return result;
	}	
	
	private List<String> getAllWindowNames() throws HarnessException{ 
		logger.info("...WebDriver...getAllWindowNames()");
		
		List<String> list = new ArrayList<String>();
		WebDriver driver = webDriver();			
		try{
			Set<String> windowHandles = driver.getWindowHandles(); 		
			if (windowHandles!=null && !windowHandles.isEmpty()) {
				for (String handle : windowHandles) { 
					list.add(driver.switchTo().window(handle).getTitle());
				}
			}
		}catch(Exception ex){
			logger.error(ex);
		}
		return list;
	}
	
	protected boolean switchTo(String name) throws HarnessException {
		logger.info("...WebDriver...switchTo() " + name);	
		WebDriver driver = null;
		Set<String> handles = null;
		boolean found = false;
		if (ZimbraSeleniumProperties.isWebDriverBackedSelenium()){
			driver = webDriverBackedSelenium().getWrappedDriver();
		}else{ 
			driver = webDriver();
		}	
		
		try{	
			logger.info("handles size" );
			handles = driver.getWindowHandles();	
			logger.info(" : " + handles.size());
			if (handles != null && !handles.isEmpty()) {
				String url = "";
				String title = null;
				for (String handle : handles) {
					
					logger.info("about to switch to handle: " + handle);
					try{						
						title = driver.switchTo().window(handle).getTitle();
						logger.info("switched to title: " + title);											
					}catch(Exception ex){
						logger.error(ex);
					}
					url = driver.getCurrentUrl();
					
					if (title!=null && (title.contentEquals(name) || url.contains("/" + name + "?"))) {
						found = true;
						logger.info("found: " + title);
						break;
					}
				}				
			}
		}catch(Exception ex){
			logger.error(ex);
		}
		finally{
			if(!found){
				String defaultContent = driver.switchTo().defaultContent().getTitle();
				logger.info("back to defaultContent()" + defaultContent);
				sWindowFocus();
			}
		}
		return found;
	}

	private boolean isWindowOpen(String name) {
		logger.info("...WebDriver...isWindowOpen() " + name);
	
		boolean found = false;
		for (int i = 0; i < 5; i++) {
			try{
				found = switchTo(name);
			}catch(Exception ex){
				logger.error(ex);
			}
			if (found){
				break;
			}
			SleepUtil.sleepSmall();
			//webDriver().manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		}
		return found;
	}
	
	private boolean waitForWindowClosed(final String name, Long timeout, int ... handlesSize) {
		logger.info("...WebDriver...waitForWindowClosed() " + name);
		return waitForWindow(name, false, timeout, handlesSize);
	}
	
	private boolean waitForWindowOpen(final String name, Long timeout, int ... handlesSize) {
		logger.info("...WebDriver...waitForWindowOpen() " + name);
		return waitForWindow(name, true, timeout, handlesSize);
	}
	
	private boolean waitForWindow(final String name, final Boolean flag, Long timeout, int ... handlesSize) {
		logger.info("...WebDriver...waitForWindow() " + name);
		
		Wait<WebDriver> wait = null;
		
		if(handlesSize != null && handlesSize.length > 0){
			final int size = handlesSize[0];
			try{
				wait = new FluentWait<WebDriver>(webDriver()).withTimeout(5L, TimeUnit.SECONDS).pollingEvery(500, TimeUnit.MILLISECONDS);
				wait.until(new ExpectedCondition<Boolean>(){	
					Boolean result = false;
					public Boolean apply(WebDriver driver) {
						if(flag){
							result = driver.getWindowHandles().size() > size;
						}else{
							if(size > 1){
								result = driver.getWindowHandles().size() < size;
							}
						}
						return result;
				}});
			}catch(Exception te){
				logger.info("...wait for getWindowHandles().size differ from " + size + " timed out");
			}
		}
		
		boolean status = false;
		wait = new FluentWait<WebDriver>(webDriver()).withTimeout(timeout, TimeUnit.SECONDS).pollingEvery(500, TimeUnit.MILLISECONDS);
		try{
			status = wait.until(new ExpectedCondition<Boolean>(){					
				public Boolean apply(WebDriver driver) {
					try {
						if(flag){
							return switchTo(name);
						}else{
							return !switchTo(name);
						}
					} catch (HarnessException ex) {
						logger.info(ex);
						return false;						
					}
				}
			});
		}catch(Exception te){
			logger.info("...wait for window " + name + " become:" + flag + " timed out");
		}
		return status;
	}	
		
	// // ***
	// End: WebDriver methods
	// // ***
}
