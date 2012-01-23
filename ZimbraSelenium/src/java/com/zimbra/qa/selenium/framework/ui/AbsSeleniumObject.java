package com.zimbra.qa.selenium.framework.ui;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;
import com.zimbra.qa.selenium.framework.core.ClientSession;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.core.ExecuteHarnessMain;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

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
			BrowserUserAgent = sGetEval("navigator.userAgent;");
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

		this.sMouseDownAt(locator, coord);
		this.sMouseUpAt(locator, coord);

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

		this.sMouseDown(locator);
		this.sMouseUp(locator);

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

		this.sMouseDownRightAt(locator, coord);
		this.sMouseUpRightAt(locator, coord);

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

		this.sMouseDownRight(locator);
		this.sMouseUpRight(locator);
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

		this.sSelectWindow(windowID);

		this.sWindowFocus();

		this.sWindowMaximize();

	}

	public String zGetHtml(String locator) throws HarnessException {
		try {
			String script = "this.page().findElement('" + locator
					+ "').innerHTML";
			String html = this.sGetEval(script);
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
	   sTypeKeys(locator, value);
	   sType(locator, value);
	   logger.info("zTypeKeys(" + locator + "," + value + ")");
	}

	public void zKeyDown(String keyCode) throws HarnessException {

		if (keyCode == null || keyCode.isEmpty())
			throw new HarnessException("keyCode needs to be provided");

		tracer.trace("keyboard shortcut " + keyCode);

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

	public void zKeyEvent(String locator, String keyCode, String event)
			throws HarnessException {

		sFocus(locator);

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

	/**
     * Enter HTML formatted text into a iframe specified by locator.
     * @param locator selenium locator, e.g. css=iframe[id^=’iframe_DWT’]
     * @param html HTML string, e.g. <strong><i>foo</i></strong>
     * @throws HarnessException
     */
	public void zTypeFormattedText(String locator, String html)
			throws HarnessException {
		
			logger.info("zTypeFormattedText(" + locator + ", " + html + ")");

			sGetEval("var bodytext=\""
					+ html
					+ "\";"
					+ "var iframe_locator=\""
					+ locator
					+ "\";"
					+ "var iframe_body=selenium.browserbot.findElement(iframe_locator).contentWindow.document.body;"
					+ "iframe_body.innerHTML = bodytext;");	
	}

	/**
	 * DefaultSelenium.fireEvent(locator, eventName)
	 * 
	 * @param locator
	 * @param eventName
	 */
	public void sFireEvent(String locator, String eventName) throws HarnessException {
		ClientSessionFactory.session().selenium().fireEvent(locator, eventName);
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
		String value = ClientSessionFactory.session().selenium()
				.getEval(script);
		logger.info("getEval(" + script + ") = " + value);
		return (value);
	}

	/**
	 * DefaultSelenium.getHtmlSource()
	 * 
	 * @param locator
	 */
	public String sGetHtmlSource() throws HarnessException {
		String htmlString = ClientSessionFactory.session().selenium()
				.getHtmlSource();
		logger.info("getHtmlSource()");
		return (htmlString);
	}

	/**
	 * DefaultSelenium.getElementHeight()
	 * 
	 * @param locator
	 */
	public int sGetElementHeight(String locator) throws HarnessException {
		try {
			int n = ClientSessionFactory.session().selenium().getElementHeight(
					locator).intValue();
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
			int n = ClientSessionFactory.session().selenium().getElementWidth(
					locator).intValue();
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
			int n = ClientSessionFactory.session().selenium()
					.getElementPositionLeft(locator).intValue();
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
			int n = ClientSessionFactory.session().selenium()
					.getElementPositionTop(locator).intValue();
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
		String sibLingid = ClientSessionFactory.session().selenium().getEval(
				"this.browserbot.getUserWindow().document.getElementById('"
						+ id + "')" + ".nextSibling.id");
		logger.info("sGetNextSiblingId( " + id + ") = " + sibLingid);
		return (sibLingid);
	}

	/**
	 * getPreviousSiblingId()
	 * 
	 * @param
	 */
	public String sGetPreviousSiblingId(String id) throws HarnessException {
		String sibLingid = ClientSessionFactory.session().selenium().getEval(
				"this.browserbot.getUserWindow().document.getElementById('"
						+ id + "')" + ".previousSibling.id");
		logger.info("sGetPreviousSiblingId( " + id + ") = " + sibLingid);
		return (sibLingid);
	}

	/**
	 * DefaultSelenium.getSelectedId()
	 * 
	 * @param locator
	 */
	public String sGetSelectedId(String locator) throws HarnessException {
		String id = ClientSessionFactory.session().selenium().getSelectedId(
				locator);
		logger.info("getSelectedId(" + locator + ") = " + id);
		return (id);
	}

	/**
	 * DefaultSelenium.sClickAt(String locator, String coord)
	 */
	public void sClickAt(String locator, String coord) throws HarnessException {
		// Cast to DefaultSelenium ... Workaround until ZimbraSelnium is removed
		((DefaultSelenium) ClientSessionFactory.session().selenium()).clickAt(
				locator, coord);
		logger.info("clickAt(" + locator + "," + coord + ")");
	}

	/**
	 * DefaultSelenium.sClick()
	 * @throws HarnessException 
	 */
	public void sClick(String locator) throws HarnessException {
		try {

			// Cast to DefaultSelenium ... Workaround until ZimbraSelnium is removed
			((DefaultSelenium) ClientSessionFactory.session().selenium()).click(locator);
			logger.info("click(" + locator + ")");

		} catch (SeleniumException e){
			throw new HarnessException(e);
		}
	}

	/**
	 * DefaultSelenium.close()
	 */
	public void sClose() throws HarnessException {
		ClientSessionFactory.session().selenium().close();
		logger.info("close()");
	}

	/**
	 * DefaultSelenium.doubleClick()
	 */
	public void sDoubleClick(String locator) throws HarnessException {
		// Cast to DefaultSelenium ... Workaround until ZimbraSelnium is removed
		((DefaultSelenium) ClientSessionFactory.session().selenium())
				.doubleClick(locator);
		logger.info("doubleClick(" + locator + ")");
	}

	/**
	 * Ger Center point of item in "(x,y)" format based on given locator
	 * @param locator
	 * @return
	 */
	public String zGetCenterPoint(String locator) throws HarnessException {
	   String centerHeight = Integer.toString(ClientSessionFactory.session().selenium().getElementHeight(locator).intValue() / 2);
	   String centerWidth = Integer.toString(ClientSessionFactory.session().selenium().getElementWidth(locator).intValue() / 2);
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
			((DefaultSelenium) ClientSessionFactory.session().selenium())
					.waitForPageToLoad(timeout);

		} catch (Exception ex) {
			logger.warn("sWaitForPageToLoad() error", ex);
		}
	}

	/**
	 * DefaultSelenium.mouseDown()
	 * @throws HarnessException 
	 */
	public void sMouseDown(String locator) throws HarnessException {
		ClientSessionFactory.session().selenium().mouseDown(locator);
		logger.info("mouseDown(" + locator + ")");
	}

	public void sMouseDownAt(String locator, String coordString) throws HarnessException {
		ClientSessionFactory.session().selenium().mouseDownAt(locator,
				coordString);
		logger.info("mouseDownAt(" + locator + ",'" + coordString + "')");
	}

	public void sMouseDownRightAt(String locator, String coordString) throws HarnessException {
		ClientSessionFactory.session().selenium().mouseDownRightAt(locator,
				coordString);
		logger.info("mouseDownRightAt(" + locator + ",'" + coordString + "')");
	}

	public void sMouseUpRightAt(String locator, String coordString) throws HarnessException {
		ClientSessionFactory.session().selenium().mouseUpRightAt(locator,
				coordString);
		logger.info("mouseUpRightAt(" + locator + ",'" + coordString + "')");
	}

	/**
	 * DefaultSelenium.mouseOver()
	 */
	public void sMouseOver(String locator) throws HarnessException {
		ClientSessionFactory.session().selenium().mouseOver(locator);
		logger.info("mouseOver(" + locator + ")");
	}

	/**
	 * DefaultSelenium.mouseOut()
	 */
	public void sMouseOut(String locator) throws HarnessException {
		ClientSessionFactory.session().selenium().mouseOut(locator);
		logger.info("mouseOver(" + locator + ")");
	}

	/**
	 * DefaultSelenium.refresh()
	 */
	public void sRefresh() throws HarnessException {
		ClientSessionFactory.session().selenium().refresh();
		logger.info("refresh()");
	}

	/**
	 * DefaultSelenium.mouseUp()
	 * @throws HarnessException 
	 */
	public void sMouseUp(String locator) throws HarnessException {
		ClientSessionFactory.session().selenium().mouseUp(locator);
		logger.info("mouseUp(" + locator + ")");
	}

	/**
	 * DefaultSelenium.mouseMoveAt()
	 */
	public void sMouseMoveAt(String locator, String coordString) throws HarnessException {
		ClientSessionFactory.session().selenium().mouseMoveAt(locator,
				coordString);
		logger.info("mouseMoveAt(" + locator + ",'" + coordString + "')");
	}

	public void sMouseMove(String locator) throws HarnessException {
		ClientSessionFactory.session().selenium().mouseMove(locator);
		logger.info("mouseMoveAt(" + locator + ")");
	}

	/**
	 * DefaultSelenium.mouseUpAt()
	 */
	public void sMouseUpAt(String locator, String coordString) throws HarnessException {
		ClientSessionFactory.session().selenium().mouseUpAt(locator,
				coordString);
		logger.info("mouseUpAt(" + locator + ",'" + coordString + ")'");
	}

	/**
	 * DefaultSelenium.mouseDownRight()
	 */
	public void sMouseDownRight(String locator) throws HarnessException {
		ClientSessionFactory.session().selenium().mouseDownRight(locator);
		logger.info("mouseDownRight(" + locator + ")");
	}

	/**
	 * DefaultSelenium.mouseUpRight()
	 */
	public void sMouseUpRight(String locator) throws HarnessException {
		ClientSessionFactory.session().selenium().mouseUpRight(locator);
		logger.info("mouseUpRight(" + locator + ")");
	}

	/**
	 * DefaultSelenium.focus()
	 * @throws HarnessException 
	 */
	public void sFocus(String locator) throws HarnessException {
		ClientSessionFactory.session().selenium().focus(locator);
		logger.info("focus(" + locator + ")");
	}

	/**
	 * DefaultSelenium.isElementPresent()
	 * @throws HarnessException 
	 */
	public boolean sIsElementPresent(String locator) throws HarnessException {
		// Cast to DefaultSelenium ... Workaround until ZimbraSelnium is removed
		if (locator.startsWith("//") || locator.startsWith("xpath")) {
			logger.warn("FIXME: the locator " + locator
					+ " is a xpath - should change to css");
		}

		long startTime = System.currentTimeMillis();
		boolean present = ((DefaultSelenium) ClientSessionFactory.session()
				.selenium()).isElementPresent(locator);
		long runTime = System.currentTimeMillis() - startTime;
		// if run time > 2 sec, the locator is probably xpath; should change to
		// css
		if (runTime > 2000) {
			logger.warn("FIXME: Run time = " + runTime
					+ " sec for sIsElementPresent(" + locator + ")");
		}
		logger.info("sIsElementPresent(" + locator + ") = " + present);

		return (present);
	}

	/**
	 * DefaultSelenium.getXpathCount()
	 */
	public int sGetXpathCount(String xpath) throws HarnessException {
		int count = ClientSessionFactory.session().selenium().getXpathCount(
				xpath).intValue();
		logger.info("getXpathCount(" + xpath + ") = " + count);
		return (count);
	}

	/**
	 * DefaultSelenium.getCssCount()
	 * @throws HarnessException 
	 */
	public int sGetCssCount(String css) throws HarnessException {
		int count = ClientSessionFactory.session().selenium().getCssCount(css)
				.intValue();
		logger.info("getCssCount(" + css + ") = " + count);
		return (count);
	}

	/**
	 * DefaultSelenium.getAllWindowTitles()
	 */
	public List<String> sGetAllWindowTitles() throws HarnessException {
		logger.info("getAllWindowTitles()");
		String[] windows = ClientSessionFactory.session().selenium()
				.getAllWindowTitles();
		return (Arrays.asList(windows));
	}

	/**
	 * DefaultSelenium.getAllWindowIds()
	 */
	public List<String> sGetAllWindowIds() throws HarnessException {
		logger.info("getAllWindowIds()");
		String[] ids = ClientSessionFactory.session().selenium().getAllWindowIds();
		return (Arrays.asList(ids));
	}

	/**
	 * DefaultSelenium.getAllWindowNames()
	 */
	public List<String> sGetAllWindowNames() throws HarnessException {
		logger.info("getAllWindowNames()");
		String[] windows = ClientSessionFactory.session().selenium()
				.getAllWindowNames();
		return (Arrays.asList(windows));
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
			String attrs = ClientSessionFactory.session().selenium()
					.getAttribute(locator);
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
		boolean visible = ClientSessionFactory.session().selenium().isVisible(
				locator);
		logger.info("isVisible(" + locator + ") = " + visible);
		return (visible);
	}

	/**
	 * zIsBusyOverlay()
	 * 
	 * @throws HarnessException
	 */
	public boolean zIsBusyOverlay() throws HarnessException {
		boolean isBusyOverlay = (this
				.sGetEval("this.browserbot.getUserWindow().top.appCtxt.getShell().getBusy()"))
				.equals("true");

		logger.info("isBusyOverlay(" + ") = " + isBusyOverlay);
		return (isBusyOverlay);
	}

	/**
	 * zWaitForBusyOverlay()
	 */

	public void zWaitForBusyOverlay() throws HarnessException {
		logger.info("zWaitForBusyOverlay()");

		try {
			sWaitForCondition("selenium.browserbot.getUserWindow().top.appCtxt.getShell().getBusy()==false");
		} catch (Exception ex) {
			throw new HarnessException("Busy Overlay never disappeared!", ex);
		}
	}

	/**
	 * zWaitForBusyOverlayHTML
	 */
	public void zWaitForBusyOverlayHTML() throws HarnessException {
		logger.info("zWaitForBusyOverlayHTML()");
		SleepUtil.sleepLong();
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
			ClientSessionFactory.session().selenium().waitForCondition(
					condition, "" + LoadDelay);
			return true;
		} catch (Exception ex) {
			logger.info(condition + " never become true: ");
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
			ClientSessionFactory.session().selenium().waitForCondition(
					condition, timeout);
			return true;
		} catch (Exception ex) {
			logger.info(condition + " never become true: ");
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

		return sWaitForCondition("selenium.isElementPresent(\"" + locator
				+ "\")");
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

		return sWaitForCondition("selenium.isElementPresent(\"" + locator
				+ "\")", timeout);
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
			sWaitForCondition("!selenium.isElementPresent(\"" + locator + "\")");
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
		return sWaitForCondition("!selenium.isElementPresent(\"" + locator + "\")", timeout);		
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
		for (int i = 0; i < 15; i++) {
			if (zIsVisiblePerPosition(locator, 0, 0)) {
				return;
			}
			SleepUtil.sleepSmall();
		}
		throw new HarnessException(locator + "never visibled!");
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
		for (int i = 0; i < 15; i++) {
			if (!zIsVisiblePerPosition(locator, 0, 0)) {
				return;
			}
			SleepUtil.sleepSmall();
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

	/**
	 * zWaitForIframeText() Waits for condition when text appears in the iframe
	 * body
	 * 
	 * @param iframe
	 * @param text
	 * @throws HarnessException
	 */
	public boolean zWaitForIframeText(String iframe, String text)
			throws HarnessException {
		logger.info("zWaitForIframeText(" + iframe + ", " + text + ")");

		try {
			sWaitForCondition("var x = selenium.browserbot.findElementOrNull(\""
					+ iframe
					+ "\");if(x!=null){x=x.contentWindow.document.body;}if(browserVersion.isChrome){x.textContent.indexOf('"
					+ text
					+ "') >= 0;}else if(browserVersion.isIE){x.innerText.indexOf('"
					+ text
					+ "') >= 0;}else{x.textContent.indexOf('"
					+ text
					+ "') >= 0;}");
			return true;
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

	/**
	 * zWaitForWindowClosed() Waits for condition when window with a given name
	 * is closed
	 * 
	 * @param name
	 * @throws HarnessException
	 */
	public boolean zWaitForWindowClosed(String name) throws HarnessException {
		logger.info("zWaitForWindowClosed(" + name + ")");

		try {
			String condition = "{var x; for(var windowName in selenium.browserbot.openedWindows ){"
					+ "var targetWindow = selenium.browserbot.openedWindows[windowName];"
					+ "if((!selenium.browserbot._windowClosed(targetWindow))&&"
					+ "(targetWindow.name == '"
					+ name
					+ "' || targetWindow.document.title == '"
					+ name
					+ "')){x=windowName;" + "}}}; x==null;";

			sWaitForCondition(condition);
			return true;
		} catch (Exception ex) {
			logger.info("Error: win not opened " + name, ex.fillInStackTrace());
			return false;
		}
	}

	/**
	 * DefaultSelenium.check()
	 */
	public void sCheck(String locator) throws HarnessException {
		ClientSessionFactory.session().selenium().check(locator);
		logger.info("check(" + locator + ")");
	}

	/**
    * DefaultSelenium.uncheck()
    */
	public void sUncheck(String locator) throws HarnessException {
	   ClientSessionFactory.session().selenium().uncheck(locator);
      logger.info("uncheck(" + locator + ")");
	}

	/**
	 * DefaultSelenium.isChecked()
	 */
	public boolean sIsChecked(String locator) throws HarnessException {
		// Cast to DefaultSelenium ... Workaround until ZimbraSelnium is removed
		boolean checked = ((DefaultSelenium) ClientSessionFactory.session()
				.selenium()).isChecked(locator);
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
			String text = ((DefaultSelenium) ClientSessionFactory.session().selenium()).getText(locator);
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
		String text = ClientSessionFactory.session().selenium().getValue(
				locator);
		logger.info("DefaultSelenium.getValue(" + locator + ") = " + text);
		return (text);
	}

	/**
	 * DefaultSelenium.getBodyText()
	 * 
	 * @return
	 */
	public String sGetBodyText() throws HarnessException {
		String text = ClientSessionFactory.session().selenium().getBodyText();
		return text;

	}

	/**
	 * DefaultSelenium.getTitle()
	 * 
	 * @return
	 */
	public String sGetTitle() throws HarnessException {
		String text = ClientSessionFactory.session().selenium().getTitle();
		logger.info("DefaultSelenium.getTitle() = " + text);
		return text;

	}

	/**
	 * DefaultSelenium.type()
	 * @throws HarnessException 
	 */
	public void sType(String locator, String text) throws HarnessException {
		try {

			ClientSessionFactory.session().selenium().type(locator, text);
			logger.info("type(" + locator + ", " + text + ")");

		} catch (SeleniumException e) {
			throw new HarnessException(e);
		}
	}

	/**
	 * DefaultSelenium.typeKeys()
	 */
	public void sTypeKeys(String locator, String text) throws HarnessException {
		ClientSessionFactory.session().selenium().typeKeys(locator, text);
		logger.info("typeKeys(" + locator + ", " + text + ")");
	}

	/**
	 * DefaultSelenium.getConfirmation()
	 */
	public String sGetConfirmation() throws HarnessException {
		logger.info("getConfirmation()");
		return ClientSessionFactory.session().selenium().getConfirmation();
	}

	/**
	 * DefaultSelenium.keyPressNative()
	 */
	public void sKeyPressNative(String code) throws HarnessException {
		ClientSessionFactory.session().selenium().keyPressNative(code);
		logger.info("keyPressNative(" + code + ")");
	}

	/**
	 * DefaultSelenium.keyPress()
	 */
	public void sKeyPress(String locator, String code) throws HarnessException {
		ClientSessionFactory.session().selenium().keyPress(locator, code);
		logger.info("keypress(" + code + ")");
	}

	/**
	 * DefaultSelenium.keyUp()
	 */
	public void sKeyUp(String locator, String code) throws HarnessException {
		ClientSessionFactory.session().selenium().keyUp(locator, code);
		logger.info("keypress(" + code + ")");
	}

	/**
	 * DefaultSelenium.keyDownNative()
	 */
	public void sKeyDownNative(String code) throws HarnessException {
		ClientSessionFactory.session().selenium().keyDownNative(code);
		logger.info("keyDownNative(" + code + ")");
	}

	/**
	 * DefaultSelenium.keyUpNative()
	 */
	public void sKeyUpNative(String code) throws HarnessException {
		ClientSessionFactory.session().selenium().keyUpNative(code);
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
		ClientSessionFactory.session().selenium().select(selectLocator,
				optionLocator);
		logger.info("sSelectDropDown(" + selectLocator + ", " + optionLocator
				+ ")");
	}

	/**
	 * DefaultSeleniu.selectFrame()
	 * @throws HarnessException 
	 */
	public void sSelectFrame(String locator) throws HarnessException {
		
		try {

			ClientSessionFactory.session().selenium().selectFrame(locator);
			logger.info("sSelectFrame(" + locator + ")");
		
		} catch (SeleniumException e) {
			
			throw new HarnessException(e); // In case the frame doesn't exist
			
		}
		
	}

	/**
	 * DefaultSelenium.selectWindow()
	 */
	public void sSelectWindow(String windowID) throws HarnessException {
		ClientSessionFactory.session().selenium().selectWindow(windowID);
		logger.info("sSelectWindow(" + windowID + ")");
	}

	/**
	 * DefaultSelenium.deleteAllVisibleCookies()
	 */
	public void sDeleteAllVisibleCookies() {
		logger.info("sDeleteAllVisibleCookies()");
		ClientSessionFactory.session().selenium().deleteAllVisibleCookies();	
	}
	
	/**
	 * DefaultSelenium.deleteCookie()
	 */
	public void sDeleteCookie(String name, String optionString) {
		logger.info("sDeleteCookie("+ name +", "+ optionString +")");
		ClientSessionFactory.session().selenium().deleteCookie(name , optionString);	
	}
	
	/**
	 * DefaultSelenium.openWindow()
	 * 
	 * @param url
	 * @param windowID
	 */
	public void sOpen(String url) throws HarnessException {
		logger.info("open(" + url + ")");
		ClientSessionFactory.session().selenium().open(url);
	}

	/**
	 * DefaultSelenium.openWindow()
	 * 
	 * @param url
	 * @param windowID
	 */
	public void sOpenWindow(String url, String windowID) throws HarnessException {
		ClientSessionFactory.session().selenium().openWindow(url, windowID);
		logger.info("openWindow(" + url + ", " + windowID + ")");
	}

	public void sWaitForPopUp(String windowID, String timeout) throws HarnessException {
		ClientSessionFactory.session().selenium().waitForPopUp(windowID,
				timeout);
		logger.info("sWaitForPopUp(" + windowID + ")");
	}

	/**
	 * DefaultSelenium.windowFocus()
	 */
	public void sWindowFocus() throws HarnessException {
		ClientSessionFactory.session().selenium().windowFocus();
		logger.info("sWindowFocus()");
	}

	/**
	 * DefaultSelenium.wwindowMaximize()
	 */
	public void sWindowMaximize() throws HarnessException {
		ClientSessionFactory.session().selenium().windowMaximize();
		logger.info("sWindowMaximize()");
	}

	// // ***
	// End: Selenium methods
	// // ***

}
