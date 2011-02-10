package com.zimbra.qa.selenium.framework.ui;

import org.apache.log4j.*;

import com.thoughtworks.selenium.*;
import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;

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
	protected static Logger logger = LogManager
			.getLogger(AbsSeleniumObject.class);

	public AbsSeleniumObject() {
		logger.info("new AbsSeleniumObject");
	}

	/**
	 * Zimbra: return if the specified element is visible per style coordinates
	 * 
	 * @param locator
	 * @param leftLimit
	 * @param topLimit
	 * @return
	 */
	public boolean zIsVisiblePerPosition(String locator, int leftLimit,
			int topLimit) {

		// Check if the locator is present
		if (!sIsElementPresent(locator)) {
			logger.info("isVisiblePerPosition(" + locator
					+ ") element is not present");
			return (false);
		}

		// Find the current position
		Number left = ClientSessionFactory.session().selenium()
				.getElementPositionLeft(locator);
		Number top = ClientSessionFactory.session().selenium()
				.getElementPositionTop(locator);

		// If the position is less than the limits, then it is hidden
		boolean hidden = ((left.intValue() < leftLimit) && (top.intValue() < topLimit));
		logger.info("isVisiblePerPosition(" + locator + ") - (left, top) = ("
				+ left.intValue() + ", " + top.intValue()
				+ ") (limit, limit) = (" + leftLimit + ", " + topLimit + ") "
				+ (!hidden));
		return (!hidden);
	}

	/**
	 * Execute mouseDownAt followed by mouseUpAt on the (0,0) position of a
	 * locator
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

		ClientSessionFactory.session().selenium().mouseDownAt(locator, "0,0");
		ClientSessionFactory.session().selenium().mouseUpAt(locator, "0,0");

		logger.info("zClick(" + locator + ")");
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

		ClientSessionFactory.session().selenium().mouseDownRight(locator);
		ClientSessionFactory.session().selenium().mouseUpRight(locator);
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
			String html = ClientSessionFactory.session().selenium().getEval(
					script);
			logger.info("zGetHtml(" + locator + ") = " + html);
			return (html);
		} catch (SeleniumException e) {
			throw new HarnessException("Unable to grab HTML from locator "
					+ locator, e);
		}

	}

	// // ***
	// Start: Selenium methods
	// // ***

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
	 * DefaultSelenium.getSelectedId()
	 * 
	 * @param locator
	 */
	public String sGetSelectedId(String locator) {
		String id = ClientSessionFactory.session().selenium().getSelectedId(
				locator);
		logger.info("getSelectedId(" + locator + ") = " + id);
		return (id);
	}

	/**
	 * DefaultSelenium.click()
	 */
	public void sClick(String locator) {
		// Cast to DefaultSelenium ... Workaround until ZimbraSelnium is removed
		((DefaultSelenium) ClientSessionFactory.session().selenium())
				.click(locator);
		logger.info("click(" + locator + ")");
	}

	/**
	 * DefaultSelenium.waitForPageToLoad()
	 */
	public void sWaitForPageToLoad() {
		try {
			String timeout = "10000";
			// Cast to DefaultSelenium ... Workaround until ZimbraSelnium is
			// removed
			((DefaultSelenium) ClientSessionFactory.session().selenium())
					.waitForPageToLoad(timeout);
			logger.info("waitForPageToLoad(" + timeout + ")");
		} catch (Exception ex) {
			logger.info(ex.fillInStackTrace());
		}
	}

	/**
	 * DefaultSelenium.mouseDown()
	 */
	public void sMouseDown(String locator) {
		ClientSessionFactory.session().selenium().mouseDown(locator);
		logger.info("mouseDown(" + locator + ")");
	}

	/**
	 * DefaultSelenium.mouseUp()
	 */
	public void sMouseUp(String locator) {
		ClientSessionFactory.session().selenium().mouseUp(locator);
		logger.info("mouseUp(" + locator + ")");
	}

	/**
	 * DefaultSelenium.focus()
	 */
	public void sFocus(String locator) {
		ClientSessionFactory.session().selenium().focus(locator);
		logger.info("focus(" + locator + ")");
	}

	/**
	 * DefaultSelenium.isElementPresent()
	 */
	public boolean sIsElementPresent(String locator) {
		// Cast to DefaultSelenium ... Workaround until ZimbraSelnium is removed
		boolean present = ((DefaultSelenium) ClientSessionFactory.session()
				.selenium()).isElementPresent(locator);
		logger.info("isElementPresent(" + locator + ") = " + present);
		return (present);
	}

	/**
	 * DefaultSelenium.getXpathCount()
	 */
	public int sGetXpathCount(String xpath) {
		int count = ClientSessionFactory.session().selenium().getXpathCount(
				xpath).intValue();
		logger.info("getXpathCount(" + xpath + ") = " + count);
		return (count);
	}

	/**
	 * DefaultSelenium.getAttribute()
	 * 
	 * @throws HarnessException
	 */
	public String sGetAttribute(String locator) throws HarnessException {
		try {
			String attrs = ClientSessionFactory.session().selenium()
					.getAttribute(locator);
			logger.info("getAttribute(" + locator + ") = " + attrs);
			return (attrs);
		} catch (SeleniumException e) {
			throw new HarnessException(e);
		}
	}

	/**
	 * DefaultSelenium.isVisible()
	 */
	public boolean sIsVisible(String locator) {
		boolean visible = ClientSessionFactory.session().selenium().isVisible(
				locator);
		logger.info("isVisible(" + locator + ") = " + visible);
		return (visible);
	}

	/**
	 * zIsBusyOverlay()
	 */
	public boolean zIsBusyOverlay() {
		boolean isBusyOverlay = (ClientSessionFactory
				.session()
				.selenium()
				.getEval(
						"this.browserbot.getUserWindow().top.appCtxt.getShell().getBusy()")
				.equals("true"));

		logger.info("isBusyOverlay(" + ") = " + isBusyOverlay);
		return (isBusyOverlay);
	}

	/**
	 * zWaitForBusyOverlay()
	 */

	public void zWaitForBusyOverlay() throws HarnessException {
		try {
			ClientSessionFactory
					.session()
					.selenium()
					.waitForCondition(
							"selenium.browserbot.getUserWindow().top.appCtxt.getShell().getBusy()==false",
							"15000");
		} catch (Exception ex) {
			throw new HarnessException("Busy Overlay never disappeared!", ex);
		}
	}

	public void sWaitForCondition(String condition, String timeout)
			throws HarnessException {
		try {
			ClientSessionFactory.session().selenium().waitForCondition(
					condition, timeout);
		} catch (Exception ex) {
			throw new HarnessException(condition + " never become true: ", ex);
		}
	}

	public void zWaitForElement(String element, String timeout)
			throws HarnessException {
		try {
			ClientSessionFactory.session().selenium().waitForCondition(
					"selenium.isElementPresent(\"" + element + "\")", timeout);
		} catch (Exception ex) {
			throw new HarnessException(element + " never appeared : ", ex);
		}
	}
	
	public void zWaitForWindow(String name, String timeout) throws HarnessException {
		try {
			ClientSessionFactory
					.session()
					.selenium()
					.waitForCondition(
							"{var x; for(var windowName in selenium.browserbot.openedWindows ){"
									+ "var targetWindow = selenium.browserbot.openedWindows[windowName];"
									+ "if((!selenium.browserbot._windowClosed(targetWindow))&&"
									+ "(targetWindow.name == '" + name
									+ "' || targetWindow.document.title == '"
									+ name + "')){x=windowName;"
									+ "}}}; x!=null;", timeout);		
		} catch (Exception ex) {
			throw new HarnessException(name + " never opened : ", ex);
		}
	}	
	
	/**
	 * DefaultSelenium.isChecked()
	 */
	public boolean sIsChecked(String locator) {
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
			String text = ClientSessionFactory.session().selenium().getText(
					locator);
			logger.info("DefaultSelenium.getText(" + locator + ") = " + text);
			return (text);
		} catch (SeleniumException e) {
			throw new HarnessException(e);
		}
	}

	/**
	 * DefaultSelenium.getValue()
	 */
	public String sGetValue(String locator) {
		String text = ClientSessionFactory.session().selenium().getValue(
				locator);
		logger.info("DefaultSelenium.getValue(" + locator + ") = " + text);
		return (text);
	}

	/**
	 * DefaultSelenium.type()
	 */
	public void sType(String locator, String text) {
		ClientSessionFactory.session().selenium().type(locator, text);
		logger.info("type(" + locator + ", " + text + ")");
	}

	/**
	 * DefaultSelenium.typeKeys()
	 */
	public void sTypeKeys(String locator, String text) {
		ClientSessionFactory.session().selenium().typeKeys(locator, text);
		logger.info("typeKeys(" + locator + ", " + text + ")");
	}

	/**
	 * DefaultSelenium.keyPressNative()
	 */
	public void sKeyPressNative(String code) {
		ClientSessionFactory.session().selenium().keyPressNative(code);
		logger.info("keyPressNative(" + code + ")");
	}

	/**
	 * DefaultSeleniu.selectFrame()
	 */
	public void sSelectFrame(String locator) {
		ClientSessionFactory.session().selenium().selectFrame(locator);
		logger.info("sSelectFrame(" + locator + ")");
	}

	/**
	 * DefaultSelenium.selectWindow()
	 */
	public void sSelectWindow(String windowID) {
		ClientSessionFactory.session().selenium().selectWindow(windowID);
		logger.info("sSelectWindow(" + windowID + ")");
	}

	/**
	 * DefaultSelenium.windowFocus()
	 */
	public void sWindowFocus() {
		ClientSessionFactory.session().selenium().windowFocus();
		logger.info("sWindowFocus()");
	}

	/**
	 * DefaultSelenium.wwindowMaximize()
	 */
	public void sWindowMaximize() {
		ClientSessionFactory.session().selenium().windowMaximize();
		logger.info("sWindowMaximize()");
	}

	// // ***
	// End: Selenium methods
	// // ***

}
