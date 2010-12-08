package framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.util.HarnessException;
import framework.util.SleepUtil;

/**
 * A <code>AbsPage</code> object represents a major Zimbra "application" tab,
 * such as a Mail, Addressbook, Calendar, Tasks, Briefcase, Preferences, etc.
 * <p>
 * In addition to the major application tabs, the AbsPage also implements
 * other frames in the clients, such as the top title area and the search area.
 * <p>
 * Implementing AbsPage classes must define the {@link AbsPage#zIsActive()} and
 * {@link AbsPage#zNavigateTo()} methods.  The test method classes can set a
 * "startingapp", which the harness will attempt to navigate-to before running
 * each test method.
 * <p>
 * Most AbsPage objects include methods for managing and interacting with
 * the toolbars, lists, mouseclick actions, and other GUI elements.  It is
 * intended that the test case methods would use the AbsPage objects to
 * perform the majority of the GUI interaction, without having to access
 * Selenium methods or locators directly.
 * <p>
 * @author Matt Rhoades
 *
 */
public abstract class AbsPage extends AbsSeleniumObject {
	protected static Logger logger = LogManager.getLogger(AbsPage.class);

	protected static final int PageLoadDelay = 30000; // wait 30 seconds for pages to load

	/**
	 * A pointer to the application that created this object
	 */
	protected AbsApplication MyAbsApplication = null;

	/**
	 * Create this page object that exists in the specified application
	 * @param application
	 */
	public AbsPage(AbsApplication application) {
		logger.info("new AbsAdminPage");
		MyAbsApplication = application;
	}
	
	/**
	 * Return the unique name for this page class
	 * @return
	 */
	public abstract String myPageName();
	
	/**
	 * Determines if this page is active, usually by detecting
	 * whether a GUI element is present or not.
	 * <p>
	 * @return true if active.  false if not.
	 * @throws HarnessException
	 */
	public abstract boolean zIsActive() throws HarnessException;

	/**
	 * Wait for this page to become active (default PageLoadDelay)
	 * @throws HarnessException
	 */
	public void zWaitForActive() throws HarnessException {
		zWaitForActive(PageLoadDelay);
	}
	
	/**
	 * Wait for this page to become active
	 * @throws HarnessException
	 */
	public void zWaitForActive(long millis) throws HarnessException {
		
		if ( zIsActive() ) {
			return; // Page became active
		}
		
		do {
			SleepUtil.sleep(SleepUtil.SleepGranularity);
			millis = millis - SleepUtil.SleepGranularity;
			if ( zIsActive() ) {
				return; // Page became active
			}
		} while (millis > SleepUtil.SleepGranularity);
		
		SleepUtil.sleep(millis);
		if ( zIsActive() ) {
			return;	// Page became active
		}

		throw new HarnessException("Page never became active");
	}
	
	/**
	 * Navigate to this page
	 * @throws HarnessException
	 */
	public abstract void zNavigateTo() throws HarnessException;
	

}
