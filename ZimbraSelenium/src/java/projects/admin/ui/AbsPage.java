package projects.admin.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.core.ClientSessionFactory;
import framework.util.HarnessException;
import framework.util.SleepUtil;

/**
 * This class defines an abstract Zimbra Application "Page"
 * @author Matt Rhoades
 *
 */
public abstract class AbsPage {
	protected static Logger logger = LogManager.getLogger(AbsPage.class);

	protected static final int PageLoadDelay = 30000; // wait 30 seconds for pages to load

	protected AppAdminConsole MyApplication = null;

	public AbsPage(AbsApplication application) {
		logger.info("new AbsPage");
		MyApplication = (AppAdminConsole)application;
	}
	
	/**
	 * Return the unique name for this page
	 * @return
	 */
	public abstract String myPageName();
	
	/**
	 * Determines if this page is active
	 * @return true if active.  false if not.
	 * @throws HarnessException
	 */
	public abstract boolean isActive() throws HarnessException;

	/**
	 * Wait for this page to become active (default PageLoadDelay)
	 * @throws HarnessException
	 */
	public void waitForActive() throws HarnessException {
		waitForActive(PageLoadDelay);
	}
	
	/**
	 * Wait for this page to become active (default PageLoadDelay)
	 * @throws HarnessException
	 */
	public void waitForActive(long millis) throws HarnessException {
		
		if ( isActive() ) {
			return; // Page became active
		}
		
		do {
			SleepUtil.sleep(SleepUtil.SleepGranularity);
			millis = millis - SleepUtil.SleepGranularity;
			if ( isActive() ) {
				return; // Page became active
			}
		} while (millis > SleepUtil.SleepGranularity);
		
		SleepUtil.sleep(millis);
		if ( isActive() ) {
			return;	// Page became active
		}

		throw new HarnessException("Page never became active");
	}
	
	/**
	 * Navigate to this page
	 * @throws HarnessException
	 */
	public abstract void navigateTo() throws HarnessException;
	

	//// ***
	// Start: Selenium methods
	//// ***
	
	/**
	 * Get the HTML source of the current page
	 * @param locator
	 */
	public String getHtmlSource() throws HarnessException {
		String htmlString = ClientSessionFactory.session().selenium().getHtmlSource();
		logger.info("getHtmlSource()");
		return (htmlString);
	}
	
	/**
	 * TBD
	 * @param locator
	 */
	public String getSelectedId(String locator) {
		String id = ClientSessionFactory.session().selenium().getSelectedId(locator);
		logger.info("getSelectedId(" + locator + ") = "+ id);
		return (id);
	}
	
	public void chooseOkOnNextConfirmation() {
		ClientSessionFactory.session().selenium().chooseOkOnNextConfirmation();
		logger.info("chooseOkOnNextConfirmation()");
	}
	
	/**
	 * Left-Click on element
	 * @param locator
	 */
	public void click(String locator) {
		ClientSessionFactory.session().selenium().click(locator);
		logger.info("click(" + locator + ")");
	}
	
	/**
	 * Put focus on element
	 * @param locator
	 */
	public void focus(String locator) {
		ClientSessionFactory.session().selenium().focus(locator);
		logger.info("focus(" + locator + ")");
	}
	
	/**
	 * Return true/false whether the element is present in the DOM
	 * @param locator
	 * @return
	 */
	public boolean isElementPresent(String locator) {
		boolean present = ClientSessionFactory.session().selenium().isElementPresent(locator);
		logger.info("isElementPresent(" + locator + ") = " + present);
		return (present);
	}

	public String getAttribute(String locator) {
		String attrs = ClientSessionFactory.session().selenium().getAttribute(locator);
		logger.info("getAttribute(" + locator + ") = " + attrs);
		return (attrs);
	}

	/**
	 * Return true/false whether the specified element is visible
	 * @param locator
	 * @return
	 */
	public boolean isVisible(String locator) {
		boolean visible = ClientSessionFactory.session().selenium().isVisible(locator);
		logger.info("isVisible(" + locator + ") = " + visible);
		return (visible);
	}


	/**
	 * Get the displayed text from the specied element
	 * @param locator
	 * @return
	 */
	public String getText(String locator) {
		String text = ClientSessionFactory.session().selenium().getText(locator);
		logger.info("DefaultSelenium.getText(" + locator + ") = " + text);
		return (ClientSessionFactory.session().selenium().getText(locator));
	}
	
	/**
	 * Simulate typing text.  Type text into the specified element.
	 * @param locator
	 * @param text
	 */
	public void type(String locator, String text) {
		ClientSessionFactory.session().selenium().type(locator, text);
		logger.info("type(" + locator + ", " + text + ")");
	}


	//// ***
	// End: Selenium methods
	//// ***

	public boolean isVisiblePerPosition(String locator, int leftLimit, int topLimit) {
		
		// Find the current position
		Number left = ClientSessionFactory.session().selenium().getElementPositionLeft(locator);
		Number top = ClientSessionFactory.session().selenium().getElementPositionTop(locator);
		
		// If the position is less than the limits, then it is hidden
		boolean hidden = ( (left.intValue() < leftLimit) && (top.intValue() < topLimit) );
		logger.info("isVisiblePerPosition("+ locator +") - (left, top) = ("+ left.intValue() +", "+ top.intValue() +") (limit, limit) = ("+ leftLimit +", "+ topLimit +") "+ (!hidden));
		return (!hidden);
	}
}
